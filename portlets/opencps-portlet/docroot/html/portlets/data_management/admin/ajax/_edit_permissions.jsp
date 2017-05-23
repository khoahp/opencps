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

<p class="breadcrumb bold">
	<a title='<%=LanguageUtil.get(locale, "dict-collection-mgt") %>' href="javascript:getDictCollectionDetail();" >
		<liferay-ui:message key='dict-collection-mgt' />
	</a>
	<liferay-ui:message key='<%=" >> " %>' />
	<liferay-ui:message key='edit-dictcollection-permissions' />
</p>

<div class="row-fluid">
	<aui:button type="submit" value="back" name="back-permission-button" cssClass="back-icon" title='<%=LanguageUtil.get(locale, "back") %>'/>
	<aui:button type="submit" value="save" name="save-permission-button" cssClass="save-icon" title='<%=LanguageUtil.get(locale, "save") %>'/>
</div>

<div class="row-fluid">
	<div class="span4" >
		<div class="opencps-searchcontainer-wrapper default-box-shadow radius8 data-manager-action">
			<div class="openCPSTree yui3-widget component tree-view tree-drag-drop">
				<div>
					<b><liferay-ui:message key="admin" /></b>
					<aui:input 
						name="user-name" 
						placeholder='<%= LanguageUtil.get(locale, "admin-name") %>' 
						cssClass="input100" 
						label=""
						title='<%= LanguageUtil.get(locale, "admin-name") %>' 
					/>
					<aui:button name="search-users-button" value="search" type="submit" title='<%=LanguageUtil.get(locale, "search") %>'/>
				</div>
				<div id='<%=renderResponse.getNamespace() + "users-container" %>' class="scrollbar-datamgt"></div>
			</div>
		</div>
	</div>
	
	<div class="span8" >
		<div class="opencps-searchcontainer-wrapper default-box-shadow radius8 data-manager-action">
			<div class="openCPSTree yui3-widget component tree-view tree-drag-drop">
				<div>
					<b><liferay-ui:message key="dictcollection-list" /></b>
					<aui:input 
						name="collection-name-permission" 
						placeholder='<%= LanguageUtil.get(locale, "collection-name") %>' 
						cssClass="input100" 
						label=""
						title='<%= LanguageUtil.get(locale, "collection-name") %>' 
					/>
					<aui:button name="search-collection-permission-button" value="search" type="submit" title='<%=LanguageUtil.get(locale, "search") %>'/>
				</div>
				<div id='<%=renderResponse.getNamespace() + "collection-permissions" %>' class="scrollbar-datamgt"></div>
			</div>
		</div>
	</div>
</div>

<%!
	private Log _log = LogFactoryUtil.getLog("html.portlets.data_management.admin.ajax._edit_permissions.jsp");
%>