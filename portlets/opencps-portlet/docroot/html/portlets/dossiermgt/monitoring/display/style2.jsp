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

<%@page import="com.liferay.portal.kernel.log.LogFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.log.Log"%>
<%@page import="com.liferay.portal.model.Layout"%>
<%@page import="com.liferay.portal.service.LayoutLocalServiceUtil"%>

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
	
	String submitDossierButtonPageUrl = themeDisplay.getPortalURL() + submitDossierButtonPage;
%>

<liferay-portlet:actionURL name="searchAction" var="searchUrl" plid="<%= dossierPagePlid %>"/>

<div class="monitoring-style2-wrapper">
	<div class="monitoring-style2-content">
		<h2><liferay-ui:message key="lookup-dossier"/></h2>
		<aui:form action="<%= searchUrl %>" method="post" name="fm">
			<input 
				class="search-query" 
				name="<portlet:namespace/>keywords" 
				type="text" 
				title='<liferay-ui:message key="enter-dossier-no" />'
				placeholder='<liferay-ui:message key="enter-dossier-no" />'
			>
			<liferay-ui:message key="enter-dossier-no-from-system" />
			<button class="btn search-button" type="submit" title='<liferay-ui:message key="enter-dossier-no-to-lookup" />'><i></i><liferay-ui:message key="keywords" /></button>
		</aui:form>
	</div>
	<div>
		<button onclick="redirectSubmit();" class="btn submit-dossier-button" type="submit" title='<liferay-ui:message key="submit-dossier-online" />'><i></i><liferay-ui:message key="submit-dossier-online" /></button>
	</div>
</div>

<aui:script>
	AUI().ready(function(A){
		A.one('.monitoring-style2-wrapper .search-button').on('click', function(event){
			event.preventDefault();
			if (A.one('.monitoring-style2-wrapper .search-query').val().length > 0){
				document.getElementById('<portlet:namespace />fm').submit();
			}
		});
	});
	
	var redirectSubmit = function(){
		var url = '<%=submitDossierButtonPageUrl %>';
		window.location.href = url;
	};
</aui:script>

<%!
	private Log _log = LogFactoryUtil.getLog("html_portlets_dossiermgt_monitoring_display_style1_jsp");
%>
