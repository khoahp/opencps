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
import java.util.Locale;

import org.opencps.processmgt.NoSuchProcessStepException;
import org.opencps.processmgt.NoSuchProcessWorkflowException;
import org.opencps.processmgt.model.ActionHistory;
import org.opencps.processmgt.model.ProcessStep;
import org.opencps.processmgt.model.ProcessWorkflow;
import org.opencps.processmgt.service.ActionHistoryLocalServiceUtil;
import org.opencps.processmgt.service.ProcessStepLocalServiceUtil;
import org.opencps.processmgt.service.ProcessWorkflowLocalServiceUtil;
import org.opencps.processmgt.util.OutDateStatus;
import org.opencps.util.DateTimeUtil;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;

public class HolidayCheckUtils {

	private static Log _log = LogFactoryUtil.getLog(HolidayCheckUtils.class);

	/**
	 * @param startDate
	 * @param endDate
	 * @param daysDuration
	 * @return
	 */
	public OutDateStatus checkActionDateOver(Date startDate, Date endDate,
			String daysDuration) {

		OutDateStatus outDateStatus = new OutDateStatus();

		long timeOver = 0;

		if (daysDuration.trim().length() > 0) {

			Calendar endDayCal = Calendar.getInstance();

			endDayCal.setTime(endDate);

			Calendar endDateMax = HolidayUtils.getEndDate(startDate,
					daysDuration);

			long endDay = endDayCal.getTimeInMillis();
			long endDayMax = endDateMax.getTimeInMillis();

			timeOver = endDayMax - endDay;

			if (timeOver > 0) {
				outDateStatus.setIsOutDate(false);
				outDateStatus.setTimeOutDate(timeOver);
				return outDateStatus;
			} else if (timeOver < 0) {
				outDateStatus.setIsOutDate(true);
				outDateStatus.setTimeOutDate(timeOver);
				return outDateStatus;
			}
		}
		return outDateStatus;
	}

	public static int calculatorDateUntilDealine(Date startDate, Date endDate,
			int daysDuration) {

		int dateOverNumbers = 0;

		if (daysDuration > 0) {

			Calendar endayCal = Calendar.getInstance();
			endayCal.setTime(endDate);

			Calendar dealineCal = Calendar.getInstance();
			dealineCal.setTime(startDate);

			for (int i = 0; i < daysDuration; i++) {

				dealineCal.add(Calendar.DATE, 1);
			}

			long endDay = endayCal.getTimeInMillis();
			long deadline = dealineCal.getTimeInMillis();
			long result = 0;

			result = deadline - endDay;

			result = DateTimeUtil.convertTimemilisecondsToDays(result);

			dateOverNumbers = (int) result;
		}
		return dateOverNumbers;
	}

	public static String calculatorDateUntilDealineReturnFormart(
			Date startDate, Date endDate, int daysDuration, Locale locale) {

		String dateOverNumbers = StringPool.BLANK;

		if (daysDuration > 0 && Validator.isNotNull(startDate)
				&& Validator.isNotNull(endDate)) {

			Calendar endayCal = Calendar.getInstance();
			endayCal.setTime(endDate);

			Calendar dealineCal = Calendar.getInstance();
			dealineCal.setTime(startDate);

			for (int i = 0; i < daysDuration; i++) {

				dealineCal.add(Calendar.DATE, 1);
			}

			long endDay = endayCal.getTimeInMillis();
			long deadline = dealineCal.getTimeInMillis();
			long result = 0;

			result = deadline - endDay;

			dateOverNumbers = DateTimeUtil.convertTimemilisecondsToFormat(
					result, locale);
		}
		return dateOverNumbers;
	}

	/**
	 * @param processOrderId
	 * @param processWorkflowId
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 */
	public OutDateStatus getOutDateStatus(long processOrderId,
			long processWorkflowId) {

		ActionHistory actionHistoryNewest = null;
		List<ActionHistory> actionHistoryList = new ArrayList<ActionHistory>();

		ProcessWorkflow processWorkflow = null;

		ProcessStep processStep = null;

		OutDateStatus outDateStatus = null;

		try {
			if (processOrderId > 0 && processWorkflowId > 0) {

				actionHistoryList = ActionHistoryLocalServiceUtil
						.getActionHistoryByProcessOrderId(processOrderId, 0,
								1, false);

				if (actionHistoryList.size() > 0) {

					actionHistoryNewest = actionHistoryList.get(0);
				}

				try {
					processWorkflow = ProcessWorkflowLocalServiceUtil
							.getProcessWorkflow(processWorkflowId);
				} catch (NoSuchProcessWorkflowException e) {

				}

				if (Validator.isNotNull(processWorkflow)) {

					if (processWorkflow.getPostProcessStepId() > 0) {

						try {
							processStep = ProcessStepLocalServiceUtil
									.getProcessStep(processWorkflow
											.getPostProcessStepId());
						} catch (NoSuchProcessStepException e) {

						}

						if (Validator.isNotNull(processStep)) {

							if (Validator.isNotNull(actionHistoryNewest)
									&& Validator.isNotNull(actionHistoryNewest
											.getCreateDate())) {
								outDateStatus = checkActionDateOver(
										actionHistoryNewest.getCreateDate(),
										new Date(),
										processStep.getDaysDuration());
							} else {
								outDateStatus = checkActionDateOver(new Date(),
										new Date(),
										processStep.getDaysDuration());
							}

						}
					}
				}
			}
		} catch (Exception e) {
			_log.error(e);
		}

		return outDateStatus;

	}
	
	public static Date getEndDate(Date baseDate, String pattern) {
		
		Calendar estimateDate = null;
		
		estimateDate = HolidayUtils.getEndDate(baseDate, pattern);
		
		return estimateDate.getTime();
	}

}
