
<%@page import="org.opencps.paymentmgt.service.PaymentFileLocalServiceUtil"%>
<%@page import="org.opencps.paymentmgt.model.PaymentFile"%>
<%@page import="org.opencps.dossiermgt.service.DossierFileLocalServiceUtil"%>
<%@page import="org.opencps.dossiermgt.model.DossierFile"%>
<%@page import="org.opencps.util.DateTimeUtil"%>
<%@page import="org.opencps.dossiermgt.util.DossierMgtUtil"%>
<%@page import="org.opencps.dossiermgt.service.DossierTemplateLocalServiceUtil"%>
<%@page import="org.opencps.dossiermgt.model.DossierTemplate"%>
<%@page import="org.opencps.dossiermgt.service.DossierPartLocalServiceUtil"%>
<%@page import="org.opencps.servicemgt.service.ServiceInfoLocalServiceUtil"%>
<%@page import="org.opencps.dossiermgt.model.DossierPart"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.opencps.dossiermgt.service.DossierLogLocalServiceUtil"%>
<%@page import="org.opencps.dossiermgt.model.DossierLog"%>
<%@page import="java.util.List"%>
<%@page import="javax.portlet.PortletRequest"%>
<%@page import="com.liferay.portlet.PortletURLFactoryUtil"%>
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
	
	String[] actors = new String[] {};
	
	String[] requestCommands = new String[]{StringPool.APOSTROPHE + WebKeys.DOSSIER_LOG_RESUBMIT_REQUEST + StringPool.APOSTROPHE, 
											StringPool.APOSTROPHE + WebKeys.DOSSIER_LOG_PAYMENT_REQUEST + StringPool.APOSTROPHE};
	
	List<DossierLog> dossierLogs = new ArrayList<DossierLog>();
	
	if (Validator.isNotNull(dossier)) {
		dossierLogs =	DossierLogLocalServiceUtil.findRequiredProcessDossier(dossier.getDossierId(), actors, requestCommands);
	}
	List<DossierPart> dossierPartsLevel1 = new ArrayList<DossierPart>();
	
	ServiceInfo info = null;
	String serviceInfoName = StringPool.BLANK;
	
	DossierTemplate dossierTemplate = null;
	
	try {
		
		info = ServiceInfoLocalServiceUtil.getServiceInfo(dossier.getServiceInfoId());
		serviceInfoName = info.getServiceName();
		
		dossierTemplate = DossierTemplateLocalServiceUtil.fetchDossierTemplate(serviceConfig.getDossierTemplateId());
		
	} catch (Exception e) {}
		
	if(dossierTemplate != null){
		
		try{
			List<DossierPart> lstTmp1 = DossierPartLocalServiceUtil.getDossierPartsByT_P_PT(dossierTemplate.getDossierTemplateId(), 0, PortletConstants.DOSSIER_PART_TYPE_RESULT);
			if(lstTmp1 != null){
				dossierPartsLevel1.addAll(lstTmp1);
			}
		}catch(Exception e){}
		
		try{
			List<DossierPart> lstTmp2 = DossierPartLocalServiceUtil.getDossierPartsByT_P_PT(dossierTemplate.getDossierTemplateId(), 0, PortletConstants.DOSSIER_PART_TYPE_MULTIPLE_RESULT);
			if(lstTmp2 != null){
				dossierPartsLevel1.addAll(lstTmp2);
			}
		}catch(Exception e){}
		
		
	}

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

<%
	String cmdDossier = ParamUtil.get(request, "cmdDossier", "new");

	String title = "add-dossier";

	if (cmdDossier.contentEquals("view")) {
		title = "view-dossier";
	}

	if (cmdDossier.contentEquals("update")) {
		title = "update-dossier";
	}
	
%>


<portlet:renderURL var="viewHistoryURL" windowState="<%=LiferayWindowState.POP_UP.toString()%>">
	<portlet:param name="mvcPath" value='<%= templatePath + "dossier/classical_result.jsp" %>'/>
</portlet:renderURL>

