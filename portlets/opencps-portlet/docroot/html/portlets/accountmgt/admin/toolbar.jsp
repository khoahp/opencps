<%@page import="org.opencps.datamgt.model.DictItem"%>
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
<%@page import="org.opencps.util.PortletConstants"%>
<%@page import="org.opencps.accountmgt.search.BusinessDisplayTerms"%>
<%@page import="org.opencps.accountmgt.search.CitizenDisplayTerms"%>
<%@page import="org.opencps.util.PortletUtil"%>
<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@page import="org.opencps.accountmgt.permissions.BusinessPermission"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@page import="org.opencps.util.ActionKeys"%>
<%@page import="org.opencps.accountmgt.permissions.CitizenPermission"%>
<%@page import="javax.portlet.PortletURL"%>
<%@page import="org.opencps.accountmgt.util.AccountMgtUtil"%>
<%@page import="org.opencps.datamgt.search.DictItemDisplayTerms"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.liferay.portal.kernel.log.LogFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.log.Log"%>
<%@page import="java.util.List"%>
<%@page import="org.opencps.accountmgt.model.BusinessDomain"%>
<%@page import="org.opencps.util.PortletPropsValues"%>

<%@ include file="../init.jsp"%>
<%
	String tabs1 = ParamUtil.getString(request, "tabs1", AccountMgtUtil.TOP_TABS_CITIZEN);
	
	PortletURL searchURL = renderResponse.createRenderURL();
	
	String businessDomain = ParamUtil.getString(request, BusinessDisplayTerms.BUSINESS_DOMAIN);
	
	DictItem dictItemBusinessDomain = PortletUtil
					.getDictItem(PortletPropsValues.DATAMGT_MASTERDATA_BUSINESS_DOMAIN, businessDomain, scopeGroupId);
	
	long businessDomainItemId = Validator.isNotNull(dictItemBusinessDomain) ? dictItemBusinessDomain.getDictItemId() : 0L;
	
	int [] accoutStatuses = new int [4];
	accoutStatuses[0] = PortletConstants.ACCOUNT_STATUS_REGISTERED;
	accoutStatuses[1] = PortletConstants.ACCOUNT_STATUS_CONFIRMED;
	accoutStatuses[2] = PortletConstants.ACCOUNT_STATUS_APPROVED;
	accoutStatuses[3] = PortletConstants.ACCOUNT_STATUS_LOCKED;
	
	
%>

