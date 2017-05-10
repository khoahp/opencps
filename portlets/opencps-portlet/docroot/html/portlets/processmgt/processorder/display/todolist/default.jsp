
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

<%@page import="org.opencps.processmgt.model.ProcessWorkflow"%>
<%@page import="org.opencps.processmgt.service.ProcessWorkflowLocalServiceUtil"%>
<%@page import="org.opencps.processmgt.permissions.ProcessOrderPermission"%>
<%@page import="java.util.Date"%>
<%@page import="org.opencps.processmgt.util.ProcessOrderUtils"%>
<%@page import="org.opencps.util.MessageKeys"%>
<%@page import="org.opencps.processmgt.search.ProcessOrderSearchTerms"%>
<%@page import="org.opencps.processmgt.search.ProcessOrderSearch"%>
<%@page import="org.opencps.processmgt.util.ProcessUtils"%>
<%@page import="org.opencps.dossiermgt.bean.ProcessOrderBean"%>
<%@page import="com.liferay.portal.kernel.dao.search.RowChecker"%>
<%@page import="org.opencps.processmgt.service.ProcessOrderLocalServiceUtil"%>
<%@page import="org.opencps.processmgt.search.ProcessOrderDisplayTerms"%>
<%@page import="org.opencps.holidayconfig.util.HolidayCheckUtils"%>
<%@page import="org.opencps.util.DateTimeUtil"%>
<%@page import="org.opencps.dossiermgt.service.DossierLocalServiceUtil"%>
<%@page import="org.opencps.dossiermgt.model.Dossier"%>
<%@page import="org.opencps.processmgt.permissions.ProcessOrderPermission"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.HashSet"%>
<%@page import="java.util.Set"%>

<%@ include file="../../init.jsp"%>

<liferay-ui:success  key="<%=MessageKeys.DEFAULT_SUCCESS_KEY %>" message="<%=MessageKeys.DEFAULT_SUCCESS_KEY %>"/>

<liferay-util:include page='<%=templatePath + "toolbar.jsp" %>' servletContext="<%=application %>" />

<%
	boolean success = false;
	boolean stopRefresh = false;
	
	success = ParamUtil.getBoolean(request, "success");
	stopRefresh = ParamUtil.getBoolean(request, "stopRefresh");
	
	//TODO
	//loading portlet 1 time
	if(stopRefresh){
		success = false;
	}
	
	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter("mvcPath", templatePath + "processordertodolist.jsp");
	iteratorURL.setParameter("tabs1", ProcessUtils.TOP_TABS_PROCESS_ORDER_WAITING_PROCESS);
	
	List<ProcessOrderBean> processOrders =  new ArrayList<ProcessOrderBean>();
	
	int totalCount = 0;
	
	RowChecker rowChecker = null;
	
	List<String> headerNames = new ArrayList<String>();
	
	headerNames.add("general");
	headerNames.add("detail");
	headerNames.add("action");
	
	String headers = StringUtil.merge(headerNames, StringPool.COMMA);
	
	String dossierSubStatus = ParamUtil.getString(request, "dossierSubStatus");
	
	String processOrderStage = ParamUtil.getString(request, "processOrderStage", "false");
	
	String tabs1 = ParamUtil.getString(request, "tabs1", ProcessUtils.TOP_TABS_PROCESS_ORDER_WAITING_PROCESS);
	
	long serviceInfoId = ParamUtil.getLong(request, "serviceInfoId");
	
	long processStepId = ParamUtil.getLong(request, "processStepId");
	
	iteratorURL.setParameter("dossierSubStatus", dossierSubStatus);
	iteratorURL.setParameter("processOrderStage", processOrderStage);
	boolean isShowRowChecker = false;
	if(ProcessOrderPermission.contains(permissionChecker, scopeGroupId, ActionKeys.ASSIGN_PROCESS_ORDER) && 
			tabs1.equals(ProcessUtils.TOP_TABS_PROCESS_ORDER_WAITING_PROCESS) &&
			serviceInfoId > 0 && processStepId > 0){
		
		rowChecker = new RowChecker(liferayPortletResponse);
		isShowRowChecker = true;
		
	}
%>

<c:if test="<%=stopRefresh %>">
	<div class="alert alert-success">
		<liferay-ui:message key="<%=MessageKeys.DEFAULT_SUCCESS_KEY %>" />
	</div>
