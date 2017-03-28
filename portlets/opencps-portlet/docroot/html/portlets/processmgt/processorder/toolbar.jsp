<%@page import="org.opencps.servicemgt.service.ServiceInfoLocalServiceUtil"%>
<%@page import="org.opencps.datamgt.service.DictItemLocalServiceUtil"%>
<%@page import="org.opencps.util.PortletPropsValues"%>
<%@page import="org.opencps.datamgt.service.DictCollectionLocalServiceUtil"%>
<%@page import="org.opencps.datamgt.model.DictCollection"%>
<%@page import="org.opencps.datamgt.model.DictItem"%>
<%@page import="org.opencps.dossiermgt.search.DossierDisplayTerms"%>
<%@page import="org.opencps.util.DateTimeUtil"%>
<%@page import="java.util.Date"%>
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

<%@page import="com.liferay.portal.kernel.log.LogFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.log.Log"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@page import="org.opencps.util.ActionKeys"%>

<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@page import="org.opencps.processmgt.permissions.ProcessOrderPermission"%>
<%@page import="org.opencps.processmgt.util.ProcessUtils"%>
<%@page import="com.liferay.portlet.PortletURLFactoryUtil"%>
<%@page import="javax.portlet.PortletRequest"%>
<%@page import="com.liferay.portal.kernel.language.UnicodeLanguageUtil"%>
<%@page import="org.opencps.processmgt.service.ProcessOrderLocalServiceUtil"%>
<%@page import="org.opencps.dossiermgt.bean.ProcessOrderBean"%>

<%@ include file="../init.jsp"%>

<%
	String tabs1 = ParamUtil.getString(request, "tabs1", ProcessUtils.TOP_TABS_PROCESS_ORDER_WAITING_PROCESS);

	List<ProcessOrderBean> processOrderServices = new ArrayList<ProcessOrderBean>();
	
	List<ProcessOrderBean> processOrderSteps = new ArrayList<ProcessOrderBean>();
	
	long serviceInfoId = ParamUtil.getLong(request, "serviceInfoId");
	
	long processStepId = ParamUtil.getLong(request, "processStepId");
	
	String dossierSubStatus = ParamUtil.getString(request, "dossierSubStatus");
	
	String todolistDisplayStyle = GetterUtil.getString(portletPreferences.getValue("todolistDisplayStyle", "default"));
	
	String domainCode = ParamUtil.getString(request, "serviceDomainCode");
	
	List<ServiceInfo> serviceInfoList = new ArrayList<ServiceInfo>();
	try {
		serviceInfoList = ServiceInfoLocalServiceUtil.getServiceInfoByDomainCode(domainCode);
	} catch (Exception e) {
		//
	}
	
	try{
		
		if(tabs1.equals(ProcessUtils.TOP_TABS_PROCESS_ORDER_WAITING_PROCESS)){
			processOrderServices = (List<ProcessOrderBean>) ProcessOrderLocalServiceUtil.getProcessOrderServiceByUser(themeDisplay.getUserId());
			if(serviceInfoId > 0){
				processOrderSteps = (List<ProcessOrderBean>) ProcessOrderLocalServiceUtil.getUserProcessStep(themeDisplay.getUserId(), serviceInfoId);
			}
		}else{
			processOrderServices = (List<ProcessOrderBean>) ProcessOrderLocalServiceUtil.getProcessOrderServiceJustFinishedByUser(themeDisplay.getUserId());
			if(serviceInfoId > 0){
				processOrderSteps = (List<ProcessOrderBean>) ProcessOrderLocalServiceUtil.getUserProcessStepJustFinished(themeDisplay.getUserId(), serviceInfoId);
			}
		}
	}catch(Exception e){}
	
	int colWidth = 25;
	if(!todolistDisplayStyle.equals("treemenu_left")){
		colWidth = 20;
	}
	
	Date fromDate = null;
	Date toDate = null;
	
	int fromDateDay = ParamUtil.getInteger(request, "fromDateDay");
	int fromDateMonth = ParamUtil.getInteger(request, "fromDateMonth");
	int fromDateYear = ParamUtil.getInteger(request, "fromDateYear");
	int toDateDay = ParamUtil.getInteger(request, "toDateDay");
	int toDateMonth = ParamUtil.getInteger(request, "toDateMonth");
	int toDateYear = ParamUtil.getInteger(request, "toDateYear");
	
	if(fromDateDay > 0
			&& fromDateMonth >= 0
			&& fromDateYear > 0){
		fromDate = 
			DateTimeUtil.getDateBeginOfDay(fromDateDay, fromDateMonth, fromDateYear);
	}
	if(toDateDay > 0
			&& toDateMonth >= 0
			&& toDateYear > 0){
		toDate = 
			DateTimeUtil.getDateEndOfDay(toDateDay, toDateMonth, toDateYear);
	} else if (fromDateDay > 0
			&& fromDateMonth >= 0
			&& fromDateYear > 0){
		toDate = 
			DateTimeUtil.getDateEndOfDay(fromDateDay, fromDateMonth, fromDateYear);
	}
	
	boolean nullableFromDate = true;
	boolean nullableToDate = true;
	
	if(fromDate != null){
		nullableFromDate = false;
	}
	if(toDate != null){
		nullableToDate = false;
	}
	DictItem curDictItem = null;
	
	DictCollection dictCollection = DictCollectionLocalServiceUtil.
			getDictCollection(themeDisplay.getScopeGroupId(), PortletPropsValues.DATAMGT_MASTERDATA_SERVICE_DOMAIN);
	
	List<DictItem> dictItems = DictItemLocalServiceUtil.getDictItemsByDictCollectionId(dictCollection.getDictCollectionId());
	
