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

<%@ include file="../../init.jsp"%>
 
<%
	ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
	DictItem dictItem = (DictItem) row.getObject();
	int itemsStatus = ParamUtil.getInteger(request, "itemsStatus", 1);
%> 

<c:if test="<%=DictItemPermission.contains(permissionChecker, scopeGroupId, ActionKeys.ADD_DICTITEM) %>">
	<aui:button name='<%=renderResponse.getNamespace() + "dictItemId_" + dictItem.getDictItemId() %>'
	 	type="submit" value="edit" 
	 	cssClass='<%="edit-button " + renderResponse.getNamespace() + "edit_dictItem_button" %>'
	 	title='<%=LanguageUtil.get(locale, "edit") %>'
	/>
</c:if>
<c:if test="<%=DictItemPermission.contains(permissionChecker, scopeGroupId, ActionKeys.DELETE) %>">
	<aui:button name='<%=renderResponse.getNamespace() + "dictItemId_" + dictItem.getDictItemId() %>'
	 	type="submit" value='<%=itemsStatus != 1 ? "delete" : "no-use" %>' 
	 	cssClass='<%="delete-button " + renderResponse.getNamespace() + (itemsStatus != 1 ? "delete_dictItem_button" : "no_use_dictItem_button") %>'
	 	title='<%=itemsStatus != 1 ? LanguageUtil.get(locale, "delete") : LanguageUtil.get(locale, "no-use") %>'
	/>
</c:if>
	  
