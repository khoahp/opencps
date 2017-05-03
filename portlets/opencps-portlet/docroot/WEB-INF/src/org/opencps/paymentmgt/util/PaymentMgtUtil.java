/**
 * OpenCPS is the open source Core Public Services software
 * Copyright (C) 2016-present OpenCPS community

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>
 */

package org.opencps.paymentmgt.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opencps.dossiermgt.model.Dossier;
import org.opencps.dossiermgt.service.DossierLocalServiceUtil;
import org.opencps.dossiermgt.service.DossierLogLocalServiceUtil;
import org.opencps.dossiermgt.util.ActorBean;
import org.opencps.notificationmgt.utils.NotificationUtils;
import org.opencps.paymentmgt.keypay.model.KeyPay;
import org.opencps.paymentmgt.model.PaymentConfig;
import org.opencps.paymentmgt.model.PaymentFile;
import org.opencps.paymentmgt.service.PaymentConfigLocalServiceUtil;
import org.opencps.paymentmgt.service.PaymentFileLocalServiceUtil;
import org.opencps.paymentmgt.vtcpay.model.VTCPay;
import org.opencps.util.PortletConstants;
import org.opencps.util.WebKeys;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.User;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;

/**
 * @author trungdk
 */
public class PaymentMgtUtil {

	public static final int PAYMENT_STATUS_REQUESTED = 0;
	public static final int PAYMENT_STATUS_CONFIRMED = 1;
	public static final int PAYMENT_STATUS_APPROVED = 2;
	public static final int PAYMENT_STATUS_REJECTED = 3;

	public static final int PAYMENT_METHOD_CASH = 1;
	public static final int PAYMENT_METHOD_KEYPAY = 2;
	public static final int PAYMENT_METHOD_BANK = 3;

	public static final int PAYMENT_STATUS_KEYPAY_OK = 0;
	public static final int PAYMENT_STATUS_KEYPAY_PENDING = 1;

	/**
	 * @param ownerUserId
	 * @param ownerOrgId
	 * @return
	 */
	public static String getOwnerPayment(long ownerUserId, long ownerOrgId) {

		String ownerName = StringPool.BLANK;

		if (ownerUserId != 0) {
			try {
				User user = UserLocalServiceUtil.fetchUser(ownerUserId);
				ownerName = user.getFullName();
			} catch (Exception e) {
				_log.error(e);
			}
		}

		if (ownerOrgId != 0) {
			try {
				Organization org = OrganizationLocalServiceUtil
						.fetchOrganization(ownerOrgId);
				ownerName = org.getName();
			} catch (Exception e) {
				_log.error(e);
			}
		}

		return ownerName;
	}

	/**
	 * @param paymentFileId
	 * @return
	 */
	public static String getPaymentMethod(long paymentFileId) {

		String paymentMethodName = StringPool.BLANK;

		PaymentFile paymentFile = null;

		try {
			paymentFile = PaymentFileLocalServiceUtil
					.fetchPaymentFile(paymentFileId);

			int paymentMethod = paymentFile.getPaymentMethod();

			if (paymentFile.getPaymentStatus() == 1
					|| paymentFile.getPaymentStatus() == 2) {

				switch (paymentMethod) {
				case 1:
					paymentMethodName = "payment-method-cash";
					break;
				case 2:
					paymentMethodName = "payment-method-keypay";
					break;
				case 4:
					paymentMethodName = "payment-method-bank";
					break;
				default:
					break;
				}

			}
		} catch (Exception e) {
			_log.error(e);
		}

		return paymentMethodName;
	}

	/**
	 * Get dossierStatus by PaymentFile
	 * 
	 * @param paymentFile
	 * @return String (DossierStatus)
	 */
	public static String getDossierStatus(PaymentFile paymentFile) {

		String dossierStatus = StringPool.BLANK;

		try {
			Dossier dossier = DossierLocalServiceUtil.fetchDossier(paymentFile
					.getDossierId());

			dossierStatus = dossier.getDossierStatus();

		} catch (Exception e) {
			_log.error(e);
		}

		return dossierStatus;

	}