<aui:nav-bar cssClass="opencps-toolbar custom-toolbar">
	<aui:nav id="toolbarContainer" cssClass="nav-display-style-buttons pull-left" >
		<c:if test="<%=CitizenPermission.contains(permissionChecker, scopeGroupId, ActionKeys.ADD_CITIZEN) && tabs1.equals(AccountMgtUtil.TOP_TABS_CITIZEN)%>">
			<%
				searchURL.setParameter("mvcPath", templatePath + "citizenlist.jsp");
				searchURL.setParameter("tabs1", AccountMgtUtil.TOP_TABS_CITIZEN);
			%>
		</c:if>
		<c:if test="<%=BusinessPermission.contains(permissionChecker, scopeGroupId, ActionKeys.ADD_BUSINESS) && tabs1.equals(AccountMgtUtil.TOP_TABS_BUSINESS)%>">
			<%
				searchURL.setParameter("mvcPath", templatePath + "businesslist.jsp");
				searchURL.setParameter("tabs1", AccountMgtUtil.TOP_TABS_BUSINESS);
			%>
			
		</c:if>
	</aui:nav>
	
	<aui:nav-bar-search cssClass="pull-right input100">
		<div class="form-search">
			<aui:form action="<%= searchURL %>" method="post" name="fm">
				<div class="toolbar_search_input">
					<c:if test="<%=tabs1.equals(AccountMgtUtil.TOP_TABS_CITIZEN)%>">
						<aui:row>
							<aui:col width="30" cssClass="search-col">
 							<% 	
 								String status = (String)request.getParameter("accountStatus");
 							%>
 							<portlet:resourceURL var="exportToExcelURL">
 								<portlet:param name="status" value="<%= status %>"/>
 								<portlet:param name="word" value="<%= keyword %>"/>
 								<portlet:param name="type" value="<%= AccountMgtUtil.TOP_TABS_CITIZEN %>"/>
 							</portlet:resourceURL>
 							
 							 	<aui:button icon="icon-file" name="btExportExclel" href="<%=exportToExcelURL.toString() %>" cssClass="action-button-csv"  value="export-account-csv" />
 								
 							 </aui:col>				
							<aui:col width="30" cssClass="search-col">
								<aui:select name="<%=CitizenDisplayTerms.CITIZEN_ACCOUNTSTATUS %>" 
									label="<%=StringPool.BLANK %>" 
									cssClass="search-input select-box"
									>
										<aui:option value="<%= -1 %>">
											<liferay-ui:message key="account-status" />
										</aui:option>
										<%
											for(int i=0; i<accoutStatuses.length; i++) {
												%>
													<aui:option value="<%=accoutStatuses[i] %>">
														<liferay-ui:message key="<%=PortletUtil.getAccountStatus(accoutStatuses[i], themeDisplay.getLocale()) %>" />
													</aui:option>
												<%
												
											}
										%>
								</aui:select>
							</aui:col>
							<aui:col width="30" cssClass="search-col">
								<liferay-ui:input-search 
									id="keywords1" 
									name="keywords" 
									title='<%= LanguageUtil.get(locale, "keywords") %>' 
									placeholder='<%= LanguageUtil.get(locale, "name") %>' 			
									cssClass="search-input input-keyword"
								/>
							</aui:col>
							
						</aui:row>
					</c:if>
					<c:if test="<%=tabs1.equals(AccountMgtUtil.TOP_TABS_BUSINESS)%>">
						
						<aui:row>
							<aui:col width="25" cssClass="search-col">
 							<% 	
 								String status = (String)request.getParameter("accountStatus");
 							%>
 							<portlet:resourceURL var="exportToExcelURL">
 								<portlet:param name="status" value="<%= status %>"/>
 								<portlet:param name="word" value="<%= keyword %>"/>
 								<portlet:param name="type" value="<%= AccountMgtUtil.TOP_TABS_BUSINESS %>"/>
 							</portlet:resourceURL>
 							
 							 	<aui:button icon="icon-file" name="btExportExclel" href="<%=exportToExcelURL.toString() %>" cssClass="action-button-csv"  value="export-account-csv" />
 								
 							 </aui:col>	
							<aui:col width="25" cssClass="search-col">
									<datamgt:ddr 
										depthLevel="1" 
										dictCollectionCode="<%=PortletPropsValues.DATAMGT_MASTERDATA_BUSINESS_DOMAIN %>"
										name="<%=BusinessDisplayTerms.BUSINESS_DOMAIN %>"
										inlineField="<%=true%>"
										inlineLabel="left"
										showLabel="<%=false%>"
										emptyOptionLabels="business-domain"
										itemsEmptyOption="true"
										itemNames= "<%=BusinessDisplayTerms.BUSINESS_DOMAIN %>"
										cssClass="search-input select-box"
										optionValueType="code"
										selectedItems="<%= businessDomain %>"
									/>
							</aui:col>
							
							 <aui:col width="25" cssClass="search-col">
								<aui:select name="<%=BusinessDisplayTerms.BUSINESS_ACCOUNTSTATUS %>" 
									label="<%=StringPool.BLANK %>"
									cssClass="search-input select-box"
								>
									<aui:option value="<%=-1 %>">
										<liferay-ui:message key="account-status" />
									</aui:option>
									<%
										for(int i=0; i<accoutStatuses.length; i++) {
											%>
												<aui:option value="<%=accoutStatuses[i] %>">
													<liferay-ui:message key="<%=PortletUtil.getAccountStatus(accoutStatuses[i], themeDisplay.getLocale()) %>" />
												</aui:option>
											<%
											
										}
									%>
								</aui:select>
							 </aui:col>
							 <aui:col width="25" cssClass="search-col">
								<liferay-ui:input-search 
									id="keywords1" 
									name="keywords" 
									title='<%= LanguageUtil.get(locale, "keywords") %>'
									placeholder='<%= LanguageUtil.get(locale, "name") %>' 			
									cssClass="search-input input-keyword"
								/>
							 </aui:col>
							
						</aui:row>
					</c:if>
				</div>
			</aui:form>
		</div>
	</aui:nav-bar-search>
</aui:nav-bar>
<%!
	private Log _log = LogFactoryUtil.getLog("html.portlets.data_management.admin.toolbar.jsp");
%>