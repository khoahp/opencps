<%@page import="com.liferay.portal.kernel.util.Constants"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayPortletMode"%>
<%@page import="javax.portlet.PortletRequest"%>
<%@page import="com.liferay.portlet.PortletURLFactoryUtil"%>
<%@page import="org.opencps.util.PortletPropsKeys"%>
<%@page import="org.opencps.util.DataMgtUtils"%>
<%@page import="org.opencps.servicemgt.model.ServiceInfo"%>
<%@page import="java.util.List"%>
<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@page import="org.opencps.dossiermgt.search.ServiceDisplayTerms"%>
<%@page import="org.opencps.servicemgt.search.ServiceSearchTerms"%>
<%@page import="org.opencps.servicemgt.search.ServiceSearch"%>
<%@page import="javax.portlet.PortletURL"%>
<%@page import="org.opencps.servicemgt.service.ServiceInfoLocalServiceUtil"%>
<%@ include file="../../../init.jsp"%>
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
<%
	String govAgencyCode = ParamUtil.getString(request, ServiceDisplayTerms.GOVAGENCY_CODE,StringPool.BLANK);

	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter(ServiceDisplayTerms.GOVAGENCY_CODE, govAgencyCode);
	iteratorURL.setParameter("keywords", ParamUtil.getString(request, "keywords"));
	
	PortletURL searchURL = renderResponse.createRenderURL();
	
	
%>

<aui:nav-bar cssClass="opencps-toolbar custom-toolbar">
	<aui:nav-bar-search cssClass="pull-right">
		<div class="form-search">
			<aui:form action="<%= searchURL %>" method="post" name="fm">
				<div class="toolbar_search_input">
					<aui:row>
						
						<aui:col width="100" cssClass="search-col">
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


<div class="submit-dossier">
	<liferay-ui:search-container searchContainer="<%= new ServiceSearch(renderRequest, SearchContainer.DEFAULT_DELTA, iteratorURL) %>" 
		>
			
		
			<%
				ServiceSearchTerms searchTerms = (ServiceSearchTerms) searchContainer.getSearchTerms();
				
				List<String> domainCodeList = ServiceInfoLocalServiceUtil.getDomainCodes(themeDisplay.getScopeGroupId(), searchTerms.getKeywords(), searchTerms.getGovAgencyCode());
				
				PortletURL renderToSubmitOnline = PortletURLFactoryUtil.create(request, WebKeys.DOSSIER_MGT_PORTLET, plIdAddDossier, PortletRequest.RENDER_PHASE);
 				renderToSubmitOnline.setWindowState(LiferayWindowState.NORMAL);
				renderToSubmitOnline.setPortletMode(LiferayPortletMode.VIEW);
				renderToSubmitOnline.setParameter("mvcPath", "/html/portlets/dossiermgt/frontoffice/edit_dossier.jsp");
				renderToSubmitOnline.setParameter("isEditDossier", String.valueOf(true));
 				renderToSubmitOnline.setParameter("backURL", currentURL);
 				renderToSubmitOnline.setParameter(Constants.CMD, Constants.ADD);
			%>
			
			<liferay-ui:panel-container 
				extended="<%= true %>" 
				persistState="<%= true %>"
			>
			
				<%for(int i =0;i<domainCodeList.size();i++){ 
					String domainCode = domainCodeList.get(i);
				%>
					<liferay-ui:panel 
						collapsible="<%= true %>" 
						extended="<%= true %>" 
						persistState="<%= true %>" 
						title="<%=DataMgtUtils.getDictItemName(themeDisplay.getScopeGroupId(), Long.valueOf(domainCode), themeDisplay.getLocale()) %>"
					>
						<%
							List<ServiceInfo> serviceInfos = ServiceInfoLocalServiceUtil.getServiceInfoByDomainCode(domainCode);
						
							for(ServiceInfo serviceInfoSub :serviceInfos){
								
								renderToSubmitOnline.setParameter(ServiceDisplayTerms.GOVAGENCY_CODE, govAgencyCode);
						%>
							<aui:row>
								<a href="<%=renderToSubmitOnline.toString()%>"><%=serviceInfoSub.getServiceName() %></a>
							</aui:row>
						<%
							}
						%>
					</liferay-ui:panel>
				<%} %>
			</liferay-ui:panel-container>

	</liferay-ui:search-container>
</div>