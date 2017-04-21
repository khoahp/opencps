<%@page import="org.opencps.postal.utils.PostalKeys"%>
<%@page import="org.opencps.postal.search.PostOfficeMappingDisplayTerms"%>
<%@page import="org.opencps.postal.model.impl.PostOfficeMappingImpl"%>
<%@page import="org.opencps.postal.service.PostOfficeMappingLocalServiceUtil"%>
<%@page import="org.opencps.postal.model.PostOfficeMapping"%>
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


<%@page import="org.opencps.datamgt.model.DictItem"%>
<%@page import="org.opencps.util.DictItemUtil"%>
<%@page import="org.opencps.util.PortletPropsValues"%>

<%@ include file="../init.jsp"%>



<%
	String backURL = ParamUtil.getString(request, "backURL");
	long postOfficeMappingId = ParamUtil.getLong(request, PostOfficeMappingDisplayTerms.POSTOFFICEMAPPING_ID, 0L);
	
	PostOfficeMapping postOfficeMapping = new PostOfficeMappingImpl();
	
	String selectItems = StringPool.BLANK;
	
	if(postOfficeMappingId > 0){
		try {
			postOfficeMapping = PostOfficeMappingLocalServiceUtil.fetchPostOfficeMapping(postOfficeMappingId);
		}
		catch (Exception e) {
			
		}
	}
	
	if(Validator.isNotNull(postOfficeMapping)){
		selectItems = postOfficeMapping.getOpencpsCityCode();
	}
	List<PostOfficeMapping> postOfficeMappings = PostOfficeMappingLocalServiceUtil.getPostOfficeMappings(QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	
	DictItemUtil dicItemUtil = new DictItemUtil();
%>	

<div class="opencps-bound-wrapper pd20 default-box-shadow">

	<c:choose>
		<c:when test="<%= true %>">
			

			<portlet:actionURL var="updatePostOfficeMappingURL"
				name="updatePostOfficeMapping">
				<portlet:param name="returnURL" value="<%=currentURL%>" />
				<portlet:param name="backURL" value="<%=backURL%>" />
			</portlet:actionURL>

			<liferay-ui:success key="update-post-office-mapping-success"
				message="update-post-office-mapping-success" />

			<liferay-ui:error key="update-post-office-mapping-error"
				message="update-post-office-mapping-error" />

			<aui:form action="<%=updatePostOfficeMappingURL.toString()%>"
				method="post" name="fm" id="fm">
				
				<aui:input type="hidden"
					id="<%=PostalKeys.AJAX_REQUEST_NAME%>"
					name="<%=PostalKeys.AJAX_REQUEST_NAME%>"
					value="<%=PostalKeys.REQUEST_POSTOFFICEMAPPING%>" />
					
				<aui:input type="hidden"
					id="<%=PostOfficeMappingDisplayTerms.POSTOFFICEMAPPING_ID%>"
					name="<%=PostOfficeMappingDisplayTerms.POSTOFFICEMAPPING_ID%>"
					value="<%=String.valueOf(postOfficeMappingId)%>" />

				<aui:row>
					<aui:col width="50">
						<aui:select
							id="<%=PostOfficeMappingDisplayTerms.POSTOFFICE_CODE%>"
							onChange="loadPostOfficeMapping()"
							name="<%=PostOfficeMappingDisplayTerms.POSTOFFICE_CODE%>"
							label="postoffice-code">
							<%
							boolean selected = false;
							
							for (PostOfficeMapping postMapping1 : postOfficeMappings) {
								
								if(postMapping1.getPostOfficeCode().equals(postOfficeMapping.getPostOfficeCode())){
									
									selected = true;
								}else{
									selected = false;
								}

								
							%>
								<aui:option selected="<%=selected %>"
 									value="<%=postMapping1.getPostOfficeCode()%>"><%=postMapping1.getPostOfficeName()%></aui:option>
								
							<%
									
							}
							%>
						</aui:select>
					</aui:col>
					
					
				</aui:row>
				

				<aui:row>
					<aui:col width="30">
						<aui:input 
							id="<%=PostOfficeMappingDisplayTerms.POSTOFFICE_CODE_TXT%>"
							cssClass="input100"
							name="<%=PostOfficeMappingDisplayTerms.POSTOFFICE_CODE_TXT%>" type="text"
							label="post-office-code"
							value="<%=postOfficeMapping.getPostOfficeCode() %>"
							>
							<aui:validator name="required"></aui:validator>
						</aui:input>
					</aui:col>
					
					<aui:col width="30">
						<aui:input 
							id="<%=PostOfficeMappingDisplayTerms.POSTOFFICE_NAME_TXT%>"
							cssClass="input100"
							name="<%=PostOfficeMappingDisplayTerms.POSTOFFICE_NAME_TXT%>" type="text"
							label="post-office-name"
							value="<%=postOfficeMapping.getPostOfficeName() %>"
							>
							<aui:validator name="required"></aui:validator>
						</aui:input>
					</aui:col>
					
					<aui:col width="30">
						<datamgt:ddr 
 						depthLevel="1"  
 						dictCollectionCode='<%=PortletPropsValues.DATAMGT_MASTERDATA_ADMINISTRATIVE_REGION %>'
 						itemNames="<%=PostOfficeMappingDisplayTerms.OPENCPS_CITY_CODE%>"
 						itemsEmptyOption="true"
 						selectedItems="<%=selectItems.toString() %>"
 						emptyOptionLabels="cityId"
 						showLabel="<%=true%>"
 						cssClass="input100"
						/>
 					</aui:col>

				</aui:row>
				
				<aui:row>
					<aui:col>
						<aui:button name="submit" type="submit" value="submit" />
					</aui:col>
				</aui:row>

			</aui:form>
			
			<portlet:resourceURL var="resourceURL"/>
			<script type="text/javascript">
				loadPostOfficeMapping();
				function loadPostOfficeMapping() {
					console.log("loadPostOfficeMapping()");
					AUI().use('aui-io-request', function(A){
						
						var postOfficeCode = A.one('#<portlet:namespace /><%= PostOfficeMappingDisplayTerms.POSTOFFICE_CODE %>').val();
						var ajaxRequestName = A.one('#<portlet:namespace /><%= PostalKeys.AJAX_REQUEST_NAME %>').val();
					
						A.io.request('<%=resourceURL.toString()%>',{
							
							method: 'post',
							data: {
								<portlet:namespace /><%= PostOfficeMappingDisplayTerms.POSTOFFICE_CODE %>: postOfficeCode,
								<portlet:namespace /><%= PostalKeys.AJAX_REQUEST_NAME %>: ajaxRequestName
							},
							dataType:'json',
							on:{
								success:function (){
									
									var postOfficeMapping = this.get('responseData');
									
									A.Array.each(postOfficeMapping,function(obj,idx){
										
										if(obj){
											
											if (obj.<%= PostOfficeMappingDisplayTerms.POSTOFFICE_CODE_TXT %>){
												A.one('#<portlet:namespace /><%= PostOfficeMappingDisplayTerms.POSTOFFICE_CODE_TXT %>').set('value', obj.<%= PostOfficeMappingDisplayTerms.POSTOFFICE_CODE_TXT %>);
											}else{
												A.one('#<portlet:namespace /><%= PostOfficeMappingDisplayTerms.POSTOFFICE_CODE_TXT %>').set('value', '');
											}
											
											if (obj.<%= PostOfficeMappingDisplayTerms.POSTOFFICE_NAME_TXT %>){
												A.one('#<portlet:namespace /><%= PostOfficeMappingDisplayTerms.POSTOFFICE_NAME_TXT %>').set('value', obj.<%= PostOfficeMappingDisplayTerms.POSTOFFICE_NAME_TXT %>);
											}else{
												A.one('#<portlet:namespace /><%= PostOfficeMappingDisplayTerms.POSTOFFICE_NAME_TXT %>').set('value', '');
											}
											
											if (obj.<%= PostOfficeMappingDisplayTerms.POSTOFFICEMAPPING_ID %>){
												A.one('#<portlet:namespace /><%= PostOfficeMappingDisplayTerms.POSTOFFICEMAPPING_ID %>').set('value', obj.<%= PostOfficeMappingDisplayTerms.POSTOFFICEMAPPING_ID %>);
											}else{
												A.one('#<portlet:namespace /><%= PostOfficeMappingDisplayTerms.POSTOFFICEMAPPING_ID %>').set('value', '');
											}
											
											
											if (obj.<%= PostOfficeMappingDisplayTerms.OPENCPS_CITY_CODE %>){
												A.one('#<portlet:namespace /><%= PostOfficeMappingDisplayTerms.OPENCPS_CITY_CODE %>').set('value', obj.<%= PostOfficeMappingDisplayTerms.OPENCPS_CITY_CODE %>);
												A.one('#<portlet:namespace /><%= PostOfficeMappingDisplayTerms.OPENCPS_CITY_CODE %>').set('selected', 'true');
											}else{
												A.one('#<portlet:namespace /><%= PostOfficeMappingDisplayTerms.OPENCPS_CITY_CODE %>').set('value', '');
											}

										
										}
									});
								}
							}
						});
					});
				} 
			</script>

		
		</c:when>
		
		<c:otherwise>
			<liferay-ui:message key="do-not-have-permission"></liferay-ui:message>
		</c:otherwise>
	
	</c:choose>
</div>