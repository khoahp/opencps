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
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.liferay.portal.service.UserLocalServiceUtil"%>
<%@page import="org.opencps.util.DateTimeUtil"%>
<%@page import="org.opencps.processmgt.service.ActionHistoryLocalServiceUtil"%>
<%@page import="org.opencps.processmgt.model.ActionHistory"%>
<%@page import="org.opencps.processmgt.model.ProcessOrder"%>
<%@page import="org.opencps.dossiermgt.service.FileGroupLocalServiceUtil"%>
<%@page import="org.opencps.dossiermgt.model.FileGroup"%>
<%@page import="org.opencps.processmgt.service.ProcessOrderLocalServiceUtil"%>
<%@page import="org.opencps.processmgt.service.ProcessOrderLocalService"%>
<%@page import="org.opencps.dossiermgt.NoSuchDossierException"%>
<%@page import="org.opencps.servicemgt.service.ServiceInfoLocalServiceUtil"%>
<%@page import="org.opencps.dossiermgt.service.DossierLocalServiceUtil"%>
<%@page import="org.opencps.dossiermgt.model.Dossier"%>
<%@page import="org.opencps.dossiermgt.service.DossierLogLocalServiceUtil"%>
<%@page import="org.opencps.processmgt.util.ProcessUtils"%>
<%@page import="org.opencps.dossiermgt.util.DossierMgtUtil"%>
<%@page import="org.opencps.dossiermgt.model.DossierFileLog"%>

<%@ include file="../../init.jsp"%>

<%
	long dossierId = ParamUtil.getLong(request, "dossierId");
	
	Dossier dossier = null;
	
	try {
		dossier = DossierLocalServiceUtil.getDossier(dossierId);
	}
	catch (NoSuchDossierException ex) {
		
	}
	
	ProcessOrder processOrder = null;
	
	try {
		processOrder = ProcessOrderLocalServiceUtil.getProcessOrder(dossierId, 0);
	}
	catch (Exception ex) {
		
	}
	
	long processOrderId = Validator.isNotNull(processOrder) ? processOrder.getProcessOrderId() : 0L;

	
	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter("mvcPath", templatePath + "dossier/history.jsp");
	
	List<ActionHistory> actionHistories = new ArrayList<ActionHistory>();
	
	SimpleDateFormat sdf = new SimpleDateFormat();
	
	
	ServiceInfo serviceInfo = null;
	String receptionNo = StringPool.BLANK;
	String serviceName = StringPool.BLANK;
	if(Validator.isNotNull(processOrder)) {
		try {
			/* dossier = DossierLocalServiceUtil.getDossier(processOrder.getDossierId()); */
			receptionNo = dossier.getReceptionNo();
			
			serviceInfo = ServiceInfoLocalServiceUtil.getServiceInfo(processOrder.getServiceInfoId());
			serviceName = serviceInfo.getServiceName();
		} catch (Exception e) {
			
		}
	}
	 
	
%>
<div class="ocps-title-detail">
	<aui:row>
		<aui:col width="50">
			<aui:row>
				<aui:col width="30" cssClass="bold">
					<liferay-ui:message key="dossier-no"/>
				</aui:col>
				<aui:col width="70">
					<%=Validator.isNotNull(dossier.getDossierId()) ? dossier.getDossierId() : StringPool.DASH %>
				</aui:col>
			</aui:row>
		</aui:col>
		<aui:col width="50">
			<aui:row>
				<aui:col width="30" cssClass="bold">
					<liferay-ui:message key="dossier-reception-no"/>
				</aui:col>
				<aui:col width="70">
					<%=Validator.isNotNull(dossier.getReceptionNo()) ? dossier.getReceptionNo() : StringPool.DASH %>
				</aui:col>
			</aui:row>
		</aui:col>
	</aui:row>
	<div class="ocps-title-detail-bot">
		<p class="service-service-name"><%=serviceName%></p>
	</div>
</div>

