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

<%@page import="com.liferay.portal.kernel.language.UnicodeLanguageUtil"%>
<%@page import="javax.portlet.WindowState"%>
<%@page import="org.opencps.datamgt.search.DictItemDisplayTerms"%>
<%@page import="javax.portlet.PortletURL"%>
<%@page import="javax.portlet.PortletRequest"%>
<%@page import="org.opencps.util.WebKeys"%>
<%@page import="com.liferay.portlet.PortletURLFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@page import="com.liferay.portal.kernel.log.LogFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.log.Log"%>

<%@ include file="../../init.jsp"%>

<div class="row-fluid">
	<div class="span3" id="<portlet:namespace/>anchor-scroll">
		<div class="opencps-searchcontainer-wrapper default-box-shadow radius8">
			<div class="openCPSTree yui3-widget component tree-view tree-drag-drop">
				<aui:button type="submit" value="add-collection" onClick="editDictCollection()"/>
				<aui:input name="collection-name" placeholder='<%= LanguageUtil.get(locale, "name") %>' />
				<aui:button name="search-button" value="search" />
				<div id='<%=renderResponse.getNamespace() + "collections-container" %>' ></div>
			</div>
		</div>
		
		<!-- update items sibling -->
		<liferay-portlet:renderURL var="updateItemsURL">
			<liferay-portlet:param name="mvcPath" value="/html/portlets/data_management/admin/display/update_items.jsp"/>
		</liferay-portlet:renderURL>
		<aui:button href="<%=updateItemsURL.toString() %>" value="update-data-items"/>
	</div>
	
	<div class="span9">
		<div class="opencps-searchcontainer-wrapper default-box-shadow radius8">
			<div id='<%=renderResponse.getNamespace() + "collection-detail" %>' ></div>
		</div>
	</div>
</div>

<%
	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter("mvcPath", "/html/portlets/data_management/admin/ajax/_dictitems.jsp");
	iteratorURL.setWindowState(LiferayWindowState.EXCLUSIVE);
	iteratorURL.setParameter("actionKey", "ajax-load-dict-items");
%>