	public static PaymentConfig validatePaymentConfig(long groupId,
			long govAgencyOrganizationId) {

		PaymentConfig paymentConfigValid = null;

		List<PaymentConfig> paymentConfigList = new ArrayList<PaymentConfig>();

		try {
			paymentConfigList = PaymentConfigLocalServiceUtil
					.getPaymentConfigListByGovAgencyAndStatus(groupId,
							govAgencyOrganizationId, true);
		} catch (SystemException e) {
			_log.error(e);
		}

		if (paymentConfigList.size() == 1) {
			paymentConfigValid = paymentConfigList.get(0);
		}

		return paymentConfigValid;

	}

	public static HttpServletResponse runVTCGateData(
			HttpServletRequest request, HttpServletResponse response,
			VTCPay vtcPay) {

		try {

			Dossier dossier = null;
			PaymentFile paymentFile = null;

			boolean isVerify = VTCPay.validateSign(vtcPay);

			if (vtcPay.getReference_number().trim().length() > 0) {

				try {

					paymentFile = PaymentFileLocalServiceUtil
							.getByTransactionId(Long.parseLong(vtcPay
									.getReference_number()));

					dossier = DossierLocalServiceUtil.getDossier(paymentFile
							.getDossierId());
				} catch (Exception e) {

				}
			}

			if (isVerify && Validator.isNotNull(paymentFile)
					&& Validator.isNotNull(dossier)) {

				Double amountDouble = paymentFile.getAmount();
				int amountInt = amountDouble.intValue();

				if (String.valueOf(amountInt).equals(vtcPay.getAmount())) {

					if (paymentFile.getPaymentStatus() != PaymentMgtUtil.PAYMENT_STATUS_APPROVED) {

						// UserActionMsg actionMsg = new UserActionMsg();
						//
						// actionMsg.setAction(WebKeys.ACTION_PAY_VALUE);
						//
						// actionMsg.setPaymentFileId(paymentFile.getPaymentFileId());
						//
						// actionMsg.setDossierId(paymentFile.getDossierId());
						//
						// actionMsg.setCompanyId(dossier.getCompanyId());
						//
						// actionMsg.setGovAgencyCode(dossier.getGovAgencyCode());
						//
						// Message message = new Message();
						//
						// message.put("msgToEngine", actionMsg);
						//
						// MessageBusUtil.sendMessage("opencps/frontoffice/out/destination",
						// message);

						paymentFile
								.setPaymentStatus(PaymentMgtUtil.PAYMENT_STATUS_APPROVED);
						paymentFile
								.setPaymentMethod(WebKeys.PAYMENT_METHOD_KEYPAY);

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
										new Date(), 1, actorBean.getActor(),
										actorBean.getActorId(),
										actorBean.getActorName());

						NotificationUtils.sendNotificationToAccountant(dossier,
								paymentFile);
					}
					paymentFile.setPaymentGateStatusCode(Integer.valueOf(vtcPay
							.getStatus()));
				} else {
					paymentFile.setPaymentGateStatusCode(-100);
				}
				JSONObject jsonData = JSONFactoryUtil.createJSONObject();

				jsonData.put("amount", vtcPay.getAmount());
				jsonData.put("message", vtcPay.getMessage());
				jsonData.put("payment_type", vtcPay.getPaymentType());
				jsonData.put("reference_number", vtcPay.getReference_number());
				jsonData.put("status", vtcPay.getStatus());
				jsonData.put("trans_ref_no", vtcPay.getTrans_ref_no());
				jsonData.put("signature", vtcPay.getSignature());

				paymentFile.setKeypayGoodCode(vtcPay.getTrans_ref_no());
				paymentFile.setPaymentGateResponseData(jsonData.toString());

				PaymentFileLocalServiceUtil.updatePaymentFile(paymentFile);

			}

			if (Validator.isNotNull(dossier)
					&& Validator.isNotNull(paymentFile)) {

				String redirectUrl = dossier.getKeypayRedirectUrl().toString();

				StringBuffer param = new StringBuffer();
				param.append("&paymentFileId=").append(
						paymentFile.getPaymentFileId());
				param.append("&dossierId=").append(dossier.getDossierId());
				param.append("&serviceInfoId=").append(
						dossier.getServiceInfoId());

				redirectUrl += param;

				response.sendRedirect(redirectUrl);

			}

		} catch (SystemException | IOException | NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;
	}

