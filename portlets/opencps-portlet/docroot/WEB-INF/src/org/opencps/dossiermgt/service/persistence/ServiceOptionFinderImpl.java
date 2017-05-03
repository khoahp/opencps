package org.opencps.dossiermgt.service.persistence;

import java.util.List;

import org.opencps.dossiermgt.model.DossierFile;
import org.opencps.dossiermgt.model.ServiceOption;
import org.opencps.dossiermgt.model.impl.DossierFileImpl;
import org.opencps.dossiermgt.model.impl.ServiceOptionImpl;

import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.service.persistence.impl.BasePersistenceImpl;
import com.liferay.util.dao.orm.CustomSQLUtil;

public class ServiceOptionFinderImpl extends BasePersistenceImpl<ServiceOption>
		implements ServiceOptionFinder {
	
	public static final String SEARCH_SERVICE_OPTION_SQL = ServiceOptionFinder.class
			.getName() + ".searchServiceOption";
	
	public List<ServiceOption> searchServiceOption(long serviceInfoId,
			String govAgencyCode) throws SystemException {

		Session session = null;
		try {

			session = openSession();

			String sql = CustomSQLUtil.get(SEARCH_SERVICE_OPTION_SQL);

			if (serviceInfoId == 0) {
				sql = StringUtil.replace(sql, "AND serviceInfoId = ?",
						StringPool.BLANK);
			}

			if (Validator.isNull(govAgencyCode)) {
				sql = StringUtil.replace(sql, "AND govAgencyCode = ?",
						StringPool.BLANK);
			}

			SQLQuery q = session.createSQLQuery(sql);

			q.addEntity("ServiceOption", ServiceOptionImpl.class);

			QueryPos qPos = QueryPos.getInstance(q);

			if (serviceInfoId != 0) {
				qPos.add(serviceInfoId);
			}

			if (Validator.isNotNull(govAgencyCode)) {
				qPos.add(govAgencyCode);
			}

			return (List<ServiceOption>) QueryUtil.list(q, getDialect(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		} catch (Exception e) {
			throw new SystemException();
		} finally {
			closeSession(session);
		}
	}
	
}
