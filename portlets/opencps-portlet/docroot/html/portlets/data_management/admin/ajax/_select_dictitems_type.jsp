
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

	List<DictCollectionType> collectionsTypes = new ArrayList<DictCollectionType>();
	List<DictItemType> itemsType = new ArrayList<DictItemType>();
	try {
		collectionsTypes = DictCollectionTypeLocalServiceUtil
				.getByDictCollectionId(dictCollectionId);
		itemsType = DictItemTypeLocalServiceUtil
				.getByDictItemId(dictItemId);
	} catch (Exception e){
		_log.error(e);
	}
	
	DictCollection dictCollection = null;
	List<DictItem> dictItems = new ArrayList<DictItem>();
	List<DictItem> dictItemsOrdered = new ArrayList<DictItem>();
	
%>

<div class="opencps-searchcontainer-wrapper default-box-shadow radius8 data-manager-action">
	<div class="openCPSTree yui3-widget component tree-view tree-drag-drop">
		<div class="scrollbar-datamgt">
			<ul class="tree-view-content tree-drag-drop-content tree-file tree-root-container" >
			<%
				for (DictCollectionType type : collectionsTypes){
					try {
						dictCollection = DictCollectionLocalServiceUtil
								.getDictCollection(type.getDictCollectionLinkedId());
						%>
							<li class="tree-node bold" >
								<%=dictCollection.getCollectionName(locale) %>
							</li>
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
						
						for (DictItem item : dictItemsOrdered){
							boolean checked = false;
							for (DictItemType itemType : itemsType){
								if (item.getDictItemId() == itemType.getDictItemLinkedId()){
									checked = true;
									break;
								}
							}
							int level = StringUtil.count(item.getTreeIndex(), StringPool.PERIOD);
							String index = "";
							for(int i = 0; i < level; i++){
								index += "&&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
							}
							%>
								<li class="tree-node click-select-dict-item-type" 
									id='<%=renderResponse.getNamespace() + "dictItemId_" + item.getDictItemId() %>'
									title='<%=item.getItemName(locale) %>'
								>
									<aui:input 
										name="dictItemLinked" 
										value="<%=item.getDictItemId() %>"
										label=""
										type="checkbox" 
										inlineField="true"
										inlineLabel="true"
										checked="<%=checked %>"
										cssClass='<%=!checked ? "unchecked-checkbox" : "" %>'
									/>
									<liferay-ui:message key="<%=index + item.getItemName(locale) %>" />
								</li>
							<%
						}
					} catch (Exception e){
						_log.error(e);
					}
				}
			%>
			</ul>
		</div>
	</div>
</div>

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

	private Log _log = LogFactoryUtil.getLog("html.portlets.data_management.admin.ajax._select_dictitems_type.jsp.jsp");
%>

