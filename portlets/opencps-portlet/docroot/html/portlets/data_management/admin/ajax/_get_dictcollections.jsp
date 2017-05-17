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

<%@ include file="../../init.jsp"%>

<%
	String collectionName = ParamUtil.getString(request, "collectionName");
	collectionName = StringPool.PERCENT + collectionName + StringPool.PERCENT;
	String[] collectionNames = new String[]{collectionName};
	List<DictCollection> collections = new ArrayList<DictCollection>();
	try {
		collections = DictCollectionLocalServiceUtil
				.getDictCollections(scopeGroupId, collectionNames, 
						QueryUtil.ALL_POS, QueryUtil.ALL_POS, 
						DataMgtUtil.getDictCollectionOrderByComparator(
							DictCollectionDisplayTerms.COLLECTION_NAME, 
							WebKeys.ORDER_BY_ASC));
	} catch (Exception e){
		_log.error(e);
	}
%>

<ul class="tree-view-content tree-drag-drop-content tree-file tree-root-container">
	<%
		for (DictCollection collection : collections){
			%>
				<li class="tree-node collection-tree-node" 
					id='<%=renderResponse.getNamespace() +  "collectionId_" + collection.getDictCollectionId() %>'
				>
					<%=collection.getCollectionName(locale) %>
				</li>
			<%
		}
	%>
</ul>

<%!
	private Log _log = LogFactoryUtil.getLog("html.portlets.data_management.admin._get_dictcollection.jsp");
%>