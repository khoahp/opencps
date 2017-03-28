
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>
 */
%>

<%@page import="com.liferay.portal.kernel.util.ArrayUtil"%>
<%@page import="org.opencps.util.PortletPropsValues"%>
<%@page import="org.opencps.statisticsmgt.model.DossiersStatistics"%>
<%@page import="com.liferay.portal.kernel.template.TemplateHandlerRegistryUtil"%>
<%@page import="com.liferay.portal.kernel.util.Constants"%>
<%@page import="com.liferay.portal.kernel.template.TemplateHandler"%>
<%@page import="java.util.ArrayList"%>
<%@ include file="init.jsp" %>

<%
	int[] fieldsIndexes = null;
	if(fieldDatasShemas == null || fieldDatasShemas.isEmpty()){
		fieldDatasShemas = new ArrayList<FieldDatasShema>();
		FieldDatasShema fieldDatasShema = new FieldDatasShema(LanguageUtil.get(locale, "received-number"),"k1","receivedNumber");
		fieldDatasShemas.add(fieldDatasShema);
		fieldsIndexes = new int[]{0};
	}else{
		
		fieldsIndexes = new int[fieldDatasShemas.size()];
		for(int f = 0; f < fieldDatasShemas.size(); f++){
			fieldsIndexes[f] = f;
		}
	}
	
%>

<liferay-portlet:actionURL var="configurationActionURL" portletConfiguration="true"/>

