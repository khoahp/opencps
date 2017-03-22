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

<%@page import="org.opencps.usermgt.service.WorkingUnitLocalServiceUtil"%>
<%@page import="org.opencps.usermgt.model.WorkingUnit"%>
<%@page import="com.liferay.portal.model.Organization"%>
<%@page import="com.liferay.portal.service.OrganizationLocalServiceUtil"%>
<%@page import="org.opencps.postal.service.PostalConfigLocalServiceUtil"%>
<%@page import="org.opencps.postal.model.PostalConfig"%>
<%@page import="org.opencps.postal.search.PostalConfigDisplayTerms"%>
<%@page import="org.opencps.postal.model.impl.PostalConfigImpl"%>
<%@page import="org.opencps.datamgt.model.DictItem"%>
<%@page import="org.opencps.util.DictItemUtil"%>
<%@page import="org.opencps.util.PortletPropsValues"%>

<%@ include file="../init.jsp"%>

<%
	String backURL = ParamUtil.getString(request, "backURL");
	long postalConfigId = ParamUtil.getLong(request, PostalConfigDisplayTerms.POSTAL_CONFIG_ID, 0L);
	PostalConfig postalConfig = new PostalConfigImpl();
	
	if(postalConfigId > 0){
		try {
			postalConfig = PostalConfigLocalServiceUtil.fetchPostalConfig(postalConfigId);
		}
		catch (Exception e) {
			
		}
	}
	List<WorkingUnit> wunits = WorkingUnitLocalServiceUtil.getWorkingUnits(scopeGroupId);
	
	DictItemUtil dicItemUtil = new DictItemUtil();
	
	List<DictItem> postalGates = dicItemUtil.getListDictItemBy(themeDisplay.getScopeGroupId(), PortletPropsValues.DM_POSTAL_GATE);
%>	

