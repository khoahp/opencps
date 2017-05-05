<%@page import="org.opencps.util.WebKeys"%>
<%@page import="org.opencps.datamgt.util.DataMgtUtil"%>
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

<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@page import="org.opencps.datamgt.service.DictCollectionLocalServiceUtil"%>
<%@page import="org.opencps.datamgt.model.DictCollection"%>
<%@page import="com.liferay.portal.kernel.dao.search.SearchEntry"%>
<%@page import="java.util.List"%>
<%@page import="javax.portlet.PortletURL"%>
<%@page import="com.liferay.portal.kernel.dao.search.SearchContainer"%>
<%@page import="com.liferay.portal.kernel.log.LogFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.log.Log"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.liferay.util.dao.orm.CustomSQLUtil"%>
<%@page import="org.opencps.datamgt.search.DictItemDisplayTerms"%>
<%@page import="org.opencps.datamgt.search.DictItemSearchTerms"%>
<%@page import="org.opencps.datamgt.search.DictItemSearch"%>
<%@page import="org.opencps.datamgt.model.DictItem"%>
<%@page import="org.opencps.datamgt.service.DictItemLocalServiceUtil"%>

<%@ include file="../../init.jsp"%>

<%
	long dictCollectionId = ParamUtil.getLong(request, DictItemSearchTerms.DICTCOLLECTION_ID);
	SearchContainer itemsListSearchContainer = (SearchContainer) request.getAttribute("itemsListSearchContainer");
	
	DictCollection collection = null;
	try {
		collection = DictCollectionLocalServiceUtil.getDictCollection(dictCollectionId);
	} catch (Exception e) {}
	
	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter("mvcPath", "/html/portlets/data_management/admin/display/dictitems.jsp");
	iteratorURL.setParameter(DictItemDisplayTerms.DICTCOLLECTION_ID, String.valueOf(dictCollectionId));
	iteratorURL.setParameter("actionKey", "ajax-load-dict-items");
	
	List<DictItem> dictItems = new ArrayList<DictItem>();
	
	int totalCount = 0;
%>

<div>
	<p><liferay-ui:message key='dictcollection' /> > <%=collection != null ? collection.getCollectionName() : StringPool.BLANK %> > <liferay-ui:message key='list' /></p>
</div>

<div>
	<aui:button id='<%=renderResponse.getNamespace() + "add-item" %>' type="submit" value="add-item" />
	<span><aui:input name="item-name" placeholder='<%= LanguageUtil.get(locale, "name") %>' /></span>
	<span><aui:button name="search-item-button" value="search" /></span>
</div>

<div class="opencps-searchcontainer-wrapper-width-header default-box-shadow radius8 items-container">
	<liferay-ui:search-container 
		searchContainer="<%=itemsListSearchContainer == null ? new DictItemSearch(renderRequest, SearchContainer.DEFAULT_DELTA, iteratorURL) : itemsListSearchContainer %>" 
		headerNames="STT,code,name,tree-index,action"
	>
	
		<liferay-ui:search-container-results>
			<%
				DictItemSearchTerms searchTerms = (DictItemSearchTerms)searchContainer.getSearchTerms();
				
				String[] itemNames = null;
				
				if(Validator.isNotNull(searchTerms) && Validator.isNotNull(searchTerms.getKeywords())){
					itemNames = CustomSQLUtil.keywords(searchTerms.getKeywords());
				}
				
				try{
					%>
						<%@include file="/html/portlets/data_management/admin/dictitem_search_results.jspf" %>
					<%
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
					
					//id column
					row.addText(String.valueOf((row.getPos() + 1) 
							+ (searchContainer.getCur() - 1) * searchContainer.getDelta()));
					
					row.addText(dictItem.getItemCode());
					
					row.addText(dictItem.getItemName(locale));
					
					row.addText(dictItem.getTreeIndex());
					
					//action column
					row.addJSP("center", SearchEntry.DEFAULT_VALIGN, 
							"/html/portlets/data_management/admin/ajax/_dictitem_actions.jsp", 
							config.getServletContext(), request, response);
				%>	
			</liferay-ui:search-container-row> 
		
		<liferay-ui:search-iterator type="opencs_page_iterator"/>
	</liferay-ui:search-container>
</div>

<%!
	private Log _log = LogFactoryUtil.getLog("html.portlets.data_management.admin.dictitem.jsp");
%>

