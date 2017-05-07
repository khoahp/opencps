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
<%@page import="org.opencps.util.WebKeys"%>
<%@page import="org.opencps.datamgt.model.DictItem"%>
<%@page import="com.liferay.portal.kernel.dao.search.ResultRow"%>
<%@page import="org.opencps.datamgt.permissions.DictItemPermission"%>
<%@page import="org.opencps.util.ActionKeys"%>

<%@ include file="../../init.jsp"%>
 
<%
	ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
	DictItem dictItem = (DictItem) row.getObject();
%> 

<aui:row>
	<aui:col>
		<c:if test="<%=DictItemPermission.contains(permissionChecker, scopeGroupId, ActionKeys.ADD_DICTITEM) %>">
			<liferay-ui:icon 
				image="edit" 
				message="edit" 
				id='<%=renderResponse.getNamespace() + "dictItemId_" + dictItem.getDictItemId() %>'
				cssClass='<%=renderResponse.getNamespace() + "edit_dictItem_button" %>'
			/> 
		<!-- 		cssClass="search-container-action fa edit"  -->
		</c:if>
	</aui:col>
	<aui:col>
		<c:if test="<%=DictItemPermission.contains(permissionChecker, scopeGroupId, ActionKeys.DELETE) %>">
			<liferay-ui:icon-delete 
				image="delete" 
				message="delete"  
				url=""
				id='<%=renderResponse.getNamespace() + "dictItemId_" + dictItem.getDictItemId() %>'
				cssClass='<%=renderResponse.getNamespace() + "delete_dictItem_button" %>'
			/>
		<!-- 		cssClass="search-container-action fa delete"  -->
		</c:if>
	</aui:col>	
</aui:row>
	  
