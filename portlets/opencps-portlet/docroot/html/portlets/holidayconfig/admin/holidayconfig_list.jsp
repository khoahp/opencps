
<%@page
	import="org.opencps.holidayconfig.model.impl.HolidayConfigExtendImpl"%>
<%@page
	import="org.opencps.holidayconfig.service.HolidayConfigExtendLocalServiceUtil"%>
<%@page import="org.opencps.holidayconfig.model.HolidayConfigExtend"%>
<%@page
	import="org.opencps.holidayconfig.search.HolidayConfigSearchTerms"%>
<%@page
	import="org.opencps.holidayconfig.permission.HolidayConfigPermission"%>
<%@page
	import="org.opencps.holidayconfig.search.HolidayConfigDisplayTerms"%>
<%@page import="org.opencps.util.DateTimeUtil"%>
<%@page import="org.opencps.holidayconfig.search.HolidayConfigSearch"%>
<%@page import="com.liferay.portal.kernel.dao.orm.QueryUtil"%>
<%@page
	import="org.opencps.holidayconfig.service.HolidayConfigLocalServiceUtil"%>
<%@page import="org.opencps.holidayconfig.model.HolidayConfig"%>
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

<%@ include file="../init.jsp"%>

<liferay-util:include page='<%=templatePath + "toolbar.jsp"%>'
	servletContext="<%=application%>" />

<%
	PortletURL iteratorURL = renderResponse.createRenderURL();
	iteratorURL.setParameter("mvcPath", templatePath
	+ "holidayconfig_list.jsp");

	boolean isPermission = HolidayConfigPermission.contains(
	themeDisplay.getPermissionChecker(),
	themeDisplay.getScopeGroupId(), ActionKeys.ADD_HOLIDAY);
	
	
	List<HolidayConfigExtend> holidayExtendList = new ArrayList<HolidayConfigExtend>();
	HolidayConfigExtend holidayExtend = new HolidayConfigExtendImpl();
	
	holidayExtendList = HolidayConfigExtendLocalServiceUtil
			.getHolidayConfigExtends(QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);
%>
<div
	class="opencps-searchcontainer-wrapper-width-header default-box-shadow radius8">

	<portlet:actionURL var="updateHolidayConfigURL"
		name="updateHolidayExtend" />

	<aui:form action="<%=updateHolidayConfigURL.toString()%>" method="post"
		name="fm">

		<aui:input name="<%=WebKeys.RETURN_URL%>" type="hidden"
			value="<%=currentURL%>" />

		<aui:fieldset>
			<%
				for (int i = 0; i < holidayExtendList.size(); i++) {
					holidayExtend = holidayExtendList.get(i);
			%>
			<input type="checkbox"
				name="<portlet:namespace/>_<%=holidayExtend.getKey() %>"
				value="<%=holidayExtend.getStatus()%>"
				<%=holidayExtend.getStatus() == WebKeys.ACTIVE ? "checked": ""%>>
			<%=LanguageUtil.get(pageContext,holidayExtend.getKey())%>
			</input>
			<%
				}
			%>
		</aui:fieldset>
		<aui:fieldset>
			<aui:button type="submit" name="submit" value="submit" />
		</aui:fieldset>
	</aui:form>

	<liferay-ui:search-container
		searchContainer="<%=new HolidayConfigSearch(renderRequest,
						SearchContainer.DEFAULT_DELTA, iteratorURL)%>">

		<liferay-ui:search-container-results>
			<%
				HolidayConfigSearchTerms searchTerms = (HolidayConfigSearchTerms) searchContainer
								.getSearchTerms();

						List<HolidayConfig> holidayConfigList = HolidayConfigLocalServiceUtil
								.getHolidayConfigs(searchContainer.getStart(),
										searchContainer.getEnd());

						int totalSize = HolidayConfigLocalServiceUtil
								.getHolidayConfigs(QueryUtil.ALL_POS,
										QueryUtil.ALL_POS).size();

						pageContext.setAttribute("results", holidayConfigList);
						pageContext.setAttribute("total", totalSize);
			%>

		</liferay-ui:search-container-results>
		<liferay-ui:search-container-row
			className="org.opencps.holidayconfig.model.HolidayConfig"
			modelVar="holidayconfig" keyProperty="holidayId">
			<%
				PortletURL editURL = renderResponse.createRenderURL();
						editURL.setParameter("mvcPath",
								"/html/portlets/holidayconfig/admin/holidayconfig_edit.jsp");
						editURL.setParameter(HolidayConfigDisplayTerms.HOLIDAY_ID,
								String.valueOf(holidayconfig.getHolidayId()));
						editURL.setParameter(WebKeys.BACK_URL, currentURL);

						row.setClassName("opencps-searchcontainer-row");

						row.addText(String.valueOf(holidayconfig.getHolidayId()),
								editURL);
						row.addText(DateTimeUtil.convertDateToString(
								holidayconfig.getHoliday(),
								DateTimeUtil._VN_DATE_FORMAT), editURL);
						row.addText(holidayconfig.getDescription(),
								editURL);
						row.addText(DateTimeUtil.convertDateToString(
								holidayconfig.getCreatedDate(),
								DateTimeUtil._VN_DATE_TIME_FORMAT), editURL);
						row.addText(DateTimeUtil.convertDateToString(
								holidayconfig.getModifiedDate(),
								DateTimeUtil._VN_DATE_TIME_FORMAT), editURL);
						row.addText(
								String.valueOf(holidayconfig.getStatus() == WebKeys.ACTIVE ? LanguageUtil
										.get(pageContext, "active") : LanguageUtil
										.get(pageContext, "inactive")), editURL);

						if (isPermission) {
							row.addJSP(
									"center",
									SearchEntry.DEFAULT_VALIGN,
									"/html/portlets/holidayconfig/admin/holidayconfig_actions.jsp",
									config.getServletContext(), request, response);
						}
			%>

		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator type="opencs_page_iterator" />
	</liferay-ui:search-container>
</div>

<%!private static Log _log = LogFactoryUtil
			.getLog("html.portlets.holidayconfig.admin.holidayconfig_list.jsp");%>