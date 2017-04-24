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

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;

import org.opencps.datamgt.model.DictItem;
import org.opencps.util.DateTimeUtil;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * @author nhanhoang
 *
 */
public class UserNotificationEventSearch extends SearchContainer<DictItem> {
	static List<String> headerNames = new ArrayList<String>();
	static {
		headerNames.add("");
		headerNames.add("receptionNo");
		headerNames.add("actionName");
		headerNames.add("note");
		headerNames.add("create-date");
	}

	public static final String EMPTY_RESULTS_MESSAGE = "no-notification-event-config-where-found";

	public UserNotificationEventSearch(PortletRequest portletRequest, int delta,
			PortletURL iteratorURL) {

		super(portletRequest,
				new UserNotificationEventDisplayTerms(portletRequest),
				new UserNotificationEventDisplayTerms(portletRequest),
				DEFAULT_CUR_PARAM, delta, iteratorURL, headerNames,
				EMPTY_RESULTS_MESSAGE);

		UserNotificationEventDisplayTerms displayTerms = (UserNotificationEventDisplayTerms) getDisplayTerms();

		iteratorURL.setParameter(UserNotificationEventDisplayTerms.USER_NOTIFICATION_EVENT_ID,String.valueOf(displayTerms.getUserNotificationEventId()));
		iteratorURL.setParameter(UserNotificationEventDisplayTerms.RECEPTION_NO,displayTerms.getReceptionNo());
		iteratorURL.setParameter(UserNotificationEventDisplayTerms.ACTION_NAME,displayTerms.getActionName());
		iteratorURL.setParameter(UserNotificationEventDisplayTerms.NOTE,displayTerms.getNote());
		iteratorURL.setParameter(UserNotificationEventDisplayTerms.DELIVERIED,String.valueOf(displayTerms.isDelivered()));
		iteratorURL.setParameter(UserNotificationEventDisplayTerms.ARCHIVED,String.valueOf(displayTerms.isArchived()));
		iteratorURL.setParameter(UserNotificationEventDisplayTerms.CREATE_DATE,String.valueOf(displayTerms.getCreateDate()));

		

	}

	private static Log _log = LogFactoryUtil
			.getLog(UserNotificationEventSearch.class);

}
