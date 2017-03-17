<%@page import="org.opencps.util.PortletPropsValues"%>
<%@page import="org.opencps.util.DataMgtUtils"%>
<%@page import="org.opencps.datamgt.model.DictItem"%>
<%@page import="org.opencps.datamgt.service.DictCollectionLocalServiceUtil"%>
<%@page import="org.opencps.datamgt.model.impl.DictCollectionImpl"%>
<%@page import="org.opencps.datamgt.model.DictCollection"%>
<%@page import="org.opencps.notificationmgt.service.NotificationStatusConfigLocalServiceUtil"%>
<%@page import="org.opencps.notificationmgt.model.impl.NotificationStatusConfigImpl"%>
<%@page import="org.opencps.notificationmgt.model.NotificationStatusConfig"%>
<%@page import="org.opencps.notificationmgt.search.NotificationStatusConfigDisplayTerms"%>
<%@page import="net.sf.jasperreports.util.NoWriteFieldHandler"%>
<%@page import="org.opencps.util.PortletUtil"%>
<%@page import="java.util.Date"%>
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
<%@page import="org.opencps.util.MessageKeys"%>
<%@page import="org.opencps.util.WebKeys"%>
<%@page import="com.liferay.portal.kernel.log.LogFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.log.Log"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@page import="javax.portlet.PortletRequest"%>
<%@page import="com.liferay.portlet.PortletURLFactoryUtil"%>
<%@ include file="../../init.jsp"%>

<portlet:actionURL var="updateNofificationConfigURL" name="updateNofificationConfig" />

<%
	long notificationConfigId = ParamUtil.getLong(request,NotificationStatusConfigDisplayTerms.NOTICE_CONFIG_ID,0);
	String backURL = ParamUtil.getString(request, WebKeys.BACK_URL);

	NotificationStatusConfig notificationConfig = new NotificationStatusConfigImpl();

	if (notificationConfigId > 0) {

		try {
			notificationConfig = NotificationStatusConfigLocalServiceUtil.getNotificationStatusConfig(notificationConfigId);
		} catch (Exception e) {
			_log.error(e);
		}
	}
	
	
	
	
	DataMgtUtils dataMgtUtils = new DataMgtUtils();
	
	List<DictItem> dictItems = dataMgtUtils.getDictItemList(themeDisplay.getScopeGroupId(), PortletPropsValues.DM_USER_GROUP_NOTIFY);
%>

<liferay-ui:header backURL="<%=backURL%>"
	title='<%=(notificationConfig == null) ? "add-notification-config"
					: "update-notification-config"%>' />


<div class=" opencps-bound-wrapper pd20 default-box-shadow"">
	<div class="edit-form">

		<liferay-ui:error key="<%= MessageKeys.HOLIDAYCONFIG_SYSTEM_EXCEPTION_OCCURRED%>" 
		message="<%=MessageKeys.HOLIDAYCONFIG_SYSTEM_EXCEPTION_OCCURRED %>" />
		
		<liferay-ui:success key="<%= MessageKeys.HOLIDAYCONFIG_ADD_SUCESS%>" 
		message="<%= MessageKeys.HOLIDAYCONFIG_ADD_SUCESS%>"/>
		
		<liferay-ui:success key="<%= MessageKeys.HOLIDAYCONFIG_UPDATE_SUCESS%>" 
		message="<%= MessageKeys.HOLIDAYCONFIG_UPDATE_SUCESS%>"/>

		<aui:form action="<%=updateNofificationConfigURL.toString()%>"
			method="post" name="fm">

			<aui:model-context bean="<%=notificationConfig%>"
				model="<%=NotificationStatusConfig.class%>" />
			<aui:input name="<%=NotificationStatusConfigDisplayTerms.NOTICE_CONFIG_ID%>"
				type="hidden" />
			<aui:input name="<%=WebKeys.REDIRECT_URL%>" type="hidden"
				value="<%=backURL%>" />
			<aui:input name="<%=WebKeys.RETURN_URL%>" type="hidden"
				value="<%=currentURL%>" />
			<aui:fieldset>

				<aui:select name="<%=NotificationStatusConfigDisplayTerms.DOSSIER_NEXT_STATUS%>"
					label="status">
					
					<% for (DictItem dictItem: dictItems){ %>
					
					<aui:option label="<%= dictItem.getItemName(themeDisplay.getLocale()) %>" 
								selected="<%= dictItem.getItemCode().equals(notificationConfig.getDossierNextStatus())%>" 
								value="<%= dictItem.getItemCode() %>" />
					<%} %>
				</aui:select>
				
				<aui:input name=""></aui:input>


			</aui:fieldset>

			<aui:fieldset>
				<aui:button type="submit" name="submit" value="submit" />
				<aui:button type="reset" value="clear" />
			</aui:fieldset>
		</aui:form>
	</div>
</div>

<%!private Log _log = LogFactoryUtil
			.getLog("html.portlets.holidayconfig.admin.holidayconfig_edit.jsp");%>