<aui:form action="<%= configurationActionURL %>" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
	
	<liferay-ui:panel-container 
		extended="<%= true %>" 
		id="chartConfigPanelContainer" 
		persistState="<%= true %>"
	>
		<liferay-ui:panel 
			collapsible="<%= true %>" 
			extended="<%= true %>" 
			id="chartConfigPanel" 
			persistState="<%= true %>" 
			title="chart-configuration"
		>
			<aui:fieldset>
				<aui:col width="100">
					<aui:input name="chartTitle" type="text" value="<%=chartTitle %>"/>
				</aui:col>
			</aui:fieldset>
			
			<aui:fieldset>
				<aui:col width="100">
					<aui:input name="xaxisUnit" type="text" value="<%=xaxisUnit %>"/>
				</aui:col>
			</aui:fieldset>
			
			<aui:fieldset>
				<aui:col width="100">
					<aui:input name="yaxisUnit" type="text" value="<%=yaxisUnit %>"/>
				</aui:col>
			</aui:fieldset>
			
			<%-- <aui:fieldset>
				<aui:col width="100">
					<aui:select name="chartType">
						<aui:option value="pie" selected='<%=chartType.equals("pie") %>'><liferay-ui:message key="pie"/></aui:option>
						<aui:option value="bar" selected='<%=chartType.equals("bar") %>'><liferay-ui:message key="bar"/></aui:option>
						<aui:option value="table" selected='<%=chartType.equals("table") %>'><liferay-ui:message key="table"/></aui:option>
					</aui:select>
				</aui:col>
			</aui:fieldset> --%>
		</liferay-ui:panel>
		
		<liferay-ui:panel 
			collapsible="<%= true %>" 
			extended="<%= true %>" 
			id="chartFilterPanel" 
			persistState="<%= true %>" 
			title="chart-filter"
		>
			<%-- <aui:fieldset>
				<aui:col width="100">
					<aui:select name="fields" multiple="<%=true %>" label="statistics-status">
						<aui:option value="remaining-number" selected="<%=ArrayUtil.contains(fields, \"remaining-number\")%>">
							<liferay-ui:message key="remaining-number"/>
						</aui:option>
						<aui:option value="received-number" selected="<%=ArrayUtil.contains(fields, \"received-number\")%>">
							<liferay-ui:message key="received-number"/>
						</aui:option>
						<aui:option value="ontime-number" selected="<%=ArrayUtil.contains(fields, \"ontime-number\")%>">
							<liferay-ui:message key="ontime-number"/>
						</aui:option>
						<aui:option value="overtime-number" selected="<%=ArrayUtil.contains(fields, \"overtime-number\")%>">
							<liferay-ui:message key="overtime-number"/>
						</aui:option>
						<aui:option value="processing-number" selected="<%=ArrayUtil.contains(fields, \"processing-number\")%>">
							<liferay-ui:message key="processing-number"/>
						</aui:option>
						<aui:option value="delaying-number" selected="<%=ArrayUtil.contains(fields, \"delaying-number\")%>">
							<liferay-ui:message key="delaying-number"/>
						</aui:option>
					</aui:select>
				</aui:col>
			</aui:fieldset> --%>
				
				
			<aui:fieldset id="dynamicFormula">
				<div class="formul-note">
					remainingNumber, receivedNumber, receivedNumber, ontimeNumber, overtimeNumber, processingNumber, delayingNumber
				</div>
				<%
					if(fieldsIndexes != null){
						for(int f = 0; f < fieldsIndexes.length; f++){
							int index = fieldsIndexes[f];
							FieldDatasShema fieldDatasShema = fieldDatasShemas.get(f);
							%>
								<div class="lfr-form-row lfr-form-row-inline">
									<div class="row-fields">
										<aui:col width="40">
											<aui:input label="field-label" name='<%= "fieldLabel" + index %>' id='<%= "fieldLabel" + index %>' type="text" value="<%=fieldDatasShema.getFieldLabel() %>"/>
										</aui:col>
										
										<aui:col width="20">
											<aui:input label="field-key" name='<%= "fieldKey" + index %>'  id='<%= "fieldKey" + index %>' type="text" value="<%=fieldDatasShema.getFieldKey() %>"/>							
										</aui:col>
										
										<aui:col width="40">
											<aui:input label="field-formula" name='<%= "fieldFormula" + index %>'  id='<%= "fieldFormula" + index %>' type="text" value="<%=fieldDatasShema.getFieldFomula() %>"/>
										</aui:col>
									</div>
								</div>
							<%
						}
					}
				%>
				
				<aui:input name="fieldsIndexes" type="hidden" value="<%= StringUtil.merge(fieldsIndexes) %>" />
			</aui:fieldset>
			
			<%-- <aui:fieldset>
				<aui:col width="40">
					<aui:input name="fieldTotalLabel" type="text" value="<%=fieldTotalLabel %>"/>
				</aui:col>
				
				<aui:col width="20">
					<aui:input name="fieldTotalKey" type="text" value="<%=fieldTotalKey %>"/>							
				</aui:col>
				
				<aui:col width="40">
					<aui:input name="fieldTotalFormula" type="text" value="<%=fieldTotalFormula %>"/>
				</aui:col>
			</aui:fieldset> --%>
			
			<aui:fieldset>
				<aui:col width="30">
					<aui:select name="startMonth">
						<%
							for(int m = 1; m <=12; m++){
								%>
									<aui:option value="<%=m %>" selected="<%=m == startMonth %>"><liferay-ui:message key="month"/> <%=m %></aui:option>
								<%
							}
						%>
					</aui:select>
				</aui:col>
				
				<aui:col width="30">
					<aui:select name="startYear">
						<%
							for(int y = 2016; y <=2030; y++){
								%>
									<aui:option value="<%=y %>" selected="<%=y == startYear %>"><liferay-ui:message key="year"/> <%=y %></aui:option>
								<%
							}
						%>
					</aui:select>
				</aui:col>
				
				<aui:col width="30">
					<aui:input name="period" value="<%=period %>">
						 <aui:validator name="digits"/>
					</aui:input>
				</aui:col>
			</aui:fieldset>
			
			<%-- <aui:fieldset>
				<aui:col width="50">
					<aui:input name="notNullGov" type="checkbox" value="<%=notNullGov %>"/>
				</aui:col>
				<aui:col width="50">
					<aui:input name="notNullDomain" type="checkbox" value="<%=notNullDomain %>"/>
				</aui:col>
			</aui:fieldset> --%>
		
			<aui:fieldset id="domainFilter" >
				<%-- <aui:col width="50">
					<datamgt:ddr
						cssClass="input100"
						depthLevel="1" 
						dictCollectionCode="<%=PortletPropsValues.DATAMGT_MASTERDATA_SERVICE_DOMAIN %>"
						itemNames="domainCode"
						itemsEmptyOption="true"
						selectedItems="<%=domainCode%>"
						optionValueType="code"
						showLabel="true"
					/>
				</aui:col> --%>
				<aui:col width="50">
					<aui:input name="domainCodes" type="text" value="<%=domainCodes %>"/>
				</aui:col>
				<aui:col width="50">
					<aui:select name="domainDeepLevel" label="domain-deep-level">
						<aui:option value="1" selected="<%=domainDeepLevel == 1 %>">1</aui:option>
						<aui:option value="2" selected="<%=domainDeepLevel == 2 %>">2</aui:option>
						<aui:option value="3" selected="<%=domainDeepLevel == 3 %>">3</aui:option>
						<aui:option value="4" selected="<%=domainDeepLevel == 4 %>">4</aui:option>
						<aui:option value="5" selected="<%=domainDeepLevel == 5 %>">5</aui:option>
					</aui:select>
				</aui:col>
			</aui:fieldset>
			
			<aui:fieldset id="govFilter">
				<%-- <aui:col width="50">
					<datamgt:ddr
						cssClass="input100"
						depthLevel="1" 
						dictCollectionCode="<%=PortletPropsValues.DATAMGT_MASTERDATA_GOVERNMENT_AGENCY %>"
						itemNames="govCode"
						itemsEmptyOption="true"
						selectedItems="<%=govCode%>"
						optionValueType="code"
						showLabel="true"
					/>
				</aui:col> --%>
				<aui:col width="50">
					<aui:input name="govCodes" type="text" value="<%=govCodes %>"/>
				</aui:col>
				<aui:col width="50">
					<aui:select name="level" label="level">
						<aui:option value="-1" selected="<%=level == -1 %>" ></aui:option>
						<aui:option value="0" selected="<%=level == 0 %>"><liferay-ui:message key="tw"/></aui:option>
						<aui:option value="1" selected="<%=level == 1 %>"><liferay-ui:message key="province"/></aui:option>
						<aui:option value="2" selected="<%=level == 2 %>"><liferay-ui:message key="district"/></aui:option>
						<aui:option value="3" selected="<%=level == 3 %>"><liferay-ui:message key="ward"/></aui:option>
					</aui:select>
				</aui:col>
				
			</aui:fieldset>
			
			<aui:fieldset>
				<aui:col width="30">
					<aui:select name="filterKey">
						<aui:option value="gov" selected='<%=filterKey.equals("gov") %>'><liferay-ui:message key="gov-code"/></aui:option>
						<aui:option value="domain" selected='<%=filterKey.equals("domain") %>'><liferay-ui:message key="domain-code"/></aui:option>
					</aui:select>
				</aui:col>
				<aui:col width="30"></aui:col>
				<aui:col width="30"></aui:col>
			</aui:fieldset>
		</liferay-ui:panel>
		
	</liferay-ui:panel-container>
	
	<aui:fieldset>
		<div class="display-template">
		<%
			TemplateHandler templateHandler = TemplateHandlerRegistryUtil.getTemplateHandler(DossiersStatistics.class.getName() + "#DETAIL");
		%>
		<liferay-ui:ddm-template-selector
			classNameId="<%= PortalUtil.getClassNameId(templateHandler.getClassName()) %>"
			displayStyle="<%= displayStyle %>"
			displayStyleGroupId="<%= displayStyleGroupId %>"
			refreshURL="<%= PortalUtil.getCurrentURL(request) %>"
			showEmptyOption="<%=true %>"
		/>
		</div>
	</aui:fieldset>
	<aui:button type="submit" name="submit"></aui:button>
</aui:form>

<aui:script use="liferay-auto-fields">
	var autoFields = new Liferay.AutoFields(
		{
			contentBox: '#<portlet:namespace/>dynamicFormula',
			fieldIndexes: '<portlet:namespace />fieldsIndexes',
			namespace: '<portlet:namespace />'
		}
	).render();
</aui:script>