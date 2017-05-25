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

package org.opencps.statisticsmgt.service.persistence;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.opencps.datamgt.model.DictItem;
import org.opencps.datamgt.service.DictItemLocalServiceUtil;
import org.opencps.statisticsmgt.bean.DossierStatisticsBean;
import org.opencps.statisticsmgt.model.DossiersStatistics;
import org.opencps.statisticsmgt.model.impl.DossiersStatisticsImpl;
import org.opencps.statisticsmgt.service.DossiersStatisticsLocalServiceUtil;
import org.opencps.statisticsmgt.util.StatisticsUtil;
import org.opencps.util.PortletConstants;

import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.service.persistence.impl.BasePersistenceImpl;
import com.liferay.util.dao.orm.CustomSQLUtil;

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
public class DossiersStatisticsFinderImpl
	extends BasePersistenceImpl<DossiersStatistics>
	implements DossiersStatisticsFinder {

	/*
	 * NOTE FOR DEVELOPERS: Never reference this interface directly. Always use
	 * {@link
	 * org.opencps.statisticsmgt.service.DossiersStatisticsLocalServiceUtil} to
	 * access the dossiers statistics local service.
	 */

	private static final String SQL_STATISTICS_COLUMN_NAMES_0 =
		DossiersStatisticsFinder.class.getName() + ".[COLUMN-NAMES-0]";

	private static final String SQL_STATISTICS_DATA_TYPES_0 =
		DossiersStatisticsFinder.class.getName() + ".[DATA-TYPES-0]";

	private static final String SQL_STATISTICS_COLUMN_NAMES_1 =
		DossiersStatisticsFinder.class.getName() + ".[COLUMN-NAMES-1]";

	private static final String SQL_STATISTICS_DATA_TYPES_1 =
		DossiersStatisticsFinder.class.getName() + ".[DATA-TYPES-1]";

	private static final String SQL_STATISTICS_COLUMN_NAMES_2 =
		DossiersStatisticsFinder.class.getName() + ".[COLUMN-NAMES-2]";

	private static final String SQL_STATISTICS_DATA_TYPES_2 =
		DossiersStatisticsFinder.class.getName() + ".[DATA-TYPES-2]";

	private static final String SQL_GENERAL_STATISTICS =
		DossiersStatisticsFinder.class.getName() + ".generalStatistics";

	private static final String SQL_STATISTICS_BY_DOMAIN =
		DossiersStatisticsFinder.class.getName() + ".statisticsByDomain";

	private static final String SQL_STATISTICS_BY_GOVAGENCY =
		DossiersStatisticsFinder.class.getName() + ".statisticsByGovagency";

	private static final String SQL_STATISTICS_MONTHS =
		DossiersStatisticsFinder.class.getName() + ".getMonths";

	private static final String SQL_GET_STATS_BY_GOV_DOMAIN =
		DossiersStatisticsFinder.class.getName() + ".getStatsByGovAndDomain";

	private static final String SQL_GET_STATISTICS =
		DossiersStatisticsFinder.class.getName() + ".getStatistics";

	// -------------------------------------------------------------------------

	private static final String SQL_STATS_DOSSIER_RECEIVED_BY_SERVICE_DOMAIN =
		DossiersStatisticsFinder.class.getName() +
			".doStatsDossierReceivedByServiceDomain";

	private static final String SQL_STATS_DOSSIER_PROCESSING_BY_SERVICE_DOMAIN =
		DossiersStatisticsFinder.class.getName() +
			".doStatsDossierProcessingByServiceDomain";

	private static final String SQL_STATS_DOSSIER_PROCESSING_BUT_FINISHED_AT_ANOTHER_TIME_SERVICE_DOMAIN =
		DossiersStatisticsFinder.class.getName() +
			".doStatsDossierProcessingButFinishedAtAnotherTimeByServiceDomain";

	private static final String SQL_STATS_DOSSIER_FINISHED_BY_SERVICE_DOMAIN =
		DossiersStatisticsFinder.class.getName() +
			".doStatsDossierFinishedByServiceDomain";

	private static final String SQL_STATS_DOSSIER_RECEIVED_BY_GOV_AGENCY =
		DossiersStatisticsFinder.class.getName() +
			".doStatsDossierReceivedByGovAgency";

	private static final String SQL_STATS_DOSSIER_PROCESSING_BY_GOV_AGENCY =
		DossiersStatisticsFinder.class.getName() +
			".doStatsDossierProcessingByGovAgency";

	private static final String SQL_STATS_DOSSIER_PROCESSING_BUT_FINISHED_AT_ANOTHER_TIME_GOV_AGENCY =
		DossiersStatisticsFinder.class.getName() +
			".doStatsDossierProcessingButFinishedAtAnotherTimeByGovAgency";

	private static final String SQL_STATS_DOSSIER_FINISHED_BY_GOV_AGENCY =
		DossiersStatisticsFinder.class.getName() +
			".doStatsDossierFinishedByGovAgency";

	private static final String SQL_STATS_DOSSIER_RECEIVED =
		DossiersStatisticsFinder.class.getName() + ".doStatsDossierReceived";

	private static final String SQL_STATS_DOSSIER_PROCESSING =
		DossiersStatisticsFinder.class.getName() + ".doStatsDossierProcessing";

	private static final String SQL_STATS_DOSSIER_PROCESSING_BUT_FINISHED_AT_ANOTHER_TIME =
		DossiersStatisticsFinder.class.getName() +
			".doStatsDossierProcessingButFinishedAtAnotherTime";

	private static final String SQL_STATS_DOSSIER_FINISHED =
		DossiersStatisticsFinder.class.getName() + ".doStatsDossierFinished";

	/**
	 * @param groupId
	 * @param month
	 * @param year
	 * @param field
	 * @param delayStatus
	 * @return
	 */
	@Deprecated
	public List generalStatistics(
		long groupId, int month, int year, String field, int delayStatus) {

		Session session = null;
		try {
			session = openSession();

			String sql = CustomSQLUtil.get(SQL_GENERAL_STATISTICS);

			String definedColumnNames =
				CustomSQLUtil.get(SQL_STATISTICS_COLUMN_NAMES_0);

			String definedCondition =
				StatisticsUtil.getFilterCondition(field, delayStatus);

			sql = StringUtil.replace(sql, "$COLUMNS$", definedColumnNames);

			sql = StringUtil.replace(sql, "$FILTER$", definedCondition);

			String definedColumnDataTypes =
				CustomSQLUtil.get(SQL_STATISTICS_DATA_TYPES_0);

			String[] columnNames = StringUtil.split(definedColumnNames);

			String[] columnDataTypes = StringUtil.split(definedColumnDataTypes);

			_log.info(sql);

			SQLQuery q = session.createSQLQuery(sql);

			q = StatisticsUtil.bindingProperties(q, columnDataTypes, false);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(groupId);

			if (delayStatus >= 0) {
				qPos.add(delayStatus);
			}

			qPos.add(month);

			qPos.add(year);

			Iterator<Integer> itr = q.iterate();

			List<DossierStatisticsBean> statisticsBeans =
				new ArrayList<DossierStatisticsBean>();

			if (itr.hasNext()) {
				Integer count = itr.next();

				DossierStatisticsBean statisticsBean =
					new DossierStatisticsBean();

				statisticsBean.setMonth(month);

				statisticsBean.setGroupId(groupId);

				statisticsBean.setYear(year);
				String columnName = columnNames[0];
				String coulmnDataType = columnDataTypes[0];
				Method method =
					StatisticsUtil.getMethod(columnName, coulmnDataType, field);
				if (method != null) {
					method.invoke(statisticsBean, count.intValue());
				}
				statisticsBean.setAdministrationLevel(0);

				statisticsBeans.add(statisticsBean);
			}

			return statisticsBeans;
		}
		catch (Exception e) {
			_log.error(e);
		}
		finally {
			closeSession(session);
		}
		return null;
	}

	/**
	 * @param groupId
	 * @param month
	 * @param year
	 * @param option
	 * @param delayStatus
	 * @return
	 */
	@Deprecated
	public List statisticsByDomain(
		long groupId, int month, int year, String field, int delayStatus) {

		Session session = null;
		try {
			session = openSession();

			String sql = CustomSQLUtil.get(SQL_STATISTICS_BY_DOMAIN);

			String definedColumnNames =
				CustomSQLUtil.get(SQL_STATISTICS_COLUMN_NAMES_1);

			String definedCondition =
				StatisticsUtil.getFilterCondition(field, delayStatus);

			sql = StringUtil.replace(sql, "$COLUMNS$", definedColumnNames);

			sql = StringUtil.replace(sql, "$FILTER$", definedCondition);

			// _log.info(sql);

			String definedColumnDataTypes =
				CustomSQLUtil.get(SQL_STATISTICS_DATA_TYPES_1);

			String[] columnNames = StringUtil.split(definedColumnNames);

			String[] columnDataTypes = StringUtil.split(definedColumnDataTypes);

			SQLQuery q = session.createSQLQuery(sql);

			q = StatisticsUtil.bindingProperties(q, columnDataTypes, false);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(groupId);

			if (delayStatus >= 0) {
				qPos.add(delayStatus);
			}

			qPos.add(month);

			qPos.add(year);

			Iterator<Object[]> itr =
				(Iterator<Object[]>) QueryUtil.list(
					q, getDialect(), QueryUtil.ALL_POS, QueryUtil.ALL_POS).iterator();

			List<DossierStatisticsBean> statisticsBeans =
				new ArrayList<DossierStatisticsBean>();

			if (itr.hasNext()) {
				while (itr.hasNext()) {
					DossierStatisticsBean statisticsBean =
						new DossierStatisticsBean();

					statisticsBean.setMonth(month);

					statisticsBean.setYear(year);

					statisticsBean.setGroupId(groupId);

					Object[] objects = itr.next();

					if (objects.length == columnDataTypes.length) {
						for (int i = 0; i < objects.length; i++) {
							String columnName = columnNames[i];
							String coulmnDataType = columnDataTypes[i];
							Method method =
								StatisticsUtil.getMethod(
									columnName, coulmnDataType, field);
							if (method != null) {
								method.invoke(statisticsBean, objects[i]);
							}

						}
					}

					statisticsBeans.add(statisticsBean);
				}
			}

			return statisticsBeans;
		}
		catch (Exception e) {
			_log.error(e);
		}
		finally {
			closeSession(session);
		}
		return null;
	}

	/**
	 * @param groupId
	 * @param month
	 * @param year
	 * @param option
	 * @param delayStatus
	 * @return
	 */
	@Deprecated
	public List statisticsByGovAgency(
		long groupId, int month, int year, String field, int delayStatus) {

		Session session = null;
		try {
			session = openSession();

			String sql = CustomSQLUtil.get(SQL_STATISTICS_BY_GOVAGENCY);

			String definedColumnNames =
				CustomSQLUtil.get(SQL_STATISTICS_COLUMN_NAMES_2);

			String definedCondition =
				StatisticsUtil.getFilterCondition(field, delayStatus);

			sql = StringUtil.replace(sql, "$COLUMNS$", definedColumnNames);

			sql = StringUtil.replace(sql, "$FILTER$", definedCondition);

			// _log.info(sql);

			String definedColumnDataTypes =
				CustomSQLUtil.get(SQL_STATISTICS_DATA_TYPES_2);

			String[] columnNames = StringUtil.split(definedColumnNames);

			String[] columnDataTypes = StringUtil.split(definedColumnDataTypes);

			SQLQuery q = session.createSQLQuery(sql);

			q = StatisticsUtil.bindingProperties(q, columnDataTypes, false);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(groupId);

			if (delayStatus >= 0) {
				qPos.add(delayStatus);
			}

			qPos.add(month);

			qPos.add(year);

			Iterator<Object[]> itr =
				(Iterator<Object[]>) QueryUtil.list(
					q, getDialect(), QueryUtil.ALL_POS, QueryUtil.ALL_POS).iterator();

			List<DossierStatisticsBean> statisticsBeans =
				new ArrayList<DossierStatisticsBean>();

			if (itr.hasNext()) {
				while (itr.hasNext()) {
					DossierStatisticsBean statisticsBean =
						new DossierStatisticsBean();

					Object[] objects = itr.next();

					statisticsBean.setMonth(month);

					statisticsBean.setYear(year);

					statisticsBean.setGroupId(groupId);

					if (objects.length == columnDataTypes.length) {
						for (int i = 0; i < objects.length; i++) {
							String columnName = columnNames[i];
							String coulmnDataType = columnDataTypes[i];
							Method method =
								StatisticsUtil.getMethod(
									columnName, coulmnDataType, field);
							if (method != null) {
								method.invoke(statisticsBean, objects[i]);
							}

						}
					}
					statisticsBeans.add(statisticsBean);
				}
			}

			return statisticsBeans;
		}
		catch (Exception e) {
			_log.error(e);
		}
		finally {
			closeSession(session);
		}
		return null;
	}

	@Deprecated
	public List<Integer> getStatisticsMonths(long groupId, int year) {

		Session session = null;
		List<Integer> months = new ArrayList<Integer>();
		try {
			session = openSession();

			String sql = CustomSQLUtil.get(SQL_STATISTICS_MONTHS);

			// _log.info(sql);

			SQLQuery q = session.createSQLQuery(sql);

			q.addScalar("COL0", Type.INTEGER);

			QueryPos qPos = QueryPos.getInstance(q);

			_log.info(groupId + "|" + year);

			qPos.add(groupId);

			qPos.add(year);

			Iterator<Integer> itr = q.iterate();

			if (itr.hasNext()) {
				while (itr.hasNext()) {
					Integer it = itr.next();
					int month = it.intValue();
					_log.info("########################## " + month);
					months.add(month);
				}
			}
		}
		catch (Exception e) {
			_log.error(e);
		}
		finally {
			closeSession(session);
		}

		_log.info("########################## months.size()" + months.size());
		return months;
	}

	/**
	 * @param groupId
	 * @param month
	 * @param year
	 * @param period
	 * @param govCode
	 * @param domainCode
	 * @param level
	 * @return
	 */
	@Deprecated
	public List<DossiersStatistics> getStattistics(
		long groupId, int startMonth, int startYear, int period,
		String govCode, String domainCode, int level) {

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(SQL_GET_STATISTICS);

			// _log.info(sql);

			if (Validator.isNull(domainCode)) {
				sql =
					StringUtil.replace(
						sql, "AND (opencps_dossierstatistics.domainCode = ?)",
						StringPool.BLANK);
			}

			if (Validator.isNull(govCode)) {
				sql =
					StringUtil.replace(
						sql,
						"AND (opencps_dossierstatistics.govAgencyCode = ?)",
						StringPool.BLANK);
			}

			String conditions =
				StatisticsUtil.getPeriodConditions(
					startMonth, startYear, period);

			sql = StringUtil.replace(sql, "$FILTER$", conditions);
			_log.info(sql);

			SQLQuery q = session.createSQLQuery(sql);

			q.addEntity("DossiersStatistics", DossiersStatisticsImpl.class);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(groupId);

			if (Validator.isNotNull(domainCode)) {
				qPos.add(domainCode);
			}

			if (Validator.isNotNull(govCode)) {
				qPos.add(govCode);
			}

			qPos.add(level);

			return (List<DossiersStatistics>) QueryUtil.list(
				q, getDialect(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		}
		catch (Exception e) {
			_log.error(e);
		}
		finally {
			closeSession(session);
		}

		return null;
	}

	/**
	 * @param groupId
	 * @param month
	 * @param year
	 * @param govCode
	 * @param domainCode
	 * @param level
	 * @param notNullGov
	 * @param notNullDomain
	 * @return
	 */
	@Deprecated
	public List<DossiersStatistics> getStatsByGovAndDomain(
		long groupId, int startMonth, int startYear, int period,
		String govCodes, String domainCodes, int level, int domainDeepLevel) {

		// Get tree dictitem by code
		// List<DictItem> dictItems = new ArrayList<DictItem>();

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(SQL_GET_STATS_BY_GOV_DOMAIN);

			// _log.info(sql);

			if (Validator.isNull(govCodes)) {
				sql =
					StringUtil.replace(
						sql, "(opencps_dossierstatistics.govAgencyCode = ?)",
						"(opencps_dossierstatistics.govAgencyCode = '')");
			}
			else {
				if (govCodes.equalsIgnoreCase("all")) {
					sql =
						StringUtil.replace(
							sql,
							"(opencps_dossierstatistics.govAgencyCode = ?)",
							"(opencps_dossierstatistics.govAgencyCode != '')");
				}
				else {
					String[] arrGovCode = StringUtil.split(govCodes);
					List<String> tmp = new ArrayList<String>();

					if (arrGovCode != null && arrGovCode.length > 0) {
						for (int g = 0; g < arrGovCode.length; g++) {
							if (Validator.isNotNull(arrGovCode[g])) {
								tmp.add(StringPool.APOSTROPHE + arrGovCode[g] +
									StringPool.APOSTROPHE);
							}
						}
					}

					sql =
						StringUtil.replace(
							sql,
							"(opencps_dossierstatistics.govAgencyCode = ?)",
							"(opencps_dossierstatistics.govAgencyCode IN (" +
								StringUtil.merge(tmp) + "))");
				}
			}

			if (Validator.isNull(domainCodes)) {
				sql =
					StringUtil.replace(
						sql, "(opencps_dossierstatistics.domainCode = ?)",
						"(opencps_dossierstatistics.domainCode = '')");
			}
			else {
				if (domainCodes.equalsIgnoreCase("all")) {
					sql =
						StringUtil.replace(
							sql, "(opencps_dossierstatistics.domainCode = ?)",
							"(opencps_dossierstatistics.domainCode != '')");
				}
				else {
					String[] arrDomainCode = StringUtil.split(domainCodes);
					List<String> tmp = new ArrayList<String>();

					if (arrDomainCode != null && arrDomainCode.length > 0) {
						for (int d = 0; d < arrDomainCode.length; d++) {
							if (Validator.isNotNull(arrDomainCode[d])) {
								tmp.add(StringPool.APOSTROPHE +
									arrDomainCode[d] + StringPool.APOSTROPHE);

							}
						}
					}

					sql =
						StringUtil.replace(
							sql, "(opencps_dossierstatistics.domainCode = ?)",
							"(opencps_dossierstatistics.domainCode IN (" +
								StringUtil.merge(tmp) + "))");
				}
			}

			String conditions =
				StatisticsUtil.getPeriodConditions(
					startMonth, startYear, period);

			sql = StringUtil.replace(sql, "$FILTER$", conditions);

			_log.info(sql);

			SQLQuery q = session.createSQLQuery(sql);

			q.addEntity("DossiersStatistics", DossiersStatisticsImpl.class);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(groupId);

			qPos.add(level);

			return (List<DossiersStatistics>) QueryUtil.list(
				q, getDialect(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		}
		catch (Exception e) {
			_log.error(e);
		}
		finally {
			closeSession(session);
		}

		return null;
	}

	/**
	 * @param companyId
	 * @param groupId
	 * @param month
	 * @param year
	 * @param delayStatus
	 * @param domainCode
	 * @param administrationLevel
	 * @return
	 */
	public List<DossiersStatistics> doStatsDossierReceivedByServiceDomain(
		long companyId, long groupId, int month, int year, int delayStatus,
		String domainCode) {

		Session session = null;

		List<DossiersStatistics> dossiersStatistics =
			new ArrayList<DossiersStatistics>();

		try {
			session = openSession();

			String sql =
				CustomSQLUtil.get(SQL_STATS_DOSSIER_RECEIVED_BY_SERVICE_DOMAIN);

			SQLQuery q = session.createSQLQuery(sql);

			q.setCacheable(false);

			q.addScalar("total", Type.INTEGER);
			q.addScalar("govAgencyCode", Type.STRING);
			q.addScalar("month", Type.INTEGER);
			q.addScalar("year", Type.INTEGER);
			q.addScalar("domainCode", Type.STRING);

			QueryPos qPos = QueryPos.getInstance(q);
			qPos.add(groupId);
			qPos.add(month);
			qPos.add(year);

			// Bang service config luu sai domainCode -> dictItenId
			// qPos.add(domainCode);

			qPos.add(domainCode);

			DictItem dictItem =
				DictItemLocalServiceUtil.getDictItem(GetterUtil.getLong(domainCode));

			Iterator<Object[]> itr = (Iterator<Object[]>) q.list().iterator();

			if (itr.hasNext()) {
				while (itr.hasNext()) {
					DossiersStatistics dossiersStatistic =
						new DossiersStatisticsImpl();

					Object[] object = itr.next();

					int totalTemp = GetterUtil.getInteger(object[0]);

					String govAgencyCodeTemp = (String) object[1];

					if (Validator.isNull(govAgencyCodeTemp)) {
						govAgencyCodeTemp = StringPool.BLANK;
					}

					//int monthTemp = GetterUtil.getInteger(object[2]);
					//int yearTemp = GetterUtil.getInteger(object[3]);

					dossiersStatistic.setReceivedNumber(totalTemp);
					dossiersStatistic.setGovAgencyCode(govAgencyCodeTemp);
					dossiersStatistic.setMonth(month);
					dossiersStatistic.setYear(year);
					dossiersStatistic.setCompanyId(companyId);
					dossiersStatistic.setGroupId(groupId);
					dossiersStatistic.setCreateDate(new Date());
					//dossiersStatistic.setDomainCode(domainCode);
					dossiersStatistics.add(dossiersStatistic);
				}
			}

			int totalReceivedNumber = 0;

			if (dossiersStatistics != null) {
				for (DossiersStatistics dossiersStatistic : dossiersStatistics) {
					totalReceivedNumber +=
						dossiersStatistic.getReceivedNumber();
					DossiersStatistics temp = null;
					try {
						temp =
							DossiersStatisticsLocalServiceUtil.getDossiersStatisticsByG_GC_DC_M_Y_L(
								groupId, dossiersStatistic.getGovAgencyCode(),
								dictItem.getItemCode(), month, year, -1);
					}
					catch (Exception e) {

					}
					// System.out.println(dossiersStatistic.getGovAgencyCode() +
					// "|" + month + "|" +
					// dossiersStatistic.getReceivedNumber());

					if (temp != null) {
						DossiersStatisticsLocalServiceUtil.updateDossiersStatistics(
							temp.getDossierStatisticId(), 0,
							dossiersStatistic.getReceivedNumber(), -1, -1, -1,
							-1);
					}
					else {
						DossiersStatisticsLocalServiceUtil.addDossiersStatistics(
							groupId, companyId, 0, -1,
							dossiersStatistic.getReceivedNumber(), -1, -1, -1,
							-1, month, year,
							dossiersStatistic.getGovAgencyCode(),
							dictItem.getItemCode(), -1);
					}

				}
			}

			DossiersStatistics temp = null;

			try {
				temp =
					DossiersStatisticsLocalServiceUtil.getDossiersStatisticsByG_GC_DC_M_Y_L(
						groupId, StringPool.BLANK, dictItem.getItemCode(),
						month, year, 0);
			}
			catch (Exception e) {

			}

			if (temp != null) {
				DossiersStatisticsLocalServiceUtil.updateDossiersStatistics(
					temp.getDossierStatisticId(), -1, totalReceivedNumber, -1,
					-1, -1, -1);
			}
			else {
				DossiersStatisticsLocalServiceUtil.addDossiersStatistics(
					groupId, companyId, 0, -1, totalReceivedNumber, -1, -1, -1,
					-1, month, year, StringPool.BLANK, dictItem.getItemCode(),
					0);
			}

		}
		catch (Exception e) {
			_log.error(e);
		}
		finally {
			closeSession(session);
		}

		return null;
	}

	/**
	 * @param companyId
	 * @param groupId
	 * @param month
	 * @param year
	 * @param delayStatus
	 * @param domainCode
	 * @return
	 */
	public List<DossiersStatistics> doStatsDossierFinishedByServiceDomain(
		long companyId, long groupId, int month, int year, int delayStatus,
		String domainCode) {

		Session session = null;

		List<DossiersStatistics> dossiersStatistics =
			new ArrayList<DossiersStatistics>();

		try {
			session = openSession();

			String sql =
				CustomSQLUtil.get(SQL_STATS_DOSSIER_FINISHED_BY_SERVICE_DOMAIN);

			SQLQuery q = session.createSQLQuery(sql);

			q.setCacheable(false);

			q.addScalar("total", Type.INTEGER);
			q.addScalar("govAgencyCode", Type.STRING);
			q.addScalar("month", Type.INTEGER);
			q.addScalar("year", Type.INTEGER);
			q.addScalar("domainCode", Type.STRING);

			QueryPos qPos = QueryPos.getInstance(q);
			qPos.add(groupId);
			qPos.add(month);
			qPos.add(year);

			// Bang service config luu sai domainCode -> dictItenId
			// qPos.add(domainCode);

			qPos.add(domainCode);
			qPos.add(delayStatus);

			DictItem dictItem =
				DictItemLocalServiceUtil.getDictItem(GetterUtil.getLong(domainCode));

			Iterator<Object[]> itr = (Iterator<Object[]>) q.list().iterator();

			if (itr.hasNext()) {
				while (itr.hasNext()) {
					DossiersStatistics dossiersStatistic =
						new DossiersStatisticsImpl();

					Object[] object = itr.next();

					int totalTemp = GetterUtil.getInteger(object[0]);

					String govAgencyCodeTemp = (String) object[1];
					
					if (Validator.isNull(govAgencyCodeTemp)) {
						govAgencyCodeTemp = StringPool.BLANK;
					}
					
					
					//int monthTemp = GetterUtil.getInteger(object[2]);
					//int yearTemp = GetterUtil.getInteger(object[3]);

					if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_ONTIME) {
						dossiersStatistic.setOntimeNumber(totalTemp);
					}
					else if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_LATE) {
						dossiersStatistic.setOvertimeNumber(totalTemp);
					}

					dossiersStatistic.setGovAgencyCode(govAgencyCodeTemp);
					dossiersStatistic.setMonth(month);
					dossiersStatistic.setYear(year);
					dossiersStatistic.setCompanyId(companyId);
					dossiersStatistic.setGroupId(groupId);
					dossiersStatistic.setCreateDate(new Date());

					dossiersStatistics.add(dossiersStatistic);
				}
			}

			int total = 0;

			if (dossiersStatistics != null) {
				for (DossiersStatistics dossiersStatistic : dossiersStatistics) {
					if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_ONTIME) {
						total += dossiersStatistic.getOntimeNumber();
					}
					else if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_LATE) {
						total += dossiersStatistic.getOvertimeNumber();
					}
					DossiersStatistics temp = null;
					try {
						temp =
							DossiersStatisticsLocalServiceUtil.getDossiersStatisticsByG_GC_DC_M_Y_L(
								groupId, dossiersStatistic.getGovAgencyCode(),
								dictItem.getItemCode(), month, year, -1);
					}
					catch (Exception e) {
						_log.info("Can not get statistic by " +
							dossiersStatistic.getGovAgencyCode() + "|" +
							dictItem.getItemCode());
					}
					// System.out.println(dossiersStatistic.getGovAgencyCode() +
					// "|" + month + "|" +
					// dossiersStatistic.getReceivedNumber());

					if (temp != null) {

						if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_ONTIME) {
							DossiersStatisticsLocalServiceUtil.updateDossiersStatistics(
								temp.getDossierStatisticId(), -1, -1,
								dossiersStatistic.getOntimeNumber(), -1, -1, -1);
						}
						else if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_LATE) {
							DossiersStatisticsLocalServiceUtil.updateDossiersStatistics(
								temp.getDossierStatisticId(), -1, -1, -1,
								dossiersStatistic.getOvertimeNumber(), -1, -1);
						}
					}
					else {

						if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_ONTIME) {
							DossiersStatisticsLocalServiceUtil.addDossiersStatistics(
								groupId, companyId, 0, -1, -1,
								dossiersStatistic.getOntimeNumber(), -1, -1,
								-1, month, year,
								dossiersStatistic.getGovAgencyCode(),
								dictItem.getItemCode(), -1);
						}
						else if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_LATE) {
							DossiersStatisticsLocalServiceUtil.addDossiersStatistics(
								groupId, companyId, 0, -1, -1, -1,
								dossiersStatistic.getOvertimeNumber(), -1, -1,
								month, year,
								dossiersStatistic.getGovAgencyCode(),
								dictItem.getItemCode(), -1);
						}
					}

				}
			}

			DossiersStatistics temp = null;

			try {
				temp =
					DossiersStatisticsLocalServiceUtil.getDossiersStatisticsByG_GC_DC_M_Y_L(
						groupId, StringPool.BLANK, dictItem.getItemCode(),
						month, year, 0);
			}
			catch (Exception e) {

			}

			if (temp != null) {
				if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_ONTIME) {
					DossiersStatisticsLocalServiceUtil.updateDossiersStatistics(
						temp.getDossierStatisticId(), -1, -1, total, -1, -1, -1);
				}
				else if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_LATE) {
					DossiersStatisticsLocalServiceUtil.updateDossiersStatistics(
						temp.getDossierStatisticId(), -1, -1, -1, total, -1, -1);
				}
			}
			else {
				if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_ONTIME) {
					DossiersStatisticsLocalServiceUtil.addDossiersStatistics(
						groupId, companyId, 0, -1, -1, total, -1, -1, -1,
						month, year, StringPool.BLANK, dictItem.getItemCode(),
						0);
				}
				else if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_LATE) {
					DossiersStatisticsLocalServiceUtil.addDossiersStatistics(
						groupId, companyId, 0, -1, -1, -1, total, -1, -1,
						month, year, StringPool.BLANK, dictItem.getItemCode(),
						0);
				}
			}

		}
		catch (Exception e) {
			_log.error(e);
		}
		finally {
			closeSession(session);
		}

		return dossiersStatistics;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.opencps.statisticsmgt.service.persistence.DossiersStatisticsFinder
	 * #doStatsDossierProcessingByServiceDomain(long, long, int, int, int,
	 * java.lang.String)
	 */
	public List<DossiersStatistics> doStatsDossierProcessingByServiceDomain(
		long companyId, long groupId, int month, int year, int delayStatus,
		String domainCode) {

		Session session = null;

		List<DossiersStatistics> dossiersStatistics =
			new ArrayList<DossiersStatistics>();

		try {
			session = openSession();

			String sql =
				CustomSQLUtil.get(SQL_STATS_DOSSIER_PROCESSING_BY_SERVICE_DOMAIN);

			SQLQuery q = session.createSQLQuery(sql);

			q.setCacheable(false);

			q.addScalar("total", Type.INTEGER);
			q.addScalar("govAgencyCode", Type.STRING);
			q.addScalar("month", Type.INTEGER);
			q.addScalar("year", Type.INTEGER);
			q.addScalar("domainCode", Type.STRING);

			QueryPos qPos = QueryPos.getInstance(q);
			qPos.add(groupId);
			qPos.add(month);
			qPos.add(year);
			qPos.add(year);
			// Bang service config luu sai domainCode -> dictItenId
			// qPos.add(domainCode);

			qPos.add(domainCode);
			qPos.add(delayStatus);

			DictItem dictItem =
				DictItemLocalServiceUtil.getDictItem(GetterUtil.getLong(domainCode));

			Iterator<Object[]> itr = (Iterator<Object[]>) q.list().iterator();

			if (itr.hasNext()) {
				while (itr.hasNext()) {
					DossiersStatistics dossiersStatistic =
						new DossiersStatisticsImpl();

					Object[] object = itr.next();

					int totalTemp = GetterUtil.getInteger(object[0]);

					String govAgencyCodeTemp = (String) object[1];
					
					if (Validator.isNull(govAgencyCodeTemp)) {
						govAgencyCodeTemp = StringPool.BLANK;
					}
					
					
					//int monthTemp = GetterUtil.getInteger(object[2]);
					//int yearTemp = GetterUtil.getInteger(object[3]);

					if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_UNEXPIRED) {
						dossiersStatistic.setProcessingNumber(totalTemp);
					}
					else if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_EXPIRED) {
						dossiersStatistic.setDelayingNumber(totalTemp);
					}

					dossiersStatistic.setGovAgencyCode(govAgencyCodeTemp);
					dossiersStatistic.setMonth(month);
					dossiersStatistic.setYear(year);
					dossiersStatistic.setCompanyId(companyId);
					dossiersStatistic.setGroupId(groupId);
					dossiersStatistic.setCreateDate(new Date());

					dossiersStatistics.add(dossiersStatistic);
				}
			}

			int total = 0;

			if (dossiersStatistics != null) {
				for (DossiersStatistics dossiersStatistic : dossiersStatistics) {
					if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_UNEXPIRED) {
						total += dossiersStatistic.getProcessingNumber();
					}
					else if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_EXPIRED) {
						total += dossiersStatistic.getDelayingNumber();
					}
					DossiersStatistics temp = null;
					try {
						temp =
							DossiersStatisticsLocalServiceUtil.getDossiersStatisticsByG_GC_DC_M_Y_L(
								groupId, dossiersStatistic.getGovAgencyCode(),
								dictItem.getItemCode(), month, year, -1);
					}
					catch (Exception e) {
						_log.info("Can not get statistic by " +
							dossiersStatistic.getGovAgencyCode() + "|" +
							dictItem.getItemCode());
					}
					// System.out.println(dossiersStatistic.getGovAgencyCode() +
					// "|" + month + "|" +
					// dossiersStatistic.getReceivedNumber());

					if (temp != null) {

						if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_UNEXPIRED) {
							DossiersStatisticsLocalServiceUtil.updateDossiersStatistics(
								temp.getDossierStatisticId(), -1, -1, -1, -1,
								dossiersStatistic.getProcessingNumber(), -1);
						}
						else if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_EXPIRED) {
							DossiersStatisticsLocalServiceUtil.updateDossiersStatistics(
								temp.getDossierStatisticId(), -1, -1, -1, -1,
								-1, dossiersStatistic.getDelayingNumber());
						}
					}
					else {

						if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_UNEXPIRED) {
							DossiersStatisticsLocalServiceUtil.addDossiersStatistics(
								groupId, companyId, 0, -1, -1, -1, -1,
								dossiersStatistic.getProcessingNumber(), -1,
								month, year,
								dossiersStatistic.getGovAgencyCode(),
								dictItem.getItemCode(), -1);
						}
						else if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_EXPIRED) {
							DossiersStatisticsLocalServiceUtil.addDossiersStatistics(
								groupId, companyId, 0, -1, -1, -1, -1, -1,
								dossiersStatistic.getDelayingNumber(), month,
								year, dossiersStatistic.getGovAgencyCode(),
								dictItem.getItemCode(), -1);
						}
					}

				}
			}

			DossiersStatistics temp = null;

			try {
				temp =
					DossiersStatisticsLocalServiceUtil.getDossiersStatisticsByG_GC_DC_M_Y_L(
						groupId, StringPool.BLANK, dictItem.getItemCode(),
						month, year, 0);
			}
			catch (Exception e) {

			}

			if (temp != null) {
				if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_UNEXPIRED) {
					DossiersStatisticsLocalServiceUtil.updateDossiersStatistics(
						temp.getDossierStatisticId(), -1, -1, -1, -1, total, -1);
				}
				else if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_EXPIRED) {
					DossiersStatisticsLocalServiceUtil.updateDossiersStatistics(
						temp.getDossierStatisticId(), -1, -1, -1, -1, -1, total);
				}
			}
			else {
				if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_UNEXPIRED) {
					DossiersStatisticsLocalServiceUtil.addDossiersStatistics(
						groupId, companyId, 0, -1, -1, -1, -1, total, -1,
						month, year, StringPool.BLANK, dictItem.getItemCode(),
						0);
				}
				else if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_EXPIRED) {
					DossiersStatisticsLocalServiceUtil.addDossiersStatistics(
						groupId, companyId, 0, -1, -1, -1, -1, -1, total,
						month, year, StringPool.BLANK, dictItem.getItemCode(),
						0);
				}
			}

		}
		catch (Exception e) {
			_log.error(e);
		}
		finally {
			closeSession(session);
		}

		return dossiersStatistics;
	}

	/**
	 * @param companyId
	 * @param groupId
	 * @param month
	 * @param year
	 * @param domainCode
	 * @return
	 */
	public List<DossiersStatistics> doStatsDossierProcessingButFinishedAtAnotherTimeByServiceDomain(
		long companyId, long groupId, int month, int year, String domainCode) {

		Session session = null;

		List<DossiersStatistics> dossiersStatistics =
			new ArrayList<DossiersStatistics>();

		try {
			session = openSession();

			String sql =
				CustomSQLUtil.get(SQL_STATS_DOSSIER_PROCESSING_BUT_FINISHED_AT_ANOTHER_TIME_SERVICE_DOMAIN);

			SQLQuery q = session.createSQLQuery(sql);

			q.setCacheable(false);

			q.addScalar("total", Type.INTEGER);
			q.addScalar("govAgencyCode", Type.STRING);
			q.addScalar("month", Type.INTEGER);
			q.addScalar("year", Type.INTEGER);
			q.addScalar("domainCode", Type.STRING);

			QueryPos qPos = QueryPos.getInstance(q);
			qPos.add(groupId);
			qPos.add(month);
			qPos.add(year);
			qPos.add(year);

			qPos.add(month);
			qPos.add(year);
			qPos.add(year);
			// Bang service config luu sai domainCode -> dictItenId
			// qPos.add(domainCode);

			qPos.add(domainCode);

			DictItem dictItem =
				DictItemLocalServiceUtil.getDictItem(GetterUtil.getLong(domainCode));

			Iterator<Object[]> itr = (Iterator<Object[]>) q.list().iterator();

			if (itr.hasNext()) {
				while (itr.hasNext()) {
					DossiersStatistics dossiersStatistic =
						new DossiersStatisticsImpl();

					Object[] object = itr.next();

					int totalTemp = GetterUtil.getInteger(object[0]);

					String govAgencyCodeTemp = (String) object[1];
					
					if (Validator.isNull(govAgencyCodeTemp)) {
						govAgencyCodeTemp = StringPool.BLANK;
					}
					
					//int monthTemp = GetterUtil.getInteger(object[2]);
					//int yearTemp = GetterUtil.getInteger(object[3]);

					dossiersStatistic.setProcessingNumber(totalTemp);

					dossiersStatistic.setGovAgencyCode(govAgencyCodeTemp);
					dossiersStatistic.setMonth(month);
					dossiersStatistic.setYear(year);
					dossiersStatistic.setCompanyId(companyId);
					dossiersStatistic.setGroupId(groupId);
					dossiersStatistic.setCreateDate(new Date());

					dossiersStatistics.add(dossiersStatistic);
				}
			}

			int total = 0;

			if (dossiersStatistics != null) {
				for (DossiersStatistics dossiersStatistic : dossiersStatistics) {
					total += dossiersStatistic.getProcessingNumber();
					DossiersStatistics temp = null;
					try {
						temp =
							DossiersStatisticsLocalServiceUtil.getDossiersStatisticsByG_GC_DC_M_Y_L(
								groupId, dossiersStatistic.getGovAgencyCode(),
								dictItem.getItemCode(), month, year, -1);
					}
					catch (Exception e) {
						_log.info("Can not get statistic by " +
							dossiersStatistic.getGovAgencyCode() + "|" +
							dictItem.getItemCode());
					}
					// System.out.println(dossiersStatistic.getGovAgencyCode() +
					// "|" + month + "|" +
					// dossiersStatistic.getReceivedNumber());

					if (temp != null) {
						total += temp.getProcessingNumber();
						DossiersStatisticsLocalServiceUtil.updateDossiersStatistics(
							temp.getDossierStatisticId(),
							-1,
							-1,
							-1,
							-1,
							dossiersStatistic.getProcessingNumber() +
								temp.getProcessingNumber(), -1);
					}
					else {
						DossiersStatisticsLocalServiceUtil.addDossiersStatistics(
							groupId, companyId, 0, -1, -1, -1, -1,
							dossiersStatistic.getProcessingNumber(), -1, month,
							year, dossiersStatistic.getGovAgencyCode(),
							dictItem.getItemCode(), -1);
					}

				}
			}

			DossiersStatistics temp = null;

			try {
				temp =
					DossiersStatisticsLocalServiceUtil.getDossiersStatisticsByG_GC_DC_M_Y_L(
						groupId, StringPool.BLANK, dictItem.getItemCode(),
						month, year, 0);
			}
			catch (Exception e) {

			}

			if (temp != null) {
				DossiersStatisticsLocalServiceUtil.updateDossiersStatistics(
					temp.getDossierStatisticId(), -1, -1, -1, -1, total, -1);
			}
			else {
				DossiersStatisticsLocalServiceUtil.addDossiersStatistics(
					groupId, companyId, 0, -1, -1, -1, -1, total, -1, month,
					year, StringPool.BLANK, dictItem.getItemCode(), 0);
			}

		}
		catch (Exception e) {
			_log.error(e);
		}
		finally {
			closeSession(session);
		}

		return dossiersStatistics;
	}

	/**
	 * @param companyId
	 * @param groupId
	 * @param month
	 * @param year
	 * @param delayStatus
	 * @param govCode
	 * @return
	 */
	public List<DossiersStatistics> doStatsDossierReceivedByGovAgency(
		long companyId, long groupId, int month, int year, int delayStatus,
		String govCode) {

		Session session = null;

		List<DossiersStatistics> dossiersStatistics =
			new ArrayList<DossiersStatistics>();

		try {
			session = openSession();

			String sql =
				CustomSQLUtil.get(SQL_STATS_DOSSIER_RECEIVED_BY_GOV_AGENCY);

			SQLQuery q = session.createSQLQuery(sql);

			q.setCacheable(false);

			q.addScalar("total", Type.INTEGER);
			q.addScalar("govAgencyCode", Type.STRING);
			q.addScalar("month", Type.INTEGER);
			q.addScalar("year", Type.INTEGER);

			QueryPos qPos = QueryPos.getInstance(q);
			qPos.add(groupId);
			qPos.add(month);
			qPos.add(year);
			qPos.add(govCode);

			Iterator<Object[]> itr = (Iterator<Object[]>) q.list().iterator();

			if (itr.hasNext()) {
				while (itr.hasNext()) {
					DossiersStatistics dossiersStatistic =
						new DossiersStatisticsImpl();

					Object[] object = itr.next();

					int totalTemp = GetterUtil.getInteger(object[0]);

					// String govAgencyCodeTemp = (String) object[1];

					// int monthTemp = GetterUtil.getInteger(object[2]);
					// int yearTemp = GetterUtil.getInteger(object[3]);

					dossiersStatistic.setReceivedNumber(totalTemp);
					dossiersStatistic.setGovAgencyCode(govCode);
					dossiersStatistic.setMonth(month);
					dossiersStatistic.setYear(year);
					dossiersStatistic.setCompanyId(companyId);
					dossiersStatistic.setGroupId(groupId);
					dossiersStatistic.setDomainCode(StringPool.BLANK);
					dossiersStatistic.setCreateDate(new Date());

					dossiersStatistics.add(dossiersStatistic);
				}
			}

			if (dossiersStatistics != null) {
				for (DossiersStatistics dossiersStatistic : dossiersStatistics) {

					DossiersStatistics temp = null;
					try {
						temp =
							DossiersStatisticsLocalServiceUtil.getDossiersStatisticsByG_GC_DC_M_Y_L(
								groupId, dossiersStatistic.getGovAgencyCode(),
								StringPool.BLANK, month, year, -1);
					}
					catch (Exception e) {

					}

					if (temp != null) {
						DossiersStatisticsLocalServiceUtil.updateDossiersStatistics(
							temp.getDossierStatisticId(), 0,
							dossiersStatistic.getReceivedNumber(), -1, -1, -1,
							-1);
					}
					else {
						DossiersStatisticsLocalServiceUtil.addDossiersStatistics(
							groupId, companyId, 0, -1,
							dossiersStatistic.getReceivedNumber(), -1, -1, -1,
							-1, month, year,
							dossiersStatistic.getGovAgencyCode(),
							StringPool.BLANK, -1);
					}

				}
			}

		}
		catch (Exception e) {
			_log.error(e);
		}
		finally {
			closeSession(session);
		}

		return null;
	}

	/**
	 * @param companyId
	 * @param groupId
	 * @param month
	 * @param year
	 * @param delayStatus
	 * @param govCode
	 * @return
	 */
	public List<DossiersStatistics> doStatsDossierFinishedByGovAgency(
		long companyId, long groupId, int month, int year, int delayStatus,
		String govCode) {

		Session session = null;

		List<DossiersStatistics> dossiersStatistics =
			new ArrayList<DossiersStatistics>();

		try {
			session = openSession();

			String sql =
				CustomSQLUtil.get(SQL_STATS_DOSSIER_FINISHED_BY_GOV_AGENCY);

			SQLQuery q = session.createSQLQuery(sql);

			q.setCacheable(false);

			q.addScalar("total", Type.INTEGER);
			q.addScalar("govAgencyCode", Type.STRING);
			q.addScalar("month", Type.INTEGER);
			q.addScalar("year", Type.INTEGER);

			QueryPos qPos = QueryPos.getInstance(q);
			qPos.add(groupId);
			qPos.add(month);
			qPos.add(year);
			qPos.add(govCode);
			qPos.add(delayStatus);

			Iterator<Object[]> itr = (Iterator<Object[]>) q.list().iterator();

			if (itr.hasNext()) {
				while (itr.hasNext()) {
					DossiersStatistics dossiersStatistic =
						new DossiersStatisticsImpl();

					Object[] object = itr.next();

					int totalTemp = GetterUtil.getInteger(object[0]);

					// String govAgencyCodeTemp = (String) object[1];
					// int monthTemp = GetterUtil.getInteger(object[2]);
					// int yearTemp = GetterUtil.getInteger(object[3]);

					if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_ONTIME) {
						dossiersStatistic.setOntimeNumber(totalTemp);
					}
					else if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_LATE) {
						dossiersStatistic.setOvertimeNumber(totalTemp);
					}

					dossiersStatistic.setGovAgencyCode(govCode);
					dossiersStatistic.setMonth(month);
					dossiersStatistic.setYear(year);
					dossiersStatistic.setCompanyId(companyId);
					dossiersStatistic.setGroupId(groupId);
					dossiersStatistic.setCreateDate(new Date());
					dossiersStatistic.setDomainCode(StringPool.BLANK);
					dossiersStatistics.add(dossiersStatistic);
				}
			}

			if (dossiersStatistics != null) {
				for (DossiersStatistics dossiersStatistic : dossiersStatistics) {

					DossiersStatistics temp = null;
					try {
						temp =
							DossiersStatisticsLocalServiceUtil.getDossiersStatisticsByG_GC_DC_M_Y_L(
								groupId, dossiersStatistic.getGovAgencyCode(),
								StringPool.BLANK, month, year, -1);
					}
					catch (Exception e) {
						_log.info("Can not get statistic by " +
							dossiersStatistic.getGovAgencyCode() + "|");
					}

					if (temp != null) {

						if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_ONTIME) {
							DossiersStatisticsLocalServiceUtil.updateDossiersStatistics(
								temp.getDossierStatisticId(), -1, -1,
								dossiersStatistic.getOntimeNumber(), -1, -1, -1);
						}
						else if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_LATE) {
							DossiersStatisticsLocalServiceUtil.updateDossiersStatistics(
								temp.getDossierStatisticId(), -1, -1, -1,
								dossiersStatistic.getOvertimeNumber(), -1, -1);
						}
					}
					else {
						if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_ONTIME) {
							DossiersStatisticsLocalServiceUtil.addDossiersStatistics(
								groupId, companyId, 0, -1, -1,
								dossiersStatistic.getOntimeNumber(), -1, -1,
								-1, month, year,
								dossiersStatistic.getGovAgencyCode(),
								StringPool.BLANK, -1);
						}
						else if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_LATE) {
							DossiersStatisticsLocalServiceUtil.addDossiersStatistics(
								groupId, companyId, 0, -1, -1, -1,
								dossiersStatistic.getOvertimeNumber(), -1, -1,
								month, year,
								dossiersStatistic.getGovAgencyCode(),
								StringPool.BLANK, -1);
						}
					}

				}
			}

		}
		catch (Exception e) {
			_log.error(e);
		}
		finally {
			closeSession(session);
		}

		return dossiersStatistics;
	}

	/**
	 * @param companyId
	 * @param groupId
	 * @param month
	 * @param year
	 * @param delayStatus
	 * @param govCode
	 * @return
	 */
	public List<DossiersStatistics> doStatsDossierProcessingByGovAgency(
		long companyId, long groupId, int month, int year, int delayStatus,
		String govCode) {

		Session session = null;

		List<DossiersStatistics> dossiersStatistics =
			new ArrayList<DossiersStatistics>();

		try {
			session = openSession();

			String sql =
				CustomSQLUtil.get(SQL_STATS_DOSSIER_PROCESSING_BY_GOV_AGENCY);

			SQLQuery q = session.createSQLQuery(sql);

			q.setCacheable(false);

			q.addScalar("total", Type.INTEGER);
			q.addScalar("govAgencyCode", Type.STRING);
			q.addScalar("month", Type.INTEGER);
			q.addScalar("year", Type.INTEGER);

			QueryPos qPos = QueryPos.getInstance(q);
			qPos.add(groupId);
			qPos.add(month);
			qPos.add(year);
			qPos.add(year);
			qPos.add(govCode);
			qPos.add(delayStatus);

			Iterator<Object[]> itr = (Iterator<Object[]>) q.list().iterator();

			if (itr.hasNext()) {
				while (itr.hasNext()) {
					DossiersStatistics dossiersStatistic =
						new DossiersStatisticsImpl();

					Object[] object = itr.next();

					int totalTemp = GetterUtil.getInteger(object[0]);

					// String govAgencyCodeTemp = (String) object[1];
					// int monthTemp = GetterUtil.getInteger(object[2]);
					// int yearTemp = GetterUtil.getInteger(object[3]);

					if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_UNEXPIRED) {
						dossiersStatistic.setProcessingNumber(totalTemp);
					}
					else if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_EXPIRED) {
						dossiersStatistic.setDelayingNumber(totalTemp);
					}

					dossiersStatistic.setGovAgencyCode(govCode);
					dossiersStatistic.setMonth(month);
					dossiersStatistic.setYear(year);
					dossiersStatistic.setCompanyId(companyId);
					dossiersStatistic.setGroupId(groupId);
					dossiersStatistic.setCreateDate(new Date());
					dossiersStatistic.setDomainCode(StringPool.BLANK);
					dossiersStatistics.add(dossiersStatistic);
				}
			}

			if (dossiersStatistics != null) {
				for (DossiersStatistics dossiersStatistic : dossiersStatistics) {

					DossiersStatistics temp = null;
					try {
						temp =
							DossiersStatisticsLocalServiceUtil.getDossiersStatisticsByG_GC_DC_M_Y_L(
								groupId, dossiersStatistic.getGovAgencyCode(),
								StringPool.BLANK, month, year, -1);
					}
					catch (Exception e) {
						_log.info("Can not get statistic by " +
							dossiersStatistic.getGovAgencyCode() + "|" +
							StringPool.BLANK);
					}

					if (temp != null) {

						if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_UNEXPIRED) {
							DossiersStatisticsLocalServiceUtil.updateDossiersStatistics(
								temp.getDossierStatisticId(), -1, -1, -1, -1,
								dossiersStatistic.getProcessingNumber(), -1);
						}
						else if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_EXPIRED) {
							DossiersStatisticsLocalServiceUtil.updateDossiersStatistics(
								temp.getDossierStatisticId(), -1, -1, -1, -1,
								-1, dossiersStatistic.getDelayingNumber());
						}
					}
					else {

						if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_UNEXPIRED) {
							DossiersStatisticsLocalServiceUtil.addDossiersStatistics(
								groupId, companyId, 0, -1, -1, -1, -1,
								dossiersStatistic.getProcessingNumber(), -1,
								month, year,
								dossiersStatistic.getGovAgencyCode(),
								StringPool.BLANK, -1);
						}
						else if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_EXPIRED) {
							DossiersStatisticsLocalServiceUtil.addDossiersStatistics(
								groupId, companyId, 0, -1, -1, -1, -1, -1,
								dossiersStatistic.getDelayingNumber(), month,
								year, dossiersStatistic.getGovAgencyCode(),
								StringPool.BLANK, -1);
						}
					}

				}
			}

		}
		catch (Exception e) {
			_log.error(e);
		}
		finally {
			closeSession(session);
		}

		return dossiersStatistics;
	}

	/**
	 * @param companyId
	 * @param groupId
	 * @param month
	 * @param year
	 * @param govCode
	 * @return
	 */
	public List<DossiersStatistics> doStatsDossierProcessingButFinishedAtAnotherTimeByGovAgency(
		long companyId, long groupId, int month, int year, String govCode) {

		Session session = null;

		List<DossiersStatistics> dossiersStatistics =
			new ArrayList<DossiersStatistics>();

		try {
			session = openSession();

			String sql =
				CustomSQLUtil.get(SQL_STATS_DOSSIER_PROCESSING_BUT_FINISHED_AT_ANOTHER_TIME_GOV_AGENCY);

			SQLQuery q = session.createSQLQuery(sql);

			q.setCacheable(false);

			q.addScalar("total", Type.INTEGER);
			q.addScalar("govAgencyCode", Type.STRING);
			q.addScalar("month", Type.INTEGER);
			q.addScalar("year", Type.INTEGER);

			QueryPos qPos = QueryPos.getInstance(q);
			qPos.add(groupId);
			qPos.add(month);
			qPos.add(year);
			qPos.add(year);

			qPos.add(month);
			qPos.add(year);
			qPos.add(year);

			qPos.add(govCode);

			Iterator<Object[]> itr = (Iterator<Object[]>) q.list().iterator();

			if (itr.hasNext()) {
				while (itr.hasNext()) {
					DossiersStatistics dossiersStatistic =
						new DossiersStatisticsImpl();

					Object[] object = itr.next();

					int totalTemp = GetterUtil.getInteger(object[0]);

					// String govAgencyCodeTemp = (String) object[1];
					// int monthTemp = GetterUtil.getInteger(object[2]);
					// int yearTemp = GetterUtil.getInteger(object[3]);

					dossiersStatistic.setProcessingNumber(totalTemp);

					dossiersStatistic.setGovAgencyCode(govCode);
					dossiersStatistic.setMonth(month);
					dossiersStatistic.setYear(year);
					dossiersStatistic.setCompanyId(companyId);
					dossiersStatistic.setGroupId(groupId);
					dossiersStatistic.setCreateDate(new Date());
					dossiersStatistic.setDomainCode(StringPool.BLANK);
					dossiersStatistics.add(dossiersStatistic);
				}
			}

			if (dossiersStatistics != null) {
				for (DossiersStatistics dossiersStatistic : dossiersStatistics) {

					DossiersStatistics temp = null;
					try {
						temp =
							DossiersStatisticsLocalServiceUtil.getDossiersStatisticsByG_GC_DC_M_Y_L(
								groupId, dossiersStatistic.getGovAgencyCode(),
								StringPool.BLANK, month, year, -1);
					}
					catch (Exception e) {
						_log.info("Can not get statistic by " +
							dossiersStatistic.getGovAgencyCode() + "|" +
							StringPool.BLANK);
					}

					if (temp != null) {

						DossiersStatisticsLocalServiceUtil.updateDossiersStatistics(
							temp.getDossierStatisticId(),
							-1,
							-1,
							-1,
							-1,
							dossiersStatistic.getProcessingNumber() +
								temp.getProcessingNumber(), -1);
					}
					else {
						DossiersStatisticsLocalServiceUtil.addDossiersStatistics(
							groupId, companyId, 0, -1, -1, -1, -1,
							dossiersStatistic.getProcessingNumber(), -1, month,
							year, dossiersStatistic.getGovAgencyCode(),
							StringPool.BLANK, -1);
					}

				}
			}

		}
		catch (Exception e) {
			_log.error(e);
		}
		finally {
			closeSession(session);
		}

		return dossiersStatistics;
	}

	/**
	 * @param companyId
	 * @param groupId
	 * @param month
	 * @param year
	 * @param delayStatus
	 * @return
	 */
	public List<DossiersStatistics> doStatsDossierReceived(
		long companyId, long groupId, int month, int year, int delayStatus) {

		Session session = null;

		List<DossiersStatistics> dossiersStatistics =
			new ArrayList<DossiersStatistics>();

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(SQL_STATS_DOSSIER_RECEIVED);

			SQLQuery q = session.createSQLQuery(sql);

			q.setCacheable(false);

			q.addScalar("total", Type.INTEGER);
			q.addScalar("month", Type.INTEGER);
			q.addScalar("year", Type.INTEGER);

			QueryPos qPos = QueryPos.getInstance(q);
			qPos.add(groupId);
			qPos.add(month);
			qPos.add(year);

			Iterator<Object[]> itr = (Iterator<Object[]>) q.list().iterator();

			if (itr.hasNext()) {
				while (itr.hasNext()) {
					DossiersStatistics dossiersStatistic =
						new DossiersStatisticsImpl();

					Object[] object = itr.next();

					int totalTemp = GetterUtil.getInteger(object[0]);
					//int monthTemp = GetterUtil.getInteger(object[1]);
					//int yearTemp = GetterUtil.getInteger(object[2]);

					dossiersStatistic.setReceivedNumber(totalTemp);
					dossiersStatistic.setGovAgencyCode(StringPool.BLANK);
					dossiersStatistic.setMonth(month);
					dossiersStatistic.setYear(year);
					dossiersStatistic.setCompanyId(companyId);
					dossiersStatistic.setGroupId(groupId);
					dossiersStatistic.setDomainCode(StringPool.BLANK);
					dossiersStatistic.setCreateDate(new Date());

					dossiersStatistics.add(dossiersStatistic);
				}
			}

			if (dossiersStatistics != null) {
				for (DossiersStatistics dossiersStatistic : dossiersStatistics) {

					DossiersStatistics temp = null;
					try {
						temp =
							DossiersStatisticsLocalServiceUtil.getDossiersStatisticsByG_GC_DC_M_Y_L(
								groupId, dossiersStatistic.getGovAgencyCode(),
								dossiersStatistic.getDomainCode(), month, year,
								-1);
					}
					catch (Exception e) {

					}

					if (temp != null) {
						DossiersStatisticsLocalServiceUtil.updateDossiersStatistics(
							temp.getDossierStatisticId(), 0,
							dossiersStatistic.getReceivedNumber(), -1, -1, -1,
							-1);
					}
					else {
						DossiersStatisticsLocalServiceUtil.addDossiersStatistics(
							groupId, companyId, 0, -1,
							dossiersStatistic.getReceivedNumber(), -1, -1, -1,
							-1, month, year,
							dossiersStatistic.getGovAgencyCode(),
							dossiersStatistic.getDomainCode(), -1);
					}

				}
			}

		}
		catch (Exception e) {
			_log.error(e);
		}
		finally {
			closeSession(session);
		}

		return null;
	}

	/**
	 * @param companyId
	 * @param groupId
	 * @param month
	 * @param year
	 * @param delayStatus
	 * @return
	 */
	public List<DossiersStatistics> doStatsDossierFinished(
		long companyId, long groupId, int month, int year, int delayStatus) {

		Session session = null;

		List<DossiersStatistics> dossiersStatistics =
			new ArrayList<DossiersStatistics>();

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(SQL_STATS_DOSSIER_FINISHED);

			SQLQuery q = session.createSQLQuery(sql);

			q.setCacheable(false);

			q.addScalar("total", Type.INTEGER);
			q.addScalar("month", Type.INTEGER);
			q.addScalar("year", Type.INTEGER);

			QueryPos qPos = QueryPos.getInstance(q);
			qPos.add(groupId);
			qPos.add(month);
			qPos.add(year);
			qPos.add(delayStatus);

			Iterator<Object[]> itr = (Iterator<Object[]>) q.list().iterator();

			if (itr.hasNext()) {
				while (itr.hasNext()) {
					DossiersStatistics dossiersStatistic =
						new DossiersStatisticsImpl();

					Object[] object = itr.next();

					int totalTemp = GetterUtil.getInteger(object[0]);
					//int monthTemp = GetterUtil.getInteger(object[1]);
					//int yearTemp = GetterUtil.getInteger(object[2]);

					if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_ONTIME) {
						dossiersStatistic.setOntimeNumber(totalTemp);
					}
					else if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_LATE) {
						dossiersStatistic.setOvertimeNumber(totalTemp);
					}

					dossiersStatistic.setGovAgencyCode(StringPool.BLANK);
					dossiersStatistic.setMonth(month);
					dossiersStatistic.setYear(year);
					dossiersStatistic.setCompanyId(companyId);
					dossiersStatistic.setGroupId(groupId);
					dossiersStatistic.setCreateDate(new Date());
					dossiersStatistic.setDomainCode(StringPool.BLANK);
					dossiersStatistics.add(dossiersStatistic);
				}
			}

			if (dossiersStatistics != null) {
				for (DossiersStatistics dossiersStatistic : dossiersStatistics) {

					DossiersStatistics temp = null;
					try {
						temp =
							DossiersStatisticsLocalServiceUtil.getDossiersStatisticsByG_GC_DC_M_Y_L(
								groupId, dossiersStatistic.getGovAgencyCode(),
								dossiersStatistic.getDomainCode(), month, year,
								-1);
					}
					catch (Exception e) {
						_log.info("Can not get statistic by " +
							dossiersStatistic.getGovAgencyCode() + "|");
					}

					if (temp != null) {

						if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_ONTIME) {
							DossiersStatisticsLocalServiceUtil.updateDossiersStatistics(
								temp.getDossierStatisticId(), -1, -1,
								dossiersStatistic.getOntimeNumber(), -1, -1, -1);
						}
						else if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_LATE) {
							DossiersStatisticsLocalServiceUtil.updateDossiersStatistics(
								temp.getDossierStatisticId(), -1, -1, -1,
								dossiersStatistic.getOvertimeNumber(), -1, -1);
						}
					}
					else {
						if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_ONTIME) {
							DossiersStatisticsLocalServiceUtil.addDossiersStatistics(
								groupId, companyId, 0, -1, -1,
								dossiersStatistic.getOntimeNumber(), -1, -1,
								-1, month, year,
								dossiersStatistic.getGovAgencyCode(),
								dossiersStatistic.getDomainCode(), -1);
						}
						else if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_LATE) {
							DossiersStatisticsLocalServiceUtil.addDossiersStatistics(
								groupId, companyId, 0, -1, -1, -1,
								dossiersStatistic.getOvertimeNumber(), -1, -1,
								month, year,
								dossiersStatistic.getGovAgencyCode(),
								dossiersStatistic.getDomainCode(), -1);
						}
					}

				}
			}

		}
		catch (Exception e) {
			_log.error(e);
		}
		finally {
			closeSession(session);
		}

		return dossiersStatistics;
	}

	/**
	 * @param companyId
	 * @param groupId
	 * @param month
	 * @param year
	 * @param delayStatus
	 * @return
	 */
	public List<DossiersStatistics> doStatsDossierProcessing(
		long companyId, long groupId, int month, int year, int delayStatus) {

		Session session = null;

		List<DossiersStatistics> dossiersStatistics =
			new ArrayList<DossiersStatistics>();

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(SQL_STATS_DOSSIER_PROCESSING);

			SQLQuery q = session.createSQLQuery(sql);

			q.setCacheable(false);

			q.addScalar("total", Type.INTEGER);
			q.addScalar("month", Type.INTEGER);
			q.addScalar("year", Type.INTEGER);

			QueryPos qPos = QueryPos.getInstance(q);
			qPos.add(groupId);
			qPos.add(month);
			qPos.add(year);
			qPos.add(year);

			qPos.add(delayStatus);

			Iterator<Object[]> itr = (Iterator<Object[]>) q.list().iterator();

			if (itr.hasNext()) {
				while (itr.hasNext()) {
					DossiersStatistics dossiersStatistic =
						new DossiersStatisticsImpl();

					Object[] object = itr.next();

					int totalTemp = GetterUtil.getInteger(object[0]);
					//int monthTemp = GetterUtil.getInteger(object[1]);
					//int yearTemp = GetterUtil.getInteger(object[2]);

					if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_UNEXPIRED) {
						dossiersStatistic.setProcessingNumber(totalTemp);
					}
					else if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_EXPIRED) {
						dossiersStatistic.setDelayingNumber(totalTemp);
					}

					dossiersStatistic.setGovAgencyCode(StringPool.BLANK);
					dossiersStatistic.setMonth(month);
					dossiersStatistic.setYear(year);
					dossiersStatistic.setCompanyId(companyId);
					dossiersStatistic.setGroupId(groupId);
					dossiersStatistic.setCreateDate(new Date());
					dossiersStatistic.setDomainCode(StringPool.BLANK);
					dossiersStatistics.add(dossiersStatistic);
				}
			}

			if (dossiersStatistics != null) {
				for (DossiersStatistics dossiersStatistic : dossiersStatistics) {

					DossiersStatistics temp = null;
					try {
						temp =
							DossiersStatisticsLocalServiceUtil.getDossiersStatisticsByG_GC_DC_M_Y_L(
								groupId, dossiersStatistic.getGovAgencyCode(),
								dossiersStatistic.getDomainCode(), month, year,
								-1);
					}
					catch (Exception e) {
						_log.info("Can not get statistic by " +
							dossiersStatistic.getGovAgencyCode() + "|" +
							StringPool.BLANK);
					}
					// System.out.println(dossiersStatistic.getGovAgencyCode() +
					// "|" + month + "|" +
					// dossiersStatistic.getReceivedNumber());

					if (temp != null) {

						if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_UNEXPIRED) {
							DossiersStatisticsLocalServiceUtil.updateDossiersStatistics(
								temp.getDossierStatisticId(), -1, -1, -1, -1,
								dossiersStatistic.getProcessingNumber(), -1);
						}
						else if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_EXPIRED) {
							DossiersStatisticsLocalServiceUtil.updateDossiersStatistics(
								temp.getDossierStatisticId(), -1, -1, -1, -1,
								-1, dossiersStatistic.getDelayingNumber());
						}
					}
					else {

						if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_UNEXPIRED) {
							DossiersStatisticsLocalServiceUtil.addDossiersStatistics(
								groupId, companyId, 0, -1, -1, -1, -1,
								dossiersStatistic.getProcessingNumber(), -1,
								month, year,
								dossiersStatistic.getGovAgencyCode(),
								dossiersStatistic.getDomainCode(), -1);
						}
						else if (delayStatus == PortletConstants.DOSSIER_DELAY_STATUS_EXPIRED) {
							DossiersStatisticsLocalServiceUtil.addDossiersStatistics(
								groupId, companyId, 0, -1, -1, -1, -1, -1,
								dossiersStatistic.getDelayingNumber(), month,
								year, dossiersStatistic.getGovAgencyCode(),
								dossiersStatistic.getDomainCode(), -1);
						}
					}

				}
			}

		}
		catch (Exception e) {
			_log.error(e);
		}
		finally {
			closeSession(session);
		}

		return dossiersStatistics;
	}

	/**
	 * @param companyId
	 * @param groupId
	 * @param month
	 * @param year
	 * @return
	 */
	public List<DossiersStatistics> doStatsDossierProcessingButFinishedAtAnotherTime(
		long companyId, long groupId, int month, int year) {

		Session session = null;

		List<DossiersStatistics> dossiersStatistics =
			new ArrayList<DossiersStatistics>();

		try {
			session = openSession();

			String sql =
				CustomSQLUtil.get(SQL_STATS_DOSSIER_PROCESSING_BUT_FINISHED_AT_ANOTHER_TIME);

			SQLQuery q = session.createSQLQuery(sql);

			q.setCacheable(false);

			q.addScalar("total", Type.INTEGER);
			q.addScalar("month", Type.INTEGER);
			q.addScalar("year", Type.INTEGER);

			QueryPos qPos = QueryPos.getInstance(q);
			qPos.add(groupId);
			qPos.add(month);
			qPos.add(year);
			qPos.add(year);

			qPos.add(month);
			qPos.add(year);
			qPos.add(year);

			Iterator<Object[]> itr = (Iterator<Object[]>) q.list().iterator();

			if (itr.hasNext()) {
				while (itr.hasNext()) {
					DossiersStatistics dossiersStatistic =
						new DossiersStatisticsImpl();

					Object[] object = itr.next();

					int totalTemp = GetterUtil.getInteger(object[0]);
					//int monthTemp = GetterUtil.getInteger(object[1]);
					//int yearTemp = GetterUtil.getInteger(object[2]);

					dossiersStatistic.setProcessingNumber(totalTemp);

					dossiersStatistic.setGovAgencyCode(StringPool.BLANK);
					dossiersStatistic.setMonth(month);
					dossiersStatistic.setYear(year);
					dossiersStatistic.setCompanyId(companyId);
					dossiersStatistic.setGroupId(groupId);
					dossiersStatistic.setCreateDate(new Date());
					dossiersStatistic.setDomainCode(StringPool.BLANK);
					dossiersStatistics.add(dossiersStatistic);
				}
			}

			if (dossiersStatistics != null) {
				for (DossiersStatistics dossiersStatistic : dossiersStatistics) {

					DossiersStatistics temp = null;
					try {
						temp =
							DossiersStatisticsLocalServiceUtil.getDossiersStatisticsByG_GC_DC_M_Y_L(
								groupId, dossiersStatistic.getGovAgencyCode(),
								dossiersStatistic.getDomainCode(), month, year,
								-1);
					}
					catch (Exception e) {
						_log.info("Can not get statistic by " +
							dossiersStatistic.getGovAgencyCode() + "|" +
							dossiersStatistic.getDomainCode());
					}

					if (temp != null) {

						DossiersStatisticsLocalServiceUtil.updateDossiersStatistics(
							temp.getDossierStatisticId(),
							-1,
							-1,
							-1,
							-1,
							dossiersStatistic.getProcessingNumber() +
								temp.getProcessingNumber(), -1);
					}
					else {
						DossiersStatisticsLocalServiceUtil.addDossiersStatistics(
							groupId, companyId, 0, -1, -1, -1, -1,
							dossiersStatistic.getProcessingNumber(), -1, month,
							year, dossiersStatistic.getGovAgencyCode(),
							dossiersStatistic.getDomainCode(), -1);
					}

				}
			}

		}
		catch (Exception e) {
			_log.error(e);
		}
		finally {
			closeSession(session);
		}

		return dossiersStatistics;
	}

	private Log _log =
		LogFactoryUtil.getLog(DossiersStatisticsFinderImpl.class.getName());
}
