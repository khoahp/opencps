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

<p class="breadcrumb bold">
	<a href="#"><liferay-ui:message key='dict-collection-mgt' /></a> >> 
	<liferay-ui:message key='<%= (dictCollection == null) ? "add-dictcollection" : "update-dictcollection" %>' />
	<liferay-ui:message key='<%= (dictCollection == null) ? "" : " >> " + dictCollection.getCollectionName() %>' />
</p>

<div class="opencps-datamgt collection-wrapper opencps-bound-wrapper pd20 default-box-shadow"">
	<div class="edit-form">
		<aui:form action="<%=updateDictCollectionURL.toString() %>" method="post" name="fm">
			
			<aui:model-context bean="<%=dictCollection %>" model="<%=DictCollection.class %>" />
			<aui:input name="<%=DictCollectionDisplayTerms.DICTCOLLECTION_ID %>" type="hidden"/>
			<aui:input name="redirectURL" type="hidden" value="<%=backURL %>"/>
			<aui:input name="returnURL" type="hidden" value="<%=currentURL %>"/>
			
			<aui:fieldset>
				<aui:row>
					<aui:col width="50">
						<aui:row>
							<aui:input name="<%=DictCollectionDisplayTerms.COLLECTION_CODE %>" type="text" cssClass="input100">
								<aui:validator name="required"/>
								<aui:validator name="maxLength">100</aui:validator>
							</aui:input>
						</aui:row>
						<aui:row>
							<aui:input name="<%=DictCollectionDisplayTerms.COLLECTION_NAME %>" cssClass="input100">
								<aui:validator name="required"/>
								<aui:validator name="minLength">3</aui:validator>
								<aui:validator name="maxLength">255</aui:validator>
							</aui:input>
						</aui:row>
						<aui:row>
							<%-- <aui:input name="<%=DictCollectionDisplayTerms.DESCRIPTION %>" type="textarea" cssClass="input100" /> --%>
							<label><liferay-ui:message key="<%=DictCollectionDisplayTerms.DESCRIPTION %>" /></label>
							<textarea 
								rows="14" 
								id="<%=renderResponse.getNamespace() + DictCollectionDisplayTerms.DESCRIPTION %>"
								name="<%=renderResponse.getNamespace() + DictCollectionDisplayTerms.DESCRIPTION %>" 
								class="input100"
							><%=dictCollection != null ? dictCollection.getDescription() : "" %></textarea>
						</aui:row>
					</aui:col>
					
					<aui:col width="50">
						<aui:row>
							<!-- dictCollections linked -->
							<label><liferay-ui:message key="dict-collection-linked" /></label>
							<div class="opencps-searchcontainer-wrapper default-box-shadow radius8 data-manager-action">
								<div class="openCPSTree yui3-widget component tree-view tree-drag-drop">
									<div class="scrollbar-datamgt">
										<ul class="tree-view-content tree-drag-drop-content tree-file tree-root-container">
											<%
												List<DictCollection> dictCollections = DictCollectionLocalServiceUtil.getDictCollections();
												List<DictCollectionType> dictCollectionsTypes = DictCollectionTypeLocalServiceUtil.getByDictCollectionId(collectionId);
												for (DictCollection collection : dictCollections){
													if (collection.getDictCollectionId() != collectionId){
														boolean checked = false;
														for (DictCollectionType type : dictCollectionsTypes){
															if (type.getDictCollectionLinkedId() == collection.getDictCollectionId()){
																checked = true;
																break;
															}
														}
														%>
															<li class="tree-node click-select-dict-collection"
																id='<%=renderResponse.getNamespace() + "collectionId_" + collection.getDictCollectionId() %>'
															>
																<aui:input 
																	name="dictCollectionsLinked" 
																	value="<%=collection.getDictCollectionId() %>"
																	label=""
																	type="checkbox" 
																	inlineField="true"
																	checked="<%=checked %>"
																	cssClass='<%=!checked ? "unchecked-checkbox" : "" %>'
																/>
																<liferay-ui:message key="<%=collection.getCollectionName() %>" />
															</li>
														<%
													}
												}
											%>
										</ul>
									</div>
								</div>
							</div>
						</aui:row>
					</aui:col>
				</aui:row>
			</aui:fieldset>
			<aui:fieldset>
				<aui:button type="submit" name="submit" value="submit"/>
				<aui:button type="submit" name="cancel" value="cancel"/>
			</aui:fieldset>	
		</aui:form>
	</div>
</div>

