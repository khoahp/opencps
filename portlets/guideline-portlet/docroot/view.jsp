<%@page import="org.opencps.dossiermgt.service.DossierLocalServiceUtil"%>
<%@page import="org.opencps.dossiermgt.model.Dossier"%>
<%
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
%>

<%@ include file="/init.jsp" %>


<%
	String imgStepOne = "step_1.png";
	String imgStepTwo = "step_2.png";
	String imgStepThree = "step_3.png";
	String imgStepFour = "step_4.png";
	
	HttpServletRequest originalRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(renderRequest));
	
	long dossierId = GetterUtil.getLong(originalRequest.getParameter("_13_WAR_opencpsportlet_dossierId"));
	
	Dossier dossier = null;
	
	
%>

<c:if test='<%= pageArea.contentEquals("login") %>'>
	<c:if test="<%= !themeDisplay.isSignedIn() %>">
		<div class="gl-imgcontent">
			<img alt="" src='<%= renderRequest.getContextPath() + "/imgs/" + imgStepOne %>'>
		</div>
	</c:if>
</c:if>
	
<c:if test='<%= pageArea.contentEquals("dossier") %>'>
	
	<c:if test='<%= themeDisplay.getURLCurrent().toString().contains("/group/guest/nop-ho-so") %>'>
		<img alt="" src='<%= renderRequest.getContextPath() + "/imgs/" + imgStepTwo %>'>
	</c:if>
	<c:if test='<%= !themeDisplay.getURLCurrent().toString().contains("/group/guest/nop-ho-so") %>'>
		<c:if test='<%= dossierId != 0 %>'>
			<% 
				try {
					dossier = DossierLocalServiceUtil.getDossier(dossierId);
				} catch (Exception e) {}
				
			%>
	 		<c:if test='<%= Validator.isNotNull(dossier) && dossier.getDossierStatus().contentEquals("new") %>'>
				<img alt="" src='<%= renderRequest.getContextPath() + "/imgs/" + imgStepThree %>'>
	 		</c:if>
		</c:if>	
	</c:if>
</c:if>


