<%@page import="com.liferay.portal.kernel.util.HtmlUtil"%>
<%@page import="org.opencps.dossiermgt.model.ServiceConfig"%>
<%@page import="org.opencps.datamgt.model.DictItem"%>
<%@page import="org.opencps.util.PortletPropsValues"%>
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

<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="javax.portlet.PortletURL"%>
<%@page import="org.opencps.dossiermgt.model.Dossier"%>
<%@page import="org.opencps.dossiermgt.model.DossierLog"%>
<%@page import="org.opencps.dossiermgt.service.DossierLogLocalServiceUtil"%>
<%@page import="org.opencps.dossiermgt.util.DossierMgtUtil"%>
<%@page import="org.opencps.servicemgt.model.ServiceInfo"%>
<%@page import="org.opencps.servicemgt.service.ServiceInfoLocalServiceUtil"%>
<%@page import="org.opencps.util.DateTimeUtil"%>
<%@page import="org.opencps.util.PortletUtil"%>
<%@page import="org.opencps.util.WebKeys"%>
<%@page import="org.opencps.processmgt.util.ProcessUtils"%>
<%@page import="org.opencps.processmgt.model.ProcessStep"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="org.opencps.dossiermgt.model.DossierFileLog"%>

<%@ include file="../../../init.jsp"%>

<%
	Dossier dossier = (Dossier) request.getAttribute(WebKeys.DOSSIER_ENTRY);

	ServiceConfig serviceConfig = (ServiceConfig) request.getAttribute(WebKeys.SERVICE_CONFIG_ENTRY);

	long dossierId = dossier != null ? dossier.getDossierId() : 0L;
	
	List<DossierLog> dossierLogs = new ArrayList<DossierLog>();
	
	String serviceName = StringPool.DASH;
	String receptionNo = StringPool.DASH;
	
	ServiceInfo serviceInfo = ServiceInfoLocalServiceUtil.getServiceInfo(serviceConfig.getServiceInfoId());
	
	if(serviceInfo != null) {
		serviceName = serviceInfo.getServiceName();
	}
	
	if(Validator.isNotNull(dossier)) {
		receptionNo = dossier.getReceptionNo();
	}
	
	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter("mvcPath", "/html/portlets/dossiermgt/frontoffice/dossier/history.jsp");
	
	boolean isEditDossier = ParamUtil.getBoolean(request, "isEditDossier");
	
	DictItem adminAction = PortletUtil.getDictItem(PortletPropsValues.DATAMGT_MASTERDATA_GOVERNMENT_AGENCY, 
			serviceConfig.getGovAgencyCode(), 
			scopeGroupId);

%>