%>
<liferay-portlet:renderURL varImpl="searchURL" portletName="<%=WebKeys.PROCESS_ORDER_PORTLET %>">
	<liferay-portlet:param name="tabs1" value="<%=tabs1 %>"/>
	<c:choose>
		<c:when test="<%=tabs1.equals(ProcessUtils.TOP_TABS_PROCESS_ORDER_WAITING_PROCESS) %>">
			<liferay-portlet:param name="mvcPath" value='<%=templatePath +  "processordertodolist.jsp"%>'/>
		</c:when>
		<c:otherwise>
			<liferay-portlet:param name="mvcPath" value='<%=templatePath +  "processorderjustfinishedlist.jsp"%>'/>
		</c:otherwise>
	</c:choose>
</liferay-portlet:renderURL>

<aui:nav-bar cssClass="opencps-toolbar custom-toolbar">

	
	<aui:nav-bar-search cssClass="pull-right">
		<div class="form-search">
			<aui:form action="<%= searchURL %>" method="post" name="fmSearch">
			<liferay-portlet:renderURLParams varImpl="searchURL" />
				<aui:row>
				<c:choose>
						<c:when test="<%=!todolistDisplayStyle.equals(\"treemenu_left\") %>">
							<aui:col width="<%=colWidth %>" cssClass="search-col div100">
								<datamgt:ddr 
									depthLevel="1" 
									dictCollectionCode="DOSSIER_SUB_STATUS" 
									showLabel="<%=false%>"
									emptyOptionLabels="filter-by-subStatus-left"
									itemsEmptyOption="true"
									itemNames="dossierSubStatus"
									optionValueType="code"
									selectedItems="<%=dossierSubStatus %>"
									cssClass="search-input select-box"
								/>
							
							</aui:col>
						</c:when>
							<c:otherwise>
								<aui:input name="dossierSubStatus" type="hidden" value="<%=dossierSubStatus %>"></aui:input>
							</c:otherwise>
					</c:choose>
				</aui:row>
				<aui:row>
					<aui:col width="25" cssClass="search-col">
						<aui:select 
							name="processOrderStage" 
							label="<%=StringPool.BLANK %>" 
							inlineField="<%=true %>" 
							inlineLabel="left"
							onChange='<%=renderResponse.getNamespace() + "searchByProcecssStep(this)"%>'
							cssClass="search-input select-box"
						>
							<aui:option value="<%=false %>"><liferay-ui:message key="filter-process-order-stage-0"/></aui:option>
							<aui:option value="<%=true %>"><liferay-ui:message key="filter-process-order-stage-1"/></aui:option>
						</aui:select>
					</aui:col>
					<aui:col width="25" cssClass="search-col">
					<aui:select name="<%=DossierDisplayTerms.SERVICE_DOMAIN_CODE %>" 
								label="" 
								cssClass="search-input select-box" 
								onChange='<%=renderResponse.getNamespace() + "searchByProcecssOrderService(this)"%>' >
									<aui:option value="">
										<liferay-ui:message key="filter-by-service-domain"/>
									</aui:option>
									<%
										if(dictItems != null){
											for(DictItem dictItem : dictItems){
												if((curDictItem != null && dictItem.getDictItemId() == curDictItem.getDictItemId())||
													(curDictItem != null && dictItem.getTreeIndex().contains(curDictItem.getDictItemId() + StringPool.PERIOD))){
													continue;
											}
															
											int level = StringUtil.count(dictItem.getTreeIndex(), StringPool.PERIOD);
											String index = "|";
											for(int i = 0; i < level; i++){
												index += "_";
											}
									%>
										<aui:option value="<%=dictItem.getDictItemId() %>"><%=index + dictItem.getItemName(locale) %></aui:option>
									<%
											}
										}
									%>
								</aui:select>
							</aui:col>
					<aui:col width="25" cssClass="search-col">
						<aui:select 
							name="serviceInfoId" 
							label="<%=StringPool.BLANK %>" 
							inlineField="<%=true %>" 
							inlineLabel="left"
							onChange='<%=renderResponse.getNamespace() + "searchByProcecssOrderService(this)"%>'
							cssClass="search-input select-box"
						>
							<aui:option value="0" title="service-info"><liferay-ui:message key="filter-service-info"/></aui:option>
							<%
							
								if(Validator.isNotNull(serviceInfoList)){
									for(ServiceInfo serviceInfo : serviceInfoList){
										%>
											<aui:option title="<%=StringUtil.shorten(serviceInfo.getFullName(), 50)%>" value="<%= serviceInfo.getServiceinfoId()%>">
												<%=StringUtil.shorten(serviceInfo.getFullName(), 50) %>
											</aui:option>
										<%
									}
								}
								
							%>
						</aui:select>
					</aui:col>
				
					<aui:col width="25" cssClass="search-col">
						<aui:select 
							name="processStepId" 
							label="<%=StringPool.BLANK %>" 
							inlineField="<%=true %>" 
							inlineLabel="left"
							onChange='<%=renderResponse.getNamespace() + "searchByProcecssStep(this)"%>'
							cssClass="search-input select-box"
						>
							<aui:option value="0"><liferay-ui:message key="filter-process-step"/></aui:option>
							<%
							
								if(processOrderSteps != null){
									for(ProcessOrderBean processOrderStep : processOrderSteps){
										%>
											<aui:option value="<%= processOrderStep.getProcessStepId()%>"><%=processOrderStep.getStepName() %></aui:option>
										<%
									}
								}
								
							%>
						</aui:select>
					</aui:col>
				</aui:row>
				
				<aui:row>
					<aui:col width="25" cssClass="search-col">
							<button type="button" id="<portlet:namespace/>arrowButton" class="fa fa-search-advanced">
								<aui:option value="0" class="fa fa-search-advanced-key" ><liferay-ui:message key="search-advanced"/>
								</aui:option>
							</button>
					</aui:col>
					<div id="<portlet:namespace/>spoiler" class="showBottomRow span9">
					<aui:col width="33" cssClass="search-col">
						<liferay-ui:input-date 
		 					name="fromDate"
		 					nullable="<%= nullableFromDate %>"
		 					dayParam="fromDateDay"
		 					dayValue="<%= fromDateDay %>"
		 					monthParam="fromDateMonth"
		 					monthValue="<%= fromDateMonth %>"
		 					yearParam="fromDateYear"
		 					yearValue="<%= fromDateYear %>"
		 					formName="fmSearch"
		 					autoFocus="<%=true %>"
		 					cssClass="search-input input-keyword" 
		 				>
		 				</liferay-ui:input-date>
					</aui:col>
					
					<aui:col width="33" cssClass="search-col">
						<liferay-ui:input-date 
		 					name="toDate"
		 					nullable="<%= nullableToDate %>"
		 					dayParam="toDateDay"
		 					dayValue="<%= toDateDay %>"
		 					monthParam="toDateMonth"
		 					monthValue="<%= toDateMonth %>"
		 					yearParam="toDateYear"
		 					yearValue="<%= toDateYear %>"
		 					formName="fmSearch"
		 					autoFocus="<%=true %>"
		 					cssClass="search-input input-keyword"
		 				>
		 				</liferay-ui:input-date> 
					</aui:col>
					<aui:col width="33" cssClass="search-col">
						<liferay-ui:input-search 
							id="keywords"
							name="keywords"
							title='<%= LanguageUtil.get(locale, "keywords") %>'
							placeholder='<%=LanguageUtil.get(locale, "keywords") %>'
							cssClass="search-input input-keyword"
						/>
					</aui:col>
					</div>
				</aui:row>
				<aui:row>
					<aui:nav id="toolbarContainer" cssClass="nav-button-container  nav-display-style-buttons pull-left" >
						<c:if test="<%=ProcessOrderPermission.contains(permissionChecker, scopeGroupId, ActionKeys.ASSIGN_PROCESS_ORDER) && 
							tabs1.equals(ProcessUtils.TOP_TABS_PROCESS_ORDER_WAITING_PROCESS) &&
							serviceInfoId > 0 && processStepId > 0%>">
						<portlet:renderURL var="processDossierURL" windowState="<%=LiferayWindowState.NORMAL.toString() %>">
						<portlet:param name="mvcPath" value='<%=templatePath + "processordertodolist.jsp" %>'/>
						<portlet:param name="backURL" value="<%=currentURL %>"/>
						</portlet:renderURL>
					
						<div id ="<portlet:namespace />multiAssignBtn"> 
					
						</div>
						<aui:nav-item 
						cssClass="item-config search-input input-keyword btn-XL"
						id="processDossier" 
						label="process-dossier" 
						iconCssClass="icon-plus icon-config"  
						href='<%="javascript:" + renderResponse.getNamespace() + "processMultipleDossier()" %>'
						/>
						</c:if>
					</aui:nav>
				</aui:row>
			</aui:form>
		</div>
	</aui:nav-bar-search>
