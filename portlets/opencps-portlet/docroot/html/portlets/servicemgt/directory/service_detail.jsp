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
	long serviceinfoId = ParamUtil.getLong(request, "serviceinfoId");
	ServiceInfo serviceInfo = null;
	DictItem dictItem = null;
	
	try {
		serviceInfo = ServiceInfoLocalServiceUtil.getServiceInfo(serviceinfoId);
	} catch (Exception e) {
		//nothing to do
	}
	
	String backURL = ParamUtil.getString(request, "backURL");
	
	ServiceConfig scf = null;
	try {
		scf = ServiceConfigLocalServiceUtil.getServiceConfigByG_S(scopeGroupId, serviceinfoId);
	} catch(Exception e){
		//
	}
	boolean serviceIsConfiged;
	if(Validator.isNotNull(scf) && scf.getServiceLevel() >= 3){
		serviceIsConfiged = true;
	} else {
		serviceIsConfiged = false;
	}
	
	List<DictItem> listAdmin = new ArrayList<DictItem>();
	List<ServiceConfig> listServiceConfig = new ArrayList<ServiceConfig>();
	
	try {
		//Lay thong tin co quan thuc hien theo serviceConfigId tu man hinh tiep nhan ho so
		if(Validator.isNotNull(scf)){
			dictItem = PortletUtil.getDictItem(PortletPropsValues.DATAMGT_MASTERDATA_GOVERNMENT_AGENCY, scf.getGovAgencyCode(), scopeGroupId);
			if(dictItem != null){
				listAdmin.add(dictItem);
			}
		}
		//Lay thong tin co quan thuc hien tu dich vu cong END
		
		//Lay thong tin co quan thuc hien theo serviceinfoId tu man hinh thu tuc hanh chinh 
		if(Validator.isNotNull(serviceinfoId)){
			listServiceConfig = ServiceConfigLocalServiceUtil.getServiceConfigsByS_G(serviceinfoId, scopeGroupId);
			if(Validator.isNotNull(listServiceConfig)){
				for(ServiceConfig s: listServiceConfig){
					dictItem = PortletUtil.getDictItem(PortletPropsValues.DATAMGT_MASTERDATA_GOVERNMENT_AGENCY, s.getGovAgencyCode(), scopeGroupId);
					if(dictItem != null){
						listAdmin.add(dictItem);
					}
				}
			}
		}
		//Lay thong tin co quan thuc hien theo serviceinfoId tu man hinh thu tuc hanh chinh END
		
		
		
	} catch (Exception e) {
		//nothing to do
	}
	
	long frontServicePlid = PortalUtil.getPlidFromPortletId(scopeGroupId, WebKeys.DOSSIER_MGT_PORTLET);

	long plidSubmit = 0;
	
	if(Long.valueOf(plidRes) == 0) {
		plidSubmit = frontServicePlid;
	} else {
		plidSubmit = Long.valueOf(plidRes);
	}
%>

<%-- <liferay-portlet:renderURL  --%>
<%-- 		var="renderToSubmitOnline"  --%>
<%-- 		portletName="<%=WebKeys.P26_SUBMIT_ONLINE %>" --%>
<%-- 		plid="<%=plidServiceDetail %>" --%>
<%-- 		portletMode="VIEW" --%>
<%-- 		windowState="<%=LiferayWindowState.NORMAL.toString() %>" --%>
<%-- 	> --%>
<%-- 	<portlet:param name="mvcPath" value="/html/portlets/dossiermgt/submit/dossier_submit_online.jsp"/> --%>
<%-- 	<portlet:param name="serviceinfoId" value="<%=String.valueOf(serviceinfoId) %>"/> --%>
<%-- 	<portlet:param name="backURL" value="<%=backURL %>"/> --%>
<%-- </liferay-portlet:renderURL> --%>

