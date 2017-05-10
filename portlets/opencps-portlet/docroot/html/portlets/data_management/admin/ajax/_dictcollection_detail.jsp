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

<%@page import="java.util.ArrayList"%>
<%@page import="org.opencps.datamgt.service.DictCollectionLinkLocalServiceUtil"%>
<%@page import="org.opencps.datamgt.model.DictCollectionLink"%>
<%@page import="java.util.List"%>
<%@page import="org.opencps.util.ActionKeys"%>
<%@page import="org.opencps.datamgt.permissions.DictCollectionPermission"%>
<%@page import="org.opencps.util.DateTimeUtil"%>
<%@page import="org.opencps.util.WebKeys"%>
<%@page import="org.opencps.datamgt.search.DictCollectionDisplayTerms"%>
<%@page import="org.opencps.datamgt.util.DataMgtUtil"%>
<%@page import="org.opencps.datamgt.service.DictItemLocalServiceUtil"%>
<%@page import="com.liferay.portal.kernel.log.LogFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.log.Log"%>
<%@page import="org.opencps.datamgt.service.DictCollectionLocalServiceUtil"%>
<%@page import="org.opencps.datamgt.model.DictCollection"%>

<%@ include file="../../init.jsp"%>

<%
	long collectionId = ParamUtil.getLong(request, "collectionId");
	DictCollection collection = null;
	List<DictCollectionLink> collectionsLinked = new ArrayList<DictCollectionLink>();
	try {
		collection = DictCollectionLocalServiceUtil.getDictCollection(collectionId);
		collectionsLinked = DictCollectionLinkLocalServiceUtil.getByDictCollectionId(collectionId);
	} catch (Exception e){}
	List<String> collectionTypes = new ArrayList<String>();
	for (DictCollectionLink linked : collectionsLinked){
		collectionTypes.add(linked.getDictCollectionLinkedName());
	}
	String typesStr = StringUtil.merge(collectionTypes, StringPool.COMMA + StringPool.SPACE);
%>

<c:if test="<%=collection == null %>">
	<p class="breadcrumb"><liferay-ui:message key='dictcollection-statistic' /></p>
	<p><span><liferay-ui:message key='tatal-dictcollections' />:</span> <%=DictCollectionLocalServiceUtil.countAll() %></p>
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
		<p class="breadcrumb"><liferay-ui:message key='dictcollection' /> > <%=collection != null ? collection.getCollectionName() : StringPool.BLANK %></p>
	</div>

	<div>
		<p><span><liferay-ui:message key='dictcollection-code' />:</span> <%=collection.getCollectionCode() %></p>
		<p><span><liferay-ui:message key='dictcollection-name' />:</span> <%=collection.getCollectionName(locale) %></p>
		<p><span><liferay-ui:message key='dictcollection-types' />:</span> <%=typesStr %></p>
	</div>
	
	<div>
		<%-- <liferay-ui:icon 
			id='<%=renderResponse.getNamespace() + "view-items-button" %>' 
			image="view" 
		/> --%>
		<aui:button 
			name="view-items-button"
			type="submit" cssClass="view-button" value="view"
		/>
			
		<c:if test="<%=DictCollectionPermission.contains(permissionChecker, scopeGroupId, ActionKeys.ADD_DICTCOLLECTION) %>">
	 		<%-- <liferay-ui:icon 
	 			id='<%=renderResponse.getNamespace() + "edit-items-button" %>' 
	 			image="edit" 
	 		/> --%>
	 		<aui:button 
	 			name="edit-collection-button"
	 			type="submit" cssClass="edit-button" value="edit"
	 		/>
	 	</c:if>
		
		<c:if test="<%=collection != null && DictCollectionPermission.contains(permissionChecker, scopeGroupId, ActionKeys.DELETE) %>">
	 		<%-- <portlet:actionURL var="deleteDictCollectionURL" name="deleteDictCollection" >
				<portlet:param name="<%=DictCollectionDisplayTerms.DICTCOLLECTION_ID %>" 
					value="<%=String.valueOf(collection.getDictCollectionId()) %>"/>
				<portlet:param name="redirectURL" value="<%=currentURL %>"/>
			</portlet:actionURL> 
			<liferay-ui:icon-delete 
				id="delete-button"
				image="delete" 
				confirmation="are-you-sure-delete-entry" 
				message="delete"  
				url="<%=deleteDictCollectionURL.toString() %>" 
			/> --%>
			<aui:button 
				type="submit" cssClass="delete-button" value="delete"
			/>
	 	</c:if>
	 </div>
</c:if>

<%!
	private Log _log = LogFactoryUtil.getLog("html.portlets.data_management.admin.display.dictcollection_detail.jsp");
%>