</aui:nav-bar>

<aui:script use="liferay-util-list-fields,liferay-portlet-url">
	Liferay.provide(window, '<portlet:namespace/>processMultipleDossier', function() {
		
		var A = AUI();
		
		var currentURL = '<%=currentURL.toString()%>';
		
		var processOrderIds = Liferay.Util.listCheckedExcept(document.<portlet:namespace />fm, '<portlet:namespace />allRowIds');
		
		processOrderIds = processOrderIds.split(",");
		
		if(processOrderIds != ''){
			if(processOrderIds.length > 1){
				// alert('<%= UnicodeLanguageUtil.get(pageContext, "multiple-process-order-handle-is-developing") %>');
				var multiAssignURL = Liferay.PortletURL.createURL('<%= PortletURLFactoryUtil.create(request, WebKeys.PROCESS_ORDER_PORTLET, themeDisplay.getPlid(), PortletRequest.RENDER_PHASE) %>');
				multiAssignURL.setParameter("mvcPath","/html/portlets/processmgt/processorder/assign_multil_process_order.jsp");
				multiAssignURL.setParameter("processOrderIds",processOrderIds.toString());
				multiAssignURL.setWindowState("<%=LiferayWindowState.POP_UP.toString()%>");
				multiAssignURL.setPortletMode("normal");
				openDialog(multiAssignURL.toString(), "assign-multi-dossier", "assign-multi-dossier");
				return;
			}else if(processOrderIds.length == 0){
				alert('<%= UnicodeLanguageUtil.get(pageContext, "you-need-select-any-process-order-to-process") %>');
				return;
			}else{
				var portletURL = Liferay.PortletURL.createURL('<%= PortletURLFactoryUtil.create(request, WebKeys.PROCESS_ORDER_PORTLET, themeDisplay.getPlid(), PortletRequest.RENDER_PHASE) %>');
				portletURL.setParameter("mvcPath", "/html/portlets/processmgt/processorder/process_order_detail.jsp");
				portletURL.setWindowState("<%=LiferayWindowState.NORMAL.toString()%>"); 
				portletURL.setPortletMode("normal");
			
				portletURL.setParameter("processOrderId", processOrderIds[0]);
				portletURL.setParameter("backURL", currentURL);
				window.location.href = portletURL.toString();
			}
		}else{
			alert('<%= UnicodeLanguageUtil.get(pageContext, "you-need-select-any-process-order-to-process") %>');
			return;
		}
	});
	
	A.one('#<portlet:namespace />dossierSubStatus').on('change', function(){
		submitForm(document.<portlet:namespace />fmSearch);
	});
	
	Liferay.provide(window, '<portlet:namespace/>searchByProcecssStep', function(e) {
		submitForm(document.<portlet:namespace />fmSearch);
	},['liferay-portlet-url']);
	
	Liferay.provide(window, '<portlet:namespace/>searchByProcecssOrderService', function(e) {
		submitForm(document.<portlet:namespace />fmSearch);
	},['liferay-portlet-url']);
</aui:script>

<%!
	private Log _log = LogFactoryUtil.getLog("html.portlets.dossiermgt.frontoffice.toolbar.jsp");
%>