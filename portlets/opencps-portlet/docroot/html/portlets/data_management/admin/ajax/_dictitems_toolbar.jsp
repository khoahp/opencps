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
	
	List<DictCollectionType> collectionsTypes = new ArrayList<DictCollectionType>();
	try {
		collectionsTypes = DictCollectionTypeLocalServiceUtil
				.getByDictCollectionId(dictCollectionId);
	} catch (Exception e){
		_log.error(e);
	}
%>

<div>
	<p class="breadcrumb bold">
		<liferay-ui:message key='dictcollection' /> > 
		<%=collection != null ? collection.getCollectionName() : StringPool.BLANK %> > 
		<liferay-ui:message key='list' />
	</p>
</div>

<<liferay-ui:message key="lookups-items" />
<div>
	<aui:row>
		<aui:col width='<%=collectionsTypes.size() == 0 ? 50 : 30 %>' >
			<aui:input 
				name="item-name" 
				value="<%=searchKeyword %>" 
				placeholder='<%= LanguageUtil.get(locale, "item-name") %>' 
				cssClass="input100"
			/>
		</aui:col>
		<aui:col width="30" cssClass='<%=collectionsTypes.size() == 0 ? "hidden" : "" %>' >
			<%
			if (collectionsTypes.size() > 0){
				%>
				<aui:select name="item-linked">
					<aui:option value="0" />
					<%
					DictCollection dictCollection = null;
					List<DictItem> dictItemsL = new ArrayList<DictItem>();
					List<DictItem> dictItemsOrdered = new ArrayList<DictItem>();
					
					for (DictCollectionType type : collectionsTypes){
						
						try {
							dictCollection = DictCollectionLocalServiceUtil
									.getDictCollection(type.getDictCollectionLinkedId());
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
								String index = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
								for(int i = 0; i < level; i++){
									index += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
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
						} catch (Exception e){}
					}
					%>
				</aui:select>
				<%
			}
			%>
		</aui:col>
		
		<aui:col width='<%=collectionsTypes.size() == 0 ? 50 : 30 %>'>
			<aui:select name="itemsStatusInUsed" label="status">
				<aui:option value="0" label="draf" selected="<%=itemsStatus == 0 %>" />
				<aui:option value="1" label="in-used" selected="<%=itemsStatus == 1 %>" />
				<aui:option value="2" label="no-used" selected="<%=itemsStatus == 2 %>" />
			</aui:select>
		</aui:col>
	</aui:row>
	
	<aui:button name="search-item-button" value="search" type="submit" cssClass="search-icon"/>
	<aui:button id='<%=renderResponse.getNamespace() + "add-item" %>' type="submit" value="add-item" cssClass="plus-icon"/>
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