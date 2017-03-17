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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.opencps.holidayconfig.model.HolidayConfig;
import org.opencps.holidayconfig.model.HolidayConfigExtend;
import org.opencps.holidayconfig.service.HolidayConfigExtendLocalServiceUtil;
import org.opencps.holidayconfig.service.HolidayConfigLocalServiceUtil;
import org.opencps.util.DateTimeUtil;
import org.opencps.util.DateTimeUtil.DateTimeBean;

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
	private int dayGoing = 0;
	private int minutesGoing = 0;
	private Calendar baseCalendar = Calendar.getInstance();
	private List<HolidayConfig> holidayConfigList1 = null;

	public static Calendar getEndDate(Date baseDate, String pattern) {

		/* format pattern = "3 10:30" */

		if (baseDate == null) {
			baseDate = new Date();
		}

		Calendar baseDateCal = Calendar.getInstance();
		baseDateCal.setTime(baseDate);

		try {

			int saturdayIsHoliday = 0;
			int sundayIsHoliday = 0;

			DateTimeUtil dateTimeUtil = new DateTimeUtil();
			DateTimeBean dateTimeBean = dateTimeUtil
					.getDateTimeFromPattern(pattern);

			/* Kiem tra xem flag sunday,saturday co duoc tinh la ngay nghi khong */

			List<HolidayConfigExtend> holidayConfigExtendList = new ArrayList<HolidayConfigExtend>();
			holidayConfigExtendList = HolidayConfigExtendLocalServiceUtil
					.getHolidayConfigExtends(QueryUtil.ALL_POS,
							QueryUtil.ALL_POS);

			if (holidayConfigExtendList.size() > 0) {

				for (HolidayConfigExtend holidayConfigExtend : holidayConfigExtendList) {

					if (holidayConfigExtend.getKey().equals(SATURDAY)) {
						saturdayIsHoliday = holidayConfigExtend.getStatus();
					}

					if (holidayConfigExtend.getKey().equals(SUNDAY)) {
						sundayIsHoliday = holidayConfigExtend.getStatus();
					}
				}
			}

			for (int i = 0; i < dateTimeBean.getDays(); i++) {

				baseDateCal.add(Calendar.DATE, 1);

				baseDateCal = checkDay(baseDateCal, baseDate, null,
						saturdayIsHoliday, sundayIsHoliday);

			}
			baseDateCal.add(Calendar.HOUR, dateTimeBean.getHours());
			baseDateCal.add(Calendar.MINUTE, dateTimeBean.getMinutes());
		} catch (Exception e) {
			_log.error(e);
		}

		return baseDateCal;
	}

	private static Calendar checkDay(
		Calendar baseDateCal, Date baseDate, List<HolidayConfig> holidayConfigList,
		int saturdayIsHoliday, int sundayIsHoliday) {

		boolean isHoliday = false;

		try {

			if (Validator.isNull(holidayConfigList) || (holidayConfigList.size() <= 0)) {
				holidayConfigList = HolidayConfigLocalServiceUtil.getHolidayConfig(ACTIVE);
			}

			/*
			 * Kiem tra ngay xu ly co trung vao list ngay nghi da config hay
			 * chua, Neu trung thi + them ngay xu ly
			 */
			isHoliday = isHoliday(baseDateCal, holidayConfigList);

			if (baseDateCal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
				baseDateCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || isHoliday) {

				baseDateCal = isHolidayCal(baseDateCal, holidayConfigList);

				/*
				 * Neu flag saturday,sunday bat thi tinh la ngay nghi, + them
				 * ngay xu ly
				 */

				if (saturdayIsHoliday == ACTIVE) {

					baseDateCal = checkSaturday(baseDateCal);
				}

				if (sundayIsHoliday == ACTIVE) {
					baseDateCal = checkSunday(baseDateCal);
				}

				checkDay(
					baseDateCal, baseDate, holidayConfigList, saturdayIsHoliday, sundayIsHoliday);
			}
			else {

			}
		}
		catch (Exception e) {
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

	private static Calendar isHolidayCal(Calendar baseDateCal, List<HolidayConfig> holidayConfigList) {

		int baseDay = 0;
		int baseMonth = 0;
		int baseYear = 0;

		int holidayDay = 0;
		int holidayMonth = 0;
		int holidayYear = 0;

		Calendar holidayCal = Calendar.getInstance();

		try {

			if (Validator.isNull(holidayConfigList) || (holidayConfigList.size() <= 0)) {

				holidayConfigList = HolidayConfigLocalServiceUtil.getHolidayConfig(ACTIVE);
			}

			for (int i = 0; i < holidayConfigList.size(); i++) {

				holidayCal.setTime(holidayConfigList.get(i).getHoliday());

				baseDay = baseDateCal.get(Calendar.DATE);
				holidayDay = holidayCal.get(Calendar.DATE);

				baseMonth = baseDateCal.get(Calendar.MONTH);
				holidayMonth = holidayCal.get(Calendar.MONTH);

				baseYear = baseDateCal.get(Calendar.YEAR);
				holidayYear = holidayCal.get(Calendar.YEAR);

				if (baseDay == holidayDay && baseMonth == holidayMonth && baseYear == holidayYear) {
					baseDateCal.add(Calendar.DATE, 1);
				}
			}
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			_log.error(e);
		}
		return baseDateCal;
	}

	private static boolean isHoliday(Calendar baseDateCal, List<HolidayConfig> holidayConfigList) {

		int baseDay = 0;
		int baseMonth = 0;
		int baseYear = 0;

		int holidayDay = 0;
		int holidayMonth = 0;
		int holidayYear = 0;

		Calendar holidayCal = Calendar.getInstance();

		try {

			if (Validator.isNull(holidayConfigList) || (holidayConfigList.size() <= 0)) {

				holidayConfigList = HolidayConfigLocalServiceUtil.getHolidayConfig(ACTIVE);
			}

			for (int i = 0; i < holidayConfigList.size(); i++) {

				holidayCal.setTime(holidayConfigList.get(i).getHoliday());

				baseDay = baseDateCal.get(Calendar.DATE);
				holidayDay = holidayCal.get(Calendar.DATE);

				baseMonth = baseDateCal.get(Calendar.MONTH);
				holidayMonth = holidayCal.get(Calendar.MONTH);

				baseYear = baseDateCal.get(Calendar.YEAR);
				holidayYear = holidayCal.get(Calendar.YEAR);

				if (baseDay == holidayDay && baseMonth == holidayMonth && baseYear == holidayYear) {
					return true;
				}
			}
		}
		catch (Exception e) {
			_log.error(e);
		}

		return false;

	}

	/**
	 * @param startDate
	 * @param endDate
	 * @return minutesGoing
	 */
	public int getDurationMinutes(Date startDate, Date endDate) {

		if (Validator.isNull(startDate)) {
			startDate = new Date();
		}

		baseCalendar.setTime(startDate);
		baseCalendar.set(Calendar.HOUR, 0);
		baseCalendar.set(Calendar.MINUTE, 0);
		baseCalendar.set(Calendar.SECOND, 0);

		Calendar endDateCal = Calendar.getInstance();
		endDateCal.setTime(endDate);
		endDateCal.set(Calendar.HOUR, 0);
		endDateCal.set(Calendar.MINUTE, 0);
		endDateCal.set(Calendar.SECOND, 0);

		Calendar startDateCal1 = Calendar.getInstance();
		startDateCal1.setTime(startDate);

		Calendar endDateCal1 = Calendar.getInstance();
		endDateCal1.setTime(endDate);

		long timeInMillis = endDateCal.getTimeInMillis() - baseCalendar.getTimeInMillis();
		long timeInMillis1 = endDateCal1.getTimeInMillis() - startDateCal1.getTimeInMillis();

		long diffMinutes = DateTimeUtil.convertTimemilisecondsToMinutes(timeInMillis1);
		int diffDays = DateTimeUtil.convertTimemilisecondsToDays(timeInMillis);

		minutesGoing = (int) diffMinutes;
		dayGoing = diffDays;

		try {

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

			for (int i = 0; i < diffDays; i++) {

				baseCalendar.add(Calendar.DATE, 1);

				checkDay1(saturdayIsHoliday, sundayIsHoliday);

			}
		}
		catch (Exception e) {
			_log.error(e);
		}

		int minutesReturn = minutesGoing;
		int daysReturn = dayGoing;

		return minutesReturn;
	}

	private void checkDay1(int saturdayIsHoliday, int sundayIsHoliday) {

		boolean isHoliday = false;

		try {

			if (Validator.isNull(holidayConfigList1) || (holidayConfigList1.size() <= 0)) {
				holidayConfigList1 = HolidayConfigLocalServiceUtil.getHolidayConfig(ACTIVE);
			}

			isHoliday = isHoliday(baseCalendar, holidayConfigList1);

			if (baseCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
				baseCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || isHoliday) {

				if (isHoliday) {
					isHolidayCal1();
				}

				if (saturdayIsHoliday == ACTIVE) {

					checkSaturday1();
				}

				if (sundayIsHoliday == ACTIVE) {
					checkSunday1();
				}

			}
			else {

			}
		}
		catch (Exception e) {
			_log.error(e);
		}
	}

	private void isHolidayCal1() {

		int baseDay = 0;
		int baseMonth = 0;
		int baseYear = 0;

		int holidayDay = 0;
		int holidayMonth = 0;
		int holidayYear = 0;

		Calendar holidayCal = Calendar.getInstance();

		try {

			if (Validator.isNull(holidayConfigList1) || (holidayConfigList1.size() <= 0)) {

				holidayConfigList1 = HolidayConfigLocalServiceUtil.getHolidayConfig(ACTIVE);
			}

			for (int i = 0; i < holidayConfigList1.size(); i++) {

				holidayCal.setTime(holidayConfigList1.get(i).getHoliday());

				baseDay = baseCalendar.get(Calendar.DATE);
				holidayDay = holidayCal.get(Calendar.DATE);

				baseMonth = baseCalendar.get(Calendar.MONTH);
				holidayMonth = holidayCal.get(Calendar.MONTH);

				baseYear = baseCalendar.get(Calendar.YEAR);
				holidayYear = holidayCal.get(Calendar.YEAR);

				if (baseDay == holidayDay && baseMonth == holidayMonth && baseYear == holidayYear) {
					--dayGoing;
					minutesGoing = minutesGoing - 1440;
				}
			}
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			_log.error(e);
		}

	}

	private Calendar checkSunday1() {

		if (baseCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {

			--dayGoing;
			minutesGoing = minutesGoing - 1440;
		}
		return baseCalendar;
	}

	private Calendar checkSaturday1() {

		if (baseCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
			--dayGoing;
			minutesGoing = minutesGoing - 1440;

		}
		return baseCalendar;
	}

}
