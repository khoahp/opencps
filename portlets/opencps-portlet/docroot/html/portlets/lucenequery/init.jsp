
<%@page import="org.opencps.lucenequery.service.LuceneMenuGroupLocalServiceUtil"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.opencps.lucenequery.model.LuceneMenuGroup"%>
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


<%@page import="org.opencps.lucenequery.util.LuceneQueryUtil"%>
<%@page import="org.opencps.lucenequery.menu.bean.LuceneMenuSchema"%>
<%@page import="java.util.List"%>

<%@ include file="/init.jsp"%>


<%
	/*
	String[] levels = portletPreferences.getValues("levels",
			new String[] { String.valueOf(0) });
	String[] names = portletPreferences.getValues("names",
			new String[] {});
	String[] patterns = portletPreferences.getValues("patterns",
			new String[] {});
	String[] params = portletPreferences.getValues("params",
			new String[] {});
	String[] paramTypes = portletPreferences.getValues("paramTypes",
			new String[] {});
	
	List<LuceneMenuSchema> luceneMenuSchemas = LuceneQueryUtil
			.getLuceneMenuSchemas(levels, names, patterns, params,
					paramTypes); 
	*/

	String targetPortletName = portletPreferences.getValue(
			"targetPortletName", StringPool.BLANK);

	String layoutUUID = portletPreferences.getValue("layoutUUID",
			StringPool.BLANK);
	
	/* 
	String[] menuGroupIds = portletPreferences.getValues(
			"menuGroupIds", new String[] {String.valueOf(0)});
	 */
	 
	int startLevel = GetterUtil.getInteger(portletPreferences.getValue(
				"startLevel", String.valueOf(0)), 0);
	 
	String[] menuGroupIds = portletPreferences.getValues(
				"menuGroupIds", new String[] {String.valueOf(0)});
	 
	List<LuceneMenuGroup> luceneMenuGroups = new ArrayList<LuceneMenuGroup>();

	if(menuGroupIds != null && menuGroupIds.length > 0){
		for(int m = 0; m < menuGroupIds.length; m++){
			long menuGroupId = GetterUtil.getLong(menuGroupIds[m]);
			if(menuGroupId > 0){
				try{
					LuceneMenuGroup luceneMenuGroup = LuceneMenuGroupLocalServiceUtil.getLuceneMenuGroup(menuGroupId);
					luceneMenuGroups.add(luceneMenuGroup);
				}catch(Exception e){
					continue;
				}
			}
		}
	}
%>
