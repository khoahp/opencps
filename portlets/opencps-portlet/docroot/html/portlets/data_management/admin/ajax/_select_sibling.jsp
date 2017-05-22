
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

<%@page import="com.liferay.portal.kernel.dao.orm.QueryUtil"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.opencps.datamgt.model.DictItem"%>
<%@page import="java.util.List"%>
<%@page import="org.opencps.util.WebKeys"%>
<%@page import="org.opencps.datamgt.util.DataMgtUtil"%>
<%@page import="org.opencps.datamgt.service.DictItemLocalServiceUtil"%>
<%@page import="org.opencps.datamgt.search.DictItemDisplayTerms"%>
<%@page import="com.liferay.portal.kernel.log.LogFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.log.Log"%>

<%@ include file="../../init.jsp"%>

<%
	long dictCollectionId = ParamUtil.getLong(request, DictItemDisplayTerms.DICTCOLLECTION_ID);
	long parentItemId = ParamUtil.getLong(request, DictItemDisplayTerms.PARENTITEM_ID);
	long dictItemId = ParamUtil.getLong(request, DictItemDisplayTerms.DICTITEM_ID);
	
	List<DictItem> items = new ArrayList<DictItem>();
	DictItem dictItem = null;
	try {
		if (dictItemId > 0){
			dictItem = DictItemLocalServiceUtil.getDictItem(dictItemId);
			parentItemId = dictItem.getParentItemId();
		}
		items = DictItemLocalServiceUtil
				.getBy_D_P(dictCollectionId, parentItemId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, 
						DataMgtUtil.getDictItemOrderByComparator(DictItemDisplayTerms.SIBLING, WebKeys.ORDER_BY_ASC));
	} catch (Exception e){
		_log.error(e);
	}
	long maxSibling = items.size() > 0 ? items.size() + 1 : 1;
%>

<aui:select name="<%=DictItemDisplayTerms.SIBLING %>" label="sibling">
	<%
		for(DictItem item : items){
			%>
				<aui:option value="<%=item.getSibling() %>" selected="<%=item.getDictItemId() == dictItemId %>">
					<%=item.getSibling() + StringPool.PERIOD + StringPool.SPACE + item.getItemName(locale) %>
				</aui:option>
			<%
		}
	%>
	<c:if test="<%=dictItemId == 0 %>">
		<aui:option value="<%=maxSibling %>" selected="true">
			<%=maxSibling + StringPool.PERIOD %>
		</aui:option>
	</c:if>
</aui:select>

<%!
	private Log _log = LogFactoryUtil.getLog("html.portlets.data_management.admin.ajax._select_sibling.jsp");
%>

