
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
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="com.liferay.portal.kernel.log.LogFactoryUtil"%>
<%@page import="org.opencps.dossiermgt.model.DossierPart"%>
<%@page import="org.opencps.dossiermgt.model.DossierTemplate"%>
<%@page import="org.opencps.dossiermgt.search.DossierPartDisplayTerms"%>
<%@page import="com.liferay.portal.kernel.log.Log"%>
<%@page import="org.opencps.dossiermgt.util.DossierMgtUtil"%>
<%@page import="org.opencps.dossiermgt.service.DossierPartLocalServiceUtil"%>
<%@page import="org.opencps.util.MessageKeys"%>
<%@page import="org.opencps.dossiermgt.DuplicateDossierPartNumberException"%>
<%@page import="org.opencps.dossiermgt.OutOfLengthDossierTemplateFileNumberException"%>
<%@page import="org.opencps.dossiermgt.OutOfLengthDossierPartNumberException"%>
<%@page import="org.opencps.dossiermgt.OutOfLengthDossierPartNameException"%>
<%@page import="org.opencps.dossiermgt.DuplicateDossierPartSiblingException"%>

<%@ include file="../init.jsp"%>
<%

	List<DossierPart> dossierPartSameLevels = new ArrayList<DossierPart>();
	DossierTemplate dossierTemplate = (DossierTemplate) request.getAttribute(WebKeys.DOSSIER_TEMPLATE_ENTRY);
	DossierPart dossierPart = (DossierPart) request.getAttribute(WebKeys.DOSSIER_PART_ENTRY);
	DossierPart dossierPartIsAddChilds = null;
	
	DossierPart dossierPartParent = null; 
	
	long dossierTemplateId = dossierTemplate != null ? dossierTemplate.getDossierTemplateId() : 0L;
	long dossierPartId = dossierPart != null ? dossierPart.getDossierpartId() : 0L;
	int [] dossierType = new int[6];
	long parentId = 0;
	dossierType[0] = PortletConstants.DOSSIER_TYPE_PAPER_SUBMITED ; 
	dossierType[1] = PortletConstants.DOSSIER_TYPE_OTHER_PAPERS_GROUP;
	dossierType[2] = PortletConstants.DOSSIER_TYPE_GROUPS_OPTIONAL; 
	dossierType[3] = PortletConstants.DOSSIER_TYPE_OWN_RECORDS; 
	dossierType[4] = PortletConstants.DOSSIER_TYPE_ONE_PAPERS_RESULTS;
	dossierType[5] = PortletConstants.DOSSIER_TYPE_MULTY_PAPERS_RESULTS;
	
	String isAddChilds = ParamUtil.getString(request, "isAddChild");
	String backURL = ParamUtil.getString(request, "backURL");
	boolean isHasSign = false; 

	try {
		if(dossierPart != null) {
			isHasSign = dossierPart.getHasSign();
			dossierPartParent = DossierPartLocalServiceUtil
							.getDossierPart(dossierPart.getParentId());
		}
	}catch(Exception e) {
		
	}

	
	if(Validator.isNotNull(dossierPart)) {
		dossierPartSameLevels = DossierPartLocalServiceUtil
				.getDossierPartsByT_P(dossierTemplateId,dossierPart.getParentId());
	}
	if(Validator.isNotNull(isAddChilds)) {
		parentId =  dossierPartId;
	}
	
	double maxSibling = 0;
	try {
		maxSibling = DossierMgtUtil.getMaxSibLingDossierPartInDepth(dossierTemplateId, parentId);
		maxSibling = maxSibling + 1;
	} catch (Exception e) {
		
	}

%>

<liferay-ui:header
	backURL="<%= backURL %>"
	title="update-dossier"
	backLabel="back"
/>

<liferay-ui:error 
	exception="<%= OutOfLengthDossierPartNameException.class %>"
	message="<%=OutOfLengthDossierPartNameException.class.getName() %>"
/>

<liferay-ui:error 
	exception="<%= OutOfLengthDossierPartNumberException.class %>"
	message="<%=OutOfLengthDossierPartNumberException.class.getName() %>"
/>

<liferay-ui:error 
	exception="<%= OutOfLengthDossierTemplateFileNumberException.class %>"
	message="<%=OutOfLengthDossierTemplateFileNumberException.class.getName() %>"
/>

<liferay-ui:error 
	exception="<%= DuplicateDossierPartNumberException.class %>"
	message="<%=DuplicateDossierPartNumberException.class.getName() %>"
/>

