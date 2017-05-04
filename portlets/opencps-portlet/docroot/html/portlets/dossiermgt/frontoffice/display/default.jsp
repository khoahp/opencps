
<%@page import="org.opencps.util.WebKeys"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
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

<%@page import="com.liferay.portal.kernel.servlet.SessionMessages"%>
<%@page import="com.liferay.portal.kernel.dao.search.SearchEntry"%>
<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@page import="com.liferay.portal.kernel.log.Log"%>
<%@page import="com.liferay.portal.kernel.log.LogFactoryUtil"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="javax.portlet.PortletURL"%>
<%@page import="org.opencps.datamgt.model.DictItem"%>
<%@page import="org.opencps.datamgt.service.DictItemLocalServiceUtil"%>
<%@page import="org.opencps.dossiermgt.bean.DossierBean"%>
<%@page import="org.opencps.dossiermgt.model.Dossier"%>
<%@page import="org.opencps.dossiermgt.NoSuchDossierException"%>
<%@page import="org.opencps.dossiermgt.NoSuchDossierTemplateException"%>
<%@page import="org.opencps.dossiermgt.RequiredDossierPartException"%>
<%@page import="org.opencps.dossiermgt.search.DossierDisplayTerms"%>
<%@page import="org.opencps.dossiermgt.search.DossierSearch"%>
<%@page import="org.opencps.dossiermgt.search.DossierSearchTerms"%>
<%@page import="org.opencps.dossiermgt.service.DossierLocalServiceUtil"%>
<%@page import="org.opencps.dossiermgt.util.DossierMgtUtil"%>
<%@page import="org.opencps.servicemgt.model.ServiceInfo"%>
<%@page import="org.opencps.servicemgt.service.ServiceInfoLocalServiceUtil"%>
<%@page import="org.opencps.util.DateTimeUtil"%>
<%@page import="org.opencps.util.PortletUtil"%>
<%@page import="org.opencps.util.MessageKeys"%>
<%@page import="org.opencps.util.DictItemUtil"%>


<%@ include file="../../init.jsp"%>


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

<liferay-util:include page='<%=templatePath + "toolbar.jsp" %>' servletContext="<%=application %>" />

<%
	String dossierStatus = ParamUtil.getString(request, DossierDisplayTerms.DOSSIER_STATUS, StringPool.BLANK);

	int itemsToDisplay_cfg = GetterUtil.getInteger(portletPreferences.getValue("itemsToDisplay", "2"));
	
	int timeToReLoad_cfg = GetterUtil.getInteger(portletPreferences.getValue("timeToReLoad", "5"));
	
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
	
	try{
		
		dossiers = DossierLocalServiceUtil.getDossierByUserNewRequest(scopeGroupId, themeDisplay.getUserId(), 0, itemsToDisplay_cfg, 
			null);
		totalCount = dossiers.size();
	
	}catch(Exception e){
		_log.error(e);
		
	}
%>

