<%@page import="org.opencps.processmgt.util.ReportUtils"%>
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
<%@page import="com.liferay.portal.kernel.language.UnicodeLanguageUtil"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@page import="com.liferay.portlet.PortletURLFactoryUtil"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="javax.portlet.PortletRequest"%>
<%@page import="javax.portlet.WindowState"%>
<%@page import="org.opencps.dossiermgt.EmptyDossierFileException"%>
<%@page import="org.opencps.dossiermgt.model.Dossier"%>
<%@page import="org.opencps.dossiermgt.model.DossierPart"%>
<%@page import="org.opencps.dossiermgt.model.DossierTemplate"%>
<%@page import="org.opencps.dossiermgt.model.ServiceConfig"%>
<%@page import="org.opencps.dossiermgt.search.DossierFileDisplayTerms"%>
<%@page import="org.opencps.dossiermgt.service.DossierPartLocalServiceUtil"%>
<%@page import="org.opencps.dossiermgt.util.DossierMgtUtil"%>
<%@page import="org.opencps.servicemgt.model.ServiceInfo"%>
<%@page import="org.opencps.util.PortletConstants"%>
<%@page import="org.opencps.dossiermgt.service.DossierFileLocalServiceUtil"%>
<%@page import="org.opencps.dossiermgt.model.DossierFile"%>
<%@page import="org.opencps.dossiermgt.service.FileGroupLocalServiceUtil"%>
<%@page import="org.opencps.dossiermgt.model.FileGroup"%>
<%@page import="org.opencps.util.WebKeys"%>
<%@page import="org.opencps.dossiermgt.search.DossierDisplayTerms"%>
<%@page import="com.liferay.portal.kernel.util.HtmlUtil"%>
<%@page import="org.opencps.util.PortletPropsValues"%>
<%@page import="org.opencps.util.PortletUtil"%>
<%@page import="org.opencps.datamgt.model.DictItem"%>

<%@ include file="../../../init.jsp"%>

<portlet:renderURL 
	var="updateDossierFileURL" 
	windowState="<%=LiferayWindowState.POP_UP.toString() %>"
>
	<portlet:param 
		name="mvcPath" 
		value='<%=templatePath + "upload_dossier_file.jsp" %>'
	/>
</portlet:renderURL>

<liferay-ui:error-marker key="errorSection" value="dossier_part" />

<liferay-ui:error 
	exception="<%= EmptyDossierFileException.class %>" 
	message="<%=EmptyDossierFileException.class.getName() %>"
/>

<%
	
	Dossier dossier = (Dossier) request.getAttribute(WebKeys.DOSSIER_ENTRY);
	
	ServiceConfig serviceConfig = (ServiceConfig) request.getAttribute(WebKeys.SERVICE_CONFIG_ENTRY);
	
	ServiceInfo serviceInfo = (ServiceInfo) request.getAttribute(WebKeys.SERVICE_INFO_ENTRY);
	
	DossierTemplate dossierTemplate = (DossierTemplate) request.getAttribute(WebKeys.DOSSIER_TEMPLATE_ENTRY);
	
	String privateDossierGroup = StringPool.BLANK;
	
	List<DossierPart> dossierPartsLevel1 = new ArrayList<DossierPart>();
	
	boolean isEditDossier = ParamUtil.getBoolean(request, "isEditDossier");
	
	String cssRequired = StringPool.BLANK;
	String cssDossierPartRequired = StringPool.BLANK;
	
	String urlDownload = StringPool.BLANK;
	
	DictItem adminAction = PortletUtil.getDictItem(PortletPropsValues.DATAMGT_MASTERDATA_GOVERNMENT_AGENCY, 
			serviceConfig.getGovAgencyCode(), 
			scopeGroupId);

%>

