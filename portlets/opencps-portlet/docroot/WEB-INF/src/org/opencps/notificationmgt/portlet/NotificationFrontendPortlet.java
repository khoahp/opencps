package org.opencps.notificationmgt.portlet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.opencps.util.WebKeys;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.service.UserNotificationEventLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.util.bridges.mvc.MVCPortlet;

public class NotificationFrontendPortlet extends MVCPortlet {

	private static Log _log = LogFactoryUtil
			.getLog(NotificationFrontendPortlet.class);

	public void markMessageReaded(ActionRequest actionRequest,
			ActionResponse actionResponse) {

		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest
				.getAttribute(WebKeys.THEME_DISPLAY);

		long[] userNotificationEventIds = ParamUtil.getLongValues(
				actionRequest, "checkboxs", null);

		try {
			for (long userNotificationEventId : userNotificationEventIds) {
				updateArchived(userNotificationEventId);
			}

		} catch (Exception e) {
			_log.error(e);
		}

	}

	protected void updateArchived(long userNotificationEventId)
			throws Exception {

		com.liferay.portal.model.UserNotificationEvent userNotificationEvent = UserNotificationEventLocalServiceUtil
				.getUserNotificationEvent(userNotificationEventId);

		userNotificationEvent.setArchived(true);

		UserNotificationEventLocalServiceUtil
				.updateUserNotificationEvent(userNotificationEvent);
	}

}