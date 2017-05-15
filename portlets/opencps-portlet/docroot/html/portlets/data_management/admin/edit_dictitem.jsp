
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

<%@page import="org.opencps.datamgt.model.DictCollection"%>
<%@page import="org.opencps.util.MessageKeys"%>
<%@page import="org.opencps.datamgt.EmptyItemCodeException"%>
<%@page import="org.opencps.datamgt.OutOfLengthItemCodeException"%>
<%@page import="org.opencps.datamgt.EmptyDictItemNameException"%>
<%@page import="org.opencps.datamgt.OutOfLengthItemNameException"%>
<%@page import="org.opencps.datamgt.DuplicateItemException"%>
<%@page import="org.opencps.datamgt.NoSuchDictItemException"%>
<%@page import="org.opencps.util.WebKeys"%>
<%@page import="com.liferay.portal.kernel.log.LogFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.log.Log"%>
<%@page import="org.opencps.datamgt.service.DictCollectionLocalServiceUtil"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="org.opencps.datamgt.search.DictItemDisplayTerms"%>
<%@page import="org.opencps.datamgt.model.DictItem"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@page import="javax.portlet.PortletRequest"%>
<%@page import="com.liferay.portlet.PortletURLFactoryUtil"%>
<%@page import="org.opencps.datamgt.service.DictItemTypeLocalServiceUtil"%>
<%@page import="org.opencps.datamgt.model.DictItemType"%>

<%@ include file="../init.jsp"%>

<portlet:actionURL var="updateDictItemURL" name="updateDictItem" />

<%
	DictItem dictItem = (DictItem)request.getAttribute(WebKeys.DICT_ITEM_ENTRY);
	long dictItemId = dictItem != null ? dictItem.getDictItemId() : 0L;
	String backURL = ParamUtil.getString(request, "backURL");
	
	List<DictCollection> dictCollections = new ArrayList<DictCollection>();
	List<DictItem> dictItems = new ArrayList<DictItem>();
	
	try{
		dictCollections = DictCollectionLocalServiceUtil.getDictCollections(scopeGroupId);
	}catch(Exception e){
		_log.error(e);
	}
	
	List<DictItemType> itemsType = new ArrayList<DictItemType>();
	String itemsTypeStr = StringPool.BLANK;
	try {
		itemsType = DictItemTypeLocalServiceUtil.getByDictItemId(dictItemId);
		List<Long> itemsTypeId = new ArrayList<Long>();
		for (DictItemType itemLinked : itemsType){
			itemsTypeId.add(itemLinked.getDictItemLinkedId());
		}
		itemsTypeStr = StringUtil.merge(itemsTypeId);
	} catch (Exception e){}
	
%>

<liferay-ui:header
	backURL="<%= backURL %>"
	title='<%= (dictItem == null) ? "add-dictitem" : "update-dictitem" %>'
/>


