package org.opencps.notificationmgt.portlet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.opencps.notificationmgt.utils.NotificationUtils;
import org.opencps.util.WebKeys;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
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
		
		long userNotiEventId = ParamUtil.getLong(
				actionRequest, "userNotificationEventId", 0);

		try {
			
			if (userNotificationEventIds.length > 0) {
				for (long userNotificationEventId : userNotificationEventIds) {
					NotificationUtils.updateArchived(userNotificationEventId);
				}
			}

			if (userNotiEventId > 0) {
				NotificationUtils.updateArchived(userNotiEventId);
			}

		} catch (Exception e) {
			_log.error(e);
		}

	}



}