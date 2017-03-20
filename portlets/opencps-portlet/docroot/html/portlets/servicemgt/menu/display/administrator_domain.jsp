
<%@page import="com.liferay.portlet.PortletURLFactoryUtil"%>
<%@page import="com.liferay.portal.theme.ThemeDisplay"%>
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
						filter.setParameter(ServiceDisplayTerms.SERVICE_ADMINISTRATION, String.valueOf(di.getDictItemId()));
					
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
				DictCollection serviceDomainCollection = DictCollectionLocalServiceUtil.getDictCollection(scopeGroupId, ServiceUtil.SERVICE_DOMAIN);
				%>
				
				<%= buildTreeServiceDomainToBullet(serviceDomainCollection.getDictCollectionId(), 0, 0, 0, 
						themeDisplay, filter) %>
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

<%!
private String buildTreeServiceDomainToBullet(long dictCollectionId, long seldId,
		long parentId, int indent, ThemeDisplay themeDisplay, PortletURL viewURL) {

	StringBuilder sb = new StringBuilder();

	try {
		List<DictItem> items = DictItemLocalServiceUtil
			.getDictItemsInUseByDictCollectionIdAndParentItemId(dictCollectionId, parentId);

		for (DictItem item : items) {
			long id = item.getDictItemId();

			String itemName = HtmlUtil.escape(item.getItemName(themeDisplay.getLocale()));

			String cssClass = "";

			if (seldId == id) {
				cssClass += " active";
			}
			
			viewURL.setParameter(ServiceDisplayTerms.SERVICE_DOMAINCODE, String.valueOf(id));

			sb.append("<li class=\"" + cssClass + "\">");
			sb.append("<a href=\"" + viewURL.toString() + "\">");
			sb.append(itemName);
			sb.append("</a>");
			
			String sbTmp = buildTreeServiceDomainToBullet(dictCollectionId, seldId, id,
					indent + 1, themeDisplay, viewURL);

			if(Validator.isNotNull(sbTmp)) {
				sb.append("<ul class=\"ul-" + indent + "\">");
				sb.append(sbTmp);
				sb.append("</ul>");
			}
			
			sb.append("</li>");
		}
	} catch (Exception e) {
		_log.error(e);
	}

	return sb.toString();
}

private static Log _log = LogFactoryUtil.getLog("html_portlets_servicemgt_menu_display_administration_domain_jsp");
%>
