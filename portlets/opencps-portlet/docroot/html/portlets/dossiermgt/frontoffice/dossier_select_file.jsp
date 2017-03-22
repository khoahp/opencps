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

<%@page import="org.opencps.dossiermgt.service.DossierLocalServiceUtil"%>
<%@page import="org.opencps.dossiermgt.model.Dossier"%>
<%@page import="org.opencps.util.DateTimeUtil"%>
<%@page import="com.liferay.portal.kernel.util.Constants"%>
<%@page import="com.liferay.portlet.documentlibrary.util.DLUtil"%>
<%@page import="org.opencps.util.DLFileEntryUtil"%>
<%@page import="com.liferay.portal.kernel.repository.model.FileEntry"%>
<%@page import="org.opencps.servicemgt.service.TemplateFileLocalServiceUtil"%>
<%@page import="org.opencps.servicemgt.model.TemplateFile"%>
<%@page import="com.liferay.portal.kernel.servlet.SessionErrors"%>
<%@page import="com.liferay.portal.kernel.servlet.SessionMessages"%>
<%@page import="org.opencps.util.WebKeys"%>
<%@page import="org.opencps.dossiermgt.model.DossierPart"%>
<%@page import="org.opencps.dossiermgt.service.DossierPartLocalServiceUtil"%>
<%@page import="org.opencps.util.MessageKeys"%>
<%@page import="com.liferay.portlet.documentlibrary.DuplicateFileException"%>
<%@page import="org.opencps.accountmgt.NoSuchAccountOwnUserIdException"%>
<%@page import="com.liferay.portlet.documentlibrary.NoSuchFileEntryException"%>
<%@page import="org.opencps.dossiermgt.PermissionDossierException"%>
<%@page import="org.opencps.accountmgt.NoSuchAccountOwnOrgIdException"%>
<%@page import="org.opencps.accountmgt.NoSuchAccountFolderException"%>
<%@page import="org.opencps.accountmgt.NoSuchAccountTypeException"%>
<%@page import="org.opencps.accountmgt.NoSuchAccountException"%>
<%@page import="org.opencps.dossiermgt.NoSuchDossierPartException"%>
<%@page import="org.opencps.dossiermgt.NoSuchDossierException"%>
<%@page import="org.opencps.util.PortletConstants"%>
<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@page import="org.opencps.dossiermgt.search.DossierDisplayTerms"%>
<%@page import="org.opencps.dossiermgt.search.DossierFileDisplayTerms"%>
<%@page import="org.opencps.dossiermgt.service.DossierFileLocalServiceUtil"%>
<%@page import="org.opencps.dossiermgt.model.DossierFile"%>
<%@page import="org.opencps.dossiermgt.search.DossierFileSearchTerms"%>
<%@page import="javax.portlet.PortletURL"%>
<%@page import="org.opencps.dossiermgt.search.DossierFileSearch"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<%@ include file="../init.jsp"%>

