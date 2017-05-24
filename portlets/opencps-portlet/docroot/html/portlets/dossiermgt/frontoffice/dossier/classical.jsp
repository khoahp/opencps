
<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>
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
<%@page import="com.liferay.portlet.documentlibrary.DuplicateFolderNameException"%>
<%@page import="org.opencps.dossiermgt.search.DossierDisplayTerms"%>
<%@page import="org.opencps.util.PortletPropsValues"%>
<%@page import="org.opencps.util.WebKeys"%>
<%@page import="org.opencps.dossiermgt.model.ServiceConfig"%>
<%@page import="com.liferay.portal.kernel.util.Constants"%>
<%@page import="org.opencps.accountmgt.model.Business"%>
<%@page import="org.opencps.accountmgt.model.Citizen"%>
<%@page import="org.opencps.dossiermgt.model.Dossier"%>
<%@page import="org.opencps.servicemgt.model.ServiceInfo"%>
<%@page import="org.opencps.dossiermgt.EmptyDossierCityCodeException"%>
<%@page import="org.opencps.dossiermgt.EmptyDossierDistrictCodeException"%>
<%@page import="org.opencps.dossiermgt.EmptyDossierWardCodeException"%>
<%@page import="org.opencps.dossiermgt.InvalidDossierObjectException"%>
<%@page import="org.opencps.dossiermgt.CreateDossierFolderException"%>
<%@page import="org.opencps.dossiermgt.EmptyDossierSubjectNameException"%>
<%@page import="org.opencps.dossiermgt.OutOfLengthDossierSubjectNameException"%>
<%@page import="org.opencps.dossiermgt.EmptyDossierSubjectIdException"%>
<%@page import="org.opencps.dossiermgt.OutOfLengthDossierSubjectIdException"%>
<%@page import="org.opencps.dossiermgt.EmptyDossierAddressException"%>
<%@page import="org.opencps.dossiermgt.OutOfLengthDossierContactEmailException"%>
<%@page import="org.opencps.dossiermgt.OutOfLengthDossierContactNameException"%>
<%@page import="org.opencps.dossiermgt.OutOfLengthDossierContactTelNoException"%>
<%@page import="org.opencps.dossiermgt.EmptyDossierContactNameException"%>
<%@page import="org.opencps.dossiermgt.OutOfLengthDossierAddressException"%>

<%@ include file="../../init.jsp"%>

<%

	Dossier dossier = (Dossier) request.getAttribute(WebKeys.DOSSIER_ENTRY);

	ServiceConfig serviceConfig = (ServiceConfig) request.getAttribute(WebKeys.SERVICE_CONFIG_ENTRY);
	
	ServiceInfo serviceInfo = (ServiceInfo) request.getAttribute(WebKeys.SERVICE_INFO_ENTRY);
	
	String itemSelected = GetterUtil.getString(request.getAttribute(WebKeys.DICT_ITEM_SELECTED), StringPool.BLANK);
	
	String cmd = ParamUtil.getString(request, Constants.CMD);
	
	String govAgencyCode = serviceConfig.getGovAgencyCode();
	
	
%>


<aui:model-context bean="<%=dossier%>" model="<%=Dossier.class%>" />

<liferay-ui:error-marker key="errorSection" value="dossier_info" />

<liferay-ui:error 
	exception="<%= EmptyDossierCityCodeException.class %>" 
	message="<%=EmptyDossierCityCodeException.class.getName() %>"
/>
<liferay-ui:error 
	exception="<%= EmptyDossierDistrictCodeException.class %>"
	message="<%=EmptyDossierDistrictCodeException.class.getName() %>"
/>
<liferay-ui:error 
	exception="<%= EmptyDossierWardCodeException.class %>" 
	message="<%=EmptyDossierWardCodeException.class.getName() %>"
/>
<liferay-ui:error 
	exception="<%= InvalidDossierObjectException.class %>" 
	message="<%=InvalidDossierObjectException.class.getName() %>"
/>
<liferay-ui:error 
	exception="<%= CreateDossierFolderException.class %>" 
	message="<%=CreateDossierFolderException.class.getName() %>"
/>
<liferay-ui:error 
	exception="<%= EmptyDossierSubjectNameException.class %>" 
	message="<%=EmptyDossierSubjectNameException.class.getName() %>"
