


<%@page import="org.opencps.util.LayoutView"%>
<%@page import="org.opencps.util.LayoutLister"%>
<%@page import="org.opencps.util.PortletPropsValues"%>
<%@page import="org.opencps.util.PortletUtil"%>
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

<%@page import="com.liferay.portal.model.Layout"%>
<%@page import="com.liferay.portal.service.LayoutLocalServiceUtil"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="org.opencps.util.PortletConstants"%>

<%@ include file="init.jsp" %>

<liferay-ui:success key="potlet-config-saved" message="portlet-configuration-have-been-successfully-saved" />

<liferay-portlet:actionURL var="configurationNotificationURL" portletConfiguration="true"/>

<%
	LayoutLister layoutLister = new LayoutLister();

	String rootNodeName = StringPool.BLANK;
	
	LayoutView layoutView  = layoutLister.getLayoutView(layout.getGroupId(), layout.isPrivateLayout(), rootNodeName, locale);
	
	List layoutList = layoutView.getList();
%>

<aui:form action="<%= configurationNotificationURL %>" method="post" name="configurationForm">
	<aui:select label="root-layout" name="<%=PortletKeys.PREFER__ROOTGROUPID__ %>">
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

				<aui:option label="<%= name %>" selected="<%= curRootLayout.getPlid() ==rootGroupId %>" value="<%= curRootLayout.getPlid() %>" />

			<%
				}
			}
			%>

	</aui:select>
		
	<aui:button-row>
		<aui:button type="submit" />
	</aui:button-row>
</aui:form>
