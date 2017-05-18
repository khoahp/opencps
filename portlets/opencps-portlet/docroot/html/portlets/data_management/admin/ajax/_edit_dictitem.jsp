
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

<portlet:actionURL var="updateDictItemURL" name="updateDictItem" />

<%
	DictItem dictItem = (DictItem)request.getAttribute(WebKeys.DICT_ITEM_ENTRY);
	long dictItemId = dictItem != null ? dictItem.getDictItemId() : 0L;
	long collectionId = ParamUtil.getLong(request, DictItemDisplayTerms.DICTCOLLECTION_ID);
	String backURL = ParamUtil.getString(request, "backURL");
	
	DictCollection collection = null;
	List<DictItem> dictItems = new ArrayList<DictItem>();
	
	try{
		collection = DictCollectionLocalServiceUtil.getDictCollection(collectionId);
	}catch(Exception e){
		_log.error(e);
	}
	
	List<DictItemType> itemTypes = new ArrayList<DictItemType>();
	String itemTypesStr = StringPool.BLANK;
	try {
		itemTypes = DictItemTypeLocalServiceUtil.getByDictItemId(dictItemId);
		List<Long> itemsTypesId = new ArrayList<Long>();
		for (DictItemType itemLinked : itemTypes){
			itemsTypesId.add(itemLinked.getDictItemLinkedId());
		}
		itemTypesStr = StringUtil.merge(itemsTypesId);
	} catch (Exception e){}
	
%>

<p class="breadcrumb bold">
	<liferay-ui:message key='dict-collection' /> > <%=collection != null ? collection.getCollectionName() : StringPool.BLANK %> >
	<liferay-ui:message key='<%= (dictItem == null) ? "add-dictitem" : "update-dictitem" %>' />
	<liferay-ui:message key='<%= (dictItem == null) ? StringPool.BLANK : dictItem.getItemName(locale) %>' />
</p>

<div class="opencps-datamgt dictitem-wrapper opencps-bound-wrapper pd20 default-box-shadow">
	<div class="edit-form">
		<aui:form action="<%=updateDictItemURL.toString() %>" method="post" name="fm">
			
			<aui:model-context bean="<%=dictItem %>" model="<%=DictItem.class %>" />
			<aui:input name="<%=DictItemDisplayTerms.DICTITEM_ID %>" type="hidden"/>
			<aui:input name="redirectURL" type="hidden" value="<%=backURL %>"/>
			<aui:input name="returnURL" type="hidden" value="<%=currentURL %>"/>
			
			<aui:fieldset>
				<aui:row>
					<aui:col width="50">
						<aui:row>
							<aui:input name="<%=DictItemDisplayTerms.ITEM_CODE%>" type="text" cssClass="input20" label="item-code">
								<aui:validator name="required"/>
								<aui:validator name="maxLength">100</aui:validator> 
							</aui:input>
						</aui:row>
						<aui:row>
							<aui:input name="<%=DictItemDisplayTerms.ITEM_NAME %>" cssClass="input80" label="item-name">
								<aui:validator name="required"/>
								<aui:validator name="minLength">3</aui:validator>
								<aui:validator name="maxLength">255</aui:validator>
							</aui:input>
						</aui:row>
						<aui:row>
							<aui:select name="<%=DictItemDisplayTerms.DICTCOLLECTION_ID %>" label="dict-collection">
								<aui:option value="<%=collectionId %>"><%=collection.getCollectionName(locale) %></aui:option>
							</aui:select>
						</aui:row>
						<aui:row>
							<div id='<%=renderResponse.getNamespace() + "parentItem" %>'>
								<aui:select name="<%=DictItemDisplayTerms.PARENTITEM_ID %>" label="parent-item">
									<aui:option value="0"></aui:option>
								</aui:select>
							</div>
						</aui:row>
						<aui:row>
							<div id='<%=renderResponse.getNamespace() + "sibling-container" %>'>
								<aui:select name="<%=DictItemDisplayTerms.SIBLING %>" label="sibling">
									<aui:option value="0"></aui:option>
								</aui:select>
							</div>
						</aui:row>
						<aui:row>
							<aui:select name="itemsStatusInUsed">
								<aui:option value="0" label="draf" selected="<%=dictItem != null ? dictItem.getIssueStatus() == 0 : false %>" />
								<aui:option value="1" label="in-used" selected="<%=(dictItem != null ? dictItem.getIssueStatus() == 1 : false) || dictItem == null%>" />
								<aui:option value="2" label="no-used" selected="<%=dictItem != null ? dictItem.getIssueStatus() == 2 : false %>" />
							</aui:select>
						</aui:row>
					</aui:col>
					<aui:col width="50">
						<label><liferay-ui:message key="dict-items-linked" /></label>
						<div id='<%=renderResponse.getNamespace() + "itemLinkedContainer" %>' ></div>
					</aui:col>
				</aui:row>
			</aui:fieldset>
			
				<%-- <aui:select name="<%=DictItemDisplayTerms.DICTVERSION_ID %>" label="dict-version">
					<aui:option value="0"></aui:option>
				</aui:select> --%>
			
			<aui:fieldset>
				<aui:button type="submit" name="submit" value="submit"/>
				<aui:button type="submit" name="cancel" value="cancel"/>
			</aui:fieldset>	
		</aui:form>
	</div>
</div>

<%!
	private Log _log = LogFactoryUtil.getLog("html.portlets.data_management.admin.edit_dictitem.jsp");
%>