<c:if test="<%=totalCount > 0 %>">
	<!-- cap nhat thay doi moi nhat -->
		<c:if test="<%= (Validator.isNull(keyword) && displayRecentlyResultWhenSearch) || displayRecentlyResultWhenSearch == false%>">
			<div class="opencps-searchcontainer-wrapper default-box-shadow radius8 mrb25 " id="<portlet:namespace />is-hidden">
				<div class="opcs-serviceinfo-list-label">
					<div class="title_box">
				           <p class="file_manage_title_new"><liferay-ui:message key="title-danh-sach-ho-so-thay-doi" /></p>
				           <p class="count"></p>
				    </div>
				</div>
				<liferay-ui:search-container searchContainer="<%= new DossierSearch(renderRequest, SearchContainer.DEFAULT_DELTA, iteratorURL) %>">
				
					<liferay-ui:search-container-results>
						<%
						
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
						%>
						
						<liferay-util:buffer var="info">
					
							<c:choose>
								<c:when test='<%=Validator.isNotNull(displayDossierNo) && displayDossierNo %>'>

									<!--hot fix moit  -->
									<%-- <div class="row-fluid">
										<div class='<%= "text-align-right span1 " + cssStatusColor%>'>
											<i class='<%="fa fa-circle sx10 " + dossier.getDossierStatus()%>'></i>
										</div>										
										<div class="span2 bold-label">
											<liferay-ui:message key="dossier-no"/>
										</div>
										
										<div class="span9"><%= dossier.getDossierId() %></div>
									</div>
									
									<div class="row-fluid">
										<div class="span1"></div>
										
										<div class="span2 bold-label">
											<liferay-ui:message key="reception-no"/>
										</div>
										
										<div class="span9"><%=dossier.getReceptionNo() %></div>
									</div> --%>
									
									<div class="row-fluid">
										<div class='<%= "text-align-right span1 " + cssStatusColor%>'>
											<i class='<%="fa fa-circle sx10 " + dossier.getDossierStatus()%>'></i>
										</div>
										<div class="span3 bold-label">
											<liferay-ui:message key="reception-no"/>
										</div>
										
										<div class="span3">
											<c:if test="<%= Validator.isNotNull(dossier.getReceptionNo()) %>">
												<%=dossier.getReceptionNo() %>
											</c:if>
											<c:if test="<%= Validator.isNull(dossier.getReceptionNo()) %>">
												<span style="font-style: italic;"><liferay-ui:message key="no-reception-no"/></span>
											</c:if>
										
										</div>
									</div>
								</c:when>
								
								<c:otherwise>
									<div class="row-fluid">
										<div class='<%= "text-align-right span1 " + cssStatusColor%>'>
											<i class='<%="fa fa-circle sx10 " + dossier.getDossierStatus()%>'></i>
										</div>
										<div class="span2 bold-label">
											<liferay-ui:message key="reception-no"/>
										</div>
										<div class="span9"><%=dossier.getReceptionNo() %></div>
									</div>
								</c:otherwise>
							</c:choose>
							
							
							<div class="row-fluid">
								<div class="span1"></div>
								
								<div class="span2 bold-label">
									<liferay-ui:message key="service-name"/>
								</div>
								
								<div class="span9"><%=dossierBean.getServiceName() %></div>
							</div>
							
							<div class="row-fluid">
								<div class="span1"></div>
								
								<div class="span2 bold-label"><liferay-ui:message key="gov-agency-name"/></div>
								
								<div class="span9"><%=dossier.getGovAgencyName() %></div>
							</div>
							
						</liferay-util:buffer>
						
						<liferay-util:buffer var="status">
							<div class="row-fluid">
								<div class="span5 bold-label"><liferay-ui:message key="create-date"/></div>
								<div class="span7">
									<%=
										Validator.isNotNull(dossier.getCreateDate()) ? 
										DateTimeUtil.convertDateToString(dossier.getCreateDate(), DateTimeUtil._VN_DATE_TIME_FORMAT) : 
										DateTimeUtil._EMPTY_DATE_TIME  
									%>
								</div>
							</div>
							
							<div class="row-fluid">
								<div class="span5 bold-label">
									 <liferay-ui:message key="receive-datetime"/>
								</div>
								
								<div class="span7">
									<%=
										Validator.isNotNull(dossier.getReceiveDatetime()) ? 
										DateTimeUtil.convertDateToString(dossier.getReceiveDatetime(), DateTimeUtil._VN_DATE_TIME_FORMAT): 
										DateTimeUtil._EMPTY_DATE_TIME  
									%>
								</div>
							</div>
							
							<div class="row-fluid">
							
								<div class="span5 bold-label">
									<liferay-ui:message key="finish-date"/>
								</div>
								<div class="span7">
									<%=
										Validator.isNotNull(dossier.getFinishDatetime()) ? 
										DateTimeUtil.convertDateToString(dossier.getFinishDatetime(), DateTimeUtil._VN_DATE_TIME_FORMAT): 
										StringPool.DASH 
									%>
								</div>
							</div>
							
							<div class="row-fluid">
								
								<div class="span5 bold-label">
									<liferay-ui:message key="dossier-status"/>
								</div>
								
								<div class='<%="span7 " + cssStatusColor %>'>
									<%=PortletUtil.getDossierStatusLabel(dossier.getDossierStatus(), locale) %>
								</div>
							</div>
						</liferay-util:buffer>
							
							<%
								row.setClassName("opencps-searchcontainer-row");
								row.addText(info);
								row.addText(status);
								row.addJSP("center", SearchEntry.DEFAULT_VALIGN,"/html/portlets/dossiermgt/frontoffice/dossier_actions.jsp", 
											config.getServletContext(), request, response);
								
							%>	
						</liferay-ui:search-container-row> 
					
					<liferay-ui:search-iterator paginate="false" />
					
				</liferay-ui:search-container>
			</div>
		</c:if>
	
	<!-- ket thuc tay doi moi nhat -->
</c:if>

