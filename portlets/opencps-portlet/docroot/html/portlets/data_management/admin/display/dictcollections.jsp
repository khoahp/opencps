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
				<aui:button value="add" onClick="editDictCollection()"/>
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
		
		A.one('#<portlet:namespace/>search-button').on('click', function(){
			var collectionName = A.one('#<portlet:namespace/>collection-name').attr('value');
			getDictCollections(collectionName);
		});
		
		A.one('#<portlet:namespace/>collection-name').on('change', function(){
			var collectionName = A.one('#<portlet:namespace/>collection-name').attr('value');
			getDictCollections(collectionName);
		});
	});
	
	var selectedDictCollectionId = 0;	
	Liferay.provide(window, 'getDictCollectionDetail', function(collectionId){
		
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
			    data:{    	
			                	
			    },   
			    on: {
			        success: function(event, id, obj) {
			        	loadingMask.hide();
			        	
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
						
						scrollWindow();
					},
			    	error: function(){
			    		loadingMask.hide();
			    	}
				}
			}
		);
	},['aui-base','liferay-portlet-url','aui-io']);
	
	Liferay.provide(window, 'getDictItems', function(dictCollectionId, cur){
		
		var loadingMask = new A.LoadingMask(
			{
				'strings.loading': '<%= UnicodeLanguageUtil.get(pageContext, "...") %>',
				target: A.one('#<portlet:namespace/>collection-detail')
			}
		);
		loadingMask.show();
		
		var iteratorURL = Liferay.PortletURL.createURL('<%=iteratorURL.toString()%>');
		iteratorURL.setParameter('<%=DictItemDisplayTerms.DICTCOLLECTION_ID %>', selectedDictCollectionId);
		if (!cur){
			iteratorURL.setParameter('cur', '1');
		} else {
			iteratorURL.setParameter('cur', cur);
		}
		
		A.io.request(
			iteratorURL.toString(),
			{
			    dataType: 'json',
			    data:{    	
			                	
			    },   
			    on: {
			        success: function(event, id, obj) {
			        	loadingMask.hide();
			        	
						var instance = this;
						var items = instance.get('responseData');
						var itemsContainer = A.one("#<portlet:namespace/>collection-detail");
						
						if(itemsContainer){
							itemsContainer.html(items);
						}
						
						A.all('.items-container').all('.pagination').all('a').each(function(navigation){
							navigation.on('click', function(event){
								event.preventDefault();
								
								var cur = event['target']['_node']['innerText'];
								
								getDictItems(selectedDictCollectionId, cur);
							});
						});
						
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
			    data:{    	
			                	
			    },   
			    on: {
			        success: function(event, id, obj) {
			        	loadingMask.hide();
			        	
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
						
						scrollWindow();
					},
			    	error: function(){
			    		loadingMask.hide();
			    	}
				}
			}
		);
	},['aui-base','liferay-portlet-url','aui-io']);
	
	Liferay.provide(window, 'editDictCollection', function(){
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
		
		A.io.request(
			portletURL.toString(),
			{
			    dataType: 'json',
			    data:{    	
			                	
			    },   
			    on: {
			        success: function(event, id, obj) {
			        	loadingMask.hide();
			        	
						var instance = this;
						var result = instance.get('responseData');
						var container = A.one("#<portlet:namespace/>collection-detail");
						
						if(container){
							container.html(result);
						}
						
						// initial value for dictcollection link checkbox
						var collectionsLinked = '';
						var linkedArr = collectionsLinked.split(',');
						var match = false;
						A.all('#<portlet:namespace/>dictCollectionsLinked').each(function(dictCol){
							match = false;
							for (var i = 0; i < linkedArr.length; i++) {
						        if (linkedArr[i] == dictCol.attr('value')) {
						        	match = true;
						        	break;
						        }
						    }
							if (!match){
								dictCol.attr('value', '0');
							}
						});
						
						A.one('#<portlet:namespace/>fm')
							.one('#<portlet:namespace/>submit')
								.on('click', function(event){
							event.preventDefault();
							updateDictCollection();
						});
					},
			    	error: function(){
			    		loadingMask.hide();
			    	}
				}
			}
		);
	},['aui-base','liferay-portlet-url','aui-io']);
	
	Liferay.provide(window, 'updateDictCollection', function(){
		
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
		A.all('#<portlet:namespace/>dictCollectionsLinked').each(function(dictCol){
			if (parseInt(dictCol.attr('value')) > 0){
				collectionLinked += dictCol.attr('value') + ',';
			}
		});
		
		console.log('collectionLinked: '+collectionLinked);
		
		A.io.request(
			portletURL.toString(),
			{
				method: 'POST',
			    data:{    	
			    	<portlet:namespace/>collectionName: A.one('#<portlet:namespace/>collectionName').attr('value'),
			    	<portlet:namespace/>collectionCode: A.one('#<portlet:namespace/>collectionCode').attr('value'),
			    	<portlet:namespace/>description: A.one('#<portlet:namespace/>description').attr('value'),
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
	
	Liferay.provide(window, 'scrollWindow', function(){
		var anchor = A.one('#<portlet:namespace/>anchor-scroll');
		$("html, body").animate({ scrollTop: anchor.getY() - 60 }, "normal");
	});
</aui:script>

<%!
	private Log _log = LogFactoryUtil.getLog("html.portlets.data_management.admin.display.dictcollections.jsp");
%>

