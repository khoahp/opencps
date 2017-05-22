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

<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.liferay.portal.kernel.log.Log"%>
<%@page import="com.liferay.portal.kernel.log.LogFactoryUtil"%>
<%@page import="org.opencps.datamgt.service.DictPermissionsLocalServiceUtil"%>
<%@page import="org.opencps.datamgt.model.DictPermissions"%>
<%@page import="org.opencps.datamgt.service.persistence.DictPermissionsPK"%>
<%@page import="org.opencps.datamgt.service.DictCollectionLocalServiceUtil"%>
<%@page import="org.opencps.datamgt.model.DictCollection"%>
<%@page import="org.opencps.datamgt.service.DictCollectionTypeLocalServiceUtil"%>
<%@page import="org.opencps.datamgt.model.DictCollectionType"%>
<%@page import="org.opencps.util.ActionKeys"%>
<%@page import="org.opencps.datamgt.permissions.DictCollectionPermission"%>
<%@page import="org.opencps.util.DateTimeUtil"%>
<%@page import="org.opencps.util.WebKeys"%>
<%@page import="org.opencps.datamgt.search.DictCollectionDisplayTerms"%>
<%@page import="org.opencps.datamgt.util.DataMgtUtil"%>
<%@page import="org.opencps.datamgt.service.DictItemLocalServiceUtil"%>
<%@page import="org.opencps.datamgt.model.DictItem"%>
<%@page import="com.liferay.portal.kernel.dao.search.ResultRow"%>
<%@page import="org.opencps.datamgt.permissions.DictItemPermission"%>
<%@page import="com.liferay.portal.kernel.dao.orm.QueryUtil"%>
<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@page import="com.liferay.portal.kernel.dao.search.SearchEntry"%>
<%@page import="javax.portlet.PortletURL"%>
<%@page import="com.liferay.portal.kernel.dao.search.SearchContainer"%>
<%@page import="org.opencps.datamgt.search.DictItemDisplayTerms"%>
<%@page import="org.opencps.datamgt.search.DictItemSearchTerms"%>
<%@page import="org.opencps.util.MessageKeys"%>
<%@page import="org.opencps.datamgt.service.DictItemTypeLocalServiceUtil"%>
<%@page import="org.opencps.datamgt.model.DictItemType"%>
<%@page import="com.liferay.portal.service.UserLocalServiceUtil"%>
<%@page import="com.liferay.portal.model.User"%>

<%@ include file="/init.jsp" %>

<%
	String viewTemplate = GetterUtil.getString(portletPreferences.getValue("view-template", "dictcollection.jsp"));
	List<DictPermissions> dictPermissions = new ArrayList<DictPermissions>();
	try {
		dictPermissions = DictPermissionsLocalServiceUtil.getByUserIdMap(user.getUserId());
	} catch (Exception e){}
%>

