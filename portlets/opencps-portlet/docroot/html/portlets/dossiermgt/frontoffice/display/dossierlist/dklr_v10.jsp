
<%@page import="com.liferay.portal.kernel.util.Constants"%>
<%@page import="org.opencps.dossiermgt.service.ServiceConfigLocalServiceUtil"%>
<%@page import="org.opencps.dossiermgt.model.ServiceConfig"%>
<%@page import="com.liferay.portal.kernel.util.HtmlUtil"%>
<%@page import="org.opencps.dossiermgt.service.DossierTemplateLocalServiceUtil"%>
<%@page import="org.opencps.dossiermgt.model.DossierTemplate"%>
<%@page import="org.opencps.util.ActionKeys"%>
<%@page import="org.opencps.dossiermgt.permissions.DossierPermission"%>
<%@page import="com.liferay.portal.kernel.search.Indexer"%>
<%@page import="com.liferay.portal.kernel.search.IndexerRegistryUtil"%>
<%@page import="org.opencps.dossiermgt.service.DossierFileLocalServiceUtil"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="org.opencps.servicemgt.NoSuchServiceInfoException"%>
<%@page import="org.opencps.servicemgt.model.ServiceInfo"%>
<%@page import="org.opencps.servicemgt.service.ServiceInfoLocalServiceUtil"%>
<%@page import="com.liferay.portal.kernel.dao.orm.QueryUtil"%>
<%@page import="org.opencps.dossiermgt.service.DossierLogLocalServiceUtil"%>
<%@page import="org.opencps.dossiermgt.model.DossierLog"%>
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

<%@page import="com.liferay.portal.kernel.dao.search.SearchEntry"%>
<%@page import="com.liferay.portal.kernel.json.JSONFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.json.JSONObject"%>
<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@page import="com.liferay.portal.kernel.log.Log"%>
<%@page import="com.liferay.portal.kernel.log.LogFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@page import="com.liferay.portlet.PortletURLFactoryUtil"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="javax.portlet.PortletRequest"%>
<%@page import="javax.portlet.PortletURL"%>
<%@page import="org.opencps.datamgt.model.DictItem"%>
<%@page import="org.opencps.datamgt.service.DictItemLocalServiceUtil"%>
<%@page import="org.opencps.dossiermgt.model.Dossier"%>
<%@page import="org.opencps.dossiermgt.NoSuchDossierException"%>
<%@page import="org.opencps.dossiermgt.NoSuchDossierTemplateException"%>
<%@page import="org.opencps.dossiermgt.RequiredDossierPartException"%>
<%@page import="org.opencps.dossiermgt.search.DossierDisplayTerms"%>
<%@page import="org.opencps.dossiermgt.search.DossierSearch"%>
<%@page import="org.opencps.dossiermgt.search.DossierSearchTerms"%>
<%@page import="org.opencps.dossiermgt.service.DossierLocalServiceUtil"%>
<%@page import="org.opencps.dossiermgt.util.DossierMgtUtil"%>
<%@page import="org.opencps.processmgt.util.ProcessOrderUtils"%>
<%@page import="org.opencps.util.DateTimeUtil"%>
<%@page import="org.opencps.util.MessageKeys"%>
<%@page import="org.opencps.util.PortletConstants"%>
<%@page import="org.opencps.util.PortletPropsValues"%>
<%@page import="org.opencps.util.PortletUtil"%>
<%@page import="org.opencps.util.WebKeys"%>

<%@ include file="../../../init.jsp"%>

<liferay-ui:success  key="<%=MessageKeys.DEFAULT_SUCCESS_KEY %>" message="<%=MessageKeys.DEFAULT_SUCCESS_KEY %>"/>

<liferay-ui:success  key="<%=MessageKeys.DEFAULT_SUCCESS_KEY_X %>" message="<%=MessageKeys.DEFAULT_SUCCESS_KEY_X %>"/>

<liferay-ui:error 
	exception="<%= NoSuchDossierException.class %>" 
	message="<%=NoSuchDossierException.class.getName() %>"
