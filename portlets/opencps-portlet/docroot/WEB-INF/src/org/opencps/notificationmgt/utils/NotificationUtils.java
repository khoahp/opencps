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

package org.opencps.notificationmgt.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.opencps.accountmgt.model.Business;
import org.opencps.accountmgt.model.Citizen;
import org.opencps.backend.util.PaymentRequestGenerator;
import org.opencps.dossiermgt.bean.AccountBean;
import org.opencps.dossiermgt.model.Dossier;
import org.opencps.dossiermgt.model.ServiceConfig;
import org.opencps.dossiermgt.model.impl.DossierImpl;
import org.opencps.dossiermgt.service.DossierLocalServiceUtil;
import org.opencps.dossiermgt.service.ServiceConfigLocalServiceUtil;
import org.opencps.notificationmgt.engine.UserNotificationHandler;
import org.opencps.notificationmgt.message.SendNotificationMessage;
import org.opencps.notificationmgt.model.NotificationEventConfig;
import org.opencps.notificationmgt.model.NotificationRedirectConfig;
import org.opencps.notificationmgt.model.NotificationStatusConfig;
import org.opencps.notificationmgt.service.NotificationEventConfigLocalServiceUtil;
import org.opencps.notificationmgt.service.NotificationRedirectConfigLocalServiceUtil;
import org.opencps.notificationmgt.service.NotificationStatusConfigLocalServiceUtil;
import org.opencps.paymentmgt.model.PaymentFile;
import org.opencps.processmgt.model.ProcessStep;
import org.opencps.processmgt.model.ProcessWorkflow;
import org.opencps.processmgt.model.StepAllowance;
import org.opencps.processmgt.service.ProcessStepLocalServiceUtil;
import org.opencps.processmgt.service.ProcessWorkflowLocalServiceUtil;
import org.opencps.processmgt.service.StepAllowanceLocalServiceUtil;
import org.opencps.processmgt.util.ProcessUtils;
import org.opencps.usermgt.model.Employee;
import org.opencps.usermgt.service.EmployeeLocalServiceUtil;
import org.opencps.util.AccountUtil;
import org.opencps.util.MessageBusKeys;
import org.opencps.util.PortletPropsValues;
import org.opencps.util.SendMailUtils;

import com.liferay.portal.NoSuchLayoutFriendlyURLException;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.service.UserNotificationEventLocalServiceUtil;
import com.liferay.util.portlet.PortletProps;

/**
 * @author nhanhoang
 */

public class NotificationUtils {

	private static Log _log = LogFactoryUtil.getLog(NotificationUtils.class);

	protected static String CITIZEN = "CITIZEN";
	protected static String EMPLOYEE = "EMPLOYEE";

	public static void addUserNotificationEvent(
			SendNotificationMessage message, JSONObject payloadJSON,
			long userIdDelivery) {

		try {

			ServiceContext serviceContext = new ServiceContext();

			UserNotificationEventLocalServiceUtil.addUserNotificationEvent(
					userIdDelivery, UserNotificationHandler.PORTLET_ID,
					(new Date()).getTime(), 0, payloadJSON.toString(), false,
					serviceContext);

		} catch (Exception e) {
			_log.error(e);
		}
	}

	public static JSONObject createNotification(
			SendNotificationMessage message, String event, String group,
			long userIdDelivery, boolean privatePage, long groupId) {

		JSONObject payloadJSONObject = JSONFactoryUtil.createJSONObject();
		Locale locale = new Locale("vi", "VN");

		long plId = 0;
		StringBuffer title = new StringBuffer();
		StringBuffer content = new StringBuffer();

		try {

			title.append("[").append(message.getDossierId()).append("]")
					.append(LanguageUtil.get(locale, event));

			Layout layOut = null;
			try {
				layOut = LayoutLocalServiceUtil.getFriendlyURLLayout(groupId,
						true, group);
			} catch (NoSuchLayoutFriendlyURLException e) {

			}

			if (Validator.isNotNull(layOut)) {
				plId = layOut.getPlid();
			}

			Dossier dossiser = null;

			if (message.getDossierId() > 0) {

				dossiser = DossierLocalServiceUtil.getDossier(message
						.getDossierId());

				content.append(dossiser.getReceptionNo()).append("<br>")
						.append(message.getNotificationContent());
			}

		} catch (Exception e) {
			_log.error(e);
		}

		payloadJSONObject.put("processOrderId", message.getProcessOrderId());
		payloadJSONObject.put("dossierId", message.getDossierId());
		payloadJSONObject.put("paymentFileId", message.getPaymentFileId());
		payloadJSONObject.put("userIdDelivery", userIdDelivery);
		payloadJSONObject.put("title", title.toString());
		payloadJSONObject.put("notificationText", content.toString());
		payloadJSONObject.put("plId", plId);
		payloadJSONObject.put("friendlyUrl", group);
		payloadJSONObject.put("groupId", groupId);

		return payloadJSONObject;
	}

