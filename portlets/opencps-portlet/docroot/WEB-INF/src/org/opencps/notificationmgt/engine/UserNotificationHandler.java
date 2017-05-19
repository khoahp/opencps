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

package org.opencps.notificationmgt.engine;

import java.util.Locale;

import org.opencps.notificationmgt.utils.NotificationUtils;
import org.opencps.util.PortletPropsValues;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.notifications.BaseUserNotificationHandler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.model.UserNotificationEvent;
import com.liferay.portal.service.ServiceContext;
import com.liferay.util.ContentUtil;

/**
 * @author nhanhoang
 */
public class UserNotificationHandler extends BaseUserNotificationHandler {

	private static Log _log = LogFactoryUtil
			.getLog(UserNotificationHandler.class);

	public static final String PORTLET_ID = "113_WAR_opencpsnotificationportlet";

	public UserNotificationHandler() {

		setPortletId(UserNotificationHandler.PORTLET_ID);

	}

	@Override
	protected String getBody(UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext) throws Exception {

		JSONObject jsonObject = JSONFactoryUtil
				.createJSONObject(userNotificationEvent.getPayload());

		long dossierId = 0;
		String receptionNo = StringPool.BLANK;

		String dossierNo = StringPool.BLANK;
		String actionName = StringPool.BLANK;
		String note = StringPool.BLANK;

		String title_profile = StringPool.BLANK;
		String title_action = StringPool.BLANK;
		String title_note = StringPool.BLANK;

		Locale locale = serviceContext.getLocale();

		try {

			dossierId = jsonObject.getLong("dossierId");
			actionName = jsonObject.getString("actionName");
			receptionNo = jsonObject.getString("receptionNo");

			if (receptionNo.length() > 0) {
				dossierNo = receptionNo;
			} else {
				dossierNo = String.valueOf(dossierId);
			}

			title_profile = LanguageUtil.get(locale, "profile");
			title_action = LanguageUtil.get(locale, "actions");
			title_note = LanguageUtil.get(locale, "notes");

		} catch (Exception e) {
			_log.error(e);
		}

		String content = StringUtil.replace(
				ContentUtil.get(PortletPropsValues.USER_NOTIFICATION_CONTENT),
				new String[] { "[$PROFILE$]", "[$DOSSIER_NO$]", "[$ACTIONS$]",
						"[$ACTION_NAME$]", "[$NOTES$]", "[$NOTE$]", },
				new String[] { title_profile, dossierNo, title_action,
						actionName, title_note, note });

		return content;
	}

	@Override
	protected String getLink(UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext) throws Exception {

		String viewUrl = StringPool.BLANK;

		viewUrl = NotificationUtils.getLink(userNotificationEvent,
				serviceContext, null);

		return viewUrl;
	}
}
