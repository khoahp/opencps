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
	<p><label><liferay-ui:message key='dictcollection-statistic' /></label></p>
	<p><liferay-ui:message key='tatal-dictcollections' />: <%=DictCollectionLocalServiceUtil.countAll() %></p>
	<p><liferay-ui:message key='tatal-dictitems' />: <%=DictItemLocalServiceUtil.countAll() %></p>
	<%
		DictCollection col = null;
		try {
			col = DictCollectionLocalServiceUtil.getDictCollections(0, 1, 
					DataMgtUtil.getDictCollectionOrderByComparator(
							DictCollectionDisplayTerms.MODIFIED_DATE, 
							WebKeys.ORDER_BY_DESC)).get(0);
		} catch (Exception e){}
	%>
	<p><liferay-ui:message key="update-date" />
		: <%=col != null ? DateTimeUtil.convertDateToString(col.getModifiedDate(), DateTimeUtil._VN_DATE_TIME_FORMAT) : StringPool.DASH %>
	</p>
</c:if>

<c:if test="<%=collection != null %>">
	<div>
		<p><liferay-ui:message key='dictcollection' /> > <%=collection != null ? collection.getCollectionName() : StringPool.BLANK %></p>
	</div>

	<div>
		<p><liferay-ui:message key='dictcollection-code' />: <%=collection.getCollectionCode() %></p>
		<p><liferay-ui:message key='dictcollection-name' />: <%=collection.getCollectionName(locale) %></p>
		<p><liferay-ui:message key='dictcollection-types' />: <%=typesStr %></p>
	</div>
	
	<div>
		<liferay-ui:icon 
			id='<%=renderResponse.getNamespace() + "view-items-button" %>' 
			image="view" 
		/>
	<!-- 		cssClass="search-container-action fa view" -->
			
		<c:if test="<%=DictCollectionPermission.contains(permissionChecker, scopeGroupId, ActionKeys.ADD_DICTCOLLECTION) %>">
	 		<portlet:renderURL var="updateDictCollectionURL">
				<portlet:param name="mvcPath" value="/html/portlets/data_management/admin/edit_dictcollection.jsp"/>
				<portlet:param name="<%=DictCollectionDisplayTerms.DICTCOLLECTION_ID %>" value="<%=String.valueOf(collection.getDictCollectionId()) %>"/>
				<portlet:param name="backURL" value="<%=currentURL %>"/>
			</portlet:renderURL> 
	 		<liferay-ui:icon 
	 			image="edit" 
	 			message="edit" 
	 			url="<%=updateDictCollectionURL.toString() %>" 
	 		/> 
	<!--  			cssClass="search-container-action fa  edit"  -->
	 	</c:if>
		<%-- <liferay-ui:icon-delete url="" confirmation='<%=LanguageUtil.get(locale, "are-you-sure-delete-entry") %>'/> --%>
		<c:if test="<%=collection != null && DictCollectionPermission.contains(permissionChecker, scopeGroupId, ActionKeys.DELETE) %>">
	 		<portlet:actionURL var="deleteDictCollectionURL" name="deleteDictCollection" >
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
			/>
	<!-- 			cssClass="search-container-action fa delete"  -->
	 	</c:if>
	 </div>
</c:if>

<%!
	private Log _log = LogFactoryUtil.getLog("html.portlets.data_management.admin.display.dictcollection_detail.jsp");
%>