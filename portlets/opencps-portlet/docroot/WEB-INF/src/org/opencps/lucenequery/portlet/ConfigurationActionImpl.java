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
/**
 * 
 */
package org.opencps.lucenequery.portlet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portlet.PortletPreferencesFactoryUtil;

/**
 * @author trungnt
 *
 */
public class ConfigurationActionImpl implements ConfigurationAction {

	@Override
	public void processAction(PortletConfig portletConfig,
			ActionRequest actionRequest, ActionResponse actionResponse)
			throws Exception {

		String portletResource = ParamUtil.getString(actionRequest,
				"portletResource");

		PortletPreferences preferences = PortletPreferencesFactoryUtil
				.getPortletSetup(actionRequest, portletResource);

		int[] fieldsIndexes = StringUtil.split(
				ParamUtil.getString(actionRequest, "fieldsIndexes"), 0);

		if (fieldsIndexes != null && fieldsIndexes.length > 0) {

			String[] levels = new String[fieldsIndexes.length];
			String[] names = new String[fieldsIndexes.length];
			String[] patterns = new String[fieldsIndexes.length];
			String[] params = new String[fieldsIndexes.length];
			String[] paramTypes = new String[fieldsIndexes.length];
			for (int f = 0; f < fieldsIndexes.length; f++) {

				String level = ParamUtil.getString(actionRequest, "level"
						+ fieldsIndexes[f]);
				String name = ParamUtil.getString(actionRequest, "name"
						+ fieldsIndexes[f]);
				String pattern = ParamUtil.getString(actionRequest, "pattern"
						+ fieldsIndexes[f]);
				String param = ParamUtil.getString(actionRequest, "param"
						+ fieldsIndexes[f]);
				String paramType = ParamUtil.getString(actionRequest,
						"paramType" + fieldsIndexes[f]);

				levels[f] = level;
				names[f] = name;
				patterns[f] = pattern;
				params[f] = param;
				paramTypes[f] = paramType;

			}

			preferences.setValues("levels", levels);
			preferences.setValues("names", names);
			preferences.setValues("patterns", patterns);
			preferences.setValues("params", params);
			preferences.setValues("paramTypes", paramTypes);
		}
		
		String layoutUUID =  ParamUtil.getString(actionRequest, "layoutUUID");
		
		String targetPortletName = ParamUtil.getString(actionRequest, "targetPortletName");
		
		preferences.setValue("layoutUUID", layoutUUID);
		
		preferences.setValue("targetPortletName", targetPortletName);
		
		preferences.store();

		SessionMessages.add(actionRequest, "potlet-config-saved");

	}

	@Override
	public String render(PortletConfig arg0, RenderRequest arg1,
			RenderResponse arg2) throws Exception {

		// TODO Auto-generated method stub
		return "/html/portlets/lucenequery/menu/configuration.jsp";
	}

}
