/**
 * OpenCPS is the open source Core Public Services software Copyright (C)
 * 2016-present OpenCPS community This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3
 * of the License, or any later version. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details. You should have received a
 * copy of the GNU Affero General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>
 */

package org.opencps.servicemgt.service.persistence;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.opencps.servicemgt.model.ServiceInfo;
import org.opencps.servicemgt.model.impl.ServiceInfoImpl;

import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.service.persistence.impl.BasePersistenceImpl;
import com.liferay.util.dao.orm.CustomSQLUtil;

/**
 * @author khoavd
 */
public class ServiceInfoFinderImpl extends BasePersistenceImpl<ServiceInfo>
		implements ServiceInfoFinder {

	public static final String SEARCH_SERVICE_SQL = ServiceInfoFinder.class
			.getName() + ".searchService";

	public static final String COUNT_SERVICE_SQL = ServiceInfoFinder.class
			.getName() + ".countService";

	public static final String SEARCH_SERVICE_ACTIVE_SQL = ServiceInfoFinder.class
			.getName() + ".searchServiceActive";

	public static final String COUNT_SERVICE_ACTIVE_SQL = ServiceInfoFinder.class
			.getName() + ".countServiceActive";
	
	public static final String GET_DOMAIN_CODES = ServiceInfoFinder.class
			.getName() + ".getDomainCodes";

	/**
	 * @param groupId
	 * @param keywords
	 * @param administrationCode
	 * @param domainCode
	 * @return
	 */
	public int countService(long groupId, String keywords,
			String administrationCode, String domainCode, Integer serviceLevel) {

		// String[] names = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			// names = CustomSQLUtil.keywords(keywords);
		} else {
			andOperator = true;
		}

		return _countService(groupId, keywords, administrationCode, domainCode,
				serviceLevel, andOperator);
	}

	/**
	 * @param groupId
	 * @param keywords
	 * @param administrationCode
	 * @param domainCode
	 * @param start
	 * @param end
	 * @return
	 */
	public List<ServiceInfo> searchService(long groupId, String keywords,
			String administrationCode, String domainCode, Integer serviceLevel,
			int start, int end) {

		// String[] names = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			// names = CustomSQLUtil.keywords(keywords);
		} else {
			andOperator = true;
		}

		return _searchService(groupId, keywords, administrationCode,
				domainCode, serviceLevel, andOperator, start, end);
	}

	/**
	 * @param groupId
	 * @param keywords
	 * @param adminCode
	 * @param domainCode
	 * @param andOperator
	 * @param start
	 * @param end
	 * @return
	 */
	private List<ServiceInfo> _searchService(long groupId, String keyword,
			String adminCode, String domainCode, Integer serviceLevel,
			boolean andOperator, int start, int end) {

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(SEARCH_SERVICE_SQL);

			if (Validator.isNull(keyword)) {
				sql = StringUtil
						.replace(
								sql,
								"AND ((lower(opencps_serviceinfo.serviceName) LIKE ?) OR (lower(opencps_serviceinfo.fullName) LIKE ?))",
								StringPool.BLANK);
			}

			// remove condition query
			if (Validator.equals(adminCode, "0")
					|| Validator.equals(adminCode, StringPool.BLANK)) {
				sql = StringUtil.replace(sql,
						"AND (opencps_serviceinfo.administrationCode = ?)",
						StringPool.BLANK);
			}

			if (Validator.equals(domainCode, "0")
					|| Validator.equals(domainCode, StringPool.BLANK)) {
				sql = StringUtil
						.replace(
								sql,
								"AND ((opencps_serviceinfo.domainCode = ?) OR (opencps_serviceinfo.domainIndex like ?))",
								StringPool.BLANK);
			}

			if (serviceLevel <= 0) {
				sql = StringUtil
						.replace(
								sql,
								"LEFT JOIN opencps_service_config ON opencps_serviceinfo.serviceInfoId = opencps_service_config.serviceInfoId",
								StringPool.BLANK);

				sql = StringUtil.replace(sql,
						"AND (opencps_service_config.serviceLevel = ?)",
						StringPool.BLANK);

				sql = StringUtil.replace(sql,
						"GROUP BY opencps_serviceinfo.serviceInfoId",
						StringPool.BLANK);
			}

			SQLQuery q = session.createSQLQuery(sql);

			q.setCacheable(false);

			q.addEntity("ServiceInfo", ServiceInfoImpl.class);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(groupId);

			if (Validator.isNotNull(keyword)) {
				qPos.add("%" + keyword + "%");

				qPos.add("%" + keyword + "%");
			}

			if (!Validator.equals(adminCode, "0")
					&& !Validator.equals(adminCode, StringPool.BLANK)) {
				qPos.add(adminCode);
			}

			if (!Validator.equals(domainCode, "0")
					&& !Validator.equals(domainCode, StringPool.BLANK)) {
				qPos.add(domainCode);
				qPos.add(StringPool.PERCENT + domainCode + StringPool.PERIOD
						+ StringPool.PERCENT);
			}

			if (serviceLevel > 0) {
				qPos.add(serviceLevel);
			}

			return (List<ServiceInfo>) QueryUtil.list(q, getDialect(), start,
					end);
		} catch (Exception e) {
			_log.error(e);
		} finally {
			closeSession(session);
		}

		return null;

	}

	/**
	 * @param groupId
	 * @param keywords
	 * @param adminCode
	 * @param domainCode
	 * @param andOperator
	 * @return
	 */
	private int _countService(long groupId, String keyword, String adminCode,
			String domainCode, Integer serviceLevel, boolean andOperator) {

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(COUNT_SERVICE_SQL);

			if (Validator.isNull(keyword)) {
				sql = StringUtil
						.replace(
								sql,
								"AND ((lower(opencps_serviceinfo.serviceName) LIKE ?) OR (lower(opencps_serviceinfo.fullName) LIKE ?))",
								StringPool.BLANK);
			}

			// remove condition query
			if (Validator.equals(adminCode, "0")
					|| Validator.equals(adminCode, StringPool.BLANK)) {
				sql = StringUtil.replace(sql,
						"AND (opencps_serviceinfo.administrationCode = ?)",
						StringPool.BLANK);
			}

			if (Validator.equals(domainCode, "0")
					|| Validator.equals(domainCode, StringPool.BLANK)) {
				sql = StringUtil
						.replace(
								sql,
								"AND ((opencps_serviceinfo.domainCode = ?) OR (opencps_serviceinfo.domainIndex like ?))",
								StringPool.BLANK);
			}

			if (serviceLevel <= 0) {
				sql = StringUtil
						.replace(
								sql,
								"LEFT JOIN opencps_service_config ON opencps_serviceinfo.serviceInfoId = opencps_service_config.serviceInfoId",
								StringPool.BLANK);

				sql = StringUtil.replace(sql,
						"AND (opencps_service_config.serviceLevel = ?)",
						StringPool.BLANK);

				sql = StringUtil.replace(sql,
						"GROUP BY opencps_serviceinfo.serviceInfoId",
						StringPool.BLANK);
			}

			SQLQuery q = session.createSQLQuery(sql);

			q.setCacheable(false);

			q.addScalar(COUNT_COLUMN_NAME, Type.INTEGER);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(groupId);

			if (Validator.isNotNull(keyword)) {
				qPos.add("%" + keyword + "%");

				qPos.add("%" + keyword + "%");
			}

			if (!Validator.equals(adminCode, "0")
					&& !Validator.equals(adminCode, StringPool.BLANK)) {
				qPos.add(adminCode);
			}

			if (!Validator.equals(domainCode, "0")
					&& !Validator.equals(domainCode, StringPool.BLANK)) {
				qPos.add(domainCode);
				qPos.add(StringPool.PERCENT + domainCode + StringPool.PERIOD
						+ StringPool.PERCENT);
			}

			if (serviceLevel > 0) {
				qPos.add(serviceLevel);
			}

			Iterator<Integer> itr = q.iterate();

			if (itr.hasNext()) {
				Integer count = itr.next();

				if (count != null) {
					return count.intValue();
				}
			}

			return 0;

		} catch (Exception e) {
			_log.error(e);
		} finally {
			closeSession(session);
		}

		return 0;
	}

	public List<ServiceInfo> searchServiceActive(long groupId, String keywords,
			String administrationCode, String[] domainCode,
			Integer serviceLevel, int start, int end) {

		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {

		} else {
			andOperator = true;
		}

		return _searchServiceActive(groupId, keywords, administrationCode,
				domainCode, serviceLevel, andOperator, start, end);
	}

	/**
	 * @param groupId
	 * @param keywords
	 * @param adminCode
	 * @param domainCode
	 * @param andOperator
	 * @param start
	 * @param end
	 * @return
	 */
	private List<ServiceInfo> _searchServiceActive(long groupId,
			String keyword, String adminCode, String[] domainCode,
			Integer serviceLevel, boolean andOperator, int start, int end) {

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(SEARCH_SERVICE_ACTIVE_SQL);

			if (Validator.isNull(keyword)) {
				sql = StringUtil
						.replace(
								sql,
								"AND ((lower(opencps_serviceinfo.serviceName) LIKE ?) OR (lower(opencps_serviceinfo.fullName) LIKE ?))",
								StringPool.BLANK);
			}

			// remove condition query
			if (Validator.equals(adminCode, "0")
					|| Validator.equals(adminCode, StringPool.BLANK)) {
				sql = StringUtil.replace(sql,
						"AND (opencps_serviceinfo.administrationCode = ?)",
						StringPool.BLANK);
			}

			if (domainCode.length <= 0) {
				sql = StringUtil
						.replace(
								sql,
								"AND (opencps_serviceinfo.domainCode IN ($DOMAIN_CODE$))",
								StringPool.BLANK);
			} else {

				String mergeCode = StringUtil.merge(domainCode);

				sql = StringUtil.replace(sql, "$DOMAIN_CODE$", mergeCode);
			}

			if (serviceLevel <= 0) {
				sql = StringUtil
						.replace(
								sql,
								"LEFT JOIN opencps_service_config ON opencps_serviceinfo.serviceInfoId = opencps_service_config.serviceInfoId",
								StringPool.BLANK);

				sql = StringUtil.replace(sql,
						"AND (opencps_service_config.serviceLevel = ?)",
						StringPool.BLANK);

				sql = StringUtil.replace(sql,
						"GROUP BY opencps_serviceinfo.serviceInfoId",
						StringPool.BLANK);
			}

			SQLQuery q = session.createSQLQuery(sql);

			q.setCacheable(false);

			q.addEntity("ServiceInfo", ServiceInfoImpl.class);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(groupId);

			if (Validator.isNotNull(keyword)) {
				qPos.add("%" + keyword + "%");

				qPos.add("%" + keyword + "%");
			}

			if (!Validator.equals(adminCode, "0")
					&& !Validator.equals(adminCode, StringPool.BLANK)) {
				qPos.add(adminCode);
			}

			// if (domainCode.length > 0) {
			//
			// String mergeCode = StringUtil.merge(domainCode);
			// qPos.add(mergeCode);
			// }

			if (serviceLevel > 0) {
				qPos.add(serviceLevel);
			}

			return (List<ServiceInfo>) QueryUtil.list(q, getDialect(), start,
					end);
		} catch (Exception e) {
			_log.error(e);
		} finally {
			closeSession(session);
		}

		return null;

	}

	/**
	 * @param groupId
	 * @param keywords
	 * @param administrationCode
	 * @param domainCode
	 * @return
	 */
	public int countServiceActive(long groupId, String keywords,
			String administrationCode, String[] domainCode, int serviceLevel) {

		// String[] names = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			// names = CustomSQLUtil.keywords(keywords);
		} else {
			andOperator = true;
		}

		return _countServiceActive(groupId, keywords, administrationCode,
				domainCode, serviceLevel, andOperator);
	}

	/**
	 * @param groupId
	 * @param keywords
	 * @param adminCode
	 * @param domainCode
	 * @param andOperator
	 * @return
	 */
	private int _countServiceActive(long groupId, String keyword,
			String adminCode, String[] domainCode, int serviceLevel,
			boolean andOperator) {

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(COUNT_SERVICE_ACTIVE_SQL);

			if (Validator.isNull(keyword)) {
				sql = StringUtil
						.replace(
								sql,
								"AND ((lower(opencps_serviceinfo.serviceName) LIKE ?) OR (lower(opencps_serviceinfo.fullName) LIKE ?))",
								StringPool.BLANK);
			}

			// remove condition query
			if (Validator.equals(adminCode, "0")
					|| Validator.equals(adminCode, StringPool.BLANK)) {
				sql = StringUtil.replace(sql,
						"AND (opencps_serviceinfo.administrationCode = ?)",
						StringPool.BLANK);
			}

			if (domainCode.length <= 0) {
				sql = StringUtil
						.replace(
								sql,
								"AND (opencps_serviceinfo.domainCode IN ($DOMAIN_CODE$))",
								StringPool.BLANK);
			} else {
				String mergeCode = StringUtil.merge(domainCode);

				sql = StringUtil.replace(sql, "$DOMAIN_CODE$", mergeCode);
			}

			if (serviceLevel <= 0) {
				sql = StringUtil
						.replace(
								sql,
								"LEFT JOIN opencps_service_config ON opencps_serviceinfo.serviceInfoId = opencps_service_config.serviceInfoId",
								StringPool.BLANK);

				sql = StringUtil.replace(sql,
						"AND (opencps_service_config.serviceLevel = ?)",
						StringPool.BLANK);

				sql = StringUtil.replace(sql,
						"GROUP BY opencps_serviceinfo.serviceInfoId",
						StringPool.BLANK);
			}

			SQLQuery q = session.createSQLQuery(sql);

			q.setCacheable(false);

			q.addScalar(COUNT_COLUMN_NAME, Type.INTEGER);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(groupId);

			if (Validator.isNotNull(keyword)) {
				qPos.add("%" + keyword + "%");

				qPos.add("%" + keyword + "%");
			}

			if (!Validator.equals(adminCode, "0")
					&& !Validator.equals(adminCode, StringPool.BLANK)) {
				qPos.add(adminCode);
			}

			if (domainCode.length > 0) {

				String mergeCode = StringUtil.merge(domainCode);

				qPos.add(mergeCode);

			}

			if (serviceLevel > 0) {
				qPos.add(serviceLevel);
			}

			Iterator<Integer> itr = q.iterate();

			if (itr.hasNext()) {
				Integer count = itr.next();

				if (count != null) {
					return count.intValue();
				}
			}

			return 0;

		} catch (Exception e) {
			_log.error(e);
		} finally {
			closeSession(session);
		}

		return 0;
	}
	
	/**
	 * @param groupId
	 * @param keywords
	 * @param govAgencyCode
	 * @param start
	 * @param end
	 * @return
	 */
	public List<String> _getDomainCodes(long groupId, String keywords,
			String govAgencyCode) {

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(GET_DOMAIN_CODES);

			if (Validator.isNull(keywords)) {
				sql = StringUtil
						.replace(
								sql,
								"AND ((lower(opencps_serviceinfo.serviceName) LIKE ?) OR (lower(opencps_serviceinfo.fullName) LIKE ?))",
								StringPool.BLANK);
			}

			// remove condition query
			if (Validator.equals(govAgencyCode, "0")
					|| Validator.equals(govAgencyCode, StringPool.BLANK)) {
				sql = StringUtil.replace(sql,
						"AND (opencps_service_config.govAgencyCode LIKE ?)",
						StringPool.BLANK);
			}

			SQLQuery q = session.createSQLQuery(sql);

			q.setCacheable(false);

			q.addScalar("domainCode", Type.STRING);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(groupId);

			if (Validator.isNotNull(keywords)) {
				qPos.add("%" + keywords + "%");

				qPos.add("%" + keywords + "%");
			}
			
			if(Validator.isNotNull(govAgencyCode)){
				qPos.add(govAgencyCode);
			}
			
			

			return (List<String>) QueryUtil.list(q, getDialect(), QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);
		} catch (Exception e) {
			_log.error(e);
		} finally {
			closeSession(session);
		}

		return new ArrayList<String>();

	}

	private static Log _log = LogFactoryUtil.getLog(ServiceInfoFinderImpl.class
			.getName());

}