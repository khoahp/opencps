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

package org.opencps.processmgt.service.persistence;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.opencps.dossiermgt.bean.ProcessOrderBean;
import org.opencps.processmgt.model.ProcessOrder;
import org.opencps.processmgt.model.impl.ProcessOrderImpl;

import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.CalendarUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.service.persistence.impl.BasePersistenceImpl;
import com.liferay.util.dao.orm.CustomSQLUtil;

/**
 * @author trungnt
 */
public class ProcessOrderFinderImpl extends BasePersistenceImpl<ProcessOrder>
		implements ProcessOrderFinder {

	public final static String SQL_PROCESS_ORDER_COUNT = ProcessOrderFinder.class
			.getName() + ".countProcessOrder";
	public final static String SQL_PROCESS_ORDER_FINDER = ProcessOrderFinder.class
			.getName() + ".searchProcessOrder";

	public final static String SQL_PROCESS_ORDER_COUNT_KEY_WORDS =
			ProcessOrderFinder.class
				.getName() + ".countProcessOrderKeyWords";
	public final static String SQL_PROCESS_ORDER_FINDER_KEY_WORDS =
			ProcessOrderFinder.class
				.getName() + ".searchProcessOrderKeyWords";
	public final static String SQL_PROCESS_ORDER_JUST_FINISHED_COUNT =
		ProcessOrderFinder.class
			.getName() + ".countProcessOrderJustFinished";
	public final static String SQL_PROCESS_ORDER_JUST_FINISHED_FINDER = ProcessOrderFinder.class
			.getName() + ".searchProcessOrderJustFinished";

	public final static String SQL_USER_PROCESS_SERVICE = ProcessOrderFinder.class
			.getName() + ".getProcessOrderServiceByUser";

	public final static String SQL_USER_PROCESS_SERVICE_JUST_FINISHED = ProcessOrderFinder.class
			.getName() + ".getProcessOrderServiceJustFinishedByUser";

	public final static String SQL_USER_PROCESS_STEP = ProcessOrderFinder.class
			.getName() + ".getUserProcessStep";

	public final static String SQL_USER_PROCESS_STEP_JUST_FINISHED = ProcessOrderFinder.class
			.getName() + ".getUserProcessStepJustFinished";

	public final static String SQL_RE_ASSIGN_COUNT = ProcessOrderFinder.class
			.getName() + ".countReAssigToUser";

	public final static String SQL_RE_ASSIGN_SEARCH = ProcessOrderFinder.class
			.getName() + ".searchReAssigToUser";

	private Log _log = LogFactoryUtil.getLog(ProcessOrderFinderImpl.class
			.getName());

	/**
	 * @param serviceInfoId
	 * @param processStepId
	 * @param loginUserId
	 * @param assignToUserId
	 * @return
	 */
	public int countProcessOrder(long serviceInfoId, long processStepId,
			long loginUserId, long assignToUserId) {

		Session session = null;
		try {
			session = openSession();

			String sql = CustomSQLUtil.get(SQL_PROCESS_ORDER_COUNT);

			if (serviceInfoId <= 0) {
				sql = StringUtil.replace(sql,
						"AND opencps_processorder.serviceInfoId = ?",
						StringPool.BLANK);
			}

			if (processStepId <= 0) {
				sql = StringUtil.replace(sql,
						"AND opencps_processstep.processStepId = ?",
						StringPool.BLANK);
			}

			SQLQuery q = session.createSQLQuery(sql);
			q.setCacheable(false);

			q.addScalar(COUNT_COLUMN_NAME, Type.INTEGER);

			QueryPos qPos = QueryPos.getInstance(q);

			if (serviceInfoId > 0) {
				qPos.add(serviceInfoId);
			}

			if (processStepId > 0) {
				qPos.add(processStepId);
			}

			qPos.add(loginUserId);
			qPos.add(assignToUserId);

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
	 * @param serviceInfoId
	 * @param processStepId
	 * @param actionUserId
	 * @return
	 */
	public int countProcessOrderJustFinished(long serviceInfoId,
			long processStepId, long actionUserId) {

		Session session = null;
		try {
			session = openSession();

			String sql = CustomSQLUtil
					.get(SQL_PROCESS_ORDER_JUST_FINISHED_COUNT);

			if (serviceInfoId <= 0) {
				sql = StringUtil.replace(sql,
						"AND opencps_processorder.serviceInfoId = ?",
						StringPool.BLANK);
			}

			if (processStepId <= 0) {
				sql = StringUtil.replace(sql,
						"AND opencps_processstep.processStepId = ?",
						StringPool.BLANK);
			}

			SQLQuery q = session.createSQLQuery(sql);
			q.setCacheable(false);

			q.addScalar(COUNT_COLUMN_NAME, Type.INTEGER);

			QueryPos qPos = QueryPos.getInstance(q);

			if (serviceInfoId > 0) {
				qPos.add(serviceInfoId);
			}

			if (processStepId > 0) {
				qPos.add(processStepId);
			}

			qPos.add(actionUserId);

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
	 * @param longinUserId
	 * @return
	 */
	public List getProcessOrderServiceByUser(

	long loginUserId) {

		Session session = null;
		try {
			session = openSession();

			String sql = CustomSQLUtil.get(SQL_USER_PROCESS_SERVICE);

			SQLQuery q = session.createSQLQuery(sql);

			q.setCacheable(false);

			q.addScalar("serviceInfoId", Type.LONG);
			q.addScalar("serviceName", Type.STRING);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(loginUserId);
			qPos.add(loginUserId);

			Iterator<Object[]> itr = (Iterator<Object[]>) QueryUtil.list(q,
					getDialect(), QueryUtil.ALL_POS, QueryUtil.ALL_POS)
					.iterator();

			List<ProcessOrderBean> processOrderBeans = new ArrayList<ProcessOrderBean>();

			if (itr.hasNext()) {
				while (itr.hasNext()) {
					ProcessOrderBean processOrderBean = new ProcessOrderBean();

					Object[] objects = itr.next();

					long serviceInfoId = GetterUtil.getLong(objects[0]);
					String serviceName = (String) objects[1];

					processOrderBean.setServiceInfoId(serviceInfoId);
					processOrderBean.setServiceName(serviceName);

					processOrderBeans.add(processOrderBean);
				}
			}

			return processOrderBeans;
		} catch (Exception e) {
			_log.error(e);
		} finally {
			closeSession(session);
		}

		return null;
	}

	/**
	 * @param longinUserId
	 * @return
	 */
	public List getProcessOrderServiceJustFinishedByUser(

	long loginUserId) {

		Session session = null;
		try {
			session = openSession();

			String sql = CustomSQLUtil
					.get(SQL_USER_PROCESS_SERVICE_JUST_FINISHED);

			SQLQuery q = session.createSQLQuery(sql);

			q.setCacheable(false);

			q.addScalar("serviceInfoId", Type.LONG);
			q.addScalar("serviceName", Type.STRING);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(loginUserId);
			Iterator<Object[]> itr = (Iterator<Object[]>) QueryUtil.list(q,
					getDialect(), QueryUtil.ALL_POS, QueryUtil.ALL_POS)
					.iterator();

			List<ProcessOrderBean> processOrderBeans = new ArrayList<ProcessOrderBean>();

			if (itr.hasNext()) {
				while (itr.hasNext()) {
					ProcessOrderBean processOrderBean = new ProcessOrderBean();

					Object[] objects = itr.next();

					long serviceInfoId = GetterUtil.getLong(objects[0]);
					String serviceName = (String) objects[1];

					processOrderBean.setServiceInfoId(serviceInfoId);
					processOrderBean.setServiceName(serviceName);

					processOrderBeans.add(processOrderBean);
				}
			}

			return processOrderBeans;
		} catch (Exception e) {
			_log.error(e);
		} finally {
			closeSession(session);
		}

		return null;
	}

	/**
	 * @param loginUserId
	 * @param serviceInfoId
	 * @return
	 */
	public List getUserProcessStep(

	long loginUserId, long serviceInfoId) {

		Session session = null;
		try {
			session = openSession();

			String sql = CustomSQLUtil.get(SQL_USER_PROCESS_STEP);

			if (serviceInfoId <= 0) {
				sql = StringUtil.replace(sql,
						"AND opencps_processorder.serviceInfoId = ?",
						StringPool.BLANK);

			}

			SQLQuery q = session.createSQLQuery(sql);

			q.setCacheable(false);

			q
				.addScalar("processStepId", Type.LONG);
			q
				.addScalar("stepName", Type.STRING);
			q
				.addScalar("sequenceNo", Type.STRING);
			
			QueryPos qPos = QueryPos
				.getInstance(q);

			if (serviceInfoId > 0) {
				qPos.add(serviceInfoId);
			}

			qPos.add(loginUserId);
			qPos.add(loginUserId);

			Iterator<Object[]> itr = (Iterator<Object[]>) QueryUtil.list(q,
					getDialect(), QueryUtil.ALL_POS, QueryUtil.ALL_POS)
					.iterator();

			List<ProcessOrderBean> processOrderBeans = new ArrayList<ProcessOrderBean>();

			if (itr.hasNext()) {
				while (itr.hasNext()) {
					ProcessOrderBean processOrderBean = new ProcessOrderBean();

					Object[] objects = itr.next();

					long processStepId = GetterUtil.getLong(objects[0]);
					String processStepName = (String) objects[1];


					String sequenceNo = (String) objects[2];
					
					processOrderBean
						.setProcessStepId(processStepId);
					processOrderBean
						.setStepName(processStepName);
					processOrderBean
						.setSequenceNo(sequenceNo);
					
					processOrderBeans
						.add(processOrderBean);
				}
			}

			return processOrderBeans;
		} catch (Exception e) {
			_log.error(e);
		} finally {
			closeSession(session);
		}

		return null;
	}

	/**
	 * @param loginUserId
	 * @param serviceInfoId
	 * @return
	 */
	public List getUserProcessStepJustFinished(

	long loginUserId, long serviceInfoId) {

		Session session = null;
		try {
			session = openSession();

			String sql = CustomSQLUtil.get(SQL_USER_PROCESS_STEP_JUST_FINISHED);

			if (serviceInfoId <= 0) {
				sql = StringUtil.replace(sql,
						"AND opencps_processorder.serviceInfoId = ?",
						StringPool.BLANK);

			}

			SQLQuery q = session.createSQLQuery(sql);

			q.setCacheable(false);

			q
				.addScalar("processStepId", Type.LONG);
			q
				.addScalar("stepName", Type.STRING);
			q
				.addScalar("sequenceNo", Type.STRING);
			
			QueryPos qPos = QueryPos
				.getInstance(q);

			if (serviceInfoId > 0) {
				qPos.add(serviceInfoId);
			}

			qPos.add(loginUserId);

			Iterator<Object[]> itr = (Iterator<Object[]>) QueryUtil.list(q,
					getDialect(), QueryUtil.ALL_POS, QueryUtil.ALL_POS)
					.iterator();

			List<ProcessOrderBean> processOrderBeans = new ArrayList<ProcessOrderBean>();

			if (itr.hasNext()) {
				while (itr.hasNext()) {
					ProcessOrderBean processOrderBean = new ProcessOrderBean();

					Object[] objects = itr.next();

					long processStepId = GetterUtil.getLong(objects[0]);
					String processStepName = (String) objects[1];

					String sequenceNo = (String) objects[2];
					
					processOrderBean
						.setProcessStepId(processStepId);
					processOrderBean
						.setStepName(processStepName);
					processOrderBean
					.setSequenceNo(sequenceNo);
					
					processOrderBeans
						.add(processOrderBean);
				}
			}

			return processOrderBeans;
		} catch (Exception e) {
			_log.error(e);
		} finally {
			closeSession(session);
		}

		return null;
	}

	/**
	 * @param serviceInfoId
	 * @param processStepId
	 * @param loginUserId
	 * @param assignToUserId
	 * @param start
	 * @param end
	 * @param orderByComparator
	 * @return
	 */
	public List searchProcessOrder(long serviceInfoId,

	long processStepId, long loginUserId, long assignToUserId, int start,
			int end, OrderByComparator orderByComparator) {

		Session session = null;
		try {
			session = openSession();

			String sql = CustomSQLUtil.get(SQL_PROCESS_ORDER_FINDER);

			if (serviceInfoId <= 0) {
				sql = StringUtil.replace(sql,
						"AND opencps_processorder.serviceInfoId = ?",
						StringPool.BLANK);
			}

			if (processStepId <= 0) {
				sql = StringUtil.replace(sql,
						"AND opencps_processstep.processStepId = ?",
						StringPool.BLANK);
			}

			sql = CustomSQLUtil.replaceOrderBy(sql, orderByComparator);

			SQLQuery q = session.createSQLQuery(sql);

			q.setCacheable(false);

			q.addEntity("ProcessOrder", ProcessOrderImpl.class);

			q.addScalar("serviceConfigId", Type.LONG);
			q.addScalar("subjectId", Type.STRING);
			q.addScalar("subjectName", Type.STRING);
			q.addScalar("receptionNo", Type.STRING);
			q.addScalar("serviceName", Type.STRING);
			q.addScalar("stepName", Type.STRING);
			q.addScalar("sequenceNo", Type.STRING);
			q.addScalar("daysDuration", Type.INTEGER);
			q.addScalar("referenceDossierPartId", Type.LONG);
			q.addScalar("readOnly", Type.BOOLEAN);

			QueryPos qPos = QueryPos.getInstance(q);

			if (serviceInfoId > 0) {
				qPos.add(serviceInfoId);
			}

			if (processStepId > 0) {
				qPos.add(processStepId);
			}

			qPos.add(loginUserId);
			qPos.add(assignToUserId);

			Iterator<Object[]> itr = (Iterator<Object[]>) QueryUtil.list(q,
					getDialect(), start, end).iterator();

			List<ProcessOrderBean> processOrderBeans = new ArrayList<ProcessOrderBean>();

			if (itr.hasNext()) {
				while (itr.hasNext()) {
					ProcessOrderBean processOrderBean = new ProcessOrderBean();

					Object[] objects = itr.next();

					ProcessOrder processOrder = (ProcessOrder) objects[0];

					long serviceConfigId = GetterUtil.getLong(objects[1]);
					String subjectId = (String) objects[2];
					String subjectName = (String) objects[3];
					String receptionNo = (String) objects[4];
					String serviceName = (String) objects[5];
					String stepName = (String) objects[6];
					String sequenceNo = (String) objects[7];
					int daysDuration = GetterUtil.getInteger(objects[8]);
					long referenceDossierPartId = GetterUtil
							.getLong(objects[9]);

					boolean readOnly = GetterUtil.getBoolean(objects[10]);

					processOrderBean.setActionDatetime(processOrder
							.getActionDatetime());
					processOrderBean.setActionUserId(processOrder
							.getActionUserId());
					processOrderBean.setAssignToUserId(processOrder
							.getAssignToUserId());
					// processOrderBean.setAssignToUserName(assignToUserName);
					processOrderBean.setCompanyId(processOrder.getCompanyId());
					processOrderBean.setDaysDuration(daysDuration);
					// processOrderBean.setDealine(dealine);
					processOrderBean.setDossierId(processOrder.getDossierId());
					processOrderBean.setDossierStatus(processOrder
							.getDossierStatus());
					processOrderBean.setDossierTemplateId(processOrder
							.getDossierTemplateId());
					processOrderBean.setFileGroupId(processOrder
							.getFileGroupId());
					processOrderBean.setGovAgencyCode(processOrder
							.getGovAgencyCode());
					processOrderBean.setGovAgencyName(processOrder
							.getGovAgencyName());
					processOrderBean.setGovAgencyOrganizationId(processOrder
							.getGovAgencyOrganizationId());
					processOrderBean.setGroupId(processOrder.getGroupId());
					processOrderBean.setProcessOrderId(processOrder
							.getProcessOrderId());
					processOrderBean.setProcessStepId(processStepId);
					processOrderBean.setReceptionNo(receptionNo);
					processOrderBean
							.setReferenceDossierPartId(referenceDossierPartId);
					processOrderBean.setSequenceNo(sequenceNo);
					processOrderBean.setServiceConfigId(serviceConfigId);
					processOrderBean.setServiceInfoId(processOrder
							.getServiceInfoId());
					processOrderBean.setServiceName(serviceName);
					processOrderBean.setServiceProcessId(processOrder
							.getServiceProcessId());
					processOrderBean.setStepName(stepName);
					processOrderBean.setSubjectId(subjectId);
					processOrderBean.setSubjectName(subjectName);
					processOrderBean.setUserId(processOrder.getUserId());
					processOrderBean.setReadOnly(readOnly);

					processOrderBeans.add(processOrderBean);
				}
			}

			return processOrderBeans;
		} catch (Exception e) {
			_log.error(e);
		} finally {
			closeSession(session);
		}

		return null;

	}

	/**
	 * @param serviceInfoId
	 * @param processStepId
	 * @param actionUserId
	 * @param start
	 * @param end
	 * @param orderByComparator
	 * @return
	 */
	public List searchProcessOrderJustFinished(

	long serviceInfoId, long processStepId, long actionUserId, int start,
			int end, OrderByComparator orderByComparator) {

		Session session = null;
		try {
			session = openSession();

			String sql = CustomSQLUtil
					.get(SQL_PROCESS_ORDER_JUST_FINISHED_FINDER);

			if (serviceInfoId <= 0) {
				sql = StringUtil.replace(sql,
						"AND opencps_processorder.serviceInfoId = ?",
						StringPool.BLANK);
			}

			if (processStepId <= 0) {
				sql = StringUtil.replace(sql,
						"AND opencps_processstep.processStepId = ?",
						StringPool.BLANK);
			}

			sql = CustomSQLUtil.replaceOrderBy(sql, orderByComparator);

			SQLQuery q = session.createSQLQuery(sql);

			q.setCacheable(false);

			q.addEntity("ProcessOrder", ProcessOrderImpl.class);

			q.addScalar("serviceConfigId", Type.LONG);
			q.addScalar("subjectId", Type.STRING);
			q.addScalar("subjectName", Type.STRING);
			q.addScalar("receptionNo", Type.STRING);
			q.addScalar("serviceName", Type.STRING);
			q.addScalar("stepName", Type.STRING);
			q.addScalar("sequenceNo", Type.STRING);
			q.addScalar("daysDuration", Type.INTEGER);
			q.addScalar("referenceDossierPartId", Type.LONG);

			QueryPos qPos = QueryPos.getInstance(q);

			if (serviceInfoId > 0) {
				qPos.add(serviceInfoId);
			}

			if (processStepId > 0) {
				qPos.add(processStepId);
			}

			qPos.add(actionUserId);

			Iterator<Object[]> itr = (Iterator<Object[]>) QueryUtil.list(q,
					getDialect(), start, end).iterator();

			List<ProcessOrderBean> processOrderBeans = new ArrayList<ProcessOrderBean>();

			if (itr.hasNext()) {
				while (itr.hasNext()) {
					ProcessOrderBean processOrderBean = new ProcessOrderBean();

					Object[] objects = itr.next();

					ProcessOrder processOrder = (ProcessOrder) objects[0];

					long serviceConfigId = GetterUtil.getLong(objects[1]);
					String subjectId = (String) objects[2];
					String subjectName = (String) objects[3];
					String receptionNo = (String) objects[4];
					String serviceName = (String) objects[5];
					String stepName = (String) objects[6];
					String sequenceNo = (String) objects[7];
					int daysDuration = GetterUtil.getInteger(objects[8]);
					long referenceDossierPartId = GetterUtil
							.getLong(objects[9]);

					processOrderBean.setActionDatetime(processOrder
							.getActionDatetime());
					processOrderBean.setActionUserId(processOrder
							.getActionUserId());
					processOrderBean.setAssignToUserId(processOrder
							.getAssignToUserId());
					// processOrderBean.setAssignToUserName(assignToUserName);
					processOrderBean.setCompanyId(processOrder.getCompanyId());
					processOrderBean.setDaysDuration(daysDuration);
					// processOrderBean.setDealine(dealine);
					processOrderBean.setDossierId(processOrder.getDossierId());
					processOrderBean.setDossierStatus(processOrder
							.getDossierStatus());
					processOrderBean.setDossierTemplateId(processOrder
							.getDossierTemplateId());
					processOrderBean.setFileGroupId(processOrder
							.getFileGroupId());
					processOrderBean.setGovAgencyCode(processOrder
							.getGovAgencyCode());
					processOrderBean.setGovAgencyName(processOrder
							.getGovAgencyName());
					processOrderBean.setGovAgencyOrganizationId(processOrder
							.getGovAgencyOrganizationId());
					processOrderBean.setGroupId(processOrder.getGroupId());
					processOrderBean.setProcessOrderId(processOrder
							.getProcessOrderId());
					processOrderBean.setProcessStepId(processStepId);
					processOrderBean.setReceptionNo(receptionNo);
					processOrderBean
							.setReferenceDossierPartId(referenceDossierPartId);
					processOrderBean.setSequenceNo(sequenceNo);
					processOrderBean.setServiceConfigId(serviceConfigId);
					processOrderBean.setServiceInfoId(processOrder
							.getServiceInfoId());
					processOrderBean.setServiceName(serviceName);
					processOrderBean.setServiceProcessId(processOrder
							.getServiceProcessId());
					processOrderBean.setStepName(stepName);
					processOrderBean.setSubjectId(subjectId);
					processOrderBean.setSubjectName(subjectName);
					processOrderBean.setUserId(processOrder.getUserId());

					processOrderBean
							.setActionNote(processOrder.getActionNote());

					processOrderBeans.add(processOrderBean);
				}
			}

			return processOrderBeans;
		} catch (Exception e) {
			_log.error(e);
		} finally {
			closeSession(session);
		}

		return null;

	}

	/**
	 * @param actionUserId
	 * @return
	 * @throws SystemException
	 */
	public List<ProcessOrder> searchReAssigToUser(long actionUserId, int start,
			int end) throws SystemException {
		Session session = null;

		try {
			session = openSession();
			String sql = CustomSQLUtil.get(SQL_RE_ASSIGN_SEARCH);
			
			SQLQuery q = session.createSQLQuery(sql);

			q.setCacheable(false);

			q.addEntity("ProcessOrder", ProcessOrderImpl.class);

			QueryPos qPos = QueryPos.getInstance(q);
			
			qPos.add(actionUserId);
			qPos.add(actionUserId);
			qPos.add(actionUserId);

			return (List<ProcessOrder>) QueryUtil.list(q, getDialect(), start,
					end);

		} catch (Exception e) {
			throw new SystemException();
		} finally {
			session.close();
		}
	}

	public int countReAssigToUser(long actionUserId) throws SystemException {
		Session session = null;
		try {
			session = openSession();
			String sql = CustomSQLUtil.get(SQL_RE_ASSIGN_COUNT);

			SQLQuery q = session.createSQLQuery(sql);

			q.setCacheable(false);

			q.addScalar(COUNT_COLUMN_NAME, Type.INTEGER);

			QueryPos qPos = QueryPos.getInstance(q);
			
			qPos.add(actionUserId);
			qPos.add(actionUserId);
			qPos.add(actionUserId);
			
			Iterator<Integer> itr = q.iterate();

			if (itr.hasNext()) {
				Integer count = itr.next();

				if (count != null) {
					return count.intValue();
				}
			}

			return 0;

		} catch (Exception e) {
			throw new SystemException();
		} finally {
			session.close();
		}
	}	
	/**
	 * @param serviceInfoId
	 * @param processStepId
	 * @param loginUserId
	 * @param assignToUserId
	 * @param keyWords
	 * @return
	 */
	public int countProcessOrderKeyWords(
		long serviceInfoId, long processStepId, long loginUserId,
		long assignToUserId, String keyWords, String dossierSubStatus, 
		String processOrderStage, Date fromDate, Date toDate, String domainCode) {
		
		Timestamp fromDate_TS = null;
		Timestamp toDate_TS = null;
		if (Validator.isNotNull(fromDate)){
			fromDate_TS = CalendarUtil.getTimestamp(fromDate);
		}
		if (Validator.isNotNull(toDate)){
			toDate_TS = CalendarUtil.getTimestamp(toDate);
		}

		Session session = null;
		try {
			session = openSession();

			String sql = CustomSQLUtil
				.get(SQL_PROCESS_ORDER_COUNT_KEY_WORDS);

			if (serviceInfoId <= 0) {
				sql = StringUtil
					.replace(sql, "AND opencps_processorder.serviceInfoId = ?",
						StringPool.BLANK);
			}

			if (processStepId <= 0) {
				sql = StringUtil
					.replace(sql, "AND opencps_processstep.processStepId = ?",
						StringPool.BLANK);
			}

			if(Validator.isNull(keyWords)){
				sql = StringUtil
						.replace(sql, "AND (opencps_dossier.receptionNo = ? or opencps_serviceinfo.serviceName like ? or opencps_dossier.subjectName like ? or opencps_dossier.dossierId = ?)",
							StringPool.BLANK);
			}
			
			if(Validator.isNull(dossierSubStatus)){
				sql = StringUtil
						.replace(sql, "AND opencps_processstep.dossierSubStatus = ?",
							StringPool.BLANK);
			}
			
			if(Boolean.valueOf(processOrderStage)){
				sql = StringUtil
						.replace(sql, "OR opencps_processorder.assignToUserId = ?",
							"OR opencps_processorder.assignToUserId = ? OR 1=1");
			}
			
			if (Validator.isNull(fromDate_TS) && Validator.isNull(toDate_TS)) {
				sql = StringUtil
						.replace(sql, "AND (opencps_dossier.submitDatetime BETWEEN ? AND ?)",
							StringPool.BLANK);
			}
			if(Validator.isNull(domainCode)){
				sql = StringUtil
						.replace(sql, "AND opencps_serviceinfo.domainCode= ?", StringPool.BLANK);
			}
			
			SQLQuery q = session
				.createSQLQuery(sql);
			q
				.setCacheable(false);

			q
				.addScalar(COUNT_COLUMN_NAME, Type.INTEGER);

			QueryPos qPos = QueryPos
				.getInstance(q);

			if (serviceInfoId > 0) {
				qPos
					.add(serviceInfoId);
			}

			if (processStepId > 0) {
				qPos
					.add(processStepId);
			}

			qPos
				.add(loginUserId);

			qPos
				.add(Boolean.valueOf(processOrderStage));
			
			qPos
				.add(loginUserId);
			
			if(Validator.isNotNull(dossierSubStatus)){
				qPos
					.add(dossierSubStatus);
			}
			
			if(Validator.isNotNull(keyWords)){
				qPos
					.add(keyWords);
				qPos
					.add(StringPool.PERCENT+keyWords+StringPool.PERCENT);
				qPos
					.add(StringPool.PERCENT+keyWords+StringPool.PERCENT);
				qPos
					.add(keyWords);
			}
			
			if (Validator.isNotNull(fromDate_TS) && Validator.isNotNull(toDate_TS)) {
				qPos.add(fromDate_TS);
				qPos.add(toDate_TS);
			}
			if(Validator.isNotNull(domainCode)){
				qPos
					.add(domainCode);
			}
			Iterator<Integer> itr = q
				.iterate();

			if (itr
				.hasNext()) {
				Integer count = itr
					.next();

				if (count != null) {
					return count
						.intValue();
				}
			}

			return 0;
		}
		catch (Exception e) {
			_log
				.error(e);
		}
		finally {
			closeSession(session);
		}

		return 0;

	}

	/**
	 * @param serviceInfoId
	 * @param processStepId
	 * @param loginUserId
	 * @param assignToUserId
	 * @param keyWords
	 * @param start
	 * @param end
	 * @param orderByComparator
	 * @return
	 */
	public List searchProcessOrderKeyWords(
			long serviceInfoId, long processStepId, long loginUserId, 
			long assignToUserId, String keyWords, String dossierSubStatus, 
			String processOrderStage, Date fromDate, Date toDate, String domainCode,
			int start, int end, OrderByComparator orderByComparator) {

		Timestamp fromDate_TS = null;
		Timestamp toDate_TS = null;
		if (Validator.isNotNull(fromDate)){
			fromDate_TS = CalendarUtil.getTimestamp(fromDate);
		}
		if (Validator.isNotNull(toDate)){
			toDate_TS = CalendarUtil.getTimestamp(toDate);
		}
		
		Session session = null;
		try {
			session = openSession();

			String sql = CustomSQLUtil
				.get(SQL_PROCESS_ORDER_FINDER_KEY_WORDS);

			if (serviceInfoId <= 0) {
				sql = StringUtil
					.replace(sql, "AND opencps_processorder.serviceInfoId = ?",
						StringPool.BLANK);
			}

			if (processStepId <= 0) {
				sql = StringUtil
					.replace(sql, "AND opencps_processstep.processStepId = ?",
						StringPool.BLANK);
			}
			
			if(Validator.isNull(keyWords)){
				sql = StringUtil
						.replace(sql, "AND (opencps_dossier.receptionNo = ? or opencps_serviceinfo.serviceName like ? or opencps_dossier.subjectName like ? or opencps_dossier.dossierId = ?)",
							StringPool.BLANK);
			}
			
			if(Validator.isNull(dossierSubStatus)){
				sql = StringUtil
						.replace(sql, "AND opencps_processstep.dossierSubStatus = ?",
							StringPool.BLANK);
			}
			
			if(Boolean.valueOf(processOrderStage)){
				sql = StringUtil
						.replace(sql, "OR opencps_processorder.assignToUserId = ?",
							"OR opencps_processorder.assignToUserId = ? OR 1=1");
			}
			
			if (Validator.isNull(fromDate_TS) && Validator.isNull(toDate_TS)) {
				sql = StringUtil
						.replace(sql, "AND (opencps_dossier.submitDatetime BETWEEN ? AND ?)",
							StringPool.BLANK);
			}
			
			if(Validator.isNull(domainCode)){
				sql = StringUtil
						.replace(sql, "AND opencps_serviceinfo.domainCode= ?", StringPool.BLANK);
			}
			
			sql = CustomSQLUtil.replaceOrderBy(sql, orderByComparator);
			
			SQLQuery q = session
				.createSQLQuery(sql);

			q
				.setCacheable(false);

			q
				.addEntity("ProcessOrder", ProcessOrderImpl.class);

			q
				.addScalar("serviceConfigId", Type.LONG);
			q
				.addScalar("subjectId", Type.STRING);
			q
				.addScalar("subjectName", Type.STRING);
			q
				.addScalar("receptionNo", Type.STRING);
			q
				.addScalar("serviceName", Type.STRING);
			q
				.addScalar("stepName", Type.STRING);
			q
				.addScalar("sequenceNo", Type.STRING);
			q
				.addScalar("daysDuration", Type.INTEGER);
			q
				.addScalar("referenceDossierPartId", Type.LONG);
			q
				.addScalar("readOnly", Type.BOOLEAN);
			
			q
				.addScalar("dossierSubStatus", Type.STRING);
			
			QueryPos qPos = QueryPos
				.getInstance(q);

			if (serviceInfoId > 0) {
				qPos
					.add(serviceInfoId);
			}

			if (processStepId > 0) {
				qPos
					.add(processStepId);
			}

			qPos
				.add(loginUserId);

			qPos
				.add(Boolean.valueOf(processOrderStage));
			
			qPos
				.add(loginUserId);
			
			if(Validator.isNotNull(dossierSubStatus)){
				qPos
					.add(dossierSubStatus);
			}
			
			if(Validator.isNotNull(keyWords)){
				qPos
					.add(keyWords);
				qPos
					.add(StringPool.PERCENT+keyWords+StringPool.PERCENT);
				qPos
					.add(StringPool.PERCENT+keyWords+StringPool.PERCENT);
				qPos
					.add(keyWords);
			}
			
			if (Validator.isNotNull(fromDate_TS) && Validator.isNotNull(toDate_TS)) {
				qPos.add(fromDate_TS);
				qPos.add(toDate_TS);
			}
			
			if(Validator.isNotNull(domainCode)){
				qPos
					.add(domainCode);
			}
			
			Iterator<Object[]> itr = (Iterator<Object[]>) QueryUtil
				.list(q, getDialect(), start, end).iterator();

			List<ProcessOrderBean> processOrderBeans =
				new ArrayList<ProcessOrderBean>();

			if (itr
				.hasNext()) {
				while (itr
					.hasNext()) {
					ProcessOrderBean processOrderBean = new ProcessOrderBean();

					Object[] objects = itr
						.next();

					ProcessOrder processOrder = (ProcessOrder) objects[0];

					long serviceConfigId = GetterUtil
						.getLong(objects[1]);
					String subjectId = (String) objects[2];
					String subjectName = (String) objects[3];
					String receptionNo = (String) objects[4];
					String serviceName = (String) objects[5];
					String stepName = (String) objects[6];
					String sequenceNo = (String) objects[7];
					String dossierSubStatusSos = (String) objects[11];
					int daysDuration = GetterUtil
						.getInteger(objects[8]);
					long referenceDossierPartId = GetterUtil
						.getLong(objects[9]);

					boolean readOnly = GetterUtil
						.getBoolean(objects[10]);

					processOrderBean
						.setActionDatetime(processOrder
							.getActionDatetime());
					processOrderBean
						.setActionUserId(processOrder
							.getActionUserId());
					processOrderBean
						.setAssignToUserId(processOrder
							.getAssignToUserId());
					// processOrderBean.setAssignToUserName(assignToUserName);
					processOrderBean
						.setCompanyId(processOrder
							.getCompanyId());
					processOrderBean
						.setDaysDuration(daysDuration);
					// processOrderBean.setDealine(dealine);
					processOrderBean
						.setDossierId(processOrder
							.getDossierId());
					processOrderBean
						.setDossierStatus(processOrder
							.getDossierStatus());
					processOrderBean
						.setDossierTemplateId(processOrder
							.getDossierTemplateId());
					processOrderBean
						.setFileGroupId(processOrder
							.getFileGroupId());
					processOrderBean
						.setGovAgencyCode(processOrder
							.getGovAgencyCode());
					processOrderBean
						.setGovAgencyName(processOrder
							.getGovAgencyName());
					processOrderBean
						.setGovAgencyOrganizationId(processOrder
							.getGovAgencyOrganizationId());
					processOrderBean
						.setGroupId(processOrder
							.getGroupId());
					processOrderBean
						.setProcessOrderId(processOrder
							.getProcessOrderId());
					processOrderBean
						.setProcessStepId(processStepId);
					processOrderBean
						.setReceptionNo(receptionNo);
					processOrderBean
						.setReferenceDossierPartId(referenceDossierPartId);
					processOrderBean
						.setSequenceNo(sequenceNo);
					processOrderBean
						.setServiceConfigId(serviceConfigId);
					processOrderBean
						.setServiceInfoId(processOrder
							.getServiceInfoId());
					processOrderBean
						.setServiceName(serviceName);
					processOrderBean
						.setServiceProcessId(processOrder
							.getServiceProcessId());
					processOrderBean
						.setStepName(stepName);
					processOrderBean
						.setSubjectId(subjectId);
					processOrderBean
						.setSubjectName(subjectName);
					processOrderBean
						.setUserId(processOrder
							.getUserId());
					processOrderBean
						.setReadOnly(readOnly);
					
					processOrderBean.setDossierSubStatus(dossierSubStatusSos);
					
					processOrderBeans
						.add(processOrderBean);
				}
			}

			return processOrderBeans;
		}
		catch (Exception e) {
			_log
				.error(e);
		}
		finally {
			closeSession(session);
		}

		return null;

	}
}
