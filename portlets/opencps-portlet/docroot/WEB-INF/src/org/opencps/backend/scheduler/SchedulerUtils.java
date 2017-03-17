/**
 * OpenCPS is the open source Core Public Services software
 * Copyright (C) 2016-present OpenCPS community
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>
 */

package org.opencps.backend.scheduler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.opencps.backend.message.SendToEngineMsg;
import org.opencps.backend.util.BackendUtils;
import org.opencps.backend.util.PaymentRequestGenerator;
import org.opencps.dossiermgt.NoSuchDossierException;
import org.opencps.dossiermgt.RequiredDossierPartException;
import org.opencps.dossiermgt.model.Dossier;
import org.opencps.dossiermgt.model.DossierFile;
import org.opencps.dossiermgt.model.DossierPart;
import org.opencps.dossiermgt.model.impl.DossierImpl;
import org.opencps.dossiermgt.service.DossierFileLocalServiceUtil;
import org.opencps.dossiermgt.service.DossierLocalServiceUtil;
import org.opencps.dossiermgt.service.DossierLogLocalServiceUtil;
import org.opencps.dossiermgt.service.DossierPartLocalServiceUtil;
import org.opencps.dossiermgt.util.ActorBean;
import org.opencps.notificationmgt.utils.NotificationUtils;
import org.opencps.paymentmgt.keypay.model.KeyPay;
import org.opencps.paymentmgt.keypay.wssoap.KpWebservicesProxy;
import org.opencps.paymentmgt.model.PaymentConfig;
import org.opencps.paymentmgt.model.PaymentFile;
import org.opencps.paymentmgt.service.PaymentFileLocalServiceUtil;
import org.opencps.paymentmgt.util.PaymentMgtUtil;
import org.opencps.paymentmgt.util.VTCPayEventKeys;
import org.opencps.paymentmgt.vtcpay.model.VTCPay;
import org.opencps.paymentmgt.vtcpay.wssoap.WSCheckTransSoapProxy;
import org.opencps.processmgt.NoSuchProcessStepException;
import org.opencps.processmgt.NoSuchWorkflowOutputException;
import org.opencps.processmgt.model.ProcessOrder;
import org.opencps.processmgt.model.ProcessStepDossierPart;
import org.opencps.processmgt.model.ProcessWorkflow;
import org.opencps.processmgt.model.SchedulerJobs;
import org.opencps.processmgt.model.WorkflowOutput;
import org.opencps.processmgt.model.impl.ProcessOrderImpl;
import org.opencps.processmgt.model.impl.ProcessWorkflowImpl;
import org.opencps.processmgt.service.ProcessOrderLocalServiceUtil;
import org.opencps.processmgt.service.ProcessWorkflowLocalServiceUtil;
import org.opencps.processmgt.service.SchedulerJobsLocalServiceUtil;
import org.opencps.processmgt.service.WorkflowOutputLocalServiceUtil;
import org.opencps.processmgt.util.ProcessUtils;
import org.opencps.util.PortletConstants;
import org.opencps.util.WebKeys;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;


/**
 * @author khoavd
 *
 */
public class SchedulerUtils {
	
	private static Log _log = LogFactoryUtil.getLog(SchedulerUtils.class);

	/**
	 * @param SchedulerJobs schJob
	 */
	public static void doScheduler(SchedulerJobs schJob)
	    throws PortalException, SystemException {

		boolean conditionCheck =
		    BackendUtils.checkPreCondition(
		        schJob.getShedulerPattern(), schJob.getDossierId());

		if (conditionCheck) {

			SchedulerJobsLocalServiceUtil.updateScheduler(
			    schJob.getSchedulerJobsId(), 1);

			Date now = new Date();

			Dossier dossier = new DossierImpl();
			ProcessWorkflow processWorkflow = new ProcessWorkflowImpl();
			ProcessOrder processOrder = new ProcessOrderImpl();

			dossier = DossierLocalServiceUtil.getDossier(schJob.getDossierId());
			processWorkflow =
			    ProcessWorkflowLocalServiceUtil.fetchProcessWorkflow(schJob.getProcessWorkflowId());
			processOrder =
			    ProcessOrderLocalServiceUtil.getProcessOrder(
			        schJob.getDossierId(), schJob.getFileGroupId());

			SendToEngineMsg engineMsg = new SendToEngineMsg();

			engineMsg.setUserId(dossier.getUserId());
			engineMsg.setGroupId(dossier.getGroupId());
			engineMsg.setCompanyId(dossier.getCompanyId());
			engineMsg.setActionDatetime(now);
			engineMsg.setAction(processWorkflow.getActionName());
			engineMsg.setSignature(0);
			engineMsg.setPaymentValue(PaymentRequestGenerator.getTotalPayment(processWorkflow.getPaymentFee(), dossier.getDossierId()));
			engineMsg.setEstimateDatetime(now);
			engineMsg.setReceptionNo(dossier.getReceptionNo());
			engineMsg.setAssignToUserId(processWorkflow.getActionUserId());
			engineMsg.setActionNote(BackendUtils.buildActionName(schJob.getShedulerPattern()));
			engineMsg.setActionUserId(processWorkflow.getActionUserId());
			engineMsg.setProcessWorkflowId(processWorkflow.getProcessWorkflowId());
			engineMsg.setProcessOrderId(processOrder.getProcessOrderId());
			engineMsg.setDossierId(schJob.getDossierId());
			engineMsg.setFileGroupId(schJob.getFileGroupId());

			Message msg = new Message();

			msg.put("msgToEngine", engineMsg);

			MessageBusUtil.sendMessage(
			    "opencps/backoffice/engine/destination", msg);

		}

	}
	
