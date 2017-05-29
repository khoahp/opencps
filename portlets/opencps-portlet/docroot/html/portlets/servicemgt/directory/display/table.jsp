
<%@page import="org.opencps.dossiermgt.NoSuchServiceConfigException"%>
<%@page import="org.opencps.dossiermgt.service.ServiceConfigLocalServiceUtil"%>
<%@page import="org.opencps.dossiermgt.model.ServiceConfig"%>
<%@page import="javax.portlet.PortletRequest"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayPortletMode"%>
<%@page import="com.liferay.portlet.PortletURLFactoryUtil"%>
<%@page import="org.opencps.util.PortletConstants"%>
<%@page import="org.opencps.util.PortletPropsValues"%>
<%@page import="org.opencps.processmgt.util.ProcessOrderUtils"%>
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
<%@page import="org.opencps.util.DictItemUtil"%>
<%@page import="org.opencps.servicemgt.service.ServiceInfoLocalServiceUtil"%>
<%@page import="org.opencps.servicemgt.search.ServiceSearch"%>
<%@page import="org.opencps.servicemgt.search.ServiceSearchTerms"%>
<%@page import="com.liferay.portal.kernel.log.Log"%>
<%@ include file="../init.jsp" %>


<%
	int serviceLevel  = ParamUtil.getInteger(request, ServiceDisplayTerms.SERVICE_LEVEL,0);
	
	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter("mvcPath", templatePath + "serviceinfodirectorylist.jsp");
	
	List<String> headerNames = new ArrayList<String>();
	
	headerNames.add("row-index");
	headerNames.add("service-name");
	headerNames.add("service-domain");
	headerNames.add("service-administrator");
	headerNames.add("muc-do");
// 	headerNames.add("template-file");
	 
	String headers = StringUtil.merge(headerNames, StringPool.COMMA);
	
	
	PortletURL searchURL = renderResponse.createRenderURL();
	
	String administrationCode = ParamUtil.getString(request, ServiceDisplayTerms.SERVICE_ADMINISTRATION);
	
	String domainCode = ParamUtil.getString(request, ServiceDisplayTerms.SERVICE_DOMAINCODE);
	
	request.setAttribute(ServiceDisplayTerms.SERVICE_ADMINISTRATION, administrationCode);
	
	request.setAttribute(ServiceDisplayTerms.SERVICE_DOMAINCODE, domainCode);
	
	List<DictItem> dictItems = DictItemLocalServiceUtil.findDictItemsByG_DC_S(scopeGroupId, ServiceUtil.SERVICE_DOMAIN);
	
	String myComboTree = ProcessOrderUtils.generateComboboxTree(PortletPropsValues.DATAMGT_MASTERDATA_SERVICE_DOMAIN, PortletConstants.TREE_VIEW_ALL_ITEM, 
			PortletConstants.TREE_VIEW_LEVER_3, false, renderRequest);
	
	iteratorURL.setParameter(ServiceDisplayTerms.SERVICE_ADMINISTRATION, administrationCode);
	iteratorURL.setParameter(ServiceDisplayTerms.SERVICE_DOMAINCODE, domainCode);
	iteratorURL.setParameter("keywords", ParamUtil.getString(request, "keywords"));
	iteratorURL.setParameter(ServiceDisplayTerms.SERVICE_LEVEL, String.valueOf(serviceLevel));
%>

<aui:script use="aui-base,aui-io">
$(document).ready(function(){
	var myComboTree = '<%=myComboTree %>';
	var domainCode = '<%=HtmlUtil.escape(domainCode)%>';
	var comboboxTree = $('#comboboxTree').comboTree({  
		boundingBox: 'comboboxTree',
		name: '#<portlet:namespace /><%=ServiceDisplayTerms.SERVICE_DOMAINCODE %>',
		form: document.<portlet:namespace />fm,
		formSubmit: false,
		isMultiple: false,
	    source: JSON.parse(myComboTree)
	});

	comboboxTree.setValue(domainCode);
	
// 	$("#<portlet:namespace />administrationCode").change(function() {
// 		<portlet:namespace />onSelectSubmit();
// 	});
// 	Liferay.provide(window, '<portlet:namespace/>onSelectSubmit', function() {
// 		var A = AUI();
		
// 		submitForm(document.<portlet:namespace />fm);
// 	});
});