<div class="opencps-datamgt dictitem-wrapper opencps-bound-wrapper pd20 default-box-shadow">
	<div class="edit-form">
		<liferay-ui:error exception="<%= EmptyItemCodeException.class %>" message="<%=EmptyItemCodeException.class.getName() %>" />
		<liferay-ui:error exception="<%= OutOfLengthItemCodeException.class %>" message="<%=OutOfLengthItemCodeException.class.getName() %>" />
		<liferay-ui:error exception="<%= EmptyDictItemNameException.class %>" message="<%=EmptyDictItemNameException.class.getName() %>" />
		<liferay-ui:error exception="<%= OutOfLengthItemNameException.class %>" message="<%=OutOfLengthItemNameException.class.getName() %>" />
		<liferay-ui:error exception="<%= DuplicateItemException.class %>" message="<%=DuplicateItemException.class.getName() %>" />
		<liferay-ui:error exception="<%= NoSuchDictItemException.class %>" message="<%=NoSuchDictItemException.class.getName() %>" />
		<liferay-ui:error key="<%= MessageKeys.DATAMGT_SYSTEM_EXCEPTION_OCCURRED%>" message="<%=MessageKeys.DATAMGT_SYSTEM_EXCEPTION_OCCURRED %>" />

		<aui:form action="<%=updateDictItemURL.toString() %>" method="post" name="fm">
			
			<aui:model-context bean="<%=dictItem %>" model="<%=DictItem.class %>" />
			<aui:input name="<%=DictItemDisplayTerms.DICTITEM_ID %>" type="hidden"/>
			<aui:input name="redirectURL" type="hidden" value="<%=backURL %>"/>
			<aui:input name="returnURL" type="hidden" value="<%=currentURL %>"/>
			<aui:fieldset>
				
				<aui:input name="<%=DictItemDisplayTerms.ITEM_CODE%>" type="text" cssClass="input20">
					<aui:validator name="required"/>
					<aui:validator name="maxLength">100</aui:validator> 
				</aui:input>
			
				<aui:input name="<%=DictItemDisplayTerms.ITEM_NAME %>" cssClass="input80" label="item-name">
					<aui:validator name="required"/>
					<aui:validator name="minLength">3</aui:validator>
					<aui:validator name="maxLength">255</aui:validator>
				</aui:input>
				
				<aui:select name="<%=DictItemDisplayTerms.DICTCOLLECTION_ID %>" label="dict-collection">
					<aui:option value="0"/>
					<%
						if(dictCollections != null){
							for(DictCollection dictCollection : dictCollections){
								%>
									<aui:option value="<%=dictCollection.getDictCollectionId() %>">
										<%=dictCollection.getCollectionName(locale) %>
									</aui:option>
								<%
							}
						}
					%>
				</aui:select> 
				<div id='<%=renderResponse.getNamespace() + "parentItem" %>'>
					<aui:select name="<%=DictItemDisplayTerms.PARENTITEM_ID %>" label="parent-item">
						<aui:option value="0"></aui:option>
					</aui:select>
				</div>
				<%-- <aui:select name="<%=DictItemDisplayTerms.DICTVERSION_ID %>" label="dict-version">
					<aui:option value="0"></aui:option>
				</aui:select> --%>
				
				<!-- sibling -->
				<div id='<%=renderResponse.getNamespace() + "sibling-container" %>'>
					<aui:select name="<%=DictItemDisplayTerms.SIBLING %>" label="sibling">
						<aui:option value="0"></aui:option>
					</aui:select>
				</div>
				
				<!-- dictItem linked -->
				<label><liferay-ui:message key="dict-items-linked" /></label>
				<div id='<%=renderResponse.getNamespace() + "itemLinkedContainer" %>' ></div>
				
			</aui:fieldset>
			
			<aui:fieldset>
				<aui:button type="submit" name="submit" value="submit"/>
				<aui:button type="reset" value="clear"/>
			</aui:fieldset>	
		</aui:form>
	</div>
</div>

<!-- edit sibling -->
<div class="opencps-datamgt dictitem-wrapper opencps-bound-wrapper pd20 default-box-shadow">
	<aui:row>
		<liferay-ui:message key="edit-sibling"/>
	</aui:row>
	<div class="edit-form">
		<portlet:actionURL name="editDictItemSibling" var="editDictItemSiblingURL"/>
		<aui:form action="<%=editDictItemSiblingURL.toString() %>" method="POST" name="fm_editSibling">
			<aui:row>
				<aui:select name="numberedSiblingMode">
					<aui:option value="1" label="numbered-for-all-dictItems"/>
					<aui:option value="2" label="numbered-for-all-dictItems-in-dictcollection" selected="true"/>
				</aui:select>
			</aui:row>
			<aui:row>
				<aui:select name="<%=DictItemDisplayTerms.DICTCOLLECTION_ID %>">
					<aui:option value="0" label="select-dictcollection"/>
					<%
						List<DictCollection> collections = DictCollectionLocalServiceUtil.getDictCollections();
						for (DictCollection collection : collections){
							%>
								<aui:option value="<%=collection.getDictCollectionId() %>" ><%=collection.getCollectionName(locale) %></aui:option>
							<%
						}
					%>
				</aui:select>
			</aui:row>
			
			<aui:button type="submit"/>
		</aui:form>
	</div>
</div>