	public void validateAssignTask(
		long dossierId, long processWorkflowId, long processStepId)
		throws NoSuchDossierException, NoSuchWorkflowOutputException,
		RequiredDossierPartException, NoSuchProcessStepException {

		boolean requiredFlag = false;

		if (processWorkflowId <= 0) {
			throw new NoSuchWorkflowOutputException();
		}

		if (dossierId <= 0) {
			throw new NoSuchDossierException();
		}

		/*
		 * if (processStepId <= 0) { throw new NoSuchProcessStepException(); }
		 */
		List<WorkflowOutput> workflowOutputs = new ArrayList<WorkflowOutput>();

		List<ProcessStepDossierPart> processStepDossierParts =
			new ArrayList<ProcessStepDossierPart>();

		if (processStepId > 0) {
			processStepDossierParts =
				ProcessUtils.getDossierPartByStep(processStepId);
		}

		if (processStepDossierParts != null) {
			for (ProcessStepDossierPart processStepDossierPart : processStepDossierParts) {

				if (processStepDossierPart.getDossierPartId() > 0) {
					try {

						List<WorkflowOutput> workflowOutputsTemp =
							WorkflowOutputLocalServiceUtil.getByProcessByPWID_DPID(
								processWorkflowId,
								processStepDossierPart.getDossierPartId());

						if (workflowOutputsTemp != null) {
							workflowOutputs.addAll(workflowOutputsTemp);
						}
					}
					catch (Exception e) {
					}
				}

			}
		}

		if (workflowOutputs != null && !workflowOutputs.isEmpty()) {
			for (WorkflowOutput workflowOutput : workflowOutputs) {
				if (workflowOutput.getRequired()) {

					DossierFile dossierFile = null;
					try {
						DossierPart dossierPart =
							DossierPartLocalServiceUtil.getDossierPart(workflowOutput.getDossierPartId());
						dossierFile =
							DossierFileLocalServiceUtil.getDossierFileInUse(
								dossierId, dossierPart.getDossierpartId());
					}
					catch (Exception e) {
						// TODO: handle exception
					}

					if (dossierFile == null) {
						requiredFlag = true;
						break;
					}

				}
			}
		}

		if (requiredFlag) {
			throw new RequiredDossierPartException();
		}
	}
	