<div class="ocps-dossier-process">
	<aui:row cssClass="header-title custom-title">
		<aui:col width="100">
			<c:choose>
				<c:when test="<%= (dossier == null) || (dossier != null && dossier.getDossierStatus().equalsIgnoreCase(PortletConstants.DOSSIER_STATUS_NEW) ) %>">
					<liferay-ui:message key="add-dossier"/>
				</c:when>
				
				<c:when test="<%= isEditDossier %>">
					<liferay-ui:message key="update-dossier"/>
				</c:when>
				
				<c:otherwise>
					<liferay-ui:message key="dossier"/>
				</c:otherwise>
			</c:choose>
		</aui:col>
	</aui:row>
	
	<div class="dossier-info-header">
		<div class="row-fluid">
			<label class="span3"><liferay-ui:message key="service-name"/>:</label>
			<p class="span9"><%=HtmlUtil.escape(serviceName) %></p>
		</div>
		
		<div class="row-fluid">
			<label class="span3"><liferay-ui:message key="service-administration-action"/>:</label>
			<p class="span9"><%=Validator.isNotNull(adminAction) ? adminAction.getItemName(locale,true) : StringPool.BLANK %></p>
		</div>
		
		<div class="row-fluid">
			<label class="span3"><liferay-ui:message key="dossier-no"/>:</label>
			<p class="span3"><%=dossier != null ? dossier.getDossierId() : StringPool.DASH %></p>
			<label class="span3"><liferay-ui:message key="dossier-reception-no"/>:</label>
			<p class="span3"><%=dossier != null && Validator.isNotNull(dossier.getReceptionNo()) ? dossier.getReceptionNo() : StringPool.DASH %></p>
		</div>
		
		<div class="row-fluid">
			<label class="span3"><liferay-ui:message key="dossier-status"/>:</label>
			<p class="span9"><span class="red"><%=dossier != null ? PortletUtil.getDossierStatusLabel(dossier.getDossierStatus(), locale) : "" %></span></p>
		</div>
	</div>
	
	<liferay-ui:search-container 
		emptyResultsMessage="no-history-were-found"
		iteratorURL="<%=iteratorURL %>"
		delta="<%= PAGINATE_NUMBER %>"
		deltaConfigurable="true"
	>
		<liferay-ui:search-container-results>
			<%
	/* 			dossierLogs = DossierLogLocalServiceUtil.getDossierLogByDossierId(dossierId, searchContainer.getStart(), searchContainer.getEnd());
	 */			
	 			dossierLogs = DossierLogLocalServiceUtil.findDossierLog(1, dossierId, searchContainer.getStart(), searchContainer.getEnd());
	
				results = dossierLogs;
				
				//total = DossierLogLocalServiceUtil.countDossierLogByDossierId(dossierId);
				total = DossierLogLocalServiceUtil.countDossierLog(1, dossierId);
				
				pageContext.setAttribute("results", results);
				pageContext.setAttribute("total", total);
				
				//List<ResultRow> rows = searchContainer.getResultRows();
				
			%>
		</liferay-ui:search-container-results>
		
		<liferay-ui:search-container-row 
			className="org.opencps.dossiermgt.model.DossierLog" 
			modelVar="dossierLog" 
			keyProperty="dossierLogId"
		>
			<aui:row cssClass="top-line pd_b20 pd_t20">
				<aui:col width="30">
					<span class="span1">
						<i class="fa fa-circle blue sx10"></i>
					</span>
					
					<span class="span8">
						<%=
							Validator.isNotNull(dossierLog.getUpdateDatetime()) ?
							DateTimeUtil.convertDateToString(dossierLog.getUpdateDatetime(), DateTimeUtil._VN_DATE_TIME_FORMAT) : 
							DateTimeUtil._EMPTY_DATE_TIME 
						%>
					</span>
				</aui:col>
				<aui:col width="70">
	
					<aui:row>
						<span class="span4 bold">
							<liferay-ui:message key="action" />
						</span>
						
						<span class="span8">
							<liferay-ui:message key="<%= DossierMgtUtil.getDossierLogs(StringPool.BLANK, dossierLog.getActionInfo())  %>"/>
						</span>
						
					</aui:row>
					
					<aui:row>
						<span class="span4 bold">
							<liferay-ui:message key="dossier-status" />
						</span>
						
						<span class="span8">
							
							<%
								String dossierSubStatus = StringPool.BLANK;
								
								ProcessStep processStep = ProcessUtils.getPostProcessStep(dossierLog.getStepId());
	
								if (Validator.isNotNull(processStep) && Validator.isNotNull(processStep.getDossierSubStatus())) {
									dossierSubStatus = LanguageUtil.get(locale, processStep.getDossierSubStatus());
								}
								
							%>
							
							<%=PortletUtil.getDossierStatusLabel(dossierLog.getDossierStatus(), locale)%> 
							
							<c:if test="<%= Validator.isNotNull(dossierSubStatus) %>">
								<span class="sub-status">
									<%= StringPool.COLON + StringPool.SPACE + dossierSubStatus %> 
								</span>
							</c:if>
							
						</span>
					</aui:row>
				
					<aui:row>
						<span class="span4 bold">
							<liferay-ui:message key="actor" />
						</span>
						
						<span class="span8">
							<liferay-ui:message key="<%= dossierLog.getActorName() %>"/>
						</span>
					</aui:row>
	
					
					<aui:row>
						<span class="span4 bold">
							<liferay-ui:message key="note" />
						</span>
						
						<span class="span8">
							<liferay-ui:message key="<%= DossierMgtUtil.getDossierLogs(StringPool.BLANK, dossierLog.getMessageInfo()).replaceAll(\"update-version-file\", LanguageUtil.get(pageContext, \"update-version-file\"))  %>"/>
						</span>
						
					</aui:row>
					
					<%
						List<DossierFileLog> logFiles = DossierMgtUtil.getFileLogs(dossierLog.getDossierLogId(), dossierId);
					
					
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
									SimpleDateFormat sdf = new SimpleDateFormat ("dd/MM/yy | hh:mm:ss");
									for (DossierFileLog lf : logFiles) {
										
										String cssClass = "dossier-file-status-" + lf.getActionCode();
										String actionCode = LanguageUtil.get(locale, cssClass);
								%>
									<div style="padding: 3px; ">
										<%= StringPool.GREATER_THAN %> 
										<%= LanguageUtil.get(pageContext, "history-file-action-"+lf.getActionCode()) %> :
											 <aui:a href="#" >
											 	<%= lf.getFileName() %> 
											 	<span style="font: smaller; color: #cbcbcb;">(<%= sdf.format(lf.getModifiedDate()) %> )</span>
											 	
											 </aui:a>
									</div>
								<%
									}
								%>
								
							</span>
							
						</aui:row>
					
					</c:if>
					
				</aui:col>
			</aui:row>
		</liferay-ui:search-container-row>
		<liferay-ui:search-iterator paginate="false"/>
	</liferay-ui:search-container>
</div>

<%!
	private int PAGINATE_NUMBER = 200;
%>

