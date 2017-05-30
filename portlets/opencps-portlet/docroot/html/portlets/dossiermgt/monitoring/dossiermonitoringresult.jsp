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
<%@page import="org.opencps.util.DateTimeUtil"%>
<%@page import="org.opencps.dossiermgt.service.DossierLogLocalServiceUtil"%>
<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@page import="org.opencps.dossiermgt.model.DossierLog"%>
<%@page import="java.util.List"%>
<%@page import="javax.portlet.PortletURL"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.liferay.portal.kernel.util.FastDateFormatFactoryUtil"%>
<%@page import="java.text.Format"%>
<%@page import="org.opencps.servicemgt.service.ServiceInfoLocalServiceUtil"%>
<%@page import="org.opencps.servicemgt.model.ServiceInfo"%>
<%@page import="org.opencps.dossiermgt.util.DossierMgtUtil"%>
<%@page import="org.opencps.dossiermgt.model.Dossier"%>

<%@ include file="../init.jsp"%>

<%
	String backURL = ParamUtil.getString(request, "backURL");
	String receptionNo = ParamUtil.getString(request, "keywords", StringPool.BLANK);
	Dossier dossier = DossierMgtUtil.searchDossier(receptionNo);
	
	long dossierId = dossier != null ? dossier.getDossierId() : 0;
	
	ServiceInfo serviceInfo = null;
	if(Validator.isNotNull(dossier)) {
		try {
			serviceInfo = ServiceInfoLocalServiceUtil.getServiceInfo(dossier.getServiceInfoId());
		}
		catch (Exception ex) {
			
		}
	}
	
	Format dateFormatDate = FastDateFormatFactoryUtil.getDate(locale, timeZone);
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy | hh:MM:ss");

	String keywordSearch = ParamUtil.getString(request, "keywords", StringPool.BLANK);
	
	PortletURL iterator = renderResponse.createRenderURL();
	iterator.setParameter("mvcPath","/html/portlets/dossiermgt/monitoring/dossiermonitoringresult.jsp");
	
	List<DossierLog> dossierLogs = null;
	int dossierLogsCount = 0;
%>

<liferay-portlet:renderURL var="searchUrl" >
	<portlet:param name="mvcPath" value="/html/portlets/dossiermgt/monitoring/dossiermonitoringresult.jsp"/>
	<portlet:param name="backURL" value="<%=backURL %>"/>
</liferay-portlet:renderURL>

<liferay-ui:header backURL="<%= backURL %>" title="dossier-list" backLabel="back" />

<div class = "page-search">
	<div class="monitoring-style2-wrapper">
		<div class="monitoring-style2-content">
			<aui:form action="<%= searchUrl %>" method="post" name="fm">
				<input 
					class="search-query" 
					name="<portlet:namespace/>keywords" 
					type="text" 
					title='<liferay-ui:message key="enter-dossier-no" />'
					placeholder='<liferay-ui:message key="enter-dossier-no" />'
				>
				<button class="btn search-button" type="submit" title='<liferay-ui:message key="enter-dossier-no-to-lookup" />'><i></i><liferay-ui:message key="lookup" /></button>
			</aui:form>
		</div>
	</div>
</div>

<div class="head">
	<h5 class="headh5"><liferay-ui:message key="ket-qua-tra-cuu-ho-so"/> 
		<%= receptionNo %>
	</h5>
</div>

