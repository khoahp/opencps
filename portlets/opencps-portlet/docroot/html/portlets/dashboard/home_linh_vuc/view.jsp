<%@page import="com.liferay.portal.service.LayoutLocalServiceUtil"%>
<%@page import="javax.portlet.PortletRequest"%>
<%@page import="org.opencps.util.WebKeys"%>
<%@page import="com.liferay.portlet.PortletURLFactoryUtil"%>
<%@page import="java.util.Comparator"%>
<%@page import="java.util.Collections"%>
<%@page import="org.opencps.util.PortletPropsValues"%>
<%@page import="org.opencps.util.PortletUtil"%>
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

<%@page import="com.liferay.portal.kernel.dao.search.SearchEntry"%>
<%@page import="com.liferay.portal.kernel.log.Log"%>
<%@page import="com.liferay.portal.kernel.log.LogFactoryUtil"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="javax.portlet.PortletURL"%>
<%@page import="org.opencps.datamgt.model.DictItem"%>
<%@page import="org.opencps.datamgt.service.DictItemLocalServiceUtil"%>
<%@page import="org.opencps.dossiermgt.bean.ServiceBean"%>
<%@page import="org.opencps.dossiermgt.model.ServiceConfig"%>
<%@page import="org.opencps.dossiermgt.search.ServiceSearch"%>
<%@page import="org.opencps.dossiermgt.search.ServiceSearchTerms"%>
<%@page import="org.opencps.dossiermgt.service.ServiceConfigLocalServiceUtil"%>
<%@page import="org.opencps.servicemgt.model.ServiceInfo"%>
<%@page import="org.opencps.servicemgt.service.ServiceInfoLocalServiceUtil"%>
<%@page import="org.opencps.servicemgt.util.ServiceUtil"%>
<%@page import="org.opencps.util.PortletConstants"%>

<%@ include file="../init.jsp"%>

<%
List<DictItem> dictItems = PortletUtil.getDictItemInUseByCode(themeDisplay.getScopeGroupId(), 
		PortletPropsValues.DATAMGT_MASTERDATA_SERVICE_DOMAIN, 
		PortletConstants.TREE_VIEW_DEFAULT_ITEM_CODE);
%>

<aui:row>
	<aui:col width="100" >
	<hr style="opacity: 0.7;">
	<div class="home-search-sologan">
	
		<p style="font-size: 16px;margin-bottom: 0px;padding-left: 2px;"><liferay-ui:message key="slogan-key-home-lv"/></p>
		
		<h2 style="font-size: 26px;padding-bottom: 5px;"></h2>
		
	</div>
		<ul class="sitemap-class opencps-horizontal links-image">
		
			<%
			int i=0;
			for(DictItem dictItem: dictItems){
					
					int layout_cfg = GetterUtil.getInteger(portletPreferences.getValue("img-home-"+i+"_plid", ""));
					String itemCode_cfg = GetterUtil.getString(portletPreferences.getValue("img-home-"+i+"_itemCode", ""));
					
					PortletURL renderUrl = PortletURLFactoryUtil.create(request, WebKeys.SERVICE_MGT_DIRECTORY, layout_cfg, PortletRequest.RENDER_PHASE);
					renderUrl.setParameter("mvcPath", "/html/portlets/servicemgt/directory/serviceinfodirectorylist.jsp");
					renderUrl.setParameter("domainCode", itemCode_cfg);

			%>
			<li onclick="window.location.href='<%=renderUrl.toString() %>'">
				
				<a class='<%="img-home-"+i %>' href="<%=renderUrl.toString() %>">
				
				</a> 
				
			</li>
			
			<%
				i++; }
			%>
		
		</ul>
		
	</aui:col>
</aui:row>
