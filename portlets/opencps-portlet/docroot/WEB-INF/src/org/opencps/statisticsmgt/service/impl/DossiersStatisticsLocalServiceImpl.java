/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package org.opencps.statisticsmgt.service.impl;

import java.util.Date;
import java.util.List;

import org.opencps.statisticsmgt.NoSuchDossiersStatisticsException;
import org.opencps.statisticsmgt.model.DossiersStatistics;
import org.opencps.statisticsmgt.service.base.DossiersStatisticsLocalServiceBaseImpl;

import com.liferay.counter.service.CounterLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringPool;

/**
 * The implementation of the dossiers statistics local service. <p> All custom
 * service methods should be put in this class. Whenever methods are added,
 * rerun ServiceBuilder to copy their definitions into the
 * {@link org.opencps.statisticsmgt.service.DossiersStatisticsLocalService}
 * interface. <p> This is a local service. Methods of this service will not have
 * security checks based on the propagated JAAS credentials because this service
 * can only be accessed from within the same VM. </p>
 *
 * @author trungnt
 * @see org.opencps.statisticsmgt.service.base.DossiersStatisticsLocalServiceBaseImpl
 * @see org.opencps.statisticsmgt.service.DossiersStatisticsLocalServiceUtil
 */
public class DossiersStatisticsLocalServiceImpl
	extends DossiersStatisticsLocalServiceBaseImpl {

	/*
	 * NOTE FOR DEVELOPERS: Never reference this interface directly. Always use
	 * {@link
	 * org.opencps.statisticsmgt.service.DossiersStatisticsLocalServiceUtil} to
	 * access the dossiers statistics local service.
	 */

	/**
	 * @param groupId
	 * @param companyId
	 * @param userId
	 * @param remainingNumber
	 * @param receivedNumber
	 * @param ontimeNumber
	 * @param processingNumber
	 * @param delayingNumber
	 * @param month
	 * @param year
	 * @param govAgencyCode
	 * @param domainCode
	 * @param administrationLevel
	 * @return
	 * @throws SystemException
	 */
	public DossiersStatistics addDossiersStatistics(
		long groupId, long companyId, long userId, int remainingNumber,
		int receivedNumber, int ontimeNumber, int overtimeNumber,
		int processingNumber, int delayingNumber, int month, int year,
		String govAgencyCode, String domainCode, int administrationLevel)
		throws SystemException {

		long dossierStatisticId =
			CounterLocalServiceUtil.increment(DossiersStatistics.class.getName());

		DossiersStatistics dossiersStatistics =
			dossiersStatisticsPersistence.create(dossierStatisticId);

		Date now = new Date();
		dossiersStatistics.setAdministrationLevel(administrationLevel);
		dossiersStatistics.setCompanyId(companyId);
		dossiersStatistics.setCreateDate(now);
		dossiersStatistics.setDelayingNumber(delayingNumber);
		dossiersStatistics.setDomainCode(domainCode);
		dossiersStatistics.setGovAgencyCode(govAgencyCode);
		dossiersStatistics.setGroupId(groupId);
		dossiersStatistics.setModifiedDate(now);
		dossiersStatistics.setMonth(month);
		dossiersStatistics.setOntimeNumber(ontimeNumber);
		dossiersStatistics.setOvertimeNumber(overtimeNumber);
		dossiersStatistics.setProcessingNumber(processingNumber);
		dossiersStatistics.setReceivedNumber(receivedNumber);
		dossiersStatistics.setRemainingNumber(remainingNumber);
		dossiersStatistics.setUserId(userId);
		dossiersStatistics.setYear(year);

		return dossiersStatisticsPersistence.update(dossiersStatistics);
	}

	/**
	 * @param groupId
	 * @param year
	 * @return
	 */
	public List<Integer> getMonths(long groupId, int year) {

		return dossiersStatisticsFinder.getStatisticsMonths(groupId, year);
	}

	/**
	 * @param groupId
	 * @param month
	 * @param year
	 * @param field
	 * @param delayStatus
	 * @return
	 * @throws SystemException
	 */
	public List generalStatistics(
		long groupId, int month, int year, String field, int delayStatus)
		throws SystemException {

		return dossiersStatisticsFinder.generalStatistics(
			groupId, month, year, field, delayStatus);
	}

	/**
	 * @param groupId
	 * @param month
	 * @param year
	 * @return
	 * @throws SystemException
	 */
	public List statisticsByDomain(
		long groupId, int month, int year, String field, int delayStatus)
		throws SystemException {

		return dossiersStatisticsFinder.statisticsByDomain(
			groupId, month, year, field, delayStatus);
	}

	/**
	 * @param groupId
	 * @param month
	 * @param year
	 * @return
	 * @throws SystemException
	 */
	public List statisticsByGovAgency(
		long groupId, int month, int year, String field, int delayStatus)
		throws SystemException {

		return dossiersStatisticsFinder.statisticsByGovAgency(
			groupId, month, year, field, delayStatus);
	}

	/**
	 * @param domainCode
	 * @param month
	 * @param year
	 * @return
	 * @throws SystemException
	 * @throws NoSuchDossiersStatisticsException
	 */
	public DossiersStatistics getDossiersStatisticsByDC_M_Y(
		long groupId, String domainCode, int month, int year)
		throws SystemException, NoSuchDossiersStatisticsException {

		return dossiersStatisticsPersistence.findByG_DC_M_Y(
			groupId, domainCode, month, year);
	}

	/**
	 * @param groupId
	 * @param month
	 * @param year
	 * @param administrationLevel
	 * @return
	 * @throws SystemException
	 * @throws NoSuchDossiersStatisticsException
	 */
	public DossiersStatistics getDossiersStatisticsByG_M_Y(
		long groupId, int month, int year)
		throws SystemException, NoSuchDossiersStatisticsException {

		return dossiersStatisticsPersistence.findByG_M_Y(groupId, month, year);
	}

	/**
	 * @param govAgencyCode
	 * @param domainCode
	 * @param month
	 * @param year
	 * @return
	 * @throws SystemException
	 * @throws NoSuchDossiersStatisticsException
	 */
	public DossiersStatistics getDossiersStatisticsByG_GC_DC_M_Y_L(
		long groupId, String govAgencyCode, String domainCode, int month,
		int year, int administrationLevel)
		throws SystemException, NoSuchDossiersStatisticsException {

		return dossiersStatisticsPersistence.findByG_GC_DC_M_Y_L(
			groupId, govAgencyCode, domainCode, month, year,
			administrationLevel);
	}

	/**
	 * @param groupId
	 * @param govAgencyCode
	 * @param domainCode
	 * @param month
	 * @param year
	 * @param source
	 * @return
	 * @throws SystemException
	 * @throws NoSuchDossiersStatisticsException
	 */
	public DossiersStatistics getDossiersStatisticsByG_GC_DC_M_Y_S(
		long groupId, String govAgencyCode, String domainCode, int month,
		int year, String source)
		throws SystemException, NoSuchDossiersStatisticsException {

		return dossiersStatisticsPersistence.findByG_GC_DC_M_Y_S(
			groupId, govAgencyCode, domainCode, month, year, source);
	}

	/**
	 * @param govAgencyCode
	 * @param domainCode
	 * @param year
	 * @return
	 * @throws SystemException
	 */
	public List<DossiersStatistics> getDossiersStatisticsByG_GC_DC_Y(
		long groupId, String govAgencyCode, String domainCode, int year)
		throws SystemException {

		return dossiersStatisticsPersistence.findByG_GC_DC_Y(
			groupId, govAgencyCode, domainCode, year);
	}

	/**
	 * @param groupId
	 * @param govAgencyCode
	 * @param domainCode
	 * @param year
	 * @param level
	 * @return
	 * @throws SystemException
	 */
	public List<DossiersStatistics> getDossiersStatisticsByG_GC_DC_Y_L(
		long groupId, String govAgencyCode, String domainCode, int year,
		int level)
		throws SystemException {

		return dossiersStatisticsPersistence.findByG_GC_DC_Y_L(
			groupId, govAgencyCode, domainCode, year, level);
	}

	/**
	 * @param groupId
	 * @param govAgencyCode
	 * @param domainCode
	 * @param year
	 * @param level
	 * @param start
	 * @param end
	 * @param orderByComparator
	 * @return
	 * @throws SystemException
	 */
	public List<DossiersStatistics> getDossiersStatisticsByG_GC_DC_Y_L(
		long groupId, String govAgencyCode, String domainCode, int year,
		int level, int start, int end, OrderByComparator orderByComparator)
		throws SystemException {

		return dossiersStatisticsPersistence.findByG_GC_DC_Y_L(
			groupId, govAgencyCode, domainCode, year, level, start, end,
			orderByComparator);
	}

	/**
	 * @param dossierStatisticId
	 * @param remainingNumber
	 * @param receivedNumber
	 * @param ontimeNumber
	 * @param overtimeNumber
	 * @param processingNumber
	 * @param delayingNumber
	 * @return
	 * @throws SystemException
	 * @throws PortalException
	 */
	public DossiersStatistics updateDossiersStatistics(
		long dossierStatisticId, int remainingNumber, int receivedNumber,
		int ontimeNumber, int overtimeNumber, int processingNumber,
		int delayingNumber)
		throws SystemException, PortalException {

		DossiersStatistics dossiersStatistics =
			dossiersStatisticsLocalService.getDossiersStatistics(dossierStatisticId);

		Date now = new Date();

		dossiersStatistics.setDelayingNumber(delayingNumber);
		dossiersStatistics.setModifiedDate(now);
		dossiersStatistics.setOntimeNumber(ontimeNumber);
		dossiersStatistics.setOvertimeNumber(overtimeNumber);
		dossiersStatistics.setProcessingNumber(processingNumber);
		dossiersStatistics.setReceivedNumber(receivedNumber);
		dossiersStatistics.setRemainingNumber(remainingNumber);

		return dossiersStatisticsPersistence.update(dossiersStatistics);
	}

	/**
	 * @param groupId
	 * @param govCoce
	 * @param domainCode
	 * @param year
	 * @param level
	 * @return
	 * @throws SystemException
	 */
	public List<DossiersStatistics> getDossiersStatisticsG_GC_DC_Y_L(
		long groupId, String govCoce, String domainCode, int year, int level)
		throws SystemException {

		return dossiersStatisticsPersistence.findByG_GC_DC_Y_L(
			groupId, govCoce, domainCode, year, level);
	}

	/**
	 * @param groupId
	 * @param month
	 * @param year
	 * @param govCode
	 * @param domainCode
	 * @param level
	 * @return
	 */
	public List<DossiersStatistics> getStatsByGovAndDomain(
		long groupId, int startMonth, int startYear, int period,
		String govCodes, String domainCodes, int level, int domainDeepLevel) {

		return dossiersStatisticsFinder.getStatsByGovAndDomain(
			groupId, startMonth, startYear, period, govCodes, domainCodes,
			level, domainDeepLevel);
	}

	/**
	 * @param groupId
	 * @param startMonth
	 * @param startYear
	 * @param period
	 * @param govCode
	 * @param domainCode
	 * @param level
	 * @return
	 */
	public List<DossiersStatistics> getStatatistic(
		long groupId, int startMonth, int startYear, int period,
		String govCode, String domainCode, int level) {

		return dossiersStatisticsFinder.getStattistics(
			groupId, startMonth, startYear, period, govCode, domainCode, level);
	}

	/**
	 * @param groupId
	 * @param govCode
	 * @param domainCode
	 * @param remainingNumber
	 * @param receivedNumber
	 * @param ontimeNumber
	 * @param overtimeNumber
	 * @param processingNumber
	 * @param delayingNumber
	 * @param month
	 * @param year
	 * @param source
	 * @return
	 * @throws SystemException
	 */
	public JSONObject updateStatistic(
		long groupId, String govCode, String domainCode, int remainingNumber,
		int receivedNumber, int ontimeNumber, int overtimeNumber,
		int processingNumber, int delayingNumber, int month, int year,
		String source) {

		JSONObject msg = JSONFactoryUtil.createJSONObject();
		try {

			DossiersStatistics dossiersStatistics = null;
			DossiersStatistics dossiersStatisticsGroupByGov = null;
			DossiersStatistics dossiersStatisticsGroupByDomain = null;
			DossiersStatistics dossiersStatisticsGroupAll = null;

			DossiersStatistics[] referenceDossiersStatistics =
				new DossiersStatistics[3];

			int remainingNumberDelta = 0;
			int receivedNumberDelta = 0;
			int ontimeNumberDelta = 0;
			int overtimeNumberDelta = 0;
			int processingNumberDelta = 0;
			int delayingNumberDelta = 0;

			Date now = new Date();

			try {
				dossiersStatistics =
					dossiersStatisticsLocalService.getDossiersStatisticsByG_GC_DC_M_Y_S(
						0, govCode, domainCode, month, year, source);
			}
			catch (NoSuchDossiersStatisticsException e) {
				_log.info("########################## No found stats -----> add new staft: " +
					govCode + " | " + domainCode);
			}
			catch (SystemException e) {
				_log.info("########################## System exception -----> return error");
				msg.put("message", "error");
				return msg;
			}

			try {

				dossiersStatisticsGroupByGov =
					dossiersStatisticsLocalService.getDossiersStatisticsByG_GC_DC_M_Y_S(
						0, govCode, StringPool.BLANK, month, year, source);

			}
			catch (NoSuchDossiersStatisticsException e) {
				_log.info("########################## No found stats -----> add new staft: " +
								govCode + " | " + "Blank Domain");
			}
			catch (SystemException e) {
				_log.info("########################## System exception -----> return error");
				msg.put("message", "error");
				return msg;
			}

			try {

				dossiersStatisticsGroupByDomain =
					dossiersStatisticsLocalService.getDossiersStatisticsByG_GC_DC_M_Y_S(
						0, StringPool.BLANK, domainCode, month, year, source);

			}
			catch (NoSuchDossiersStatisticsException e) {
				_log.info("########################## No found stats -----> add new staft: " +
								"Blank Gov" + " | " + domainCode);
				
			}
			catch (SystemException e) {
				_log.info("########################## System exception -----> return error");
				msg.put("message", "error");
				return msg;
			}

			try {

				dossiersStatisticsGroupAll =
					dossiersStatisticsLocalService.getDossiersStatisticsByG_GC_DC_M_Y_S(
						0, StringPool.BLANK, StringPool.BLANK, month, year,
						source);
			}
			catch (NoSuchDossiersStatisticsException e) {
				_log.info("########################## No found stats -----> add new staft: Blank Gov | Blank Domain");
			}
			catch (SystemException e) {
				_log.info("########################## System exception -----> return error");
				msg.put("message", "error");
				return msg;
			}

			referenceDossiersStatistics[0] = dossiersStatisticsGroupByGov;
			referenceDossiersStatistics[1] = dossiersStatisticsGroupByDomain;
			referenceDossiersStatistics[2] = dossiersStatisticsGroupAll;

			if (dossiersStatistics == null) {

				long dossierStatisticId =
					counterLocalService.increment(DossiersStatistics.class.getName());
				dossiersStatistics =
					dossiersStatisticsPersistence.create(dossierStatisticId);

				// TODO get AdministrationLeve from GovagencyLevel
				dossiersStatistics.setAdministrationLevel(-1);
				dossiersStatistics.setCompanyId(0);
				dossiersStatistics.setCreateDate(now);
				dossiersStatistics.setDomainCode(domainCode);
				dossiersStatistics.setGovAgencyCode(govCode);
				dossiersStatistics.setGroupId(groupId);
				dossiersStatistics.setModifiedDate(now);
				dossiersStatistics.setMonth(month);
				dossiersStatistics.setSource(source);
				dossiersStatistics.setUserId(0);
				dossiersStatistics.setYear(year);

				remainingNumberDelta = remainingNumber;
				receivedNumberDelta = receivedNumber;
				ontimeNumberDelta = ontimeNumber;
				overtimeNumberDelta = overtimeNumber;
				processingNumberDelta = processingNumber;
				delayingNumberDelta = delayingNumber;
			}
			else {
				remainingNumberDelta =
					remainingNumber - dossiersStatistics.getRemainingNumber();
				receivedNumberDelta =
					receivedNumber - dossiersStatistics.getReceivedNumber();
				ontimeNumberDelta =
					ontimeNumber - dossiersStatistics.getOntimeNumber();
				overtimeNumberDelta =
					overtimeNumber - dossiersStatistics.getOvertimeNumber();
				processingNumberDelta =
					processingNumber - dossiersStatistics.getProcessingNumber();
				delayingNumberDelta =
					delayingNumber - dossiersStatistics.getDelayingNumber();
			}

			dossiersStatistics.setModifiedDate(now);
			dossiersStatistics.setDelayingNumber(delayingNumber);
			dossiersStatistics.setOntimeNumber(ontimeNumber);
			dossiersStatistics.setOvertimeNumber(overtimeNumber);
			dossiersStatistics.setProcessingNumber(processingNumber);
			dossiersStatistics.setReceivedNumber(receivedNumber);
			dossiersStatistics.setRemainingNumber(remainingNumber);

			dossiersStatistics =
				dossiersStatisticsPersistence.update(dossiersStatistics);

			for (int i = 0; i < 3; i++) {
				DossiersStatistics referenceDossiersStatistic =
					referenceDossiersStatistics[i];
				if (referenceDossiersStatistic == null) {
					long referenceDossiersStatisticId =
						counterLocalService.increment(DossiersStatistics.class.getName());
					referenceDossiersStatistic =
						dossiersStatisticsPersistence.create(referenceDossiersStatisticId);

					referenceDossiersStatistic.setAdministrationLevel(0);
					referenceDossiersStatistic.setCompanyId(0);
					referenceDossiersStatistic.setCreateDate(now);
					if (i == 0) {
						referenceDossiersStatistic.setGovAgencyCode(govCode);
					}
					else if (i == 1) {
						referenceDossiersStatistic.setDomainCode(domainCode);
					}

					referenceDossiersStatistic.setGroupId(groupId);
					referenceDossiersStatistic.setModifiedDate(now);
					referenceDossiersStatistic.setMonth(month);
					referenceDossiersStatistic.setSource(source);
					referenceDossiersStatistic.setUserId(0);
					referenceDossiersStatistic.setYear(year);
				}

				referenceDossiersStatistic.setModifiedDate(now);
				referenceDossiersStatistic.setDelayingNumber(referenceDossiersStatistic.getDelayingNumber() +
					delayingNumberDelta);
				referenceDossiersStatistic.setOntimeNumber(referenceDossiersStatistic.getOntimeNumber() +
					ontimeNumberDelta);
				referenceDossiersStatistic.setOvertimeNumber(referenceDossiersStatistic.getOvertimeNumber() +
					overtimeNumberDelta);
				referenceDossiersStatistic.setProcessingNumber(referenceDossiersStatistic.getProcessingNumber() +
					processingNumberDelta);
				referenceDossiersStatistic.setReceivedNumber(referenceDossiersStatistic.getReceivedNumber() +
					receivedNumberDelta);
				referenceDossiersStatistic.setRemainingNumber(referenceDossiersStatistic.getRemainingNumber() +
					remainingNumberDelta);

				referenceDossiersStatistic =
					dossiersStatisticsPersistence.update(referenceDossiersStatistic);
			}

			msg.put("message", "success");
		}
		catch (Exception e) {
			msg.put("message", "error");
			_log.error(e);
		}

		return msg;
	}

	private Log _log =
		LogFactoryUtil.getLog(DossiersStatisticsLocalServiceImpl.class.getName());
}
