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
import org.opencps.dossiermgt.model.Dossier;
import org.opencps.dossiermgt.service.DossierFileLocalServiceUtil;
import org.opencps.dossiermgt.service.DossierLocalServiceUtil;
import org.opencps.processmgt.model.ProcessOrder;
import org.opencps.processmgt.model.ProcessWorkflow;
import org.opencps.processmgt.model.WorkflowOutput;
import org.opencps.processmgt.service.ProcessOrderLocalServiceUtil;
import org.opencps.processmgt.service.ProcessWorkflowLocalServiceUtil;
import org.opencps.processmgt.service.WorkflowOutputLocalServiceUtil;
import org.opencps.processmgt.util.ProcessMgtUtil;
import org.opencps.processmgt.util.ProcessOrderUtils;
import org.opencps.util.PortletConstants;
import org.opencps.util.WebKeys;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.messaging.MessageListenerException;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author khoavd
 */
public class OneMinute implements MessageListener {

	/*
	 * (non-Javadoc)
	 * @see
	 * com.liferay.portal.kernel.messaging.MessageListener#receive(com.liferay
	 * .portal.kernel.messaging.Message)
	 */
	@Override
	public void receive(Message message)
		throws MessageListenerException {

		_log.info("RUNNING _ONE_MINUTELY XXXXXXXXXXXXXXXX****");

		List<ProcessWorkflow> processWorkflows = new ArrayList<ProcessWorkflow>();

		List<ProcessOrder> processOrders = new ArrayList<ProcessOrder>();

		Dossier dossier = null;

		SchedulerUtils schedulerUtil = new SchedulerUtils();

		try {
			processWorkflows =
				ProcessWorkflowLocalServiceUtil.getProcessWorkflowByEvent(SchedulerKeys.ONE_MIUTELY);
			
			_log.info("DSDSALDJSAKD SAKLD " + processWorkflows.size());

			for (ProcessWorkflow processWorkflow : processWorkflows) {

				processOrders =
					ProcessOrderLocalServiceUtil.getProcessOrderByStep(processWorkflow.getPreProcessStepId());

				if (processOrders.size() != 0) {
					for (ProcessOrder processOrder : processOrders) {

						boolean preCondition = false;
						preCondition =
							BackendUtils.checkPreCondition(
								processWorkflow.getPreCondition(), processOrder.getDossierId());

						_log.info("Scheduler _ONE_MINUTELY ########" +
							processWorkflow.getActionName() + "_" + processOrder.getDossierId());
						if (preCondition) {
							long processWorkflowId = processWorkflow.getProcessWorkflowId();

							long processOrderId = processOrder.getProcessOrderId();

							long assignToUserId =
								ProcessMgtUtil.getAssignUser(
									processWorkflowId, processOrderId,
									processWorkflow.getPostProcessStepId());

							long dossierId = processOrder.getDossierId();

							dossier = DossierLocalServiceUtil.getDossier(dossierId);

							Date receiveDate = dossier.getReceiveDatetime();

							if (Validator.isNull(receiveDate)) {
								receiveDate =
									ProcessOrderUtils.getRecevieDate(
										dossierId, processWorkflowId, 0);
							}

							schedulerUtil.validateAssignTask(
								dossierId, processWorkflowId, processWorkflow.getPreProcessStepId());

							Message msg = new Message();

							SendToEngineMsg sendToEngineMsg = new SendToEngineMsg();

							// sendToEngineMsg.setAction(WebKeys.ACTION);
							sendToEngineMsg.setActionNote("auto-event");
							sendToEngineMsg.setEvent(StringPool.BLANK);
							sendToEngineMsg.setGroupId(dossier.getGroupId());
							sendToEngineMsg.setCompanyId(dossier.getCompanyId());
							sendToEngineMsg.setAssignToUserId(assignToUserId);
							sendToEngineMsg.setActionUserId(0);
							sendToEngineMsg.setDossierId(dossierId);
							sendToEngineMsg.setEstimateDatetime(receiveDate);
							sendToEngineMsg.setFileGroupId(processOrder.getFileGroupId());
							// sendToEngineMsg.setPaymentValue(GetterUtil.getDouble(paymentValue));
							sendToEngineMsg.setProcessOrderId(processOrderId);
							sendToEngineMsg.setProcessWorkflowId(processWorkflowId);
							sendToEngineMsg.setReceptionNo(Validator.isNotNull(dossier.getReceptionNo())
								? dossier.getReceptionNo() : StringPool.BLANK);
							sendToEngineMsg.setSignature(0);
							sendToEngineMsg.setDossierStatus(dossier.getDossierStatus());
							sendToEngineMsg.setActionDatetime(new Date());
							sendToEngineMsg.setActorType(WebKeys.DOSSIER_ACTOR_SYSTEM);
							sendToEngineMsg.setReceiveDate(receiveDate);

							msg.put("msgToEngine", sendToEngineMsg);

							MessageBusUtil.sendMessage("opencps/backoffice/engine/destination", msg);

							List<WorkflowOutput> workflowOutputs =
								WorkflowOutputLocalServiceUtil.getByProcessWF(processWorkflowId);

							// Lat co trang trai sau khi gui thanh cong len jms
							// va
							// engine
							DossierFileLocalServiceUtil.updateDossierFileResultSyncStatus(
								0, dossierId, PortletConstants.DOSSIER_FILE_SYNC_STATUS_NOSYNC,
								PortletConstants.DOSSIER_FILE_SYNC_STATUS_REQUIREDSYNC, 0,
								workflowOutputs);
						}

					}

				}
				else {

				}

			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}

	}

	private Log _log = LogFactoryUtil.getLog(OneMinute.class);

}