</aui:script>

<aui:nav-bar cssClass="opencps-toolbar custom-toolbar">
	<aui:nav-bar-search cssClass="pull-right">
		<div class="form-search">
			<aui:form action="<%= searchURL %>" method="post" name="fm">
				<div class="toolbar_search_input">
					<aui:row>
						<aui:col width="25" cssClass="search-col">
							<datamgt:ddr
								cssClass="search-input select-box"
								depthLevel="1" 
								dictCollectionCode="SERVICE_ADMINISTRATION"
								itemNames="<%= ServiceDisplayTerms.SERVICE_ADMINISTRATION %>"
								itemsEmptyOption="true"
								selectedItems="<%= administrationCode %>"
								emptyOptionLabels="<%=ServiceDisplayTerms.SERVICE_ADMINISTRATION %>"
								showLabel="false"
							>
							</datamgt:ddr>

						</aui:col>
						<aui:col width="25" cssClass="search-col">
							<aui:input name="<%=ServiceDisplayTerms.SERVICE_DOMAINCODE %>" type="hidden" value="<%=domainCode %>"></aui:input>
							<input type="text" id="comboboxTree" class="opencps-combotree" readonly="readonly" />
						</aui:col>
						<aui:col width="25" cssClass="search-col">
							<aui:select name="<%=ServiceDisplayTerms.SERVICE_LEVEL %>" label="">
								<aui:option value="0"><liferay-ui:message key="muc-do"/></aui:option>
								<aui:option value="2" selected='<%=serviceLevel == 2 %>'><liferay-ui:message key="muc-do" /> 2</aui:option>
								<aui:option value="3" selected='<%=serviceLevel == 3 %>'><liferay-ui:message key="muc-do" /> 3</aui:option>
								<aui:option value="4" selected='<%=serviceLevel == 4 %>'><liferay-ui:message key="muc-do" /> 4</aui:option>
							</aui:select>
						</aui:col>
						<aui:col width="25" cssClass="search-col">
							<liferay-ui:input-search 
								cssClass="search-input input-keyword"
								id="keywords1"
								name="keywords"
								title='<%= LanguageUtil.get(locale, "keywords") %>'
								placeholder='<%= LanguageUtil.get(portletConfig, locale, "put-keyword") %>' 
							/>
						</aui:col>
					</aui:row>
				</div>
			</aui:form>
		</div>
	</aui:nav-bar-search>
</aui:nav-bar>


