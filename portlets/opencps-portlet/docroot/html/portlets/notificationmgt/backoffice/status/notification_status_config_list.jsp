
<%@page import="com.liferay.portal.kernel.dao.search.SearchEntry"%>
<%@page import="org.opencps.notificationmgt.search.NotificationStatusConfigDisplayTerms"%>
<%@page import="org.opencps.notificationmgt.service.NotificationStatusConfigLocalServiceUtil"%>
<%@page import="org.opencps.notificationmgt.model.impl.NotificationStatusConfigImpl"%>
<%@page import="org.opencps.notificationmgt.model.NotificationStatusConfig"%>
<%@page import="org.opencps.notificationmgt.permisson.NotificationStatusConfigPermission"%>
<%@page import="org.opencps.notificationmgt.search.NotificationStatusConfigSearch"%>
<%@page import="org.opencps.util.DateTimeUtil"%>
<%@page import="com.liferay.portal.kernel.dao.orm.QueryUtil"%>
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

<%@ include file="../../init.jsp"%>

<liferay-util:include page='<%=templatePath + "backoffice/status/toolbar.jsp"%>'
	servletContext="<%=application%>" />

<%
	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter("mvcPath", templatePath
	+ "notification_config_list.jsp");
	
%>
<div
	class="opencps-searchcontainer-wrapper-width-header default-box-shadow radius8">
	
	<liferay-ui:search-container
		searchContainer="<%=new NotificationStatusConfigSearch(renderRequest,
						SearchContainer.DEFAULT_DELTA, iteratorURL)%>">

		<liferay-ui:search-container-results>
			<%
				List<NotificationStatusConfig> notificationStatusConfigs = NotificationStatusConfigLocalServiceUtil
										.getNotificationStatusConfigs(searchContainer.getStart(),
												searchContainer.getEnd());

								int totalSize = NotificationStatusConfigLocalServiceUtil
										.getNotificationStatusConfigsCount();

								pageContext.setAttribute("results", notificationStatusConfigs);
								pageContext.setAttribute("total", totalSize);
			%>

		</liferay-ui:search-container-results>
		<liferay-ui:search-container-row
			className="org.opencps.notificationmgt.model.NotificationStatusConfig"
			modelVar="notificationStatusConfig" keyProperty="notificationStatusConfigId">
			<%
				PortletURL editURL = renderResponse.createRenderURL();
						editURL.setParameter("mvcPath",
								templatePath+"backoffice/status/notification_status_config_edit.jsp");
						editURL.setParameter(NotificationStatusConfigDisplayTerms.NOTICE_CONFIG_ID,
								String.valueOf(notificationStatusConfig.getNotiStatusConfigId()));
						editURL.setParameter(WebKeys.BACK_URL, currentURL);

						row.setClassName("opencps-searchcontainer-row");

						row.addText(String.valueOf(notificationStatusConfig.getNotiStatusConfigId()),
								editURL);
						row.addText(notificationStatusConfig.getDossierNextStatus(), editURL);
						
						row.addText(DateTimeUtil.convertDateToString(
								notificationStatusConfig.getCreateDate(),
								DateTimeUtil._VN_DATE_TIME_FORMAT), editURL);
						
						row.addText(DateTimeUtil.convertDateToString(
								notificationStatusConfig.getModifiedDate(),
								DateTimeUtil._VN_DATE_TIME_FORMAT), editURL);
						
						row.addText(
								String.valueOf(notificationStatusConfig.getIsSendNotification() == true ? LanguageUtil
										.get(pageContext, "active") : LanguageUtil
										.get(pageContext, "inactive")), editURL);

						if (isPermisson) {
							row.addJSP(
									"center",
									SearchEntry.DEFAULT_VALIGN,
									templatePath+"backoffice/status/notification_status_config_actions.jsp",
									config.getServletContext(), request, response);
						}
			%>

		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator type="opencs_page_iterator" />
	</liferay-ui:search-container>
</div>

<%!private static Log _log = LogFactoryUtil
			.getLog("html.portlets.notificationmgt.backoffice.notification_config_list.jsp");%>