<%

	boolean success = false;
	
	try{
		success = !SessionMessages.isEmpty(renderRequest) && SessionErrors.isEmpty(renderRequest);
		
	}catch(Exception e){
		
	}
	long dossierId = ParamUtil.getLong(request, DossierDisplayTerms.DOSSIER_ID);
	
	long dossierPartId = ParamUtil.getLong(request, DossierFileDisplayTerms.DOSSIER_PART_ID);
	
	long dossierFileId = ParamUtil.getLong(request, DossierFileDisplayTerms.DOSSIER_FILE_ID);
	
	long fileGroupId = ParamUtil.getLong(request, DossierDisplayTerms.FILE_GROUP_ID);
	
	long groupDossierPartId = ParamUtil.getLong(request, "groupDossierPartId");
	
	String groupName = ParamUtil.getString(request, DossierFileDisplayTerms.GROUP_NAME);
	
	String modalDialogId = ParamUtil.getString(request, "modalDialogId");
	
	String redirectURL = ParamUtil.getString(request, "redirectURL");
	
	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter("mvcPath", templatePath + "dossier_file.jsp");
	iteratorURL.setParameter("tab1", "select-file-store");
	
	iteratorURL.setParameter("dossierId", String.valueOf(dossierId));
	iteratorURL.setParameter("dossierPartId", String.valueOf(dossierPartId));
	iteratorURL.setParameter("dossierFileId", String.valueOf(dossierFileId));
	iteratorURL.setParameter("fileGroupId", String.valueOf(fileGroupId));
	iteratorURL.setParameter("groupDossierPartId", String.valueOf(groupDossierPartId));
	iteratorURL.setParameter("groupName", groupName);
	iteratorURL.setParameter("redirectURL", redirectURL);
	iteratorURL.setParameter("modalDialogId", modalDialogId);


	PortletURL searchURL = renderResponse.createRenderURL();
	searchURL.setParameter("mvcPath", templatePath + "dossier_file.jsp");
	searchURL.setParameter("tab1", "select-file-store");
	
	searchURL.setParameter("dossierId", String.valueOf(dossierId));
	searchURL.setParameter("dossierPartId", String.valueOf(dossierPartId));
	searchURL.setParameter("dossierFileId", String.valueOf(dossierFileId));
	searchURL.setParameter("fileGroupId", String.valueOf(fileGroupId));
	searchURL.setParameter("groupDossierPartId", String.valueOf(groupDossierPartId));
	searchURL.setParameter("groupName", groupName);
	searchURL.setParameter("redirectURL", redirectURL);
	searchURL.setParameter("modalDialogId", modalDialogId);
	
	boolean isSameTemplate = ParamUtil.getBoolean(request, "isSameTemplate", false);
					
	String templateFileNo = StringPool.BLANK;
	templateFileNo = ParamUtil.getString(request, DossierDisplayTerms.TEMPLATE_FILE_NO);
	
	String templateFileNoTemp = StringPool.BLANK;
	Dossier dossier = null;
	try {
		dossier = DossierLocalServiceUtil.getDossier(dossierId);
	} catch (Exception e) {}
	
	try{
		DossierPart dossierPart = DossierPartLocalServiceUtil.getDossierPart(dossierPartId);
		if(dossierPart != null && Validator.isNotNull(dossierPart.getTemplateFileNo())){
			templateFileNoTemp = dossierPart.getTemplateFileNo();
		}
	}catch(Exception e){
		e.printStackTrace();
	}
	
	if(isSameTemplate){
		templateFileNo = templateFileNoTemp;
	}
	
	
	List<String> headerNames = new ArrayList<String>();
	headerNames.add("stt");
	headerNames.add("dossier-file-no");
	headerNames.add("dossier-file-date");
	headerNames.add("dossier-file-name");
	headerNames.add("dossier");
	headerNames.add("action");
	
	String headers = StringUtil.merge(headerNames);
%>

<liferay-ui:error message="upload-error" key="upload-error"/>

<liferay-ui:error 
	exception="<%= NoSuchDossierException.class %>" 
	message="<%= NoSuchDossierException.class.getName() %>" 
/>
<liferay-ui:error 
	exception="<%= NoSuchDossierPartException.class %>" 
	message="<%= NoSuchDossierPartException.class.getName() %>" 
/>
<liferay-ui:error 
	exception="<%= NoSuchAccountException.class %>" 
	message="<%= NoSuchAccountException.class.getName() %>" 
/>
<liferay-ui:error 
	exception="<%= NoSuchAccountTypeException.class %>" 
	message="<%= NoSuchAccountTypeException.class.getName() %>" 
/>
<liferay-ui:error 
	exception="<%= NoSuchAccountFolderException.class %>" 
	message="<%= NoSuchAccountFolderException.class.getName() %>" 
/>
<liferay-ui:error 
	exception="<%= NoSuchAccountOwnUserIdException.class %>" 
	message="<%= NoSuchAccountOwnUserIdException.class.getName() %>" 
/>

<liferay-ui:error 
	exception="<%= NoSuchAccountOwnOrgIdException.class %>" 
	message="<%= NoSuchAccountOwnOrgIdException.class.getName() %>" 
/>
<liferay-ui:error 
	exception="<%= PermissionDossierException.class %>" 
	message="<%= PermissionDossierException.class.getName() %>" 
/>

<liferay-ui:error 
	exception="<%= NoSuchFileEntryException.class %>" 
	message="<%= NoSuchFileEntryException.class.getName() %>" 
/>
<liferay-ui:error 
	exception="<%= DuplicateFileException.class %>" 
	message="<%= MessageKeys.DOSSIER_FILE_DUPLICATE_NAME %>"
/>

