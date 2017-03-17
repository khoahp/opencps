
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

<%@page import="org.opencps.processmgt.util.ProcessUtils"%>
<%@page import="com.liferay.portal.kernel.process.ProcessUtil"%>
<%@page import="org.opencps.processmgt.service.ProcessWorkflowLocalServiceUtil"%>
<%@page import="org.opencps.processmgt.search.WorkflowSearchTerms"%>
<%@page import="org.opencps.processmgt.search.WorkflowSearch"%>
<%@page import="org.opencps.processmgt.model.ProcessWorkflow"%>

<%@ include file="../../init.jsp" %>

<%

	ServiceProcess serviceProcess  = (ServiceProcess) request.getAttribute(WebKeys.SERVICE_PROCESS_ENTRY);

	long serviceProcessId = 0l;
	
	if (Validator.isNotNull(serviceProcess)) {
		serviceProcessId = serviceProcess.getServiceProcessId();
	}

	ProcessWorkflow workflow  = (ProcessWorkflow) request.getAttribute(WebKeys.PROCESS_WORKFLOW_ENTRY);
	
	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter("mvcPath", templatePath + "process/action.jsp");

	boolean isPermission =
				    ProcessPermission.contains(
				        themeDisplay.getPermissionChecker(),
				        themeDisplay.getScopeGroupId(), ActionKeys.ADD_PROCESS);
	
	int totalCount = ProcessWorkflowLocalServiceUtil.countWorkflow(serviceProcessId);
%>

<liferay-portlet:renderURL var="editActionURL" windowState="<%= LiferayWindowState.NORMAL.toString() %>">
	<portlet:param name="mvcPath" value='<%= templatePath + "edit_action.jsp" %>'/>
	<portlet:param name="serviceProcessId" value="<%= Validator.isNotNull(serviceProcess) ? Long.toString(serviceProcess.getServiceProcessId()) : StringPool.BLANK %>"/>
	<portlet:param name="processWorkflowId" value="<%= Validator.isNotNull(workflow) ? Long.toString(workflow.getProcessWorkflowId()) : StringPool.BLANK %>"/>
	<portlet:param name="redirectURL" value="<%=currentURL %>"/>
	<portlet:param name="backURL" value='<%=currentURL + "#_15_WAR_opencpsportlet_tab=_15_WAR_opencpsportlet_action" %>'/>
</liferay-portlet:renderURL>

<aui:button-row>
	<aui:button name="addAction" href="<%= editActionURL %>" value="add-action" ></aui:button>
</aui:button-row>

<div class="opencps-searchcontainer-wrapper default-box-shadow radius8">
	<liferay-ui:search-container searchContainer="<%= new WorkflowSearch(renderRequest, totalCount, iteratorURL) %>">
			
		<liferay-ui:search-container-results>
			<%
				WorkflowSearchTerms searchTerms = (WorkflowSearchTerms) searchContainer.getSearchTerms();
			
				total = totalCount;
	
				results = ProcessWorkflowLocalServiceUtil.searchWorkflow(serviceProcessId, searchContainer.getStart(), searchContainer.getEnd());
				
				pageContext.setAttribute("results", results);
				pageContext.setAttribute("total", total);
			%>
			
		</liferay-ui:search-container-results>
	
		<liferay-ui:search-container-row 
			className="org.opencps.processmgt.model.ProcessWorkflow" 
			modelVar="processWorkflow" 
			keyProperty="processWorkflowId"
		>
			<%
			
				String preName = LanguageUtil.get(portletConfig, themeDisplay.getLocale(), "start-step");
				
				String postName = LanguageUtil.get(portletConfig, themeDisplay.getLocale(), "end-step");
				
				if(!ProcessUtils.getPreProcessStepName(processWorkflow.getPreProcessStepId()).equals(StringPool.BLANK)) {
					preName = ProcessUtils.getPreProcessStepName(processWorkflow.getPreProcessStepId());
				}
				
				if(!ProcessUtils.getPostProcessStepName(processWorkflow.getPostProcessStepId()).equals(StringPool.BLANK)) {
					postName = ProcessUtils.getPostProcessStepName(processWorkflow.getPostProcessStepId());
				}
			%>	
			
			<liferay-util:buffer var="rowIndex">
				<div class="row-fluid min-width10">
					<div class="span12">
						<%=row.getPos() + 1 %>
					</div>
				</div>
			</liferay-util:buffer>
			
			<liferay-util:buffer var="actionInfo">
				<div class="row-fluid">
					<div class="span6">
						<div class="row-fluid">
							<div class="span4 bold">
								<liferay-ui:message key="action-name"/>
							</div>
							<div class="span8">
								<%= processWorkflow.getActionName() %>
							</div>
						</div>
					</div>
					
					<div class="span6">
						<div class="row-fluid">
							<div class="span4 bold">
								<liferay-ui:message key="start-step"/>
							</div>
							<div class="span8">
								<%= preName %>
							</div>
						</div>
						<div class="row-fluid">
							<div class="span4 bold">
								<liferay-ui:message key="end-step"/>
							</div>
							<div class="span8">
								<%= postName %>
							</div>
						</div>
					</div>
				</div>
			</liferay-util:buffer>
			
			<%
				row.addText(rowIndex);
				
				row.addText(actionInfo);
				
				if(isPermission) {
					//action column
					row.addJSP("center", SearchEntry.DEFAULT_VALIGN, templatePath + "workflow_actions.jsp", config.getServletContext(), request, response);
				}
			%>	
			
			
			
		</liferay-ui:search-container-row>	
	
		<liferay-ui:search-iterator paginate="false"/>
	
	</liferay-ui:search-container>
</div>


<%!
	private int ITEM_PERPAGE = 100;
%>

<aui:script use="liferay-util-window">
	Liferay.provide(window, 'addAction', function(action) {
		page = '<%= editActionURL %>'
		Liferay.Util.openWindow({
			dialog: {
				cache: false,
				centered: true,
				modal: true,
				resizable: false,
				width: 1000
			},
			id: 'addaction',
			title: 'adding-process-workflow',
			uri: page
		});
	});
</aui:script>

<aui:script>
	Liferay.provide(window, 'refreshPortlet', function() 
		{	
			window.location.reload();
		},
		['aui-dialog','aui-dialog-iframe']
	);
</aui:script>

<aui:script>
	Liferay.provide(window, 'closePopup', function(dialogId) 
		{
			var A = AUI();
			var dialog = Liferay.Util.Window.getById(dialogId);
			dialog.destroy();
		},
		['liferay-util-window']
	);
</aui:script>