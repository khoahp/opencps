/*******************************************************************************
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
 *******************************************************************************/

package org.opencps.notificationmgt.permisson;

import org.opencps.notificationmgt.model.NotificationStatusConfig;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.security.permission.PermissionChecker;

/**
 * @author nhanhoang
 */
public class NotificationStatusConfigPermission {

	public static void check(PermissionChecker permissionChecker,
			long threadId, String actionId) throws PortalException,
			SystemException {

		if (!contains(permissionChecker, threadId, actionId)) {
			throw new PrincipalException();
		}
	}

	public static void check(PermissionChecker permissionChecker,
			NotificationStatusConfig notificationStatusConfig, String actionId)
			throws PortalException, SystemException {
		if (!contains(permissionChecker, notificationStatusConfig, actionId)) {
			throw new PrincipalException();
		}
	}

	public static boolean contains(PermissionChecker permissionChecker,
			NotificationStatusConfig notificationStatusConfig, String actionId)
			throws PortalException, SystemException {

		return permissionChecker.hasPermission(notificationStatusConfig.getGroupId(),
				NotificationStatusConfig.class.getName(), notificationStatusConfig.getPrimaryKey(),
				actionId);
	}

	public static boolean contains(PermissionChecker permissionChecker,
			long groupId, String actionId) {
		
		boolean permission = false;

		permission = permissionChecker.hasPermission(groupId,
				NotificationStatusConfig.class.getName(), groupId, actionId);
		
		
		return permission;
	}
}