<div class="ocps-dossier-process">

	<aui:row cssClass="header-title custom-title">
		<aui:col width="100">
			<c:choose>
				<c:when test="<%= (dossier == null) || (dossier != null && dossier.getDossierStatus().equalsIgnoreCase(PortletConstants.DOSSIER_STATUS_NEW) ) %>">
					<liferay-ui:message key="add-dossier"/>
				</c:when>
				
				<c:when test="<%= (dossier != null) && (dossier.getDossierStatus().equalsIgnoreCase(PortletConstants.DOSSIER_STATUS_PROCESSING)
						|| dossier.getDossierStatus().equalsIgnoreCase(PortletConstants.DOSSIER_STATUS_SYSTEM)) %>">
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
			<p class="span9"><%=HtmlUtil.escape(serviceInfo.getServiceName()) %></p>
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
	
	<h4><liferay-ui:message key="dossier_part"/></h4>
	
	<table class="table table-bordered fit-width">
		<thead>
			<tr>
				<th><div class="text-center"><liferay-ui:message key="stt"/></div></th>
				<th><div class="text-center"><liferay-ui:message key="ten-thanh-phan"/></div></th>
				<th><div class="text-center"><liferay-ui:message key="tai-bieu-mau"/></div></th>
				<th><div class="text-center"><liferay-ui:message key="thao-tac"/></div></th>
			</tr>
		</thead>
	
