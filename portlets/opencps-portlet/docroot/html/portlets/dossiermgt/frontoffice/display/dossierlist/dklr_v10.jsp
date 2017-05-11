
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
	String dklr_v10_govAgencyCode = preferences.getValue("dklr_v10_govAgencyCode", StringPool.BLANK);
	String dklr_v10_administrationCode = preferences.getValue("dklr_v10_administrationCode", StringPool.BLANK);
	String dklr_v10_dynamicFormKeyPattern = preferences.getValue("dklr_v10_dynamicFormKeyPattern", StringPool.BLANK);	//vehicleClass#VEHICLE_CLASS#doi-tuong
	
	String[] dynamicFormKeyPatterns = Validator.isNotNull(dklr_v10_dynamicFormKeyPattern) ? StringUtil.split(dklr_v10_dynamicFormKeyPattern, StringPool.DOLLAR) : new String[0];	// cat thanh mang cac key trong dynamicform

	String receptionNo = ParamUtil.getString(request, DossierDisplayTerms.RECEPTION_NO);
	String serviceInfoNo = ParamUtil.getString(request, DossierDisplayTerms.SERVICE_INFO_NO);
	String dossierStatus = ParamUtil.getString(request, DossierDisplayTerms.DOSSIER_STATUS);
	String submitDateTimeFrom = ParamUtil.getString(request, DossierDisplayTerms.SUBMIT_DATETIME_FROM);
	String submitDateTimeTo = ParamUtil.getString(request, DossierDisplayTerms.SUBMIT_DATETIME_TO);
	
	Map<String, String> mapDynamicFormSelects = new HashMap<String, String>();	// Luu gia tri chon tuong ung voi key
	Map<String, String> mapDynamicFormValues = new HashMap<String, String>();	// Luu gia tri tu index cua ho so tuong ung voi key
	
	for (String keyPatterns : dynamicFormKeyPatterns) {
		String[] keys = StringUtil.split(keyPatterns, StringPool.POUND);
		
		String val = ParamUtil.getString(request, keys[0]);
		
		mapDynamicFormSelects.put(keys[0], val);
	}
	
	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter("mvcPath", templatePath + "frontofficedossierlist.jsp");
	iteratorURL.setParameter("tabs1", DossierMgtUtil.TOP_TABS_DOSSIER);
	iteratorURL.setParameter("receptionNo", receptionNo);
	iteratorURL.setParameter("serviceInfoNo", serviceInfoNo);
	iteratorURL.setParameter("dossierStatus", dossierStatus);
	iteratorURL.setParameter("submitDateTimeFrom", submitDateTimeFrom);
	iteratorURL.setParameter("submitDateTimeTo", submitDateTimeTo);
	for (Map.Entry<String, String> entry : mapDynamicFormSelects.entrySet()) {
		iteratorURL.setParameter(entry.getKey(), entry.getValue());
	}
	
	List<Dossier> dossiers =  new ArrayList<Dossier>();
	
	int totalCount = 0;

	List<String> headerNames = new ArrayList<String>();
	headerNames.add("stt");
	headerNames.add("reception-no");
	for (String keyPatterns : dynamicFormKeyPatterns) {
		String[] keys = StringUtil.split(keyPatterns, StringPool.POUND);
		
		String keyLabel = keys[0];
		if(keys.length > 2) {
			keyLabel = keys[2];
		}
		
		headerNames.add(keyLabel);
	}
	headerNames.add("service-info-name");
	headerNames.add("submit-date-time");
	headerNames.add("note");
	headerNames.add("action");
	
	List<DictItem> listDossierStatus = DictItemLocalServiceUtil.findDictItemsByG_DC_S(scopeGroupId, "DOSSIER_STATUS");
	
	List<ServiceInfo> listServiceInfo = ServiceInfoLocalServiceUtil.searchService(scopeGroupId, StringPool.BLANK, 
			dklr_v10_administrationCode, StringPool.BLANK, null, -1, -1);
	
	Map<String, String> keywordIndexs = new HashMap<String, String>();
	keywordIndexs.put("govAgencyCode", dklr_v10_govAgencyCode);
	keywordIndexs.put(DossierDisplayTerms.RECEPTION_NO, receptionNo);
	keywordIndexs.put(DossierDisplayTerms.SERVICE_INFO_NO, serviceInfoNo);
	keywordIndexs.put(DossierDisplayTerms.DOSSIER_STATUS, dossierStatus);
	for (Map.Entry<String, String> entry : mapDynamicFormSelects.entrySet()) {
		keywordIndexs.put(entry.getKey(), entry.getValue());
	}
%>

<liferay-portlet:renderURL varImpl="searchURL">
	<portlet:param name="mvcPath" value='<%= templatePath + "frontofficedossierlist.jsp" %>' />
	<portlet:param name="tabs1" value='<%= DossierMgtUtil.TOP_TABS_DOSSIER %>' />
</liferay-portlet:renderURL>