<aui:script>
	var A = AUI();
	
	AUI().ready('aui-base','liferay-portlet-url','aui-io', function(A){
		getDictCollections();
		getDictCollectionDetail();
		
		if (A.one('#<portlet:namespace/>search-button')){
			A.one('#<portlet:namespace/>search-button').on('click', function(){
				var collectionName = A.one('#<portlet:namespace/>collection-name') ? 
						A.one('#<portlet:namespace/>collection-name').attr('value') : '';
				getDictCollections(collectionName);
			});
		}
		if (A.one('#<portlet:namespace/>collection-name')){
			A.one('#<portlet:namespace/>collection-name').on('change', function(){
				var collectionName = A.one('#<portlet:namespace/>collection-name') ?
						A.one('#<portlet:namespace/>collection-name').attr('value') : '';
				getDictCollections(collectionName);
			});
		}
	});
	
	//////// functions list
	// 'getDictCollectionDetail' : load detail of the dict collection
	// 'getDictItems' : load dict item list of the dict colleciton
	// 'getDictCollections' : load dict collection list and push into the left div
	// 'editDictCollection' : load page edit collection
	// 'updateDictCollection' : do update dict collection
	// 'editDictItem' : load page edit dict item
	
	// 'scrollWindow'
	
	var selectedDictCollectionId = 0;	
	var updateDictCollectionId = 0;
	var selectedDictItemId = 0;
	var needConfirnChangeView = false;
	
	Liferay.provide(window, 'getDictCollectionDetail', function(collectionId){
		if (needConfirnChangeView){
			if (!confirm(Liferay.Language.get('are-you-sure'))){
				return;
			}
		}
		needConfirnChangeView = false;
		
		var loadingMask = new A.LoadingMask(
			{
				'strings.loading': '<%= UnicodeLanguageUtil.get(pageContext, "...") %>',
				target: A.one('#<portlet:namespace/>collection-detail')
			}
		);
		loadingMask.show();
		
		var portletURL = Liferay.PortletURL.createURL('<%= PortletURLFactoryUtil.create(request, WebKeys.DATA_MANAGEMENT_ADMIN_PORTLET, themeDisplay.getPlid(), PortletRequest.RENDER_PHASE) %>');
		portletURL.setParameter("mvcPath", "/html/portlets/data_management/admin/ajax/_dictcollection_detail.jsp");
		portletURL.setWindowState("<%=LiferayWindowState.EXCLUSIVE.toString()%>"); 
		portletURL.setPortletMode("normal");
		portletURL.setParameter("collectionId", collectionId);
		
		A.io.request(
			portletURL.toString(),
			{
			    dataType: 'json',
			    data: {},   
			    on: {
			        success: function(event, id, obj){
			        	loadingMask.hide();
			        	
						var instance = this;
						var content = instance.get('responseData');
						var collectionDetail = A.one("#<portlet:namespace/>collection-detail");
						
						if (collectionDetail){
							collectionDetail.html(content);
						}
						if (A.one('#<portlet:namespace/>view-items-button')){
							A.one('#<portlet:namespace/>view-items-button').on('click', function(){
								getDictItems(selectedDictCollectionId);
							});
						}
						if (A.one('#<portlet:namespace/>edit-items-button')){
							A.one('#<portlet:namespace/>edit-items-button').on('click', function(){
								editDictCollection(selectedDictCollectionId);
							});
						}
						
						scrollWindow();
					},
			    	error: function(){
			    		loadingMask.hide();
			    	}
				}
			}
		);
	},['aui-base','liferay-portlet-url','aui-io']);
	
	Liferay.provide(window, 'getDictItems', function(dictCollectionId, cur, keyword, itemLinkedId){
		if (needConfirnChangeView){
			if (!confirm(Liferay.Language.get('are-you-sure'))){
				return;
			}
		}
		needConfirnChangeView = false;
		
		var loadingMask = new A.LoadingMask(
			{
				'strings.loading': '<%= UnicodeLanguageUtil.get(pageContext, "...") %>',
				target: A.one('#<portlet:namespace/>collection-detail')
			}
		);
		loadingMask.show();
		
		var iteratorURL = Liferay.PortletURL.createURL('<%=iteratorURL.toString()%>');
		iteratorURL.setParameter('<%=DictItemDisplayTerms.DICTCOLLECTION_ID %>', selectedDictCollectionId);
		if (cur){
			iteratorURL.setParameter('cur', cur);
		} else {
			iteratorURL.setParameter('cur', '1');
		}
		if (keyword){
			iteratorURL.setParameter('searchKeyword', keyword);
		}
		if (itemLinkedId){
			iteratorURL.setParameter('itemLinkedId', itemLinkedId);
		}
		
		A.io.request(
			iteratorURL.toString(),
			{
			    dataType: 'json',
			    data: {},   
			    on: {
			        success: function(event, id, obj){
			        	loadingMask.hide();
			        	
						var instance = this;
						var items = instance.get('responseData');
						var itemsContainer = A.one("#<portlet:namespace/>collection-detail");
						
						if (itemsContainer){
							itemsContainer.html(items);
						}
						// search container navigator
						if (A.all('.items-container') 
								&& A.all('.items-container').all('.pagination') 
								&& A.all('.items-container').all('.pagination').all('a')){
							A.all('.items-container').all('.pagination').all('a').each(function(navigation){
								navigation.on('click', function(event){
									event.preventDefault();
									
									var cur = event['target']['_node']['innerText'];
									var searchKeyword = A.one('#<portlet:namespace/>item-name') ?
											A.one('#<portlet:namespace/>item-name').attr('value') : '';
									
									getDictItems(selectedDictCollectionId, cur, searchKeyword);
								});
							});
						}
						// edit button dict item
						if (A.all('.<portlet:namespace/>edit_dictItem_button')){
							A.all('.<portlet:namespace/>edit_dictItem_button').each(function(button){
								var itemId = button.one('img')['_node']['id'].replace(/.+dictItemId_/, '');
								button.on('click', function(){
									editDictItem(itemId);
								});
							});
						}
						// add button dict item
						if (A.one('#<portlet:namespace/>add-item')){
							A.one('#<portlet:namespace/>add-item').on('click', function(){
								editDictItem();
							});
						}
						// search items
						if (A.one('#<portlet:namespace/>search-item-button')){
							A.one('#<portlet:namespace/>search-item-button').on('click', function(){
								var searchKeyword = A.one('#<portlet:namespace/>item-name') ? 
										A.one('#<portlet:namespace/>item-name').attr('value') : '';
								var itemLinkedIdSearch = A.one('#<portlet:namespace/>item-linked') ?
										A.one('#<portlet:namespace/>item-linked').attr('value') : 0;
								getDictItems(selectedDictCollectionId, 1, searchKeyword, itemLinkedId);
							});
						}
						if (A.one('#<portlet:namespace/>item-name')){
							A.one('#<portlet:namespace/>item-name').on('change', function(){
								var searchKeyword = A.one('#<portlet:namespace/>item-name') ? 
										A.one('#<portlet:namespace/>item-name').attr('value') : '';
								var itemLinkedIdSearch = A.one('#<portlet:namespace/>item-linked') ?
										A.one('#<portlet:namespace/>item-linked').attr('value') : 0;
								getDictItems(selectedDictCollectionId, 1, searchKeyword, itemLinkedId);
							})
						}
						if (A.one('#<portlet:namespace/>item-linked')){
							A.one('#<portlet:namespace/>item-linked').on('change', function(){
								var searchKeyword = A.one('#<portlet:namespace/>item-name') ? 
										A.one('#<portlet:namespace/>item-name').attr('value') : '';
								var itemLinkedIdSearch = A.one('#<portlet:namespace/>item-linked') ?
										A.one('#<portlet:namespace/>item-linked').attr('value') : 0;
								getDictItems(selectedDictCollectionId, 1, searchKeyword, itemLinkedIdSearch);
							})
						}
						//
						if (A.one('.lfr-pagination-delta-selector')){
							A.one('.lfr-pagination-delta-selector').on('click', function(even){
								even.preventDefault();
							});
						}
						
						scrollWindow();
					},
			    	error: function(){
			    		loadingMask.hide();
			    	}
				}
			}
		);
	},['aui-base','liferay-portlet-url','aui-io']);
	
	Liferay.provide(window, 'getDictCollections', function(collectionName){
		var loadingMask = new A.LoadingMask(
			{
				'strings.loading': '<%= UnicodeLanguageUtil.get(pageContext, "...") %>',
				target: A.one('#<portlet:namespace/>collections-container')
			}
		);
		loadingMask.show();
		
		var portletURL = Liferay.PortletURL.createURL('<%= PortletURLFactoryUtil.create(request, WebKeys.DATA_MANAGEMENT_ADMIN_PORTLET, themeDisplay.getPlid(), PortletRequest.RENDER_PHASE) %>');
		portletURL.setParameter("mvcPath", "/html/portlets/data_management/admin/ajax/_get_dictcollections.jsp");
		portletURL.setWindowState("<%=LiferayWindowState.EXCLUSIVE.toString()%>"); 
		portletURL.setPortletMode("normal");
		portletURL.setParameter("collectionName", collectionName);
		
		A.io.request(
			portletURL.toString(),
			{
			    dataType: 'json',
			    data: {},   
			    on: {
			        success: function(event, id, obj){
			        	loadingMask.hide();
			        	
						var instance = this;
						var collections = instance.get('responseData');
						var container = A.one("#<portlet:namespace/>collections-container");
						
						if (container){
							container.html(collections);
						}
						
						if (A.all('.collection-tree-node')){
							A.all('.collection-tree-node').each(function(node){
								node.on('click', function(){
									if (A.one('.selected')){
										A.one('.selected').removeClass('selected');
									}
									node.addClass('selected');
									var collectionId = node['_node']['id'].replace(/^.+collectionId_/, '');
									selectedDictCollectionId = collectionId;
									getDictCollectionDetail(collectionId);
								});
							});
						}
						
						scrollWindow();
					},
			    	error: function(){
			    		loadingMask.hide();
			    	}
				}
			}
		);
	},['aui-base','liferay-portlet-url','aui-io']);
	
	Liferay.provide(window, 'editDictCollection', function(collectionId){
		if (needConfirnChangeView){
			if (!confirm(Liferay.Language.get('are-you-sure'))){
				return;
			}
		}
		needConfirnChangeView = true;
		
		var loadingMask = new A.LoadingMask(
			{
				'strings.loading': '<%= UnicodeLanguageUtil.get(pageContext, "...") %>',
				target: A.one('#<portlet:namespace/>collection-detail')
			}
		);
		loadingMask.show();
		
		var portletURL = Liferay.PortletURL.createURL('<%= PortletURLFactoryUtil.create(request, WebKeys.DATA_MANAGEMENT_ADMIN_PORTLET, themeDisplay.getPlid(), PortletRequest.RENDER_PHASE) %>');
		portletURL.setParameter("mvcPath", "/html/portlets/data_management/admin/ajax/_edit_dictcollection.jsp");
		portletURL.setWindowState("<%=LiferayWindowState.EXCLUSIVE.toString()%>"); 
		portletURL.setPortletMode("normal");
		
		if (collectionId){
			portletURL.setParameter('<%=DictItemDisplayTerms.DICTCOLLECTION_ID %>', collectionId);
			updateDictCollectionId = collectionId;
		} else {
			updateDictCollectionId = 0;
		}
		
		A.io.request(
			portletURL.toString(),
			{
			    dataType: 'json',
			    data: {},   
			    on: {
			        success: function(event, id, obj){
			        	loadingMask.hide();
			        	
						var instance = this;
						var result = instance.get('responseData');
						var container = A.one("#<portlet:namespace/>collection-detail");
						
						if (container){
							container.html(result);
						}
						// initial value for dictcollection link checkbox
						if (A.all('.no-linked-to-selected-collection')){
							A.all('.no-linked-to-selected-collection').each(function(noSelected){
								noSelected.ancestor().one('#<portlet:namespace/>dictCollectionsLinked').attr('value', '0');
							});
						}
						if (A.one('#<portlet:namespace/>fm') 
								&& A.one('#<portlet:namespace/>fm').one('#<portlet:namespace/>submit')){
							A.one('#<portlet:namespace/>fm')
								.one('#<portlet:namespace/>submit')
									.on('click', function(event){
								event.preventDefault();
								updateDictCollection(updateDictCollectionId);
								needConfirnChangeView = false;
							});
						}
						if (A.one('#<portlet:namespace/>fm')
								&& A.one('#<portlet:namespace/>fm').one('#<portlet:namespace/>cancel')){
							A.one('#<portlet:namespace/>fm')
								.one('#<portlet:namespace/>cancel')
									.on('click', function(event){
								event.preventDefault();
								getDictCollectionDetail(selectedDictCollectionId);
							});
						}
					},
			    	error: function(){
			    		loadingMask.hide();
			    	}
				}
			}
		);
	},['aui-base','liferay-portlet-url','aui-io']);
	
	Liferay.provide(window, 'editDictItem', function(itemId){
		needConfirnChangeView = true;
		previousId = '';
		
		var loadingMask = new A.LoadingMask(
			{
				'strings.loading': '<%= UnicodeLanguageUtil.get(pageContext, "...") %>',
				target: A.one('#<portlet:namespace/>collection-detail')
			}
		);
		loadingMask.show();
		
		var portletURL = Liferay.PortletURL.createURL('<%= PortletURLFactoryUtil.create(request, WebKeys.DATA_MANAGEMENT_ADMIN_PORTLET, themeDisplay.getPlid(), PortletRequest.RENDER_PHASE) %>');
		portletURL.setParameter("mvcPath", "/html/portlets/data_management/admin/ajax/_edit_dictitem.jsp");
		portletURL.setWindowState("<%=LiferayWindowState.EXCLUSIVE.toString()%>"); 
		portletURL.setPortletMode("normal");
		portletURL.setParameter('<%=DictItemDisplayTerms.DICTCOLLECTION_ID %>', selectedDictCollectionId);
		
		if (itemId){
			portletURL.setParameter('<%=DictItemDisplayTerms.DICTITEM_ID %>', itemId);
			selectedDictItemId = itemId;
		} else {
			portletURL.setParameter('<%=DictItemDisplayTerms.DICTCOLLECTION_ID %>', selectedDictCollectionId);
			selectedDictItemId = 0;
		}
		
		A.io.request(
			portletURL.toString(),
			{
			    dataType: 'json',
			    data: {},   
			    on: {
			        success: function(event, id, obj){
			        	loadingMask.hide();
			        	
						var instance = this;
						var result = instance.get('responseData');
						var container = A.one("#<portlet:namespace/>collection-detail");
						
						if (container){
							container.html(result);
						}
						
						var dictCollection = A.one('#<portlet:namespace/><%=DictItemDisplayTerms.DICTCOLLECTION_ID%>');
						
						if (dictCollection){
							var dictCollectionId = dictCollection.val();
							var dictItemId = selectedDictItemId;
							getDictItemsList(dictCollectionId, dictItemId);
							getDictItemsLinked(dictCollectionId, dictItemId);
							getSelectSibling(dictCollectionId, 0, dictItemId);
							dictCollection.on('change', function(){
								dictCollectionId = dictCollection.val();
								getDictItemsList(dictCollectionId, dictItemId);
								getDictItemsLinked(dictCollectionId, dictItemId);
								getSelectSibling(dictCollectionId, 0, 0);
							});
						}
						
						if (A.one('#<portlet:namespace/>fm') 
								&& A.one('#<portlet:namespace/>fm').one('#<portlet:namespace/>submit')){
							A.one('#<portlet:namespace/>fm')
								.one('#<portlet:namespace/>submit')
									.on('click', function(event){
								event.preventDefault();
								updateDictItem(selectedDictItemId, selectedDictCollectionId);
								needConfirnChangeView = false;
							});
						}
						
						if (A.one('#<portlet:namespace/>fm') 
								&& A.one('#<portlet:namespace/>fm').one('#<portlet:namespace/>cancel')){
							A.one('#<portlet:namespace/>fm')
								.one('#<portlet:namespace/>cancel')
									.on('click', function(event){
								event.preventDefault();
								getDictItems(selectedDictCollectionId);
							});
						}
					},
			    	error: function(){
			    		loadingMask.hide();
			    	}
				}
			}
		);
	},['aui-base','liferay-portlet-url','aui-io']);
	
	Liferay.provide(window, 'updateDictCollection', function(dictCollectionId){
		var loadingMask = new A.LoadingMask(
			{
				'strings.loading': '<%= UnicodeLanguageUtil.get(pageContext, "...") %>',
				target: A.one('#<portlet:namespace/>collection-detail')
			}
		);
		loadingMask.show();
		
		var portletURL = Liferay.PortletURL.createURL('<%= PortletURLFactoryUtil.create(request, WebKeys.DATA_MANAGEMENT_ADMIN_PORTLET, themeDisplay.getPlid(), PortletRequest.ACTION_PHASE) %>');
		portletURL.setParameter("javax.portlet.action", "updateDictCollection");
		portletURL.setWindowState('<%=WindowState.NORMAL%>');
		
		var collectionLinked = '';
		if (A.all('#<portlet:namespace/>dictCollectionsLinked')){
			A.all('#<portlet:namespace/>dictCollectionsLinked').each(function(dictCol){
				if (parseInt(dictCol.attr('value')) > 0){
					collectionLinked += dictCol.attr('value') + ',';
				}
			});
		}
		if (!dictCollectionId){
			dictCollectionId = 0;
		}
		
		A.io.request(
			portletURL.toString(),
			{
				method: 'POST',
			    data:{    	
			    	<portlet:namespace/>collectionName: A.one('#<portlet:namespace/>collectionName') ? 
			    			A.one('#<portlet:namespace/>collectionName').attr('value') : '',
			    	<portlet:namespace/>collectionCode: A.one('#<portlet:namespace/>collectionCode') ? 
			    			A.one('#<portlet:namespace/>collectionCode').attr('value') : '',
			    	<portlet:namespace/>description: A.one('#<portlet:namespace/>description') ? 
			    			A.one('#<portlet:namespace/>description').attr('value') : '',
			    	<portlet:namespace/>dictCollectionId: dictCollectionId,
			    	<portlet:namespace/>collectionLinked: collectionLinked,
			    },   
			    on: {
			        success: function(event, id, obj){
			        	loadingMask.hide();
			        	getDictCollections();
						alert(Liferay.Language.get('success'));
					},
			    	error: function(){
			    		loadingMask.hide();
			    		alert(Liferay.Language.get('error'));
			    	}
				}
			}
		);
		
	},['aui-base','liferay-portlet-url','aui-io']);
	
	Liferay.provide(window, 'updateDictItem', function(dictItemId, dictCollectionId){
		var loadingMask = new A.LoadingMask(
			{
				'strings.loading': '<%= UnicodeLanguageUtil.get(pageContext, "...") %>',
				target: A.one('#<portlet:namespace/>collection-detail')
			}
		);
		loadingMask.show();
		
		var portletURL = Liferay.PortletURL.createURL('<%= PortletURLFactoryUtil.create(request, WebKeys.DATA_MANAGEMENT_ADMIN_PORTLET, themeDisplay.getPlid(), PortletRequest.ACTION_PHASE) %>');
		portletURL.setParameter("javax.portlet.action", "updateDictItem");
		portletURL.setWindowState('<%=WindowState.NORMAL%>');
		
		var itemsLinked = '';
		if (A.all('#<portlet:namespace/>dictItemLinked')){
			A.all('#<portlet:namespace/>dictItemLinked').each(function(item){
				if (parseInt(item.attr('value')) > 0){
					itemsLinked += item.attr('value') + ',';
				}
			});
		}
		if (!dictItemId){
			dictItemId = 0;
		}
		if (!dictCollectionId){
			dictCollectionId = 0;
		}
		
		A.io.request(
			portletURL.toString(),
			{
				method: 'POST',
			    data:{    	
			    	<portlet:namespace/><%=DictItemDisplayTerms.DICTITEM_ID%>: dictItemId,
			    	<portlet:namespace/><%=DictItemDisplayTerms.DICTCOLLECTION_ID%>: dictCollectionId,
			    	<portlet:namespace/><%=DictItemDisplayTerms.PARENTITEM_ID%>: A.one('#<portlet:namespace/>parentItemId') ?
			    			A.one('#<portlet:namespace/>parentItemId').attr('value') : 0,
			    	<portlet:namespace/><%=DictItemDisplayTerms.ITEM_NAME%>: A.one('#<portlet:namespace/>itemName') ?
			    			A.one('#<portlet:namespace/>itemName').attr('value') : '',
			    	<portlet:namespace/><%=DictItemDisplayTerms.ITEM_CODE%>: A.one('#<portlet:namespace/>itemCode') ?
			    			A.one('#<portlet:namespace/>itemCode').attr('value') : '',
			    	<portlet:namespace/><%=DictItemDisplayTerms.SIBLING%>: A.one('#<portlet:namespace/>sibling') ?
			    			A.one('#<portlet:namespace/>sibling').attr('value') : 0,
			    	<portlet:namespace/>dictItemLinked: itemsLinked,
			    },   
			    on: {
			        success: function(event, id, obj){
			        	setTimeout(function(){
							getDictItems(selectedDictCollectionId);
							alert(Liferay.Language.get('success'));
						}, 1000);
					},
			    	error: function(){
			    		loadingMask.hide();
			    		alert(Liferay.Language.get('error'));
			    	}
				}
			}
		);
		
	},['aui-base','liferay-portlet-url','aui-io']);
	
	Liferay.provide(window, 'getSelectSibling', function(dictCollectionId, parentItemId, dictItemId){

		var getSelectSiblingURL = Liferay.PortletURL.createURL('<%= PortletURLFactoryUtil.create(request, WebKeys.DATA_MANAGEMENT_ADMIN_PORTLET, themeDisplay.getPlid(), PortletRequest.RENDER_PHASE) %>');
		getSelectSiblingURL.setParameter("mvcPath", "/html/portlets/data_management/admin/select_sibling.jsp");
		getSelectSiblingURL.setWindowState("<%=LiferayWindowState.EXCLUSIVE.toString()%>"); 
		getSelectSiblingURL.setPortletMode("normal");
		getSelectSiblingURL.setParameter("dictCollectionId", dictCollectionId);
		getSelectSiblingURL.setParameter("parentItemId", parentItemId);
		getSelectSiblingURL.setParameter("dictItemId", dictItemId);
		
		A.io.request(
			getSelectSiblingURL.toString(),
			{
			    dataType: 'json',
			    data: {},   
			    on: {
			        success: function(event, id, obj){
						var instance = this;
						var siblings = instance.get('responseData');
						var siblingsContainer = A.one("#<portlet:namespace/>sibling-container");
						if (siblingsContainer){
							siblingsContainer.html(siblings);
						}
					},
			    	error: function(){}
				}
			}
		);
	},['aui-base','liferay-portlet-url','aui-io']);
	
	// use for toggle expand
	var previousId = '';
	
	Liferay.provide(window, 'getDictItemsLinked', function(dictCollectionId, dictItemId){
		
		var getItemsLinkedURL = Liferay.PortletURL.createURL('<%= PortletURLFactoryUtil.create(request, WebKeys.DATA_MANAGEMENT_ADMIN_PORTLET, themeDisplay.getPlid(), PortletRequest.RENDER_PHASE) %>');
		getItemsLinkedURL.setParameter("mvcPath", "/html/portlets/data_management/admin/select_dictitems_linked.jsp");
		getItemsLinkedURL.setWindowState("<%=LiferayWindowState.EXCLUSIVE.toString()%>"); 
		getItemsLinkedURL.setPortletMode("normal");
		getItemsLinkedURL.setParameter("dictCollectionId", dictCollectionId);
		getItemsLinkedURL.setParameter("dictItemId", dictItemId);
		
		A.io.request(
			getItemsLinkedURL.toString(),
			{
			    dataType: 'json',
			    data: {},   
			    on: {
			        success: function(event, id, obj){
						var instance = this;
						var itemsLinked = instance.get('responseData');
						var itemsLinkedContainer = A.one("#<portlet:namespace/>itemLinkedContainer");
						if (itemsLinkedContainer){
							itemsLinkedContainer.html(itemsLinked);
						}
						
						// todo itemsLinkedStr
						// initial value for item link checkbox
						if (A.all('.no-linked-to-selected-item')){
							A.all('.no-linked-to-selected-item').each(function(noSelected){
								if (noSelected.ancestor().one('#<portlet:namespace/>dictItemLinked')){
									noSelected.ancestor().one('#<portlet:namespace/>dictItemLinked').attr('value', '0');
								}
							});
						}
						
						// toggle expand
						if ($('.expand-anchor')){
							for (var i = 0; i < $('.expand-anchor').length; i++){
								var colId = $('.expand-anchor')[i]['id'].replace(/^.+dictCollectionId_/, '');
								$('#<portlet:namespace/>expandable_' + colId) ? 
										$('#<portlet:namespace/>expandable_' + colId).slideToggle( "normal") : '';
								if (A.one('#<portlet:namespace/>expand-anchor_dictCollectionId_' + colId)){
									A.one('#<portlet:namespace/>expand-anchor_dictCollectionId_' + colId).on('click', function(){
										var id = this['_node']['id'].replace(/^.+dictCollectionId_/, '');
										$('#<portlet:namespace/>expandable_' + id) ?
												$('#<portlet:namespace/>expandable_' + id).slideToggle( "normal") : '';
										if (previousId.length > 0 && previousId != id && $('#<portlet:namespace/>expandable_' + previousId)){
											$('#<portlet:namespace/>expandable_' + previousId).slideToggle("normal");
										} 
										if (previousId == id){
											previousId = '';
										} else {
											previousId = id;
										}
									});
								}
							}
						}
					},
				}
			}
		);
	},['aui-base','liferay-portlet-url','aui-io']);
	
	Liferay.provide(window, 'getDictItemsList', function(dictCollectionId, dictItemId){
		
		var portletURL = Liferay.PortletURL.createURL('<%= PortletURLFactoryUtil.create(request, WebKeys.DATA_MANAGEMENT_ADMIN_PORTLET, themeDisplay.getPlid(), PortletRequest.RENDER_PHASE) %>');
		portletURL.setParameter("mvcPath", "/html/portlets/data_management/admin/select_dictitems.jsp");
		portletURL.setWindowState("<%=LiferayWindowState.EXCLUSIVE.toString()%>"); 
		portletURL.setPortletMode("normal");
		portletURL.setParameter("dictCollectionId", dictCollectionId);
		portletURL.setParameter("dictItemId", dictItemId);
		
		A.io.request(
			portletURL.toString(),
			{
			    dataType: 'json',
			    data: {},   
			    on: {
			        success: function(event, id, obj){
						var instance = this;
						var dictItems = instance.get('responseData');
						var parentItemContainer = A.one("#<portlet:namespace/>parentItem");
						
						if (parentItemContainer){
							parentItemContainer.empty();
							parentItemContainer.html(dictItems);
						}
						
						var dictCollection = A.one('#<portlet:namespace/><%=DictItemDisplayTerms.DICTCOLLECTION_ID%>');
						var parentItem = A.one('#<portlet:namespace/><%=DictItemDisplayTerms.PARENTITEM_ID%>');
						
						if (dictCollection && parentItem){
							parentItem.on('change', function(){
								dictCollectionId = dictCollection.val();
								parentItemId = parentItem.val();
								
								getSelectSibling(dictCollectionId, parentItemId, 0);
							});
						}
					},
			    	error: function(){}
				}
			}
		);
	},['aui-base','liferay-portlet-url','aui-io']);
	
	Liferay.provide(window, 'scrollWindow', function(){
		var anchor = A.one('#<portlet:namespace/>anchor-scroll');
		var y = anchor ? anchor.getY() : 100;
		$("html, body").animate({ scrollTop: y - 100 }, "normal");
	});
</aui:script>

<%!
	private Log _log = LogFactoryUtil.getLog("html.portlets.data_management.admin.display.dictcollections.jsp");
%>