/>
<liferay-ui:error 
	exception="<%= OutOfLengthDossierSubjectNameException.class %>" 
	message="<%=OutOfLengthDossierSubjectNameException.class.getName() %>"
/>
<liferay-ui:error 
	exception="<%= EmptyDossierSubjectIdException.class %>" 
	message="<%=EmptyDossierSubjectIdException.class.getName() %>"
/>
<liferay-ui:error 
	exception="<%= EmptyDossierAddressException.class %>" 
	message="<%=EmptyDossierAddressException.class.getName() %>"
/>
<liferay-ui:error 
	exception="<%= OutOfLengthDossierContactEmailException.class %>" 
	message="<%=OutOfLengthDossierContactEmailException.class.getName() %>"
/>
<liferay-ui:error 
	exception="<%= OutOfLengthDossierContactNameException.class %>" 
	message="<%=OutOfLengthDossierContactNameException.class.getName() %>"
/>
<liferay-ui:error 
	exception="<%= OutOfLengthDossierContactTelNoException.class %>" 
	message="<%=OutOfLengthDossierContactTelNoException.class.getName() %>"
/>
<liferay-ui:error 
	exception="<%= EmptyDossierContactNameException.class %>" 
	message="<%=EmptyDossierContactNameException.class.getName() %>"
/>
<liferay-ui:error 
	exception="<%= OutOfLengthDossierAddressException.class %>" 
	message="<%=OutOfLengthDossierAddressException.class.getName() %>"
/>
<liferay-ui:error 
	exception="<%= InvalidDossierObjectException.class %>" 
	message="<%=InvalidDossierObjectException.class.getName() %>"
/>

<liferay-ui:error 
	exception="<%= DuplicateFolderNameException.class %>" 
	message="<%=DuplicateFolderNameException.class.getName() %>"
/>

