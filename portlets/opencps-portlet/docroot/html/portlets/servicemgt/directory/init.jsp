<%
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
%>
<%@ include file="../init.jsp" %>
<%
	PortletPreferences preferencesPortlet = renderRequest.getPreferences();
	
	portletResource = ParamUtil.getString(request, "portletResource");
	
	if (Validator.isNotNull(portletResource)) {
		preferencesPortlet = PortletPreferencesFactoryUtil.getPortletSetup(request, portletResource);
	}
	long plidServiceDetail = GetterUtil.getLong(preferences.getValue("plidServiceDetail","0"));
	long plidAddDossier = GetterUtil.getLong(preferences.getValue("plidAddDossier","0"));
	
	System.out.println("++++plidServiceDetail:"+plidServiceDetail);
	System.out.println("++++plidAddDossier:"+plidAddDossier);
	
	String style = preferences.getValue("style","default");
	
	boolean showListServiceTemplateFile = GetterUtil.getBoolean(preferences.getValue("showListServiceTemplateFile", null), true);
%>