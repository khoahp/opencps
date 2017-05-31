package com.fds.guideline;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;

import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portlet.PortletPreferencesFactoryUtil;

public class Configuration extends DefaultConfigurationAction {
	@Override
	public void processAction(PortletConfig portletConfig,
			ActionRequest actionRequest, ActionResponse actionResponse)
			throws Exception {
		super.processAction(portletConfig, actionRequest, actionResponse);

		String portletResource = ParamUtil.getString(actionRequest,
				"portletResource");

		PortletPreferences preferences = PortletPreferencesFactoryUtil
				.getPortletSetup(actionRequest, portletResource);

		String pageArea = ParamUtil.getString(actionRequest, "pageArea");

		preferences.setValue("pageArea", pageArea);

		preferences.store();

		SessionMessages.add(actionRequest, "config-stored");
	}
}
