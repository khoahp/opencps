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

package org.opencps.datamgt.service.impl;

import java.util.Date;
import java.util.List;

import org.opencps.datamgt.NoSuchDictPermissionsException;
import org.opencps.datamgt.model.DictPermissions;
import org.opencps.datamgt.service.base.DictPermissionsLocalServiceBaseImpl;
import org.opencps.datamgt.service.persistence.DictPermissionsPK;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.service.ServiceContext;

/**
 * The implementation of the dict permissions local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are
 * added, rerun ServiceBuilder to copy their definitions into the
 * {@link org.opencps.datamgt.service.DictPermissionsLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security
 * checks based on the propagated JAAS credentials because this service can only
 * be accessed from within the same VM.
 * </p>
 *
 * @author khoavd
 * @see org.opencps.datamgt.service.base.DictPermissionsLocalServiceBaseImpl
 * @see org.opencps.datamgt.service.DictPermissionsLocalServiceUtil
 */
public class DictPermissionsLocalServiceImpl extends
		DictPermissionsLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 * 
	 * Never reference this interface directly. Always use {@link
	 * org.opencps.datamgt.service.DictPermissionsLocalServiceUtil} to access
	 * the dict permissions local service.
	 */

	public void updateDictPermissions(long userIdMap, long[] dictCollectionId,
			boolean view, boolean add, boolean edit, boolean delete,
			ServiceContext serviceContext) throws SystemException,
			NoSuchDictPermissionsException {

		List<DictPermissions> permissions = dictPermissionsPersistence
				.findByUserIdMap(userIdMap);
		for (DictPermissions permission : permissions) {
			deleteDictPermission(userIdMap, permission.getDictCollectionId());
		}

		for (long l : dictCollectionId) {
			if (l > 0) {
				addDictPermission(userIdMap, l, view, add, edit, delete,
						serviceContext);
			}
		}
	}

	public DictPermissions addDictPermission(long userIdMap,
			long dictCollectionId, boolean view, boolean add, boolean edit,
			boolean delete, ServiceContext serviceContext)
			throws SystemException {

		DictPermissionsPK permissionsPK = new DictPermissionsPK(userIdMap,
				dictCollectionId);
		DictPermissions permissions = dictPermissionsPersistence
				.fetchByPrimaryKey(permissionsPK);

		if (Validator.isNull(permissions)) {
			permissions = dictPermissionsPersistence.create(permissionsPK);

			permissions.setCompanyId(serviceContext.getCompanyId());
			permissions.setGroupId(serviceContext.getScopeGroupId());
			permissions.setUserId(serviceContext.getUserId());
			permissions.setCreateDate(new Date());

			permissions.setView(view);
			permissions.setAdd(add);
			permissions.setEdit(edit);
			permissions.setDelete(delete);

			permissions = dictPermissionsPersistence.update(permissions);
		}

		return permissions;
	}

	public DictPermissions deleteDictPermission(long userIdMap,
			long dictCollectionId) throws SystemException,
			NoSuchDictPermissionsException {

		DictPermissionsPK permissionsPK = new DictPermissionsPK(userIdMap,
				dictCollectionId);
		DictPermissions permissions = dictPermissionsPersistence
				.remove(permissionsPK);

		return permissions;
	}

	public List<DictPermissions> getByUserIdMap(long userIdMap)
			throws SystemException {
		return dictPermissionsPersistence.findByUserIdMap(userIdMap);
	}
}