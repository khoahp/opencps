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
<%@page import="org.opencps.datamgt.search.DictItemDisplayTerms"%>
<%@page import="org.opencps.datamgt.search.DictCollectionDisplayTerms"%>
<%@page import="org.opencps.datamgt.model.DictCollection"%>
<%@page import="org.opencps.util.MessageKeys"%>
<%@page import="org.opencps.datamgt.OutOfLengthCollectionNameException"%>
<%@page import="org.opencps.datamgt.OutOfLengthCollectionCodeException"%>
<%@page import="org.opencps.datamgt.DuplicateCollectionException"%>
<%@page import="org.opencps.datamgt.NoSuchDictCollectionException"%>
<%@page import="org.opencps.datamgt.EmptyCollectionCodeException"%>
<%@page import="org.opencps.datamgt.EmptyDictCollectionNameException"%>
<%@page import="org.opencps.util.WebKeys"%>
<%@page import="org.opencps.datamgt.service.DictCollectionLinkLocalServiceUtil"%>
<%@page import="org.opencps.datamgt.model.DictCollectionLink"%>
<%@page import="org.opencps.datamgt.service.DictCollectionLocalServiceUtil"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<%@ include file="../../init.jsp"%>

<portlet:actionURL var="updateDictCollectionURL" name="updateDictCollection" />

<%
	DictCollection dictCollection = (DictCollection)request.getAttribute(WebKeys.DICT_COLLECTION_ENTRY);
	long collectionId = 0;
	if (dictCollection == null){
		collectionId = ParamUtil.getLong(request, DictItemDisplayTerms.DICTCOLLECTION_ID);
		if (collectionId > 0){
			try {
				dictCollection = DictCollectionLocalServiceUtil.getDictCollection(collectionId);
			} catch (Exception e) {}
		}
	} else {
		collectionId = dictCollection.getDictCollectionId();
	}
	String backURL = ParamUtil.getString(request, "backURL");
%>

<liferay-ui:header
	backURL="<%= backURL %>"
	title='<%= (dictCollection == null) ? "add-dictcollection" : "update-dictcollection" %>'
/>

<div class="opencps-datamgt collection-wrapper opencps-bound-wrapper pd20 default-box-shadow"">
	<div class="edit-form">
		<liferay-ui:error exception="<%= OutOfLengthCollectionCodeException.class %>" message="<%=OutOfLengthCollectionCodeException.class.getName() %>" />
		<liferay-ui:error exception="<%= OutOfLengthCollectionNameException.class %>" message="<%=OutOfLengthCollectionNameException.class.getName() %>" />
		<liferay-ui:error exception="<%= DuplicateCollectionException.class %>" message="<%=DuplicateCollectionException.class.getName() %>" />
		<liferay-ui:error exception="<%= NoSuchDictCollectionException.class %>" message="<%=NoSuchDictCollectionException.class.getName() %>" />
		<liferay-ui:error exception="<%= EmptyCollectionCodeException.class %>" message="<%=EmptyCollectionCodeException.class.getName() %>" />
		<liferay-ui:error exception="<%= EmptyDictCollectionNameException.class %>" message="<%=EmptyDictCollectionNameException.class.getName() %>" />
		<liferay-ui:error key="<%= MessageKeys.DATAMGT_SYSTEM_EXCEPTION_OCCURRED%>" message="<%=MessageKeys.DATAMGT_SYSTEM_EXCEPTION_OCCURRED %>" />

		<aui:form action="<%=updateDictCollectionURL.toString() %>" method="post" name="fm">
			
			<aui:model-context bean="<%=dictCollection %>" model="<%=DictCollection.class %>" />
			<aui:input name="<%=DictCollectionDisplayTerms.DICTCOLLECTION_ID %>" type="hidden"/>
			<aui:input name="redirectURL" type="hidden" value="<%=backURL %>"/>
			<aui:input name="returnURL" type="hidden" value="<%=currentURL %>"/>
			
			<aui:fieldset>
				<aui:row>
					<aui:col width="70">
						<aui:input name="<%=DictCollectionDisplayTerms.COLLECTION_NAME %>" cssClass="input100">
							<aui:validator name="required"/>
							<aui:validator name="minLength">3</aui:validator>
							<aui:validator name="maxLength">255</aui:validator>
						</aui:input>
					</aui:col>
					
					<aui:col width="30">
						<aui:input name="<%=DictCollectionDisplayTerms.COLLECTION_CODE %>" type="text" cssClass="input100">
							<aui:validator name="required"/>
							<aui:validator name="maxLength">100</aui:validator>
						</aui:input>
					</aui:col>
				</aui:row>
				
				<aui:input name="<%=DictCollectionDisplayTerms.DESCRIPTION %>" type="textarea" cssClass="input100"/>
				
				<!-- dictCollections linked -->
				<label><liferay-ui:message key="dict-collection-linked" /></label>
				<div style="overflow-y:scroll;height:250px;width:100%;overflow-x:hidden">
					<ul>
						<%
							List<DictCollection> dictCollections = DictCollectionLocalServiceUtil.getDictCollections();
							List<DictCollectionLink> dictCollectionsLinked = DictCollectionLinkLocalServiceUtil.getByDictCollectionId(collectionId);
							for (DictCollection collection : dictCollections){
								if (collection.getDictCollectionId() != collectionId){
									boolean checked = false;
									for (DictCollectionLink linked : dictCollectionsLinked){
										if (linked.getDictCollectionLinkedId() == collection.getDictCollectionId()){
											checked = true;
											break;
										}
									}
									%>
										<li>
											<aui:input 
												name="dictCollectionsLinked" 
												value="<%=collection.getDictCollectionId() %>"
												label="<%=collection.getCollectionName() %>"
												type="checkbox" 
												inlineField="true"
												checked="<%=checked %>"
												cssClass='<%=!checked ? "no-linked-to-selected-collection" : "" %>'
											/>
										</li>
									<%
								}
							}
						%>
					</ul>
				</div>

			</aui:fieldset>
			<aui:fieldset>
				<aui:button type="submit" name="submit" value="submit"/>
				<aui:button type="submit" name="cancel" value="cancel"/>
			</aui:fieldset>	
		</aui:form>
	</div>
</div>

