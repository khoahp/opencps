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

<%@page import="org.opencps.datamgt.search.DictItemDisplayTerms"%>
<%@page import="org.opencps.datamgt.service.DictCollectionLocalServiceUtil"%>
<%@page import="org.opencps.datamgt.model.DictCollection"%>
<%@page import="java.util.List"%>

<%@ include file="../../init.jsp"%>

<div class="opencps-searchcontainer-wrapper default-box-shadow radius8">
	<aui:row>
		<liferay-ui:message key="edit-sibling"/>
	</aui:row>
	<div class="edit-form">
		<portlet:actionURL name="editDictItemSibling" var="editDictItemSiblingURL"/>
		<aui:form action="<%=editDictItemSiblingURL.toString() %>" method="POST" name="fm_editSibling">
			<aui:row>
				<aui:select name="numberedSiblingMode">
					<aui:option value="1" label="numbered-for-all-dictItems"/>
					<aui:option value="2" label="numbered-for-all-dictItems-in-dictcollection" selected="true"/>
				</aui:select>
			</aui:row>
			<aui:row>
				<aui:select name="<%=DictItemDisplayTerms.DICTCOLLECTION_ID %>">
					<aui:option value="0" label="select-dictcollection"/>
					<%
						List<DictCollection> collections = DictCollectionLocalServiceUtil.getDictCollections();
						for (DictCollection collection : collections){
							%>
								<aui:option value="<%=collection.getDictCollectionId() %>" ><%=collection.getCollectionName(locale) %></aui:option>
							<%
						}
					%>
				</aui:select>
			</aui:row>
			
			<aui:button type="submit"/>
		</aui:form>
	</div>
</div>