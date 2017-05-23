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

<%
	boolean showAddButton = false;
	DictPermissions permission = null;
	DictPermissionsPK permissionPk = 
			new DictPermissionsPK(user != null ? user.getUserId() : 0, -1);
	try {
		permission = DictPermissionsLocalServiceUtil
				.getDictPermissions(permissionPk);
	} catch (Exception e){}
	if (permission != null){
		showAddButton = true;
	}
%>

<div class="row-fluid">
	<div class="span3" id="<portlet:namespace/>anchor-scroll">
		<div class="opencps-searchcontainer-wrapper default-box-shadow radius8 data-manager-action">
			<div class="openCPSTree yui3-widget component tree-view tree-drag-drop">
				<c:if test="<%=permissionChecker.isOmniadmin() || showAddButton %>">
					<aui:button 
						type="submit" 
						value="add-collection" 
						onClick="editDictCollection()" 
						cssClass="plus-icon hide-when-edit-permission" 
						title="<%=LanguageUtil.get(locale, \"add-collection\") %>"
					/>
				</c:if>
				<c:if test="<%=permissionChecker.isOmniadmin() %>">
					<aui:button 
						type="submit" 
						value="collection-permissions" 
						onClick="editPermission()" 
						cssClass="permission-icon hide-when-add-collection"
						title="<%=LanguageUtil.get(locale, \"collection-permissions\") %>"
					/>
				</c:if>
				<div class="hide-when-edit-permission hide-when-add-collection">
					<aui:input 
						name="collection-name" 
						placeholder='<%= LanguageUtil.get(locale, "collection-name") %>' 
						cssClass="input100" 
						label="lookups-collections"
						title="<%=LanguageUtil.get(locale, \"lookups-collections-is-used-in-system\") %>"
					/>
					<aui:button 
						name="search-button" 
						value="search" 
						type="submit" 
						title="<%=LanguageUtil.get(locale, \"search\") %>"
					/>
				</div>
				<div id='<%=renderResponse.getNamespace() + "collections-container" %>' class="scrollbar-datamgt hide-when-edit-permission hide-when-add-collection"></div>
			</div>
		</div>
		
		<!-- update items sibling -->
		<c:if test="<%=permissionChecker.isOmniadmin() %>">
			<liferay-portlet:renderURL var="updateItemsURL" windowState="<%=LiferayWindowState.POP_UP.toString() %>" >
				<liferay-portlet:param name="mvcPath" value="/html/portlets/data_management/admin/display/update_items.jsp"/>
			</liferay-portlet:renderURL>
			<aui:button 
				href="<%=
						\"javascript:\" +  \"openDialog('\" + 
						updateItemsURL + \"','\" + 
						renderResponse.getNamespace() + \"updateItems\" + \"','\" +
						UnicodeLanguageUtil.get(pageContext, \"update-db-items\") +
						\"');\"  
					%>" 
				value="update-data-items"
			/>
		</c:if>
	</div>
	
	<div class="span9">
		<div class="opencps-searchcontainer-wrapper default-box-shadow radius8 data-manager-container">
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
			A.one('#<portlet:namespace/>collection-name').on('keyup', function(){
				var collectionName = A.one('#<portlet:namespace/>collection-name') ?
						A.one('#<portlet:namespace/>collection-name').attr('value') : '';
				getDictCollections(collectionName);
			});
		}
	});
	
//////// functions list
// 	Liferay.provide(window, 'getDictCollectionDetail', function(collectionId){
// 	Liferay.provide(window, 'getDictItemsToolbar', function(dictCollectionId){
// 	Liferay.provide(window, 'getDictItems', function(dictCollectionId, cur){
// 	Liferay.provide(window, 'getDictCollections', function(collectionName){
//	Liferay.provide(window, 'getDictPermissions', function(userId){
// 	Liferay.provide(window, 'getDictItemsLinked', function(dictCollectionId, dictItemId){
// 	Liferay.provide(window, 'getSelectSibling', function(dictCollectionId, parentItemId, dictItemId){
//  Liferay.provide(window, 'getUsers', function(name){
// 	Liferay.provide(window, 'getParentDictItemsList', function(dictCollectionId, dictItemId){
	
// 	Liferay.provide(window, 'editDictCollection', function(collectionId){
// 	Liferay.provide(window, 'editDictItem', function(itemId){
// 	Liferay.provide(window, 'editPermission', function(collectionId){
	
// 	Liferay.provide(window, 'updateDictCollection', function(dictCollectionId){
// 	Liferay.provide(window, 'updateDictItem', function(dictItemId, dictCollectionId){
//	Liferay.provide(window, 'updateDictPermissions', function(userId){
	
// 	Liferay.provide(window, 'deleteDictCollection', function(collectionId){
// 	Liferay.provide(window, 'deleteDictItem', function(dictItemId){
	