<div class="opencps-searchcontainer-wrapper default-box-shadow radius8">

	<div class="opcs-serviceinfo-list-label">
		<div class="title_box">
	           <p class="file_manage_title ds"><liferay-ui:message key="title-danh-sach-ho-so" /></p>
	           <p class="count"></p>
	    </div>
	</div>
	
	<liferay-ui:search-container searchContainer="<%= new DossierSearch(renderRequest, SearchContainer.DEFAULT_DELTA, iteratorURL) %>">
	
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
			%>
			
			<liferay-util:buffer var="info">
				
				<c:choose>
					<c:when test='<%=Validator.isNotNull(displayDossierNo) && displayDossierNo %>'>
						<div class="row-fluid">
							<div class='<%= "text-align-right span1 " + cssStatusColor%>'>
								<i class='<%="fa fa-circle sx10 " + dossier.getDossierStatus()%>'></i>
							</div>
							<div class="span2 bold-label">
								<liferay-ui:message key="dossier-no"/>
							</div>
							
							<div class="span9"><%= dossier.getDossierId() %></div>
						</div>
						
						<div class="row-fluid">
							<div class="span1"></div>
							
							<div class="span2 bold-label">
								<liferay-ui:message key="reception-no"/>
							</div>
							
							<div class="span9"><%=dossier.getReceptionNo() %></div>
						</div>
					</c:when>
					
					<c:otherwise>
						<div class="row-fluid">
							<div class='<%= "text-align-right span1 " + cssStatusColor%>'>
								<i class='<%="fa fa-circle sx10 " + dossier.getDossierStatus()%>'></i>
							</div>
							<div class="span2 bold-label">
								<liferay-ui:message key="reception-no"/>
							</div>
							<div class="span9"><%=dossier.getReceptionNo() %></div>
						</div>
					</c:otherwise>
				</c:choose>
				
				<div class="row-fluid">
					<div class="span1"></div>
					
					<div class="span2 bold-label">
						<liferay-ui:message key="service-name"/>
					</div>
					
					<div class="span9"><%=dossierBean.getServiceName() %></div>
				</div>
				
				<div class="row-fluid">
					<div class="span1"></div>
					
					<div class="span2 bold-label"><liferay-ui:message key="gov-agency-name"/></div>
					
					<div class="span9"><%=dossier.getGovAgencyName() %></div>
				</div>
				
			</liferay-util:buffer>
			
			<liferay-util:buffer var="status">
				<div class="row-fluid">
					<div class="span5 bold-label"><liferay-ui:message key="create-date"/></div>
					<div class="span7">
						<%=
							Validator.isNotNull(dossier.getCreateDate()) ? 
							DateTimeUtil.convertDateToString(dossier.getCreateDate(), DateTimeUtil._VN_DATE_TIME_FORMAT) : 
							StringPool.DASH 
						%>
					</div>
				</div>
				
				<div class="row-fluid">
					<div class="span5 bold-label">
						 <liferay-ui:message key="receive-datetime"/>
					</div>
					
					<div class="span7">
						<%=
							Validator.isNotNull(dossier.getReceiveDatetime()) ? 
							DateTimeUtil.convertDateToString(dossier.getReceiveDatetime(), DateTimeUtil._VN_DATE_TIME_FORMAT): 
							StringPool.DASH 
						%>
					</div>
				</div>
				
				<div class="row-fluid">
				
					<div class="span5 bold-label">
						<liferay-ui:message key="finish-date"/>
					</div>
					<div class="span7">
						<%=
							Validator.isNotNull(dossier.getFinishDatetime()) ? 
							DateTimeUtil.convertDateToString(dossier.getFinishDatetime(), DateTimeUtil._VN_DATE_TIME_FORMAT): 
							StringPool.DASH 
						%>
					</div>
				</div>
				
				<div class="row-fluid">
					
					<div class="span5 bold-label">
						<liferay-ui:message key="dossier-status"/>
					</div>
					
					<div class='<%="span7 " + cssStatusColor %>'>
						<%-- <%=PortletUtil.getDossierStatusLabel(dossier.getDossierStatus(), locale) %> --%>
						<%= DictItemUtil.getDictItemName(dossier.getDossierStatus(), locale)%>
					</div>
				</div>
			</liferay-util:buffer>
				
				<%
					row.setClassName("opencps-searchcontainer-row");
					row.addText(info);
					row.addText(status);
					row.addJSP("center", SearchEntry.DEFAULT_VALIGN,"/html/portlets/dossiermgt/frontoffice/dossier_actions.jsp", 
								config.getServletContext(), request, response);
					
				%>	
			</liferay-ui:search-container-row> 
		
		<liferay-ui:search-iterator type="opencs_page_iterator"/>
		
	</liferay-ui:search-container>
</div>

<c:if test="<%= timeToReLoad_cfg > 0 %>">

	<liferay-portlet:renderURL var="newDossierURL" portletName="<%=WebKeys.DOSSIER_MGT_PORTLET %>" windowState="<%=LiferayWindowState.EXCLUSIVE.toString() %>">
		<liferay-portlet:param name="mvcPath" value="/html/portlets/dossiermgt/frontoffice/ajax/_default_new_dossier.jsp" />
	</liferay-portlet:renderURL>
	
	<aui:script use="aui-base,aui-io-plugin">
		
		var timer = '<%=timeToReLoad_cfg * 1000 %>';
	
		AUI().ready(function(A){
			
			setInterval(function(){ 
				
				console.log("call new dossier list");
	
				$("#<portlet:namespace />is-hidden").load( '<%= newDossierURL %>', function () {
					
					selector: '#<portlet:namespace />is-hidden > .lfr-search-container'
					
				});
				
			}, timer);
			
		});
	</aui:script>
	
</c:if>

<%!
	private Log _log = LogFactoryUtil.getLog("html.portlets.dossiermgt.frontoffice.frontofficedossierlist.jsp");
%>
