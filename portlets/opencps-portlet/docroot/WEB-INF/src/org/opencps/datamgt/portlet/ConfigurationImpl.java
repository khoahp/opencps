package org.opencps.datamgt.portlet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portlet.PortletPreferencesFactoryUtil;

/**
 * @author phucnv
 * @date May 8, 2017
 */
public class ConfigurationImpl implements ConfigurationAction{

	@Override
	public void processAction(PortletConfig arg0, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		
		String portletResource =
			    ParamUtil.getString(actionRequest, "portletResource");
		PortletPreferences preferences =
			    PortletPreferencesFactoryUtil.getPortletSetup(
			        actionRequest, portletResource);
		
		String viewTemplate = ParamUtil.getString(actionRequest, "view-template");
		
		preferences.setValue("view-template", viewTemplate);
		
		preferences.store();
		
		SessionMessages.add(actionRequest, "config-stored");
	}

	@Override
	public String render(PortletConfig arg0, RenderRequest arg1,
			RenderResponse arg2) throws Exception {
		
		return "/html/portlets/data_management/admin/configuration.jsp";
	}

}
