
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
<%@page import="org.opencps.datamgt.service.DictItemLinkLocalServiceUtil"%>
<%@page import="org.opencps.datamgt.model.DictItemLink"%>

<%@ include file="../../init.jsp"%>

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
	
	List<DictItemLink> itemsLinked = new ArrayList<DictItemLink>();
	String itemsLinkedStr = StringPool.BLANK;
	try {
		itemsLinked = DictItemLinkLocalServiceUtil.getByDictItemId(dictItemId);
		List<Long> itemsLinkedId = new ArrayList<Long>();
		for (DictItemLink itemLinked : itemsLinked){
			itemsLinkedId.add(itemLinked.getDictItemLinkedId());
		}
		itemsLinkedStr = StringUtil.merge(itemsLinkedId);
	} catch (Exception e){}
	
%>

<liferay-ui:header
	backURL="<%= backURL %>"
	title='<%= (dictItem == null) ? "add-dictitem" : "update-dictitem" %>'
/>


<div class="opencps-datamgt dictitem-wrapper opencps-bound-wrapper pd20 default-box-shadow">
	<div class="edit-form">
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
				<div id='<%=renderResponse.getNamespace() + "sibling" %>'>
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

<%!
	private Log _log = LogFactoryUtil.getLog("html.portlets.data_management.admin.edit_dictitem.jsp");
%>
