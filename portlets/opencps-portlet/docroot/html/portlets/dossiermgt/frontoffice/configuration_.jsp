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
<%@page import="org.opencps.jasperreport.util.JRReportUtil.DocType"%>
<%@page import="org.opencps.util.PortletUtil"%>
<%@page import="com.liferay.portal.kernel.util.ArrayUtil"%>
<%@page import="com.liferay.portal.kernel.util.KeyValuePair"%>
<%@page import="org.opencps.datamgt.model.DictItem"%>
<%@page import="com.liferay.portal.service.LayoutLocalServiceUtil"%>
<%@page import="com.liferay.portal.model.Layout"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.opencps.dossiermgt.search.DossierFileDisplayTerms"%>
<%@page import="org.opencps.util.PortletConstants.FileSizeUnit"%>
<%@page import="org.opencps.util.PortletPropsValues"%>
<%@page import="org.opencps.datamgt.service.DictCollectionLocalServiceUtil"%>
<%@page import="org.opencps.datamgt.model.DictCollection"%>
<%@page import="org.opencps.datamgt.service.DictItemLocalServiceUtil"%>
<%@ include file="../init.jsp"%>

<%
	String tabs2 = ParamUtil.getString(request, "tabs2", "dossier-list");
	String tabs2Names = "dossier-list,dossier,dossier-file-list, digital-signature";
	
	String templatesToDisplay_cfg = GetterUtil.getString(portletPreferences.getValue("templatesToDisplay", "default"));

	List<Layout> privateLayouts = LayoutLocalServiceUtil.getLayouts(scopeGroupId, true);
	List<Layout> publicLayouts = LayoutLocalServiceUtil.getLayouts(scopeGroupId, false);
	List<Layout> allLayouts = new ArrayList<Layout>();
	
	if(privateLayouts != null){
		allLayouts.addAll(privateLayouts);
	}
	
	if(publicLayouts != null){
		allLayouts.addAll(publicLayouts);
	}
	
	List leftList = new ArrayList();
	
	if(dossierStatusCodes != null){
		for(int i = 0; i < dossierStatusCodes.length; i++){
			try{
				DictItem dictItem = DictItemLocalServiceUtil.getDictItemByCode(dossierStatusCodes[i]);
				KeyValuePair keyValuePair =  new KeyValuePair(dictItem.getItemCode(), dictItem.getItemName(themeDisplay.getLocale()));
				leftList.add(keyValuePair);
			}catch(Exception e){
				continue;
			}
		}
	}
	
	List rightList = new ArrayList();
	
	try{
		DictCollection collection = DictCollectionLocalServiceUtil.getDictCollection(scopeGroupId, PortletPropsValues.DATAMGT_MASTERDATA_DOSSIER_STATUS);
		List<DictItem> dictItems = DictItemLocalServiceUtil.getDictItemsByDictCollectionId(collection.getDictCollectionId());
		
		if(dictItems != null){
			for(DictItem dictItem : dictItems){
				if(!ArrayUtil.contains(dossierStatusCodes, dictItem.getItemCode())){
					KeyValuePair keyValuePair =  new KeyValuePair(dictItem.getItemCode(), dictItem.getItemName(themeDisplay.getLocale()));
					rightList.add(keyValuePair);
				}
			}
		}
		
	}catch(Exception e){
		
	}
	
	List<DictItem> dictItems = PortletUtil.getDictItemInUseByCode(themeDisplay.getScopeGroupId(), 
			PortletPropsValues.DATAMGT_MASTERDATA_SERVICE_DOMAIN, 
			PortletConstants.TREE_VIEW_DEFAULT_ITEM_CODE);
%>


<liferay-ui:success 
	key="potlet-config-saved" 
	message="portlet-configuration-have-been-successfully-saved"
/>

<liferay-portlet:actionURL 
	var="configurationActionURL" 
	portletConfiguration="true">
	<portlet:param name="tabs2" value="<%= tabs2 %>" 
/>
</liferay-portlet:actionURL>


<liferay-portlet:renderURL 
	portletConfiguration="true" 
	var="configurationRenderURL"
>
	<portlet:param name="tabs2" value="<%= tabs2 %>" />
