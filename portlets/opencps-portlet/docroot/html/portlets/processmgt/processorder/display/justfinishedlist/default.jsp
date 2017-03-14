
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

<%@page import="org.opencps.util.DateTimeUtil"%>
<%@page import="org.opencps.processmgt.util.ProcessOrderUtils"%>
<%@page import="org.opencps.util.MessageKeys"%>
<%@page import="org.opencps.processmgt.search.ProcessOrderSearchTerms"%>
<%@page import="org.opencps.processmgt.search.ProcessOrderSearch"%>
<%@page import="org.opencps.processmgt.util.ProcessUtils"%>
<%@page import="org.opencps.dossiermgt.bean.ProcessOrderBean"%>
<%@page import="org.opencps.processmgt.service.ProcessOrderLocalServiceUtil"%>
<%@page import="org.opencps.processmgt.search.ProcessOrderDisplayTerms"%>

<%@ include file="../../init.jsp"%>

<liferay-ui:success  key="<%=MessageKeys.DEFAULT_SUCCESS_KEY %>" message="<%=MessageKeys.DEFAULT_SUCCESS_KEY %>"/>

<liferay-util:include page='<%=templatePath + "toolbar.jsp" %>' servletContext="<%=application %>" />

<%
	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter("mvcPath", templatePath + "processorderjustfinishedlist.jsp");
	iteratorURL.setParameter("tabs1", ProcessUtils.TOP_TABS_PROCESS_ORDER_FINISHED_PROCESSING);
	
	List<ProcessOrderBean> processOrders =  new ArrayList<ProcessOrderBean>();
	
	int totalCount = 0;
	
// 	RowChecker rowChecker = new RowChecker(liferayPortletResponse);
	
	List<String> headerNames = new ArrayList<String>();
	
	headerNames.add("col1");
	headerNames.add("col2");
	headerNames.add("col3");
	
	String headers = StringUtil.merge(headerNames, StringPool.COMMA);
%>

<aui:form name="fm">
	<div class="opencps-searchcontainer-wrapper">
		<liferay-ui:search-container 
			searchContainer="<%= new ProcessOrderSearch(renderRequest, SearchContainer.DEFAULT_DELTA, iteratorURL) %>"
			headerNames="<%= headers%>"
		>
		
			<liferay-ui:search-container-results>
				<%
					ProcessOrderSearchTerms searchTerms = (ProcessOrderSearchTerms)searchContainer.getSearchTerms();
				
					long serviceInfoId = searchTerms.getServiceInfoId();
					
					long processStepId = searchTerms.getProcessStepId();
					
					long assignToUserId = themeDisplay.getUserId();
					try{
						
						%>
							<%@include file="/html/portlets/processmgt/processorder/processorder_justfinished_searchresults.jspf" %>
						<%
					}catch(Exception e){
						_log.error(e);
					}
				
					total = totalCount;
					results = processOrders;
					
					pageContext.setAttribute("results", results);
					pageContext.setAttribute("total", total);
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
						//processURL.setParameter("isEditDossier", (processOrder.isReadOnly() || (processOrder.getAssignToUsesrId() != 0 &&  processOrder.getAssignToUsesrId() != user.getUserId())) ? String.valueOf(false) : String.valueOf(true));
						processURL.setParameter("isEditDossier", String.valueOf(false));
						String deadlineVal = Validator.isNotNull(processOrder.getDealine()) ? processOrder.getDealine() : StringPool.DASH;
						
						String hrefFix = "location.href='" + processURL.toString()+"'";
						String cssStatusColor = "status-color-" + processOrder.getDossierStatus();
					%>
					
					<liferay-util:buffer var="boundcol1">
						<div class="row-fluid">	
							<div class="row-fluid">
								<div class='<%= "text-align-right span1 " + cssStatusColor%>'>
									<i class='<%="fa fa-circle sx10 " + processOrder.getDossierStatus()%>'></i>
								</div>
								<div class="span2 bold">
									<liferay-ui:message key="reception-no"/>
								</div>
								<span class="span9">
									<%=processOrder.getReceptionNo() %>
								</span>
							</div>
							
							<div class="row-fluid">
								<div class='<%= "text-align-right span1 " + cssStatusColor%>'>
								</div>
								<div class="span2 bold">
									<liferay-ui:message key="service-name"/>
								</div>
								<span class="span9">
									<%=processOrder.getServiceName() %>
								</span>
							</div>
						</div>
					</liferay-util:buffer>
					
					
					<liferay-util:buffer var="boundcol2">
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
							 <liferay-ui:message key="action-date"/>
						</div>
						
						<div class="span7">
							<%=Validator.isNotNull(processOrder.getActionDatetime())? 
									DateTimeUtil.convertDateToString(processOrder.getActionDatetime(), DateTimeUtil._VN_DATE_FORMAT)
									:StringPool.BLANK %>
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
								<liferay-ui:message key="y-kien"/>
							</div>
							
							<div class='<%="span7"%>'>
								<%= processOrder.getActionNote() %>
							</div>
						</div>
					</liferay-util:buffer>
					<%
						
						
						String actionButt = LanguageUtil.get(portletConfig, themeDisplay.getLocale(), "action");
						row.setClassName("opencps-searchcontainer-row");
						row.addText(boundcol1);
						row.addText(boundcol2);
						row.addButton(actionButt, hrefFix);
						row.setClassName((processOrder.isReadOnly() || (processOrder.getAssignToUsesrId() != 0 &&  processOrder.getAssignToUsesrId() != user.getUserId())) ? "readonly" : StringPool.BLANK);
						
						//row.setClassHoverName("");
					%>	
				</liferay-ui:search-container-row> 
			
			<liferay-ui:search-iterator type="opencs_page_iterator"/>
		</liferay-ui:search-container>
	</div>
</aui:form>
<%!
	private Log _log = LogFactoryUtil.getLog("html.portlets.processmgt.processorder.processordertodolist.jsp");
%>
