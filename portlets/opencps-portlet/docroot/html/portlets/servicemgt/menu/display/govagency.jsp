<%@page import="javax.portlet.PortletRequest"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayPortletMode"%>
<%@page import="com.liferay.portlet.PortletURLFactoryUtil"%>
<%@page import="org.opencps.util.PortletPropsValues"%>
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
<%@ include file="../../init.jsp" %>

<%
	List<DictItem> govAgencies = DictItemLocalServiceUtil.findDictItemsByG_DC_S(scopeGroupId, PortletPropsValues.DATAMGT_MASTERDATA_GOVERNMENT_AGENCY);

	HttpServletRequest originRequest = PortalUtil.getOriginalServletRequest(request);
	String dossierMGTPortletName = StringPool.UNDERLINE + WebKeys.DOSSIER_MGT_PORTLET + StringPool.UNDERLINE;

	String govAgencyCode = GetterUtil.getString(originRequest.getParameter(dossierMGTPortletName + ServiceDisplayTerms.GOVAGENCY_CODE));
	
	String selGovAgencyCode = Validator.isNotNull(govAgencyCode) ? govAgencyCode : preferences.getValue("selGovAgencyCode","");
%>

<div class="govagency-menu side-nav">
	<div id="<portlet:namespace/>govAgency">
		<ul>
			<%
				for (DictItem di : govAgencies) {
					PortletURL filterURL = PortletURLFactoryUtil.create(request, WebKeys.DOSSIER_MGT_PORTLET, plid, PortletRequest.RENDER_PHASE);
					filterURL.setWindowState(LiferayWindowState.NORMAL);
					filterURL.setPortletMode(LiferayPortletMode.VIEW);
					filterURL.setParameter(ServiceDisplayTerms.GOVAGENCY_CODE, di.getItemCode());
					
					String css = "odd";
					
					if(govAgencies.indexOf(di) % 2 == 0){
						css = "even";
					}
					
					if(di.getItemCode().equals(selGovAgencyCode)) {
						css += " active";
					}
			%>
			<li class="<%=css%>">
					<i class="fa fa-chevron-circle-right" aria-hidden="true"></i>
					<a href="<%= filterURL.toString() %>">
						<%= di.getItemName(locale) %> 
					</a>
				</li>
			<%
				}
			%>
		</ul>
	</div>
</div>

<%!
private static Log _log = LogFactoryUtil.getLog("html.portlets.servicemgt.menu.display.govagency_jsp");
%>
