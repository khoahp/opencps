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

package org.opencps.notificationmgt.search;

import java.util.Date;

import javax.portlet.PortletRequest;

import org.opencps.util.DateTimeUtil;

import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.util.ParamUtil;

/**
 * @author nhanhoang
 */
public class UserNotificationEventSearchTerms extends DisplayTerms {
	
	public static final String USER_NOTIFICATION_EVENT_ID = "userNotificationEventId";

	public static final String RECEPTION_NO = "receptionNo";

	public static final String ACTION_NAME = "actionName";

	public static final String NOTE = "note";

	public static final String DELIVERIED = "delivered";

	public static final String ARCHIVED = "archived";

	public static final String CREATE_DATE = "createDate";


	public UserNotificationEventSearchTerms(PortletRequest portletRequest) {
		super(portletRequest);

		userNotificationEventId = ParamUtil.getLong(portletRequest, USER_NOTIFICATION_EVENT_ID);
		receptionNo = ParamUtil.getString(portletRequest,
				RECEPTION_NO);
		actionName = ParamUtil.getString(portletRequest,
				ACTION_NAME);
		note = ParamUtil.getString(portletRequest, NOTE);
		delivered = ParamUtil.getBoolean(portletRequest, DELIVERIED);
		archived = ParamUtil.getBoolean(portletRequest, ARCHIVED);
		createDate = ParamUtil.getDate(portletRequest, CREATE_DATE, DateTimeUtil
						.getDateTimeFormat(DateTimeUtil._VN_DATE_TIME_FORMAT));
	}
	
	protected long userNotificationEventId;
	protected boolean delivered;
	protected boolean archived;
	protected String receptionNo;
	protected String actionName;
	protected String note;
	protected Date createDate;

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