<div class="opencps-searchcontainer-wrapper">
	<h3 style="text-transform: uppercase;"><b><liferay-ui:message key="danh-sach-thu-tuc-hanh-chinh"/></b></h3>
	<liferay-ui:search-container searchContainer="<%= new ServiceSearch(renderRequest, SearchContainer.DEFAULT_DELTA, iteratorURL) %>" 
		headerNames="<%= headers %>">
			
		<liferay-ui:search-container-results>
			<%
				ServiceSearchTerms searchTerms = (ServiceSearchTerms) searchContainer.getSearchTerms();
		
				total = ServiceInfoLocalServiceUtil.countServiceActive(
						scopeGroupId, searchTerms.getKeywords(),
						searchTerms.getAdministrationCode(),
						searchTerms.getDomainCode(),
						searchTerms.getServiceLevel());

				results = ServiceInfoLocalServiceUtil.searchServiceActive(
						scopeGroupId, searchTerms.getKeywords(),
						searchTerms.getAdministrationCode(),
						searchTerms.getDomainCode(),
						searchTerms.getServiceLevel(),
						searchContainer.getStart(),
						searchContainer.getEnd());

				pageContext.setAttribute("results", results);
				pageContext.setAttribute("total", total);
			%>
			
		</liferay-ui:search-container-results>
	
		<liferay-ui:search-container-row 
			className="org.opencps.servicemgt.model.ServiceInfo" 
			modelVar="service" 
			keyProperty="serviceinfoId"
			indexVar="index"
		>
			<%
				PortletURL viewURL = renderResponse.createRenderURL();
				viewURL.setParameter("mvcPath", templatePath + "service_detail.jsp");
				viewURL.setParameter("serviceinfoId", String.valueOf(service.getServiceinfoId()));
				viewURL.setParameter("backURL", currentURL);
				
				PortletURL renderToSubmitOnline = PortletURLFactoryUtil.create(request, WebKeys.P26_SUBMIT_ONLINE, plidServiceDetail, PortletRequest.RENDER_PHASE);
				renderToSubmitOnline.setWindowState(LiferayWindowState.NORMAL);
				renderToSubmitOnline.setPortletMode(LiferayPortletMode.VIEW);
				renderToSubmitOnline.setParameter("mvcPath", "/html/portlets/dossiermgt/submit/dossier_submit_online.jsp");
				renderToSubmitOnline.setParameter("serviceinfoId", String.valueOf(service.getServiceinfoId()));
				renderToSubmitOnline.setParameter("backURL", currentURL);
			%>
				<liferay-util:buffer var="boundcol1">
					
					<div class="row-fluid service-directory-service-info">
						<div class="span12">
							<a href="<%=viewURL.toString() %>"><%=service.getServiceName() %></a>
						</div>
						
						<c:if test="<%= showListServiceTemplateFile %>">
							<div class="span12">
								<%
									List<TemplateFile> templates = new ArrayList<TemplateFile>();
									
									String iconType = StringPool.BLANK;
									
								
									if (Validator.isNotNull(service)) {
										templates = TemplateFileLocalServiceUtil.getServiceTemplateFiles(service.getServiceinfoId());
									}
								%>
								<ul class="ls-file-download">
									<%
										for (TemplateFile tf : templates) {
									%>
										<li> <i class="icon-file"></i> <a href="<%= ServiceUtil.getDLFileURL(tf.getFileEntryId()) %>"> <%= tf.getFileName() %> </a></li>
									<%		
										}
									%>
								</ul>
							</div>
						</c:if>
					</div>
				</liferay-util:buffer>
				
				<liferay-util:buffer var="boundcol2">
					<div class="row-fluid service-directory-service-info">
						<div class="span12">
							<span><%=DictItemUtil.getNameDictItem(service.getDomainCode())%></span>
						</div>
					</div>
				</liferay-util:buffer>
				
				<liferay-util:buffer var="boundcol3">
					<div class="row-fluid service-directory-service-info">
						<div class="span12">
							<span><%=DictItemUtil.getNameDictItem(service.getAdministrationCode())%></span>
						</div>
					</div>
				</liferay-util:buffer>
				
				<liferay-util:buffer var="boundcol4">
					<div class="row-fluid service-directory-service-level">
						<div class="span12">
						<%
						int serviceLevelDisplay = 2;

						List<ServiceConfig> serviceConfigs = ServiceConfigLocalServiceUtil
								.getServiceConfigsByS_G(
										service.getServiceinfoId(),
										scopeGroupId);

						if (serviceConfigs != null && serviceConfigs.size() > 0) {
							serviceLevelDisplay = serviceConfigs.get(0)
									.getServiceLevel();
						}
						%>
						<%=serviceLevelDisplay%>
					</div>
					</div>
				</liferay-util:buffer>
				
<%-- 				<liferay-util:buffer var="boundcol5"> --%>
<!-- 					<div class="row-fluid service-directory-action"> -->
<!-- 						<div class="span12"> -->
<%-- 							<aui:button href="<%= viewURL.toString() %>" cssClass="des-sub-button radius20" value="service-description"></aui:button> --%>
							
<%-- 							<aui:button href="<%= renderToSubmitOnline.toString() %>" cssClass="des-sub-button radius20" value="dossier-submit-online-temp"></aui:button> --%>
<!-- 						</div> -->
<!-- 					</div> -->
<%-- 				</liferay-util:buffer> --%>
			<%
				if(service.getActiveStatus() !=0) {
					row.setClassName("opencps-searchcontainer-row service-directory-display-table-row");
					
					// no column
					row.addText(String.valueOf((searchContainer.getCur() - 1) * searchContainer.getDelta() + index + 1), viewURL);
					row.addText(boundcol1);
					row.addText(boundcol2); 
					row.addText(boundcol3); 
					row.addText(boundcol4); 
// 					row.addText(boundcol5); 
				}
			%>	
		
		</liferay-ui:search-container-row>	
	
		<liferay-ui:search-iterator type="opencs_page_iterator"/>
	
	</liferay-ui:search-container>
</div>
<%!
	private Log _log = LogFactoryUtil.getLog("html.portlets.servicemgt.directory.serviceinfo.jsp");
%>

