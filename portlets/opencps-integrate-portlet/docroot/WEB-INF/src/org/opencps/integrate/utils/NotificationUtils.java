package org.opencps.integrate.utils;

import com.liferay.portal.model.UserNotificationEvent;
import com.liferay.portal.service.UserNotificationEventLocalServiceUtil;

public class NotificationUtils {
	
	public static boolean updateArchived(long userNotificationEventId) {

		UserNotificationEvent userNotificationEvent = null;

		try {
			userNotificationEvent = UserNotificationEventLocalServiceUtil
					.getUserNotificationEvent(userNotificationEventId);

			userNotificationEvent.setArchived(true);

			userNotificationEvent = UserNotificationEventLocalServiceUtil
					.updateUserNotificationEvent(userNotificationEvent);
			
			return true;
		} catch (Exception e) {

		}
		return false;
	}
}