<aui:script>
	AUI().ready('aui-base','liferay-portlet-url','aui-io',function(A){
		
		var dictCollection = A.one('#<portlet:namespace/><%=DictItemDisplayTerms.DICTCOLLECTION_ID%>');
		
		if(dictCollection){
			
			var dictCollectionId = dictCollection.val();
			var dictItemId = '<%=dictItemId%>';
			
			getDictItems(dictCollectionId, dictItemId);
			
			getDictItemsLinked(dictCollectionId, dictItemId);
			
			getSelectSibling(dictCollectionId, 0, dictItemId);
			
			dictCollection.on('change', function(){
				dictCollectionId = dictCollection.val();
				
				getDictItems(dictCollectionId, dictItemId);
				
				getDictItemsLinked(dictCollectionId, dictItemId);
				
				getSelectSibling(dictCollectionId, 0, 0);
			});
		}
	});
	
	Liferay.provide(window, 'getSelectSibling', function(dictCollectionId, parentItemId, dictItemId) {
		var A = AUI();
		
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
			    data:{    	
			                	
			    },   
			    on: {
			        success: function(event, id, obj) {
						var instance = this;
						var siblings = instance.get('responseData');
						var siblingsContainer = A.one("#<portlet:namespace/>sibling-container");
						if(siblingsContainer){
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
	
	Liferay.provide(window, 'getDictItemsLinked', function(dictCollectionId, dictItemId) {
		var A = AUI();
		
		var getItemsLinkedURL = Liferay.PortletURL.createURL('<%= PortletURLFactoryUtil.create(request, WebKeys.DATA_MANAGEMENT_ADMIN_PORTLET, themeDisplay.getPlid(), PortletRequest.RENDER_PHASE) %>');
		getItemsLinkedURL.setParameter("mvcPath", "/html/portlets/data_management/admin/ajax/_select_dictitems_linked.jsp");
		getItemsLinkedURL.setWindowState("<%=LiferayWindowState.EXCLUSIVE.toString()%>"); 
		getItemsLinkedURL.setPortletMode("normal");
		
		getItemsLinkedURL.setParameter("dictCollectionId", dictCollectionId);
		getItemsLinkedURL.setParameter("dictItemId", dictItemId);
		
		A.io.request(
			getItemsLinkedURL.toString(),
			{
			    dataType: 'json',
			    data:{    	
			                	
			    },   
			    on: {
			        success: function(event, id, obj) {
						var instance = this;
						var itemsLinked = instance.get('responseData');
						var itemsLinkedContainer = A.one("#<portlet:namespace/>itemLinkedContainer");
						if(itemsLinkedContainer){
							itemsLinkedContainer.html(itemsLinked);
						}
						
						// set initial value
						var itemsLinkedStr = '<%=itemsTypeStr %>';
						var itemsLinkedArr = itemsLinkedStr.split(',');
						var match = false;
						A.all('#<portlet:namespace/>dictItemLinked').each(function(dictItem){
							match = false;
							for (var i = 0; i < itemsLinkedArr.length; i++) {
						        if (itemsLinkedArr[i] == dictItem.attr('value')) {
						        	match = true;
						        	break;
						        }
						    }
							if (!match){
								dictItem.attr('value', '0');
							}
						});
						
						// toggle expand
						for (var i = 0; i < $('.expand-anchor').length; i++){
							var colId = $('.expand-anchor')[i].id.replace(/^.+dictCollectionId_/, '');
							$('#<portlet:namespace/>expandable_' + colId).slideToggle( "normal");
							A.one('#<portlet:namespace/>expand-anchor_dictCollectionId_' + colId).on('click', function(){
								var id = this['_node']['id'].replace(/^.+dictCollectionId_/, '');
								$('#<portlet:namespace/>expandable_' + id).slideToggle( "normal");
								if (previousId.length > 0 && previousId != id){
									$('#<portlet:namespace/>expandable_' + previousId).slideToggle("normal");
								} 
								if (previousId == id) {
									previousId = '';
								} else {
									previousId = id;
								}
							});
						}
					},
			    	error: function(){}
				}
			}
		);
	},['aui-base','liferay-portlet-url','aui-io']);
	
	Liferay.provide(window, 'getDictItems', function(dictCollectionId, dictItemId) {
		var A = AUI();
		
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
			    data:{    	
			                	
			    },   
			    on: {
			        success: function(event, id, obj) {
						var instance = this;
						var dictItems = instance.get('responseData');
						var parentItemContainer = A.one("#<portlet:namespace/>parentItem");
						
						if(parentItemContainer){
							parentItemContainer.empty();
							parentItemContainer.html(dictItems);
						}
						
						var dictCollection = A.one('#<portlet:namespace/><%=DictItemDisplayTerms.DICTCOLLECTION_ID%>');
						var parentItem = A.one('#<portlet:namespace/><%=DictItemDisplayTerms.PARENTITEM_ID%>');
						
						parentItem.on('change', function(){
							dictCollectionId = dictCollection.val();
							parentItemId = parentItem.val();
							
							getSelectSibling(dictCollectionId, parentItemId, 0);
						});
					},
			    	error: function(){}
				}
			}
		);
	},['aui-base','liferay-portlet-url','aui-io']);
	
</aui:script>
<%!
	private Log _log = LogFactoryUtil.getLog("html.portlets.data_management.admin.edit_dictitem.jsp");
%>