<aui:form action="<%= searchURL %>" method="get" name="fm">
	<liferay-portlet:renderURLParams varImpl="searchURL" />
	
	<aui:row>
		<aui:col width="100">
			<h4><liferay-ui:message key="tim-kiem-ho-so" /></h4>
			
			<aui:row>
				<aui:col width="100" >
					<aui:input type="text" name="<%= DossierDisplayTerms.RECEPTION_NO %>" value="" label="so-dkkt"/>
				</aui:col>
			</aui:row>
			
			<aui:row>
				<aui:col width="100" >
					<aui:select name="<%= DossierDisplayTerms.SERVICE_INFO_NO %>" label="service-info" showEmptyOption="<%= true %>">
						<% for(ServiceInfo serviceInfoTmp : listServiceInfo) { %>
							<aui:option value="<%= serviceInfoTmp.getServiceNo() %>" label="<%= serviceInfoTmp.getServiceName() %>" />
						<% } %>
					</aui:select>
				</aui:col>
			</aui:row>
			
			<aui:row>
				<aui:col width="50" >
					<%
					for (String keyPatterns : dynamicFormKeyPatterns) {
						String[] keys = StringUtil.split(keyPatterns, StringPool.POUND);
						
						String key = keys[0];
						String keyValue = mapDynamicFormSelects.get(key);
						
						String keyLabel = key;
						if(keys.length > 2) {
							keyLabel = keys[2];
						}
						
						String keyDictCollectionCode = null;
						
						if(keys.length > 1) {
							keyDictCollectionCode = keys[1];
						}
					%>
						
						<c:choose>
							<c:when test="<%= Validator.isNotNull(keyDictCollectionCode) %>">
								<!-- combobox -->
								<%
								List<DictItem> listDynamicKeyItem = DictItemLocalServiceUtil.findDictItemsByG_DC_S(scopeGroupId, keyDictCollectionCode);
								%>
								<aui:select name="<%= key %>" label="<%= keyLabel %>" showEmptyOption="<%= true %>">
									<% for(DictItem itemTmp : listDynamicKeyItem) { %>
										<aui:option selected='<%= itemTmp.getItemCode().equalsIgnoreCase(keyValue) %>' value="<%= itemTmp.getItemCode() %>" label="<%= itemTmp.getItemName(themeDisplay.getLocale()) %>" />
									<% } %>
								</aui:select>
							</c:when>
							
							<c:otherwise>
								<!-- input text -->
								<aui:input type="text" name="<%= key %>" label="<%= keyLabel %>" value="<%= keyValue %>" />
							</c:otherwise>
						</c:choose>
					<%
					}
					%>
					
				</aui:col>
				
				<aui:col width="50" >
					<aui:select name="<%= DossierDisplayTerms.DOSSIER_STATUS %>" showEmptyOption="<%= true %>">
						<% for(DictItem itemTmp : listDossierStatus) { %>
							<aui:option value="<%= itemTmp.getItemCode() %>" label="<%= itemTmp.getItemName(themeDisplay.getLocale()) %>" />
						<% } %>
					</aui:select>
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
						<c:when test="<%= redirectAddDossierPlid > 0 %>">
							<liferay-portlet:renderURL var="addDossierURL" portletName="<%= WebKeys.SERVICE_MGT_DIRECTORY %>"  plid="<%= redirectAddDossierPlid %>"
								windowState="<%=LiferayWindowState.NORMAL.toString() %>">
								<liferay-portlet:param name="backURL" value="<%=currentURL %>"/>
							</liferay-portlet:renderURL>
							<aui:button icon="icon-plus" href="<%=addDossierURL %>" cssClass="action-button" value="select-service-info"/>
						</c:when>
						<c:otherwise>
							<portlet:renderURL var="addDossierURL" windowState="<%=LiferayWindowState.NORMAL.toString() %>">
								<portlet:param name="isListServiceConfig" value="<%=String.valueOf(true) %>"/>
								<portlet:param name="tabs1" value="<%=DossierMgtUtil.TOP_TABS_DOSSIER %>"/>
								<portlet:param name="backURL" value="<%=currentURL %>"/>
								<%
									if(Validator.isNotNull(itemCode_cfg) && itemCode_cfg.length() > 0){
										
									DictItem dictItem_cfg = DictItemLocalServiceUtil.getDictItemInuseByItemCode(themeDisplay.getScopeGroupId(), PortletPropsValues.DATAMGT_MASTERDATA_SERVICE_DOMAIN, itemCode_cfg);
										
									if(Validator.isNotNull(dictItem_cfg)){
									
								%>
									<portlet:param name="mvcPath" value="<%=templatePath + \"display/20_80_servicelist_04.jsp\" %>"/>
									<portlet:param name="serviceDomainId" value="<%=String.valueOf(dictItem_cfg.getDictItemId()) %>"/>
									<portlet:param name="dictItemCode" value="<%=dictItem_cfg.getItemCode() %>"/>
								<%
									}
									}else{
								%>		
									<portlet:param name="mvcPath" value="/html/portlets/dossiermgt/frontoffice/frontofficeservicelist.jsp"/>
								<%
									}
								%>
								<portlet:param name="backURLFromList" value="<%=currentURL %>"/>
							</portlet:renderURL>
							
							<aui:button icon="icon-plus" href="<%=addDossierURL %>" cssClass="action-button" value="select-service-info"/>
						</c:otherwise>
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
							
							ServiceInfo serviceInfo = null;
							
							if(dossier.getServiceInfoId() > 0) {
								try {
									serviceInfo = ServiceInfoLocalServiceUtil.getServiceInfo(dossier.getServiceInfoId());
								} catch(NoSuchServiceInfoException e) {
								}
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
							
							for (String keyPatterns : dynamicFormKeyPatterns) {
								String[] keys = StringUtil.split(keyPatterns, StringPool.POUND);
								
								row.addText(mapDynamicFormValues.get(dossier.getDossierId() + StringPool.UNDERLINE + keys[0]));
							}
							
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