/>
<liferay-ui:error 
	exception="<%= NoSuchDossierTemplateException.class %>" 
	message="<%=NoSuchDossierTemplateException.class.getName() %>"
/>
<liferay-ui:error 
	exception="<%= RequiredDossierPartException.class %>" 
	message="<%=RequiredDossierPartException.class.getName() %>"
/>

<%
	String receptionNo = ParamUtil.getString(request, DossierDisplayTerms.RECEPTION_NO);
	String govAgencyCode = ParamUtil.getString(request, DossierDisplayTerms.GOVAGENCY_CODE);
	String dossierTemplateNo = ParamUtil.getString(request, DossierDisplayTerms.DOSSIER_TEMPLATE_NO);
	String serviceInfoNo = ParamUtil.getString(request, DossierDisplayTerms.SERVICE_INFO_NO);
	String dossierStatus = ParamUtil.getString(request, DossierDisplayTerms.DOSSIER_STATUS);
	String submitDateTimeFrom = ParamUtil.getString(request, DossierDisplayTerms.SUBMIT_DATETIME_FROM);
	String submitDateTimeTo = ParamUtil.getString(request, DossierDisplayTerms.SUBMIT_DATETIME_TO);
	
	String headerName = ParamUtil.getString(request, "headerName");
	
	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter("mvcPath", templatePath + "frontofficedossierlist.jsp");
	iteratorURL.setParameter("tabs1", DossierMgtUtil.TOP_TABS_DOSSIER);
	iteratorURL.setParameter("receptionNo", receptionNo);
	iteratorURL.setParameter("serviceInfoNo", serviceInfoNo);
	iteratorURL.setParameter("dossierStatus", dossierStatus);
	iteratorURL.setParameter("submitDateTimeFrom", submitDateTimeFrom);
	iteratorURL.setParameter("submitDateTimeTo", submitDateTimeTo);
	
	List<Dossier> dossiers =  new ArrayList<Dossier>();
	
	int totalCount = 0;

	List<String> headerNames = new ArrayList<String>();
	headerNames.add("stt");
	headerNames.add("reception-no");
	headerNames.add("service-info-name");
	headerNames.add("submit-date-time");
	headerNames.add("note");
	headerNames.add("action");
	
	List<DictItem> listDossierStatus = DictItemLocalServiceUtil.findDictItemsByG_DC_S(scopeGroupId, "DOSSIER_STATUS");
	
	Map<String, String> keywordIndexs = new HashMap<String, String>();
	keywordIndexs.put(DossierDisplayTerms.GOVAGENCY_CODE, govAgencyCode);
	keywordIndexs.put(DossierDisplayTerms.RECEPTION_NO, receptionNo);
	keywordIndexs.put(DossierDisplayTerms.SERVICE_INFO_NO, serviceInfoNo);
	keywordIndexs.put(DossierDisplayTerms.DOSSIER_STATUS, dossierStatus);
	
	ServiceInfo serviceInfo = null ;
	
	if(Validator.isNotNull(serviceInfoNo)) {
		try {
			serviceInfo = ServiceInfoLocalServiceUtil.getServiceInfoByServiceNo(serviceInfoNo);
		} catch(Exception e) {
			
		}
	}
	
	DossierTemplate dossierTemplate = null;
	
	if(Validator.isNotNull(dossierTemplateNo)) {
		try {
			dossierTemplate = DossierTemplateLocalServiceUtil.getDossierTemplate(dossierTemplateNo);
		} catch(Exception e) {
			
		}
	}
	
	ServiceConfig serviceConfig = null;
	
	if(serviceInfo != null && dossierTemplate != null && Validator.isNotNull(govAgencyCode)) {
		try {
			serviceConfig = ServiceConfigLocalServiceUtil.getServiceConfigsByG_S_G_T(
					scopeGroupId, serviceInfo.getServiceinfoId(), govAgencyCode, dossierTemplate.getDossierTemplateId());
		} catch(Exception e) {
			
		}
	}
	
	DictItem dossierStatusItem = PortletUtil.getDictItem("DOSSIER_STATUS", dossierStatus, scopeGroupId);
