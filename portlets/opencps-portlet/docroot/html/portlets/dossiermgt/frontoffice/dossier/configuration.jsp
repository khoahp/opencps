

<%@page import="org.opencps.util.PortletPropsValues"%>
<%@page import="org.opencps.util.PortletUtil"%>
<%@page import="org.opencps.datamgt.model.DictItem"%>
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

<%@page import="com.liferay.portal.model.Layout"%>
<%@page import="com.liferay.portal.service.LayoutLocalServiceUtil"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="org.opencps.util.PortletConstants"%>
<%@page import="org.opencps.util.WebKeys"%>
<%@page import="org.opencps.dossiermgt.search.DossierFileDisplayTerms"%>

<%@ include file="../init.jsp" %>

<liferay-ui:success key="potlet-config-saved" message="portlet-configuration-have-been-successfully-saved" />

<liferay-portlet:actionURL var="configurationActionURL" portletConfiguration="true"/>

<%
	boolean privateLayout = true;
	
	List<Layout> privLayouts = LayoutLocalServiceUtil.getLayouts(scopeGroupId, true);
	List<Layout> pubLayouts = LayoutLocalServiceUtil.getLayouts(scopeGroupId, false);
	
	List<Layout> allLayout = new ArrayList<Layout>();
	
	for (Layout privLayout : privLayouts) {
		allLayout.add(privLayout);
	}

	for (Layout pubLayout : pubLayouts) {
		allLayout.add(pubLayout);
	}
	
	List<DictItem> dictItems = PortletUtil.getDictItemInUseByCode(themeDisplay.getScopeGroupId(), 
			PortletPropsValues.DATAMGT_MASTERDATA_SERVICE_DOMAIN, 
			PortletConstants.TREE_VIEW_DEFAULT_ITEM_CODE);
	
	int itemsToDisplay_cfg = GetterUtil.getInteger(portletPreferences.getValue("itemsToDisplay", "2"));
	
	String templatesToDisplay_cfg = GetterUtil.getString(portletPreferences.getValue("templatesToDisplay", "default"));
	
	int timeToReLoad_cfg = GetterUtil.getInteger(portletPreferences.getValue("timeToReLoad", "0"));
	
%>

