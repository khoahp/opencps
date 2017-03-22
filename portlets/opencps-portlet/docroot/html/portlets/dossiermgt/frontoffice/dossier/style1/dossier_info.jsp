
<%@page import="com.liferay.portal.kernel.util.HtmlUtil"%>
<%@page import="org.opencps.datamgt.model.DictItem"%>
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

<%@ include file="../../../init.jsp"%>

<%

	Dossier dossier = (Dossier) request.getAttribute(WebKeys.DOSSIER_ENTRY);

	ServiceConfig serviceConfig = (ServiceConfig) request.getAttribute(WebKeys.SERVICE_CONFIG_ENTRY);
	
	ServiceInfo serviceInfo = (ServiceInfo) request.getAttribute(WebKeys.SERVICE_INFO_ENTRY);
	
	String serviceName = StringPool.DASH;
	if(serviceInfo != null) {
		serviceName = serviceInfo.getServiceName();
	}
	
	String itemSelected = GetterUtil.getString(request.getAttribute(WebKeys.DICT_ITEM_SELECTED), StringPool.BLANK);
	
	String cmd = ParamUtil.getString(request, Constants.CMD);
	
	boolean isEditDossier = ParamUtil.getBoolean(request, "isEditDossier");
	
	DictItem adminAction = PortletUtil.getDictItem(PortletPropsValues.DATAMGT_MASTERDATA_GOVERNMENT_AGENCY, 
			serviceConfig.getGovAgencyCode(), 
			scopeGroupId);
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