<div class="fe-dossier-wrapper" style="padding: 10px;">
	<div class="head-title">
		<liferay-ui:message key='<%= title %>'/>
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
							<%= Validator.isNotNull(dossier) ? LanguageUtil.get(locale, dossier.getDossierStatus())  : "-----" %> 
							<c:if test="<%= true %>"> <span style="font-weight: bold;">(<aui:a cssClass="dossier-his" href="#" id="viewHistory" label="view-history">)</aui:a></span></c:if>
							
						</span>
					</aui:row>
					
				</aui:col>
			</aui:row>
			
			<hr style="border-top: dotted 1px;" />
			
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
		

		
		<%!
			private boolean _checkDossierPart(List<DossierPart> dps, Dossier dossier) {
			
			boolean check = false;
			
				if (Validator.isNotNull(dossier)) {
				
					for(DossierPart dossierPart : dps){
						DossierFile dossierFile = null;
						
						try{
							dossierFile = DossierFileLocalServiceUtil.getDossierFileInUse(dossier.getDossierId(), dossierPart.getDossierpartId());
								
						}catch(Exception e){
							continue;
						}
							
						if(dossierFile.getFileEntryId() != 0 || dossierFile.getSyncStatus() == PortletConstants.DOSSIER_FILE_SYNC_STATUS_SYNCSUCCESS){
							check = true;
							
							continue;
						}
							
					}
				
				}
				
				return check;
			
			}
		
		%>

		<c:if test="<%= _checkDossierPart(dossierPartsLevel1, dossier) %>">
			
			<div style="margin-top: 10px;">&nbsp;</div>
			
			<liferay-ui:panel collapsible="<%= true %>" cssClass="dossier-files-result" extended="<%= true %>" id="dossierFilePanel" persistState="<%= true %>" title="dossier-files">
				<liferay-util:include page='<%= templatePath + "dossier/classical_results.jsp" %>' servletContext="<%= application %>"/>
			</liferay-ui:panel>
		</c:if>
		
		<div style="margin-top: 10px;">&nbsp;</div>
		
		<liferay-ui:panel collapsible="<%= true %>" cssClass="dossier-files-content" extended="<%= true %>" id="dossierFilePanel" persistState="<%= true %>" title="dossier-files">
			<liferay-util:include page='<%= templatePath + "dossier/classical_dossier_part.jsp" %>' servletContext="<%= application %>"/>
		</liferay-ui:panel>
		
		<div class="dossier-payment-info" style="margin-top: 20px;">
			<c:if test="<%= dossierLogs != null && !dossierLogs.isEmpty() %>">
			
			
				<aui:row cssClass="pd_t20">
					
						<%
							int count = 1;
							int flagPayment = 0; 
							int flagResubmit = 0;
							for(DossierLog dossierLog : dossierLogs){
								
								%>
									<aui:row cssClass='<%=count <  dossierLogs.size() ? "bottom-line pd_b20 pd_t20" : "pd_t20" %>'>

										<aui:col width="100">
											<span class="span4 bold">
												<c:if test='<%=dossierLog.getRequestCommand().contains("paymentRequest") && flagPayment == 0  %>'>
													<i class="fa fa-bullhorn fa-lg" style="font-size: 100%; margin: 0px 5px;"></i>
													<liferay-ui:message key="msg-request-command-payment"/>
												</c:if>
												<c:if test='<%=dossierLog.getRequestCommand().contains("resubmitRequest") && flagPayment == 0 %>'>
													<i class="fa fa-bullhorn fa-lg" style="font-size: 100%; margin: 0px 5px;"></i>
													<liferay-ui:message key="msg-request-command-update-doissier"/>
												</c:if>
											</span>
											<span class="span8">
												<%= dossierLog.getMessageInfo() %>
												<c:if test='<%=dossierLog.getRequestCommand().contains("paymentRequest")   && flagPayment == 0  %>'>
													<%
														List<PaymentFile> lsPayment = new ArrayList<PaymentFile>();
														if (Validator.isNotNull(dossier)) {
															try {
																lsPayment = PaymentFileLocalServiceUtil.getPaymentFileByD_(dossier.getDossierId());
															} catch (Exception e) {}
														}
													%>
													
													<c:if test="<%= lsPayment.size() != 0 %>">
														<ul class="ds-payment-ls">
														<%
															for (PaymentFile pf : lsPayment) {
														%>
															<li> <strong> <%= pf.getAmount() %> </strong> <liferay-ui:message key="VND"/> <span style="font-style: italic;"> <%= pf.getPaymentName() %> </span> </li>
														<%
															}
														%>
														
														</ul>
													</c:if>
													
												</c:if>
												<c:if test='<%=dossierLog.getRequestCommand().contains("resubmitRequest") %>'>
													<div class="ds-request-ls"> 
														<%= dossierLog.getMessageInfo() %>
													</div>
												</c:if>
												
											</span>
										</aui:col>
									</aui:row>
									
								<%
								flagPayment = flagPayment + 1;
								flagResubmit = flagResubmit + 1;
								count ++;
							}
						%>
					
				</aui:row>
			</c:if>
		</div>
		
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
		
		
		var openHistoryPopup = A.one("#<portlet:namespace/>viewHistory")
		openHistoryPopup.on('click', function() {
			var portletURL = Liferay.PortletURL.createURL('<%= PortletURLFactoryUtil.create(request, WebKeys.DOSSIER_MGT_PORTLET, themeDisplay.getPlid(), PortletRequest.RENDER_PHASE) %>');
			portletURL.setParameter("mvcPath", "/html/portlets/dossiermgt/frontoffice/dossier/classical_history.jsp");
			portletURL.setWindowState("<%=LiferayWindowState.POP_UP.toString()%>"); 
			portletURL.setPortletMode("normal");
			portletURL.setParameter("dossierId", '<%= Validator.isNotNull(dossier) ? String.valueOf(dossier.getDossierId()) : "0" %>');
			openDialog(portletURL.toString() , 'dossier-history', Liferay.Language.get("dossier-history"));
		});
		
	});
</aui:script>
