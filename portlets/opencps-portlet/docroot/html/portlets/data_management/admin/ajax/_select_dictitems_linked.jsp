
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
	long dictCollectionId = ParamUtil.getLong(request, DictItemDisplayTerms.DICTCOLLECTION_ID);
	long dictItemId = ParamUtil.getLong(request, DictItemDisplayTerms.DICTITEM_ID);

	List<DictCollectionLink> collectionsLinked = new ArrayList<DictCollectionLink>();
	List<DictItemLink> itemsLinked = new ArrayList<DictItemLink>();
	try {
		collectionsLinked = DictCollectionLinkLocalServiceUtil
				.getByDictCollectionId(dictCollectionId);
		itemsLinked = DictItemLinkLocalServiceUtil
				.getByDictItemId(dictItemId);
	} catch (Exception e){
		_log.error(e);
	}
	
	DictCollection dictCollection = null;
	List<DictItem> dictItems = new ArrayList<DictItem>();
	List<DictItem> dictItemsOrdered = new ArrayList<DictItem>();
	
	for (DictCollectionLink linked : collectionsLinked){
		try {
			dictCollection = DictCollectionLocalServiceUtil
					.getDictCollection(linked.getDictCollectionLinkedId());
			%>
				<label class="expand-anchor"
					id='<%=renderResponse.getNamespace() + "expand-anchor_dictCollectionId_" +linked.getDictCollectionLinkedId() %>'
				>
					<%=dictCollection.getCollectionName(locale) %>
				</label>
			<%
			try {
				dictItems = DictItemLocalServiceUtil
						.getBy_D_P(dictCollection.getDictCollectionId(), 0, 
								QueryUtil.ALL_POS, QueryUtil.ALL_POS,
								DataMgtUtil.getDictItemOrderByComparator(DictItemDisplayTerms.SIBLING, WebKeys.ORDER_BY_ASC));
				dictItemsOrdered.clear();
				dictItemsOrdered = getDictItemsOrderBySibling(dictItemsOrdered, 
						dictItems, dictCollection.getDictCollectionId());
			} catch (Exception e){
				_log.error(e);
			}
			
			%>
				<div id='<%=renderResponse.getNamespace() + "expandable_" + linked.getDictCollectionLinkedId() %>'><ul>
			<%
					for (DictItem item : dictItemsOrdered){
						boolean checked = false;
						for (DictItemLink itemLinked : itemsLinked){
							if (item.getDictItemId() == itemLinked.getDictItemLinkedId()){
								checked = true;
								break;
							}
						}
						int level = StringUtil.count(item.getTreeIndex(), StringPool.PERIOD);
						String index = "|";
						for(int i = 0; i < level; i++){
							index += "__";
						}
						%>
							<li>
								<aui:input 
									name="dictItemLinked" 
									value="<%=item.getDictItemId() %>"
									label=""
									type="checkbox" 
									inlineField="true"
									checked="<%=checked %>"
									cssClass='<%=!checked ? "no-linked-to-selected-item" : "" %>'
								/>
								<%=index + item.getItemName(locale) %>
							</li>
						<%
					}
			%>
				</ul></div>
			<%
		} catch (Exception e){
			_log.error(e);
		}
	}
%>

<%!
	private List<DictItem> getDictItemsOrderBySibling(List<DictItem> result, 
				List<DictItem> items, long dictCollectionId) 
			throws Exception{
		
		for (DictItem item : items){
			result.add(item);
			List<DictItem> subItems = DictItemLocalServiceUtil
					.getBy_D_P(dictCollectionId, item.getDictItemId(), 
							QueryUtil.ALL_POS, QueryUtil.ALL_POS,
							DataMgtUtil.getDictItemOrderByComparator(DictItemDisplayTerms.SIBLING, WebKeys.ORDER_BY_ASC));
			getDictItemsOrderBySibling(result, subItems, dictCollectionId);
		}
	
		return result;
	}

	private Log _log = LogFactoryUtil.getLog("html.portlets.data_management.admin.ajax.select_dictitems_linked.jsp");
%>