	public static void sendEmailNotification(SendNotificationMessage message,
			String email, long dossierId, String userName) {

		String fromAddress = StringPool.BLANK;
		String fromName = StringPool.BLANK;
		String to = StringPool.BLANK;
		String subject = StringPool.BLANK;
		String body = StringPool.BLANK;
		boolean htmlFormat = true;

		Locale locale = new Locale("vi", "VN");

		try {

			Dossier dossier = new DossierImpl();

			if (dossierId > 0) {
				dossier = DossierLocalServiceUtil.getDossier(dossierId);
			}

			fromAddress = Validator.isNotNull(dossier) ? PrefsPropsUtil
					.getString(dossier.getCompanyId(),
							PropsKeys.ADMIN_EMAIL_FROM_ADDRESS)
					: StringPool.BLANK;
			fromName = PrefsPropsUtil.getString(dossier.getCompanyId(),
					PropsKeys.ADMIN_EMAIL_FROM_NAME);
			to = email;
			subject = PortletPropsValues.SUBJECT_TO_CUSTOMER;
			if (Validator.isNull(dossier.getReceptionNo())) {
				body = PortletPropsValues.CONTENT_TO_CUSTOMER_WITHOUT_RECEPTION_NO;
			} else {
				body = PortletPropsValues.CONTENT_TO_CUSTOMER;
			}
			subject = StringUtil.replace(subject, "[OpenCPS]", "[" + fromName
					+ "]");

			body = StringUtil.replace(body, "[receiverUserName]", "["
					+ userName + "]");
			body = StringUtil.replace(body, "{OpenCPS}", fromName);
			body = StringUtil.replace(body, "{dossierId}",
					String.valueOf(message.getDossierId()));
			body = StringUtil.replace(body, "{receptionNo}",
					dossier.getReceptionNo());
			body = StringUtil.replace(body, "{event}",
					PortletProps.get(message.getNotificationEventName()));
			body = StringUtil.replace(body, "{message}",
					message.getNotificationContent());

			_log.info("fromAddress:" + fromAddress);
			_log.info("subject:" + subject);
			_log.info("to:" + to);

			SendMailUtils.sendEmail(fromAddress, fromName, to,
					StringPool.BLANK, subject, body, htmlFormat);
		} catch (Exception e) {
			_log.error(e);
		}
	}