<liferay-ui:error 
	exception="<%= DuplicateDossierPartSiblingException.class %>"
	message="<%=DuplicateDossierPartSiblingException.class.getName() %>"
/>

<liferay-ui:error 
	key="<%= MessageKeys.DOSSIER_SYSTEM_EXCEPTION_OCCURRED %>"
	message="<%= MessageKeys.DOSSIER_SYSTEM_EXCEPTION_OCCURRED %>"
/>


<portlet:actionURL name="updateDossierPart" var="updateDossierPartURL" >
	<portlet:param 
		name="<%=DossierPartDisplayTerms.DOSSIERPART_DOSSIERPARTID %>" 
		value="<%=String.valueOf(dossierPartId)%>"
	/>
	
	<portlet:param name="currentURL" value="<%=currentURL %>"/>
	<portlet:param name="isAddChilds" value="<%=isAddChilds %>"/>
	<portlet:param name="backURL" value="<%=backURL %>"/>
</portlet:actionURL>

<aui:form 
	action="<%=updateDossierPartURL.toString() %>"
	method="post"
	name="fm"
>	
	<c:choose>
		<c:when test="<%=Validator.isNotNull(isAddChilds)%>">
			<aui:model-context bean="<%=dossierPartIsAddChilds%>" model="<%=DossierPart.class%>" />
		</c:when>
		<c:otherwise>
			<aui:model-context bean="<%=dossierPart%>" model="<%=DossierPart.class%>" />
		</c:otherwise>
	</c:choose>
	
	<aui:row>
		<aui:col width="70">
			<aui:input name="<%=DossierPartDisplayTerms.DOSSIERPART_PARTNAME %>" cssClass="input100">
				<aui:validator name="required" />
				<aui:validator name="maxLength">500</aui:validator>
			</aui:input>
		</aui:col>
		
		<aui:col width="30">
			<aui:input name="<%=DossierPartDisplayTerms.DOSSIERPART_PARTNO %>" cssClass="input100">
				<aui:validator name="required" />
				<aui:validator name="maxLength">100</aui:validator>
			</aui:input>
		</aui:col>
	</aui:row>
	
	<aui:row >
		<aui:col width="100">
			<aui:input cssClass="input100"
				type="textarea"
				name="<%=DossierPartDisplayTerms.DOSSIERPART_PARTTIP %>"
			>
				<aui:validator name="required" />
			</aui:input>
		</aui:col>
	</aui:row>
	
	<aui:row>
		<aui:col width="70">
			<aui:select name="<%=DossierPartDisplayTerms.DOSSIERPART_PARENTID %>" cssClass="input100">
				<c:choose>
					<c:when test="<%=Validator.isNotNull(isAddChilds) && Validator.isNotNull(dossierPart)%>">
						<aui:option value="<%=dossierPart.getDossierpartId() %>">
							<%=dossierPart.getPartName()%>
						</aui:option>
					</c:when>
					<c:when test="<%=!Validator.isNotNull(isAddChilds) && Validator.isNotNull(dossierPart) %>">
						<aui:option value="<%=dossierPart.getParentId() %>">
							<%=dossierPartParent != null ?  dossierPartParent.getPartName() : StringPool.BLANK%>
						</aui:option>
					</c:when>
					<c:otherwise>
						<aui:option value="<%=0 %>">
							<liferay-ui:message key="root" />
						</aui:option>
					</c:otherwise>
				</c:choose>
			</aui:select>
		</aui:col>
		
		<aui:col width="30">
			<c:choose>
				<c:when test="<%=Validator.isNull(dossierPart) %>">
					<aui:input 
						name="<%=DossierPartDisplayTerms.DOSSIERPART_SIBLING %>" 
						type="hidden" 
						value="<%= maxSibling %>"></aui:input>
				</c:when>
				
				<c:when test="<%=Validator.isNotNull(dossierPart) && Validator.isNotNull(isAddChilds) %>">
					<aui:input 
						name="<%=DossierPartDisplayTerms.DOSSIERPART_SIBLING %>" 
						type="hidden" 
						value="<%= maxSibling %>"></aui:input>
				</c:when>
				<c:otherwise>
					<aui:select name="<%=DossierPartDisplayTerms.DOSSIERPART_SIBLING %>">
						<%
							for(DossierPart dossierPartSameLevelIndex : dossierPartSameLevels) {
								%>
									<aui:option 
										value="<%= dossierPartSameLevelIndex.getSibling() %>"
										selected="<%= dossierPartSameLevelIndex.getSibling() == dossierPart.getSibling()%>"
									>
										<%= (int)dossierPartSameLevelIndex.getSibling() %>
									</aui:option>
								<%
							}
						%>
					</aui:select>
				</c:otherwise>
			</c:choose>
		</aui:col>
	</aui:row>
	
	<aui:row>
		<aui:col width="25">
			<aui:select name="<%=DossierPartDisplayTerms.DOSSIERPART_PARTTYPE %>" required="true" cssClass="input100">
				<aui:option value="<%=StringPool.BLANK %>">
					<liferay-ui:message key="root" />
				</aui:option>
				<%
					for(int dosType : dossierType) {
						%>
							<aui:option value="<%=dosType %>">
								<liferay-ui:message key="<%=DossierMgtUtil.getNameOfPartType(dosType, themeDisplay.getLocale()) %>" />
							</aui:option>
						<%
					}
				%>
			</aui:select>
		</aui:col>
		
		<aui:col width="25">
			<aui:input cssClass="input100" name="<%=DossierPartDisplayTerms.DOSSIERPART_TEMPLATEFILENO %>" />	
		</aui:col>
		
		<aui:col width="25">
			<aui:input
			name="<%=DossierPartDisplayTerms.DOSSIERPART_REQUIRED %>"
			type="checkbox"	
			checked="<%= !Validator.isNotNull(isAddChilds) && Validator.isNotNull(dossierPart) ? dossierPart.getRequired() : false %>"
		/>
		</aui:col>
		
		<aui:col width="25">
			<aui:input
			name="<%=DossierPartDisplayTerms.DOSSIERPART_HASSING %>"
			type="checkbox"	
			value="<%=isHasSign%>"
		/>
		</aui:col>
	</aui:row>
		
	<div id = "<portlet:namespace/>displayFormScript">
		<aui:row >
			<aui:col width="100">
				<aui:input
					type="textarea"
					name="<%=DossierPartDisplayTerms.DOSSIERPART_FORMSCRIPT %>" 
					cssClass="input100"
				/>
			</aui:col>
		</aui:row>
	</div>
	
	<aui:row >
		<aui:col width="100">
			<aui:input
				type="textarea"
				name="<%=DossierPartDisplayTerms.DOSSIERPART_FORMREPORT %>" 
				cssClass="input100"
			/>
		</aui:col>	
	</aui:row>
	
	<aui:row>
		<aui:col width="100">
			<aui:input 
				type="textarea"
				name="<%=DossierPartDisplayTerms.DOSSIERPART_SAMPLEDATA %>" 
				cssClass="input100"
			/>
		</aui:col>
	</aui:row>
	
	
	<aui:input 
		name="<%=DossierPartDisplayTerms.DOSSIERPART_DOSSIERTEMPLATEID %>"
		type="hidden"
		value= "<%= String.valueOf(dossierTemplateId) %>"
	/>
			

	<aui:row>
			<aui:button name="submit" value="submit" type="submit"/>
		
			<aui:button type="reset" value="clear"/>
	</aui:row>
