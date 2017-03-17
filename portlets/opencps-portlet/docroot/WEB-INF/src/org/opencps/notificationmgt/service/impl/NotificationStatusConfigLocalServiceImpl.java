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

package org.opencps.notificationmgt.service.impl;

import java.util.List;

import org.opencps.notificationmgt.NoSuchNotificationStatusConfigException;
import org.opencps.notificationmgt.model.NotificationStatusConfig;
import org.opencps.notificationmgt.service.base.NotificationStatusConfigLocalServiceBaseImpl;

import com.liferay.portal.kernel.exception.SystemException;

/**
 * The implementation of the notification status config local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link org.opencps.notificationmgt.service.NotificationStatusConfigLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author nhanhoang
 * @see org.opencps.notificationmgt.service.base.NotificationStatusConfigLocalServiceBaseImpl
 * @see org.opencps.notificationmgt.service.NotificationStatusConfigLocalServiceUtil
 */
public class NotificationStatusConfigLocalServiceImpl
	extends NotificationStatusConfigLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this interface directly. Always use {@link org.opencps.notificationmgt.service.NotificationStatusConfigLocalServiceUtil} to access the notification status config local service.
	 */
	
	public NotificationStatusConfig findByCNS(String dossierCurrentStatus,
			String dossierNextStatus, boolean isSendNotification)
			throws SystemException, NoSuchNotificationStatusConfigException {

		return notificationStatusConfigPersistence.findByC_N_S(dossierCurrentStatus,
				dossierNextStatus, isSendNotification);

	}

	public List<NotificationStatusConfig> findByDossierCurrentStatus(
			String dossierCurrentStatus, boolean isSendNotification)
			throws SystemException {

		return notificationStatusConfigPersistence.findByC_S(dossierCurrentStatus,
				isSendNotification);

	}

	public List<NotificationStatusConfig> findByDossierNextStatus(
			String dossierNextStatus, boolean isSendNotification)
			throws SystemException {

		return notificationStatusConfigPersistence.findByC_S(dossierNextStatus,
				isSendNotification);

	}
	
	public NotificationStatusConfig getByDossierNextStatus(
			String dossierNextStatus, boolean isSendNotification)
			throws SystemException {

		try {
			return notificationStatusConfigPersistence.findByDossierNextStatus(
					dossierNextStatus, isSendNotification);
		} catch (NoSuchNotificationStatusConfigException e) {

		}

		return null;

	}
	
	public NotificationStatusConfig getByDossierNextStatus(
			String dossierNextStatus) throws SystemException {

		try {
			return notificationStatusConfigPersistence
					.findByDS_(dossierNextStatus);
		} catch (NoSuchNotificationStatusConfigException e) {

		}

		return null;

	}
}