
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

<%@page import="com.liferay.portal.model.Layout"%>
<%@page import="com.liferay.portal.service.LayoutLocalServiceUtil"%>
<%@page import="com.liferay.portal.kernel.util.Constants"%>
<%@page import="org.opencps.lucenequery.service.LuceneQueryPatternLocalServiceUtil"%>
<%@page import="org.opencps.lucenequery.model.LuceneQueryPattern"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.opencps.lucenequery.model.impl.LuceneMenuGroupImpl"%>

<%@ include file="../init.jsp"%>

<style>
	.input100 {
		width: 100% !important;
	}
</style>

<%
	int[] fieldsIndexes = null;

	if(luceneMenuGroups == null || luceneMenuGroups.isEmpty()){
		luceneMenuGroups = new ArrayList<LuceneMenuGroup>();
		LuceneMenuGroup luceneMenuGroup = new LuceneMenuGroupImpl();
		luceneMenuGroups.add(luceneMenuGroup);
		fieldsIndexes = new int[]{0};
	}else{
		fieldsIndexes = new int[luceneMenuGroups.size()];
		for(int f = 0; f < luceneMenuGroups.size(); f++){
			fieldsIndexes[f] = f;
		}
	}
	
	List<Layout> privateLayouts = LayoutLocalServiceUtil.getLayouts(scopeGroupId, true);
%>
<liferay-portlet:actionURL var="configurationActionURL" portletConfiguration="true"/>

<aui:form action="<%=configurationActionURL %>" name="fm" method="post">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
	
	<aui:fieldset>
		<aui:row>
			<aui:col width="30">
				<aui:input 
					name="targetPortletName"
					type="text" 
					label="target-portlet-name" 
					value="<%=targetPortletName %>" 
					cssClass="input100"
				/>
			</aui:col>
			
			<aui:col width="30">
				<aui:select name="layoutUUID" label="link-to-page" cssClass="input100">
					<c:if test="<%=privateLayouts != null %>">
					<%
						for(Layout privateLayout : privateLayouts){
							%>
								<aui:option value="<%=privateLayout.getUuid() %>" selected="<%=privateLayout.getUuid().equals(layoutUUID) %>">
									<%=privateLayout.getName(locale) %>
								</aui:option>
							<%
						}
					%>
					</c:if>
				</aui:select>
			</aui:col>
			
			<aui:col width="30">
				<aui:input 
					name="startLevel"
					type="text" 
					label="start-level" 
					value="<%=startLevel %>" 
					cssClass="input100"
				>
					<aui:validator name="required"/>
					<aui:validator name="number"/>
					<aui:validator name="min">0</aui:validator>
					<aui:validator name="max">10</aui:validator>
				</aui:input>
			</aui:col>
		</aui:row>
	</aui:fieldset>
	
	<aui:fieldset id="dynamicMenu">
		<%	
			List<LuceneMenuGroup> luceneMenuGroupTemps = LuceneMenuGroupLocalServiceUtil.getLuceneMenuGroupsByGroupId(scopeGroupId);
		
			if(fieldsIndexes != null){
				for(int f = 0; f < fieldsIndexes.length; f++){
					int index = fieldsIndexes[f];
					LuceneMenuGroup luceneMenuGroup = luceneMenuGroups.get(f);
					%>
						<div class="lfr-form-row lfr-form-row-inline">
							<div class="row-fields">
								
								<aui:row>
									<aui:col width="100">
										
										<aui:select name='<%="menuGroupId" + index %>' label="lucene-menu-group" cssClass="input100">
											<%
												if(luceneMenuGroupTemps != null){
													for(LuceneMenuGroup luceneMenuGroupTemp : luceneMenuGroupTemps){
														%>
															<aui:option 
																value="<%=luceneMenuGroupTemp.getMenuGroupId() %>" 
																selected="<%=luceneMenuGroupTemp.getMenuGroupId() == luceneMenuGroup.getMenuGroupId() %>">
																<%=luceneMenuGroupTemp.getName() %>
															</aui:option>
														<%
													}
												}
											%>
										</aui:select>
									</aui:col>
								</aui:row>
							</div>
						</div>
					<%
				}
			}
		%>
		<aui:input name="fieldsIndexes" type="hidden" value="<%=StringUtil.merge(fieldsIndexes) %>" />
	</aui:fieldset>
	
	<aui:row>
		<aui:button type="submit" name="save" value="save" />
	</aui:row>
</aui:form>

<aui:script use="liferay-auto-fields">
	var autoFields = new Liferay.AutoFields({
		contentBox : '#<portlet:namespace/>dynamicMenu',
		fieldIndexes : '<portlet:namespace />fieldsIndexes',
		namespace : '<portlet:namespace />'
	}).render();
</aui:script>
	






