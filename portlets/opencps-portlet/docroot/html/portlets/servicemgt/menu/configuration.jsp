<%@page import="org.opencps.util.PortletPropsValues"%>
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>
 */
%>

<%@ include file="../init.jsp" %>

<%
String selGovAgencyCode = preferences.getValue("selGovAgencyCode","");

List<DictItem> govAgencies = DictItemLocalServiceUtil.findDictItemsByG_DC_S(scopeGroupId, PortletPropsValues.DATAMGT_MASTERDATA_GOVERNMENT_AGENCY);
%>

<liferay-ui:success key="potlet-config-saved" message="portlet-configuration-have-been-successfully-saved" />

<liferay-portlet:actionURL var="configurationActionURL" portletConfiguration="true"/>

<aui:form action="<%=configurationActionURL%>" method="post" name="configurationForm">
	<aui:select name="menuType" id="menuType">
		<aui:option value="administrator" label="administrator" selected='<%= menuTye.equalsIgnoreCase("administrator") %>'></aui:option>
		<aui:option value="domain" label="domain" selected='<%= menuTye.equalsIgnoreCase("domain") %>' ></aui:option>
		<aui:option value="administrator_domain" label="administrator-domain" selected='<%= menuTye.equalsIgnoreCase("administrator_domain") %>'></aui:option>
		<aui:option value="govagency" label="co-quan-thuc-hien" selected='<%= menuTye.equalsIgnoreCase("govagency") %>'></aui:option>
	</aui:select>

	<aui:select name="selGovAgencyCode" showEmptyOption="<%= true %>">
		<% 
			for(DictItem item : govAgencies) { 
			
			
		%>
			<aui:option selected='<%= selGovAgencyCode.equals(item.getItemCode()) %>' value="<%= item.getItemCode() %>" label="<%= item.getItemName(locale) %>" />
		<% } %>
	</aui:select>

	<aui:button type="submit" name="Save" value="save"></aui:button>
</aui:form>
