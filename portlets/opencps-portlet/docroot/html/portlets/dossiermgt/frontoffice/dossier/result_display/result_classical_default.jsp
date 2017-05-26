<%@page import="java.util.ArrayList"%>
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

<%@page import="com.liferay.portal.kernel.dao.orm.QueryUtil"%>
<%@page import="com.liferay.portal.kernel.repository.model.FileEntry"%> 
<%@page import="com.liferay.portlet.documentlibrary.util.DLUtil"%>
<%@page import="java.util.List"%>
<%@page import="org.opencps.dossiermgt.model.Dossier"%>
<%@page import="org.opencps.dossiermgt.model.DossierFile"%>
<%@page import="org.opencps.dossiermgt.model.DossierPart"%>
<%@page import="org.opencps.dossiermgt.service.DossierFileLocalServiceUtil"%>
<%@page import="org.opencps.dossiermgt.util.DossierMgtUtil"%>
<%@page import="org.opencps.util.DateTimeUtil"%>
<%@page import="org.opencps.util.DLFileEntryUtil"%>

<%@ include file="../../../init.jsp"%>
<%
	List<DossierPart> dossierPartsLevel1 = (List<DossierPart>) request.getAttribute("dossierPartsLevel1");
	Dossier dossier = (Dossier) request.getAttribute("dossier");
	
%>


<c:if test="<%= dossierPartsLevel1 != null && !dossierPartsLevel1.isEmpty() %>">


<aui:row cssClass="pd_t20">
	
	<aui:col width="20">
		<label class="bold uppercase">
			<liferay-ui:message key="dossier-file-result"/>
		</label>
		
	</aui:col>
	
	<aui:col width="80">
	
	
		<%
			for (DossierPart dossierPartLevel1 : dossierPartsLevel1){
				
			int partType = dossierPartLevel1.getPartType();
			 
		%>
	
		<c:choose>
			
			<c:when test="<%=partType == PortletConstants.DOSSIER_PART_TYPE_RESULT %>">
				<%
				//	List<DossierPart> dossierParts = DossierMgtUtil.getTreeDossierPart(dossierPartLevel1.getDossierpartId());
				List<DossierPart> dossierParts = new ArrayList<DossierPart>();
				DossierMgtUtil.getTreeDossierPart(dossierPartLevel1.getDossierpartId(), dossierParts);
					if(dossierParts != null){
						for(DossierPart dossierPart : dossierParts){
							DossierFile dossierFile = null;
							try{
	
								dossierFile = DossierFileLocalServiceUtil.getDossierFileInUse(dossier.getDossierId(), dossierPart.getDossierpartId());
								
							}catch(Exception e){
								continue;
							}
							
							if(dossierFile.getFileEntryId() <= 0 || dossierFile.getSyncStatus() != PortletConstants.DOSSIER_FILE_SYNC_STATUS_SYNCSUCCESS){
								continue;
							}
							
							
							String fileURL = StringPool.BLANK;
							
							try{
								FileEntry fileEntry = DLFileEntryUtil.getFileEntry(dossierFile.getFileEntryId());
								if(fileEntry != null){
									fileURL = DLUtil.getPreviewURL(fileEntry, fileEntry.getFileVersion(), 
											themeDisplay, StringPool.BLANK);
								}
							}catch(Exception e){
								continue;
								
							}
					
					%>
									<i class="fa fa-download fa-lg" style="font-size: 100%; margin: 0px 5px;"></i>
				
									<a class="blue" style="color: #e51b05; font-weight: bold;" href="<%=fileURL%>" target="_blank">
										<%=Validator.isNotNull(dossierFile.getDisplayName()) ? dossierFile.getDisplayName() : StringPool.BLANK  %>
									</a>
							
					
							
						<%
						
					}
				}
			
			%>
			
		</c:when>

		<c:when test="<%= partType == PortletConstants.DOSSIER_PART_TYPE_MULTIPLE_RESULT%>">
			<%
			
				List<DossierFile> dossierFiles = DossierFileLocalServiceUtil.
							getDossierFileByD_DP_Config(dossier.getDossierId(), dossierPartLevel1.getDossierpartId(), null, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
				int index = 0;
				if (Validator.isNotNull(dossierFiles)) 
				{
					for(DossierFile df : dossierFiles) {
					index++;
					String fileURL = StringPool.BLANK;
					
					if(df.getFileEntryId() <= 0 || df.getSyncStatus() != PortletConstants.DOSSIER_FILE_SYNC_STATUS_SYNCSUCCESS){
						continue;
					}
					
					
					try{
						FileEntry fileEntry = DLFileEntryUtil.getFileEntry(df.getFileEntryId());
						if(fileEntry != null){
							fileURL = DLUtil.getPreviewURL(fileEntry, fileEntry.getFileVersion(), 
									themeDisplay, StringPool.BLANK);
						}
					}catch(Exception e){
						continue;
						
					}
		
			%>
					<i class="fa fa-download fa-lg" style="font-size: 100%; margin: 0px 5px;"></i>
					
					<a class="blue" style="color: #e51b05; font-weight: bold;" href="<%=fileURL%>" target="_blank">
						<%=Validator.isNotNull(df.getDisplayName()) ? df.getDisplayName() : StringPool.BLANK  %>
					</a>
					

					<%
							}
						}
					%>
				</c:when>
		</c:choose>
	<%
		}
	%>

		</aui:col>
	
	</aui:row>

</c:if>