<div class="opencps-bound-wrapper pd20 default-box-shadow">

	<c:choose>
		<c:when test="<%= true %>">
			

			<portlet:actionURL var="updatePostalConfigURL"
				name="updatePostalConfig">
				<portlet:param name="returnURL" value="<%=currentURL%>" />
				<portlet:param name="backURL" value="<%=backURL%>" />
			</portlet:actionURL>

			<liferay-ui:success key="update-postal-config-success"
				message="update-postal-config-success" />

			<liferay-ui:error key="update-postal-config-error"
				message="update-postal-config-error" />

			<aui:form action="<%=updatePostalConfigURL.toString()%>"
				method="post" name="fm" id="fm">
				
				<aui:input type="hidden"
					id="<%=PostalConfigDisplayTerms.POSTAL_CONFIG_ID%>"
					name="<%=PostalConfigDisplayTerms.POSTAL_CONFIG_ID%>"
					value="<%=String.valueOf(postalConfigId)%>" />

				<aui:row>
					<aui:col width="50">
						<aui:select
							id="<%=PostalConfigDisplayTerms.GOV_AGENCY_ORGANIZATION_ID%>"
							onChange="loadPaymentConfig()"
							name="<%=PostalConfigDisplayTerms.GOV_AGENCY_ORGANIZATION_ID%>"
							label="gov-agency-organization-id">
							<%
							boolean selected = false;
							
							for (WorkingUnit wunit : wunits) {

								if (wunit.getMappingOrganisationId() == postalConfig
										.getGovAgencyOrganizationId()) {
									selected = true;
								}else{
									selected = false;
								}
							%>
								<aui:option selected="<%=selected%>"
									value="<%=wunit.getMappingOrganisationId()%>"><%=wunit.getName()%></aui:option>
								
							<%
									
							}
							%>
						</aui:select>
					</aui:col>
					
					<aui:col width="50">
						<aui:select
							id="<%=PostalConfigDisplayTerms.POSTAL_GATETYPE%>"
							onChange="loadPostalConfig()"
							name="<%=PostalConfigDisplayTerms.POSTAL_GATETYPE%>"
							label="postal-gate-type">
							<%
							boolean postalGateSelect = false;
							for (DictItem postalGate : postalGates) {
								
								if(postalGate.getItemCode().equals(postalConfig.getPostalGateType())){
									postalGateSelect = true;
								}else{
									postalGateSelect = false;
								}
									
							%>
								<aui:option selected="<%=postalGateSelect %>"
									value="<%=postalGate.getItemCode()%>"><%=postalGate.getItemName()%></aui:option>
							<%
								}
							%>
						</aui:select>
					</aui:col>
				</aui:row>
				
				<aui:row>
					<aui:col>
						<aui:input 
							id="<%=PostalConfigDisplayTerms.STATUS%>"
							name="<%=PostalConfigDisplayTerms.STATUS%>"
							label="active"
							type="checkbox"
							
							>
							
						</aui:input>
					</aui:col>
				</aui:row>

				<aui:row>
					<aui:col width="50">
						<aui:input value="<%=postalConfig.getPostalCustomerCode()%>"
							id="<%=PostalConfigDisplayTerms.POSTAL_CUSTOMERCODE%>"
							cssClass="input100"
							name="<%=PostalConfigDisplayTerms.POSTAL_CUSTOMERCODE%>" type="text"
							label="customer-code">
							<aui:validator name="required"></aui:validator>
						</aui:input>
					</aui:col>
					<aui:col width="50">
						<aui:input value="<%=postalConfig.getPostalTokenCode()%>"
							id="<%=PostalConfigDisplayTerms.POSTAL_TOKENCODE%>"
							cssClass="input100"
							name="<%=PostalConfigDisplayTerms.POSTAL_TOKENCODE%>"
							type="text" label="token-code">
							<aui:validator name="required"></aui:validator>
						</aui:input>
					</aui:col>
				</aui:row>

				<aui:row>
					<aui:col width="50">
						<aui:input value="<%=postalConfig.getPostalDomain()%>"
							id="<%=PostalConfigDisplayTerms.POSTAL_DOMAIN%>"
							cssClass="input100"
							name="<%=PostalConfigDisplayTerms.POSTAL_DOMAIN%>"
							label="postal-domain">
							<aui:validator name="required"></aui:validator>
							<aui:validator name="url" />
						</aui:input>
					</aui:col>
					
				</aui:row>

				

				<aui:row>
					<aui:col>
						<aui:button name="submit" type="submit" value="submit" />
					</aui:col>
				</aui:row>

			</aui:form>
			
			<portlet:resourceURL var="resourceURL"/>
			<script type="text/javascript">
				loadPostalConfig();
				function loadPostalConfig() {
					AUI().use('aui-io-request', function(A){
						
						var govAgencyOrganizationId = A.one('#<portlet:namespace /><%= PostalConfigDisplayTerms.GOV_AGENCY_ORGANIZATION_ID %>').val();
						var postalGateType = A.one('#<portlet:namespace /><%= PostalConfigDisplayTerms.POSTAL_GATETYPE %>').val();
					
						A.io.request('<%=resourceURL.toString()%>',{
							
							method: 'post',
							data: {
								<portlet:namespace /><%= PostalConfigDisplayTerms.GOV_AGENCY_ORGANIZATION_ID %>: govAgencyOrganizationId,
								<portlet:namespace /><%= PostalConfigDisplayTerms.POSTAL_GATETYPE %>: postalGateType
							},
							dataType:'json',
							on:{
								success:function (){
									
									var postalConfig = this.get('responseData');
									
									A.Array.each(postalConfig,function(obj,idx){
										
										if(obj){
											
											if (obj.<%= PostalConfigDisplayTerms.POSTAL_CONFIG_ID %>){
					                 			A.one('#<portlet:namespace /><%= PostalConfigDisplayTerms.POSTAL_CONFIG_ID %>').set('value', obj.<%= PostalConfigDisplayTerms.POSTAL_CONFIG_ID %>);
				                 			
											}else{
				                 				A.one('#<portlet:namespace /><%= PostalConfigDisplayTerms.POSTAL_CONFIG_ID %>').set('value', '0');
											}

				                 			if (obj.<%= PostalConfigDisplayTerms.POSTAL_DOMAIN %>){
					                 			A.one('#<portlet:namespace /><%= PostalConfigDisplayTerms.POSTAL_DOMAIN %>').set('value', obj.<%= PostalConfigDisplayTerms.POSTAL_DOMAIN %>);
				                 			
				                 			}else{
				                 				A.one('#<portlet:namespace /><%= PostalConfigDisplayTerms.POSTAL_DOMAIN %>').set('value', '');
											}
				                 			
				                 			if (obj.<%= PostalConfigDisplayTerms.POSTAL_TOKENCODE %>){
					                 			A.one('#<portlet:namespace /><%= PostalConfigDisplayTerms.POSTAL_TOKENCODE %>').set('value', obj.<%= PostalConfigDisplayTerms.POSTAL_TOKENCODE %>);
				                 			}else{
				                 				A.one('#<portlet:namespace /><%= PostalConfigDisplayTerms.POSTAL_TOKENCODE %>').set('value', '');
				                 			}
				                 			
				                 			if (obj.<%= PostalConfigDisplayTerms.POSTAL_CUSTOMERCODE %>){
					                 			A.one('#<portlet:namespace /><%= PostalConfigDisplayTerms.POSTAL_CUSTOMERCODE %>').set('value', obj.<%= PostalConfigDisplayTerms.POSTAL_CUSTOMERCODE %>);
				                 			}else{
				                 				A.one('#<portlet:namespace /><%= PostalConfigDisplayTerms.POSTAL_CUSTOMERCODE %>').set('value', '');
				                 			}
				                 			if(obj.<%= PostalConfigDisplayTerms.POSTAL_GATETYPE %>){
				                 				A.one('#<portlet:namespace /><%= PostalConfigDisplayTerms.POSTAL_GATETYPE %>').set('selected', obj.<%= PostalConfigDisplayTerms.POSTAL_GATETYPE %>);
				                 			}
				                 			if (obj.<%= PostalConfigDisplayTerms.STATUS %>){
				                 				
				                 			A.one('#<portlet:namespace /><%= PostalConfigDisplayTerms.STATUS %>').set('value', obj.<%= PostalConfigDisplayTerms.STATUS %>);
			                 				A.one('#<portlet:namespace /><%= PostalConfigDisplayTerms.STATUS+PostalConfigDisplayTerms.CHECKBOX %>').set('checked', obj.<%= PostalConfigDisplayTerms.STATUS %>);

				                 			}else{
				                 				
				                 				A.one('#<portlet:namespace /><%= PostalConfigDisplayTerms.STATUS %>').set('value', false);
				                 				A.one('#<portlet:namespace /><%= PostalConfigDisplayTerms.STATUS+PostalConfigDisplayTerms.CHECKBOX %>').set('checked', false);
				                 			}
										}
									});
								}
							}
						});
					});
				} 
			</script>

		
		</c:when>
		
		<c:otherwise>
			<liferay-ui:message key="do-not-have-permission"></liferay-ui:message>
		</c:otherwise>
	
	</c:choose>
</div>