
<%@page import="org.opencps.notificationmgt.search.NotificationStatusConfigDisplayTerms"%>
<%@page import="org.opencps.notificationmgt.service.NotificationRedirectConfigLocalServiceUtil"%>
<%@page import="org.opencps.notificationmgt.model.impl.NotificationRedirectConfigImpl"%>
<%@page import="org.opencps.notificationmgt.model.NotificationRedirectConfig"%>
<%@page import="com.liferay.portal.service.LayoutLocalServiceUtil"%>
<%@page import="com.liferay.portal.model.Layout"%>
<%@page import="org.opencps.util.LayoutView"%>
<%@page import="org.opencps.util.LayoutLister"%>
<%@page import="org.opencps.notificationmgt.service.NotificationEventConfigLocalServiceUtil"%>
<%@page import="org.opencps.notificationmgt.model.impl.NotificationEventConfigImpl"%>
<%@page import="org.opencps.notificationmgt.model.NotificationEventConfig"%>
<%@page import="org.opencps.notificationmgt.search.NotificationEventConfigDisplayTerms"%>
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

<portlet:actionURL var="updateNotificationEventConfigURL" name="updateNotificationEventConfig" />

<%
	long notiEventConfigId = ParamUtil.getLong(request,NotificationEventConfigDisplayTerms.NOTICE_EVENT_CONFIG_ID,0);
	String backURL = ParamUtil.getString(request, WebKeys.BACK_URL,StringPool.BLANK);
	
	long notiStatusConfigId = ParamUtil.getLong(request,NotificationStatusConfigDisplayTerms.NOTICE_CONFIG_ID,0);

	NotificationEventConfig notiEventConfig = new NotificationEventConfigImpl();
	NotificationRedirectConfig notiRedirectConfig = new NotificationRedirectConfigImpl();

	if (notiEventConfigId > 0) {

		try {
			notiEventConfig = NotificationEventConfigLocalServiceUtil.fetchNotificationEventConfig(notiEventConfigId);
			
			if(Validator.isNotNull(notiEventConfig)){
				
				notiRedirectConfig = NotificationRedirectConfigLocalServiceUtil.fetchNotificationRedirectConfig(notiEventConfig.getNotiEventConfigId());
			}
		} catch (Exception e) {
			_log.error(e);
		}
	}
	
	LayoutLister layoutLister = new LayoutLister();

	String rootNodeName = StringPool.BLANK;
	
	LayoutView layoutView  = layoutLister.getLayoutView(layout.getGroupId(), layout.isPrivateLayout(), rootNodeName, locale);
	
	List layoutList = layoutView.getList();
%>

<liferay-ui:header
	title='<%=Validator.isNull(notiEventConfig) ? "add-notification-status-config"
					: "update-notification-status-config"%>' />