<aui:nav-bar cssClass="custom-toolbar">
	<aui:nav-bar-search cssClass="pull-right">
		<div class="form-search">
			<aui:form action="<%= searchURL %>" method="post" name="fmSearch" onSubmit='<%= "event.preventDefault(); " + renderResponse.getNamespace() + "searchDossierFile();" %>'>
				<aui:row>
					<aui:col width="50">
						<aui:input name="isSameTemplate" 
							type="checkbox" 
							label="search-dossier-file-same-template" 
							inlineField="<%=true %>"
							inlineLabel="left"
							checked="<%=isSameTemplate ? true : false %>"
						/>
					</aui:col>
					<aui:col width="50">
						<liferay-ui:input-search 
							id="keywords1"
							name="keywords"
							title='<%= LanguageUtil.get(locale, "keywords") %>'
							placeholder='<%= LanguageUtil.get(locale, "keywords") %>' 
						/>
					</aui:col>
				</aui:row>
			</aui:form>
		</div>
	</aui:nav-bar-search>
</aui:nav-bar>

<portlet:actionURL var="cloneDossierFileURL" name="cloneDossierFile"/>

<aui:form action="<%=cloneDossierFileURL.toString() %>" method="post" name="fmSelectDossierFile">
	<div class="opencps-searchcontainer-wrapper">
		<liferay-ui:search-container 
			searchContainer="<%= new DossierFileSearch(renderRequest, SearchContainer.DEFAULT_DELTA, iteratorURL) %>" 
			headerNames="<%= headers %>"
		>
			<liferay-ui:search-container-results>
				<%
					DossierFileSearchTerms searchTerms = (DossierFileSearchTerms)searchContainer.getSearchTerms();
								
						List<DossierFile> dossierFiles = new ArrayList<DossierFile>();
						int totalCount = 0;
						try {
							dossierFiles = DossierFileLocalServiceUtil.searchDossierFile(scopeGroupId, citizen != null ? citizen.getMappingUserId() : 0, business != null ? business.getMappingOrganizationId() : 0, searchTerms.getKeywords(), templateFileNo, 0, searchContainer.getStart(), searchContainer.getEnd(), searchContainer.getOrderByComparator());
							totalCount = DossierFileLocalServiceUtil.countDossierFile(scopeGroupId, citizen != null ? citizen.getMappingUserId() : 0, business != null ? business.getMappingOrganizationId() : 0, searchTerms.getKeywords(), templateFileNo, 0);
						} catch(Exception e){
							
						}

						total = totalCount;
						results = dossierFiles;
						
						pageContext.setAttribute("results", results);
						pageContext.setAttribute("total", total);				
				%>
			</liferay-ui:search-container-results>	
				<liferay-ui:search-container-row 
					className="org.opencps.dossiermgt.model.DossierFile" 
					modelVar="dossierFile" 
					keyProperty="dossierFileId"
				>
					<portlet:renderURL var="viewDossierURL">
						<portlet:param name="mvcPath"
							value='<%=templatePath + "edit_dossier.jsp"%>' />
						<portlet:param name="<%=DossierDisplayTerms.DOSSIER_ID%>"
							value="<%=String.valueOf(dossierFile.getDossierId())%>" />
						<portlet:param name="<%=Constants.CMD%>" value="<%=Constants.VIEW%>" />
						<portlet:param name="isEditDossier" value="<%=String.valueOf(false)%>" />
					</portlet:renderURL>
				
					<%
						String templateFileURL = StringPool.BLANK;
						String dossierFileURL = StringPool.BLANK;
							try {
								if(Validator.isNotNull(dossierFile.getTemplateFileNo())) {
									String tempNo = dossierFile.getTemplateFileNo();
									TemplateFile templateFile = TemplateFileLocalServiceUtil
											.getTemplateFileByNo(scopeGroupId, tempNo);
										long templateFileEntryId = 0;
										
										if(Validator.isNotNull(templateFile)) {
											templateFileEntryId = templateFile.getFileEntryId();
											if(templateFileEntryId > 0) {
												FileEntry templateFileEntry =
														DLFileEntryUtil.getFileEntry(templateFileEntryId);
												templateFileURL = DLUtil.getPreviewURL(
														templateFileEntry, templateFileEntry.getFileVersion(),
														themeDisplay, StringPool.BLANK);
											}
									}
								}
							} catch (Exception e) {}
							
							try {
								long dossierFileEntryId = dossierFile.getFileEntryId();
								
								if(dossierFileEntryId > 0) {
									FileEntry dossierFileEntry =
											DLFileEntryUtil.getFileEntry(dossierFileEntryId);
									dossierFileURL = DLUtil.getPreviewURL(
											dossierFileEntry, dossierFileEntry.getFileVersion(),
											themeDisplay, StringPool.BLANK);
								}
							} catch(Exception e) {} 
							
							String viewDossierUrlNomal = viewDossierURL.toString();
							if(viewDossierUrlNomal.contains("p_p_state=pop_up"))  {
								viewDossierUrlNomal = StringUtil.replace(viewDossierUrlNomal, "p_p_state=pop_up", "p_p_state=nomal");
							}
							
					%>
									
					<liferay-util:buffer var="rowTicker">
						<aui:input name="hiddenDossierFileId" type="hidden" value="<%=dossierFile.getDossierFileId() %>"	/>
						<i class="fa fa-circle-o "></i>
					</liferay-util:buffer>

					<liferay-util:buffer var="boundCol1">
						<%=Validator.isNotNull(dossierFile.getDossierFileNo()) ? dossierFile.getDossierFileNo() : StringPool.DASH %>
					</liferay-util:buffer>
					
					<liferay-util:buffer var="boundCol2">
						<% if(Validator.isNotNull(dossierFile.getDossierFileDate())) {%>
							<%= DateTimeUtil.convertDateToString(dossierFile.getDossierFileDate(), DateTimeUtil._VN_DATE_FORMAT) %>
						<%} else if (Validator.isNotNull(dossierFile.getModifiedDate())) {%>
							<%= DateTimeUtil.convertDateToString(dossierFile.getModifiedDate(), DateTimeUtil._VN_DATE_FORMAT) %>
						<% } else %>
							<%= StringPool.DASH %>
						<% } %>
					</liferay-util:buffer>
					
					<liferay-util:buffer var="boundCol3">
						<a href="<%=dossierFileURL %>" target="_blank">
							<%=Validator.isNotNull(dossierFile.getDisplayName()) ? dossierFile.getDisplayName() : StringPool.DASH %>
						</a>
					</liferay-util:buffer>
					
					
					<liferay-util:buffer var="boundCol4">
						<%=Validator.isNotNull(dossier) ? dossier.getReceptionNo() : StringPool.DASH %>
					</liferay-util:buffer>
					
					<liferay-util:buffer var="boundCol5">
						<% String jsButton = "javascript:" + renderResponse.getNamespace() + "selectDossierFile(" + dossierFile.getDossierFileId() +")"; %>
						
						<a href="javascript:void(0)" onclick="<%= jsButton %>" class="dsf-select-btn">
							<i></i> <liferay-ui:message key="select" />
						</a>
						
						<a href="<%=dossierFileURL %>" class="dsf-view-btn" target="_blank">
							<i></i> <liferay-ui:message key="view" />
						</a>
					</liferay-util:buffer>
					
					<%
						row.addText(String.valueOf((searchContainer.getCur() - 1) * searchContainer.getDelta() + index + 1));
						row.addText(boundCol1);
						row.addText(boundCol2);
						row.addText(boundCol3);
						row.addText(boundCol4);
						row.addText(boundCol5);
					%>	
				</liferay-ui:search-container-row> 
			
			<liferay-ui:search-iterator type="opencs_page_iterator"/>
		</liferay-ui:search-container>
		<aui:input name="cloneDossierFileId" type="hidden"/>
		<aui:input name="redirectURL" type="hidden" value="<%=Validator.isNull(redirectURL) ? currentURL : redirectURL %>"/>
		<aui:input name="<%=DossierDisplayTerms.DOSSIER_ID %>" type="hidden" value="<%=dossierId %>"/>
		<aui:input name="groupDossierPartId" type="hidden" value="<%=groupDossierPartId %>"/>
		<aui:input name="<%=DossierFileDisplayTerms.DOSSIER_FILE_ID %>" type="hidden" value="<%=dossierFileId %>"/>
		<aui:input name="<%=DossierDisplayTerms.FILE_GROUP_ID %>" type="hidden" value="<%=fileGroupId %>"/>
		<aui:input name="<%=DossierFileDisplayTerms.DOSSIER_PART_ID %>" type="hidden" value="<%=dossierPartId %>"/>
		<aui:input name="<%=DossierFileDisplayTerms.DOSSIER_FILE_ORIGINAL %>" type="hidden" value="<%=String.valueOf(PortletConstants.DOSSIER_FILE_ORIGINAL) %>"/>
		<aui:input name="<%=DossierFileDisplayTerms.DOSSIER_FILE_TYPE %>" type="hidden" value="<%=String.valueOf(PortletConstants.DOSSIER_FILE_TYPE_INPUT) %>"/>
		<aui:input name="<%=DossierFileDisplayTerms.GROUP_NAME %>" type="hidden" value="<%=groupName %>"/>
		<aui:input name="receiveHiddenDossierFile" type="hidden" />
		<aui:row>
			<aui:button name="btnAccept" value="agree" cssClass="dsf-btn-ok btn btn-primary"/>
			<aui:button name="btnCancel" value="cancel" cssClass="dsf-btn-cancel btn"/>
		</aui:row>
	</div>

