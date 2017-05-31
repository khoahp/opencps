<%@page import="org.opencps.dossiermgt.model.impl.ServiceConfigImpl"%>
<%@page import="org.opencps.dossiermgt.service.ServiceConfigLocalServiceUtil"%>
<%@page import="org.opencps.dossiermgt.model.ServiceConfig"%>
<%@ include file="../init.jsp" %>
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

<%
	long serviceConfigId = ParamUtil.getLong(request, "serviceConfigId",0);
	
	ServiceConfig serviceConfig = new ServiceConfigImpl();
	
	if(serviceConfigId >0){
		
		serviceConfig = ServiceConfigLocalServiceUtil.getServiceConfig(serviceConfigId);
		
	}
%>
<aui:row cssClass="serice-des">
	<liferay-ui:message key="service-description-dvc"/>
</aui:row>
				
<aui:row cssClass="des-detail">		
	<c:choose>
		<c:when test="<%=Validator.isNotNull(serviceConfig)%>">
			<c:choose>
				<c:when test="<%=serviceConfig.getServiceInstruction().equalsIgnoreCase(StringPool.BLANK) %>">
					
				</c:when>
				<c:otherwise>
					<%= serviceConfig.getServiceInstruction() %>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<liferay-ui:message key="no-config"/>
		</c:otherwise>
	</c:choose>	
</aui:row>