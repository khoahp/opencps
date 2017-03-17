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
<%@page import="org.opencps.util.PortletUtil"%>
<%@page import="org.opencps.processmgt.model.impl.ProcessStepDossierPartImpl"%>
<%@page import="org.opencps.processmgt.service.ProcessStepDossierPartLocalServiceUtil"%>
<%@page import="org.opencps.processmgt.model.ProcessStepDossierPart"%>
<%@page import="org.opencps.processmgt.service.StepAllowanceLocalServiceUtil"%>
<%@page import="org.opencps.util.PortletConstants"%>
<%@page import="org.opencps.dossiermgt.model.DossierPart"%>
<%@page import="org.opencps.processmgt.util.ProcessUtils"%>
<%@page import="com.liferay.portal.kernel.process.ProcessUtil"%>
<%@page import="com.liferay.portal.model.Role"%>
<%@page import="org.opencps.processmgt.model.impl.StepAllowanceImpl"%>
<%@page import="java.util.Collections"%>
<%@page import="org.opencps.processmgt.model.StepAllowance"%>
<%@page import="org.opencps.processmgt.model.ServiceProcess"%>
<%@page import="org.opencps.servicemgt.search.ServiceDisplayTerms"%>
<%@page import="org.opencps.processmgt.model.ProcessStep"%>
<%@page import="org.opencps.datamgt.model.DictItem"%>
<%@ include file="../init.jsp" %>

<%
	String redirectURL = ParamUtil.getString(request, "redirectURL");

	String backURL = ParamUtil.getString(request, "backURL");
	
	ProcessStep step = (ProcessStep) request.getAttribute(WebKeys.PROCESS_STEP_ENTRY);
	
	ServiceProcess serviceProcess  = (ServiceProcess) request.getAttribute(WebKeys.SERVICE_PROCESS_ENTRY);
	
	long dossierTemplateId = 0;
	
	if (Validator.isNotNull(serviceProcess)) {
		dossierTemplateId = serviceProcess.getDossierTemplateId();
	}
	
	List<DossierPart> dossiers = ProcessUtils.getDossierParts(dossierTemplateId, PortletConstants.DOSSIER_TYPE_OWN_RECORDS);
	
	List<DossierPart> dossiersResults = new ArrayList<DossierPart>();
	
	List<DossierPart> dossiersResult = ProcessUtils.getDossierParts(dossierTemplateId, PortletConstants.DOSSIER_PART_TYPE_RESULT);
	
	List<DossierPart> dossiersResultMulti = ProcessUtils.getDossierParts(dossierTemplateId, PortletConstants.DOSSIER_PART_TYPE_MULTIPLE_RESULT);
	
	
	if(dossiersResult!=null && !dossiersResult.isEmpty()){
		dossiersResults.addAll(dossiersResult);
	}
	
	if(dossiersResultMulti!=null && !dossiersResultMulti.isEmpty()){
		dossiersResults.addAll(dossiersResultMulti);
	}
	
	List<StepAllowance> stepAllowances = Collections.emptyList();
	
	List<ProcessStepDossierPart> dossierSel = Collections.emptyList();


	int[] stepAllowanceIndexs = null;
	int[] dossierIndexs = null;

	String stepAllowancesIndexesParam = ParamUtil.getString(request, "stepAllowanceIndexs");
	
	String dossierIndexesParam = ParamUtil.getString(request, "dossierIndexs");
	
	// Add StepIndex

	if (Validator.isNotNull(stepAllowancesIndexesParam)) {
		stepAllowances = new ArrayList<StepAllowance>();

		stepAllowanceIndexs = StringUtil.split(stepAllowancesIndexesParam, 0);

		for (int stepAllowancesIndexe : stepAllowanceIndexs) {
			stepAllowances.add(new StepAllowanceImpl());
		}
	}
	else {

		if (Validator.isNotNull(step)) {
			stepAllowances = StepAllowanceLocalServiceUtil.getByProcessStep(step.getProcessStepId());

			stepAllowanceIndexs = new int[stepAllowances.size()];

			for (int i = 0; i < stepAllowances.size() ; i++) {
				stepAllowanceIndexs[i] = i;
			}
		}

		if (stepAllowances.isEmpty()) {
			stepAllowances = new ArrayList<StepAllowance>();

			stepAllowances.add(new StepAllowanceImpl());

			stepAllowanceIndexs = new int[] {0};
		}

		if (stepAllowanceIndexs == null) {
			stepAllowanceIndexs = new int[0];
		}
	}
	
	// Add DossierIndex
	
	if (Validator.isNotNull(dossierIndexesParam)) {
		dossierSel = new ArrayList<ProcessStepDossierPart>();

		dossierIndexs = StringUtil.split(dossierIndexesParam, 0);

		for (int i : dossierIndexs) {
			stepAllowances.add(new StepAllowanceImpl());
		}

	} else {
		if (Validator.isNotNull(step)) {
			dossierSel = ProcessStepDossierPartLocalServiceUtil.getByStep(step.getProcessStepId());

			dossierIndexs = new int[dossierSel.size()];

			for (int i = 0; i < dossierSel.size() ; i++) {
				dossierIndexs[i] = i;
			}
		}

		if (dossierSel.isEmpty()) {
			dossierSel = new ArrayList<ProcessStepDossierPart>();

			dossierSel.add(new ProcessStepDossierPartImpl());

			dossierIndexs = new int[] {0};
		}

		if (dossierIndexs == null) {
			dossierIndexs = new int[0];
		}
	}
	
	String dictStatusCode = StringPool.BLANK;
	String dictSubStatusCode = StringPool.BLANK;
	
	DictItem itemStatus = null;
	DictItem itemSubStatus = null;
	
	if(step != null) {
		itemStatus = PortletUtil.getDictItem("DOSSIER_STATUS", step.getDossierStatus(), scopeGroupId);
		
		itemSubStatus = PortletUtil.getDictItem("DOSSIER_SUB_STATUS", step.getDossierSubStatus(), scopeGroupId);
		
		if(Validator.isNotNull(itemStatus)) {
			dictStatusCode = itemStatus.getItemCode();
		}
		
		if(Validator.isNotNull(itemSubStatus)) {
			dictSubStatusCode = itemSubStatus.getItemCode();
		}

	}
