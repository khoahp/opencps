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

import org.opencps.notificationmgt.NoSuchNotificationRedirectConfigException;
import org.opencps.notificationmgt.model.NotificationRedirectConfig;
import org.opencps.notificationmgt.service.base.NotificationRedirectConfigLocalServiceBaseImpl;

import com.liferay.portal.kernel.exception.SystemException;

/**
 * The implementation of the notification redirect config local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link org.opencps.notificationmgt.service.NotificationRedirectConfigLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author nhanhoang
 * @see org.opencps.notificationmgt.service.base.NotificationRedirectConfigLocalServiceBaseImpl
 * @see org.opencps.notificationmgt.service.NotificationRedirectConfigLocalServiceUtil
 */
public class NotificationRedirectConfigLocalServiceImpl
	extends NotificationRedirectConfigLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this interface directly. Always use {@link org.opencps.notificationmgt.service.NotificationRedirectConfigLocalServiceUtil} to access the notification redirect config local service.
	 */
	
	public List<NotificationRedirectConfig> getNotificationRedirectConfigs(long notiEventConfigId,boolean active) throws SystemException{
		
		return notificationRedirectConfigPersistence.findByN_A(notiEventConfigId,active);
		
	}
	public NotificationRedirectConfig getNotificationRedirectConfig(long notiEventConfigId,boolean active) throws SystemException, NoSuchNotificationRedirectConfigException{
		
		return notificationRedirectConfigPersistence.findByN_A_S(notiEventConfigId, active);
		
	}
}