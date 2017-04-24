<%@page import="org.opencps.notificationmgt.bean.UserNotificationEventBean"%>
<%@page import="org.opencps.notificationmgt.search.UserNotificationEventSearch"%>
<%@page import="com.liferay.portal.service.UserNotificationEventLocalServiceUtil"%>
<%@page import="com.liferay.portal.model.UserNotificationEvent"%>
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

<%@ include file="../init.jsp"%>

<%

	
	PortletURL iteratorURL = renderResponse.createRenderURL();

%>

<div class="opencps-searchcontainer-wrapper-width-header default-box-shadow radius8">

	<liferay-ui:error key="error" 
		message="error" />

	<liferay-ui:search-container
		searchContainer="<%=new UserNotificationEventSearch(renderRequest,
						SearchContainer.DEFAULT_DELTA, iteratorURL)%>">
						
		<liferay-ui:search-container-results>
			<%
			List<UserNotificationEvent> userNotificationEvents = null;
					int totalSize = 0;

			userNotificationEvents = UserNotificationEventLocalServiceUtil
					.getUserNotificationEvents(
							themeDisplay.getUserId(), true,
							searchContainer.getStart(),
							searchContainer.getEnd());

			totalSize = UserNotificationEventLocalServiceUtil
					.getDeliveredUserNotificationEventsCount(
							themeDisplay.getUserId(), false);

			pageContext.setAttribute("results", userNotificationEvents);
			pageContext.setAttribute("total", totalSize);
			%>

		</liferay-ui:search-container-results>
		<liferay-ui:search-container-row
			className="com.liferay.portal.model.UserNotificationEvent"
			modelVar="userNofiticationEvent" keyProperty="userNotificationEventId">
			
			<%
				UserNotificationEventBean userNotificationBean = UserNotificationEventBean.getBean(userNofiticationEvent);
				
				PortletURL editURL = renderResponse.createRenderURL();
			%>
			
			<liferay-util:buffer var="rowCheck">
				<div class="row-fluid">
					<div class="span12">
						<aui:input name="check_test" type="checkbox"/>
					</div>
				</div>
			</liferay-util:buffer>
			
			<liferay-util:buffer var="receptionNo">
				<div class="row-fluid">
					<div class="span12">
						<a href="<%=editURL%>"><%= userNotificationBean.getReceptionNo() %></a>
					</div>
				</div>
			</liferay-util:buffer>
			
			<liferay-util:buffer var="actionName">
				<div class="row-fluid">
					<div class="span12">
						<a href="<%=editURL%>"><%= userNotificationBean.getActionName() %></a>
					</div>
				</div>
			</liferay-util:buffer>
			
			<liferay-util:buffer var="note">
				<div class="row-fluid">
					<div class="span12">
						<a href="<%=editURL%>"><%= userNotificationBean.getNote() %></a>
					</div>
				</div>
			</liferay-util:buffer>
			
			<liferay-util:buffer var="createDate">
				<div class="row-fluid">
					<div class="span12">
						<a href="<%=editURL%>"><%= userNotificationBean.getCreateDate() %></a>
					</div>
				</div>
			</liferay-util:buffer>
			
			<%
				row.addText(rowCheck);
				row.addText(receptionNo);
				row.addText(actionName);
				row.addText(note);
				row.addText(createDate);
			%>
			

		</liferay-ui:search-container-row>

	</liferay-ui:search-container>

</div>



<%!private static Log _log = LogFactoryUtil
			.getLog("html.portlets.notificationmgt.frontend.display.notification_list");%>