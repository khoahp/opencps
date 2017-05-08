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

import javax.portlet.RenderRequest;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;

import org.opencps.accountmgt.model.Business;
import org.opencps.accountmgt.model.Citizen;
import org.opencps.backend.util.PaymentRequestGenerator;
import org.opencps.dossiermgt.bean.AccountBean;
import org.opencps.dossiermgt.model.Dossier;
import org.opencps.dossiermgt.model.impl.DossierImpl;
import org.opencps.dossiermgt.search.DossierDisplayTerms;
import org.opencps.dossiermgt.service.DossierLocalServiceUtil;
import org.opencps.notificationmgt.engine.UserNotificationHandler;
import org.opencps.notificationmgt.fac.SendNotificationMessageFactory;
import org.opencps.notificationmgt.message.SendNotificationMessage;
import org.opencps.notificationmgt.model.NotificationEventConfig;
import org.opencps.notificationmgt.model.NotificationStatusConfig;
import org.opencps.notificationmgt.service.NotificationEventConfigLocalServiceUtil;
import org.opencps.notificationmgt.service.NotificationStatusConfigLocalServiceUtil;
import org.opencps.paymentmgt.model.PaymentFile;
import org.opencps.paymentmgt.search.PaymentFileDisplayTerms;
import org.opencps.processmgt.model.ProcessOrder;
import org.opencps.processmgt.model.ProcessStep;
import org.opencps.processmgt.model.ProcessWorkflow;
import org.opencps.processmgt.search.ProcessOrderDisplayTerms;
import org.opencps.processmgt.service.ProcessOrderLocalServiceUtil;
import org.opencps.processmgt.service.ProcessStepLocalServiceUtil;
import org.opencps.processmgt.service.ProcessWorkflowLocalServiceUtil;
import org.opencps.processmgt.util.ProcessUtils;
import org.opencps.usermgt.model.Employee;
import org.opencps.util.AccountUtil;
import org.opencps.util.SendMailUtils;
import org.opencps.util.WebKeys;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.User;
import com.liferay.portal.model.UserNotificationEvent;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.service.UserNotificationEventLocalServiceUtil;
import com.liferay.util.portlet.PortletProps;

/**
 * @author nhanhoang
 */

public class NotificationUtils {

