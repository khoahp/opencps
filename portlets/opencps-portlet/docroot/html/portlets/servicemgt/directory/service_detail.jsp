<%@page import="javax.portlet.PortletRequest"%>
<%@page import="com.liferay.portlet.PortletURLFactoryUtil"%>
<%@page import="org.opencps.dossiermgt.search.DossierDisplayTerms"%>
<%@page import="org.opencps.util.PortletPropsValues"%>
<%@page import="org.opencps.util.PortletUtil"%>
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
<%@page import="org.opencps.util.DictItemUtil"%>
<%@page import="com.liferay.portal.kernel.util.StringPool"%>
<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@page import="com.liferay.portal.kernel.util.ParamUtil"%>
<%@page import="org.opencps.util.WebKeys"%>
<%@page import="org.opencps.servicemgt.model.ServiceInfo"%>
<%@page import="org.opencps.servicemgt.service.ServiceInfoLocalServiceUtil"%>
<%@page import="org.opencps.dossiermgt.service.ServiceConfigLocalServiceUtil"%>
<%@page import="org.opencps.dossiermgt.model.ServiceConfig"%>
<%@ include file="init.jsp" %>
<%
	long serviceInfoId = ParamUtil.getLong(request, "serviceinfoId");

	ServiceInfo serviceInfo = null;
	DictItem dictItem = null;
	
	try {
		serviceInfo = ServiceInfoLocalServiceUtil.getServiceInfo(serviceInfoId);
	} catch (Exception e) {
		//nothing to do
	}
	
	String backURL = ParamUtil.getString(request, "backURL");
	
	
	List<DictItem> listAdmin = new ArrayList<DictItem>();
	List<ServiceConfig> listServiceConfig = new ArrayList<ServiceConfig>();
	
	try {
		
		if(Validator.isNotNull(serviceInfoId)){
			listServiceConfig = ServiceConfigLocalServiceUtil.getServiceConfigsByS_G(serviceInfoId, scopeGroupId);
			
			if(Validator.isNotNull(listServiceConfig)){
				
				for(ServiceConfig serviceConfig: listServiceConfig){
					
					if(serviceConfig.getServiceLevel() >=3){
						
						dictItem = PortletUtil.getDictItem(PortletPropsValues.DATAMGT_MASTERDATA_GOVERNMENT_AGENCY, serviceConfig.getGovAgencyCode(), scopeGroupId);
						
						if(dictItem != null){
							listAdmin.add(dictItem);
						}
					}
				}
			}
		}
			
	} catch (Exception e) {
		//nothing to do
	}
	
%>



<liferay-portlet:renderURL 
		var="renderURL" 
		portletName='<%=WebKeys.DOSSIER_MGT_PORTLET %>' 
		plid="<%=plidAddDossier %>"
		portletMode="VIEW"
 		windowState="<%=LiferayWindowState.NORMAL.toString() %>"> 		
	<portlet:param name="mvcPath" value="/html/portlets/dossiermgt/frontoffice/edit_dossier.jsp"/> 
	<portlet:param name="isEditDossier" value="<%=String.valueOf(true) %>"/> 
	<portlet:param name="backURL" value="<%=backURL %>"/>
	<portlet:param name="<%=Constants.CMD %>" value="<%=Constants.ADD %>"/> 	
</liferay-portlet:renderURL>

<portlet:renderURL var="referToServiceInstructionURL" windowState="<%=LiferayWindowState.EXCLUSIVE.toString() %>">
	<portlet:param name="mvcPath" value="/html/portlets/servicemgt/directory/ajax/service_instruction.jsp"/>
	<portlet:param name="backURL" value="<%=backURL %>"/>
</portlet:renderURL>


