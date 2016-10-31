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
package org.opencps.holidayconfig.util;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.opencps.holidayconfig.model.HolidayConfig;
import org.opencps.holidayconfig.model.HolidayConfigExtend;
import org.opencps.holidayconfig.service.HolidayConfigExtendLocalServiceUtil;
import org.opencps.holidayconfig.service.HolidayConfigLocalServiceUtil;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

public class HolidayUtils {

	private static Log _log = LogFactoryUtil.getLog(HolidayUtils.class);

	public final static String SATURDAY = "SATURDAY";
	public final static String SUNDAY = "SUNDAY";
	private final static int ACTIVE = 1;
	
	/**
	 * Check estimateDate
	 * 
	 * @param baseDate
	 * @param pattern
	 * @return Date has been check holiday
	 */
	public static Date getEndDate(Date baseDate, String pattern) {

		/* format pattern = "3 -10:30", pattern = "3 +10:30" */

		Date estimateDate = null;

		try {

			if (baseDate == null) {
				baseDate = new Date();
			}

			Calendar baseDateCal = Calendar.getInstance();
			baseDateCal.setTime(baseDate);

			int bookingDays = 0;
			int bookingHour = 0;
			int bookingMinutes = 0;

			String[] splitPattern = StringUtil.split(pattern, StringPool.SPACE);

			if (splitPattern.length == 2) {

				bookingDays = GetterUtil.getInteger(splitPattern[0], 0);

				String[] splitHour = StringUtil.split(splitPattern[1], StringPool.COLON);

				if (splitHour.length == 2) {
					bookingHour = GetterUtil.getInteger(splitHour[0]);
					bookingMinutes = GetterUtil.getInteger(splitHour[1]);
				}
			}

			int saturdayIsHoliday = 0;
			int sundayIsHoliday = 0;

			/* Kiem tra xem flag sunday,saturday co duoc tinh la ngay nghi khong */
			List<HolidayConfigExtend> holidayConfigExtendList =
				HolidayConfigExtendLocalServiceUtil.getHolidayConfigExtends(
					QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			for (HolidayConfigExtend holidayConfigExtend : holidayConfigExtendList) {

				if (holidayConfigExtend.getKey().equals(SATURDAY)) {
					saturdayIsHoliday = holidayConfigExtend.getStatus();
				}

				if (holidayConfigExtend.getKey().equals(SUNDAY)) {
					sundayIsHoliday = holidayConfigExtend.getStatus();
				}
			}

			for (int i = 0; i < bookingDays; i++) {

				baseDateCal.add(Calendar.DATE, 1);

				baseDateCal =
					checkDay(baseDateCal, baseDate, null, saturdayIsHoliday, sundayIsHoliday);

			}

			baseDateCal.add(Calendar.HOUR, bookingHour);
			baseDateCal.add(Calendar.MINUTE, bookingMinutes);

			estimateDate = baseDateCal.getTime();

		}
		catch (Exception e) {
			_log.error(e);
		}

		return estimateDate;

	}
	
	public static Calendar getEndDate(Date baseDate, long daysDuration) {

		if (baseDate == null) {
			baseDate = new Date();
		}

		Calendar baseDateCal = Calendar.getInstance();
		baseDateCal.setTime(baseDate);

		try {

			int saturdayIsHoliday = 0;
			int sundayIsHoliday = 0;

			/* Kiem tra xem flag sunday,saturday co duoc tinh la ngay nghi khong */
			
			List<HolidayConfigExtend> holidayConfigExtendList = HolidayConfigExtendLocalServiceUtil
					.getHolidayConfigExtends(QueryUtil.ALL_POS,
							QueryUtil.ALL_POS);

			for (HolidayConfigExtend holidayConfigExtend : holidayConfigExtendList) {

				if (holidayConfigExtend.getKey().equals(SATURDAY)) {
					saturdayIsHoliday = holidayConfigExtend.getStatus();
				}

				if (holidayConfigExtend.getKey().equals(SUNDAY)) {
					sundayIsHoliday = holidayConfigExtend.getStatus();
				}
			}

			for (int i = 0; i < daysDuration; i++) {

				baseDateCal.add(Calendar.DATE, 1);

				baseDateCal = checkDay(baseDateCal, baseDate, null,
						saturdayIsHoliday, sundayIsHoliday);

			}
		} catch (Exception e) {
			_log.error(e);
		}

		return baseDateCal;
	}

	private static Calendar checkDay(Calendar baseDateCal, Date baseDate,
			List<HolidayConfig> holidayConfigList, int saturdayIsHoliday,
			int sundayIsHoliday) {

		boolean isHoliday = false;

		try {

			if (Validator.isNull(holidayConfigList)
					|| (holidayConfigList.size() <= 0)) {
				holidayConfigList = HolidayConfigLocalServiceUtil
						.getHolidayConfig(ACTIVE);
			}

			/*
			 * Kiem tra ngay xu ly co trung vao list ngay nghi da config 
			 * hay chua, Neu trung thi + them ngay xu ly
			 */
			isHoliday = isHoliday(baseDateCal, holidayConfigList);

			if (baseDateCal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
					|| baseDateCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
					|| isHoliday) {

				baseDateCal = isHolidayCal(baseDateCal, holidayConfigList);

				/* Neu flag saturday,sunday bat thi tinh la ngay nghi, + them ngay xu ly */

				if (saturdayIsHoliday == ACTIVE) {

					baseDateCal = checkSaturday(baseDateCal);
				}

				if (sundayIsHoliday == ACTIVE) {
					baseDateCal = checkSunday(baseDateCal);
				}

				checkDay(baseDateCal, baseDate, holidayConfigList,
						saturdayIsHoliday, sundayIsHoliday);
			} else {

			}
		} catch (Exception e) {
			_log.error(e);
		}

		return baseDateCal;
	}

	private static Calendar checkSaturday(Calendar baseDateCal) {

		if (baseDateCal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
			baseDateCal.add(Calendar.DATE, 2);
		}
		return baseDateCal;
	}

	private static Calendar checkSunday(Calendar baseDateCal) {

		if (baseDateCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			baseDateCal.add(Calendar.DATE, 1);
		}
		return baseDateCal;
	}

	private static Calendar isHolidayCal(Calendar baseDateCal,
			List<HolidayConfig> holidayConfigList) {

		int baseDate = 0;
		int holidayDate = 0;
		Calendar holidayCal = Calendar.getInstance();

		try {

			if (Validator.isNull(holidayConfigList)
					|| (holidayConfigList.size() <= 0)) {

				holidayConfigList = HolidayConfigLocalServiceUtil
						.getHolidayConfig(ACTIVE);
			}

			for (int i = 0; i < holidayConfigList.size(); i++) {

				holidayCal.setTime(holidayConfigList.get(i).getHoliday());

				baseDate = baseDateCal.get(Calendar.DATE);
				holidayDate = holidayCal.get(Calendar.DATE);

				if (baseDate == holidayDate) {
					baseDateCal.add(Calendar.DATE, 1);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			_log.error(e);
		}
		return baseDateCal;
	}

	private static boolean isHoliday(Calendar baseDateCal,
			List<HolidayConfig> holidayConfigList) {

		int baseDate = 0;
		int holidayDate = 0;
		Calendar holidayCal = Calendar.getInstance();

		try {

			if (Validator.isNull(holidayConfigList)
					|| (holidayConfigList.size() <= 0)) {

				holidayConfigList = HolidayConfigLocalServiceUtil
						.getHolidayConfig(ACTIVE);
			}

			for (int i = 0; i < holidayConfigList.size(); i++) {

				holidayCal.setTime(holidayConfigList.get(i).getHoliday());

				baseDate = baseDateCal.get(Calendar.DATE);
				holidayDate = holidayCal.get(Calendar.DATE);

				if (baseDate == holidayDate) {
					return true;
				}
			}
		} catch (Exception e) {
			_log.error(e);
		}

		return false;

	}
}