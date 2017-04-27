
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
<%@page import="org.opencps.lucenequery.service.LuceneQueryPatternLocalServiceUtil"%>
<%@page import="org.opencps.lucenequery.model.LuceneQueryPattern"%>
<%@page import="javax.portlet.PortletURL"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>

<%@ include file="../init.jsp"%>

<portlet:renderURL var="addPatternURL">
	<portlet:param name="mvcPath" value='<%=templatePath + "edit_lucenepatternquery.jsp" %>'/>
	<portlet:param name="backURL" value='<%=currentURL.toString()%>'/>
</portlet:renderURL>

<aui:button href="<%=addPatternURL.toString() %>" value="add-pattern" name="add-pattern"/>

<%

	List<String> headerNames = new ArrayList<String>();
	
	/* headerNames.add("#");
	headerNames.add("name");
	headerNames.add("pattern");
	headerNames.add("url");
	headerNames.add("action"); */
	
	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter("mvcPath", templatePath + "lucenequerylist.jsp");

%>

<div class="opencps-searchcontainer-wrapper default-box-shadow radius8">

	<liferay-ui:search-container 
		searchContainer="<%= new SearchContainer(renderRequest, null, null, SearchContainer.DEFAULT_CUR_PARAM, SearchContainer.DEFAULT_DELTA, iteratorURL, null, null) %>"
	>
			
		<liferay-ui:search-container-results>
			<%

				total =
					LuceneQueryPatternLocalServiceUtil.countByGroupId(scopeGroupId);
				results =
						LuceneQueryPatternLocalServiceUtil.getLuceneQueryPatternsByGroupId(scopeGroupId, searchContainer.getStart(), 
								searchContainer.getEnd());
				pageContext.setAttribute("results", results);
				pageContext.setAttribute("total", total);
			%>
			
		</liferay-ui:search-container-results>
	
		<liferay-ui:search-container-row 
			className="org.opencps.lucenequery.model.LuceneQueryPattern" 
			modelVar="luceneQueryPattern" 
			keyProperty="patternId"
		>
			<liferay-ui:search-container-column-text
				name="#"
				value="<%= String.valueOf(luceneQueryPattern.getPatternId()) %>"
			/>
			
			<liferay-ui:search-container-column-text
				name="name"
				value="<%= luceneQueryPattern.getName() %>"
			/>
			
			<liferay-ui:search-container-column-text
				name="pattern"
				value="<%= luceneQueryPattern.getPattern()%>"
			/>
			
			<liferay-ui:search-container-column-text
				name="url"
				value="<%= luceneQueryPattern.getUrl()%>"
			/>
			
			<liferay-ui:search-container-column-jsp
				path='<%=templatePath + "lucenequerypattern_actions.jsp" %>'
				name="action"
			/>
		
		</liferay-ui:search-container-row>	
	
		<liferay-ui:search-iterator type="opencs_page_iterator"/>
	
	</liferay-ui:search-container>
</div>