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
package org.opencps.processmgt.service.persistence;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.opencps.dossiermgt.model.ServiceConfig;
import org.opencps.processmgt.model.ServiceProcess;
import org.opencps.processmgt.model.impl.ServiceProcessImpl;
import org.opencps.processmgt.service.ServiceProcessLocalServiceUtil;
import org.opencps.servicemgt.model.ServiceInfo;

import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.service.persistence.impl.BasePersistenceImpl;
import com.liferay.util.dao.orm.CustomSQLUtil;

/**
 * @author khoavd
 */
public class ServiceProcessFinderImpl extends
		BasePersistenceImpl<ServiceProcess> implements ServiceProcessFinder {

	public final static String SQL_PROCESS_FINDER = ServiceProcessFinder.class
			.getName() + ".searchProcess";
	public final static String SQL_PROCESS_COUNT = ServiceProcessFinder.class
			.getName() + ".countProcess";

	/**
	 * Search Process with keywords
	 * 
	 * @param groupId
	 * @param keywords
	 * @param start
	 * @param end
	 * @return
	 */
	public List<ServiceProcess> searchProcess(long groupId, String keywords,
			int start, int end) {

		String[] names = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			names = CustomSQLUtil.keywords(keywords);
		} else {
			andOperator = true;
		}

		return _searchProcess(groupId, andOperator, names, start, end);

	}

	/**
	 * Count Process with keywords
	 * 
	 * @param groupId
	 * @param keywords
	 * @return
	 */
	public int countProcess(long groupId, String keywords) {

		String[] names = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			names = CustomSQLUtil.keywords(keywords);
		} else {
			andOperator = true;
		}

		return _countProcess(groupId, andOperator, names);

	}

	private List<ServiceProcess> _searchProcess(

	long groupId, boolean andOperator, String[] keywords, int start, int end) {

		keywords = CustomSQLUtil.keywords(keywords);

		Session session = null;
		try {
			session = openSession();

			String sql = CustomSQLUtil.get(SQL_PROCESS_FINDER);

			sql = CustomSQLUtil.replaceKeywords(sql,
					"lower(opencps_serviceprocess.processName)",
					StringPool.LIKE, true, keywords);

			sql = CustomSQLUtil.replaceKeywords(sql,
					"lower(opencps_serviceprocess.processNo)", StringPool.LIKE,
					true, keywords);

			sql = CustomSQLUtil.replaceAndOperator(sql, andOperator);

			SQLQuery q = session.createSQLQuery(sql);

			q.setCacheable(false);

			q.addEntity("ServiceProcess", ServiceProcessImpl.class);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(groupId);

			qPos.add(keywords, 2);
			qPos.add(keywords, 2);

			return (List<ServiceProcess>) QueryUtil.list(q, getDialect(),
					start, end);
		} catch (Exception e) {
			try {
				throw new SystemException(e);
			} catch (SystemException se) {
				se.printStackTrace();
			}
		} finally {
			closeSession(session);
		}

		return null;

	}

	private int _countProcess(long groupId, boolean andOperator,
			String[] keywords) {

		keywords = CustomSQLUtil.keywords(keywords);

		Session session = null;
		try {
			session = openSession();

			String sql = CustomSQLUtil.get(SQL_PROCESS_COUNT);
			sql = CustomSQLUtil.replaceKeywords(sql,
					"lower(opencps_serviceprocess.processName)",
					StringPool.LIKE, true, keywords);

			sql = CustomSQLUtil.replaceKeywords(sql,
					"lower(opencps_serviceprocess.processNo)", StringPool.LIKE,
					true, keywords);

			sql = CustomSQLUtil.replaceAndOperator(sql, andOperator);

			SQLQuery q = session.createSQLQuery(sql);
			q.setCacheable(false);

			q.addScalar(COUNT_COLUMN_NAME, Type.INTEGER);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(groupId);
			qPos.add(keywords, 2);
			qPos.add(keywords, 2);

			Iterator<Integer> itr = q.iterate();

			if (itr.hasNext()) {
				Integer count = itr.next();

				if (count != null) {
					return count.intValue();
				}
			}

			return 0;
		} catch (Exception e) {
			try {
				throw new SystemException(e);
			} catch (SystemException se) {
				se.printStackTrace();
			}
		} finally {
			closeSession(session);
		}

		return 0;

	}

	public List<ServiceProcess> searchServiceProcess(long groupId,
			String keyword, int start, int end) {


		/*
		 * SELECT * FROM opencps_serviceprocess as A
		 * 
		 * WHERE A.serviceProcessId IN (
		 * 
		 * SELECT B.serviceProcessId FROM opencps_service_config as B
		 * 
		 * WHERE B.serviceInfoId in(
		 * 
		 * 		SELECT serviceinfoId FROM opencps_serviceinfo WHERE serviceNo LIKE'%%' 
		 * 		OR serviceName LIKE '%%' 
		 * 		) 
		 * ) 
		 * OR processNo LIKE '%%' OR
		 * processName LIKE '%%' 
		 * 
		 */

		try {


			keyword = StringPool.PERCENT + keyword + StringPool.PERCENT;

			// child query serviceInfo
			DynamicQuery serviceInfoQuery = DynamicQueryFactoryUtil
					.getDynamicQueryFactory().forClass(ServiceInfo.class);

			Criterion serviceInfocriterion = null;

			serviceInfocriterion = PropertyFactoryUtil.forName("serviceNo")
					.like(keyword);
			serviceInfocriterion = RestrictionsFactoryUtil.or(
					serviceInfocriterion,
					PropertyFactoryUtil.forName("serviceName").like(keyword));

			serviceInfoQuery.add(serviceInfocriterion);
			serviceInfoQuery.setProjection(PropertyFactoryUtil
					.forName("serviceinfoId"));

			// parent query serviceConfig

			DynamicQuery serviceConfigQuery = DynamicQueryFactoryUtil
					.getDynamicQueryFactory().forClass(ServiceConfig.class);
			serviceConfigQuery.setProjection(PropertyFactoryUtil
					.forName("serviceProcessId"));
			serviceConfigQuery.add(PropertyFactoryUtil.forName("serviceInfoId")
					.in(serviceInfoQuery));

			// root query serviceProcess

			DynamicQuery serviceProcessQuery = DynamicQueryFactoryUtil
					.getDynamicQueryFactory().forClass(ServiceProcess.class);

			Criterion serviceProcessCriterion = null;

			serviceProcessCriterion = PropertyFactoryUtil.forName(
					"serviceProcessId").in(serviceConfigQuery);
			serviceProcessCriterion = RestrictionsFactoryUtil.or(
					serviceProcessCriterion,
					PropertyFactoryUtil.forName("processNo").like(keyword));
			serviceProcessCriterion = RestrictionsFactoryUtil.or(
					serviceProcessCriterion,
					PropertyFactoryUtil.forName("processName").like(keyword));

			serviceProcessQuery.add(serviceProcessCriterion);

			List<ServiceProcess> list = ServiceProcessLocalServiceUtil
					.dynamicQuery(serviceProcessQuery,start,end);


			return list;

		} catch (Exception e) {
			_log.error(e);
		}

		return new ArrayList<ServiceProcess>();
	}
	
	public int countServiceProcess(long groupId,
			String keyword) {


		/*
		 * SELECT COUNT(*) FROM opencps_serviceprocess as A
		 * 
		 * WHERE A.serviceProcessId IN (
		 * 
		 * SELECT B.serviceProcessId FROM opencps_service_config as B
		 * 
		 * WHERE B.serviceInfoId in(
		 * 
		 * 		SELECT serviceinfoId FROM opencps_serviceinfo WHERE serviceNo LIKE'%%' 
		 * 		OR serviceName LIKE '%%' 
		 * 		) 
		 * ) 
		 * OR processNo LIKE '%%' OR
		 * processName LIKE '%%' 
		 * 
		 */

		try {


			keyword = StringPool.PERCENT + keyword + StringPool.PERCENT;

			// child query serviceInfo
			DynamicQuery serviceInfoQuery = DynamicQueryFactoryUtil
					.getDynamicQueryFactory().forClass(ServiceInfo.class);

			Criterion serviceInfocriterion = null;

			serviceInfocriterion = PropertyFactoryUtil.forName("serviceNo")
					.like(keyword);
			serviceInfocriterion = RestrictionsFactoryUtil.or(
					serviceInfocriterion,
					PropertyFactoryUtil.forName("serviceName").like(keyword));

			serviceInfoQuery.add(serviceInfocriterion);
			serviceInfoQuery.setProjection(PropertyFactoryUtil
					.forName("serviceinfoId"));

			// parent query serviceConfig

			DynamicQuery serviceConfigQuery = DynamicQueryFactoryUtil
					.getDynamicQueryFactory().forClass(ServiceConfig.class);
			serviceConfigQuery.setProjection(PropertyFactoryUtil
					.forName("serviceProcessId"));
			serviceConfigQuery.add(PropertyFactoryUtil.forName("serviceInfoId")
					.in(serviceInfoQuery));

			// root query serviceProcess

			DynamicQuery serviceProcessQuery = DynamicQueryFactoryUtil
					.getDynamicQueryFactory().forClass(ServiceProcess.class);

			Criterion serviceProcessCriterion = null;

			serviceProcessCriterion = PropertyFactoryUtil.forName(
					"serviceProcessId").in(serviceConfigQuery);
			serviceProcessCriterion = RestrictionsFactoryUtil.or(
					serviceProcessCriterion,
					PropertyFactoryUtil.forName("processNo").like(keyword));
			serviceProcessCriterion = RestrictionsFactoryUtil.or(
					serviceProcessCriterion,
					PropertyFactoryUtil.forName("processName").like(keyword));

			serviceProcessQuery.add(serviceProcessCriterion);

			String countString = String.valueOf(ServiceProcessLocalServiceUtil
					.dynamicQueryCount(serviceProcessQuery));

			int count = Integer.valueOf(countString);

			return count;

		} catch (Exception e) {
			_log.error(e);
		}

		return 0;
	}

	private Log _log = LogFactoryUtil.getLog(ServiceProcessFinderImpl.class
			.getName());
}
