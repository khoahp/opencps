
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
<%@page import="org.opencps.servicemgt.service.ServiceInfoLocalServiceUtil"%>
<%@ include file="../../init.jsp" %>

<%
	List<DictItem> serviceAdministrations = DictItemLocalServiceUtil.findDictItemsByG_DC_S(scopeGroupId, ServiceUtil.SERVICE_ADMINISTRATION);
	List<DictItem> serviceDomains = DictItemLocalServiceUtil.findDictItemsByG_DC_S(scopeGroupId, ServiceUtil.SERVICE_DOMAIN);
%>

<liferay-portlet:renderURL varImpl="filter" portletName="<%= ServiceUtil.SERVICE_PUBLIC_PORTLET_NAME %>">
	
</liferay-portlet:renderURL>

<div class="service-menu side-nav">
	<ul class="nav nav-tabs">
		<li class="active"><a data-toggle="tab" href="#<portlet:namespace/>cqql"><liferay-ui:message key="co-quan-quan-ly" /></a></li>
		<li><a data-toggle="tab" href="#<portlet:namespace/>linhvuc"><liferay-ui:message key="linh-vuc" /></a></li>
		<li><a data-toggle="tab" href="#<portlet:namespace/>mucdo"><liferay-ui:message key="muc-do" /></a></li>
	</ul>
	<div class="tab-content">
		<div id="<portlet:namespace/>cqql" class="tab-pane fade in active">
			<ul>
				<%
					for (DictItem di : serviceAdministrations) {
						filter.setParameter(ServiceDisplayTerms.SERVICE_ADMINISTRATION, Long.toString(di.getDictItemId()));
					
						String css = "odd";
						
						if(serviceAdministrations.indexOf(di) % 2 == 0){
							css = "even";
						}
				%>
				<li class="<%=css%>">
						<i class="fa fa-chevron-circle-right" aria-hidden="true"></i>
						<a href="<%= filter.toString() %>">
							<%= di.getItemName(locale) %> 
						</a>
					</li>
				<%
					}
				%>
			</ul>
		</div>
		<div id="<portlet:namespace/>linhvuc" class="tab-pane fade">
			<ul>
				<%
					for (DictItem di : serviceDomains) {
						filter.setParameter(ServiceDisplayTerms.SERVICE_DOMAINCODE, Long.toString(di.getDictItemId()));
					
						String css = "odd";
						
						if(serviceAdministrations.indexOf(di) % 2 == 0){
							css = "even";
						}
				%>
				<li class="<%=css%>">
						<i class="fa fa-chevron-circle-right" aria-hidden="true"></i>
						<a href="<%= filter.toString() %>">
							<%= di.getItemName(locale) %> 
						</a>
					</li>
				<%
					}
				%>
			</ul>
		</div>
		<div id="<portlet:namespace/>mucdo" class="tab-pane fade">
			<ul>
				<%
					for (int i = 2; i < 5; i++) {
						filter.setParameter("mucDo", String.valueOf(i));
					
						String css = "odd";
						
						if(i % 2 == 0){
							css = "even";
						}
				%>
				<li class="<%=css%>">
						<i class="fa fa-chevron-circle-right" aria-hidden="true"></i>
						<a href="<%= filter.toString() %>">
							<liferay-ui:message key="muc-do" /> <%= i %>
						</a>
					</li>
				<%
					}
				%>
			</ul>
		</div>
	</div>
</div>