</aui:form>

<aui:script>

	AUI().ready(function(A){
		
		var success = '<%=success%>';
		
		var receiveHiddenDossierFile = A.one('#<portlet:namespace />receiveHiddenDossierFile');
		var btnAccept = A.one('#<portlet:namespace />btnAccept');
		var btnCancel = A.one('#<portlet:namespace />btnCancel');
		var allRows = A.all('.form tr');
		
		var circleTemp = null;
		
		btnAccept.addClass('disabled');
		btnAccept.setAttribute('disabled' , 'true');

		if(success == 'true'){
			<portlet:namespace/>closeDialog();
		}

		
		allRows.each(function(taskNode) {
			taskNode.on('click', function(){
				var instance = A.one(this);
				var childNode = instance.one('.first input');
				var circle = instance.one('.first i');
				
				receiveHiddenDossierFile.val(childNode.val());
				if(receiveHiddenDossierFile.val() != '') {
					btnAccept.removeClass('disabled');
					btnAccept.removeAttribute('disabled');
					
					if(circleTemp != null) {
						circleTemp.removeClass('fa-dot-circle-o');
						circleTemp.addClass('fa-circle-o');
					}
					circle.removeClass('fa-circle-o');
					circle.addClass('fa-dot-circle-o');
					circleTemp = circle;
				}
			});
		});
		
		if(btnAccept) {
			btnAccept.on('click', function() {
				<portlet:namespace/>selectDossierFile(receiveHiddenDossierFile.val());
			});
		}
		
		if(btnCancel) {
			btnCancel.on('click', function() {
				<portlet:namespace/>closeDialog();
			});
		}
	});
	
	Liferay.provide(window, '<portlet:namespace/>selectDossierFile', function(id) {
		var A = AUI();
		var cloneDossierFile = A.one('#<portlet:namespace/>cloneDossierFileId').val(id);
		submitForm(document.<portlet:namespace />fmSelectDossierFile);
	});
	
	Liferay.provide(window, '<portlet:namespace/>searchDossierFile', function() {
		var A = AUI();
		var templateFileNo = '<%=templateFileNoTemp%>';
		
		var isSameTemplate = A.one('#<portlet:namespace/>isSameTemplate').val();
		
		if(isSameTemplate == 'true'){
			
			templateFileNo = '<%=templateFileNoTemp%>';
			
		}else{
			templateFileNo = '';
		}
		
		var portletURL = Liferay.PortletURL.createURL('<%=searchURL.toString()%>');
		portletURL.setParameter("templateFileNo", templateFileNo);
		portletURL.setParameter("isSameTemplate", isSameTemplate);
		
		var form = A.one('#<portlet:namespace/>fmSearch');
		form.attr('action', portletURL.toString());
		
		submitForm(document.<portlet:namespace />fmSearch);
	},['liferay-portlet-url']);
	

	Liferay.provide(window, '<portlet:namespace/>closeDialog', function() {
		var data = {
			'conserveHash': true
		}
		Liferay.Util.getOpener().Liferay.Portlet.refresh('#p_p_id_<%= WebKeys.DOSSIER_MGT_PORTLET %>_', data);
		var dialog = Liferay.Util.getWindow('<portlet:namespace/>' + '<%=modalDialogId%>');
		dialog.destroy(); // You can try toggle/hide whate
	});
</aui:script>
