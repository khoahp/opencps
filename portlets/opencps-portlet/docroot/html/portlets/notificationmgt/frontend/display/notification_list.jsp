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
	iteratorURL.setParameter("mvcPath", templatePath + "display/notification_list.jsp");
	
	List<UserNotificationEvent> userNotificationEvents = new ArrayList<UserNotificationEvent>();
	
	SearchContainer searchContainer = new SearchContainer(renderRequest, null, null, SearchContainer.DEFAULT_CUR_PARAM, 5, iteratorURL, null, "");
	
	int totalSize = 0;
	userNotificationEvents = UserNotificationEventLocalServiceUtil
			.getUserNotificationEvents(themeDisplay.getUserId(), false,
					searchContainer.getStart(),
					searchContainer.getEnd());

	totalSize = UserNotificationEventLocalServiceUtil
			.getDeliveredUserNotificationEventsCount(
					themeDisplay.getUserId(), false);
	
	searchContainer.setResults(userNotificationEvents);
	searchContainer.setTotal(totalSize);
%>

<div
	class="opencps-searchcontainer-wrapper-width-header default-box-shadow radius8">
	
	<portlet:actionURL var="submitNotiFormURL" name="markMessageReaded">
		
	</portlet:actionURL>
	
	<aui:form action="<%=submitNotiFormURL.toString() %>" name="fm_notification" method="post">
		<c:choose>
			<c:when test="<%=userNotificationEvents.size() > 0 %>">
				<aui:button type="button" class="btn-default" onClick='<%=renderResponse.getNamespace()+"submitMarkAsReaded()"%>' value="mark-as-readed"/>	
				<table width="100%">
					<tbody>
						<tr>
							<td width="10%"><input name="<portlet:namespace/>checkAll" label="" type="checkbox" 
								onClick="<portlet:namespace />markAsReaded();" />
							</td>
							<td width="30%"><liferay-ui:message key="dossier-reception-no"/></td>
							<td width="30%"><liferay-ui:message key="action-name"/></td>
							<td width="30%"><liferay-ui:message key="note"/></td>
						</tr>
						<%
							PortletURL markAsReadedUrl = renderResponse.createActionURL();
						
							for(UserNotificationEvent userNotificationEvent:userNotificationEvents){
								
								UserNotificationEventBean userNotificationBean = UserNotificationEventBean.getBean(userNotificationEvent, null, renderRequest);
								
								markAsReadedUrl.setParameter("userNotificationEventId", String.valueOf(userNotificationBean.getUserNotificationEventId()));
						%>
							<tr>
								<td width="10%"><input name="<portlet:namespace/>checkboxs" label="" 
									type="checkbox" value="<%=userNotificationBean.getUserNotificationEventId()%>" />
								</td>
								<td width="30%">
									<a href="<%=userNotificationBean.getUrl()%>" 
										>
										<%= userNotificationBean.getReceptionNo() %>
									</a>
								</td>
								<td width="30%">
									<a href="<%=userNotificationBean.getUrl()%>" 
										>
										<%= userNotificationBean.getActionName() %>
									</a>
								</td>
								<td width="30%">
									<a href="<%=userNotificationBean.getUrl()%>" 
									>
									<%= userNotificationBean.getNote() %>
									</a>
								</td>
							</tr>
						<%} %>
					</tbody>
					<tfoot>
						<liferay-ui:search-paginator searchContainer="<%= searchContainer %>" type="opencs_page_iterator"/>
					</tfoot>
				</table>
			</c:when>
			<c:otherwise>
				<liferay-ui:message key="no-user-notification-event-where-found" />
			</c:otherwise>
		</c:choose>
	</aui:form>
</div>

<script type="text/javascript">
	
	function <portlet:namespace />submitMarkAsReaded(){
		
		$("form[name='<portlet:namespace/>fm_notification']").submit();

	}
	
	function <portlet:namespace />markAsReaded(){
		
		$("input[name='<portlet:namespace/>checkboxs']").prop("checked", $("input[name='<portlet:namespace/>checkAll']").prop("checked"));
		
	}
</script>

<%!private static Log _log = LogFactoryUtil.getLog("html.portlets.notificationmgt.frontend.display.notification_list");%>