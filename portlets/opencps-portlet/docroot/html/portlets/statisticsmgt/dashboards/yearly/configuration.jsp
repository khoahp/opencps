
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

<%@page import="java.util.ArrayList"%>
<%@page import="org.opencps.statisticsmgt.model.DossiersStatistics"%>
<%@page import="com.liferay.portal.kernel.template.TemplateHandlerRegistryUtil"%>
<%@page import="com.liferay.portal.kernel.util.Constants"%>
<%@page import="com.liferay.portal.kernel.template.TemplateHandler"%>
<%@page import="org.opencps.util.PortletPropsValues"%>

<%@ include file="init.jsp" %>

<%
	int[] fieldsIndexes = null;
	if(fieldDatasShemas == null || fieldDatasShemas.isEmpty()){
		fieldDatasShemas = new ArrayList<FieldDatasShema>();
		FieldDatasShema fieldDatasShema1 = new FieldDatasShema(LanguageUtil.get(locale, "remaining-number"),"k1","remainingNumber");
		FieldDatasShema fieldDatasShema2 = new FieldDatasShema(LanguageUtil.get(locale, "received-number"),"k2","receivedNumber");
		FieldDatasShema fieldDatasShema3 = new FieldDatasShema(LanguageUtil.get(locale, "ontime-number"),"k3","ontimeNumber");
		FieldDatasShema fieldDatasShema4 = new FieldDatasShema(LanguageUtil.get(locale, "overtime-number"),"k4","overtimeNumber");
		FieldDatasShema fieldDatasShema5 = new FieldDatasShema(LanguageUtil.get(locale, "processing-number"),"k5","processingNumber");
		FieldDatasShema fieldDatasShema6 = new FieldDatasShema(LanguageUtil.get(locale, "delaying-number"),"k6","delayingNumber");
		
		fieldDatasShemas.add(fieldDatasShema1);
		fieldDatasShemas.add(fieldDatasShema2);
		fieldDatasShemas.add(fieldDatasShema3);
		fieldDatasShemas.add(fieldDatasShema4);
		fieldDatasShemas.add(fieldDatasShema5);
		fieldDatasShemas.add(fieldDatasShema6);
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
				<aui:input name="chartTitle" type="text" value="<%=chartTitle %>"/>
			</aui:fieldset>
			
			<aui:fieldset>
				<aui:input name="xaxisUnit" type="text" value="<%=xaxisUnit %>"/>
			</aui:fieldset>
			
			<aui:fieldset>
				<aui:input name="yaxisUnit" type="text" value="<%=yaxisUnit %>"/>
			</aui:fieldset>
		</liferay-ui:panel>
		
		<liferay-ui:panel 
			collapsible="<%= true %>" 
			extended="<%= true %>" 
			id="chartFilterPanel" 
			persistState="<%= true %>" 
			title="chart-filter"
		>
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
			
			<aui:fieldset id="dynamicFormula">
				<div class="formula-note">
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
				<aui:col width="50">
					<datamgt:ddr
						cssClass="input100"
						depthLevel="1" 
						dictCollectionCode="<%=PortletPropsValues.DATAMGT_MASTERDATA_GOVERNMENT_AGENCY %>"
						itemNames="govCode"
						itemsEmptyOption="true"
						selectedItems="<%=govCode%>"
						optionValueType="code"
					/>
				</aui:col>
				
				<aui:col width="50">
					<datamgt:ddr
						cssClass="input100"
						depthLevel="1" 
						dictCollectionCode="<%=PortletPropsValues.DATAMGT_MASTERDATA_SERVICE_DOMAIN %>"
						itemNames="domainCode"
						itemsEmptyOption="true"
						selectedItems="<%=domainCode%>"
						optionValueType="code"
					/>
				</aui:col>
			</aui:fieldset> --%>
		</liferay-ui:panel>
	</liferay-ui:panel-container>

	<aui:fieldset>
		<div class="display-template">
			<%
				TemplateHandler templateHandler = TemplateHandlerRegistryUtil.getTemplateHandler(DossiersStatistics.class.getName() + "#GENERAL");
			%>
		<liferay-ui:ddm-template-selector
			classNameId="<%= PortalUtil.getClassNameId(templateHandler.getClassName()) %>"
			displayStyle="<%= displayStyle %>"
			displayStyleGroupId="<%= displayStyleGroupId %>"
			refreshURL="<%= PortalUtil.getCurrentURL(request) %>"
			showEmptyOption="<%= true %>"
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