%>

<liferay-ui:header
	backURL="<%= backURL %>"
	title='<%= (Validator.isNull(step)) ? "add-step" : "update-step" %>'
/>

<portlet:actionURL name="updateProcessStep" var="updateProcessStepURL" windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>"/>

<div class="opencps-bound-wrapper sub-screen-corner">
	<aui:form name="processStepFm" method="POST" action="<%= updateProcessStepURL %>" cssClass="bg-white">
	
		<aui:model-context bean="<%= step %>" model="<%= ProcessStep.class %>" />
		
		<aui:input name="redirectURL" type="hidden" value="<%= redirectURL %>"/>
		<aui:input name="returnURL" type="hidden" value="<%= currentURL %>"/>
		
		<aui:input name="<%= ServiceDisplayTerms.GROUP_ID %>" type="hidden" 
			value="<%= scopeGroupId%>"/>
		<aui:input name="<%= ServiceDisplayTerms.COMPANY_ID %>" type="hidden" 
			value="<%= company.getCompanyId()%>"/>
			
		<aui:input name="serviceProcessId" type="hidden" 
			value="<%= Validator.isNotNull(serviceProcess) ? serviceProcess.getPrimaryKey() : StringPool.BLANK %>"/>
		
		<aui:input name="processStepId" type="hidden" 
			value="<%= Validator.isNotNull(step) ? step.getProcessStepId() : StringPool.BLANK %>"/>
	
		<aui:row>
			<aui:col width="50">
				<label><liferay-ui:message key="step-name"/></label>
				<aui:input name="stepName" inlineLabel="false" label="" inlineField="false"></aui:input>
			</aui:col>
			<aui:col width="50">
				<label><liferay-ui:message key="sequence-no"/></label>
				<aui:input name="sequenceNo" inlineLabel="false" label=""></aui:input>
			</aui:col>
		</aui:row>
		<aui:row>
			<aui:col width="50">
				
				<datamgt:ddr 
					depthLevel="1" 
					dictCollectionCode="DOSSIER_STATUS" 
					showLabel="<%=true%>"
					emptyOptionLabels="dossier-status"
					itemsEmptyOption="true"
					itemNames="dossierStatus"
					selectedItems="<%=dictStatusCode%>"
					optionValueType="code"
				/>
				
			</aui:col>
			
			<aui:col width="50">
				
				<datamgt:ddr 
					depthLevel="1" 
					dictCollectionCode="DOSSIER_SUB_STATUS" 
					showLabel="<%=true%>"
					emptyOptionLabels="dossier-sub-status"
					itemsEmptyOption="true"
					itemNames="dossierSubStatus"
					selectedItems="<%=dictSubStatusCode%>"
					optionValueType="code"
				/>
				
			</aui:col>
	
		</aui:row>
		
		<aui:row>
			<aui:col width="50">
				<aui:input name="processStepNo" inlineField="false"/>		
			</aui:col>
			<aui:col width="50">
				<aui:input name="daysDuration" inlineField="false"/>		
			</aui:col>
		</aui:row>
		
		<aui:row>
			<aui:col width="50">
				<aui:select name="referenceDossierPartId" showEmptyOption="true">
					<%
						for (DossierPart dossier : dossiers) {
					%>
						<aui:option value="<%= dossier.getDossierpartId() %>">
							<%= dossier.getPartName() %>
						</aui:option>
					<%
						}
					%>
				</aui:select>
			</aui:col>
			<aui:col width="50">
				&nbsp;
			</aui:col>
		</aui:row>
		
		<label class="bold"><liferay-ui:message key="dossier-part"/></label>
	
		<div id="dossier-part">
			<%
				for (int i = 0; i < dossierIndexs.length; i++) {
					
					int dossierIndex = dossierIndexs[i];
					
					ProcessStepDossierPart stepDossier = dossierSel.get(i);
	
			%>
				<div class="lfr-form-row lfr-form-row-inline">
					<div class="row-fields">
						<aui:select id='<%= "dossierPart" + dossierIndex %>' inlineField="<%= true %>" label="" name='<%= "dossierPart" + dossierIndex %>' showEmptyOption="true">
							<%
								for (DossierPart dossier : dossiersResults) {
							%>
								<aui:option selected="<%=  Validator.equals(stepDossier.getDossierPartId(), dossier.getDossierpartId())  %>" value="<%= dossier.getDossierpartId() %>">
									<%= dossier.getPartName() %>
								</aui:option>
							<%
								}
							%>
						</aui:select>
						<aui:input checked="<%= stepDossier.getReadOnly() %>" 
						fieldParam='<%= "readOnly" + dossierIndex %>' 
						id='<%= "readOnly" + dossierIndex %>' 
						label="read-only" inlineField="<%= true %>" 
						name='<%= "partReadOnly" + dossierIndex %>' 
						type="checkbox"/>
					</div>
				</div>
			
			<%
				}
			%>
		</div>
		
		<label class="bold"><liferay-ui:message key="result-action"/></label>
		
		<div id="step-allowance">
		
			<%
				for (int i = 0; i < stepAllowanceIndexs.length; i++) {
					int stepAllowanceIndex = stepAllowanceIndexs[i];
					
					StepAllowance stepAlo = stepAllowances.get(i);
			%>
		
				<div class="lfr-form-row lfr-form-row-inline">
					<div class="row-fields">
						<aui:input name='<%= "stepAllowanceId" + stepAllowanceIndex %>' type="hidden" value="<%= stepAlo.getStepAllowanceId() %>"/>
						<aui:select id='<%= "roleId" + stepAllowanceIndex %>' inlineField="<%= true %>" name='<%= "roleId" + stepAllowanceIndex %>' label="" showEmptyOption="true">
							<%
								List<Role> roles = ProcessUtils.getRoles(renderRequest);
								
								for (Role role : roles) {
							%>
									<aui:option selected="<%= stepAllowances.get(i).getRoleId() == role.getRoleId() %>" value="<%= role.getPrimaryKey() %>"><%= role.getName() %></aui:option>
							<%
								}
							%>
						</aui:select>
	
						<aui:input checked="<%= stepAllowances.get(i).getReadOnly() %>" fieldParam='<%= "readOnly" + stepAllowanceIndex %>' id='<%= "readOnly" + stepAllowanceIndex %>' label="read-only" inlineField="<%= true %>" name='<%= "readOnly" + stepAllowanceIndex %>' type="checkbox"/>
					</div>
				</div>
			
			<%
				}
			%>
		</div>
			
		<aui:script use="liferay-auto-fields">
			new Liferay.AutoFields(
				{
					contentBox: '#step-allowance',
					fieldIndexes: '<portlet:namespace />stepAllowanceIndexs',
					namespace: '<portlet:namespace />'
				}
			).render();
		</aui:script>
	
		
		<aui:script use="liferay-auto-fields">
			new Liferay.AutoFields(
				{
					contentBox: '#dossier-part',
					fieldIndexes: '<portlet:namespace />dossierIndexs',
					namespace: '<portlet:namespace />'
				}
			).render();
		</aui:script>
	
		<aui:row>
			<aui:col width="100">
				<aui:input name="externalAppUrl" cssClass="input100"/>
			</aui:col>
		</aui:row>
		
		<aui:row cssClass="row-fluid">
			<aui:button-row >
				<aui:button name="save" type="submit" value="<%= Validator.isNotNull(step) ? Constants.ADD : Constants.UPDATE %>"/>
				<aui:button type="cancel" name="cancel" />
			</aui:button-row>
		</aui:row>
		
	</aui:form>
</div>


<aui:script use="aui-base,aui-io-request">


	AUI().ready(function(A){
		A.one('#<portlet:namespace/>saveForm').on('click', function(event) {
			var A = AUI();
			var url = '<%= updateProcessStepURL.toString() %>';
			A.io.request(
				url,
				{
					method: 'POST',
					form: {
						id: '<portlet:namespace/>processStepFm'
					},
					on: {
						success: function() {
							Liferay.Util.getOpener().refreshPortlet();
							Liferay.Util.getOpener().closePopup('addstep');
						},
						error:function() {
							alert("update-fail");
						}
					}
				}
			);
		});
	});
	
</aui:script>

