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

<%@ include file="../init.jsp"%>

<%
	String[] names = new String[] { PortletKeys.PAYMENTCONFIG_TAB };
	String tabValue = ParamUtil.getString(request, "tabs1",
			PortletKeys.PAYMENTCONFIG_TAB);

	List<String> urls = new ArrayList<String>();

	PortletURL tabURL = null;

	tabURL = PortletURLFactoryUtil.create(request, themeDisplay
			.getPortletDisplay().getId(), themeDisplay.getPlid(),
			PortletRequest.RENDER_PHASE);
%>
<liferay-ui:tabs names="<%= StringUtil.merge(names) %>" url="<%=tabURL.toString()%>" tabsValues="<%= StringUtil.merge(names) %>" value="<%=tabValue %>">

	<c:if test='<%= tabValue.equalsIgnoreCase(PortletKeys.PAYMENTCONFIG_TAB)%>' >
		<jsp:include page="/html/portlets/paymentmgt/paymentconfig/paymentconfig_list.jsp" flush="true" />
	</c:if>
</liferay-ui:tabs>	
