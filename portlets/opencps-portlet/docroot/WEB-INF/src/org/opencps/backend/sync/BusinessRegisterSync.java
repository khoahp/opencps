package org.opencps.backend.sync;

import java.util.Date;

import org.opencps.dossiermgt.model.BusinessRegister;
import org.opencps.dossiermgt.service.BusinessRegisterLocalServiceUtil;
import org.opencps.processmgt.model.ServiceProcess;
import org.opencps.processmgt.service.ServiceProcessLocalServiceUtil;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.messaging.MessageListenerException;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.service.ServiceContext;

public class BusinessRegisterSync implements MessageListener {

	public static final String BUSINESS_REGISTER = "BUSINESS_REGISTER";
	public static final String BUSINESS_REG_STATUS_DONE = "DONE";
	public static final String BUSINESS_REG_STATUS_CANCEL = "CANCEL";
	public static final String BUSINESS_REG_STATUS_DENIED = "DENIED";

	@Override
	public void receive(Message msg) throws MessageListenerException {

		String msgContent = msg.getString("content");

		try {
			JSONObject jsonContent = JSONFactoryUtil
					.createJSONObject(msgContent);

			long groupId = jsonContent.getLong("groupId");
			long companyId = jsonContent.getLong("companyId");
			long userId = jsonContent.getLong("userId");

			long ownerOrganizationId = jsonContent
					.getLong("ownerOrganizationId");
			long dossierTemplateId = jsonContent.getLong("dossierTemplateId");
			long serviceProcessId = jsonContent.getLong("serviceProcessId");
			long dossierId = jsonContent.getLong("dossierId");
			String dossierSubStatus = jsonContent.getString("dossierSubStatus");
			String dossierStatus = jsonContent.getString("dossierStatus");

			ServiceProcess serviceProcess = ServiceProcessLocalServiceUtil
					.getServiceProcess(serviceProcessId);

			String serviceCode = serviceProcess.getProcessNo();

			if (serviceCode.contentEquals(BUSINESS_REGISTER)) {
				_log.info("BUSINESS_REGISTER : this is the SERVICE PROCESS for BUSINESS_REGISTER"
						+ serviceProcessId);

				// Checking register status

				BusinessRegister businessReg = getBusinessReg(ownerOrganizationId);
				
				ServiceContext context = new ServiceContext();
				context.setCompanyId(companyId);
				context.setScopeGroupId(groupId);
				context.setUserId(userId);

				int businessStatus = 0;

				if (Validator.isNotNull(businessReg)
						&& businessReg.getValidStatus() == 0) {
					// Update BusinessRegister status
					
					businessStatus = mappingRegisterStatus(dossierStatus);
					
					businessReg.setValidStatus(businessStatus);
					businessReg.setModifiedDate(new Date());
					
					BusinessRegisterLocalServiceUtil.updateBusinessRegister(businessReg);
					
				} else {
					// Create BusinessRegister

					businessReg = BusinessRegisterLocalServiceUtil.addBusinessRegister(
							ownerOrganizationId, dossierTemplateId,
							serviceProcessId, dossierId, businessStatus, context);
				}
			}

			_log.info("groupId : " + groupId);
			_log.info("companyId : " + companyId);
			_log.info("userId: " + userId);
			_log.info("dossierTemplateId: " + dossierTemplateId);
			_log.info("serviceProcessId: " + serviceProcessId);
			_log.info("dossierId: " + dossierId);
			_log.info("dossierStatus: " + dossierStatus);
			_log.info("ownerOrganizationId: " + ownerOrganizationId);
			_log.info("dossierSubStatus: " + dossierSubStatus);

		} catch (Exception e) {
			_log.info("LOI_CMNR");
		}

	}

	/**
	 * Mapping BusinessReg's DOSSIER status with regStatus
	 * 
	 * @param dossierStatus
	 * @return
	 */
	private int mappingRegisterStatus(String dossierStatus) {

		if (dossierStatus.toUpperCase().contentEquals(BUSINESS_REG_STATUS_DONE)) {
			return 1;
		}

		if (dossierStatus.toUpperCase().contentEquals(
				BUSINESS_REG_STATUS_CANCEL)
				| dossierStatus.toUpperCase().contentEquals(
						BUSINESS_REG_STATUS_DENIED))
			return 2;

		return 0;
	}

	/**
	 * Get Business by orgId
	 * 
	 * @param ownerOrganizationId
	 * @return {@link BusinessRegister}
	 */
	private BusinessRegister getBusinessReg(long ownerOrganizationId) {

		BusinessRegister br = null;

		try {
			br = BusinessRegisterLocalServiceUtil
					.getBusinessRegisterByOrgId(ownerOrganizationId);
		} catch (Exception e) {
			// Nothing to do
		}

		return br;
	}

	private Log _log = LogFactoryUtil.getLog(BusinessRegisterSync.class);
}
