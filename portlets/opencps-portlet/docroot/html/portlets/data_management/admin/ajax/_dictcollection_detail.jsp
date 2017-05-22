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

<%@page import="org.opencps.datamgt.model.impl.DictPermissionsImpl"%>

<%@ include file="../../init.jsp"%>

<%
	long collectionId = ParamUtil.getLong(request, "collectionId");
	DictCollection collection = null;
	List<DictCollectionType> collectionsTypes = new ArrayList<DictCollectionType>();
	try {
		collection = DictCollectionLocalServiceUtil.getDictCollection(collectionId);
		collectionsTypes = DictCollectionTypeLocalServiceUtil.getByDictCollectionId(collectionId);
	} catch (Exception e){}
	List<String> collectionTypes = new ArrayList<String>();
	for (DictCollectionType type : collectionsTypes){
		collectionTypes.add(type.getDictCollectionLinkedName());
	}
	String typesStr = StringUtil.merge(collectionTypes, StringPool.COMMA + StringPool.SPACE);
	
	boolean showDeleteButton = false;
	List<DictItem> items = new ArrayList<DictItem>();
	try {
		items = DictItemLocalServiceUtil.getDictItemsByDictCollectionId(collectionId, 0, 1, null);
	} catch (Exception e){}
	if (items.size() == 0){
		showDeleteButton =  true;
	}
	
	DictPermissions dictPermission = new DictPermissionsImpl();
	DictPermissionsPK permissionPk = 
			new DictPermissionsPK(user != null ? user.getUserId() : 0, collectionId);
	try {
		dictPermission = DictPermissionsLocalServiceUtil
				.getDictPermissions(permissionPk);
	} catch (Exception e){}
	
%>

<c:if test="<%=collection == null %>">
	<p class="breadcrumb bold" title='<%=LanguageUtil.get(locale, "dictcollection-statistic") %>'>
		<liferay-ui:message key='dictcollection-statistic' />
	</p>
	<p><span><liferay-ui:message key='dict-collection' />:</span> <%=DictCollectionLocalServiceUtil.countAll() %></p>
	<p><span><liferay-ui:message key='tatal-dictitems' />:</span> <%=DictItemLocalServiceUtil.countAll() %></p>
	<%
		DictCollection col = null;
		try {
			col = DictCollectionLocalServiceUtil.getDictCollections(0, 1, 
					DataMgtUtil.getDictCollectionOrderByComparator(
							DictCollectionDisplayTerms.MODIFIED_DATE, 
							WebKeys.ORDER_BY_DESC)).get(0);
		} catch (Exception e){}
	%>
	<p><span><liferay-ui:message key="update-date" />
		:</span> <%=col != null ? DateTimeUtil.convertDateToString(col.getModifiedDate(), DateTimeUtil._VN_DATE_TIME_FORMAT) : StringPool.DASH %>
	</p>
</c:if>

<c:if test="<%=collection != null %>">
	<div>
		<p class="breadcrumb bold">
			<a title='<%=LanguageUtil.get(locale, "dict-collection-mgt") %>' href="javascript:getDictCollectionDetail();" >
				<liferay-ui:message key='dict-collection-mgt' />
			</a>
			<liferay-ui:message key='<%=" >> " + collection.getCollectionName(locale) %>' />
		</p>
	</div>

	<div>
		<p><span><liferay-ui:message key='collection-sign' />:</span> <%=collection.getCollectionCode() %></p>
		<p><span><liferay-ui:message key='collection-name' />:</span> <%=collection.getCollectionName(locale) %></p>
		<p><span><liferay-ui:message key='description' />:</span> <%=collection.getDescription() %></p>
		<c:if test="<%=Validator.isNotNull(typesStr) %>">
			<p><span><liferay-ui:message key='collection-types' />:</span> <%=typesStr %></p>
		</c:if>
	</div>
	
	<div>
		<c:if test="<%=permissionChecker.isOmniadmin() || dictPermission.getView() %>">
			<aui:button 
				name="view-items-button"
				type="submit" cssClass="view-button" 
				value="view-list" title="<%=LanguageUtil.get(locale, \"view-list\") %>"
			/>
		</c:if>
			
		<c:if test="<%=(DictCollectionPermission.contains(permissionChecker, scopeGroupId, ActionKeys.ADD_DICTCOLLECTION))
						|| dictPermission.getEdit()%>">
	 		<aui:button 
	 			name="edit-collection-button"
	 			type="submit" cssClass="edit-button" 
	 			value="edit" title="<%=LanguageUtil.get(locale, \"edit\") %>"
	 		/>
	 	</c:if>
		
		<c:if test="<%=(collection != null && DictCollectionPermission.contains(permissionChecker, scopeGroupId, ActionKeys.DELETE) 
						&& showDeleteButton)
						|| dictPermission.getDelete()%>">
			<aui:button 
				name="delete-collection-button"
				type="submit" cssClass="delete-button" 
				value="delete" title="<%=LanguageUtil.get(locale, \"delete\") %>"
			/>
	 	</c:if>
	 </div>
</c:if>

<%!
	private Log _log = LogFactoryUtil.getLog("html.portlets.data_management.admin.display.dictcollection_detail.jsp");
%>