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

package org.opencps.integrate.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portlet.documentlibrary.model.DLFolder;

/**
 * @author trungnt
 *
 */
public class PortletUtil {

	public static final int DOSSIER_FILE_MARK_UNKNOW = 0;
	public static final int DOSSIER_FILE_MARK_ORIGINAL = 1;
	public static final int DOSSIER_FILE_MARK_NOTARIZED = 2;
	public static final int DOSSIER_FILE_MARK_SCAN = 3;
	
	
	public static final int DOSSIER_FILE_SYNC_STATUS_NOSYNC = 0;
	public static final int DOSSIER_FILE_SYNC_STATUS_REQUIREDSYNC = 1;
	public static final int DOSSIER_FILE_SYNC_STATUS_SYNCSUCCESS = 2;
	public static final int DOSSIER_FILE_SYNC_STATUS_SYNCERROR = 3;


	public static String getDossierDestinationFolder(long groupId, int year,
			int month, int day, String oid) {

		return String.valueOf(groupId) + StringPool.SLASH + "Dossiers"
				+ StringPool.SLASH + String.valueOf(year) + StringPool.SLASH
				+ String.valueOf(month) + StringPool.SLASH
				+ String.valueOf(day) + StringPool.SLASH + oid;
	}

	public static DLFolder getDossierFolder(long groupId, Date date,
			String oid, ServiceContext serviceContext) {

		DLFolder dlFolder = null;

		String destination = StringPool.BLANK;
		SplitDate splitDate = splitDate(new Date());

		destination = PortletUtil.getDossierDestinationFolder(groupId,
				splitDate.getYear(), splitDate.getMonth(),
				splitDate.getDayOfMoth(), oid);

		if (Validator.isNotNull(destination)) {
			dlFolder = DLFolderUtil.getTargetFolder(serviceContext.getUserId(),
					serviceContext.getScopeGroupId(),
					serviceContext.getScopeGroupId(), false, 0, destination,
					StringPool.BLANK, false, serviceContext);
		}

		return dlFolder;
	}

	public static class SplitDate {

		public SplitDate(Date date) {

			if (date != null) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);

				_miniSecond = calendar.get(Calendar.MILLISECOND);
				_second = calendar.get(Calendar.SECOND);
				_minute = calendar.get(Calendar.MINUTE);
				_hour = calendar.get(Calendar.HOUR);
				_dayOfMoth = calendar.get(Calendar.DAY_OF_MONTH);
				_dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
				_weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
				_weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
				_month = calendar.get(Calendar.MONTH);
				_year = calendar.get(Calendar.YEAR);
			}
		}

		public int getMiniSecond() {

			return _miniSecond;
		}

		public void setMiniSecond(int _miniSecond) {

			this._miniSecond = _miniSecond;
		}

		public int getSecond() {

			return _second;
		}

		public void setSecond(int _second) {

			this._second = _second;
		}

		public int getMinute() {

			return _minute;
		}

		public void setMinute(int _minute) {

			this._minute = _minute;
		}

		public int getHour() {

			return _hour;
		}

		public void setHour(int _hour) {

			this._hour = _hour;
		}

		public int getDayOfMoth() {

			return _dayOfMoth;
		}

		public void setDayOfMoth(int _dayOfMoth) {

			this._dayOfMoth = _dayOfMoth;
		}

		public int getDayOfYear() {

			return _dayOfYear;
		}

		public void setDayOfYear(int _dayOfYear) {

			this._dayOfYear = _dayOfYear;
		}

		public int getWeekOfMonth() {

			return _weekOfMonth;
		}

		public void setWeekOfMonth(int _weekOfMonth) {

			this._weekOfMonth = _weekOfMonth;
		}

		public int getWeekOfYear() {

			return _weekOfYear;
		}

		public void setWeekOfYear(int _weekOfYear) {

			this._weekOfYear = _weekOfYear;
		}

		public int getMonth() {

			return _month;
		}

		public void setMonth(int _month) {

			this._month = _month;
		}

		public int getYear() {

			return _year;
		}

		public void setYear(int _year) {

			this._year = _year;
		}

		private int _miniSecond;
		private int _second;
		private int _minute;
		private int _hour;
		private int _dayOfMoth;
		private int _dayOfYear;
		private int _weekOfMonth;
		private int _weekOfYear;
		private int _month;
		private int _year;
	}

	public static SplitDate splitDate(Date date) {

		return new SplitDate(date);
	};

	public static String getGender(int value, Locale locale) {

		String genderLabel = StringPool.BLANK;

		switch (value) {
		case 0:
			genderLabel = LanguageUtil.get(locale, "female");
			break;
		case 1:
			genderLabel = LanguageUtil.get(locale, "male");
			break;
		default:
			genderLabel = LanguageUtil.get(locale, "male");
			break;
		}

		return genderLabel;
	}

	private static Log _log = LogFactoryUtil
			.getLog(PortletUtil.class.getName());
}