<div class="ocps-service-detal-bound-all">
	<div class="ocps-custom-header">
		<label class="opcps-label">
			<liferay-ui:message key="service-detail" />
		</label>
	</div>
	
	<div class="ocps-hide-header">
		<liferay-ui:header
			backURL="<%= backURL %>"
			title="service"
		/>
	</div>
	
	<c:if test="<%= Validator.isNotNull(serviceInfo) %>">
		<div class="service-detail-wrapper">
			<table>
				<tr>
					<td class="col-left">
						<liferay-ui:message key="service-no"/>
					</td>
					<td class="col-right">
						<%= serviceInfo.getServiceNo() %>
					</td>
				</tr>
				<tr>
					<td class="col-left">
						<liferay-ui:message key="service-name"/>
					</td>
					<td class="col-right">
						<%= serviceInfo.getServiceName() %>
					</td>
				</tr>
				<tr>
					<td class="col-left">
						<liferay-ui:message key="service-process"/>
					</td>
					<td class="col-right">
						<%= serviceInfo.getServiceProcess() %>
					</td>
				</tr>
				<tr>
					<td class="col-left">
						<liferay-ui:message key="service-method"/>
					</td>
					<td class="col-right">
						<%= serviceInfo.getServiceMethod() %>
					</td>
				</tr>
				<tr>
					<td class="col-left">
					<liferay-ui:message key="service-dossier"/>
						
					</td>
					<td class="col-right">
						<%= serviceInfo.getServiceDossier() %>	
					</td>
				</tr>
				<tr>
					<td class="col-left">
						<liferay-ui:message key="service-condition"/>
					</td>
					<td class="col-right">
						<%= serviceInfo.getServiceCondition() %>
					</td>
				</tr>
				<tr>
					<td class="col-left">
						<liferay-ui:message key="service-duration"/>
					</td>
					<td class="col-right">
						<%= serviceInfo.getServiceDuration() %>
					</td>
				</tr>
				<tr>
					<td class="col-left">
						<liferay-ui:message key="service-actors"/>
					</td>
					<td class="col-right">
						<%= serviceInfo.getServiceActors() %>
					</td>
				</tr>
				<tr>
					<td class="col-left">
						<liferay-ui:message key="service-fee"/>
					</td>
					<td class="col-right">
						<%= serviceInfo.getServiceFee() %>
					</td>
				</tr>
				<tr>
					<td class="col-left">
						<liferay-ui:message key="service-results"/>
					</td>
					<td class="col-right">
						<%= serviceInfo.getServiceResults() %>
					</td>
				</tr>
				<tr>
					<td class="col-left">
						<liferay-ui:message key="service-records"/>
					</td>
					<td class="col-right">
						<%= serviceInfo.getServiceRecords() %>
					</td>
				</tr>
				<tr>
					<td class="col-left">
						<liferay-ui:message key="template_info"/>
					</td>
					<td class="col-right">
						
						<%
							List<TemplateFile> templates = new ArrayList<TemplateFile>();
						
							if (Validator.isNotNull(serviceInfo)) {
								templates = TemplateFileLocalServiceUtil.getServiceTemplateFiles(serviceInfo.getServiceinfoId());
							}
						%>
						<ul>
							<%
								for (TemplateFile tf : templates) {
							%>
								<li>  <i class="icon-file"></i>  <a href="<%= ServiceUtil.getDLFileURL(tf.getFileEntryId()) %>"> <%= tf.getFileName() %> </a></li>
							<%		
								}
							%>
						</ul>
						
					</td>
				</tr>
			</table>
			
			<c:if test="<%=listAdmin!=null && !listAdmin.isEmpty() %>">
				<aui:row>
					<aui:col width="100">
						<aui:select name="<%=DossierDisplayTerms.SERVICE_CONFIG_ID %>" label="co-quan-thuc-hien" cssClass="submit-online input100" 
						showEmptyOption="<%= (listAdmin != null && listAdmin.size() > 1) %>"
						onChange='<%=renderResponse.getNamespace() + "onSelectGovAgency();" %>'>
							<%
							
								for(DictItem d : listAdmin){
									ServiceConfig serviceConfig = ServiceConfigLocalServiceUtil.getServiceConfigByG_S_G(themeDisplay.getScopeGroupId(), serviceInfoId, d.getItemCode());
									
									%>
										<aui:option value="<%=serviceConfig.getServiceConfigId() %>">
											<%=d.getItemName(themeDisplay.getLocale(),true) %>
										</aui:option>
									<%
								}
								
							%>
						</aui:select>
					</aui:col>
				</aui:row>
			</c:if>
			
			<div id = "<portlet:namespace />serviceInstruction"></div>
			
			<aui:button-row>
				<aui:button href="<%=backURL %>" cssClass="des-sub-button radius20 back-icon" value='<%=LanguageUtil.get(themeDisplay.getLocale(), "back") %>'/>
				<c:if test="<%= listAdmin!=null && !listAdmin.isEmpty() %>">
					<% String jsFunc = renderResponse.getNamespace() + "selectServiceConfig();"; %>
					<aui:button  cssClass="des-sub-button radius20 send-icon" value="dossier-submit-online-temp" name="btn_des" onClick="<%=jsFunc %>" ></aui:button>
				</c:if>
			</aui:button-row>
			
		</div>
	</c:if>
</div>

<aui:script use="aui-base,liferay-portlet-url">
	AUI().ready(function(A) {
		
		var serviceConfigIdSel = A.one("#<portlet:namespace/>serviceConfigId"); 
		
		
		<portlet:namespace />getServiceInstruction(serviceConfigIdSel.val());
		
	});
	
	Liferay.provide(window, '<portlet:namespace/>selectServiceConfig', function() {
 		var A = AUI();
 		
 		var serviceConfigIdSel = A.one("#<portlet:namespace/>serviceConfigId"); 
 		
 		if(serviceConfigIdSel.val() != '') {
 			
 			<portlet:namespace />getServiceInstruction(serviceConfigIdSel.val());
 			
 			var renderUrlTest = Liferay.PortletURL.createURL('<%=renderURL.toString() %>');
		
			renderUrlTest = renderUrlTest+'&_13_WAR_opencpsportlet_serviceConfigId='+serviceConfigIdSel.val();
		
			location.href=renderUrlTest;
 		} else {
 			alert(Liferay.Language.get('vui-long-chon-co-quan-thuc-hien'));
 		}
 	});
	
	Liferay.provide(window, '<portlet:namespace/>onSelectGovAgency', function() {
 		var A = AUI();
 		
 		var serviceConfigIdSel = A.one("#<portlet:namespace/>serviceConfigId"); 
 		
 		if(serviceConfigIdSel.val() != '') {
 			
 			<portlet:namespace />getServiceInstruction(serviceConfigIdSel.val());
 			
 		} 
 	});
	
	Liferay.provide(window, '<portlet:namespace />getServiceInstruction', function(serviceConfigId) {
		
		var A = AUI();
		A.io.request(
				'<%=referToServiceInstructionURL.toString() %>',
				{
					dataType : 'text/html',
					method : 'GET',
					data:{
						"<portlet:namespace />serviceConfigId" : serviceConfigId	
					},
					on: {
						success: function(event, id, obj) {
							var instance = this;
							var res = instance.get('responseData');
							
							var serviceInstruction = A.one("#<portlet:namespace/>serviceInstruction");
							
							if(serviceInstruction){
								serviceInstruction.empty();
								serviceInstruction.html(res);
							}
								
						},
						error: function(){}
					}
				}
			);
	},['aui-base','aui-io']);
</aui:script>
