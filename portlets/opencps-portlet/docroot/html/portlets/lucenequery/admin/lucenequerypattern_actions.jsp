
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

<%@page import="javax.portlet.PortletURL"%>
<%@page import="com.liferay.portal.kernel.util.WebKeys"%>
<%@page import="org.opencps.lucenequery.model.LuceneQueryPattern"%>
<%@page import="com.liferay.portal.kernel.dao.search.ResultRow"%>

<%@ include file="../init.jsp"%>

<%
	ResultRow row = (ResultRow) request
			.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
	LuceneQueryPattern luceneQueryPattern = (LuceneQueryPattern) row
			.getObject();

	PortletURL updateLuceneQueryPatternURL = renderResponse
			.createRenderURL();

	updateLuceneQueryPatternURL.setParameter("patternId",
			String.valueOf(luceneQueryPattern.getPatternId()));

	updateLuceneQueryPatternURL.setParameter("backURL", currentURL);
	updateLuceneQueryPatternURL.setParameter("mvcPath", templatePath
			+ "edit_lucenepatternquery.jsp");
	
%>

<liferay-ui:icon 
	image="edit" 
	message="edit" 
	cssClass="search-container-action fa edit"
	url="<%=updateLuceneQueryPatternURL.toString()%>" 
/>

<portlet:actionURL var="deleteLuceneQueryPatternURL" name="deleteLuceneQueryPattern">
	<portlet:param name="patternId" value="<%=String.valueOf(luceneQueryPattern.getPatternId()) %>"/>
</portlet:actionURL>
	
<liferay-ui:icon-delete 
	cssClass="search-container-action fa delete" 
	confirmation="do-you-want-to-detete?" image="delete" 
	message="delete"
	url="<%=deleteLuceneQueryPatternURL.toString()%>" 
/>