%>

<liferay-portlet:renderURL varImpl="searchURL">
	<portlet:param name="mvcPath" value='<%= templatePath + "frontofficedossierlist.jsp" %>' />
	<portlet:param name="tabs1" value='<%= DossierMgtUtil.TOP_TABS_DOSSIER %>' />
</liferay-portlet:renderURL>

<aui:form action="<%= searchURL %>" method="get" name="fm">
	<liferay-portlet:renderURLParams varImpl="searchURL" />
	
	<aui:row>
		<aui:col width="100">
			<c:if test="<%= Validator.isNotNull(headerName) %>">
				<div class="header-name-1"><%= HtmlUtil.escape(headerName) %></div>
			</c:if>
			
			<c:if test="<%= dossierTemplate != null %>">
				<div class="header-name-2"><%= HtmlUtil.escape(dossierTemplate.getTemplateName()) %></div>
			</c:if>
			
			<aui:row>
				<aui:col width="50" >
					<aui:input type="text" name="<%= DossierDisplayTerms.DOSSIER_ID %>" value=""/>
				</aui:col>
				
				<aui:col width="50" >
					<label class="dossier-status-label"><liferay-ui:message key="dossier-status"/>:</label> <span class="dossier-status"><%= dossierStatusItem != null ? dossierStatusItem.getItemName(locale) : "" %></span> 
				</aui:col>
			</aui:row>
			
			<aui:row>
				<aui:col width="50" >
					<aui:input type="text" name="<%= DossierDisplayTerms.RECEPTION_NO %>" value="" label="reception-no"/>
				</aui:col>
			</aui:row>
			
			<aui:row>
				<aui:col width="50" >
					<aui:input type="text" name="<%= DossierDisplayTerms.SUBMIT_DATETIME_FROM %>" label="ngay-nop-ho-so" />
				</aui:col>
				
				<aui:col width="50" >
					<aui:input type="text" name="<%= DossierDisplayTerms.SUBMIT_DATETIME_TO %>" label="den-ngay" />
				</aui:col>
			</aui:row>
			
			<aui:button-row>
				<aui:button type="submit" name="search" value="search" />
				
				<c:if test="<%=DossierPermission.contains(permissionChecker, scopeGroupId, ActionKeys.ADD_DOSSIER) %>">
					
					<%
					// Trong truong hop cau hinh man hinh lua chon nop ho so se quay ra man hinh hien thi thu tuc hanh chinh de chon
					// Neu khong thi toi portlet hien thi danh sach dich vu cong mac dinh
					%>
					<c:choose>
						<c:when test="<%= serviceConfig != null %>">
							<portlet:renderURL var="addDossierURL">
								<portlet:param name="mvcPath" value="/html/portlets/dossiermgt/frontoffice/edit_dossier.jsp"/>
								<portlet:param name="<%=DossierDisplayTerms.SERVICE_CONFIG_ID %>" value="<%=String.valueOf(serviceConfig.getServiceConfigId()) %>"/>
								<portlet:param name="<%=Constants.CMD %>" value="<%=Constants.ADD %>"/>
								<portlet:param name="backURL" value="<%=currentURL %>"/>
								<portlet:param name="backURLFromList" value="<%=currentURL %>"/>
								<portlet:param name="isEditDossier" value="<%=String.valueOf(true) %>"/>
							</portlet:renderURL> 
					 		<aui:button type="button" icon="icon-plus" href="<%=addDossierURL.toString() %>" cssClass="action-button" value="select-service-info"></aui:button>
						</c:when>
					</c:choose>
				</c:if>
			</aui:button-row>
		</aui:col>
	</aui:row>
	
	<aui:row>
		<aui:col width="100" >
			<div class="opencps-searchcontainer-wrapper default-box-shadow radius8">
				<liferay-ui:search-container searchContainer="<%= new DossierSearch(renderRequest, iteratorURL, headerNames, null) %>">
				
					<liferay-ui:search-container-results>
						<%@include file="/html/portlets/dossiermgt/frontoffice/dossier_search_results_index.jspf" %>
					</liferay-ui:search-container-results>	
						<liferay-ui:search-container-row 
							className="org.opencps.dossiermgt.model.Dossier" 
							escapedModel="<%= true %>"
							modelVar="dossier" 
							keyProperty="dossierId"
						>
						
						<%
							String cssStatusColor = "status-color-" + dossier.getDossierStatus();
							
							List<DossierLog> dossierLogs = new ArrayList<DossierLog>();
							
							String noteContent = StringPool.BLANK;
							
							try {
								dossierLogs = DossierLogLocalServiceUtil.findDossierLog(1, dossier.getDossierId(), 0, 1);
								if(dossierLogs.size() > 0) {
									noteContent = DossierMgtUtil.getDossierLogs(StringPool.BLANK, dossierLogs.get(0).getMessageInfo()).replaceAll("update-version-file", LanguageUtil.get(pageContext, "update-version-file"));
								}
							} catch(Exception e) {
								_log.error(e);
							}
							
							String serviceName = serviceInfo != null ? serviceInfo.getServiceName() : StringPool.BLANK;
						%>
						<liferay-util:buffer var="col1">
							<div class="dossier-list-stt">
								<%=(searchContainer.getCur() - 1) * searchContainer.getDelta() + index + 1 %>
							</div>
						</liferay-util:buffer>
						
						<liferay-util:buffer var="col2">
							<%=dossier.getReceptionNo() %>
						</liferay-util:buffer>
						
						<liferay-util:buffer var="col4">
							<div class="dossier-list-service-name">
								<%=serviceName %>
							</div>
						</liferay-util:buffer>
						
						<liferay-util:buffer var="col5">
							<div class="dossier-list-col-date">
								<div class="row-fluid"> 
										<%=
											Validator.isNotNull(dossier.getSubmitDatetime()) ? 
											DateTimeUtil.convertDateToString(dossier.getSubmitDatetime(), DateTimeUtil._VN_DATE_TIME_FORMAT) : 
											StringPool.DASH 
										%>
									</div>
								</div>
							</div>
						</liferay-util:buffer>
						
						<liferay-util:buffer var="col6">
							<div class="row-fluid dossier-list-note">
								<liferay-ui:message key="<%= noteContent  %>"/>
							</div>
						</liferay-util:buffer>
							
						<%
							row.setClassName("opencps-searchcontainer-row " + cssStatusColor);
							row.addText(col1);
							row.addText(col2);
							row.addText(col4);
							row.addText(col5);
							row.addText(col6);
							row.addJSP("center", SearchEntry.DEFAULT_VALIGN,"/html/portlets/dossiermgt/frontoffice/display/dossierlist/dossier_actions_dklr_v10.jsp", 
										config.getServletContext(), request, response);
							
						%>	
						</liferay-ui:search-container-row> 
					
					<liferay-ui:search-iterator type="opencs_page_iterator"/>
					
				</liferay-ui:search-container>
			</div>
			
		</aui:col>
	</aui:row>
</aui:form>

 <aui:script>
AUI().use('aui-datepicker', function(A) {
	new A.DatePicker({
		trigger : '#<%= renderResponse.getNamespace() + DossierDisplayTerms.SUBMIT_DATETIME_FROM %>',
		popover : {
			zIndex : 1
		},
		calendar: {
			dateFormat: '%d/%m/%y'
		}
	});
	
	new A.DatePicker({
		trigger : '#<%= renderResponse.getNamespace() + DossierDisplayTerms.SUBMIT_DATETIME_TO %>',
		popover : {
			zIndex : 1
		},
		calendar: {
			dateFormat: '%d/%m/%y'
		}
	});
});
</aui:script>

<%!
	private Log _log = LogFactoryUtil.getLog("html_portlets_dossiermgt_frontoffice_display_dklr_10_jsp");
%>