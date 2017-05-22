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

<%@page import="com.liferay.portal.service.RoleLocalServiceUtil"%>

<%@ include file="../../init.jsp"%>

<%
	List<User> users = new ArrayList<User>();
	long roleId = 0;
	try {
		roleId = RoleLocalServiceUtil
				.getRole(company.getCompanyId(), DictCollectionDisplayTerms.DICTCOLLECTION_ROLE)
					.getRoleId();
		users = UserLocalServiceUtil.getRoleUsers(roleId);
	} catch (Exception e){
		_log.error(e);
	}
	
%>

<ul class="tree-view-content tree-drag-drop-content tree-file tree-root-container">
	<%
		for (User u : users){
			%>
				<li class='<%="tree-node user-tree-node" + (users.indexOf(u) == 0 ? " selected" : "") %>' 
					id='<%=renderResponse.getNamespace() +  "userId_" + u.getUserId() %>'
				>
					<%=u.getFullName() %>
				</li>
			<%
		}
	%>
</ul>

<%!
	private Log _log = LogFactoryUtil.getLog("html.portlets.data_management.admin.ajax._get_users.jsp");
%>