<div class="fe-dossier-wrapper" style="padding: 10px;">
	<div class="head-title">
		<liferay-ui:message key='<%= Validator.isNotNull(dossier) ? "add-dossier" : "update-dossier" %>'/>
	</div>
	
	<liferay-ui:panel-container cssClass="dossier-info-panel" extended="<%= false %>" id="dossierInfoPanelContainer" persistState="<%= true %>">
		<liferay-ui:panel collapsible="<%= true %>" cssClass="general-dossier-info-content" extended="<%= true %>" id="dossierInfoPanel" persistState="<%= true %>" title="dossier-information">
			
			<aui:row>
				<aui:col width="50">
					<aui:row>
						<span class="span4">
							<liferay-ui:message key="serivce-name"/>
						</span>
						<span class="span8" style="title-serv-name">
							<%=serviceInfo != null ? serviceInfo.getServiceName() : StringPool.BLANK %>
						</span>
					</aui:row>
					
					<aui:row>
						<span class="span4">
							<liferay-ui:message key="gov-agence-name"/>
						</span>
						<span class="span8">
							<%=serviceInfo != null ? serviceConfig.getGovAgencyName() : StringPool.BLANK %>
						</span>
					</aui:row>
					<aui:row>
						<span class="span4">
							<liferay-ui:message key="dossier-number"/>
						</span>
						<span class="span8">
							<%= Validator.isNotNull(dossier) ? dossier.getDossierId() : "-----" %>
						</span>
					</aui:row>
				</aui:col>
					
				<aui:col width="50">
					<aui:row>
						<span class="span4">
							<liferay-ui:message key="reception-no"/>
						</span>
						<span class="span8">
							<% 
								String receptionNo = "----"; 
								if (Validator.isNotNull(dossier) && Validator.isNotNull(dossier.getReceptionNo()))	{
									receptionNo = dossier.getReceptionNo();
								}
							%>
							<%= receptionNo %>
						</span>
					</aui:row>
					
					<aui:row>
						<span class="span4">
							<liferay-ui:message key="dossier-status"/>
						</span>
						<span class="span8">
							<%  %>
							<%= Validator.isNotNull(dossier) ? LanguageUtil.get(locale, dossier.getDossierStatus())  : "-----" %>
						</span>
					</aui:row>
					
				</aui:col>
			</aui:row>
			
			<hr style="border-top: dotted 2px;" />
			
			<aui:row>
				<aui:col width="50">
					<aui:row>
						<span class="span4">
							<liferay-ui:message key="subject-name"/>
						</span>
						<span class="span8">
							<aui:input 
								name="<%=DossierDisplayTerms.SUBJECT_NAME %>" 
								cssClass="input100"
								type="text"
								label=""
								value="<%=citizen != null ? citizen.getFullName() : business != null ? business.getName() : StringPool.BLANK %>"
							>
								<aui:validator name="required"/>
								
								<aui:validator name="maxLength">
									<%= PortletPropsValues.DOSSIERMGT_DOSSIER_SUBJECT_NAME_LENGTH %>
								</aui:validator>
							</aui:input>	
						</span>
					</aui:row>
					<aui:row>
						<span class="span4">
							<liferay-ui:message key="subject-indentification"/>
						</span>
						<span class="span8">
							<aui:input 
								name="<%=DossierDisplayTerms.SUBJECT_ID %>" 
								cssClass="input100" 
								type="text"
								label=""
								value="<%=citizen != null ? citizen.getPersonalId() : business != null ? business.getIdNumber() : StringPool.BLANK %>"
							>
								<aui:validator name="required>"/>
								<aui:validator name="maxLength">
									<%= PortletPropsValues.DOSSIERMGT_DOSSIER_SUBJECT_ID_LENGTH %>
								</aui:validator>
							</aui:input>	
						</span>
					</aui:row>
					<aui:row>
						<span class="span4">
							<liferay-ui:message key="subject-address"/>
						</span>
						<span class="span8">

							<aui:input name="<%= DossierDisplayTerms.ADDRESS %>" value="<%=citizen != null ? citizen.getAddress() : business != null ? business.getAddress() : StringPool.BLANK %>" label="" type="textarea" cssClass="ara-address">
								<aui:validator name="maxLength">
									<%= PortletPropsValues.DOSSIERMGT_DOSSIER_ADDRESS_LENGTH %>
								</aui:validator>
							</aui:input>	
							
						</span>
					</aui:row>
					<aui:row>
						<span class="span12">
							<aui:row cssClass="nav-content-row hidden-option">
								<datamgt:ddr 
									depthLevel="3" 
									dictCollectionCode="<%=PortletPropsValues.DATAMGT_MASTERDATA_ADMINISTRATIVE_REGION %>"
									itemNames='<%=StringUtil.merge(new String[]{DossierDisplayTerms.CITY_CODE,DossierDisplayTerms.DISTRICT_CODE,DossierDisplayTerms.WARD_CODE}) %>'
									itemsEmptyOption="true,true,true"
									showLabel="true"
									selectedItems="<%=itemSelected %>"
									displayStyle="vertical"
									optionValueType="code"
								/>
							</aui:row>
						</span>
					</aui:row>
				</aui:col>
				<aui:col width="50">
					<aui:row>
						<span class="span4">
							<liferay-ui:message key="peronal-to-contact"/>
						</span>
						<span class="span8">
							<aui:input name="<%=DossierDisplayTerms.CONTACT_NAME %>" 
								cssClass="input100" 
								type="text"
								label=""
								value="<%=citizen != null ? citizen.getFullName() : business != null ? business.getName() : StringPool.BLANK %>"
							>
								<aui:validator name="required"/>
								<aui:validator name="maxLength">
									<%= PortletPropsValues.DOSSIERMGT_DOSSIER_CONTACT_NAME_LENGTH %>
								</aui:validator>
							</aui:input>	
						</span>
					</aui:row>
					<aui:row>
						<span class="span4">
							<liferay-ui:message key="contact-phone-number"/>
						</span>
						<span class="span8">
							<aui:input 
								name="<%=DossierDisplayTerms.CONTACT_TEL_NO %>" 
								type="text"
								label=""
								cssClass="input100"
								value="<%=citizen != null && Validator.isNotNull(citizen.getTelNo()) ? citizen.getTelNo() : business != null && Validator.isNotNull(business.getTelNo())? business.getTelNo() : StringPool.BLANK %>"
							>
								<aui:validator name="maxLength">
									<%= PortletPropsValues.DOSSIERMGT_DOSSIER_CONTACT_TEL_NO_LENGTH %>
								</aui:validator>
							</aui:input>	
						</span>
					</aui:row>
					<aui:row>
						<span class="span4">
							<liferay-ui:message key="contact-email"/>
						</span>
						<span class="span8">
							<aui:input 
								name="<%=DossierDisplayTerms.CONTACT_EMAIL %>" 
								type="text"
								label=""
								cssClass="input100"
								value="<%=citizen != null && Validator.isNotNull(citizen.getEmail()) ? citizen.getEmail() : business != null && Validator.isNotNull(business.getEmail())? business.getEmail() : StringPool.BLANK %>"
							>
								<aui:validator name="email"/>
								<aui:validator name="maxLength">
									<%= PortletPropsValues.DOSSIERMGT_DOSSIER_CONTACT_EMAIL_LENGTH %>
								</aui:validator>
							</aui:input>	
						</span>
					</aui:row>
					<aui:row>
						<span class="span4">
							<liferay-ui:message key="subject-note"/>
						</span>
						<span class="span8">
							<aui:input name="<%=DossierDisplayTerms.NOTE %>" label="" type="textarea" cssClass="ara-note">
								<aui:validator name="maxLength">
									<%= PortletPropsValues.DOSSIERMGT_DOSSIER_NOTE_LENGTH %>
								</aui:validator>
							</aui:input>	
						</span>
					</aui:row>
				</aui:col>
			</aui:row>
		</liferay-ui:panel>
		
		<liferay-ui:panel collapsible="<%= true %>" cssClass="dossier-files-content" extended="<%= true %>" id="dossierFilePanel" persistState="<%= true %>" title="dossier-files">
			<liferay-util:include page='<%= templatePath + "dossier/classical_dossier_part.jsp" %>' servletContext="<%= application %>"/>
		</liferay-ui:panel>
	
		<liferay-ui:panel collapsible="<%= true %>" cssClass="dossier-files-content" extended="<%= true %>" id="dossierFilePanel" persistState="<%= true %>" title="dossier-files">
			<liferay-util:include page='<%= templatePath + "dossier/classical_dossier_part.jsp" %>' servletContext="<%= application %>"/>
		</liferay-ui:panel>
	</liferay-ui:panel-container>