// 	Liferay.provide(window, 'changeStatusItemToNoUse', function(dictItemId){
	
	var selectedDictCollectionId = 0;	
	var updateDictCollectionId = 0;
	var selectedDictItemId = 0;
	var selectedUserId = 0;
	var needConfirnChangeView = false;
	
	Liferay.provide(window, 'updateDictPermissions', function(userId){
		if (!Liferay.ThemeDisplay.isSignedIn()){
			alert(Liferay.Language.get('please-login-and-try-again'));
			return;
		}
		
		if (selectedUserId == '0'){
			alert(Liferay.Language.get('please-select-user-admin'));
			return;
		}
		
		var loadingMask = new A.LoadingMask(
			{
				'strings.loading': '<%= UnicodeLanguageUtil.get(pageContext, "...") %>',
				target: A.one('#<portlet:namespace/>collection-detail')
			}
		);
		loadingMask.show();
		
		var portletURL = Liferay.PortletURL.createURL('<%= PortletURLFactoryUtil.create(request, WebKeys.DATA_MANAGEMENT_ADMIN_PORTLET, themeDisplay.getPlid(), PortletRequest.ACTION_PHASE) %>');
		portletURL.setParameter("javax.portlet.action", "updateDictPermissions");
		portletURL.setWindowState('<%=WindowState.NORMAL%>');
		
		if (userId){
			portletURL.setParameter('<%=DictItemDisplayTerms.USER_ID %>', userId);
		}
		
		var addCollectionPermission = '';
		
		if (A.one('#<portlet:namespace/>add-collections-permission')){
			addCollectionPermission = A.one('#<portlet:namespace/>add-collections-permission').attr('value');
		}
		
		portletURL.setParameter('addCollectionPermission', addCollectionPermission);
		
		var viewPermissionAll = '';
		var addPermissionAll = '';
		var editPermissionAll = '';
		var deletePermissionAll = '';
		
		if (A.one('#<portlet:namespace/>viewPermissionAll')){
			viewPermissionAll = A.one('#<portlet:namespace/>viewPermissionAll').attr('value');
		}
		if (A.one('#<portlet:namespace/>addPermissionAll')){
			addPermissionAll = A.one('#<portlet:namespace/>addPermissionAll').attr('value');
		}
		if (A.one('#<portlet:namespace/>editPermissionAll')){
			editPermissionAll = A.one('#<portlet:namespace/>editPermissionAll').attr('value');
		}
		if (A.one('#<portlet:namespace/>deletePermissionAll')){
			deletePermissionAll = A.one('#<portlet:namespace/>deletePermissionAll').attr('value');
		}
		
		portletURL.setParameter('viewPermissionAll', viewPermissionAll);
		portletURL.setParameter('addPermissionAll', addPermissionAll);
		portletURL.setParameter('editPermissionAll', editPermissionAll);
		portletURL.setParameter('deletePermissionAll', deletePermissionAll);
		
		var viewPermissions = '';
		var addPermissions = '';
		var editPermissions = '';
		var deletePermissions = '';
		
		if (A.all('#<portlet:namespace/>viewPermission') && viewPermissionAll != 'true'){
			A.all('#<portlet:namespace/>viewPermission').each(function(per){
				if (parseInt(per.attr('value')) > 0){
					viewPermissions += per.attr('value') + ',';
				}
			});
		}
		if (A.all('#<portlet:namespace/>addPermission') && addPermissionAll != 'true'){
			A.all('#<portlet:namespace/>addPermission').each(function(per){
				if (parseInt(per.attr('value')) > 0){
					addPermissions += per.attr('value') + ',';
				}
			});
		}
		if (A.all('#<portlet:namespace/>editPermission') && editPermissionAll != 'true'){
			A.all('#<portlet:namespace/>editPermission').each(function(per){
				if (parseInt(per.attr('value')) > 0){
					editPermissions += per.attr('value') + ',';
				}
			});
		}
		if (A.all('#<portlet:namespace/>deletePermission') && deletePermissionAll != 'true'){
			A.all('#<portlet:namespace/>deletePermission').each(function(per){
				if (parseInt(per.attr('value')) > 0){
					deletePermissions += per.attr('value') + ',';
				}
			});
		}
		
		portletURL.setParameter('viewPermissions', viewPermissions);
		portletURL.setParameter('addPermissions', addPermissions);
		portletURL.setParameter('editPermissions', editPermissions);
		portletURL.setParameter('deletePermissions', deletePermissions);
		
		A.io.request(
			portletURL.toString(),
			{
			    dataType: 'json',
			    data: {},   
			    on: {
			        success: function(event, id, obj){
			        	loadingMask.hide();
			        	alert(Liferay.Language.get('success'));
					},
			    	error: function(){
			    		loadingMask.hide();
			    	}
				}
			}
		);
	},['aui-base','liferay-portlet-url','aui-io']);
	
	Liferay.provide(window, 'editPermission', function(collectionId){
		if (!Liferay.ThemeDisplay.isSignedIn()){
			alert(Liferay.Language.get('please-login-and-try-again'));
			return;
		}
		
		if (needConfirnChangeView){
			if (!confirm(Liferay.Language.get('confirm-change-display'))){
				return;
			}
			needConfirnChangeView = false;
		}
		
		if (A.one('.selected')){
			A.one('.selected').removeClass("selected");
		}
		
		var loadingMask = new A.LoadingMask(
			{
				'strings.loading': '<%= UnicodeLanguageUtil.get(pageContext, "...") %>',
				target: A.one('#<portlet:namespace/>collection-detail')
			}
		);
		loadingMask.show();
		
		// hide other component
		if ($('.hide-when-edit-permission')){
			$('.hide-when-edit-permission').slideUp('normal');
		}
		
		var portletURL = Liferay.PortletURL.createURL('<%= PortletURLFactoryUtil.create(request, WebKeys.DATA_MANAGEMENT_ADMIN_PORTLET, themeDisplay.getPlid(), PortletRequest.RENDER_PHASE) %>');
		portletURL.setParameter("mvcPath", "/html/portlets/data_management/admin/ajax/_edit_permissions.jsp");
		portletURL.setWindowState("<%=LiferayWindowState.EXCLUSIVE.toString()%>"); 
		portletURL.setPortletMode("normal");
		
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
						
						getUsers();
						getDictPermissions();
						
						// save permission
						if (A.one('#<portlet:namespace/>save-permission-button')){
							A.one('#<portlet:namespace/>save-permission-button').on('click', function(){
								updateDictPermissions(selectedUserId);
							});
						}
						// search collection
						if (A.one('#<portlet:namespace/>search-collection-permission-button')){
							A.one('#<portlet:namespace/>search-collection-permission-button').on('click', function(){
								var keysearch = A.one('#<portlet:namespace/>collection-name-permission') ? 
										A.one('#<portlet:namespace/>collection-name-permission').attr('value') : '';
								searchDictPermission(keysearch);
							});
						}
						if (A.one('#<portlet:namespace/>collection-name-permission')){
							A.one('#<portlet:namespace/>collection-name-permission').on('change', function(){
								var keysearch = A.one('#<portlet:namespace/>collection-name-permission') ? 
										A.one('#<portlet:namespace/>collection-name-permission').attr('value') : '';
								searchDictPermission(keysearch);
							});
						}
						// search users
						if (A.one('#<portlet:namespace/>search-users-button')){
							A.one('#<portlet:namespace/>search-users-button').on('click', function(){
								var keysearch = A.one('#<portlet:namespace/>user-name') ? 
										A.one('#<portlet:namespace/>user-name').attr('value') : '';
								searchUsers(keysearch);
							});
						}
						if (A.one('#<portlet:namespace/>user-name')){
							A.one('#<portlet:namespace/>user-name').on('change', function(){
								var keysearch = A.one('#<portlet:namespace/>user-name') ? 
										A.one('#<portlet:namespace/>user-name').attr('value') : '';
								searchUsers(keysearch);
							});
						}
						// back button
						if (A.one('#<portlet:namespace/>back-permission-button')){
							A.one('#<portlet:namespace/>back-permission-button').on('click', function(){
								// show other component
								$('.hide-when-edit-permission').slideDown('normal');
								getDictCollectionDetail();
								getDictCollections();
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
	
	Liferay.provide(window, 'searchUsers', function(keysearch){
		if (keysearch && keysearch.length > 0){
			if (A.all('.user-tree-node')){
				A.all('.user-tree-node').each(function(node) {
					var name = node.html().replace(/<span.+span> $/, '');
					if (name.toLowerCase().includes(keysearch.toLowerCase())){
						if ($('#' + node.attr('id')).is(":hidden")){
							$('#' + node.attr('id')) ? 
									$('#' + node.attr('id')).slideToggle("slow") : '';
						}
					} else {
						if (!$('#' + node.attr('id')).is(":hidden")){
							$('#' + node.attr('id')) ? 
									$('#' + node.attr('id')).slideToggle("slow") : '';
						}
					}
				});
			}
		} else {
			if (A.all('.user-tree-node')){
				A.all('.user-tree-node').each(function(node) {
					if ($('#' + node.attr('id')).is(":hidden")){
						$('#' + node.attr('id')) ? 
								$('#' + node.attr('id')).slideToggle("slow") : '';
					}
				});
			}
		}
	});
	
	Liferay.provide(window, 'searchDictPermission', function(keysearch){
		if (keysearch && keysearch.length > 0){
			if (A.all('.collection-tree-node-permission')){
				A.all('.collection-tree-node-permission').each(function(node) {
					var name = node.html().replace(/<\/.+> $/, '');
					name = name.replace(/^.+>/, '');
					if (name.toLowerCase().includes(keysearch.toLowerCase())){
						if ($('#' + node.attr('id')).is(":hidden")){
							$('#' + node.attr('id')) ? 
									$('#' + node.attr('id')).slideToggle("slow") : '';
						}
					} else {
						if (!$('#' + node.attr('id')).is(":hidden")){
							$('#' + node.attr('id')) ? 
									$('#' + node.attr('id')).slideToggle("slow") : '';
						}
					}
				});
			}
		} else {
			if (A.all('.collection-tree-node-permission')){
				A.all('.collection-tree-node-permission').each(function(node) {
					if ($('#' + node.attr('id')).is(":hidden")){
						$('#' + node.attr('id')) ? 
								$('#' + node.attr('id')).slideToggle("slow") : '';
					}
				});
			}
		}
	});
	
	Liferay.provide(window, 'getDictPermissions', function(userId){
		if (!Liferay.ThemeDisplay.isSignedIn()){
			alert(Liferay.Language.get('please-login-and-try-again'));
			return;
		}
		
		var loadingMask = new A.LoadingMask(
			{
				'strings.loading': '<%= UnicodeLanguageUtil.get(pageContext, "...") %>',
				target: A.one('#<portlet:namespace/>collection-permissions')
			}
		);
		loadingMask.show();
		
		var portletURL = Liferay.PortletURL.createURL('<%= PortletURLFactoryUtil.create(request, WebKeys.DATA_MANAGEMENT_ADMIN_PORTLET, themeDisplay.getPlid(), PortletRequest.RENDER_PHASE) %>');
		portletURL.setParameter("mvcPath", "/html/portlets/data_management/admin/ajax/_get_dict_permissions.jsp");
		portletURL.setWindowState("<%=LiferayWindowState.EXCLUSIVE.toString()%>"); 
		portletURL.setPortletMode("normal");
		
		if (userId){
			portletURL.setParameter("userIdPermission", userId);
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
						var content = instance.get('responseData');
						var container = A.one("#<portlet:namespace/>collection-permissions");
						
						if (container){
							container.html(content);
						}
						
						// initial value for dictcollection link checkbox
						if (A.all('.unchecked-checkbox')){
							A.all('.unchecked-checkbox').each(function(noSelected){
								noSelected.ancestor().one('input[type=hidden]').attr('value', '0');
							});
						}
						
						// onclick dict collection
						if ($('.collection-tree-node-permission-name')){
							$('.collection-tree-node-permission-name').each(function(){
								$(this).click(function(){
									var li = $(this).closest('li');
									var collectionId = $(li)['0']['id'].replace(/.+collection_/, '');
									if (!($(li)['0'].className).includes('checked-collection')){
										$(li).find('input[type=checkbox]').each(function(){
											$(this).prop('checked', true);
										});
										$(li).find('input[type=hidden]').each(function(){
											$(this).prop('value', collectionId);
										});
										$(li)['0'].className += ' checked-collection';
									} else {
										$(li).find('input[type=checkbox]').each(function(){
											$(this).prop('checked', false);
										});
										$(li).find('input[type=hidden]').each(function(){
											$(this).prop('value', 0);
										});
										$(li)['0'].className = $(li)['0'].className.replace(' checked-collection', '');
									}
								});
							});
						} 
						// onclick dict collection
						if ($('.collection-tree-node-permission-all')){
							$('.collection-tree-node-permission-all').each(function(){
								$(this).click(function(){
									var li = $(this).closest('li');
									if (!($(li)['0'].className).includes('checked-collection')){
										$(li).find('input[type=checkbox]').each(function(){
											$(this).prop('checked', true);
										});
										$(li).find('input[type=hidden]').each(function(){
											$(this).prop('value', 1);
										});
										$(li)['0'].className += ' checked-collection';
									} else {
										$(li).find('input[type=checkbox]').each(function(){
											$(this).prop('checked', false);
										});
										$(li).find('input[type=hidden]').each(function(){
											$(this).prop('value', 0);
										});
										$(li)['0'].className = $(li)['0'].className.replace(' checked-collection', '');
									}
								});
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
	
	Liferay.provide(window, 'getUsers', function(name){
		if (!Liferay.ThemeDisplay.isSignedIn()){
			alert(Liferay.Language.get('please-login-and-try-again'));
			return;
		}
		
		var loadingMask = new A.LoadingMask(
			{
				'strings.loading': '<%= UnicodeLanguageUtil.get(pageContext, "...") %>',
				target: A.one('#<portlet:namespace/>users-container')
			}
		);
		loadingMask.show();
		
		var portletURL = Liferay.PortletURL.createURL('<%= PortletURLFactoryUtil.create(request, WebKeys.DATA_MANAGEMENT_ADMIN_PORTLET, themeDisplay.getPlid(), PortletRequest.RENDER_PHASE) %>');
		portletURL.setParameter("mvcPath", "/html/portlets/data_management/admin/ajax/_get_users.jsp");
		portletURL.setWindowState("<%=LiferayWindowState.EXCLUSIVE.toString()%>"); 
		portletURL.setPortletMode("normal");
		
		if (name){
			portletURL.setParameter("userName", name);
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
						var content = instance.get('responseData');
						var container = A.one("#<portlet:namespace/>users-container");
						
						if (container){
							container.html(content);
						}
						
						if (A.all('.user-tree-node')){
							A.all('.user-tree-node').each(function(node){
								node.on('click', function(){
									if (A.one('.selected')){
										A.one('.selected').removeClass('selected');
									}
									node.addClass('selected');
									var userId = node['_node']['id'].replace(/^.+userId_/, '');
									selectedUserId = userId;
									
									getDictPermissions(userId);
								});
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
	
	Liferay.provide(window, 'getDictCollectionDetail', function(collectionId){
		if (!Liferay.ThemeDisplay.isSignedIn()){
			alert(Liferay.Language.get('please-login-and-try-again'));
			return;
		}
		
		// show other component
		if ($('.hide-when-add-collection')){
			$('.hide-when-add-collection').slideDown('normal');
		}
		if ($('.hide-when-edit-permission')){
			$('.hide-when-edit-permission').slideDown('normal');
		}
		
		if (needConfirnChangeView){
			if (!confirm(Liferay.Language.get('confirm-change-display'))){
				return;
			}
			needConfirnChangeView = false;
		}
		
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
								getDictItemsToolbar(selectedDictCollectionId);
							});
						}
						if (A.one('#<portlet:namespace/>edit-collection-button')){
							A.one('#<portlet:namespace/>edit-collection-button').on('click', function(){
								editDictCollection(selectedDictCollectionId);
							});
						}
						if (A.one('#<portlet:namespace/>delete-collection-button')){
							A.one('#<portlet:namespace/>delete-collection-button').on('click', function(){
								deleteDictCollection(selectedDictCollectionId);
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
	
	Liferay.provide(window, 'getDictItemsToolbar', function(dictCollectionId){
		if (!Liferay.ThemeDisplay.isSignedIn()){
			alert(Liferay.Language.get('please-login-and-try-again'));
			return;
		}
		
		var loadingMask = new A.LoadingMask(
			{
				'strings.loading': '<%= UnicodeLanguageUtil.get(pageContext, "...") %>',
				target: A.one('#<portlet:namespace/>collection-detail')
			}
		);
		loadingMask.show();
		
		var portletURL = Liferay.PortletURL.createURL('<%= PortletURLFactoryUtil.create(request, WebKeys.DATA_MANAGEMENT_ADMIN_PORTLET, themeDisplay.getPlid(), PortletRequest.RENDER_PHASE) %>');
		portletURL.setParameter("mvcPath", "/html/portlets/data_management/admin/ajax/_dictitems_toolbar.jsp");
		portletURL.setWindowState("<%=LiferayWindowState.EXCLUSIVE.toString()%>"); 
		portletURL.setPortletMode("normal");
		if (dictCollectionId){
			portletURL.setParameter('<%=DictItemDisplayTerms.DICTCOLLECTION_ID %>', dictCollectionId);
		}
		var keyword = A.one('#<portlet:namespace/>item-name') ? 
				A.one('#<portlet:namespace/>item-name').attr('value') : '';
		var itemLinkedId = A.one('#<portlet:namespace/>item-linked') ?
				A.one('#<portlet:namespace/>item-linked').attr('value') : 0;
		var status = A.one('#<portlet:namespace/>itemsStatusInUsed') ?
				A.one('#<portlet:namespace/>itemsStatusInUsed').attr('value') : 1;
		portletURL.setParameter('searchKeyword', keyword);
		portletURL.setParameter('itemLinkedId', itemLinkedId);
		portletURL.setParameter('itemsStatus', status);
		
		A.io.request(
				portletURL.toString(),
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
						getDictItems(selectedDictCollectionId);
						// items status
						if (A.one('#<portlet:namespace/>itemsStatusInUsed')){
							A.one('#<portlet:namespace/>itemsStatusInUsed').on('change', function(){
								getDictItems(selectedDictCollectionId);
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
								getDictItems(selectedDictCollectionId);
							});
						}
						if (A.one('#<portlet:namespace/>item-name')){
							A.one('#<portlet:namespace/>item-name').on('keyup', function(){
								getDictItems(selectedDictCollectionId);
							});
						}
						if (A.one('#<portlet:namespace/>item-linked')){
							A.one('#<portlet:namespace/>item-linked').on('change', function(){
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
	
	Liferay.provide(window, 'getDictItems', function(dictCollectionId, cur){
		if (!Liferay.ThemeDisplay.isSignedIn()){
			alert(Liferay.Language.get('please-login-and-try-again'));
			return;
		}
		
		if (needConfirnChangeView){
			if (!confirm(Liferay.Language.get('confirm-change-display'))){
				return;
			}
			needConfirnChangeView = false;
		}
		
		var loadingMask = new A.LoadingMask(
			{
				'strings.loading': '<%= UnicodeLanguageUtil.get(pageContext, "...") %>',
				target: A.one('#<portlet:namespace/>dictItems_container')
			}
		);
		loadingMask.show();
		
		var iteratorURL = Liferay.PortletURL.createURL('<%=iteratorURL.toString()%>');
		if (dictCollectionId){
			iteratorURL.setParameter('<%=DictItemDisplayTerms.DICTCOLLECTION_ID %>', dictCollectionId);
		}
		if (cur){
			iteratorURL.setParameter('cur', cur);
		} else {
			iteratorURL.setParameter('cur', '1');
		}
		
		var keyword = A.one('#<portlet:namespace/>item-name') ? 
				A.one('#<portlet:namespace/>item-name').attr('value') : '';
		var itemLinkedId = A.one('#<portlet:namespace/>item-linked') ?
				A.one('#<portlet:namespace/>item-linked').attr('value') : 0;
		var status = A.one('#<portlet:namespace/>itemsStatusInUsed') ?
				A.one('#<portlet:namespace/>itemsStatusInUsed').attr('value') : 1;
		iteratorURL.setParameter('searchKeyword', keyword);
		iteratorURL.setParameter('itemLinkedId', itemLinkedId);
		iteratorURL.setParameter('itemsStatus', status);
		
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
						var itemsContainer = A.one("#<portlet:namespace/>dictItems_container");
						
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
									getDictItems(selectedDictCollectionId, cur);
								});
							});
						}
						// edit button dict item
						if (A.all('.<portlet:namespace/>edit_dictItem_button')){
							A.all('.<portlet:namespace/>edit_dictItem_button').each(function(button){
								/* var itemId = button.one('img')['_node']['id'].replace(/.+dictItemId_/, ''); */
								var itemId = button.attr('id').replace(/.+dictItemId_/, '');
								button.on('click', function(){
									editDictItem(itemId);
								});
							});
						}
						// edit dict item link
						if (A.all('.edit_dictItem_link')){
							A.all('.edit_dictItem_link').each(function(link){
								var itemId = link.attr('id').replace(/.+dictItemId_/, '');
								link.on('click', function(){
									editDictItem(itemId);
								});
							});
						}
						// no use button dict item
						if (A.all('.<portlet:namespace/>no_use_dictItem_button')){
							A.all('.<portlet:namespace/>no_use_dictItem_button').each(function(button){
								/* var itemId = button.one('img')['_node']['id'].replace(/.+dictItemId_/, ''); */
								var itemId = button.attr('id').replace(/.+dictItemId_/, '');
								button.on('click', function(event){
									event.preventDefault();
									if (confirm(Liferay.Language.get('are-you-sure-change-item-to-no-use'))){
										changeStatusItemToNoUse(itemId);
										getDictItems(selectedDictCollectionId);
									}
								});
							});
						}
						// delete button dict item
						if (A.all('.<portlet:namespace/>delete_dictItem_button')){
							A.all('.<portlet:namespace/>delete_dictItem_button').each(function(button){
								/* var itemId = button.one('img')['_node']['id'].replace(/.+dictItemId_/, ''); */
								var itemId = button.attr('id').replace(/.+dictItemId_/, '');
								button.on('click', function(event){
									event.preventDefault();
									if (confirm(Liferay.Language.get('are-you-sure-delete-item'))){
										deleteDictItem(itemId);
										getDictItemsToolbar(selectedDictCollectionId);
									}
								});
							});
						}
						// 
						if (A.one('.lfr-pagination-delta-selector')){
							A.one('.lfr-pagination-delta-selector')
								.html(A.one('.lfr-pagination-delta-selector').html().replace(/\D+/g, ''));
						}
					},
			    	error: function(){
			    		loadingMask.hide();
			    	}
				}
			}
		);
	},['aui-base','liferay-portlet-url','aui-io']);
	
	Liferay.provide(window, 'getDictCollections', function(collectionName){
		if (!Liferay.ThemeDisplay.isSignedIn()){
			alert(Liferay.Language.get('please-login-and-try-again'));
			return;
		}
		
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
									
									// todo check loggin
									if (!Liferay.ThemeDisplay.isSignedIn()){
										alert(Liferay.Language.get('please-login-and-try-again'));
										return;
									} else {
										console.log('logined');
									}
								
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
						
					},
			    	error: function(){
			    		loadingMask.hide();
			    	}
				}
			}
		);
	},['aui-base','liferay-portlet-url','aui-io']);
	
	Liferay.provide(window, 'editDictCollection', function(collectionId){
		if (!Liferay.ThemeDisplay.isSignedIn()){
			alert(Liferay.Language.get('please-login-and-try-again'));
			return;
		}
		
		if (needConfirnChangeView){
			if (!confirm(Liferay.Language.get('confirm-change-display'))){
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
			// hide other component
			$('.hide-when-add-collection').slideUp('normal');
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
						if (A.all('.unchecked-checkbox')){
							A.all('.unchecked-checkbox').each(function(noSelected){
								noSelected.ancestor().one('#<portlet:namespace/>dictCollectionsLinked').attr('value', '0');
							});
						}
						// save button
						if (A.one('#<portlet:namespace/>fm') 
								&& A.one('#<portlet:namespace/>fm').one('#<portlet:namespace/>submit')){
							A.one('#<portlet:namespace/>fm')
								.one('#<portlet:namespace/>submit')
									.on('click', function(event){
								event.preventDefault();
								updateDictCollection(updateDictCollectionId);
							});
						}
						// cancel button
						if (A.one('#<portlet:namespace/>fm')
								&& A.one('#<portlet:namespace/>fm').one('#<portlet:namespace/>cancel')){
							A.one('#<portlet:namespace/>fm')
								.one('#<portlet:namespace/>cancel')
									.on('click', function(event){
								event.preventDefault();
								getDictCollectionDetail(selectedDictCollectionId);
								// show other component
								$('.hide-when-add-collection').slideDown('normal');
							});
						}
						// click to select dict collection
						if ($('.click-select-dict-collection')){
							$('.click-select-dict-collection').each(function(){
								$(this).click(function(){
									var li = $(this).closest('li');
									var collectionId = $(li)['0']['id'].replace(/.+collectionId_/, '');
									if (!($(li)['0'].className).includes('checked-collection')){
										$(li).find('input[type=checkbox]').each(function(){
											$(this).prop('checked', true);
										});
										$(li).find('input[type=hidden]').each(function(){
											$(this).prop('value', collectionId);
										});
										$(li)['0'].className += ' checked-collection';
									} else {
										$(li).find('input[type=checkbox]').each(function(){
											$(this).prop('checked', false);
										});
										$(li).find('input[type=hidden]').each(function(){
											$(this).prop('value', 0);
										});
										$(li)['0'].className = $(li)['0'].className.replace(' checked-collection', '');
									}
								});
							});
						}
						// hide locate
						if (A.one('.input-localized-content')){
							A.one('.input-localized-content').setStyle('display', 'none');
						}
					},
			    	error: function(){
			    		loadingMask.hide();
			    	}
				}
			}
		);
	},['aui-base','liferay-portlet-url','aui-io']);
	
	Liferay.provide(window, 'deleteDictCollection', function(collectionId){
		if (!Liferay.ThemeDisplay.isSignedIn()){
			alert(Liferay.Language.get('please-login-and-try-again'));
			return;
		}
		
		if (!confirm(Liferay.Language.get('confirm-change-display'))){
			return;
		}
		
		var loadingMask = new A.LoadingMask(
			{
				'strings.loading': '<%= UnicodeLanguageUtil.get(pageContext, "...") %>',
				target: A.one('#<portlet:namespace/>collection-detail')
			}
		);
		loadingMask.show();
		
		var portletURL = Liferay.PortletURL.createURL('<%= PortletURLFactoryUtil.create(request, WebKeys.DATA_MANAGEMENT_ADMIN_PORTLET, themeDisplay.getPlid(), PortletRequest.ACTION_PHASE) %>');
		portletURL.setParameter("javax.portlet.action", "deleteDictCollection");
		portletURL.setWindowState('<%=WindowState.NORMAL%>');
		
		if (collectionId){
			portletURL.setParameter('<%=DictItemDisplayTerms.DICTCOLLECTION_ID %>', collectionId);
		}
		
		A.io.request(
			portletURL.toString(),
			{
			    dataType: 'json',
			    data: {},   
			    on: {
			        success: function(event, id, obj){
			        	loadingMask.hide();
			        	
			        	getDictCollections('');
			        	getDictCollectionDetail();
			        	
			        	if (A.one('#<portlet:namespace/>collection-name')){
			        		A.one('#<portlet:namespace/>collection-name').attr('value', '');
			        	}
			        	
			        	alert(Liferay.Language.get('success'));
					},
			    	error: function(){
			    		loadingMask.hide();
			    	}
				}
			}
		);
	},['aui-base','liferay-portlet-url','aui-io']);
	
	Liferay.provide(window, 'editDictItem', function(itemId){
		if (!Liferay.ThemeDisplay.isSignedIn()){
			alert(Liferay.Language.get('please-login-and-try-again'));
			return;
		}
		
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
							getParentDictItemsList(dictCollectionId, dictItemId);
							getDictItemsLinked(dictCollectionId, dictItemId);
							getSelectSibling(dictCollectionId, 0, dictItemId);
							dictCollection.on('change', function(){
								dictCollectionId = dictCollection.val();
								getParentDictItemsList(dictCollectionId, dictItemId);
								getDictItemsLinked(dictCollectionId, dictItemId);
								getSelectSibling(dictCollectionId, 0, 0);
							});
						}
						
						getParentDictItemsList(dictCollectionId, dictItemId);
						if (A.one('#<portlet:namespace/>parentItemShow')){
							A.one('#<portlet:namespace/>parentItemShow')
									.on('keyup', function(event){
								var keyword = A.one('#<portlet:namespace/>parentItemShow').val();
								getParentDictItemsList(dictCollectionId, dictItemId, keyword);
							});
							A.one('#<portlet:namespace/>parentItemShow')
									.on('click', function(event){
								A.one('#<portlet:namespace/>parentItem').removeClass('hide-when-focusout-datamgt');
								A.one('#<portlet:namespace/>parentItem').addClass('show-when-focus-datamgt');
							});
							A.one('#<portlet:namespace/>parentItemShow')
									.on('blur', function(event){
								if (!A.one('#<portlet:namespace/>parentItem').hasClass('no-touch-by-blur')){
									A.one('#<portlet:namespace/>parentItem').removeClass('show-when-focus-datamgt');
									A.one('#<portlet:namespace/>parentItem').addClass('hide-when-focusout-datamgt');
									if (A.one('#<portlet:namespace/>parentItemShow')){
										var value = A.one('#<portlet:namespace/>parentItemShow').val();
										if (value == '0' || value == ''){
											A.one('#<portlet:namespace/>parentItemId').attr('value', '0');
											// get sibling
											var dictCollection = A.one('#<portlet:namespace/><%=DictItemDisplayTerms.DICTCOLLECTION_ID%>');
											var parentItem = A.one('#<portlet:namespace/><%=DictItemDisplayTerms.PARENTITEM_ID%>');
											
											if (dictCollection && parentItem){
												dictCollectionId = dictCollection.val();
												parentItemId = parentItem.val();
												
												getSelectSibling(dictCollectionId, parentItemId, selectedDictItemId);
											}
										}
									}
								}
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
								getDictItemsToolbar(selectedDictCollectionId);
							});
						}
						
						// hide locate
						if (A.one('.input-localized-content')){
							A.one('.input-localized-content').setStyle('display', 'none');
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
		if (!Liferay.ThemeDisplay.isSignedIn()){
			alert(Liferay.Language.get('please-login-and-try-again'));
			return;
		}
		
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
		
		var collectionName = A.one('#<portlet:namespace/>collectionName') ? 
    			A.one('#<portlet:namespace/>collectionName').attr('value') : '';
		var collectionCode = A.one('#<portlet:namespace/>collectionCode') ? 
   				A.one('#<portlet:namespace/>collectionCode').attr('value') : '';
   		var description = A.one('#<portlet:namespace/>description') ? 
    			A.one('#<portlet:namespace/>description').attr('value') : '';
    	if (collectionName == ''){
    		alert(Liferay.Language.get('please-enter-collection-name'));
    		return false;
    	}
    	if (collectionCode == ''){
    		alert(Liferay.Language.get('please-enter-collection-code'));
    		return false;
    	}
    	
    	var loadingMask = new A.LoadingMask(
   			{
   				'strings.loading': '<%= UnicodeLanguageUtil.get(pageContext, "...") %>',
   				target: A.one('#<portlet:namespace/>collection-detail')
   			}
   		);
   		loadingMask.show();
		
		A.io.request(
			portletURL.toString(),
			{
				method: 'POST',
			    data:{    	
			    	<portlet:namespace/>collectionName: collectionName,
			    	<portlet:namespace/>collectionCode: collectionCode,
			    	<portlet:namespace/>description: description,
			    	<portlet:namespace/>dictCollectionId: dictCollectionId,
			    	<portlet:namespace/>collectionLinked: collectionLinked,
			    },   
			    on: {
			        success: function(event, id, obj){
			        	needConfirnChangeView = false;
			        	loadingMask.hide();
			        	var collectionNameSearch = A.one('#<portlet:namespace/>collection-name') ? 
								A.one('#<portlet:namespace/>collection-name').attr('value') : '';
			        	getDictCollections(collectionNameSearch);
			        	getDictCollectionDetail(selectedDictCollectionId);
			        	// show other component
						$('.hide-when-add-collection').slideDown('normal');
						alert(Liferay.Language.get('update-dict-collection') + ' ' + collectionName + ' ' + Liferay.Language.get('success'));
					},
			    	error: function(){
			    		loadingMask.hide();
			    		alert(Liferay.Language.get('error'));
			    	}
				}
			}
		);
		return true;		
	},['aui-base','liferay-portlet-url','aui-io']);
	
	Liferay.provide(window, 'updateDictItem', function(dictItemId, dictCollectionId){
		if (!Liferay.ThemeDisplay.isSignedIn()){
			alert(Liferay.Language.get('please-login-and-try-again'));
			return;
		}
		
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
		
		var itemName = A.one('#<portlet:namespace/>itemName') ?
    			A.one('#<portlet:namespace/>itemName').attr('value') : '';
    	var itemCode = A.one('#<portlet:namespace/>itemCode') ?
    			A.one('#<portlet:namespace/>itemCode').attr('value') : '';
    	var parentItemId = A.one('#<portlet:namespace/>parentItemId') ?
    			A.one('#<portlet:namespace/>parentItemId').attr('value') : 0;
    	var sibling = A.one('#<portlet:namespace/>sibling') ?
    			A.one('#<portlet:namespace/>sibling').attr('value') : 0;
		var status = A.one('#<portlet:namespace/>itemsStatusInUsed') ?
 	    		A.one('#<portlet:namespace/>itemsStatusInUsed').attr('value') : 1;
    	if (itemName == ''){
    		alert(Liferay.Language.get('please-enter-item-name'));
    		return;
    	}
    	if (itemCode == ''){
    		alert(Liferay.Language.get('please-enter-item-code'));
    		return;
    	}
    	
    	var loadingMask = new A.LoadingMask(
   			{
   				'strings.loading': '<%= UnicodeLanguageUtil.get(pageContext, "...") %>',
   				target: A.one('#<portlet:namespace/>collection-detail')
   			}
   		);
   		loadingMask.show();
		
		A.io.request(
			portletURL.toString(),
			{
				method: 'POST',
			    data:{    	
			    	<portlet:namespace/><%=DictItemDisplayTerms.DICTITEM_ID%>: dictItemId,
			    	<portlet:namespace/><%=DictItemDisplayTerms.DICTCOLLECTION_ID%>: dictCollectionId,
			    	<portlet:namespace/><%=DictItemDisplayTerms.PARENTITEM_ID%>: parentItemId,
			    	<portlet:namespace/><%=DictItemDisplayTerms.ITEM_NAME%>: itemName,
			    	<portlet:namespace/><%=DictItemDisplayTerms.ITEM_CODE%>: itemCode,
			    	<portlet:namespace/><%=DictItemDisplayTerms.SIBLING%>: sibling,
			    	<portlet:namespace/>dictItemLinked: itemsLinked,
			    	<portlet:namespace/>status: status,
			    },   
			    on: {
			        success: function(event, id, obj){
			        	setTimeout(function(){
							getDictItemsToolbar(selectedDictCollectionId);
							alert(Liferay.Language.get('success'));
						}, 500);
					},
			    	error: function(){
			    		loadingMask.hide();
			    		alert(Liferay.Language.get('error'));
			    	}
				}
			}
		);
		
	},['aui-base','liferay-portlet-url','aui-io']);
	
	Liferay.provide(window, 'changeStatusItemToNoUse', function(dictItemId){
		if (!Liferay.ThemeDisplay.isSignedIn()){
			alert(Liferay.Language.get('please-login-and-try-again'));
			return;
		}
		
		var portletURL = Liferay.PortletURL.createURL('<%= PortletURLFactoryUtil.create(request, WebKeys.DATA_MANAGEMENT_ADMIN_PORTLET, themeDisplay.getPlid(), PortletRequest.ACTION_PHASE) %>');
		portletURL.setParameter("javax.portlet.action", "changeStatusItemToNoUse");
		portletURL.setWindowState('<%=WindowState.NORMAL%>');
		
    	var loadingMask = new A.LoadingMask(
   			{
   				'strings.loading': '<%= UnicodeLanguageUtil.get(pageContext, "...") %>',
   				target: A.one('#<portlet:namespace/>collection-detail')
   			}
   		);
   		loadingMask.show();
		
		A.io.request(
			portletURL.toString(),
			{
				method: 'POST',
			    data:{    	
			    	<portlet:namespace/><%=DictItemDisplayTerms.DICTITEM_ID%>: dictItemId,
			    },   
			    on: {
			        success: function(event, id, obj){
			        	setTimeout(function(){
			        		getDictItemsToolbar(selectedDictCollectionId);
							//alert(Liferay.Language.get('success'));
						}, 500);
					},
			    	error: function(){
			    		loadingMask.hide();
			    		alert(Liferay.Language.get('error'));
			    	}
				}
			}
		);
		
	},['aui-base','liferay-portlet-url','aui-io']);
	
	Liferay.provide(window, 'deleteDictItem', function(dictItemId){
		if (!Liferay.ThemeDisplay.isSignedIn()){
			alert(Liferay.Language.get('please-login-and-try-again'));
			return;
		}
		
		var portletURL = Liferay.PortletURL.createURL('<%= PortletURLFactoryUtil.create(request, WebKeys.DATA_MANAGEMENT_ADMIN_PORTLET, themeDisplay.getPlid(), PortletRequest.ACTION_PHASE) %>');
		portletURL.setParameter("javax.portlet.action", "deleteDictItem");
		portletURL.setWindowState('<%=WindowState.NORMAL%>');
		
    	var loadingMask = new A.LoadingMask(
   			{
   				'strings.loading': '<%= UnicodeLanguageUtil.get(pageContext, "...") %>',
   				target: A.one('#<portlet:namespace/>collection-detail')
   			}
   		);
   		loadingMask.show();
		
		A.io.request(
			portletURL.toString(),
			{
				method: 'POST',
			    data:{    	
			    	<portlet:namespace/><%=DictItemDisplayTerms.DICTITEM_ID%>: dictItemId,
			    },   
			    on: {
			        success: function(event, id, obj){
			        	setTimeout(function(){
							getDictItems(selectedDictCollectionId);
							alert(Liferay.Language.get('success'));
						}, 500);
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
		if (!Liferay.ThemeDisplay.isSignedIn()){
			alert(Liferay.Language.get('please-login-and-try-again'));
			return;
		}
		
		var getSelectSiblingURL = Liferay.PortletURL.createURL('<%= PortletURLFactoryUtil.create(request, WebKeys.DATA_MANAGEMENT_ADMIN_PORTLET, themeDisplay.getPlid(), PortletRequest.RENDER_PHASE) %>');
		getSelectSiblingURL.setParameter("mvcPath", "/html/portlets/data_management/admin/ajax/_select_sibling.jsp");
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
		if (!Liferay.ThemeDisplay.isSignedIn()){
			alert(Liferay.Language.get('please-login-and-try-again'));
			return;
		}
		
		var getItemsLinkedURL = Liferay.PortletURL.createURL('<%= PortletURLFactoryUtil.create(request, WebKeys.DATA_MANAGEMENT_ADMIN_PORTLET, themeDisplay.getPlid(), PortletRequest.RENDER_PHASE) %>');
		getItemsLinkedURL.setParameter("mvcPath", "/html/portlets/data_management/admin/ajax/_select_dictitems_type.jsp");
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
						
						// initial value for item link checkbox
						if (A.all('.unchecked-checkbox')){
							A.all('.unchecked-checkbox').each(function(noSelected){
								if (noSelected.ancestor().one('#<portlet:namespace/>dictItemLinked')){
									noSelected.ancestor().one('#<portlet:namespace/>dictItemLinked').attr('value', '0');
								}
							});
						}
						
						// click select checkbox dict item type
						if ($('.click-select-dict-item-type')){
							$('.click-select-dict-item-type').each(function(){
								$(this).click(function(){
									var li = $(this).closest('li');
									var dictItemId = $(li)['0']['id'].replace(/.+dictItemId_/, '');
									if (!($(li)['0'].className).includes('checked-collection')){
										$(li).find('input[type=checkbox]').each(function(){
											$(this).prop('checked', true);
										});
										$(li).find('input[type=hidden]').each(function(){
											$(this).prop('value', dictItemId);
										});
										$(li)['0'].className += ' checked-collection';
									} else {
										$(li).find('input[type=checkbox]').each(function(){
											$(this).prop('checked', false);
										});
										$(li).find('input[type=hidden]').each(function(){
											$(this).prop('value', 0);
										});
										$(li)['0'].className = $(li)['0'].className.replace(' checked-collection', '');
									}
								});
							});
						}
						
						// toggle expand
						/* if ($('.expand-anchor')){
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
						} */
					},
				}
			}
		);
	},['aui-base','liferay-portlet-url','aui-io']);
	
	Liferay.provide(window, 'getParentDictItemsList', function(dictCollectionId, dictItemId, keyword){
		if (!Liferay.ThemeDisplay.isSignedIn()){
			alert(Liferay.Language.get('please-login-and-try-again'));
			return;
		}
		
		var portletURL = Liferay.PortletURL.createURL('<%= PortletURLFactoryUtil.create(request, WebKeys.DATA_MANAGEMENT_ADMIN_PORTLET, themeDisplay.getPlid(), PortletRequest.RENDER_PHASE) %>');
		portletURL.setParameter("mvcPath", "/html/portlets/data_management/admin/ajax/_select_parent_dictitem.jsp");
		portletURL.setWindowState("<%=LiferayWindowState.EXCLUSIVE.toString()%>"); 
		portletURL.setPortletMode("normal");
		portletURL.setParameter("dictCollectionId", dictCollectionId);
		portletURL.setParameter("dictItemId", dictItemId);
		if (keyword){
			portletURL.setParameter("keywordSearchItemParent", keyword);
		}
		
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
						
						if (A.all('.select-dict-item-parent')){
							A.all('.select-dict-item-parent').each(function(item){
								item.on('click', function(event){
									event.preventDefault();
									var itemId = A.one(this)['_node']['id'].replace(/^.+parentItemId_/, '');
									var name = A.one(this).one('a').html().replace(/^&nbsp;.+&nbsp;/, '');
									
									var parent = A.one('#<portlet:namespace/>parentItemId');
									var parentShow = A.one('#<portlet:namespace/>parentItemShow');
									parent.attr('value', itemId);
									parentShow.attr('value', name);
									
									if (A.one('#<portlet:namespace/>parentItem')){
										A.one('#<portlet:namespace/>parentItem').removeClass('show-when-focus-datamgt');
										A.one('#<portlet:namespace/>parentItem').addClass('hide-when-focusout-datamgt');
									}
									// get sibling
									var dictCollection = A.one('#<portlet:namespace/><%=DictItemDisplayTerms.DICTCOLLECTION_ID%>');
									var parentItem = A.one('#<portlet:namespace/><%=DictItemDisplayTerms.PARENTITEM_ID%>');
									
									if (dictCollection && parentItem){
										dictCollectionId = dictCollection.val();
										parentItemId = parentItem.val();
										
										getSelectSibling(dictCollectionId, parentItemId, 0);
									}
								});
								item.on('mouseover', function(event){
									event.preventDefault();
									if (A.one('#<portlet:namespace/>parentItem')){
										A.one('#<portlet:namespace/>parentItem').addClass('no-touch-by-blur');
									}
								});
								item.on('mouseout', function(event){
									event.preventDefault();
									if (A.one('#<portlet:namespace/>parentItem')){
										A.one('#<portlet:namespace/>parentItem').removeClass('no-touch-by-blur');
									}
								});
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
	
	var checkLogined = function(){
		if (!Liferay.ThemeDisplay.isSignedIn()){
			alert(Liferay.Language.get('please-login-and-try-again'));
			return false;
		}
		return true;
	}
</aui:script>

<%!
	private Log _log = LogFactoryUtil.getLog("html.portlets.data_management.admin.display.dictcollections.jsp");
%>

