<%@page import="org.opencps.datamgt.service.DictItemLocalServiceUtil"%>
<%@page import="org.opencps.datamgt.model.DictItem"%>
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

<%@ include file="../../init.jsp"%>
<%@page import="org.opencps.util.WebKeys"%>
<%@page import="org.opencps.dossiermgt.model.ServiceConfig"%>
<%@page import="com.liferay.portal.kernel.dao.orm.QueryUtil"%>
<%@page import="org.opencps.servicemgt.service.ServiceInfoLocalServiceUtil"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="org.opencps.servicemgt.model.ServiceInfo"%>
<%@page import="org.opencps.dossiermgt.search.ServiceConfigDisplayTerms"%>

<%
	ServiceConfig serviceConfig = (ServiceConfig) 
	request.getAttribute(WebKeys.SERVICE_CONFIG_ENTRY);
	String domainCode = ParamUtil.getString(request, "domainCode");
	List<ServiceInfo> serviceInfos = new ArrayList<ServiceInfo>();
	DictItem dictItemDomainTreeIndex = null;
	try {
		dictItemDomainTreeIndex = DictItemLocalServiceUtil.getDictItem(Long.valueOf(domainCode));
		serviceInfos = ServiceInfoLocalServiceUtil.getServiceInFosByG_DI(scopeGroupId, dictItemDomainTreeIndex.getTreeIndex());
	} catch (Exception e) {
		
	}
	
%>

<aui:model-context bean="<%=serviceConfig%>" model="<%=ServiceConfig.class%>" />
<aui:row>
	<aui:col width="100">
		<aui:select name="<%=ServiceConfigDisplayTerms.SERVICE_CONFIG_SERVICEINFOID %>" 
			required="true" showEmptyOption="true" cssClass="input100">
			<%
				for(ServiceInfo serviceInfo : serviceInfos ) {
					%>
						<aui:option value="<%= serviceInfo.getServiceinfoId() %>">
							<%= serviceInfo.getServiceName() %>
						</aui:option>
					<%
				}
			%>
		</aui:select>
	</aui:col>
</aui:row>

