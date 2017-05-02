
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
	String dossierStatusCHKInit = ParamUtil.getString(request, DossierDisplayTerms.DOSSIER_STATUS, "-1");
	String dossierStatus = ParamUtil.getString(request, DossierDisplayTerms.DOSSIER_STATUS, StringPool.BLANK);
	int itemsToDisplay_cfg = GetterUtil.getInteger(portletPreferences.getValue("itemsToDisplay", "2"));
	
	String dossierType = ParamUtil.getString(request, "dossierType", "");
	String vehicleClass = ParamUtil.getString(request, "vehicleClass", "");
	
	long serviceDomainId = ParamUtil.getLong(request, "serviceDomainId");

	String serviceDomainIndex_cfg = StringPool.BLANK;
	
	if(Validator.isNotNull(itemCode_cfg)){
		DictItem dictItem_cfg = DictItemLocalServiceUtil.getDictItemInuseByItemCode(themeDisplay.getScopeGroupId(), PortletPropsValues.DATAMGT_MASTERDATA_SERVICE_DOMAIN, itemCode_cfg);
		
		if(Validator.isNotNull(dictItem_cfg)){
			serviceDomainId = dictItem_cfg.getDictItemId();
			serviceDomainIndex_cfg = dictItem_cfg.getTreeIndex();
		}
		
	}
	
	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter("mvcPath", templatePath + "frontofficedossierlist.jsp");
	iteratorURL.setParameter("tabs1", DossierMgtUtil.TOP_TABS_DOSSIER);
	iteratorURL.setParameter(DossierDisplayTerms.DOSSIER_STATUS, String.valueOf(dossierStatus));
	iteratorURL.setParameter("serviceDomainId", (serviceDomainId > 0) ? String.valueOf(serviceDomainId):StringPool.BLANK);
	
	List<Dossier> dossiers =  new ArrayList<Dossier>();
	
	int totalCount = 0;

	JSONObject arrayParam = JSONFactoryUtil
		    .createJSONObject();
	arrayParam.put(DossierDisplayTerms.SERVICE_DOMAIN_ID, (serviceDomainId > 0) ? String.valueOf(serviceDomainId):StringPool.BLANK);
	arrayParam.put(DossierDisplayTerms.DOSSIER_STATUS, String.valueOf(dossierStatus));
	
	List<String> headerNames = new ArrayList<String>();
	headerNames.add("stt");
	headerNames.add("reception-no");
	headerNames.add("doi-tuong");
	headerNames.add("service-info-name");
	headerNames.add("submit-date-time");
	headerNames.add("note");
	headerNames.add("action");
	
%>

<aui:form action="" method="post" name="fm">
	<aui:row>
		<aui:col width="100">
			<h4><liferay-ui:message key="tim-kiem-ho-so" /></h4>
			
			<aui:row>
				<aui:col width="100" >
					<aui:input type="text" name="receptionNo" value="" label="so-dkkt"/>
				</aui:col>
			</aui:row>
			
			<aui:row>
				<aui:col width="100" >
					<aui:select name="serviceInfoNo" label="service-info" showEmptyOption="<%= true %>">
					</aui:select>
				</aui:col>
			</aui:row>
			
			<aui:row>
				<aui:col width="50" >
					<aui:select name="vehicleClass" label="doi-tuong" showEmptyOption="<%= true %>">
					</aui:select>
				</aui:col>
				
				<aui:col width="50" >
					<aui:select name="dossierStatus" showEmptyOption="<%= true %>">
					</aui:select>
				</aui:col>
			</aui:row>
			
			<aui:row>
				<aui:col width="50" >
					<aui:input type="date" name="submitDateTimeFrom" label="ngay-nop-ho-so" />
				</aui:col>
				
				<aui:col width="50" >
					<aui:input type="date" name="submitDateTimeTo" label="den-ngay" />
				</aui:col>
			</aui:row>
			
			<aui:button-row>
				<aui:button type="submit" name="search" value="search" />
				
				<aui:button type="button" name="add-new" value="add-new" />
			</aui:button-row>
		</aui:col>
	</aui:row>
	
	<aui:row>
		<aui:col width="100" >
			<div class="opencps-searchcontainer-wrapper default-box-shadow radius8">
				<div class="opcs-serviceinfo-list-label">
					<div class="title_box">
						<p class="file_manage_title ds"><liferay-ui:message key="title-danh-sach-ho-so" /></p>
						<p class="count"></p>
					</div>
				</div>
				
				<liferay-ui:search-container searchContainer="<%= new DossierSearch(renderRequest, SearchContainer.DEFAULT_DELTA, iteratorURL, headerNames) %>">
				
					<liferay-ui:search-container-results>
						<%
							DossierSearchTerms searchTerms = (DossierSearchTerms)searchContainer.getSearchTerms();
							
							searchTerms.setDossierStatus(dossierStatus);
							
							DictItem domainItem = null;
						
	
							try{
								if(serviceDomainId > 0){
									domainItem = DictItemLocalServiceUtil.getDictItem(serviceDomainId);
								}
				
								if(domainItem != null){
									searchTerms.setServiceDomainIndex(domainItem.getTreeIndex());
								}
								
								%>
										<%@include file="/html/portlets/dossiermgt/frontoffice/dosier_search_results.jspf" %>
								<%
							}catch(Exception e){
								_log.error(e);
							}
						
							total = totalCount;
							results = dossiers;
							
							pageContext.setAttribute("results", results);
							pageContext.setAttribute("total", total);
						%>
					</liferay-ui:search-container-results>	
						<liferay-ui:search-container-row 
							className="org.opencps.dossiermgt.bean.DossierBean" 
							modelVar="dossierBean" 
							keyProperty="dossierId"
						>
						
						<%
							Dossier dossier = dossierBean.getDossier();
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
						%>
						<liferay-util:buffer var="col1">
							<div class="dossier-list-stt">
								<%=(searchContainer.getCur() - 1) * searchContainer.getDelta() + index + 1 %>
							</div>
						</liferay-util:buffer>
						
						<liferay-util:buffer var="col2">
							<%=dossier.getReceptionNo() %>
						</liferay-util:buffer>
						
						<liferay-util:buffer var="col2">
							<liferay-ui:message key="<%=vehicleClass %>"/>
						</liferay-util:buffer>
						
						<liferay-util:buffer var="col4">
							<div class="dossier-list-service-name">
								<%=dossierBean.getServiceName() %>
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
							row.addText(col3);
							row.addText(col4);
							row.addText(col5);
							row.addText(col6);
							row.addJSP("center", SearchEntry.DEFAULT_VALIGN,"/html/portlets/dossiermgt/frontoffice/dossier_actions.jsp", 
										config.getServletContext(), request, response);
							
						%>	
						</liferay-ui:search-container-row> 
					
					<liferay-ui:search-iterator type="opencs_page_iterator"/>
					
				</liferay-ui:search-container>
			</div>
			
		</aui:col>
	</aui:row>
</aui:form>

<%!
	private Log _log = LogFactoryUtil.getLog("html_portlets_dossiermgt_frontoffice_display_dklr_10_jsp");
%>