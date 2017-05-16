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

<%@page import="org.opencps.datamgt.search.DictItemSearch"%>

<%@ include file="../../init.jsp"%>

<%
	long dictCollectionId = ParamUtil.getLong(request, DictItemSearchTerms.DICTCOLLECTION_ID);
	SearchContainer itemsListSearchContainer = (SearchContainer) request.getAttribute("itemsListSearchContainer");
	String searchKeyword = ParamUtil.getString(request, "searchKeyword");
	long itemLinkedId = ParamUtil.getLong(request, "itemLinkedId");
	int itemsStatus = ParamUtil.getInteger(request, "itemsStatus", 1);

	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL
			.setParameter("mvcPath",
					"/html/portlets/data_management/admin/display/dictitems.jsp");
	iteratorURL.setParameter(DictItemDisplayTerms.DICTCOLLECTION_ID,
			String.valueOf(dictCollectionId));
	iteratorURL.setParameter("actionKey", "ajax-load-dict-items");

	List<DictItem> dictItems = new ArrayList<DictItem>();

	int totalCount = 0;
	
%>

<div class="date-dict-items-message">
	<b><liferay-ui:message key="data-dict-items" /></b>
</div>
<div class="opencps-searchcontainer-wrapper-width-header default-box-shadow radius8 items-container">
	<liferay-ui:search-container 
		searchContainer="<%=itemsListSearchContainer == null ? 
				new DictItemSearch(renderRequest, SearchContainer.DEFAULT_DELTA, iteratorURL) : itemsListSearchContainer %>" 
		headerNames="STT,code,name,tree-index,action" 
		deltaConfigurable="false"
	>
	
		<liferay-ui:search-container-results>
			<%
				try{
					dictItems = DictItemLocalServiceUtil.searchBy_G_D_N_L_S(
							scopeGroupId, dictCollectionId, searchKeyword, itemLinkedId, itemsStatus, 
							searchContainer.getStart(), searchContainer.getEnd());
					totalCount = DictItemLocalServiceUtil.countBy_G_D_N_L_S(
							scopeGroupId, searchKeyword, dictCollectionId, itemLinkedId, itemsStatus);
				}catch(Exception e){
					_log.error(e);
				}
			
				total = totalCount;
				results = dictItems;
				pageContext.setAttribute("results", results);
				pageContext.setAttribute("total", total);
			%>
		</liferay-ui:search-container-results>	
			<liferay-ui:search-container-row 
				className="org.opencps.datamgt.model.DictItem" 
				modelVar="dictItem" 
				keyProperty="dictItemId"
			>
				<%
					row.setClassName("opencps-searchcontainer-row");
				
					String rowNumber = String.valueOf((row.getPos() + 1) 
							+ (searchContainer.getCur() - 1) * searchContainer.getDelta());
					
					String rowNumberDisplay = "<div class=\"text-center\"><a href=\"#\" class=\"edit_dictItem_link\" id=\""
								+ renderResponse.getNamespace() + "dictItemId_" + dictItem.getDictItemId() +"\" >" + rowNumber + "</a></div>";
					
					row.addText(rowNumberDisplay);
					
					String itemCodeDisplay = "<div class=\"text-center\"><a href=\"#\" class=\"edit_dictItem_link\" id=\""
							+ renderResponse.getNamespace() + "dictItemId_" + dictItem.getDictItemId() +"\" >" + dictItem.getItemCode() + "</a></div>";
					
					row.addText(itemCodeDisplay);
					
					String itemNameDisplay = "<a href=\"#\" class=\"edit_dictItem_link\" id=\""
							+ renderResponse.getNamespace() + "dictItemId_" + dictItem.getDictItemId() +"\" >" + dictItem.getItemName(locale) + "</a>";
					
					row.addText(itemNameDisplay);
					
					String treeIndexDisplay = "<a href=\"#\" class=\"edit_dictItem_link\" id=\""
							+ renderResponse.getNamespace() + "dictItemId_" + dictItem.getDictItemId() +"\" >" + dictItem.getTreeIndex() + "</a>";
					
					row.addText(treeIndexDisplay);
					
					//action column
					row.addJSP("center", SearchEntry.DEFAULT_VALIGN, 
							"/html/portlets/data_management/admin/ajax/_dictitem_actions.jsp", 
							config.getServletContext(), request, response);
				%>	
			</liferay-ui:search-container-row> 
		
		<liferay-ui:search-iterator type="opencs_page_iterator" />
	</liferay-ui:search-container>
</div>

<%!
	private Log _log = LogFactoryUtil.getLog("html.portlets.data_management.admin.dictitem.jsp");
%>

