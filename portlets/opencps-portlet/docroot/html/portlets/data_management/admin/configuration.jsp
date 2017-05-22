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

<%@ include file="../init.jsp"%>

<%
	

%>

<liferay-ui:success 
	key="config-stored" 
	message="portlet-configuration-have-been-successfully-saved"
/>

<liferay-portlet:actionURL var="configurationActionURL" portletConfiguration="true" />

<aui:form action="<%= configurationActionURL %>" method="post" name="configurationForm">
	<aui:select name="view-template">
		<aui:option 
			value="dictcollection.jsp" 
			label="defalut" 
			selected="<%=viewTemplate.equals(\"dictcollection.jsp\") %>" 
		/>
		<aui:option 
			value="display/dictcollections.jsp" 
			label="tree-menu-left" 
			selected="<%=viewTemplate.equals(\"display/dictcollections.jsp\") %>" 
		/>
	</aui:select>
	
	<aui:button type="submit" name="Save" value="save"/>
</aui:form>

