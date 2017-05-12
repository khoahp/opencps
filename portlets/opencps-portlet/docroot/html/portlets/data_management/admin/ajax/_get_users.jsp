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

<%@page import="java.util.ArrayList"%>
<%@page import="org.opencps.usermgt.service.EmployeeLocalServiceUtil"%>
<%@page import="java.util.List"%>
<%@page import="org.opencps.usermgt.model.Employee"%>
<%@page import="com.liferay.portal.kernel.log.LogFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.log.Log"%>

<%@ include file="../../init.jsp"%>

<%
	List<Employee> employees = new ArrayList<Employee>();
	try {
		employees = EmployeeLocalServiceUtil.getEmployees(scopeGroupId);
	} catch (Exception e){
		_log.error(e);
	}
%>

<ul class="tree-view-content tree-drag-drop-content tree-file tree-root-container">
	<%
		for (Employee emp : employees){
			%>
				<li class="tree-node user-tree-node" 
					id='<%=renderResponse.getNamespace() +  "userId_" + emp.getMappingUserId() %>'
				>
					<%=emp.getFullName() %>
				</li>
			<%
		}
	%>
</ul>

<%!
	private Log _log = LogFactoryUtil.getLog("html.portlets.data_management.admin.ajax._get_users.jsp");
%>