	public static String runKeyPayGateData(HttpServletRequest request,
			HttpServletResponse response, KeyPay keyPay) {

		try {

			Dossier dossier = null;
			PaymentFile paymentFile = null;

			boolean isVerify = KeyPay.checkSecureHash(keyPay);

			if (keyPay.getMerchant_trans_id().trim().length() > 0) {

				try {
					paymentFile = PaymentFileLocalServiceUtil
							.getByTransactionId(Long.parseLong(keyPay
									.getMerchant_trans_id()));

					dossier = DossierLocalServiceUtil.getDossier(paymentFile
							.getDossierId());
				} catch (Exception e) {

				}
			}
			if (isVerify && Validator.isNotNull(paymentFile)
					&& Validator.isNotNull(dossier)) {

				if (Validator.isNotNull(paymentFile)
						&& (paymentFile.getPaymentStatus() != PaymentMgtUtil.PAYMENT_STATUS_APPROVED)) {

					paymentFile
							.setPaymentStatus(PaymentMgtUtil.PAYMENT_STATUS_APPROVED);
					paymentFile.setPaymentMethod(WebKeys.PAYMENT_METHOD_KEYPAY);
					paymentFile.setApproveDatetime(new Date());

					ActorBean actorBean = new ActorBean(1, dossier.getUserId());

					DossierLogLocalServiceUtil.addDossierLog(
							dossier.getUserId(), dossier.getGroupId(),
							dossier.getCompanyId(), dossier.getDossierId(),
							paymentFile.getFileGroupId(),
							PortletConstants.DOSSIER_STATUS_NEW,
							PortletConstants.DOSSIER_ACTION_CONFIRM_PAYMENT,
							PortletConstants.DOSSIER_ACTION_CONFIRM_PAYMENT,
							new Date(), 1, actorBean.getActor(),
							actorBean.getActorId(), actorBean.getActorName());

					NotificationUtils.sendNotificationToAccountant(dossier,
							paymentFile);
				}

				else {
					paymentFile.setPaymentGateStatusCode(Integer.valueOf(keyPay
							.getService_code()));
				}

				JSONObject jsonData = JSONFactoryUtil.createJSONObject();

				jsonData.put("command", keyPay.getCommand());
				jsonData.put("merchant_trans_id", keyPay.getMerchant_trans_id());
				jsonData.put("merchant_code", keyPay.getMerchant_code());
				jsonData.put("response_code", keyPay.getResponse_code());
				jsonData.put("trans_id", keyPay.getTrans_id());
				jsonData.put("good_code", keyPay.getGood_code());
				jsonData.put("net_cost", keyPay.getNet_cost());
				jsonData.put("ship_fee", keyPay.getShip_fee());
				jsonData.put("tax", keyPay.getTax());
				jsonData.put("service_code", keyPay.getService_code());
				jsonData.put("currency_code", keyPay.getCurrency_code());
				jsonData.put("bank_code", keyPay.getBank_code());
				jsonData.put("secure_hash", keyPay.getSecure_hash());
				jsonData.put("desc_1", keyPay.getDesc_1());
				jsonData.put("desc_2", keyPay.getDesc_2());
				jsonData.put("desc_3", keyPay.getDesc_3());
				jsonData.put("desc_4", keyPay.getDesc_4());
				jsonData.put("desc_5", keyPay.getDesc_5());

				paymentFile.setKeypayGoodCode(keyPay.getGood_code());
				paymentFile.setPaymentGateStatusCode(Integer.valueOf(keyPay
						.getService_code()));
				paymentFile.setPaymentGateResponseData(jsonData.toString());

				PaymentFileLocalServiceUtil.updatePaymentFile(paymentFile);

			}

			if (Validator.isNotNull(dossier)
					&& Validator.isNotNull(paymentFile)) {

				String redirectUrl = dossier.getKeypayRedirectUrl().toString();

				StringBuffer param = new StringBuffer();
				param.append("&paymentFileId=").append(
						paymentFile.getPaymentFileId());
				param.append("&dossierId=").append(dossier.getDossierId());
				param.append("&serviceInfoId=").append(
						dossier.getServiceInfoId());

				redirectUrl += param;

				response.sendRedirect(redirectUrl);

			}

		} catch (SystemException | IOException | NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static Log _log = LogFactoryUtil.getLog(PaymentMgtUtil.class);
}
