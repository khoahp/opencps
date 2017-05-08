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

package org.opencps.lucenequery.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.opencps.lucenequery.model.LuceneMenuRole;
import org.opencps.lucenequery.service.base.LuceneMenuRoleLocalServiceBaseImpl;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.Role;
import com.liferay.portal.service.RoleLocalServiceUtil;

/**
 * The implementation of the lucene menu role local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are
 * added, rerun ServiceBuilder to copy their definitions into the
 * {@link org.opencps.lucenequery.service.LuceneMenuRoleLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security
 * checks based on the propagated JAAS credentials because this service can only
 * be accessed from within the same VM.
 * </p>
 *
 * @author trungnt
 * @see org.opencps.lucenequery.service.base.LuceneMenuRoleLocalServiceBaseImpl
 * @see org.opencps.lucenequery.service.LuceneMenuRoleLocalServiceUtil
 */
public class LuceneMenuRoleLocalServiceImpl extends
		LuceneMenuRoleLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 * 
	 * Never reference this interface directly. Always use {@link
	 * org.opencps.lucenequery.service.LuceneMenuRoleLocalServiceUtil} to access
	 * the lucene menu role local service.
	 */

	/**
	 * @param menuItemId
	 * @param userId
	 * @return
	 */
	public boolean hasPermission(long menuItemId, long userId) {
		boolean hasPermission = false;
		List<LuceneMenuRole> luceneMenuRoles = new ArrayList<LuceneMenuRole>();
		List<Long> roleIds = new ArrayList<Long>();

		try {
			luceneMenuRoles = luceneMenuRolePersistence
					.findByMenuItemId(menuItemId);
			for (LuceneMenuRole luceneMenuRole : luceneMenuRoles) {
				roleIds.add(luceneMenuRole.getRoleId());
			}
		} catch (Exception e) {
			// Nothing to do
		}
		if (roleIds != null && !roleIds.isEmpty()) {
			List<Role> userRoles = null;
			try {
				// only get regular role
				userRoles = RoleLocalServiceUtil.getUserRoles(userId);
			} catch (Exception e) {
				_log.warn("Canot get roles for user by userId = " + userId
						+ " Cause" + e.getCause());
			}

			if (userRoles != null) {
				for (Role role : userRoles) {
					if (roleIds.contains(role.getRoleId())) {
						hasPermission = true;
						break;
					}
				}
			}
		} else {
			hasPermission = true;
		}

		return hasPermission;
	}

	private Log _log = LogFactoryUtil
			.getLog(LuceneMenuRoleLocalServiceImpl.class.getName());
}