</c:if>

<aui:form name="fm">
	<div class="opencps-searchcontainer-wrapper">
		
		<div class="opcs-serviceinfo-list-label">
			<div class="title_box">
			    <p class="file_manage_title ds"><liferay-ui:message key="title-danh-sach-process-order" /></p>
				<p class="count"></p>
			</div>
		</div>
		<liferay-ui:search-container 
				searchContainer="<%= new ProcessOrderSearch(renderRequest, SearchContainer.DEFAULT_DELTA, iteratorURL) %>"
				
				headerNames="<%= headers%>"
			>
			
				<liferay-ui:search-container-results>
					<%
						ProcessOrderSearchTerms searchTerms = (ProcessOrderSearchTerms)searchContainer.getSearchTerms();
					
						long assignToUserId = themeDisplay.getUserId();
						try{
							
							%>
								<%@include file="/html/portlets/processmgt/processorder/process_order_search_results.jspf" %>
							<%
						}catch(Exception e){
							_log.error(e);
						}

						total = totalCount;
						results = processOrders;
						
						pageContext.setAttribute("results", results);
						pageContext.setAttribute("total", total);
						
						try {
							
							long processWorkFlowId = ProcessOrderLocalServiceUtil
									.getProcessOrder(processOrders.get(0).getProcessOrderId()).getProcessWorkflowId();
							
							if(processWorkFlowId > 0) {
								ProcessWorkflow processWorkflow = ProcessWorkflowLocalServiceUtil.getProcessWorkflow(processWorkFlowId);
								
								if(Validator.isNotNull(processWorkflow) && processWorkflow.getIsMultipled()) {
									isMultiAssign = true;
								}
							}
							
						} catch(Exception e) {}
						
						if(isMultiAssign) {
							searchContainer.setRowChecker(rowChecker);
						}
					%>
				</liferay-ui:search-container-results>	
				
				<liferay-ui:search-container-row 
					className="org.opencps.dossiermgt.bean.ProcessOrderBean" 
					modelVar="processOrder" 
					keyProperty="processOrderId"
					rowVar="row"
					stringKey="<%=true%>"
					
				>
					<%
						PortletURL processURL = renderResponse.createRenderURL();
						processURL.setParameter("mvcPath", templatePath + "process_order_detail.jsp");
						processURL.setParameter(ProcessOrderDisplayTerms.PROCESS_ORDER_ID, String.valueOf(processOrder.getProcessOrderId()));
						processURL.setParameter("backURL", currentURL);
						processURL.setParameter("isEditDossier", (processOrder.isReadOnly() || (processOrder.getAssignToUsesrId() != 0 &&  processOrder.getAssignToUsesrId() != user.getUserId())) ? String.valueOf(false) : String.valueOf(true));
						
						String dateOver = HolidayCheckUtils.calculatorDateUntilDealineReturnFormart(Validator.isNotNull(processOrder.getActionDatetime()) ? 
								processOrder.getActionDatetime() : null,
								new Date(), processOrder.getDaysDuration(),themeDisplay.getLocale());
						
						//String deadLine = Validator.isNotNull(processOrder.getDealine()) ? processOrder.getDealine() : StringPool.DASH;
						String redirectURL = processURL.toString() + "#" +renderResponse.getNamespace() +"tab="+ renderResponse.getNamespace() + redirectToPageProcessCfg ;
								
						String href = "location.href='" + redirectURL+"'";
						
						String cssStatusColor = "status-color-" + processOrder.getDossierStatus();
					%>
					
					<liferay-util:buffer var="generalInfo">
						<div class="row-fluid">	
							<div class="row-fluid">
								<div class='<%= "text-align-right span1 " + cssStatusColor%>'>
									<i class='<%="fa fa-circle sx10 " + processOrder.getDossierStatus()%>'></i>
								</div>
								<div class="span4 bold">
									<liferay-ui:message key="reception-no"/>
								</div>
								<div class="span7">
									<%=processOrder.getReceptionNo() %>
								</div>
							</div>
							
							<%
								Dossier dossier = DossierLocalServiceUtil.getDossierByReceptionNo(processOrder.getReceptionNo());
							%>
							
							<div class="row-fluid">
								<div class="span1"></div>
								<div class="span4 bold-label">
									<liferay-ui:message key="submit-date-time"/>
								</div>
								<div class="span7">
									<%=
										Validator.isNotNull(dossier.getSubmitDatetime()) ? 
										DateTimeUtil.convertDateToString(dossier.getSubmitDatetime(), DateTimeUtil._VN_DATE_TIME_FORMAT): 
										DateTimeUtil._EMPTY_DATE_TIME  
									%>
								</div>
							</div>
							
							<div class="row-fluid">
								<div class='<%= "text-align-right span1 " + cssStatusColor%>'>
								</div>

								<div class="span11">
									<%=processOrder.getServiceName() %>
								</div>
							</div>
						</div>
					</liferay-util:buffer>
					
					
					<liferay-util:buffer var="detail">
						<div class="row-fluid min-width340">
							<div class="span5 bold">
								<liferay-ui:message key="subject-name"/>	
							</div>
							
							<div class="span7">
								<%=processOrder.getSubjectName() %>
							</div>
						</div>
						
						<div class="row-fluid" >
							<div class="span5 bold">
								 <liferay-ui:message key="assign-to-user"/>
							</div>
							
							<div class="span7">
								<%=Validator.isNotNull(processOrder.getAssignToUserName(processOrder.getDossierStatus())) ?
										processOrder.getAssignToUserName(processOrder.getDossierStatus()) : StringPool.DASH %>
							</div>
						</div>
						
						<div class="row-fluid min-width340">
							<div class="span5 bold">
								<liferay-ui:message key="step-name"/>
							</div>
							
							<div class='<%="span7 " + cssStatusColor %>'>
								<%=processOrder.getStepName() %>
							</div>
						</div>
						
						<div class="row-fluid min-width340">
							<div class="span5 bold">
								<liferay-ui:message key="dealine"/>
							</div>
							
							<div class='<%="span7"%>'>
								<%=dateOver.trim().length() >0 ? "<div class='ocps-free-day'>"+ StringUtil.replace(LanguageUtil.get(themeDisplay.getLocale(), "until-x-day1"), "{0}", dateOver)+"</div>":"<div class='ocps-over-day'>"+StringUtil.replace(LanguageUtil.get(themeDisplay.getLocale(), "over-x-day1"), "{0}", dateOver) +"</div>"%>
							</div>
						</div>
					</liferay-util:buffer>
					<%
						
						String actionBtn = LanguageUtil.get(portletConfig, themeDisplay.getLocale(), "action");
						if((processOrder.isReadOnly() || (processOrder.getAssignToUsesrId() != 0 &&  processOrder.getAssignToUsesrId() != user.getUserId()))){
							actionBtn = LanguageUtil.get(portletConfig, themeDisplay.getLocale(), "view");
						}
						row.setClassName("opencps-searchcontainer-row");
						row.addText(generalInfo);
						row.addText(detail);
						row.addButton(actionBtn, href);
						row.setClassName((processOrder.isReadOnly() || (processOrder.getAssignToUsesrId() != 0 &&  processOrder.getAssignToUsesrId() != user.getUserId())) ? "readonly" : StringPool.BLANK);
						
						//row.setClassHoverName("");
					%>	
				</liferay-ui:search-container-row> 
				
			<liferay-ui:search-iterator type="opencs_page_iterator"/>
		</liferay-ui:search-container>
	</div>
</aui:form>

<aui:script use="aui-base">

AUI().ready(function(A){

	var success = '<%=success%>';
	
	if(success == 'true'){

		var data = {

		 <portlet:namespace />stopRefresh: true,
		 
		};

		Liferay.Portlet.refresh('#p_p_id<portlet:namespace />', data);
	
	}
	
	var processDossier = A.one("#<portlet:namespace />processDossier");
	var isMultiAssignvar = '<%= isMultiAssign %>';
	var isShowRowChecker = '<%= isShowRowChecker%>';
	console.log(isMultiAssignvar);
	console.log(processDossier);
	if(isMultiAssignvar == 'false' && processDossier && isShowRowChecker == 'false') {
		processDossier.hide();
	}
	
});

</aui:script>

<%!
	private Log _log = LogFactoryUtil.getLog("html.portlets.processmgt.processorder.disolay.default.jsp");
%>