	private static Log _log = LogFactoryUtil.getLog(NotificationUtils.class);

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
			SendNotificationMessage message,
			SendNotificationMessage.Infomations.Infomation info) {

		JSONObject payloadJSONObject = JSONFactoryUtil.createJSONObject();

		long plId = Long.valueOf(message.getPlId());
		StringBuffer title = new StringBuffer();
		StringBuffer content = new StringBuffer();
		
		String receptionNo = StringPool.BLANK;
		String actionName = StringPool.BLANK;
		String note = StringPool.BLANK;
		
		Locale locale = LocaleUtil.getDefault();

		try {

			Dossier dossiser = null;
			long dossierId = Long.valueOf(message.getDossierId());

			if (dossierId > 0) {

				dossiser = DossierLocalServiceUtil.getDossier(dossierId);

				title.append(StringUtil.replace(
						LanguageUtil.get(locale, "opencps.notificationmgt.title"),
						new String[] {"{receptionNo}", "{dossierId}"},
						new String[] {dossiser.getReceptionNo(),
								String.valueOf(dossierId) }));

				content.append(StringUtil.replace(
						LanguageUtil.get(locale, "opencps.notificationmgt.body"),
						new String[] {"{actionName}","{actionNote}"},
						new String[] {message.getEventName(),message.getNote()}));
				
				receptionNo = dossiser.getReceptionNo();
				actionName = message.getEventName();
				note  = message.getNote();
			}

		} catch (Exception e) {
			_log.error(e);
		}

		payloadJSONObject.put("processOrderId", message.getProcessOrderId());
		payloadJSONObject.put("dossierId", message.getDossierId());
		payloadJSONObject.put("paymentFileId", message.getPaymentFileId());
		payloadJSONObject.put("patternConfig", message.getPatternConfig());
		payloadJSONObject.put("userIdDelivery", info.getUserId());
		payloadJSONObject.put("title", title.toString());
		payloadJSONObject.put("notificationText", content.toString());
		payloadJSONObject.put("plId", plId);
		payloadJSONObject.put("receptionNo", receptionNo);
		payloadJSONObject.put("actionName", actionName);
		payloadJSONObject.put("note", note);
		payloadJSONObject.put("mvcPath", message.getJspRedirect());

		return payloadJSONObject;
	}

	public static void sendEmailNotification(SendNotificationMessage message,
			SendNotificationMessage.Infomations.Infomation info) {

		String fromAddress = StringPool.BLANK;
		String fromName = StringPool.BLANK;
		String to = StringPool.BLANK;
		String subject = StringPool.BLANK;
		String body = StringPool.BLANK;
		boolean htmlFormat = true;

		Locale locale = LocaleUtil.getDefault();

		try {

			long dossierId = Long.valueOf(message.getDossierId());

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
			to = info.getEmailAddress();
			subject = LanguageUtil.get(locale, "subject.email.to.customer");
			if (Validator.isNull(dossier.getReceptionNo())) {
				body = LanguageUtil.get(locale, "content.email.to.customer.without.receptionNo");
			} else {
				body = LanguageUtil.get(locale, "content.email.to.customer");
			}
			subject = StringUtil.replace(subject, "{systemName}",fromName);

			body = StringUtil.replace(body, "{receiverUserName}",info.getFullName());
			body = StringUtil.replace(body, "{systemName}", fromName);
			body = StringUtil.replace(body, "{dossierId}",
					String.valueOf(message.getDossierId()));
			body = StringUtil.replace(body, "{receptionNo}",
					dossier.getReceptionNo());
			body = StringUtil.replace(body, "{event}",
					PortletProps.get(message.getEventName()));
			body = StringUtil
					.replace(body, "{message}", message.getNote());

			SendMailUtils.sendEmail(fromAddress, fromName, to,
					StringPool.BLANK, subject, body, htmlFormat);
		} catch (Exception e) {
			_log.error(e);
		}
	}

	public static void sendNotificationToAccountant(Dossier dossier,
			PaymentFile paymentFile) {

		try {

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

	public static List<SendNotificationMessage> sendNotification(
			long processWorkflowId, long dossierId, long paymentFileId,
			long processOrderId,String actionNote) {

		List<SendNotificationMessage> notificationList = new ArrayList<SendNotificationMessage>();

		try {

			if (processWorkflowId > 0) {

				ProcessWorkflow processWorkflow = null;
				Dossier dossier = null;
				ProcessOrder processOrder = null;

				try {
					processWorkflow = ProcessWorkflowLocalServiceUtil
							.fetchProcessWorkflow(processWorkflowId);

					dossier = DossierLocalServiceUtil.fetchDossier(dossierId);

					processOrder = ProcessOrderLocalServiceUtil
							.findBy_Dossier(dossierId);

				} catch (Exception e) {

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
						} catch (Exception e) {

						}

						if (Validator.isNotNull(postProcessStep)) {

							notificationList = getListNoties(
									processWorkflow,
									dossier,
									paymentFileId,
									Validator.isNotNull(processOrder) ? processOrder
											.getProcessOrderId() : 0,
									postProcessStep,actionNote, isPay);

						}
					}

				}
			} else {
				_log.warn("===processWorkflowId:" + processWorkflowId);
			}
		} catch (Exception e) {
			_log.error(e);
		}

		return notificationList;
	}

	private static List<SendNotificationMessage> getListNoties(
			ProcessWorkflow processWorkflow, Dossier dossier,
			long paymentFileId, long processOrderId, ProcessStep processStep,String actionNote,
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

							if (notiEventConfig.getPattern().toUpperCase()
									.contains(PortletKeys.CITIZEN)) {

								/*
								 * Xac dinh user chu ho so can notice
								 */
								SendNotificationMessageFactory notiFactory = new SendNotificationMessageFactory();
								SendNotificationMessage notiMsgCitizen = notiFactory
										.createSendNotificationMessage();

								notiMsgCitizen = setMessageCitizens(
										processWorkflow, dossier.getUserId(),
										dossier.getGroupId(),
										dossier.getDossierId(), paymentFileId,
										processOrderId, notiEventConfig,actionNote);

								notificationList.add(notiMsgCitizen);
							} else if (notiEventConfig.getPattern()
									.toUpperCase()
									.contains(PortletKeys.EMPLOYEE)) {

								/*
								 * Xac dinh danh sach can bo lien quan den ho so
								 * can notice
								 */

								SendNotificationMessageFactory notiFactory = new SendNotificationMessageFactory();
								SendNotificationMessage notiMsgEmploy = notiFactory
										.createSendNotificationMessage();

								notiMsgEmploy = setMessageEmployee(
										processWorkflow,
										dossier.getDossierId(),
										dossier.getGroupId(), processOrderId,
										paymentFileId, notiEventConfig,actionNote);

								notificationList.add(notiMsgEmploy);
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
	private static List<Employee> getListEmploy(
			ProcessWorkflow processWorkflow, long groupId) {

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

	private static SendNotificationMessage setMessageCitizens(
			ProcessWorkflow processWorkflow, long userId, long groupId,
			long dossierId, long paymentFileId, long processOrderId,
			NotificationEventConfig notiEventConfig,String actionNote) {

		AccountBean accountBean = AccountUtil.getAccountBean(userId, groupId,
				null);

		Citizen citizen = null;
		Business bussines = null;

		SendNotificationMessageFactory notiFactory = new SendNotificationMessageFactory();
		SendNotificationMessage notiMsgCitizen = notiFactory
				.createSendNotificationMessage();

		if (accountBean.isCitizen() || accountBean.isBusiness()) {

			notiMsgCitizen.setDossierId(String.valueOf(dossierId));
			notiMsgCitizen.setPaymentFileId(String.valueOf(paymentFileId));
			notiMsgCitizen.setProcessOrderId(String.valueOf(processOrderId));
			notiMsgCitizen.setNote(actionNote);

			if (notiEventConfig.getPattern().toUpperCase()
					.contains(PortletKeys.USE_EVENT_DESCRIPTION)) {
				notiMsgCitizen.setEventName(notiEventConfig.getDescription());
			} else {
				notiMsgCitizen.setEventName(processWorkflow.getActionName());
			}
			notiMsgCitizen.setPatternConfig(notiEventConfig.getPattern());
			notiMsgCitizen.setPlId(notiEventConfig.getPlId());
			notiMsgCitizen.setJspRedirect(notiEventConfig.getJspRedirect());

			SendNotificationMessage.Infomations citizenInfos = notiFactory
					.createSendNotificationMessageInfomations();

			if (accountBean.isCitizen()) {
				citizen = (Citizen) accountBean.getAccountInstance();
			} else if (accountBean.isBusiness()) {
				bussines = (Business) accountBean.getAccountInstance();
			}

			SendNotificationMessage.Infomations.Infomation citizenInfo = notiFactory
					.createSendNotificationMessageInfomationsInfomation();

			if (Validator.isNotNull(citizen)) {

				citizenInfo.setUserId(String.valueOf(citizen.getUserId()));
				citizenInfo.setEmailAddress(citizen.getEmail());
				citizenInfo.setFullName(citizen.getFullName());
				citizenInfo.setPhoneNumber(citizen.getTelNo());

			} else if (Validator.isNotNull(bussines)) {

				citizenInfo.setUserId(String.valueOf(bussines.getUserId()));
				citizenInfo.setEmailAddress(bussines.getEmail());
				citizenInfo.setFullName(bussines.getName());
				citizenInfo.setPhoneNumber(bussines.getTelNo());
			}

			citizenInfos.getInfomation().add(citizenInfo);

			notiMsgCitizen.setInfomations(citizenInfos);

		}

		return notiMsgCitizen;
	}

	private static SendNotificationMessage setMessageEmployee(
			ProcessWorkflow processWorkflow, long dossierId, long groupId,
			long processOrderId, long paymentFileId,
			NotificationEventConfig notiEventConfig,String actionNote) {

		List<Employee> coordinateEmployeeList = getListEmploy(processWorkflow,
				groupId);

		SendNotificationMessageFactory notiFactory = new SendNotificationMessageFactory();
		SendNotificationMessage notiMsgEmploy = notiFactory
				.createSendNotificationMessage();

		SendNotificationMessage.Infomations infoEmployList = notiFactory
				.createSendNotificationMessageInfomations();

		notiMsgEmploy.setDossierId(String.valueOf(dossierId));
		notiMsgEmploy.setProcessOrderId(String.valueOf(processOrderId));
		notiMsgEmploy.setPaymentFileId(String.valueOf(paymentFileId));
		notiMsgEmploy.setNote(actionNote);

		if (notiEventConfig.getPattern().toUpperCase()
				.contains(PortletKeys.USE_EVENT_DESCRIPTION)) {
			notiMsgEmploy.setEventName(notiEventConfig.getDescription());
		} else {
			notiMsgEmploy.setEventName(processWorkflow.getActionName());
		}
		notiMsgEmploy.setPatternConfig(notiEventConfig.getPattern());
		notiMsgEmploy.setPlId(notiEventConfig.getPlId());
		notiMsgEmploy.setJspRedirect(notiEventConfig.getJspRedirect());

		for (Employee employee : coordinateEmployeeList) {

			if (!employee.getEmail().trim()
					.contains(notiEventConfig.getUserExcept())) {

				SendNotificationMessage.Infomations.Infomation coordinateInfoEmploy = notiFactory
						.createSendNotificationMessageInfomationsInfomation();

				coordinateInfoEmploy.setUserId(String.valueOf(employee
						.getMappingUserId()));
				coordinateInfoEmploy.setEmailAddress(employee.getEmail());
				coordinateInfoEmploy.setPhoneNumber(employee.getTelNo());
				coordinateInfoEmploy.setFullName(employee.getFullName());

				infoEmployList.getInfomation().add(coordinateInfoEmploy);
			}
		}
		notiMsgEmploy.setInfomations(infoEmployList);

		return notiMsgEmploy;

	}
	
	public static String getLink(UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext, RenderRequest renderRequest)
			throws JSONException, WindowStateException {

		String pattern = StringPool.BLANK;
		long processOrderId = 0;
		long dossierId = 0;
		long paymentFileId = 0;

		LiferayPortletResponse liferayPortletResponse = null;

		LiferayPortletURL viewURL = null;

		try {

			if (Validator.isNotNull(renderRequest)
					&& Validator.isNull(serviceContext)) {
				
				try{

				serviceContext = ServiceContextFactory
						.getInstance(renderRequest);
				}catch(PortalException e){
					_log.error(e);
				}

			}

			liferayPortletResponse = serviceContext
					.getLiferayPortletResponse();
			

			JSONObject jsonObject = JSONFactoryUtil
					.createJSONObject(userNotificationEvent.getPayload());

			dossierId = jsonObject.getLong("dossierId");
			paymentFileId = jsonObject.getLong("paymentFileId");
			processOrderId = jsonObject.getLong("processOrderId");
			pattern = jsonObject.getString("patternConfig");

			long plId = jsonObject.getString("plId").trim().length() > 0 ? Long
					.parseLong(jsonObject.getString("plId")) : 0;

			if (pattern.toUpperCase().contains(PortletKeys.EMPLOYEE)
					&& paymentFileId <= 0 && processOrderId > 0) {

				if (Validator.isNull(viewURL)) {

					viewURL = liferayPortletResponse
							.createRenderURL(WebKeys.PROCESS_ORDER_PORTLET);
				}

				viewURL.setParameter("mvcPath",
						"/html/portlets/processmgt/processorder/process_order_detail.jsp");
				viewURL.setParameter(ProcessOrderDisplayTerms.PROCESS_ORDER_ID,
						String.valueOf(processOrderId));
				viewURL.setPlid(plId);
				viewURL.setWindowState(WindowState.NORMAL);

			} else if (pattern.toUpperCase().contains(PortletKeys.CITIZEN)
					&& paymentFileId <= 0 && dossierId > 0) {

				if (Validator.isNull(viewURL)) {

					viewURL = liferayPortletResponse
							.createRenderURL(WebKeys.DOSSIER_MGT_PORTLET);
				}

				viewURL.setParameter("mvcPath",
						"/html/portlets/dossiermgt/frontoffice/edit_dossier.jsp");
				viewURL.setParameter(DossierDisplayTerms.DOSSIER_ID,
						String.valueOf(dossierId));
				viewURL.setParameter("isEditDossier", String.valueOf(false));
				viewURL.setPlid(plId);
				viewURL.setWindowState(WindowState.NORMAL);

			} else if (pattern.toUpperCase().contains(PortletKeys.EMPLOYEE)
					&& paymentFileId > 0) {

				if (Validator.isNull(viewURL)) {
					viewURL = liferayPortletResponse
							.createRenderURL(WebKeys.PAYMENT_MANAGER_PORTLET);
				}

				viewURL.setParameter("mvcPath",
						"/html/portlets/paymentmgt/backoffice/backofficepaymentdetail.jsp");
				viewURL.setParameter(PaymentFileDisplayTerms.PAYMENT_FILE_ID,
						String.valueOf(paymentFileId));
				viewURL.setPlid(plId);
				viewURL.setWindowState(WindowState.NORMAL);

			} else if (pattern.toUpperCase().contains(PortletKeys.CITIZEN)
					&& paymentFileId > 0) {

				if (Validator.isNull(viewURL)) {
					viewURL = liferayPortletResponse
							.createRenderURL(WebKeys.PAYMENT_MGT_PORTLET);
				}

				viewURL.setParameter("mvcPath",
						"/html/portlets/paymentmgt/frontoffice/frontofficepaymentdetail.jsp");
				viewURL.setParameter(PaymentFileDisplayTerms.PAYMENT_FILE_ID,
						String.valueOf(paymentFileId));
				viewURL.setPlid(plId);
				viewURL.setWindowState(WindowState.NORMAL);

			}

		} catch (Exception e) {
			_log.error(e);
		}
		
		
		return Validator.isNotNull(viewURL) ? viewURL.toString()
				: StringPool.BLANK;
	}
	
	public static boolean updateArchived(long userNotificationEventId) {

		UserNotificationEvent userNotificationEvent = null;

		try {
			userNotificationEvent = UserNotificationEventLocalServiceUtil
					.getUserNotificationEvent(userNotificationEventId);

			userNotificationEvent.setArchived(true);

			userNotificationEvent = UserNotificationEventLocalServiceUtil
					.updateUserNotificationEvent(userNotificationEvent);
			
			return true;
		} catch (Exception e) {

		}
		return false;
	}
}
