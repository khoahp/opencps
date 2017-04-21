
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
<%@page import="org.opencps.processmgt.util.ProcessMgtUtil"%>
<%@page import="org.opencps.processmgt.permissions.ProcessPermission"%>
<%@page import="com.liferay.portal.kernel.dao.search.SearchEntry"%>
<%@page import="org.opencps.processmgt.service.ServiceProcessLocalServiceUtil"%>
<%@page import="org.opencps.processmgt.search.ProcessSearchTerms"%>
<%@page import="org.opencps.processmgt.search.ProcessSearch"%>

<%@ include file="../init.jsp" %>

<liferay-ui:error 
	key="<%=ProcessMgtUtil.DELERR_EXIST_SERVICE_CONFIG %>" 
	message="<%=ProcessMgtUtil.DELERR_EXIST_SERVICE_CONFIG %>" 
/>

<liferay-ui:error 
	key="<%=ProcessMgtUtil.DELERR_EXIST_STEP %>" 
	message="<%=ProcessMgtUtil.DELERR_EXIST_STEP %>" 
/>

<liferay-ui:error 
	key="<%=ProcessMgtUtil.DELERR_EXIST_WORKFLOW %>" 
	message="<%=ProcessMgtUtil.DELERR_EXIST_WORKFLOW %>" 
/>

<liferay-ui:success 
	key="<%=ProcessMgtUtil.DEL_PROCESS_SUCC %>" 
	message="<%=ProcessMgtUtil.DEL_PROCESS_SUCC %>" 
/>
	
<%
	PortletURL searchURL = renderResponse.createRenderURL();


	boolean isPermission =
	ProcessPermission.contains(
	    themeDisplay.getPermissionChecker(),
	    themeDisplay.getScopeGroupId(), ActionKeys.ADD_PROCESS);
	
	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter("mvcPath", templatePath + "serviceprocesslist.jsp");

%>
<aui:nav-bar cssClass="opencps-toolbar custom-toolbar">
	<aui:nav id="toolbarContainer" cssClass="nav-display-style-buttons pull-left" >
		<c:if test="<%= isPermission %>">
			<portlet:renderURL var="editProcessURL">
				<portlet:param name="mvcPath" value='<%= templatePath + "edit_process.jsp" %>'/>
				<portlet:param name="redirectURL" value="<%=currentURL %>"/>
				<portlet:param name="backURL" value="<%=currentURL %>"/>
			</portlet:renderURL>
			<aui:button icon="icon-plus" href="<%=editProcessURL %>" cssClass="action-button" value="add-process"/>
		</c:if>
	</aui:nav>
	<aui:nav-bar-search cssClass="pull-right">
		<div class="form-search">
			<aui:form action="<%= iteratorURL %>" method="post" name="fm">
				<div class="toolbar_search_input">
					<liferay-ui:input-search 
						id="keywords1"
						name="keywords"
						title=''
						placeholder='<%= LanguageUtil.get(locale, "process-no")
								+StringPool.COMMA+LanguageUtil.get(locale, "process-name")
								+StringPool.COMMA+LanguageUtil.get(locale, "service-name")
								+StringPool.COMMA+LanguageUtil.get(locale, "service-no")%>'
						cssClass="search-input input-keyword"
					/>
				</div>
			</aui:form>
		</div>
	</aui:nav-bar-search>
</aui:nav-bar>

<div class="opencps-searchcontainer-wrapper default-box-shadow radius8">

	<liferay-ui:search-container searchContainer="<%= new ProcessSearch(renderRequest, SearchContainer.DEFAULT_DELTA, iteratorURL) %>">
			
		<liferay-ui:search-container-results>
			<%
				ProcessSearchTerms searchTerms = (ProcessSearchTerms) searchContainer.getSearchTerms();
	
				total = ServiceProcessLocalServiceUtil.countServiceProcess(scopeGroupId, searchTerms.getKeywords());
				
				results = ServiceProcessLocalServiceUtil.searchServiceProcess(scopeGroupId, searchTerms.getKeywords(),
						searchContainer.getStart(), searchContainer.getEnd());
				
				pageContext.setAttribute("results", results);
				pageContext.setAttribute("total", total);
			%>
			
		</liferay-ui:search-container-results>
	
		<liferay-ui:search-container-row 
			className="org.opencps.processmgt.model.ServiceProcess" 
			modelVar="process" 
			keyProperty="serviceProcessId"
		>
			<%
				PortletURL editURL = renderResponse.createRenderURL();
				editURL.setParameter("mvcPath", templatePath + "edit_process.jsp");
				editURL.setParameter("serviceProcessId", String.valueOf(process.getServiceProcessId()));
				editURL.setParameter("backURL", currentURL);
				
			%>
		
			<liferay-util:buffer var="rowIndex">
				<div class="row-fluid min-width10">
					<div class="span12 bold">
						<a href="<%=editURL%>"><%=row.getPos() + 1 %></a>
					</div>
				</div>
			</liferay-util:buffer>
			
			<liferay-util:buffer var="processInfo">
				<div class="row-fluid">
					<div class="span3 bold">
						<liferay-ui:message key="process-no"/>
					</div>
					<div class="span9">
						<a href="<%=editURL%>"><%= process.getProcessNo() %></a>
					</div>
				</div>
				
				<div class="row-fluid">
					<div class="span3 bold">
						<liferay-ui:message key="process-name"/>
					</div>
					<div class="span9">
						<a href="<%=editURL%>"><%= process.getProcessName()  %></a>
					</div>
				</div>
				<div class="row-fluid">
					<div class="span3 bold">
						<liferay-ui:message key="description"/>
					</div>
					<div class="span9">
						<a href="<%=editURL%>"><%= process.getDescription() %></a>
					</div>
				</div>
			</liferay-util:buffer>

			<%
				
				row.addText(rowIndex);
				
				row.addText(processInfo);
				
				if(isPermission) {
					//action column
					row.addJSP("center", SearchEntry.DEFAULT_VALIGN, templatePath + "process_actions.jsp", config.getServletContext(), request, response);
				}
			%>	
		
		</liferay-ui:search-container-row>	
	
		<liferay-ui:search-iterator type="opencs_page_iterator"/>
	
	</liferay-ui:search-container>

</div>