<div class="ocps-dossier-process">
	<aui:row cssClass="header-title custom-title">
		<aui:col width="100">
			<c:choose>
				<c:when test="<%= (dossier == null) || (dossier != null && dossier.getDossierStatus().equalsIgnoreCase(PortletConstants.DOSSIER_STATUS_NEW) ) %>">
					<liferay-ui:message key="add-dossier"/>
				</c:when>
				
				<c:when test="<%= dossier != null && (
						dossier.getDossierStatus().equalsIgnoreCase(PortletConstants.DOSSIER_STATUS_PROCESSING)
						|| dossier.getDossierStatus().equalsIgnoreCase(PortletConstants.DOSSIER_STATUS_SYSTEM) %>">
					<liferay-ui:message key="dossier"/>
				</c:when>
				
				<c:otherwise>
					<liferay-ui:message key="update-dossier"/>
				</c:otherwise>
			</c:choose>
		</aui:col>
	</aui:row>
	
	<div class="dossier-info-header">
		<div class="row-fluid">
			<label class="span3"><liferay-ui:message key="service-name"/>:</label>
			<p class="span9"><%=HtmlUtil.escape(serviceName) %></p>
		</div>
		
		<div class="row-fluid">
			<label class="span3"><liferay-ui:message key="service-administration-action"/>:</label>
			<p class="span9"><%=Validator.isNotNull(adminAction) ? adminAction.getItemName(locale,true) : StringPool.BLANK %></p>
		</div>
		
		<div class="row-fluid">
			<label class="span3"><liferay-ui:message key="dossier-no"/>:</label>
			<p class="span3"><%=dossier != null ? dossier.getDossierId() : StringPool.DASH %></p>
			<label class="span3"><liferay-ui:message key="dossier-reception-no"/>:</label>
			<p class="span3"><%=dossier != null && Validator.isNotNull(dossier.getReceptionNo()) ? dossier.getReceptionNo() : StringPool.DASH %></p>
		</div>
		
		<div class="row-fluid">
			<label class="span3"><liferay-ui:message key="dossier-status"/>:</label>
			<p class="span9"><span class="red"><%=dossier != null ? PortletUtil.getDossierStatusLabel(dossier.getDossierStatus(), locale) : "" %></span></p>
		</div>
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
	
	<aui:row cssClass="nav-content-row">
		<aui:col width="100">
			<aui:input 
				name="<%=DossierDisplayTerms.SUBJECT_NAME %>" 
				cssClass=""
				type="text"
				value="<%=citizen != null ? citizen.getFullName() : business != null ? business.getName() : StringPool.BLANK %>"
			>
				<aui:validator name="required"/>
				
				<aui:validator name="maxLength">
					<%= PortletPropsValues.DOSSIERMGT_DOSSIER_SUBJECT_NAME_LENGTH %>
				</aui:validator>
			</aui:input>	
		</aui:col>
	</aui:row>
	
	<aui:row cssClass="nav-content-row">
		<aui:col width="100">
				<aui:input 
					name="<%=DossierDisplayTerms.SUBJECT_ID %>" 
					cssClass="" 
					type="text"
					value="<%=citizen != null ? citizen.getPersonalId() : business != null ? business.getIdNumber() : StringPool.BLANK %>"
				>
					<aui:validator name="required>"/>
					<aui:validator name="maxLength">
						<%= PortletPropsValues.DOSSIERMGT_DOSSIER_SUBJECT_ID_LENGTH %>
					</aui:validator>
				</aui:input>	
			</aui:col>
		</aui:row>
	
	<aui:row cssClass="nav-content-row">
		<aui:col width="100">
			<p class=address-hint><liferay-ui:message key="address-hint"/></p>
			<aui:input 
				name="<%=DossierDisplayTerms.ADDRESS %>" 
				cssClass="" 
				type="text"
				value="<%=citizen != null ? citizen.getAddress() : business != null ? business.getAddress() : StringPool.BLANK %>"
			>
				<aui:validator name="required"/>
				<aui:validator name="maxLength">
					<%= PortletPropsValues.DOSSIERMGT_DOSSIER_ADDRESS_LENGTH %>
				</aui:validator>
			</aui:input>	
		</aui:col>
	</aui:row>
	
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
	
	<aui:row cssClass="nav-content-row">
		<aui:col width="100">
			<aui:input name="<%=DossierDisplayTerms.CONTACT_NAME %>" 
				cssClass="" 
				type="text"
				value="<%=citizen != null ? citizen.getFullName() : business != null ? business.getName() : StringPool.BLANK %>"
			>
				<aui:validator name="required"/>
				<aui:validator name="maxLength">
					<%= PortletPropsValues.DOSSIERMGT_DOSSIER_CONTACT_NAME_LENGTH %>
				</aui:validator>
			</aui:input>	
		</aui:col>
	</aui:row>
	
	<aui:row cssClass="nav-content-row">
		<aui:col width="100">
			<aui:input 
				name="<%=DossierDisplayTerms.CONTACT_TEL_NO %>" 
				type="text"
				value="<%=citizen != null && Validator.isNotNull(citizen.getTelNo()) ? citizen.getTelNo() : business != null && Validator.isNotNull(business.getTelNo())? business.getTelNo() : StringPool.BLANK %>"
			>
				<aui:validator name="maxLength">
					<%= PortletPropsValues.DOSSIERMGT_DOSSIER_CONTACT_TEL_NO_LENGTH %>
				</aui:validator>
			</aui:input>	
		</aui:col>
	</aui:row>
	
	<aui:row cssClass="nav-content-row">
		<aui:col width="100">
			<aui:input 
				name="<%=DossierDisplayTerms.CONTACT_EMAIL %>" 
				type="text"
				value="<%=citizen != null && Validator.isNotNull(citizen.getEmail()) ? citizen.getEmail() : business != null && Validator.isNotNull(business.getEmail())? business.getEmail() : StringPool.BLANK %>"
			>
				<aui:validator name="email"/>
				<aui:validator name="maxLength">
					<%= PortletPropsValues.DOSSIERMGT_DOSSIER_CONTACT_EMAIL_LENGTH %>
				</aui:validator>
			</aui:input>	
		</aui:col>
	</aui:row>
	
	<aui:row cssClass="nav-content-row">
		<aui:col width="100">
			<aui:input name="<%=DossierDisplayTerms.NOTE %>" type="textarea">
				<aui:validator name="maxLength">
					<%= PortletPropsValues.DOSSIERMGT_DOSSIER_NOTE_LENGTH %>
				</aui:validator>
			</aui:input>	
		</aui:col>
	</aui:row>
</div>

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