<div class="bound-search-container-history">
	<liferay-ui:search-container 
		emptyResultsMessage="no-action-history-were-found"
		iteratorURL="<%=iteratorURL %>"
		delta="<%= NUMBER_DELTA_PADDING %>"
		deltaConfigurable="true"
		>
		<liferay-ui:search-container-results>
			<%
				results = DossierLogLocalServiceUtil.findDossierLog(2, dossier.getDossierId(), searchContainer.getStart(),
						searchContainer.getEnd()); 
				total = DossierLogLocalServiceUtil.countDossierLog(2, dossier.getDossierId()); 
						
				pageContext.setAttribute("results", results);
				pageContext.setAttribute("total", total);
			%>
		</liferay-ui:search-container-results>
		<liferay-ui:search-container-row 
			className="org.opencps.dossiermgt.model.DossierLog" 
			modelVar="dossierLog" 
			keyProperty="dossierId"
		>
		
				<aui:row cssClass="top-line pd_b20 pd_t20">
					<aui:col width="50">
						<aui:row>
							<span class="span4 bold">
								<liferay-ui:message key="step-name" />
							</span>
							
							<span class="span8">
								<%= dossierLog.getStepName() %>
							</span>
						</aui:row>
						
						<aui:row>
							<span class="span4 bold">
								<liferay-ui:message key="action-date" />
							</span>
							
							<span class="span8">
								<%= sdf.format(dossierLog.getCreateDate()) %>
							</span>
						</aui:row>
						
						<aui:row>
							<span class="span4 bold">
								<liferay-ui:message key="action-date-over" />
							</span>
							
							<span class="span8">
								<%
									String timeDelay = StringPool.BLANK;
									ActionHistory actionHis = ProcessUtils.getActionHistoryByLogId(dossierLog.getDossierLogId());
								
									if (Validator.isNotNull(actionHis)) {
										timeDelay = DateTimeUtil.convertTimemilisecondsToFormat(actionHis.getDaysDelay(), themeDisplay.getLocale());
									}
								%>
								<%= timeDelay %>
							</span>
						</aui:row>
					</aui:col>
					<aui:col width="50">
						<aui:row>
							<span class="span4 bold">
								<liferay-ui:message key="action-user" />
							</span>
							
							<span class="span8">
								<%= dossierLog.getActorName() %>
							</span>
						</aui:row>
					
						<aui:row>
							<span class="span4 bold">
								<liferay-ui:message key="action-name" />
							</span>
							
							<span class="span8">
								<%= LanguageUtil.get(locale, dossierLog.getActionInfo()) %>
							</span>
						</aui:row>
						
						<aui:row>
							<span class="span4 bold">
								<liferay-ui:message key="action-note" />
							</span>
							
							<span class="span8">
								<%=LanguageUtil.get(locale, dossierLog.getMessageInfo())%>
							</span>
							
						</aui:row>
						
						<%
							List<DossierFileLog> logFiles = DossierMgtUtil.getFileLogs(dossierLog.getDossierLogId(), dossierLog.getDossierId());
						%>
						<c:if test="<%= logFiles.size() != 0 %>">
							<aui:row>
								<span class="span12 bold">
									<liferay-ui:message key="file-modified" />
								</span>
								
							</aui:row>
						
							<aui:row>
								<span class="span12">
									<%
										for (DossierFileLog lf : logFiles) {
											
											String cssClass = "dossier-file-status-" + lf.getActionCode();
											String actionCode = LanguageUtil.get(locale, cssClass);
									%>
										<span style="padding: 3px; display: block;">
											<%= StringPool.GREATER_THAN %> 
											<%= LanguageUtil.get(pageContext, "history-file-action-"+lf.getActionCode()) %> :
												 <aui:a href="#" >
												 	<%= lf.getFileName() %> 
												 	<span style="font: smaller; color: #cbcbcb;">(<%= sdf.format(lf.getModifiedDate()) %> )</span>
												 	
												 </aui:a>
										</span>
									<%
										}
									%>
									
								</span>
								
							</aui:row>
						
						</c:if>
				
					</aui:col>
				</aui:row>
				
		</liferay-ui:search-container-row>
		<liferay-ui:search-iterator type="normal"/>
	</liferay-ui:search-container>
</div>

<%!
	private int NUMBER_DELTA_PADDING = 100;
%>