</div>


<aui:row cssClass="nav-content-row hidden">
	<aui:col width="100">
		<aui:input 
			name="<%=DossierDisplayTerms.SERVICE_NAME %>"
			cssClass=""
			disabled="<%=true %>"
			type="textarea"
			value="<%=serviceInfo != null ? serviceInfo.getServiceName() : StringPool.BLANK %>"
		/>	
	</aui:col>
</aui:row>

<aui:row cssClass="nav-content-row hidden">
	<aui:col width="100">
		<aui:input 
			name="<%=DossierDisplayTerms.SERVICE_NO %>" 
			cssClass="" 
			disabled="<%=true %>"
			type="text"
			value="<%=serviceInfo != null ? serviceInfo.getServiceNo() : StringPool.BLANK %>"
		/>	
	</aui:col>
</aui:row>


<aui:row cssClass="nav-content-row hidden">
	<aui:col width="100">
		<aui:input 
			name="<%=DossierDisplayTerms.GOVAGENCY_NAME%>"
			cssClass="" 
			disabled="<%=true %>"
			value="<%=serviceConfig != null ? serviceConfig.getGovAgencyName() : StringPool.BLANK %>"
		/>	
	</aui:col>
</aui:row>

<aui:row cssClass="nav-content-row hidden">
	<aui:col width="100">
		<aui:input 
			name="<%=DossierDisplayTerms.GOVAGENCY_CODE %>" 
			cssClass=""
			disabled="<%=true %>"
			value="<%=serviceConfig != null ? serviceConfig.getGovAgencyCode() : StringPool.BLANK %>"
		/>	
	</aui:col>
</aui:row>

<aui:script>
	AUI().ready('aui-base','aui-form-validator', function(A){
		var rules = {
			'<portlet:namespace/>cityId': {
				required: true
			} ,
			'<portlet:namespace/>districtId': {
				required: true
			} ,
			'<portlet:namespace/>wardId': {
				required: true
			} 
		};
				             	            
		var validator1 = new A.FormValidator({
			boundingBox: document.<portlet:namespace />fm,
			validateOnInput: true,
			rules: rules
		});
	});
</aui:script>