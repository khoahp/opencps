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

package org.opencps.holidayconfig.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;

import org.opencps.datamgt.model.DictItem;
import org.opencps.util.DateTimeUtil;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;

/**
 * @author nhanhoang
 *
 */
public class HolidayConfigSearch extends SearchContainer<DictItem> {
	static List<String> headerNames = new ArrayList<String>();
	static Map<String, String> orderableHeaders = new HashMap<String, String>();
	static {
		headerNames.add("id");
		headerNames.add("holiday-date");
		headerNames.add("description");
		headerNames.add("create-date");
		headerNames.add("modified-date");
		headerNames.add("inuse");
		headerNames.add(StringPool.BLANK);

		orderableHeaders.put("create-date",
				HolidayConfigDisplayTerms.CREATE_DATE);
		orderableHeaders.put("modified-date",
				HolidayConfigDisplayTerms.MODIFIED_DATE);
		orderableHeaders.put("author", HolidayConfigDisplayTerms.USER_ID);
	}

	public static final String EMPTY_RESULTS_MESSAGE = "no-holiday-date-were-found";

	public HolidayConfigSearch(PortletRequest portletRequest, int delta,
			PortletURL iteratorURL) {

		super(portletRequest, new HolidayConfigDisplayTerms(portletRequest),
				new HolidayConfigSearchTerms(portletRequest),
				DEFAULT_CUR_PARAM, delta, iteratorURL, headerNames,
				EMPTY_RESULTS_MESSAGE);

		// PortletConfig portletConfig = (PortletConfig)
		// portletRequest.getAttribute(JavaConstants.JAVAX_PORTLET_CONFIG);

		HolidayConfigDisplayTerms displayTerms = (HolidayConfigDisplayTerms) getDisplayTerms();

		iteratorURL.setParameter(HolidayConfigDisplayTerms.CREATE_DATE,
				DateTimeUtil.convertDateToString(displayTerms.getCreateDate(),
						DateTimeUtil._VN_DATE_TIME_FORMAT));
		iteratorURL.setParameter(HolidayConfigDisplayTerms.MODIFIED_DATE,
				DateTimeUtil.convertDateToString(
						displayTerms.getModifiedDate(),
						DateTimeUtil._VN_DATE_TIME_FORMAT));
		iteratorURL.setParameter(HolidayConfigDisplayTerms.HOLIDAY_DATE,
				DateTimeUtil.convertDateToString(
						displayTerms.getModifiedDate(),
						DateTimeUtil._VN_DATE_FORMAT));
		iteratorURL.setParameter(HolidayConfigDisplayTerms.HOLIDAY_STATUS,
				String.valueOf(displayTerms.getHolidayStatus()));
		iteratorURL.setParameter(HolidayConfigDisplayTerms.USER_ID,
				String.valueOf(displayTerms.getUserId()));

		try {

			// String orderByCol = ParamUtil.getString(portletRequest,
			// "orderByCol");
			// String orderByType = ParamUtil.getString(portletRequest,
			// "orderByType");
			//
			// OrderByComparator orderByComparator = DataMgtUtil
			// .getDictItemOrderByComparator(orderByCol, orderByType);
			//
			// setOrderableHeaders(orderableHeaders);
			// setOrderByCol(orderByCol);
			// setOrderByType(orderByType);
			// setOrderByComparator(orderByComparator);
		} catch (Exception e) {
			_log.error(e);
		}
	}

	public HolidayConfigSearch(PortletRequest portletRequest,
			PortletURL iteratorURL) {

		this(portletRequest, DEFAULT_DELTA, iteratorURL);
	}

	private static Log _log = LogFactoryUtil.getLog(HolidayConfigSearch.class);

}
