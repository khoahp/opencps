package org.opencps.backend.sync;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.messaging.MessageListenerException;

public class BusinessRegisterSync implements MessageListener{

	@Override
	public void receive(Message msg) throws MessageListenerException {
		
		String msgContent = msg.getString("content");
		
		try {
			JSONObject jsonContent = JSONFactoryUtil.createJSONObject(msgContent);
			
			long groupId = jsonContent.getLong("groupId");
			long companyId = jsonContent.getLong("companyId");
			long userId = jsonContent.getLong("userId");
			
			long ownerOrganizationId = jsonContent.getLong("ownerOrganizationId");
			long dossierTemplateId = jsonContent.getLong("dossierTemplateId");
			long serviceProcessId = jsonContent.getLong("serviceProcessId");
			long dossierId = jsonContent.getLong("dossierId");
			
			String  dossierStatus = jsonContent.getString("dossierStatus");
			
			_log.info("groupId : " + groupId);
			_log.info("companyId : "+ companyId);
			_log.info("userId: " + userId);
			_log.info("dossierTemplateId: " + dossierTemplateId);
			_log.info("serviceProcessId: " + serviceProcessId);
			_log.info("dossierId: " + dossierId);
			_log.info("dossierStatus: " + dossierStatus);
			_log.info("ownerOrganizationId: " + ownerOrganizationId);

		} catch (Exception e) {
			_log.info("LOI_CMNR");
		}
		
		
	}
	
	private Log _log = LogFactoryUtil.getLog(BusinessRegisterSync.class);
}