<c:choose>
	<c:when test="<%= Validator.isNotNull(dossier) %>">
		<div class="content">
			<div class="row-fluid">
				<div class="span12">
					<div class="row-fluid">
						<b><liferay-ui:message key="dossier-info" /></b>
					</div>
					<div class="row-fluid">
						<b><liferay-ui:message key="reception-no" /> : </b>
							<span><%=Validator.isNotNull(dossier.getReceptionNo()) ? dossier
											.getReceptionNo() : LanguageUtil.get(pageContext,
											"monitoring-chua-co")%></span>
					</div>
					<div class="row-fluid">
						<b><liferay-ui:message key="service-name" /> : </b>
							<span><%=Validator.isNotNull(serviceInfo) ? serviceInfo
										.getServiceName() : LanguageUtil.get(pageContext,
										"monitoring-chua-co")%></span>
					</div>
					<div class="row-fluid">
						<b><liferay-ui:message key="administration-name" /> : </b>
							<span><%=Validator.isNotNull(dossier) ? dossier
								.getGovAgencyName() : LanguageUtil.get(pageContext,
								"monitoring-chua-co")%></span>
					</div>
					<div class="row-fluid">
						<b><liferay-ui:message key="subject-name" /> : </b>
							<span><%=Validator.isNotNull(dossier.getSubjectName()) ? dossier
									.getSubjectName() : LanguageUtil.get(pageContext,
									"monitoring-chua-co")%></span>
					</div>
					<div class="row-fluid">
						<b><liferay-ui:message key="address" /> : </b>
							<span><%=Validator.isNotNull(dossier.getAddress()) ? dossier
										.getAddress() : LanguageUtil.get(pageContext,
										"monitoring-chua-co")%></span>
					</div>
					<div class="row-fluid">
						<b><liferay-ui:message key="receive-datetime" /> : </b>
							<span><%=(Validator.isNotNull(dossier.getReceiveDatetime())) ? dateFormatDate
										.format(dossier.getReceiveDatetime()) : LanguageUtil
										.get(pageContext, "monitoring-chua-co")%></span>
					</div>
					<div class="row-fluid">
						<b><liferay-ui:message key="estimate-datetime" /> : </b>
							<span><%=(Validator.isNotNull(dossier.getReceiveDatetime())) ? dateFormatDate
										.format(dossier.getEstimateDatetime()) : LanguageUtil
										.get(pageContext, "monitoring-chua-co")%></span>
					</div>
					<div class="row-fluid">
						<b><liferay-ui:message key="finish-datetime" /> : </b>
							<span><%=(Validator.isNotNull(dossier.getReceiveDatetime())) ? dateFormatDate
										.format(dossier.getFinishDatetime()) : LanguageUtil
										.get(pageContext, "monitoring-chua-co")%></span>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span12">
					<div class="row-fluid">
						<b><liferay-ui:message key="qua-trinh-xu-ly-ho-so" /></b>
					</div>
					<div class="row-fluid">
						<liferay-ui:search-container
							iteratorURL="<%= iterator %>"
							emptyResultsMessage="no-action"
							delta="20"
						>
							<liferay-ui:search-container-results>
								<%
									try {
										dossierLogs = DossierLogLocalServiceUtil.findDossierLog(1, dossierId, searchContainer.getStart(), searchContainer.getEnd());
										dossierLogsCount =DossierLogLocalServiceUtil.countDossierLog(1, dossierId);
									} catch (Exception e) {
										_log.error(e);
									}
									
									total = dossierLogsCount;
									results = dossierLogs;
									pageContext.setAttribute("results", results);
									pageContext.setAttribute("total", total);
								%>
							</liferay-ui:search-container-results>
							<liferay-ui:search-container-row 
								className="org.opencps.dossiermgt.model.DossierLog"
								modelVar="dossierLog"
								keyProperty="dossierLogId"
							>
								<liferay-ui:search-container-column-text value="<%=String.valueOf(row.getPos() + 1 + (searchContainer.getCur() - 1) * searchContainer.getDelta()) %>" name="stt"/>
								<liferay-ui:search-container-column-text value="<%=dossierLog.getActionInfo() %>" name="action"/>
								<liferay-ui:search-container-column-text value="<%=DateTimeUtil.convertDateToString(dossierLog.getCreateDate(), DateTimeUtil._VN_DATE_TIME_FORMAT) %>" name="action-date"/>
								<liferay-ui:search-container-column-text value="<%=dossierLog.getMessageInfo() %>" name="note"/>
							</liferay-ui:search-container-row>
							<liferay-ui:search-iterator paginate="false"/>
						</liferay-ui:search-container>
					</div>
				</div>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<div class="error-msg" style="height: 200px;">
			<liferay-ui:message key="<%= LanguageUtil.format(pageContext, \"dossier-with-receptionno-x-does-not-exist-in-system\", receptionNo) %>" />
		</div>
	</c:otherwise>
</c:choose>

<aui:script>
	AUI().ready(function(A){
		A.one('.monitoring-style2-wrapper .search-button').on('click', function(event){
			event.preventDefault();
			if (A.one('.monitoring-style2-wrapper .search-query').val().length > 0){
				document.getElementById('<portlet:namespace />fm').submit();
			}
		});
	});
</aui:script>

<%!
	private Log _log = LogFactoryUtil.getLog("html.portlets.dossiermgt.monitoring.result.jsp");
%>