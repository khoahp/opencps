<%@page import="org.opencps.notificationmgt.search.NotificationStatusConfigDisplayTerms"%>
<%@page import="org.opencps.notificationmgt.search.NotificationEventConfigDisplayTerms"%>
<%@page import="org.opencps.notificationmgt.service.NotificationEventConfigLocalServiceUtil"%>
<%@page import="org.opencps.notificationmgt.model.NotificationEventConfig"%>
<%@page import="org.opencps.notificationmgt.search.NotificationEventConfigSearch"%>
<%@page import="com.liferay.portal.kernel.dao.search.SearchEntry"%>
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

<liferay-util:include page='<%=templatePath + "backoffice/event/toolbar.jsp"%>'
	servletContext="<%=application%>" />

<%
	long notiStatusConfigId = ParamUtil.getLong(request,NotificationStatusConfigDisplayTerms.NOTICE_CONFIG_ID,0);
	
	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter("mvcPath", templatePath
	+ "backoffice/event/notification_event_config_list.jsp");
	
%>
<div
	class="opencps-searchcontainer-wrapper-width-header default-box-shadow radius8">
	
	<liferay-ui:search-container
		searchContainer="<%=new NotificationEventConfigSearch(renderRequest,
						SearchContainer.DEFAULT_DELTA, iteratorURL)%>">

		<liferay-ui:search-container-results>
			<%
				List<NotificationEventConfig> notificationEventConfigs = new ArrayList<NotificationEventConfig>();
				
				if(notiStatusConfigId > 0){
					
					notificationEventConfigs = NotificationEventConfigLocalServiceUtil.getByNotiStatusConfigIds(notiStatusConfigId);
					
				}else{
			
					notificationEventConfigs = NotificationEventConfigLocalServiceUtil
											.getNotificationEventConfigs(searchContainer.getStart(),
													searchContainer.getEnd());
				}
				

				int totalSize = notificationEventConfigs.size();

				pageContext.setAttribute("results", notificationEventConfigs);
				pageContext.setAttribute("total", totalSize);
			%>

		</liferay-ui:search-container-results>
		<liferay-ui:search-container-row
			className="org.opencps.notificationmgt.model.NotificationEventConfig"
			modelVar="notificationEventConfig" keyProperty="notiEventConfigId">
			<%
				PortletURL editURL = renderResponse.createRenderURL();
						editURL.setParameter("mvcPath",
								templatePath+"backoffice/event/notification_event_config_edit.jsp");
						editURL.setParameter(NotificationEventConfigDisplayTerms.NOTICE_EVENT_CONFIG_ID,
								String.valueOf(notificationEventConfig.getNotiEventConfigId()));
						editURL.setParameter(WebKeys.BACK_URL, currentURL);

						row.setClassName("opencps-searchcontainer-row");
						row.addText(String.valueOf(row.getPos() +1),
								editURL);
						row.addText(DateTimeUtil.convertDateToString(
								notificationEventConfig.getCreateDate(),
								DateTimeUtil._VN_DATE_TIME_FORMAT), editURL);
						row.addText(DateTimeUtil.convertDateToString(
								notificationEventConfig.getModifiedDate(),
								DateTimeUtil._VN_DATE_TIME_FORMAT), editURL);
						row.addText(notificationEventConfig.getEventName(), editURL);
						row.addText(notificationEventConfig.getDescription(), editURL);
						row.addText(
								String.valueOf(notificationEventConfig.isActive() == true ? LanguageUtil
										.get(pageContext, "active") : LanguageUtil
										.get(pageContext, "inactive")), editURL);
						row.addJSP(
								"center",
								SearchEntry.DEFAULT_VALIGN,
								templatePath+"backoffice/event/notification_event_config_actions.jsp",
								config.getServletContext(), request, response);
						
			%>

		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator type="opencs_page_iterator" />
	</liferay-ui:search-container>
</div>

<%!private static Log _log = LogFactoryUtil
			.getLog("html.portlets.notificationmgt.backoffice.event.notification_event_config_list");%>