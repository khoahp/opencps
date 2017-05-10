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
<%@page import="org.opencps.datamgt.service.DictCollectionLinkLocalServiceUtil"%>
<%@page import="org.opencps.datamgt.model.DictCollectionLink"%>
<%@page import="org.opencps.util.WebKeys"%>
<%@page import="org.opencps.datamgt.util.DataMgtUtil"%>
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
<%@page import="org.opencps.datamgt.search.DictItemDisplayTerms"%>
<%@page import="org.opencps.datamgt.search.DictItemSearchTerms"%>
<%@page import="org.opencps.datamgt.search.DictItemSearch"%>
<%@page import="org.opencps.datamgt.model.DictItem"%>
<%@page import="org.opencps.datamgt.service.DictItemLocalServiceUtil"%>

<%@ include file="../../init.jsp"%>

<%
	long dictCollectionId = ParamUtil.getLong(request, DictItemSearchTerms.DICTCOLLECTION_ID);	
	String searchKeyword = ParamUtil.getString(request, "searchKeyword");
	long itemLinkedId = ParamUtil.getLong(request, "itemLinkedId");
	int itemsStatus = ParamUtil.getInteger(request, "itemsStatus", 1);

	DictCollection collection = null;
	try {
		collection = DictCollectionLocalServiceUtil
				.getDictCollection(dictCollectionId);
	} catch (Exception e) {}
	
	List<DictCollectionLink> collectionsLinked = new ArrayList<DictCollectionLink>();
	try {
		collectionsLinked = DictCollectionLinkLocalServiceUtil
				.getByDictCollectionId(dictCollectionId);
	} catch (Exception e){
		_log.error(e);
	}
%>

<div>
	<p class="breadcrumb">
		<liferay-ui:message key='dictcollection' /> > 
		<%=collection != null ? collection.getCollectionName() : StringPool.BLANK %> > 
		<liferay-ui:message key='list' />
	</p>
</div>

<div>
	<aui:button id='<%=renderResponse.getNamespace() + "add-item" %>' type="submit" value="add-dict-item" />
	<aui:row>
		<aui:col width="50">
			<aui:input 
				name="item-name" 
				value="<%=searchKeyword %>" 
				placeholder='<%= LanguageUtil.get(locale, "name") %>' 
				cssClass="input100"
			/>
		</aui:col>
		<aui:col width="50" cssClass='<%=collectionsLinked.size() == 0 ? "hidden" : "" %>' >
			<%
			if (collectionsLinked.size() > 0){
				%>
				<aui:select name="item-linked">
					<aui:option value="0" ></aui:option>
					<%
					DictCollection dictCollection = null;
					List<DictItem> dictItemsL = new ArrayList<DictItem>();
					List<DictItem> dictItemsOrdered = new ArrayList<DictItem>();
					
					for (DictCollectionLink linked : collectionsLinked){
						
						dictCollection = DictCollectionLocalServiceUtil
								.getDictCollection(linked.getDictCollectionLinkedId());
						%>
						<aui:option value="0" disabled="true"><%=dictCollection.getCollectionName(locale) %></aui:option>
						<%
						try {
							dictItemsL = DictItemLocalServiceUtil
									.getBy_D_P(dictCollection.getDictCollectionId(), 0, 
											QueryUtil.ALL_POS, QueryUtil.ALL_POS,
											DataMgtUtil.getDictItemOrderByComparator(
													DictItemDisplayTerms.SIBLING, WebKeys.ORDER_BY_ASC));
							dictItemsOrdered.clear();
							dictItemsOrdered = getDictItemsOrderBySibling(dictItemsOrdered, 
									dictItemsL, dictCollection.getDictCollectionId());
						} catch (Exception e){
							_log.error(e);
						}
						for (DictItem item : dictItemsOrdered){
							int level = StringUtil.count(item.getTreeIndex(), StringPool.PERIOD);
							String index = "|__";
							for(int i = 0; i < level; i++){
								index += "__";
							}
							%>
							<aui:option 
								value="<%=item.getDictItemId() %>" 
								selected="<%=item.getDictItemId() == itemLinkedId %>"
							>
								<%=index + item.getItemName(locale) %>
							</aui:option>
							<%
						}
					}
					%>
				</aui:select>
				<%
			}
			%>
		</aui:col>
		
		<aui:col width="50">
			<aui:select name="itemsStatusInUsed">
				<aui:option value="0" label="draf" selected="<%=itemsStatus == 0 %>" />
				<aui:option value="1" label="in-used" selected="<%=itemsStatus == 1 %>" />
				<aui:option value="2" label="no-used" selected="<%=itemsStatus == 2 %>" />
			</aui:select>
		</aui:col>
	</aui:row>
	
	<aui:button name="search-item-button" value="search" type="submit"/>
</div>

<div id='<%=renderResponse.getNamespace() + "dictItems_container" %>'></div>

<%!
	private List<DictItem> getDictItemsOrderBySibling(List<DictItem> 
			result, List<DictItem> items, long dictCollectionId) 
			throws Exception{
		for (DictItem item : items){
		result.add(item);
		List<DictItem> subItems = DictItemLocalServiceUtil
			.getBy_D_P(dictCollectionId, item.getDictItemId(), 
					QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					DataMgtUtil.getDictItemOrderByComparator(
							DictItemDisplayTerms.SIBLING, WebKeys.ORDER_BY_ASC));
		getDictItemsOrderBySibling(result, subItems, dictCollectionId);
		}
		
		return result;
	}
	private Log _log = LogFactoryUtil.getLog("html.portlets.data_management.admin.dictitem.jsp");
%>