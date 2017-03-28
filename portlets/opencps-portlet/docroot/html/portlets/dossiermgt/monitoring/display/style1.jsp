
<%@page import="com.liferay.portal.model.Layout"%>
<%@page import="com.liferay.portal.service.LayoutLocalServiceUtil"%>
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

<%@page import="org.opencps.util.PortletConstants"%>
<%@page import="java.text.Format"%>
<%@page import="javax.portlet.PortletRequest"%>
<%@page import="com.liferay.portlet.PortletURLFactoryUtil"%>
<%@page import="com.liferay.portal.theme.ThemeDisplay"%>
<%@page import="com.liferay.portal.kernel.log.LogFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.log.Log"%>
<%@page import="com.liferay.portal.kernel.exception.SystemException"%>
<%@page import="javax.portlet.PortletURL"%>
<%@page import="com.liferay.portal.kernel.util.FastDateFormatFactoryUtil"%>
<%@page import="org.opencps.dossiermgt.service.DossierStatusLocalServiceUtil"%>
<%@page import="org.opencps.dossiermgt.model.DossierStatus"%>
<%@page import="org.opencps.servicemgt.service.ServiceInfoLocalServiceUtil"%>
<%@page import="org.opencps.servicemgt.model.ServiceInfo"%>
<%@page import="org.opencps.dossiermgt.service.DossierLocalServiceUtil"%>
<%@page import="org.opencps.datamgt.service.DictItemLocalServiceUtil"%>
<%@page import="org.opencps.datamgt.model.DictItem"%>
<%@page import="org.opencps.datamgt.service.DictCollectionLocalServiceUtil"%>
<%@page import="org.opencps.datamgt.model.DictCollection"%>
<%@page import="org.opencps.dossiermgt.model.Dossier"%>
<%@page import="java.util.List"%>
<%@page import="org.opencps.dossiermgt.search.DossierSearchTerms"%>
<%@page import="org.opencps.dossiermgt.search.DossierNewProcessingSearch"%>

<%@ include file="../../init.jsp"%>

<%
String dossierpage = portletPreferences.getValue("dossierpage", null);
long dossierPagePlid = themeDisplay.getPlid();

if(Validator.isNotNull(dossierpage)){
	try {
		
		Layout dossierPageLayout = LayoutLocalServiceUtil.getFriendlyURLLayout(themeDisplay.getScopeGroupId(), false, dossierpage);
		
		if(dossierPageLayout != null) {
			dossierPagePlid = dossierPageLayout.getPlid();
		}
		
	} catch (Exception e) {
		_log.error(e);
		
	}
}
%>

<liferay-portlet:actionURL name="searchAction" var="searchUrl" plid="<%= dossierPagePlid %>"/>

<div class="monitoring-style1-wrapper">
	<div class="status_header">
		<p class="text-center"><liferay-ui:message key="search-dossier-status"/></p>
	</div>
	<div class="monitoring-style1-content">
		<p><liferay-ui:message key="monitoring-style1-heading"/></p>
		<aui:form action="<%= searchUrl %>" method="post" name="fm">
			<input class="search-query" name="<portlet:namespace/>keywords" type="text" title='<liferay-ui:message key="keywords" />'>
		
			<button class="btn" type="submit"><i></i> <liferay-ui:message key="keywords" /></button>
		</aui:form>
	</div>
</div>

<%!
	private Log _log = LogFactoryUtil.getLog("html_portlets_dossiermgt_monitoring_display_style1_jsp");
%>
