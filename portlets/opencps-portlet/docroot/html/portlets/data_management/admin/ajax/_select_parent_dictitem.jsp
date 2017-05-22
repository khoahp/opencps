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

<%@page import="java.util.Locale"%>

<%@ include file="../../init.jsp"%>

<%
	long dictCollectionId = ParamUtil.getLong(request, DictItemDisplayTerms.DICTCOLLECTION_ID);
	long dictItemId = ParamUtil.getLong(request, DictItemDisplayTerms.DICTITEM_ID);
	String keywordSearchItemParent = ParamUtil.getString(request, "keywordSearchItemParent").trim();
	
	DictItem curDictItem = null;
	long parentId = 0;
	List<DictItem> dictItems = new ArrayList<DictItem>();
	List<DictItem> dictItemsOrdered = new ArrayList<DictItem>();

	try{
		if(dictItemId > 0){
			curDictItem = DictItemLocalServiceUtil.getDictItem(dictItemId);
			parentId = curDictItem.getParentItemId();
		}
		if(dictCollectionId > 0){
			dictItems = DictItemLocalServiceUtil
					.getBy_D_P(dictCollectionId, 0, 
							QueryUtil.ALL_POS, QueryUtil.ALL_POS,
							DataMgtUtil.getDictItemOrderByComparator(
									DictItemDisplayTerms.SIBLING, WebKeys.ORDER_BY_ASC));
			dictItemsOrdered = getDictItemsOrderBySibling(dictItemsOrdered, 
					dictItems, dictCollectionId, keywordSearchItemParent);
			
		}
	}catch(Exception e){
		_log.error(e);
	} 
%>

<div>
	<ul>
		<%
			if(dictItems != null){
				for(DictItem dictItem : dictItemsOrdered){
					if((curDictItem != null && dictItem.getDictItemId() == curDictItem.getDictItemId())||
							(curDictItem != null && dictItem.getTreeIndex().contains(curDictItem.getDictItemId() + StringPool.PERIOD))){
						continue;
					}
					
					int level = StringUtil.count(dictItem.getTreeIndex(), StringPool.PERIOD);
					String index = "";
					for(int i = 0; i < level; i++){
						index += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
					}
					%>
						<li class="select-dict-item-parent" id='<%=renderResponse.getNamespace() + "parentItemId_" + dictItem.getDictItemId() %>' >
							<a href="#" ><%=index + dictItem.getItemName(locale) %></a>
						</li>
					<%
				}
				if (dictItemsOrdered.size() == 0){
					%>
						<li><liferay-ui:message key="no-dict-item-were-found" /></li>
					<%
				}
			}
		%>
	</ul>
</div>

<%!
	private List<DictItem> getDictItemsOrderBySibling(List<DictItem> result, 
				List<DictItem> items, long dictCollectionId, String keyword) 
			throws Exception{
		
		for (DictItem item : items){
			if (Validator.isNull(keyword)){
				result.add(item);
				List<DictItem> subItems = DictItemLocalServiceUtil
						.getBy_D_P(dictCollectionId, item.getDictItemId(), 
								QueryUtil.ALL_POS, QueryUtil.ALL_POS,
								DataMgtUtil.getDictItemOrderByComparator(
										DictItemDisplayTerms.SIBLING, WebKeys.ORDER_BY_ASC));
				getDictItemsOrderBySibling(result, subItems, dictCollectionId, keyword);
			} else {
				if (item.getItemName(new Locale("vi", "VN")).toLowerCase().contains(keyword.toLowerCase()) 
						|| checkChildrenItemNameHasKeyword(item.getDictItemId(), keyword)){
					result.add(item);
					List<DictItem> subItems = DictItemLocalServiceUtil
							.getBy_D_P(dictCollectionId, item.getDictItemId(), 
									QueryUtil.ALL_POS, QueryUtil.ALL_POS,
									DataMgtUtil.getDictItemOrderByComparator(
											DictItemDisplayTerms.SIBLING, WebKeys.ORDER_BY_ASC));
					getDictItemsOrderBySibling(result, subItems, dictCollectionId, keyword);
				}
			}
		}
		
		return result;
	}

	private boolean checkChildrenItemNameHasKeyword
				(long parentId, String keyword) throws Exception{
		List<DictItem> children = DictItemLocalServiceUtil
				.getDictItemsByParentItemId(parentId);
		for (DictItem child : children){
			if (child.getItemName(new Locale("vi", "VN")).toLowerCase()
					.contains(keyword.toLowerCase())){
				return true;
			}
			if (checkChildrenItemNameHasKeyword(child.getDictItemId(), keyword)){
				return true;
			}
		}
		return false;
	}

	private Log _log = LogFactoryUtil.getLog("html.portlets.data_management.admin._select_parent_dictitem.jsp");
%>
