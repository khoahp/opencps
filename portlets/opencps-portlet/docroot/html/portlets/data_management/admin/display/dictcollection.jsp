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

<%@page import="javax.portlet.PortletRequest"%>
<%@page import="org.opencps.util.WebKeys"%>
<%@page import="com.liferay.portlet.PortletURLFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@page import="com.liferay.portal.kernel.log.LogFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.log.Log"%>

<%@ include file="../../init.jsp"%>

<div class="span3">
	<div class="opencps-searchcontainer-wrapper default-box-shadow radius8">
		<div class="openCPSTree yui3-widget component tree-view tree-drag-drop">
			<aui:button value="add"/>
			<aui:input name="collection-name" placeholder='<%= LanguageUtil.get(locale, "name") %>' />
			<aui:button name="search-button" value="search" />
			<div id='<%=renderResponse.getNamespace() + "collections-container" %>' ></div>
		</div>
	</div>
</div>

<div class="span9">
	<div class="opencps-searchcontainer-wrapper default-box-shadow radius8">
		<div id='<%=renderResponse.getNamespace() + "collection-detail" %>' ></div>
	</div>
</div>

<aui:script>
	AUI().ready('aui-base','liferay-portlet-url','aui-io', function(A){
		getDictCollections();
		getDictCollectionDetail();
		
		A.one('#<portlet:namespace/>search-button')
			.on('click', function(){
				var collectionName = A.one('#<portlet:namespace/>collection-name').attr('value');
				getDictCollections(collectionName);
			});
		A.one('#<portlet:namespace/>collection-name')
			.on('change', function(){
				var collectionName = A.one('#<portlet:namespace/>collection-name').attr('value');
				getDictCollections(collectionName);
			});
	});
	
	var selectedDictCollectionId = 0;	
	Liferay.provide(window, 'getDictCollectionDetail', function(collectionId){
		var A = AUI();
		
		var portletURL = Liferay.PortletURL.createURL('<%= PortletURLFactoryUtil.create(request, WebKeys.DATA_MANAGEMENT_ADMIN_PORTLET, themeDisplay.getPlid(), PortletRequest.RENDER_PHASE) %>');
		portletURL.setParameter("mvcPath", "/html/portlets/data_management/admin/display/dictcollection_detail.jsp");
		portletURL.setWindowState("<%=LiferayWindowState.EXCLUSIVE.toString()%>"); 
		portletURL.setPortletMode("normal");
		
		portletURL.setParameter("collectionId", collectionId);
		
		A.io.request(
			portletURL.toString(),
			{
			    dataType: 'json',
			    data:{    	
			                	
			    },   
			    on: {
			        success: function(event, id, obj) {
						var instance = this;
						var content = instance.get('responseData');
						var collectionDetail = A.one("#<portlet:namespace/>collection-detail");
						
						if(collectionDetail){
							collectionDetail.html(content);
						}
						
						var viewItemsButton = A.one('#<portlet:namespace/>view-items-button');
						if (viewItemsButton){
							viewItemsButton.on('click', function(){
								getDictItems(selectedDictCollectionId);
							});
						}
							
					},
			    	error: function(){}
				}
			}
		);
	},['aui-base','liferay-portlet-url','aui-io']);
	
	Liferay.provide(window, 'getDictItems', function(dictCollectionId){
		var A = AUI();
		
		var portletURL = Liferay.PortletURL.createURL('<%= PortletURLFactoryUtil.create(request, WebKeys.DATA_MANAGEMENT_ADMIN_PORTLET, themeDisplay.getPlid(), PortletRequest.RENDER_PHASE) %>');
		portletURL.setParameter("mvcPath", "/html/portlets/data_management/admin/display/dictitems.jsp");
		portletURL.setWindowState("<%=LiferayWindowState.EXCLUSIVE.toString()%>"); 
		portletURL.setPortletMode("normal");
		
		portletURL.setParameter("dictCollectionId", dictCollectionId);
		
		A.io.request(
			portletURL.toString(),
			{
			    dataType: 'json',
			    data:{    	
			                	
			    },   
			    on: {
			        success: function(event, id, obj) {
						var instance = this;
						var items = instance.get('responseData');
						var itemsContainer = A.one("#<portlet:namespace/>collection-detail");
						
						if(itemsContainer){
							itemsContainer.html(items);
						}
					},
			    	error: function(){}
				}
			}
		);
	},['aui-base','liferay-portlet-url','aui-io']);
	
	Liferay.provide(window, 'getDictCollections', function(collectionName){
		var A = AUI();
		
		var portletURL = Liferay.PortletURL.createURL('<%= PortletURLFactoryUtil.create(request, WebKeys.DATA_MANAGEMENT_ADMIN_PORTLET, themeDisplay.getPlid(), PortletRequest.RENDER_PHASE) %>');
		portletURL.setParameter("mvcPath", "/html/portlets/data_management/admin/get_dictcollections.jsp");
		portletURL.setWindowState("<%=LiferayWindowState.EXCLUSIVE.toString()%>"); 
		portletURL.setPortletMode("normal");
		
		portletURL.setParameter("collectionName", collectionName);
		
		A.io.request(
			portletURL.toString(),
			{
			    dataType: 'json',
			    data:{    	
			                	
			    },   
			    on: {
			        success: function(event, id, obj) {
						var instance = this;
						var collections = instance.get('responseData');
						var container = A.one("#<portlet:namespace/>collections-container");
						
						if(container){
							container.html(collections);
						}
						
						A.all('.collection-tree-node').each(function(node){
							node.on('click', function(){
								var collectionId = node['_node']['id'].replace(/^.+collectionId_/, '');
								selectedDictCollectionId = collectionId;
								getDictCollectionDetail(collectionId);
							});
						});
					},
			    	error: function(){}
				}
			}
		);
	},['aui-base','liferay-portlet-url','aui-io']);
</aui:script>

<%!
	private Log _log = LogFactoryUtil.getLog("html.portlets.data_management.admin.display.dictcollection.jsp");
%>