<%-- <liferay-portlet:renderURL  --%>
<%-- 		var="renderToSubmitOnline"  --%>
<%-- 		portletName='<%=WebKeys.DOSSIER_MGT_PORTLET %> %>' --%>
<%-- 		plid="<%=plidSubmit %>" --%>
<%-- 		portletMode="VIEW" --%>
<%-- 		windowState="<%=LiferayWindowState.NORMAL.toString() %>" --%>
<%-- 	> --%>
<%-- 	<portlet:param name="mvcPath" value="/html/portlets/dossiermgt/frontoffice/edit_dossier.jsp"/> --%>
<%-- 	<portlet:param name="backURL" value="<%=backURL %>"/> --%>
<%-- 	<portlet:param name="<%=DossierDisplayTerms.SERVICE_CONFIG_ID %>" value="<%=String.valueOf(scf.getServiceConfigId()) %>"/> --%>
<%-- 	<portlet:param name="<%=Constants.CMD %>" value="<%=Constants.ADD %>"/> --%>
<%-- 	<portlet:param name="backURL" value="<%=backURL %>"/> --%>
<%-- 	<portlet:param name="isEditDossier" value="<%=String.valueOf(true) %>"/> --%>
<%-- </liferay-portlet:renderURL> --%>

<aui:form>
	<aui:input name="serviceInfoId" type="hidden" value="<%=serviceinfoId %>"/>
	<aui:input name="backURL" type="hidden" value="<%=backURL %>"/>
	<aui:input name="<%=Constants.CMD %>" type="hidden" value="<%=Constants.CMD %>"/>
	<aui:input name="isEditDossier" type="hidden" value="<%=String.valueOf(true) %>"/>
	<aui:input name="mvcPath" type="hidden" value="/html/portlets/dossiermgt/frontoffice/edit_dossier.jsp"/>
</aui:form>


<div class="ocps-service-detal-bound-all">
	<div class="ocps-custom-header">
		<label class="opcps-label">
			<liferay-ui:message key="service-detail" />
		</label>
		<span class="ocps-span">
			<a href="<%=backURL %>"><liferay-ui:message key="back"/></a>
		</span>
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
				<tr>
					<aui:row>
						
							<aui:select name="govAgencyCode" label="co-quan-thuc-hien" cssClass="submit-online input100">
								<%
									if(listAdmin!=null && !listAdmin.isEmpty()){
										for(DictItem d : listAdmin){
											%>
												<aui:option value="<%=d.getItemCode() %>">
													<%=d.getItemName(themeDisplay.getLocale(),true) %>
												</aui:option>
											<%
										}
									}
								%>
							</aui:select>
						
					</aui:row>
					<c:if test="<%= serviceIsConfiged %>">
						<td class="col-left" colspan="2">
							<aui:button  cssClass="des-sub-button radius20" value="dossier-submit-online-temp" name="btn_des"></aui:button>
							
						</td>
					</c:if>
				</tr>
			</table>
		</div>
	</c:if>
</div>

<aui:script>
	AUI().ready('aui-base','liferay-portlet-url',function(A) {
		var govAgencyCodeSel = A.one("#<portlet:namespace/>govAgencyCode");
		var backUrl = A.one("#<portlet:namespace/>backURL");
		var serviceInfoId = A.one("#<portlet:namespace/>serviceInfoId");
		var isEditDossier = A.one("#<portlet:namespace/>isEditDossier");
		var mvcPath = A.one("#<portlet:namespace/>mvcPath");
		var CMD = A.one("#<portlet:namespace/>cmd");
		
		console.log("govAgencyCodeSel:"+govAgencyCodeSel);
		console.log("backUrl:"+backUrl);
		console.log("serviceInfoId:"+serviceInfoId);
		console.log("isEditDossier:"+isEditDossier);
		console.log("mvcPath:"+mvcPath);
		console.log("CMD:"+CMD);

		if(govAgencyCodeSel) {
			
			govAgencyCodeSel.on('change',function() {
				
				var renderUrl = new Liferay.PortletURL.createURL();
				
				renderUrl.setWindowState("<%=LiferayWindowState.NORMAL.toString() %>");
				renderUrl.setPortletId("<%=plidServiceDetail %>");
				renderUrl.setParameter("govAgencyCode",govAgencyCodeSel);
				renderUrl.setParameter("backURL",backUrl);
				renderUrl.setParameter("serviceInfoId",serviceInfoId);
				renderUrl.setParameter("isEditDossier",isEditDossier);
				renderUrl.setParameter("mvcPath",mvcPath);
				renderUrl.setParameter("cmd",CMD);
				
				console.log("renderUrl.toString():"+renderUrl.toString());
				
				A.one("#<portlet:namespace/>btn_des").set('href',renderUrl.toString());
				
			});
		}
		
	});
</aui:script>