</liferay-portlet:renderURL>

<liferay-ui:tabs
	names="<%= tabs2Names %>"
	param="tabs2"
	url="<%= configurationRenderURL %>"
	tabsValues="<%=tabs2Names %>"
/>

<aui:form action="<%= configurationActionURL %>" method="post" name="configurationForm">

	<c:choose>
		<c:when test='<%=tabs2.equalsIgnoreCase("dossier-list") %>'>
			<liferay-ui:panel-container 
				extended="<%= true %>" 
				id="dossierlistPanelContainer" 
				persistState="<%= true %>"
			>
				<liferay-ui:panel 
					collapsible="<%= true %>" 
					extended="<%= true %>" 
					id="dossierListDisplayPanel" 
					persistState="<%= true %>" 
					title="display-style"
				>
					
					<aui:fieldset>
						<aui:select name="dossierListDisplayStyle" id="dossierListDisplayStyle">		
							<aui:option selected="<%= dossierListDisplayStyle.equals(\"default\") %>" value="default">
								<liferay-ui:message key="default"/>
							</aui:option>
								
							<aui:option selected="<%= dossierListDisplayStyle.equals(\"treemenu_left\") %>" value="treemenu_left">
								<liferay-ui:message key="treemenu-left"/>
							</aui:option>
						</aui:select>
					</aui:fieldset>
					
					<aui:fieldset>
						<aui:select name="templatesToDisplay" id="templatesToDisplay">
							<aui:option selected="<%= templatesToDisplay_cfg.equals(\"default\") %>" value="default">default</aui:option>
							
							<aui:option selected="<%= templatesToDisplay_cfg.equals(\"20_80\") %>" value="20_80">20_80</aui:option>
						
						</aui:select>
					
					</aui:fieldset>
					
					<aui:fieldset>
						<aui:input 
							type="checkbox"
							name="showServiceDomainTree" 
							value="<%= showServiceDomainTree %>"
						/>
					</aui:fieldset>
					
					<aui:fieldset>
						<aui:input 
							type="checkbox"
							name="hiddenTreeNodeEqualNone" 
							value="<%=hiddenTreeNodeEqualNone %>"
						/>
					</aui:fieldset>
					
					<aui:fieldset label="dossier-status-treemenu-display">
						<liferay-ui:input-move-boxes
							leftBoxName="dossierStatusCodes"
							leftList="<%= leftList %>"
							leftReorder="true"
							leftTitle="show"
							rightBoxName="availableDossierStatusCodes"
							rightList="<%= rightList %>"
							rightTitle="hide"
							
						/>
					</aui:fieldset>
					
					<aui:fieldset>
						<aui:input 
							type="checkbox" 
							name="displayDossierNo"
							value='<%= displayDossierNo %>'
						/>
					</aui:fieldset>
					
					<aui:fieldset>
						<aui:select name="reportType" id="reportType" multiple="true">		
							<%
								for(DocType docType : DocType.values()){
									%>
										<aui:option value="<%=docType.getValue() %>" selected="<%=ArrayUtil.contains(reportTypes, docType.getValue()) %>">
											<%=docType.getValue() %>
										</aui:option>
									<%
								}
							%>
						</aui:select>
					</aui:fieldset>
					
				</liferay-ui:panel>
				
				<liferay-ui:panel 
					collapsible="<%= true %>" 
					extended="<%= true %>" 
					id="dossierRecentListDisplayPanel" 
					persistState="<%= true %>" 
					title="dossier-resent-display"
				>
					<aui:fieldset>
						<aui:select name="dossierRecentItemDisplay" id="dossierRecentItemDisplay">
							<%
								for (int item = 2 ; item < 10; item ++) {
							%>
								<aui:option selected="<%= dossierRecentItemDisplay == item %>" value="<%= item %>"><%= item %></aui:option>
							<%
								}
							%>
						</aui:select>
					
					</aui:fieldset>
				
					<aui:fieldset>
						<aui:input 
							type="checkbox"
							name="displayRecentlyResultWhenSearch" 
							value='<%= displayRecentlyResultWhenSearch %>'
						/>
					</aui:fieldset>
					
					<aui:select name="itemCode_cfg" id="itemCode_cfg">
						<aui:option selected="<%= Validator.isNull(itemCode_cfg)  %>" value=""> </aui:option>
						<%
							for (DictItem dictItem : dictItems) {
						%>
							<aui:option selected="<%= itemCode_cfg.equals(dictItem.getItemCode()) %>" value="<%= dictItem.getItemCode() %>"><%= dictItem.getItemName(locale) %></aui:option>
						<%
							}
						%>
					</aui:select>
					
					<aui:select name="war_opencpsportlet_26_cfg" id="war_opencpsportlet_26_cfg">
						<aui:option selected="<%= Validator.isNull(war_opencpsportlet_26_cfg)  %>" value=""> </aui:option>
						<%
							for (Layout lout : publicLayouts) {
						%>
							<aui:option selected="<%= war_opencpsportlet_26_cfg.equals(String.valueOf(lout.getPlid())) %>" value="<%= lout.getPlid() %>"><%= lout.getName(locale) %></aui:option>
						<%
							}
						%>
					</aui:select>
				</liferay-ui:panel>
			</liferay-ui:panel-container>
		</c:when>
		
		<c:when test='<%=tabs2.equalsIgnoreCase("dossier") %>'>
			<liferay-ui:panel-container 
				extended="<%= true %>" 
				id="dossierSelectionPanelContainer" 
				persistState="<%= true %>"
			>
					
				<liferay-ui:panel 
					collapsible="<%= true %>" 
					extended="<%= true %>" 
					id="dossierDisplayPanel" 
					persistState="<%= true %>" 
					title="display-style"
				>
					<aui:fieldset>
						<aui:select name="dossierDisplayStyle" id="dossierDisplayStyle">		
							<aui:option selected="<%= dossierDisplayStyle.equals(\"default\") %>" value="default">
								<liferay-ui:message key="default"/>
							</aui:option>
						</aui:select>
					</aui:fieldset>
					
					<aui:fieldset>
						<aui:select name="dossierTabFocus">
							<aui:option value=""></aui:option>
							<aui:option value="dossier_info" selected="<%=dossierTabFocus.equals(\"dossier_info\") %>">
								<liferay-ui:message key="dossier-info"/>
							</aui:option>
							<aui:option value="dossier_part" selected="<%=dossierTabFocus.equals(\"dossier_part\") %>">
								<liferay-ui:message key="dossier-part"/>
							</aui:option>
							<aui:option value="history" selected="<%=dossierTabFocus.equals(\"history\") %>">
								<liferay-ui:message key="history"/>
							</aui:option>
							<aui:option value="result" selected="<%=dossierTabFocus.equals(\"result\") %>">
								<liferay-ui:message key="result"/>
							</aui:option>
						</aui:select>
						
					</aui:fieldset>
					
					<aui:fieldset>
						<aui:input 
							type="checkbox"
							name="showDossierFileVersion" 
							value="<%= showDossierFileVersion %>"
						/>
					</aui:fieldset>
					
					<aui:fieldset>
						<aui:input 
							type="checkbox"
							name="showBackToListButton" 
							value="<%= showBackToListButton %>"
						/>
					</aui:fieldset>
					
					<aui:fieldset>
						<aui:input 
							type="checkbox"
							name="showDossierSuggestionButton" 
							value="<%= showDossierSuggestionButton %>"
						/>
					</aui:fieldset>
					
				</liferay-ui:panel>
				
				<liferay-ui:panel 
					collapsible="<%= true %>" 
					extended="<%= true %>" 
					id="dossierFunctionPanel" 
					persistState="<%= true %>" 
					title="dossier-config"
				>
					<datamgt:ddr 
						depthLevel="1" 
						dictCollectionCode="DOSSIER_STATUS" 
						showLabel="<%=true%>"
						emptyOptionLabels="dossier-status"
						itemsEmptyOption="true"
						itemNames="suggestionDossierStatus"
						selectedItems="<%= suggestionDossierStatus %>"
						optionValueType="code"
						cssClass="search-input select-box input100"
					/>
		
					<aui:select name="plid" id="plid">
						<%
							for (Layout layoutTemp : allLayouts) {
						%>
							<aui:option value="<%= layoutTemp.getPlid() %>" selected="<%=layoutTemp.getPlid() == plidRes %>"><%= layoutTemp.getName(locale) %></aui:option>
						<%
							}
						%>
					</aui:select>
					
					<aui:fieldset>
						<aui:input 
							type="checkbox"
							name="allowQuickCreateDossier" 
							value="<%=allowQuickCreateDossier %>"
						/>
					</aui:fieldset>
					
					<aui:fieldset>
						<aui:input 
							type="checkbox"
							name="allowQuickViewResult" 
							value="<%=allowQuickViewResult %>"
						/>
					</aui:fieldset>
					
					
					
				</liferay-ui:panel>
				
				<liferay-ui:panel 
					collapsible="<%= true %>" 
					extended="<%= true %>" 
					id="dossierFileUploadPanel" 
					persistState="<%= true %>" 
					title="upload-config"
				>
					<aui:fieldset>
						<aui:input 
							type="text"
							name="uploadFileTypes" 
							value="<%=uploadFileTypes %>"
						/>
					</aui:fieldset>
					
					<aui:fieldset>
						<aui:row>
							<aui:col width="50">
								<aui:input 
									type="text"
									name="maxTotalUploadFileSize" 
									value="<%=maxTotalUploadFileSize %>"
								>
									<aui:validator name="number"></aui:validator>
								</aui:input>
							</aui:col>
							
							<aui:col width="50">
								<aui:select 
									name="maxTotalUploadFileSizeUnit" 
									id="maxTotalUploadFileSizeUnit"
								>
									<%
										for(FileSizeUnit unit : FileSizeUnit.values()){
											%>
												<aui:option value="<%=unit.getValue() %>" selected="<%=unit.getValue().equals(maxTotalUploadFileSizeUnit) %>">
													<%=unit.getValue() %>
												</aui:option>
											<%
										}
									%>
								</aui:select>
							</aui:col>
						</aui:row>
					</aui:fieldset>
					
					<aui:fieldset>
						<aui:row>
							<aui:col width="50">
								<aui:input 
									type="text"
									name="maxUploadFileSize" 
									value="<%=maxUploadFileSize %>"
								>
									<aui:validator name="number"></aui:validator>
								</aui:input>
							</aui:col>
							
							<aui:col width="50">
								<aui:select 
									name="maxUploadFileSizeUnit" 
									id="maxUploadFileSizeUnit"
								>
									<%
										for(FileSizeUnit unit : FileSizeUnit.values()){
											%>
												<aui:option value="<%=unit.getValue() %>" selected="<%=unit.getValue().equals(maxUploadFileSizeUnit) %>">
													<%=unit.getValue() %>
												</aui:option>
											<%
										}
									%>
								</aui:select>
							</aui:col>
						</aui:row>
					</aui:fieldset>
				</liferay-ui:panel>
				
				
			</liferay-ui:panel-container>
		</c:when>
		
		<c:when test='<%=tabs2.equalsIgnoreCase("dossier-file-list") %>'>
			<liferay-ui:panel-container 
				extended="<%= true %>" 
				id="dossierfilelistSelectionPanelContainer" 
				persistState="<%= true %>"
			>
				<liferay-ui:panel 
					collapsible="<%= true %>" 
					extended="<%= true %>" 
					id="dossierFileListOrderPanel" 
					persistState="<%= true %>" 
					title="display-style"
				>
				
					<aui:fieldset>
						<aui:select name="dossierFileDisplayStyle" id="dossierFileDisplayStyle">		
							<aui:option selected="<%= dossierFileDisplayStyle.equals(\"default\") %>" value="default">
								<liferay-ui:message key="default"/>
							</aui:option>
						</aui:select>
					</aui:fieldset>
				
					<aui:fieldset>
						<aui:input 
							type="checkbox"
							name="hideTabDossierFile" 
							value="<%= hideTabDossierFile %>"
						/>
					</aui:fieldset>
					
				</liferay-ui:panel>
			
				<liferay-ui:panel 
					collapsible="<%= true %>" 
					extended="<%= true %>" 
					id="dossierFileListOrderPanel" 
					persistState="<%= true %>" 
					title="order-by"
				>
					<aui:row>
						<aui:col width="50">
							<aui:select name="dossierFileListOrderByField">
								<aui:option value="<%=StringPool.BLANK %>" />
				
								<aui:option 
									value='<%=DossierFileDisplayTerms.DOSSIER_FILE_DATE %>' 
									selected='<%= dossierFileListOrderByField.equals(DossierFileDisplayTerms.DOSSIER_FILE_DATE) %>'
								>
									<liferay-ui:message key="order-by-dossier-file-date"/>
								</aui:option>
							</aui:select>
						</aui:col>
						
						<aui:col width="50">
							<aui:select name="dossierFileListOrderByType">
								<aui:option value="<%=StringPool.BLANK %>" />
								<aui:option value="<%= WebKeys.ORDER_BY_ASC %>" selected='<%= dossierFileListOrderByType.equals(WebKeys.ORDER_BY_ASC) %>'>
									<liferay-ui:message key="order-by-old-dossier-file"/>
								</aui:option>
								
								<aui:option value="<%= WebKeys.ORDER_BY_DESC %>" selected='<%= dossierFileListOrderByType.equals(WebKeys.ORDER_BY_DESC) %>'>
									<liferay-ui:message key="order-by-new-dossier-file"/>
								</aui:option>
							</aui:select>
						</aui:col>
					</aui:row>
				</liferay-ui:panel>
			</liferay-ui:panel-container>
		</c:when>
		<c:when test='<%=tabs2.equalsIgnoreCase("digital-signature") %>'>
			<aui:fieldset>
				<aui:select name="signatureType">
					<aui:option value="selectPoint" selected='<%= signatureType.equals("selectPoint") %>'>
						<liferay-ui:message key="selected-point"/>
					</aui:option>
					
					<aui:option value="fixAtPoint" selected='<%= signatureType.equals("fixAtPoint") %>'>
						<liferay-ui:message key="fix-at-point"/>
					</aui:option>
				</aui:select>
				
			</aui:fieldset>
			
			<div id="<portlet:namespace />fixatpoint">
				<aui:fieldset>
					<aui:row>
						<aui:col width="50">
							<aui:input name="offsetX" value="<%=offsetX %>"/>
						</aui:col>
						<aui:col width="50">
							<aui:input name="offsetY" value="<%=offsetY %>"/>
						</aui:col>
					</aui:row>
				</aui:fieldset>
			</div>
			
			<aui:fieldset>
				<aui:select name="characterAttach" multiple="true">		
					<aui:option value="image" selected='<%=ArrayUtil.contains(characterAttachs, "image") %>'>
						<liferay-ui:message key="image"/>
					</aui:option>
					
					<aui:option value="text" selected='<%= ArrayUtil.contains(characterAttachs, "text") %>'>
						<liferay-ui:message key="text" />
					</aui:option>
				</aui:select>
			</aui:fieldset>
			
			<aui:fieldset>
				<aui:select name="textPositionWithImageSign">
					<aui:option value="overlaps" selected='<%=textPositionWithImageSign.equals("overlaps") %>'>
						<liferay-ui:message key="overlaps" />
					</aui:option>
					<aui:option value="noOverlaps" selected='<%=textPositionWithImageSign.equals("noOverlaps") %>'>
						<liferay-ui:message key="no-overlaps" />
					</aui:option>
				</aui:select>
			</aui:fieldset>
		</c:when>
		<c:otherwise>
		
			
		</c:otherwise>
	</c:choose>
	
	<aui:button type="submit" name="Save" value="save"/>

</aui:form>

<aui:script>
	AUI().ready(function(A) {
		var signatureType = A.one('#<portlet:namespace />signatureType');
		var fixatpoint = A.one('#<portlet:namespace />fixatpoint');
		<portlet:namespace />checkShowCoordinate(signatureType, fixatpoint);
		signatureType.on('change', function() {
			<portlet:namespace />checkShowCoordinate(signatureType, fixatpoint);
		});
		
	});
	
	Liferay.provide(window, '<portlet:namespace />checkShowCoordinate', function(signatureType, fixatpoint) {
		if(signatureType.val() == 'selectPoint') {
			fixatpoint.hide();
		} else {
			fixatpoint.show();
		}
	})
</aui:script>