	public void checkVTCpayment(PaymentConfig paymentConfig,
			PaymentFile paymentFile) throws Exception {

		VTCPay vtcPay = new VTCPay(paymentConfig.getKeypayMerchantCode(),
				String.valueOf(paymentFile.getKeypayTransactionId()),
				paymentConfig.getBankInfo(), paymentConfig.getKeypaySecureKey());

		int website_id = 0;
		String order_code = StringPool.BLANK;
		String receiver_acc = StringPool.BLANK;
		String sign = StringPool.BLANK;

		website_id = Integer.valueOf(vtcPay.getWebsite_id());
		order_code = vtcPay.getOrder_code();
		receiver_acc = vtcPay.getReceiver_acc();
		sign = VTCPay.getSecureHashCodeCheckRequest(vtcPay);

		String dataResult = StringPool.BLANK;

		WSCheckTransSoapProxy checkTransSoapProxy = new WSCheckTransSoapProxy();

		dataResult = checkTransSoapProxy.checkPartnerTransation(website_id,
				order_code, receiver_acc, sign);

		if (dataResult.trim().length() > 0) {
			VTCPay vtcPayResult = VTCPay
					.getSecureHashCodeCheckResponse(dataResult);

			if (vtcPayResult.getOrder_code().trim().length() > 0) {

				long transactionId = 0;
				transactionId = Long.parseLong(vtcPayResult.getOrder_code());

				if (paymentFile.getKeypayTransactionId() == transactionId) {

					if (vtcPayResult.getResponsecode().equals(
							VTCPayEventKeys.SUCCESS)) {

						// kiem tra neu trang thai thanh
						// cong
						// thi cap nhat paymentfile,log
						// gui notice
						Dossier dossier = null;

						try {
							dossier = DossierLocalServiceUtil
									.getDossier(paymentFile.getDossierId());
						} catch (NoSuchDossierException e) {

						}
						if (Validator.isNotNull(dossier)) {

							paymentFile
									.setPaymentStatus(PaymentMgtUtil.PAYMENT_STATUS_APPROVED);
							paymentFile
									.setPaymentMethod(WebKeys.PAYMENT_METHOD_KEYPAY);
							paymentFile.setPaymentGateStatusCode(Integer
									.valueOf(VTCPayEventKeys.SUCCESS));

							JSONObject jsonObject = null;

							jsonObject = JSONFactoryUtil
									.createJSONObject(paymentFile
											.getPaymentGateResponseData());

							jsonObject.put("status", VTCPayEventKeys.SUCCESS);

							paymentFile.setPaymentGateResponseData(jsonObject
									.toString());

							ActorBean actorBean = new ActorBean(1,
									dossier.getUserId());

							DossierLogLocalServiceUtil
									.addDossierLog(
											dossier.getUserId(),
											dossier.getGroupId(),
											dossier.getCompanyId(),
											dossier.getDossierId(),
											paymentFile.getFileGroupId(),
											PortletConstants.DOSSIER_STATUS_NEW,
											PortletConstants.DOSSIER_ACTION_CONFIRM_PAYMENT,
											PortletConstants.DOSSIER_ACTION_CONFIRM_PAYMENT,
											new Date(), 1,
											actorBean.getActor(),
											actorBean.getActorId(),
											actorBean.getActorName());

							NotificationUtils.sendNotificationToAccountant(
									dossier, paymentFile);
						}

					}
					// truong hop khong tra ve thanh
					// cong
					// thi van luu lieu check , bao gom
					// ca loi
					JSONObject jsonData = JSONFactoryUtil.createJSONObject();
					jsonData.put("reponsecode", vtcPayResult.getResponsecode());
					jsonData.put("order_code", vtcPayResult.getOrder_code());
					jsonData.put("amount", vtcPayResult.getAmount());

					paymentFile.setPaymentGateCheckCode(Integer
							.valueOf(vtcPayResult.getResponsecode()));
					paymentFile.setPaymentGateCheckResponseData(jsonData
							.toString());

					PaymentFileLocalServiceUtil.updatePaymentFile(paymentFile);

				}
			}

		}
	}
	
	public void checkKEYPAYpayment(PaymentConfig paymentConfig,
			PaymentFile paymentFile) throws Exception {

		if (Validator.isNotNull(paymentConfig)
				&& Validator.isNotNull(paymentFile)) {

			JSONObject jsonObject = null;

			jsonObject = JSONFactoryUtil.createJSONObject(paymentFile
					.getPaymentGateResponseData());

			KeyPay keyPay = new KeyPay(paymentConfig.getKeypayMerchantCode(),
					String.valueOf(paymentFile.getKeypayTransactionId()),
					jsonObject.getString("trans_id"),
					paymentConfig.getKeypayMerchantCode(),
					paymentConfig.getKeypaySecureKey());

			String secure_hash = StringPool.BLANK;
			secure_hash = KeyPay.getSecureHashCodeCheckRequest(keyPay);
			
			String dataResult = StringPool.BLANK;

			KpWebservicesProxy keypay = new KpWebservicesProxy();
			dataResult = keypay.querryBillStatus(keyPay.getMerchant_trans_id(),
					keyPay.getGood_code(), keyPay.getTrans_id(),
					keyPay.getMerchant_code(), secure_hash);
			
			if (dataResult.trim().length() > 0) {
				
				_log.info("===dataResult:"+dataResult);
			}
		}
	}

}