<div class=" opencps-bound-wrapper pd20 default-box-shadow"">
	<div class="edit-form">

		<liferay-ui:error key="<%= MessageKeys.NOTIFICATION_STATUS_SYSTEM_EXCEPTION_OCCURRED%>" 
		message="<%=MessageKeys.NOTIFICATION_STATUS_SYSTEM_EXCEPTION_OCCURRED %>" />
		
		<liferay-ui:success key="<%= MessageKeys.NOTIFICATION_STATUS_ADD_SUCESS%>" 
		message="<%= MessageKeys.NOTIFICATION_STATUS_ADD_SUCESS%>"/>
		
		<liferay-ui:success key="<%= MessageKeys.NOTIFICATION_STATUS_UPDATE_SUCESS%>" 
		message="<%= MessageKeys.NOTIFICATION_STATUS_UPDATE_SUCESS%>"/>

		<aui:form action="<%=updateNotificationEventConfigURL.toString()%>"
			method="post" name="fm">

			<aui:model-context bean="<%=notiEventConfig%>"
				model="<%=NotificationEventConfig.class%>" />
			<aui:input name="<%=NotificationEventConfigDisplayTerms.NOTICE_EVENT_CONFIG_ID%>"
				type="hidden"
				value="<%=String.valueOf(notiEventConfig.getNotiEventConfigId()) %>" />
				
			<aui:input name="<%=NotificationStatusConfigDisplayTerms.NOTICE_CONFIG_ID%>"
				type="hidden" 
				value="<%=String.valueOf(notiStatusConfigId) %>"/>
			<aui:input name="<%=WebKeys.BACK_URL%>" type="hidden"
				value="<%=backURL%>" />
			<aui:input name="<%=WebKeys.CURRENT_URL%>" type="hidden"
				value="<%=currentURL%>" />
			<aui:fieldset>
			
				<aui:input type="text" name="<%=NotificationEventConfigDisplayTerms.EVENT_NAME %>" value="<%=notiEventConfig.getEventName() %>"/>
				
				<aui:input type="text" name="<%=NotificationEventConfigDisplayTerms.DESCRIPTION %>" value="<%=notiEventConfig.getDescription() %>"/>
				
				<aui:input type="text" name="<%=NotificationEventConfigDisplayTerms.PATTERN %>" value="<%=notiEventConfig.getPattern() %>"/>
	
				<aui:select label="root-layout" name="<%=NotificationEventConfigDisplayTerms.NOTICE_REDIRECT_CONFIG_ID %>">
					<aui:option value="" />

				<%
				for (int i = 0; i < layoutList.size(); i++) {
	
					// id | parentId | ls | obj id | name | img | depth
	
					String layoutDesc = (String)layoutList.get(i);
	
					String[] nodeValues = StringUtil.split(layoutDesc, '|');
	
					long objId = GetterUtil.getLong(nodeValues[3]);
					String name = HtmlUtil.escape(nodeValues[4]);
	
					int depth = 0;
	
					if (i != 0) {
						depth = GetterUtil.getInteger(nodeValues[6]);
					}
	
					for (int j = 0; j < depth; j++) {
						name = "-&nbsp;" + name;
					}
	
					Layout curRootLayout = null;
	
					try {
						curRootLayout = LayoutLocalServiceUtil.getLayout(objId);
					}
					catch (Exception e) {
					}
	
					if (curRootLayout != null) {
				%>
	
					<aui:option label="<%= name %>" 
						selected="<%=Validator.isNotNull(notiRedirectConfig)? curRootLayout.getPlid() ==notiRedirectConfig.getPlId():false %>" 
						value="<%= curRootLayout.getPlid() %>" />
	
				<%
					}
				}
				%>

				</aui:select>
				
				<aui:input name="<%=NotificationEventConfigDisplayTerms.ACTIVE%>" 
							type="checkbox" 
							label="inuse" 
							value="<%=Validator.isNotNull(notiRedirectConfig) ?notiRedirectConfig.isActive():false %>"></aui:input>
				
			
			
<%-- 			<aui:select name="<%=NotificationEventConfigDisplayTerms.JSP_REDIRECT %>"> --%>
<%-- 				<aui:option  --%>
<%-- 					selected="<%=Validator.isNotNull(notiRedirectConfig) ? notiRedirectConfig.getJspRedirect().equals("/html/portlets/processmgt/processorder/process_order_detail.jsp"):false %>"  --%>
<%-- 					value='<%=HtmlUtil.escape("/html/portlets/processmgt/processorder/process_order_detail.jsp") %>'/> --%>
<%-- 				<aui:option --%>
<%-- 					selected="<%=Validator.isNotNull(notiRedirectConfig) ? notiRedirectConfig.getJspRedirect().equals("/html/portlets/dossiermgt/frontoffice/edit_dossier.jsp"):false %>" --%>
<%-- 					value='<%=HtmlUtil.escape("/html/portlets/dossiermgt/frontoffice/edit_dossier.jsp") %>' --%>
<%-- 					/> --%>
<%-- 				<aui:option --%>
<%-- 					selected="<%=Validator.isNotNull(notiRedirectConfig) ? notiRedirectConfig.getJspRedirect().equals("/html/portlets/paymentmgt/frontoffice/frontofficepaymentdetail.jsp"):false %>" --%>
<%-- 					value='<%=HtmlUtil.escape("/html/portlets/paymentmgt/frontoffice/frontofficepaymentdetail.jsp") %>' --%>
<%-- 				 	/> --%>
<%-- 				<aui:option --%>
<%-- 					selected="<%=Validator.isNotNull(notiRedirectConfig) ? notiRedirectConfig.getJspRedirect().equals("/html/portlets/paymentmgt/backoffice/backofficepaymentdetail.jsp"):false %>" --%>
<%-- 					value='<%=HtmlUtil.escape("/html/portlets/paymentmgt/backoffice/backofficepaymentdetail.jsp") %>' --%>
<%-- 				 	/> --%>
<%-- 			</aui:select> --%>

				<aui:button type="submit" name="submit" icon="icon-save" />
			</aui:fieldset>
		</aui:form>
	</div>
</div>

<%!private Log _log = LogFactoryUtil
			.getLog("html.portlets.notificationmgt.backoffice.notification_status_config_edit");%>
