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
	long userIdPermission = ParamUtil.getLong(request, "userIdPermission");
	
	List<DictCollection> collections = DictCollectionLocalServiceUtil
			.getDictCollections(scopeGroupId);

	List<DictPermissions> permissions = new ArrayList<DictPermissions>();
	if (userIdPermission > 0){
		try {
			permissions = DictPermissionsLocalServiceUtil.getByUserIdMap(userIdPermission);
		} catch (Exception e){
			_log.error(e);
		}
	}
	
	List<Long> viewPermissions = new ArrayList<Long>();
	List<Long> addPermissions = new ArrayList<Long>();
	List<Long> editPermissions = new ArrayList<Long>();
	List<Long> deletePermissions = new ArrayList<Long>();
	
	for (DictPermissions permission : permissions){
		if (permission.getView()){
			viewPermissions.add((Long) permission.getDictCollectionId());
		}
		if (permission.getAdd()){
			addPermissions.add((Long) permission.getDictCollectionId());
		}
		if (permission.getEdit()){
			editPermissions.add((Long) permission.getDictCollectionId());
		}
		if (permission.getDelete()){
			deletePermissions.add((Long) permission.getDictCollectionId());
		}
	}
	
	boolean viewAll = false;
	boolean addAll = false;
	boolean editAll = false;
	boolean deleteAll = false;
	
	if (viewPermissions.size() == permissions.size() && permissions.size() > 0){
		viewAll = true;
		viewPermissions.clear();
	}
	if (addPermissions.size() == permissions.size() && permissions.size() > 0){
		addAll = true;
		addPermissions.clear();
	}
	if (editPermissions.size() == permissions.size() && permissions.size() > 0){
		editAll = true;
		editPermissions.clear();
	}
	if (deletePermissions.size() == permissions.size() && permissions.size() > 0){
		deleteAll = true;
		deletePermissions.clear();
	}
	
	DictPermissionsPK dictPermissionsPK = new DictPermissionsPK(userIdPermission, -1);
	DictPermissions permiss = null;
	try {
		permiss = DictPermissionsLocalServiceUtil.getDictPermissions(dictPermissionsPK);
	} catch (Exception e){}
	
	boolean addCollectioinPermission = false;
	if (Validator.isNotNull(permiss)){
		addCollectioinPermission = true;
	}
	
%>

<ul class="tree-view-content tree-drag-drop-content tree-file tree-root-container">
	<li class="tree-node collection-tree-node" >
		<aui:input 
			name="add-collections-permission" 
			type="checkbox" 
			label="add-collections-permission" 
			value="1"
			checked="<%=addCollectioinPermission %>"
			cssClass='<%=!addCollectioinPermission ? "unchecked-checkbox" : "" %>'
		/>
	</li>
	<li class="tree-node collection-tree-node" >
		<div class="button-permission-name">
			<div><liferay-ui:message key="view" /></div>
			<div><liferay-ui:message key="add" /></div>
			<div><liferay-ui:message key="edit" /></div>
			<div><liferay-ui:message key="delete" /></div>
		</div>
	</li>
	<li class="tree-node collection-tree-node" >
		<liferay-ui:message key="all" />
		<span class="dict-permission-container">
			<aui:input 
				name="viewPermissionAll"
				label=""
				value="1"
				inlineField="true"
				type="checkbox"
				checked="<%=viewAll %>"
				cssClass='<%=!viewAll ? "unchecked-checkbox" : "" %>'
			/>
			<aui:input 
				name="addPermissionAll"
				label=""
				value="1"
				inlineField="true"
				type="checkbox"
				checked="<%=addAll %>"
				cssClass='<%=!addAll ? "unchecked-checkbox" : "" %>'
			/>
			<aui:input 
				name="editPermissionAll"
				label=""
				value="1"
				inlineField="true"
				type="checkbox"
				checked="<%=editAll %>"
				cssClass='<%=!editAll ? "unchecked-checkbox" : "" %>'
			/>
			<aui:input 
				name="deletePermissionAll"
				label=""
				value="1"
				inlineField="true"
				type="checkbox"
				checked="<%=deleteAll %>"
				cssClass='<%=!deleteAll ? "unchecked-checkbox" : "" %>'
			/>
		</span>
	</li>
	<%
		boolean view = false;
		boolean add = false;
		boolean edit = false;
		boolean delete = false;
		
		for (DictCollection collection : collections){
			view = viewPermissions.contains(collection.getDictCollectionId());
			add = addPermissions.contains(collection.getDictCollectionId());
			edit = editPermissions.contains(collection.getDictCollectionId());
			delete = deletePermissions.contains(collection.getDictCollectionId());
			%>
				<li class="tree-node collection-tree-node-permission" 
					id='<%=renderResponse.getNamespace() + "anchor_collection_" + collection.getDictCollectionId() %>'
				>
					<liferay-ui:message key="<%=collection.getCollectionName(locale) %>" />
					<span class="dict-permission-container">
						<aui:input 
							name="viewPermission"
							label=""
							value="<%=collection.getDictCollectionId() %>"
							inlineField="true"
							type="checkbox"
							checked="<%=view %>"
							cssClass='<%=!view ? "unchecked-checkbox" : "" %>'
						/>
						<aui:input 
							name="addPermission"
							label=""
							value="<%=collection.getDictCollectionId() %>"
							inlineField="true"
							type="checkbox"
							checked="<%=add %>"
							cssClass='<%=!add ? "unchecked-checkbox" : "" %>'
						/>
						<aui:input 
							name="editPermission"
							label=""
							value="<%=collection.getDictCollectionId() %>"
							inlineField="true"
							type="checkbox"
							checked="<%=edit %>"
							cssClass='<%=!edit ? "unchecked-checkbox" : "" %>'
						/>
						<aui:input 
							name="deletePermission"
							label=""
							value="<%=collection.getDictCollectionId() %>"
							inlineField="true"
							type="checkbox"
							checked="<%=delete %>"
							cssClass='<%=!delete ? "unchecked-checkbox" : "" %>'
						/>
					</span>
				</li>
			<%
		}
	%>
</ul>

<%!
	private Log _log = LogFactoryUtil.getLog("html.portlets.data_management.admin._get_dict_permissions.jsp");
%>