
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
<%@page import="com.liferay.portal.kernel.util.WebKeys"%>
<%@page import="org.opencps.lucenequery.model.LuceneQueryPattern"%>
<%@page import="org.opencps.lucenequery.service.LuceneQueryPatternLocalServiceUtil"%>

<%@ include file="../init.jsp"%>

<%
	LuceneQueryPattern luceneQueryPattern = null;

	long patternId = ParamUtil.getLong(request, "patternId");
	
	String backURL = ParamUtil.getString(request, "backURL");
	if(patternId > 0){
		try{
			luceneQueryPattern = LuceneQueryPatternLocalServiceUtil.getLuceneQueryPattern(patternId);
		}catch(Exception e){
			
		}
	}
%>

<portlet:actionURL var="editPatternURL" name="editPattern"/>

<liferay-ui:header 
	backURL="<%= backURL %>"
	title='<%=luceneQueryPattern != null ? "update-pattern" : "add-pattern" %>' 
/>

<aui:form name="fm" method="post" action="<%=editPatternURL.toString() %>">

	<aui:model-context bean="<%= luceneQueryPattern %>" model="<%= LuceneQueryPattern.class %>" />
	<aui:input name="patternId" type="hidden"/>
	<aui:input name="name"/>
	<aui:input name="pattern" type="textarea"/>
	<aui:input name="url"/>
	
	<aui:button type="submit" name="save" value="save"/>
</aui:form>