	public static void sendNotificationToAccountant(Dossier dossier,
			PaymentFile paymentFile) {

		try {
			List<SendNotificationMessage> lsNotification = new ArrayList<SendNotificationMessage>();

			// ADD EVENT VAN THU
			SendNotificationMessage notiMsg = new SendNotificationMessage();
			notiMsg.setDossierId(dossier.getDossierId());
			notiMsg.setNotificationEventName(NotificationEventKeys.OFFICIALS.EVENT10);
			notiMsg.setPaymentFileId(paymentFile.getPaymentFileId());
			notiMsg.setType("SMS, INBOX, EMAIL");

			SendNotificationMessage.InfoList infoEmploy = new SendNotificationMessage.InfoList();

			List<SendNotificationMessage.InfoList> infoListEmploy = new ArrayList<SendNotificationMessage.InfoList>();

			ServiceConfig serviceConfig = ServiceConfigLocalServiceUtil
					.fetchServiceConfig(dossier.getServiceConfigId());

			List<ProcessWorkflow> processWorkflowList = ProcessWorkflowLocalServiceUtil
					.searchWorkflow(serviceConfig.getServiceProcessId(),
							QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			ProcessWorkflow processWorkflow = null;

			Employee employee = null;

			MainLoop: for (int i = 0; i < processWorkflowList.size(); i++) {

				processWorkflow = processWorkflowList.get(i);

				if (processWorkflow.getPaymentFee().trim().length() > 0
						&& processWorkflow.getRequestPayment()) {

					List<String> emailList = getEmailFromPattern(processWorkflow
							.getPaymentFee());

					User user = null;
					String email = StringPool.BLANK;
					if (emailList.size() > 0) {
						for (int k = 0; k < emailList.size(); k++) {
							email = emailList.get(k);

							if (Validator.isEmailAddress(email.trim())) {
								_log.info("email:" + email);
								user = UserLocalServiceUtil
										.getUserByEmailAddress(
												dossier.getCompanyId(), email);
								_log.info("user:" + user.getUserId());

								employee = EmployeeLocalServiceUtil
										.getEmployeeByEmail(
												dossier.getGroupId(), email);

								if (Validator.isNotNull(user)
										&& Validator.isNotNull(employee)) {

									infoEmploy.setUserId(user.getUserId());
									infoEmploy.setUserMail(employee.getEmail());
									infoEmploy
											.setUserPhone(employee.getTelNo());
									infoEmploy.setGroupId(dossier.getGroupId());
									infoEmploy
											.setGroup(NotificationEventKeys.GROUP4);
									infoEmploy.setFullName(employee
											.getFullName());
								}
								infoListEmploy.add(infoEmploy);
								break MainLoop;
							}
						}

					}
				}

			}
			notiMsg.setInfoList(infoListEmploy);

			lsNotification.add(notiMsg);

			// ADD EVENT CONG DAN

			AccountBean accountBean = AccountUtil.getAccountBean(
					dossier.getUserId(), dossier.getGroupId(), null);

			Citizen citizen = null;
			Business bussines = null;

			if (accountBean.isCitizen()) {
				citizen = (Citizen) accountBean.getAccountInstance();
			}
			if (accountBean.isBusiness()) {
				bussines = (Business) accountBean.getAccountInstance();
			}
			notiMsg = new SendNotificationMessage();
			notiMsg.setDossierId(dossier.getDossierId());
			notiMsg.setNotificationEventName(NotificationEventKeys.USERS_AND_ENTERPRISE.EVENT10);
			notiMsg.setPaymentFileId(paymentFile.getPaymentFileId());
			notiMsg.setType("SMS, INBOX, EMAIL");

			SendNotificationMessage.InfoList info = new SendNotificationMessage.InfoList();

			List<SendNotificationMessage.InfoList> infoList = new ArrayList<SendNotificationMessage.InfoList>();

			if (Validator.isNotNull(citizen)) {
				info.setUserId(citizen.getUserId());
				info.setUserMail(citizen.getEmail());
				info.setUserPhone(citizen.getTelNo());
				info.setFullName(citizen.getFullName());

			} else if (Validator.isNotNull(bussines)) {
				info.setUserId(bussines.getUserId());
				info.setUserMail(bussines.getEmail());
				info.setUserPhone(bussines.getTelNo());
				info.setFullName(bussines.getName());

			}

			info.setGroup(NotificationEventKeys.GROUP3);
			info.setGroupId(dossier.getGroupId());
			infoList.add(info);

			Locale vnLocale = new Locale("vi", "VN");

			notiMsg.setNotificationContent(LanguageUtil.get(vnLocale,
					"payment-order-done"));
			notiMsg.setInfoList(infoList);

			lsNotification.add(notiMsg);

			_log.info("=====lsNotification.size():" + lsNotification.size());
			if (lsNotification.size() > 0) {

				Message msgNoti = new Message();

				msgNoti.put(MessageBusKeys.Message.NOTIFICATIONS,
						lsNotification);

				MessageBusUtil.sendMessage(
						MessageBusKeys.Destination.NOTIFICATIONS, msgNoti);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static List<String> getEmailFromPattern(String pattern) {

		List<String> emailList = new ArrayList<String>();

		String[] emailArrays = StringUtil.split(pattern, "|");

		if (emailArrays.length > 0) {

			emailList = Arrays.asList(emailArrays);

		}
		return emailList;

	}

	public List<SendNotificationMessage> sendNotification(
			long processWorkflowId, long dossierId, long paymentFileId,
			long processOrderId) {

		List<SendNotificationMessage> notificationList = new ArrayList<SendNotificationMessage>();

		try {

			if (processWorkflowId > 0) {

				ProcessWorkflow processWorkflow = null;
				Dossier dossier = null;

				try {
					processWorkflow = ProcessWorkflowLocalServiceUtil
							.fetchProcessWorkflow(processWorkflowId);

					dossier = DossierLocalServiceUtil.fetchDossier(dossierId);

				} catch (SystemException e) {

				}

				if (Validator.isNotNull(processWorkflow)
						&& Validator.isNotNull(dossier)) {

					List<String> paymentMethods = PaymentRequestGenerator
							.getPaymentMethod(processWorkflow.getPaymentFee());

					boolean isPay = false;

					if (paymentMethods
							.contains(PaymentRequestGenerator.PAY_METHOD_KEYPAY)
							|| paymentMethods
									.contains(PaymentRequestGenerator.PAY_METHOD_BANK)
							|| paymentMethods
									.contains(PaymentRequestGenerator.PAY_METHOD_CASH)) {

						isPay = true;

					}

					// long preProcessStepId = 0;
					long postProcessStepId = 0;

					// preProcessStepId = processWorkflow.getPreProcessStepId();
					postProcessStepId = processWorkflow.getPostProcessStepId();

					// ProcessStep preProcessStep = null;
					ProcessStep postProcessStep = null;

					if (postProcessStepId > 0) {
						try {
							postProcessStep = ProcessStepLocalServiceUtil
									.fetchProcessStep(postProcessStepId);
						} catch (SystemException e) {

						}

						if (Validator.isNotNull(postProcessStep)) {

							notificationList = getListNoties(processWorkflow,
									dossier, paymentFileId, processOrderId,
									postProcessStep, isPay);

						}
					}

				}
			}
		} catch (Exception e) {
			_log.error(e);
		}

		return notificationList;
	}

	private List<SendNotificationMessage> getListNoties(
			ProcessWorkflow processWorkflow, Dossier dossier,
			long paymentFileId, long processOrderId, ProcessStep processStep,
			boolean isPayment) {

		List<SendNotificationMessage> notificationList = new ArrayList<SendNotificationMessage>();

		if (Validator.isNotNull(processStep)) {

			try {

				NotificationStatusConfig notiStatusConfig = null;

				notiStatusConfig = NotificationStatusConfigLocalServiceUtil
						.getByDossierNextStatus(processStep.getDossierStatus(),
								true);

				if (Validator.isNotNull(notiStatusConfig)) {

					List<NotificationEventConfig> notiEventConfigs = new ArrayList<NotificationEventConfig>();

					notiEventConfigs = NotificationEventConfigLocalServiceUtil
							.getNotificationEvents(
									notiStatusConfig.getNotiStatusConfigId(),
									true);

					if (notiEventConfigs.size() > 0) {

						for (NotificationEventConfig notiEventConfig : notiEventConfigs) {

							NotificationRedirectConfig notiRedirectConfig = null;

							notiRedirectConfig = NotificationRedirectConfigLocalServiceUtil
									.getNotificationRedirectConfig(
											notiEventConfig
													.getNotiEventConfigId(),
											true);

							if (Validator.isNotNull(notiRedirectConfig)) {

								if (notiEventConfig.getPattern().toUpperCase()
										.contains(CITIZEN)) {

									/*
									 * Xac dinh user chu ho so can notice
									 */
									SendNotificationMessage notiMsgCitizen = new SendNotificationMessage();

									notiMsgCitizen = setMessageCitizens(
											dossier.getUserId(),
											dossier.getGroupId(),
											dossier.getDossierId(),
											paymentFileId, notiEventConfig,
											notiRedirectConfig);

									notificationList.add(notiMsgCitizen);
								} else if (notiEventConfig.getPattern()
										.toUpperCase().contains(EMPLOYEE)) {

									/*
									 * Xac dinh danh sach can bo lien quan den
									 * ho so can notice
									 */

									SendNotificationMessage notiMsgEmploy = new SendNotificationMessage();

									notiMsgEmploy = setMessageEmployee(
											processWorkflow,
											dossier.getUserId(),
											processOrderId, paymentFileId,
											notiEventConfig, notiRedirectConfig);

									notificationList.add(notiMsgEmploy);
								}

							}
						}
					}
				}

			} catch (Exception e) {
				_log.error(e);
			}
		}

		return notificationList;
	}

	/**
	 * @param processWorkflow
	 * @param assignToUserId
	 * @return
	 */
	private List<Employee> getListEmploy(ProcessWorkflow processWorkflow,
			long groupId) {

		List<Employee> ls = new ArrayList<>();

		try {
			List<User> users = ProcessUtils.getAssignUsers(
					processWorkflow.getPostProcessStepId(), 3);

			for (User user : users) {
				AccountBean accountEmploy = AccountUtil.getAccountBean(
						user.getUserId(), groupId, null);

				Employee employee = (Employee) accountEmploy
						.getAccountInstance();

				ls.add(employee);

			}

		} catch (Exception e) {

		}

		return ls;

	}

	private SendNotificationMessage setMessageCitizens(long userId,
			long groupId, long dossierId, long paymentFileId,
			NotificationEventConfig notiEventConfig,
			NotificationRedirectConfig notiRedirectConfig) {

		AccountBean accountBean = AccountUtil.getAccountBean(userId, groupId,
				null);

		Citizen citizen = null;
		Business bussines = null;

		SendNotificationMessage notiMsgCitizen = new SendNotificationMessage();

		if (accountBean.isCitizen() || accountBean.isBusiness()) {

			notiMsgCitizen.setDossierId(dossierId);
			notiMsgCitizen.setPaymentFileId(paymentFileId);

			SendNotificationMessage.InfoList citizenInfo = new SendNotificationMessage.InfoList();
			List<SendNotificationMessage.InfoList> citizenInfoList = new ArrayList<SendNotificationMessage.InfoList>();

			notiMsgCitizen.setType(notiEventConfig.getPattern());
			notiMsgCitizen.setNotificationContent(notiEventConfig
					.getDescription());
			notiMsgCitizen.setNotificationEventName(notiEventConfig
					.getEventName());

			citizenInfo.setPlid(String.valueOf(notiRedirectConfig.getPlId()));

			if (accountBean.isCitizen()) {
				citizen = (Citizen) accountBean.getAccountInstance();
			} else if (accountBean.isBusiness()) {
				bussines = (Business) accountBean.getAccountInstance();
			}

			if (Validator.isNotNull(citizen)) {
				citizenInfo.setUserId(citizen.getUserId());
				citizenInfo.setUserMail(citizen.getEmail());
				citizenInfo.setUserPhone(citizen.getTelNo());
				citizenInfo.setFullName(citizen.getFullName());

			} else if (Validator.isNotNull(bussines)) {
				citizenInfo.setUserId(bussines.getUserId());
				citizenInfo.setUserMail(bussines.getEmail());
				citizenInfo.setUserPhone(bussines.getTelNo());
				citizenInfo.setFullName(bussines.getName());
			}
			citizenInfo.setGroupId(groupId);
			citizenInfoList.add(citizenInfo);
			
			notiMsgCitizen.setInfoList(citizenInfoList);

		}

		return notiMsgCitizen;
	}

	private SendNotificationMessage setMessageEmployee(
			ProcessWorkflow processWorkflow, long groupId, long processOrderId,
			long paymentFileId, NotificationEventConfig notiEventConfig,
			NotificationRedirectConfig notiRedirectConfig) {

		List<Employee> coordinateEmployeeList = getListEmploy(processWorkflow,
				groupId);

		SendNotificationMessage notiMsgEmploy = new SendNotificationMessage();
		List<SendNotificationMessage.InfoList> infoEmployList = new ArrayList<SendNotificationMessage.InfoList>();

		notiMsgEmploy.setDossierId(groupId);
		notiMsgEmploy.setProcessOrderId(processOrderId);
		notiMsgEmploy.setPaymentFileId(paymentFileId);
		notiMsgEmploy.setNotificationEventName(notiEventConfig.getEventName());
		notiMsgEmploy.setNotificationContent(notiEventConfig.getDescription());
		notiMsgEmploy.setType(notiEventConfig.getPattern());

		for (Employee employee : coordinateEmployeeList) {

			SendNotificationMessage.InfoList coordinateInfoEmploy = new SendNotificationMessage.InfoList();

			coordinateInfoEmploy.setUserId(employee.getMappingUserId());
			coordinateInfoEmploy.setUserMail(employee.getEmail());
			coordinateInfoEmploy.setUserPhone(employee.getTelNo());
			coordinateInfoEmploy.setGroupId(groupId);
			coordinateInfoEmploy.setFullName(employee.getFullName());

			boolean flag = false;
			try {
				List<Role> listRole = RoleLocalServiceUtil
						.getUserRoles(employee.getMappingUserId());
				for (Role role : listRole) {
					StepAllowance stepAllowance = StepAllowanceLocalServiceUtil
							.getStepAllowance(
									processWorkflow.getPostProcessStepId(),
									role.getRoleId());
					if (Validator.isNotNull(stepAllowance)) {
						flag = true;
						break;
					}
				}
			} catch (SystemException e) {
				_log.error(e);
			}
			if (flag) {

				coordinateInfoEmploy.setPlid(String.valueOf(notiRedirectConfig
						.getPlId()));

				infoEmployList.add(coordinateInfoEmploy);
			}

		}
		// /////////////////////////////////////////////
		notiMsgEmploy.setInfoList(infoEmployList);

		return notiMsgEmploy;

	}
}