</aui:form>

<aui:script >

AUI().ready('aui-base','liferay-form',function(A) {
	var partType = A.one('#<portlet:namespace /><%=DossierPartDisplayTerms.DOSSIERPART_PARTTYPE %>');
	var dispalyFormScript = A.one('#<portlet:namespace/>displayFormScript');
	var rules = {
		<portlet:namespace/>templateFileNo: {
			required: true,
			},
		};
	var rulesFalse = {
			<portlet:namespace/>templateFileNo: {
				required: false,
				},
			};
		
	if(partType.val() == '5') {
		new A.FormValidator(
				{
					boundingBox: '#<portlet:namespace/>fm',
					rules: rules
				}
			);
		}
	
	if(partType.val() == '' || partType.val() == '3' || partType.val() == '4' || partType.val() == "2") {
		dispalyFormScript.hide();
	}
	
	if(partType) {
		partType.on('change',function() {
			if(partType.val() == "1" ) {
				dispalyFormScript.show();
				new A.FormValidator(
						{
							boundingBox: '#<portlet:namespace/>fm',
							rules: rulesFalse
						}
					);
			} else if(partType.val() == "5"){
				dispalyFormScript.show();
				new A.FormValidator(
						{
							boundingBox: '#<portlet:namespace/>fm',
							rules: rules
						}
					);
			} 
			
			else {
				dispalyFormScript.hide();
				new A.FormValidator(
						{
							boundingBox: '#<portlet:namespace/>fm',
							rules: rulesFalse
						}
					);
			}	
		});
		
	}
});

</aui:script>

<%!
	private Log _log = LogFactoryUtil.getLog("html.portlets.dossiermgt.admin.edit_dossier_part.jsp");
%>