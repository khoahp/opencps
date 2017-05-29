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
<%@page import="com.liferay.portal.kernel.log.Log"%>
<%@page import="com.liferay.portal.kernel.log.LogFactoryUtil"%>

<%@ include file="../init.jsp"%>

<div class="dossierListStyle dossierListStyle_<%= dossierListDisplayStyle %>">
	<c:if test="<%=!hideToptabs %>">
		<liferay-util:include page='<%=templatePath + "toptabs.jsp" %>' servletContext="<%=application %>" />
	</c:if>
	
	<liferay-util:include page='<%=templatePath + "display/dossierlist/" + dossierListDisplayStyle + ".jsp" %>' servletContext="<%=application %>" />
</div>

<%!
	private Log _log = LogFactoryUtil.getLog("html.portlets.dossiermgt.frontoffice.frontofficedossierlist.jsp");
%>