<%
	
	if(dossierTemplate != null){
		try{
			dossierPartsLevel1 = DossierPartLocalServiceUtil.getDossierPartsByT_P(dossierTemplate.getDossierTemplateId(), 0);
		}catch(Exception e){}
	}
	
	int index = 0;
	
	List<Long> requiredDossierPartIds = new ArrayList<Long>();
	
	if(dossierPartsLevel1 != null){
	%>
	<tbody>
	<%
		for (DossierPart dossierPartLevel1 : dossierPartsLevel1){
	
			int partType = dossierPartLevel1.getPartType();
			
			List<DossierPart> dossierParts = DossierMgtUtil.getTreeDossierPart(dossierPartLevel1.getDossierpartId());
			
			if(dossierParts != null){
				%>
				<c:choose>
					<c:when test="<%=partType == PortletConstants.DOSSIER_PART_TYPE_OPTION ||
						partType == PortletConstants.DOSSIER_PART_TYPE_SUBMIT || 
						partType == PortletConstants.DOSSIER_PART_TYPE_OTHER %>"
					>
						<%
						
						for(DossierPart dossierPart : dossierParts){
							boolean isDynamicForm = false;
							
							if(Validator.isNotNull(dossierPart.getFormReport()) && Validator.isNotNull(dossierPart.getFormScript())){
								isDynamicForm = true;
							}
							
							int level = 1;
							
							String treeIndex = dossierPart.getTreeIndex();
							
							if(Validator.isNotNull(treeIndex)){
								level = StringUtil.count(treeIndex, StringPool.PERIOD);
							}
							
							DossierFile dossierFile = null;
							
							if(dossier != null){
								try{
									dossierFile = DossierFileLocalServiceUtil.getDossierFileInUse(dossier.getDossierId(), 
											dossierPart.getDossierpartId());
									
								}catch(Exception e){
									
								}
							}
							
							requiredDossierPartIds = PortletUtil.getDossierPartRequired(requiredDossierPartIds, dossierPartLevel1, 
									dossierPart, dossierFile);
							
							cssRequired = dossierPart.getRequired() ? "cssRequired" : StringPool.BLANK;
							
							urlDownload = DossierMgtUtil.getURLDownloadTemplateFile(themeDisplay, dossierPart.getTemplateFileNo());
							
							%>
								<tr class='<%="opencps dossiermgt dossier-part-row r-" + index + StringPool.SPACE + "dpid-" + String.valueOf(dossierPart.getDossierpartId()) + " partType-" + partType%>'>
									<td>
										<%=doubleFomart.format(dossierPart.getSibling()) %>
									</td>
									<td class='<%="level-" + level + " opencps dossiermgt dossier-part"%>'>
										<span class="opencps dossiermgt dossier-part-name <%=cssRequired %>">
											<%=dossierPart.getPartName() %> 
										</span>
									</td>
									<td>
										<c:if test="<%=Validator.isNotNull(urlDownload) %>">
											<a target="_blank" class="download-dossier-file" href="<%=urlDownload%>">
												<i></i> <liferay-ui:message key="download-file-entry" />
											</a>
										</c:if>
									</td>
									<td>
										<span class="opencps dossiermgt dossier-part-control">
											<liferay-util:include 
												page="/html/common/portlet/dossier_actions_style1.jsp" 
												servletContext="<%=application %>"
											>
												
												<portlet:param 
													name="showVersionItemReference" 
													value="<%=String.valueOf(showDossierFileVersion) %>"
												/>
											
												<portlet:param 
													name="<%=DossierDisplayTerms.DOSSIER_ID %>" 
													value="<%=String.valueOf(dossier != null ? dossier.getDossierId() : 0) %>"
												/>
												
												<portlet:param 
													name="isDynamicForm" 
													value="<%=String.valueOf(isDynamicForm) %>"
												/>
												
												<portlet:param 
													name="<%=DossierFileDisplayTerms.DOSSIER_PART_ID %>" 
													value="<%=String.valueOf(dossierPart.getDossierpartId()) %>"
												/>
												<portlet:param 
													name="<%=DossierFileDisplayTerms.FILE_ENTRY_ID %>" 
													value="<%=String.valueOf(dossierFile != null ? dossierFile.getFileEntryId() : 0) %>"
												/>
												<portlet:param 
													name="<%=DossierFileDisplayTerms.DOSSIER_FILE_ID %>" 
													value="<%=String.valueOf(dossierFile != null ? dossierFile.getDossierFileId() : 0) %>"
												/>
												<portlet:param 
													name="<%=DossierFileDisplayTerms.LEVEL %>" 
													value="<%=String.valueOf(level) %>"
												/>
												<portlet:param 
													name="<%=DossierFileDisplayTerms.GROUP_NAME %>" 
													value="<%=StringPool.BLANK%>"
												/>
												<portlet:param 
													name="<%=DossierFileDisplayTerms.PART_TYPE %>" 
													value="<%=String.valueOf(dossierPart.getPartType()) %>"
												/>
												<portlet:param 
													name="isEditDossier" 
													value="<%=String.valueOf(isEditDossier) %>"
												/>
											</liferay-util:include>
										</span>
									</td>
								</tr>
							<%
							index++;
						}
						%>
						
						<c:if test="<%=partType == PortletConstants.DOSSIER_PART_TYPE_OTHER && dossier != null%>">
							<%
								List<DossierFile> dossierFiles = DossierFileLocalServiceUtil.
									getDossierFileByDID_DP_R(dossier.getDossierId(), dossierPartLevel1.getDossierpartId(), 0);
							
								if(dossierFiles != null){
									for(DossierFile dossierFileOther : dossierFiles){
										index ++;
										%>
										<tr class='<%="opencps dossiermgt dossier-part-row r-" + index%>'>
											<td></td>
											<td class='<%="level-1 opencps dossiermgt dossier-part"%>'>
												<span class="opencps dossiermgt dossier-part-name">
													<%=dossierFileOther.getDisplayName() %>
												</span>
											</td>
											<td></td>
											<td>
												<span class="opencps dossiermgt dossier-part-control">
													<liferay-util:include 
														page="/html/common/portlet/dossier_actions_style1.jsp" 
														servletContext="<%=application %>"
													>
														<portlet:param 
															name="showVersionItemReference" 
															value="<%=String.valueOf(showDossierFileVersion) %>"
														/>
													
														<portlet:param 
															name="<%=DossierDisplayTerms.DOSSIER_ID %>" 
															value="<%=String.valueOf(dossier != null ? dossier.getDossierId() : 0) %>"
														/>
														<portlet:param 
															name="<%=DossierFileDisplayTerms.DOSSIER_PART_ID %>" 
															value="<%=String.valueOf(dossierFileOther.getDossierPartId()) %>"
														/>
														<portlet:param 
															name="<%=DossierFileDisplayTerms.FILE_ENTRY_ID %>" 
															value="<%=String.valueOf(dossierFileOther.getFileEntryId()) %>"
														/>
														<portlet:param 
															name="<%=DossierFileDisplayTerms.DOSSIER_FILE_ID %>" 
															value="<%=String.valueOf(dossierFileOther.getDossierFileId()) %>"
														/>
														<portlet:param 
															name="<%=DossierFileDisplayTerms.LEVEL %>" 
															value="<%=String.valueOf(1) %>"
														/>
														<portlet:param 
															name="<%=DossierFileDisplayTerms.GROUP_NAME %>" 
															value="<%=StringPool.BLANK%>"
														/>
														<portlet:param 
															name="<%=DossierFileDisplayTerms.PART_TYPE %>" 
															value="<%=String.valueOf(partType) %>"
														/>
														<portlet:param 
															name="isEditDossier" 
															value="<%=String.valueOf(isEditDossier) %>"
														/>
													</liferay-util:include>
												</span>
											</td>
										</tr>
										<%
									}
								}
							%>
						</c:if>
					</c:when>
					
					<c:when test="<%=partType == PortletConstants.DOSSIER_PART_TYPE_PRIVATE && dossier != null%>">
						<%
							List<FileGroup> fileGroups = new ArrayList<FileGroup>();
						
							try{
								fileGroups = FileGroupLocalServiceUtil.getFileGroupByD_DP(dossier.getDossierId(), dossierPartLevel1.getDossierpartId());
							}catch(Exception e){}
							
							cssRequired = dossierPartLevel1.getRequired() ? "cssRequired" : StringPool.BLANK;

							urlDownload = DossierMgtUtil.getURLDownloadTemplateFile(themeDisplay, dossierPartLevel1.getTemplateFileNo());
							
						%>
						<tr class='<%="opencps dossiermgt dossier-part-row r-" + index + " " + cssDossierPartRequired %>'>
							<td class='<%="level-0" + " opencps dossiermgt dossier-part"%>'></td>
							<td class='<%="level-0" + " opencps dossiermgt dossier-part"%>'>
								<span class="opencps dossiermgt dossier-part-name <%=cssRequired %>">
									<%=dossierPartLevel1.getPartName() %>
								</span>
							</td>
							<td>
								<c:if test="<%=Validator.isNotNull(urlDownload) %>">
									<a target="_blank" class="download-dossier-file" href="<%=urlDownload%>">
										<i></i> <liferay-ui:message key="download-file-entry" />
									</a>
								</c:if>
							</td>
							<td>
								<span class="opencps dossiermgt dossier-part-control">
									<c:if test="<%=isEditDossier %>">
										<aui:a 
											id="<%=String.valueOf(dossierPartLevel1.getDossierpartId()) %>"
											dossier="<%=String.valueOf(dossier.getDossierId()) %>"
											dossier-part="<%=String.valueOf(dossierPartLevel1.getDossierpartId()) %>"
											href="javascript:void(0);" 
											label="add-private-dossier" 
											cssClass="label opencps dossiermgt part-file-ctr add-individual-part-group"
										/>
									
									</c:if>
								</span>
								
								<%index++; %>
								
								<c:choose>
									<c:when test="<%=fileGroups != null && !fileGroups.isEmpty() %>">
										<%
											for(FileGroup fileGroup : fileGroups){
												%>
												<liferay-util:include 
													page="/html/common/portlet/dossier_individual_part_style1.jsp" 
													servletContext="<%=pageContext.getServletContext() %>"
												>
													<portlet:param 
														name="<%=DossierDisplayTerms.DOSSIER_ID %>" 
														value="<%=String.valueOf(dossier != null ? dossier.getDossierId() : 0) %>"
													/>
													<portlet:param 
														name="<%=DossierFileDisplayTerms.DOSSIER_PART_ID %>" 
														value="<%=String.valueOf(fileGroup.getDossierPartId()) %>"
													/>
													
													<portlet:param 
														name="<%=DossierDisplayTerms.FILE_GROUP_ID %>" 
														value="<%=String.valueOf(fileGroup.getFileGroupId()) %>"
													/>
													
													<portlet:param 
														name="<%=DossierFileDisplayTerms.INDEX %>" 
														value="<%=String.valueOf(index) %>"
													/>
													
													<portlet:param 
														name="<%=DossierFileDisplayTerms.GROUP_NAME %>" 
														value="<%=fileGroup.getDisplayName() %>"
													/>
													
													<portlet:param 
														name="isEditDossier" 
														value="<%=String.valueOf(isEditDossier) %>"
													/>
									
												</liferay-util:include>
												<%
												index ++;
											}
										%>
									</c:when>
									
									<c:otherwise>
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
					</c:when>
					<c:otherwise>
					</c:otherwise>
				</c:choose>
			<%
			}
		}
		%>
			<aui:input name="requiredDossierPart" type="hidden" value="<%= StringUtil.merge(requiredDossierPartIds) %>"/>
		<%
	}	