<aui:form action="<%= configurationActionURL %>" method="post" name="configurationForm">
	
	<datamgt:ddr 
		depthLevel="1" 
		dictCollectionCode="DOSSIER_STATUS" 
		showLabel="<%=false%>"
		emptyOptionLabels="dossier-status"
		itemsEmptyOption="true"
		itemNames="dossierStatusConfig"
		selectedItems="<%= dossierStatusConfig %>"
		optionValueType="code"
		cssClass="search-input select-box input100"
	/>
		
	<aui:select name="plid" id="plid">
		<%
			for (Layout lout : allLayout) {
		%>
			<aui:option value="<%= lout.getPlid() %>" selected="<%=lout.getPlid() == plidRes %>"><%= lout.getName(locale) %></aui:option>
		<%
			}
		%>
	</aui:select>

	<aui:select name="itemsToDisplay" id="itemsToDisplay">
		<%
			for (int iTems = 2 ; iTems < 10; iTems ++) {
		%>
			<aui:option selected="<%= itemsToDisplay_cfg == iTems %>" value="<%= iTems %>"><%= iTems %></aui:option>
		<%
			}
		%>
	</aui:select>
	
	<aui:select name="templatesToDisplay" id="templatesToDisplay">
			
		<aui:option selected="<%= templatesToDisplay_cfg.equals(\"default\") %>" value="default">default</aui:option>
		
		<aui:option selected="<%= templatesToDisplay_cfg.equals(\"20_80\") %>" value="20_80">20_80</aui:option>
	
	</aui:select>
	
	<aui:select name="timeToReLoad" id="timeToReLoad">
			
		<%
			for (int iTems = 0 ; iTems < 60; iTems += 5) {
		%>
			<aui:option selected="<%= timeToReLoad_cfg == iTems %>" value="<%= iTems %>"><%= iTems %></aui:option>
		<%
			}
		%>
	
	</aui:select>
	
	<aui:row>
		<aui:select name="orderFieldDossierFile">
			
			<aui:option value="<%=StringPool.BLANK %>" />
				
			<aui:option value='<%=DossierFileDisplayTerms.DOSSIER_FILE_DATE %>' selected='<%= orderFieldDossierFile.equals(DossierFileDisplayTerms.DOSSIER_FILE_DATE) %>'>
				<liferay-ui:message key="order-by-dossier-file-date"/>
			</aui:option>
			
		</aui:select>
	</aui:row>
	
	<aui:row>
		<div id = '<portlet:namespace/>hideOrderBydDossierFile' >
			<aui:select name="orderBydDossierFile">
				<aui:option value="<%=StringPool.BLANK %>" />
				<aui:option value="<%= WebKeys.ORDER_BY_ASC %>" selected='<%= orderBydDossierFile.equals(WebKeys.ORDER_BY_ASC) %>'>
					<liferay-ui:message key="order-by-old-dossier-file"/>
				</aui:option>
				
				<aui:option value="<%= WebKeys.ORDER_BY_DESC %>" selected='<%= orderBydDossierFile.equals(WebKeys.ORDER_BY_DESC) %>'>
					<liferay-ui:message key="order-by-new-dossier-file"/>
				</aui:option>
			</aui:select>
		</div>
	</aui:row>
	
	<aui:input 
		type="checkbox" 
		name="displayDossierNo"
		value='<%= displayDossierNo %>'
	/>
	
	<aui:input 
		type="checkbox"
		name="displayRecentlyResultWhenSearch" 
		value='<%= displayRecentlyResultWhenSearch %>'
	/>
	
	<aui:input 
		type="checkbox"
		name="showVersionItem" 
		value="<%= showVersionItem %>"
	/>
	
	<aui:input 
		type="checkbox"
		name="showBackToListButton" 
		value="<%= showBackToListButton %>"
	/>
	
	<aui:input 
		type="checkbox"
		name="showServiceDomainIdTree" 
		value="<%= showServiceDomainIdTree %>"
	/>
	
	<aui:input 
		type="checkbox"
		name="hideTabDossierFile" 
		value="<%= hideTabDossierFile %>"
	/>
	
	<aui:input 
		type="checkbox"
		name="showTabDossierResultFirst" 
		value="<%=showTabDossierResultFirst %>"
	/>
	
	<aui:input 
		type="checkbox"
		name="hiddenTreeNodeEqualNone" 
		value="<%=hiddenTreeNodeEqualNone %>"
	/>
	
	<aui:input 
		type="checkbox"
		name="allowResultQuickView" 
		value="<%=allowResultQuickView %>"
	/>
	
	<aui:input 
		type="checkbox"
		name="allowQuickCreateDossier" 
		value="<%=allowQuickCreateDossier %>"
	/>
	
	<aui:input 
		type="text"
		name="fileTypes" 
		value="<%=fileTypes %>"
	/>
	
	<aui:row>
		<aui:col>
			<aui:input 
				type="text"
				name="maxTotalUploadFileSize" 
				value="<%=maxTotalUploadFileSize %>"
			>
				<aui:validator name="number"></aui:validator>
			</aui:input>
		</aui:col>
		<aui:col>
			<aui:select 
				name="maxTotalUploadFileSizeUnit" 
				id="maxTotalUploadFileSizeUnit"
			>
				<aui:option value="<%=PortletConstants.SIZE_UNIT_B %>"
					selected="<%=maxTotalUploadFileSizeUnit.equals(PortletConstants.SIZE_UNIT_B)%>"
				>B</aui:option>
				<aui:option value="<%=PortletConstants.SIZE_UNIT_KB %>"
					selected="<%=maxTotalUploadFileSizeUnit.equals(PortletConstants.SIZE_UNIT_KB)%>"
				>kB</aui:option>
				<aui:option value="<%=PortletConstants.SIZE_UNIT_MB %>" 
					selected="<%=maxTotalUploadFileSizeUnit.equals(PortletConstants.SIZE_UNIT_MB)%>"
				>MB</aui:option>
				<aui:option value="<%=PortletConstants.SIZE_UNIT_GB %>"
					selected="<%=maxTotalUploadFileSizeUnit.equals(PortletConstants.SIZE_UNIT_GB)%>"
				>GB</aui:option>
			</aui:select>
		</aui:col>
	</aui:row>
	
	<aui:row>
		<aui:col>
			<aui:input 
				type="text"
				name="maxUploadFileSize" 
				value="<%=maxUploadFileSize %>"
			>
				<aui:validator name="number"></aui:validator>
			</aui:input>
		</aui:col>
		<aui:col>
			<aui:select 
				name="maxUploadFileSizeUnit" 
				id="maxUploadFileSizeUnit"
			>
				<aui:option value="<%=PortletConstants.SIZE_UNIT_B %>"
					selected="<%=maxTotalUploadFileSizeUnit.equals(PortletConstants.SIZE_UNIT_B)%>"
				>B</aui:option>
				<aui:option value="<%=PortletConstants.SIZE_UNIT_KB %>"
					selected="<%=maxTotalUploadFileSizeUnit.equals(PortletConstants.SIZE_UNIT_KB)%>"
				>kB</aui:option>
				<aui:option value="<%=PortletConstants.SIZE_UNIT_MB %>" 
					selected="<%=maxTotalUploadFileSizeUnit.equals(PortletConstants.SIZE_UNIT_MB)%>"
				>MB</aui:option>
				<aui:option value="<%=PortletConstants.SIZE_UNIT_GB %>"
					selected="<%=maxTotalUploadFileSizeUnit.equals(PortletConstants.SIZE_UNIT_GB)%>"
				>GB</aui:option>
			</aui:select>
		</aui:col>
	</aui:row>
	
	<aui:select name="itemCode_cfg" id="itemCode_cfg">
		<aui:option selected="<%= Validator.isNull(itemCode_cfg)  %>" value=""> </aui:option>
		<%
			for (DictItem dictItem : dictItems) {
		%>
			<aui:option selected="<%= itemCode_cfg == dictItem.getItemCode() %>" value="<%= dictItem.getItemCode() %>"><%= dictItem.getItemName(locale) %></aui:option>
		<%
			}
		%>
	</aui:select>
	
	<aui:select name="war_opencpsportlet_26_cfg" id="war_opencpsportlet_26_cfg">
		<aui:option selected="<%= Validator.isNull(war_opencpsportlet_26_cfg)  %>" value=""> </aui:option>
		<%
			for (Layout lout : pubLayouts) {
		%>
			<aui:option selected="<%= war_opencpsportlet_26_cfg.equals(String.valueOf(lout.getPlid())) %>" value="<%= lout.getPlid() %>"><%= lout.getName(locale) %></aui:option>
		<%
			}
		%>
	</aui:select>
	<aui:button type="submit" name="Save" value="save"></aui:button>

</aui:form>

<aui:script>
	
	AUI().ready(function(A) {
		var orderFieldDossierFile = A.one('#<portlet:namespace />orderFieldDossierFile');
		var orderBydDossierFile = A.one('#<portlet:namespace />orderBydDossierFile');
		var hideOrderBydDossierFile = A.one('#<portlet:namespace />hideOrderBydDossierFile');
		<portlet:namespace />checkHideOrderBydDossierFile(orderFieldDossierFile, orderBydDossierFile, hideOrderBydDossierFile);
		if(orderFieldDossierFile) {
			orderFieldDossierFile.on('change', function() {
				<portlet:namespace />checkHideOrderBydDossierFile(orderFieldDossierFile, orderBydDossierFile, hideOrderBydDossierFile);
			});
		}
		
	});
	
	 Liferay.provide(window, '<portlet:namespace />checkHideOrderBydDossierFile', function(orderFieldDossierFile, orderBydDossierFile, hideOrderBydDossierFile) {
		if(orderFieldDossierFile.val() == '') {
			hideOrderBydDossierFile.hide();
		} else {
			hideOrderBydDossierFile.show();
		}
	});
</aui:script>
