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

package org.opencps.holidayconfig.portlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.opencps.holidayconfig.model.HolidayConfigExtend;
import org.opencps.holidayconfig.model.impl.HolidayConfigExtendImpl;
import org.opencps.holidayconfig.search.HolidayConfigDisplayTerms;
import org.opencps.holidayconfig.service.HolidayConfigExtendLocalServiceUtil;
import org.opencps.holidayconfig.service.HolidayConfigLocalServiceUtil;
import org.opencps.holidayconfig.util.HolidayUtils;
import org.opencps.util.DateTimeUtil;
import org.opencps.util.MessageKeys;
import org.opencps.util.WebKeys;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.util.bridges.mvc.MVCPortlet;

public class HolidayConfigPortlet extends MVCPortlet {

	private static Log _log = LogFactoryUtil.getLog(HolidayConfigPortlet.class);

	public void updateHoliday(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException {

		long holidayId = ParamUtil.getLong(actionRequest,
				HolidayConfigDisplayTerms.HOLIDAY_ID, 0L);

		Date holidayDate = ParamUtil.getDate(actionRequest,
				HolidayConfigDisplayTerms.HOLIDAY_DATE,
				DateTimeUtil.getDateTimeFormat(DateTimeUtil._VN_DATE_FORMAT));

		String description = ParamUtil.getString(actionRequest,
				HolidayConfigDisplayTerms.DESCRIPTION);

		int status = ParamUtil.getInteger(actionRequest,
				HolidayConfigDisplayTerms.HOLIDAY_STATUS, 0);

		String redirectURL = ParamUtil.getString(actionRequest,
				WebKeys.REDIRECT_URL);
		String returnURL = ParamUtil.getString(actionRequest,
				WebKeys.RETURN_URL);
		try {

			ServiceContext serviceContext = ServiceContextFactory
					.getInstance(actionRequest);

			if (holidayId == 0) {

				HolidayConfigLocalServiceUtil.addHoliday(holidayDate,
						description, status, serviceContext);

				SessionMessages.add(actionRequest,
						MessageKeys.HOLIDAYCONFIG_ADD_SUCESS);
			} else {
				HolidayConfigLocalServiceUtil.updateHoliday(holidayId,
						holidayDate, description, status);

				SessionMessages.add(actionRequest,
						MessageKeys.HOLIDAYCONFIG_UPDATE_SUCESS);
			}
		} catch (Exception e) {

			SessionErrors.add(actionRequest,
					MessageKeys.HOLIDAYCONFIG_SYSTEM_EXCEPTION_OCCURRED);

			redirectURL = returnURL;

		} finally {
			if (Validator.isNotNull(redirectURL)) {
				actionResponse.sendRedirect(redirectURL);
			}
		}
	}

	public void updateHolidayExtend(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException {

		int saturdayStatus = ParamUtil.getInteger(actionRequest, "_"
				+ HolidayUtils.SATURDAY);
		int sundayStatus = ParamUtil.getInteger(actionRequest, "_"
				+ HolidayUtils.SUNDAY);

		String returnURL = ParamUtil.getString(actionRequest,
				WebKeys.RETURN_URL);

		List<HolidayConfigExtend> holidayExtendList = new ArrayList<HolidayConfigExtend>();
		HolidayConfigExtend holidayExtend = new HolidayConfigExtendImpl();

		try {
			holidayExtendList = HolidayConfigExtendLocalServiceUtil
					.getHolidayConfigExtends(QueryUtil.ALL_POS,
							QueryUtil.ALL_POS);

			for (int i = 0; i < holidayExtendList.size(); i++) {
				holidayExtend = holidayExtendList.get(i);

				if (holidayExtend.getKey().equals(HolidayUtils.SATURDAY)) {
					holidayExtend.setStatus(saturdayStatus);

					HolidayConfigExtendLocalServiceUtil
							.updateHolidayConfigExtend(holidayExtend);
				}

				if (holidayExtend.getKey().equals(HolidayUtils.SUNDAY)) {
					holidayExtend.setStatus(sundayStatus);

					HolidayConfigExtendLocalServiceUtil
							.updateHolidayConfigExtend(holidayExtend);
				}
			}
		} catch (Exception e) {
			SessionErrors.add(actionRequest,
					MessageKeys.HOLIDAYCONFIG_SYSTEM_EXCEPTION_OCCURRED);
		} finally {
			if (Validator.isNotNull(returnURL)) {
				actionResponse.sendRedirect(returnURL);
			}
		}

	}

}