%>
	</tbody>
	</table>
	
	<font class="requiredStyleCSS"><liferay-ui:message key="dossier-part-with-star-is-required"/></font>
</div>

<portlet:resourceURL var="signatureFrontOffice" />

<aui:script>
	
	AUI().ready('aui-base','liferay-portlet-url','aui-io', function(A){
		
		/* PDFSigningHelper.init(pluginload);
		
		function pluginload(loaded)
		{
			if(!loaded) {
				alert('Loading plugin is failed!');
			}
		}
		
		function SigningCallback(jsondata)
		{			
			if(jsondata.code == 0)
			{
				alert('suc:' + jsondata.data.path);
				PDFSigningHelper.openFile(jsondata.data.path);
			}
			else
			{
				alert('error with code:' + jsondata.errormsg);
			}
		}
		
		var url = '<%= signatureFrontOffice %>';
		var author = '<%= Validator.isNotNull(user) ? user.getFullName() : StringPool.BLANK %>';
		var imgSrcName = '<%= Validator.isNotNull(user) ? user.getScreenName() : StringPool.BLANK %>';
		var signatureItems = A.all('.signatureCls');
		signatureItems.each( function(signatureItem) {
			// console.log('start sign at here : ' + signatureItem);
			var dossierFileId = signatureItem.attr("dossier-file");
			signatureItem.on('click', function() {
				
				$.ajax({
		    		type : 'POST',
					url : url,
					data : {
						<portlet:namespace/>dossierFileId: dossierFileId,
						<portlet:namespace/>imgSrcName: imgSrcName,
						<portlet:namespace/>functionCase: '<%= PortletConstants.SIGNATURE_REQUEST_DATA %>'
					},
					success : function(datares) {
						var jsonDataResponse = JSON.parse(datares);
						
						var nameOfFile = jsonDataResponse.fileName;
						var base64String = jsonDataResponse.base64ContentString;
						var condauImageSrc = imgSrcName + "_condau.png";
						var imgContentBase64Str = jsonDataResponse.imgContentBase64Str;
						
						if(imgContentBase64Str != '' && condauImageSrc != '') {
							PDFSigningHelper.writeBase64ToFile(condauImageSrc, imgContentBase64Str, function(imgJsondata) {
								if(base64String != '' && nameOfFile != '') {
									
									PDFSigningHelper.writeBase64ToFile(nameOfFile, base64String, function(jsondata) {
										
										PDFSigningHelper.getCertIndex( function(dataJSON) {
											
											if(dataJSON.data != '-1') {
												
												PDFSigningHelper.signPDFWithSelectedPoint(jsondata.data, imgJsondata.data,
														author, "", dataJSON.data , "", function(jsondataSigned) {
													if(jsondataSigned.code == 0)
													{
														PDFSigningHelper.readFileasBase64(jsondataSigned.data.path, function(jsondataBase64) {
															
															AUI().use('aui-io-request', function(A){
														    	$.ajax({
														    		type : 'POST',
																	url : url,
																	data : {
																		<portlet:namespace/>dataSigned: jsondataBase64.data.toString(),
																		<portlet:namespace/>dossierFileId: dossierFileId,
																		<portlet:namespace/>functionCase: '<%= PortletConstants.SIGNATURE_UPDATE_DATA_AFTER_SIGN %>'
																	},
																	success : function(datares) {
																		if(datares) {
																			
																			var jsonDataResponse = JSON.parse(datares);
																			
																			if(jsonDataResponse.msg == 'success') {
																				
																				PDFSigningHelper.openFile(jsondataSigned.data.path);
									
																				Liferay.Util.getOpener().Liferay.Portlet.refresh('#p_p_id_<%= WebKeys.DOSSIER_MGT_PORTLET %>_', data);
																			}
																		}
																	}
														    	});
													   		 });
														});
													}
													else
													{
														alert('error with code:' + jsondataSigned.errormsg);
													}
												});
											}
										});
									});
								}
							});
						}
					}
		    	});
			});
		}); */
		
		//Upload buttons
		var uploadDossierFiles = A.all('.upload-dossier-file');
		
		if(uploadDossierFiles){
			uploadDossierFiles.each(function(e){
				e.on('click', function(){
					var portletURL = Liferay.PortletURL.createURL('<%= PortletURLFactoryUtil.create(request, WebKeys.DOSSIER_MGT_PORTLET, themeDisplay.getPlid(), PortletRequest.RENDER_PHASE) %>');
					portletURL.setParameter("mvcPath", "/html/portlets/dossiermgt/frontoffice/modal_dialog.jsp");
					portletURL.setWindowState("<%=LiferayWindowState.POP_UP.toString()%>"); 
					portletURL.setPortletMode("normal");
					portletURL.setParameter("content", "upload-file");
					uploadDossierFile(this, portletURL.toString(), '<portlet:namespace/>');
				});
			});
		}
		
		//View attachment buttons
		var viewAttachments = A.all('.view-attachment');
		
		if(viewAttachments){
			viewAttachments.each(function(e){
				e.on('click', function(){
					var instance = A.one(e);
					var dossierFileId = instance.attr('dossier-file');
					var portletURL = Liferay.PortletURL.createURL('<%= PortletURLFactoryUtil.create(request, WebKeys.DOSSIER_MGT_PORTLET, themeDisplay.getPlid(), PortletRequest.ACTION_PHASE) %>');
					portletURL.setParameter("javax.portlet.action", "previewAttachmentFile");
					portletURL.setParameter("dossierFileId", dossierFileId);
					portletURL.setPortletMode("view");
					portletURL.setWindowState('<%=WindowState.NORMAL%>');
					viewDossierAttachment(this, portletURL.toString());
				});
			});
		}
		
		//Remove buttons
		var removeDossierFiles = A.all('.remove-dossier-file-has-file');
		
		if(removeDossierFiles){
			removeDossierFiles.each(function(e){
				e.on('click', function(){
					if(confirm('<%= UnicodeLanguageUtil.get(pageContext, "are-you-sure-remove-dossier-file") %>')){
						
						var instance = A.one(this);
						
						var dossierFileId = instance.attr('dossier-file');
						
						if(parseInt(dossierFileId) > 0){
							var portletURL = Liferay.PortletURL.createURL('<%= PortletURLFactoryUtil.create(request, WebKeys.DOSSIER_MGT_PORTLET, themeDisplay.getPlid(), PortletRequest.ACTION_PHASE) %>');
							portletURL.setParameter("javax.portlet.action", "removeAttachmentFile");
							portletURL.setParameter("dossierFileId", dossierFileId);
							portletURL.setPortletMode("view");
							portletURL.setWindowState('<%=WindowState.NORMAL%>');
							
							A.io.request(
								portletURL.toString(),
								{
									on: {
										success: function(event, id, obj) {
											var response = this.get('responseData');
											if(response){
												response = JSON.parse(response);
												
												if(response.deleted == true){
													var data = {
														'conserveHash': true
													};
													
													Liferay.Util.getOpener().Liferay.Portlet.refresh('#p_p_id_<%= WebKeys.DOSSIER_MGT_PORTLET %>_', data);
												}else{
													alert('<%= UnicodeLanguageUtil.get(pageContext, "error-while-remove-this-file") %>');
												}
											}
										}
									}
								}
							);
						}
					}
				});
			});	
		}
		
		//Add individual part buttons
		var addIndividualPartGroups = A.all('.add-individual-part-group');
		
		if(addIndividualPartGroups){
			addIndividualPartGroups.each(function(e){
				e.on('click', function(){
					var instance = A.one(e);
					var portletURL = Liferay.PortletURL.createURL('<%= PortletURLFactoryUtil.create(request, WebKeys.DOSSIER_MGT_PORTLET, themeDisplay.getPlid(), PortletRequest.RENDER_PHASE) %>');
					portletURL.setParameter("mvcPath", "/html/portlets/dossiermgt/frontoffice/modal_dialog.jsp");
					portletURL.setWindowState("<%=LiferayWindowState.POP_UP.toString()%>"); 
					portletURL.setPortletMode("normal");
					portletURL.setParameter("content", "individual");
					addIndividualPartGroup(this, portletURL.toString(), '<portlet:namespace/>');
				});
			});
		}
		
		//Remove dossier group
		
		var removeIndividualGroups = A.all('.remove-individual-group');
		
		if(removeIndividualGroups){
			removeIndividualGroups.each(function(e){
				e.on('click', function(){
					if(confirm('<%= UnicodeLanguageUtil.get(pageContext, "are-you-sure-remove-individual-group") %>')){
						
						var instance = A.one(this);
						
						var fileGroupId = instance.attr('file-group');
						var dossierId = instance.attr('dossier');
						var dossierPartId = instance.attr('dossier-part');
						
						if(parseInt(fileGroupId) > 0){
							var portletURL = Liferay.PortletURL.createURL('<%= PortletURLFactoryUtil.create(request, WebKeys.DOSSIER_MGT_PORTLET, themeDisplay.getPlid(), PortletRequest.ACTION_PHASE) %>');
							portletURL.setParameter("javax.portlet.action", "removeIndividualGroup");
							portletURL.setParameter("fileGroupId", fileGroupId);
							portletURL.setParameter("dossierId", dossierId);
							portletURL.setParameter("dossierPartId", dossierPartId);
							portletURL.setPortletMode("view");
							portletURL.setWindowState('<%=WindowState.NORMAL%>');
							
							A.io.request(
								portletURL.toString(),
								{
									on: {
										success: function(event, id, obj) {
											var response = this.get('responseData');
											if(response){
												response = JSON.parse(response);
												
												if(response.deleted == true){
													var data = {
														'conserveHash': true
													};
													Liferay.Util.getOpener().Liferay.Portlet.refresh('#p_p_id_<%= WebKeys.DOSSIER_MGT_PORTLET %>_', data);
												}else{
													alert('<%= UnicodeLanguageUtil.get(pageContext, "error-while-remove-this-group") %>');
												}
											}
										}
									}
								}
							);
						}
					}
				});
			});
		}
		
		//Declare online
		var declarationOnlines = A.all('.declaration-online');
		
		if(declarationOnlines){
			declarationOnlines.each(function(e){
				e.on('click', function(){
					var instance = A.one(e);
					var portletURL = Liferay.PortletURL.createURL('<%= PortletURLFactoryUtil.create(request, WebKeys.DOSSIER_MGT_PORTLET, themeDisplay.getPlid(), PortletRequest.RENDER_PHASE) %>');
					portletURL.setParameter("mvcPath", "/html/portlets/dossiermgt/frontoffice/modal_dialog.jsp");
					portletURL.setWindowState("<%=LiferayWindowState.POP_UP.toString()%>"); 
					portletURL.setPortletMode("normal");
					portletURL.setParameter("content", "declaration-online");
					dynamicForm(this, portletURL.toString(), '<portlet:namespace/>');
				});
			});
		}
		
		//View form
		var viewForms = A.all('.view-form');
		
		if(viewForms){
			viewForms.each(function(e){
				e.on('click', function(){
					var instance = A.one(e);
					var portletURL = Liferay.PortletURL.createURL('<%= PortletURLFactoryUtil.create(request, WebKeys.DOSSIER_MGT_PORTLET, themeDisplay.getPlid(), PortletRequest.RENDER_PHASE) %>');
					portletURL.setParameter("mvcPath", "/html/portlets/dossiermgt/frontoffice/modal_dialog.jsp");
					portletURL.setWindowState("<%=LiferayWindowState.POP_UP.toString()%>"); 
					portletURL.setPortletMode("normal");
					portletURL.setParameter("content", "declaration-online");
					dynamicForm(this, portletURL.toString(), '<portlet:namespace/>');
				});
			});
		}
		
		//View form
		var viewVersions = A.all('.view-version');
		
		if(viewVersions){
			viewVersions.each(function(e){
				e.on('click', function(){
				
					var portletURL = Liferay.PortletURL.createURL('<%= PortletURLFactoryUtil.create(request, WebKeys.DOSSIER_MGT_PORTLET, themeDisplay.getPlid(), PortletRequest.RENDER_PHASE) %>');
					portletURL.setParameter("mvcPath", "/html/portlets/dossiermgt/frontoffice/modal_dialog.jsp");
					portletURL.setWindowState("<%=LiferayWindowState.POP_UP.toString()%>"); 
					portletURL.setPortletMode("normal");
					portletURL.setParameter("content", "view-version");
					viewVersion(this, portletURL.toString(), '<portlet:namespace/>');
				});
			});
		}
	});

</aui:script>
