<%@page import="org.opencps.dossiermgt.service.ServiceOptionLocalServiceUtil"%>
<%@page import="org.opencps.dossiermgt.model.ServiceOption"%>
<%@page import="java.util.List"%>
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

<%@page import="com.liferay.portal.kernel.util.Constants"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@page import="org.opencps.util.WebKeys"%>
<%@page import="org.opencps.servicemgt.service.ServiceInfoLocalServiceUtil"%>
<%@page import="org.opencps.dossiermgt.service.ServiceConfigLocalServiceUtil"%>
<%@page import="org.opencps.dossiermgt.model.ServiceConfig"%>
<%@page import="org.opencps.servicemgt.model.ServiceInfo"%>
<%@page import="org.opencps.dossiermgt.search.DossierDisplayTerms"%>

<%@ include file="../init.jsp"%>

<%
	String govAgencyCode = ParamUtil.getString(request, "govAgencyCode");
	long serviceInfoId = ParamUtil.getLong(request, "serviceInfoId");
	
	List<ServiceOption> serviceOpts = ServiceOptionLocalServiceUtil.searchServiceOption(serviceInfoId, govAgencyCode);
%>

<aui:select name="serviceConfigId" label="dossier-template" cssClass="submit-online input100">
	<%
		for (ServiceOption so : serviceOpts) {
	%>
		<c:if test="<%= Validator.isNotNull(so) %>">
			<aui:option value="<%= so.getServiceConfigId() %>">
				<%= so.getOptionName() %> (<%= so.getOptionCode() %>)
			</aui:option>
		</c:if>
	<%
		}
	%>
</aui:select>

