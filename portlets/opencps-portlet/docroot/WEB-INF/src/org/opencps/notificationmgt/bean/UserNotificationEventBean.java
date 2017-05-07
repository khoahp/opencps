/**
 * OpenCPS is the open source Core Public Services software
 * Copyright (C) 2016-present OpenCPS community

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>
 */

package org.opencps.notificationmgt.bean;

import java.sql.Date;

import javax.portlet.RenderRequest;
import javax.portlet.WindowStateException;

import org.opencps.notificationmgt.utils.NotificationUtils;

import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.UserNotificationEvent;
import com.liferay.portal.service.ServiceContext;

/**
 * @author nhanhoang
 */

public class UserNotificationEventBean {

	private static Log _log = LogFactoryUtil
			.getLog(UserNotificationEventBean.class);

	public static UserNotificationEventBean getBean(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext, RenderRequest renderRequest)
			throws JSONException, WindowStateException {

		UserNotificationEventBean userBean = new UserNotificationEventBean();

		try {

			JSONObject jsonObject = JSONFactoryUtil
					.createJSONObject(userNotificationEvent.getPayload());

			userBean.setUserNotificationEventId(userNotificationEvent
					.getUserNotificationEventId());
			userBean.setReceptionNo(jsonObject.getString("receptionNo"));
			userBean.setActionName(jsonObject.getString("actionName"));
			userBean.setNote(jsonObject.getString("note"));

			userBean.setDelivered(userNotificationEvent.getDelivered());
			userBean.setArchived(userNotificationEvent.getArchived());
			userBean.setUrl(NotificationUtils.getLink(userNotificationEvent,
			null,renderRequest));
			

		} catch (JSONException e) {
			_log.error(e);
		}

		return userBean;
	}

	protected long userNotificationEventId;
	protected boolean delivered;
	protected boolean archived;
	protected String receptionNo;
	protected String actionName;
	protected String note;
	protected Date createDate;
	protected String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getUserNotificationEventId() {
		return userNotificationEventId;
	}

	public void setUserNotificationEventId(long userNotificationEventId) {
		this.userNotificationEventId = userNotificationEventId;
	}

	public boolean isDelivered() {
		return delivered;
	}

	public void setDelivered(boolean delivered) {
		this.delivered = delivered;
	}

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	public String getReceptionNo() {
		return receptionNo;
	}

	public void setReceptionNo(String receptionNo) {
		this.receptionNo = receptionNo;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}