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

import org.opencps.statisticsmgt.NoSuchGovagencyLevelException;
import org.opencps.statisticsmgt.model.GovagencyLevel;
import org.opencps.statisticsmgt.service.base.GovagencyLevelLocalServiceBaseImpl;

import com.liferay.portal.kernel.exception.SystemException;

/**
 * The implementation of the govagency level local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are
 * added, rerun ServiceBuilder to copy their definitions into the
 * {@link org.opencps.statisticsmgt.service.GovagencyLevelLocalService}
 * interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security
 * checks based on the propagated JAAS credentials because this service can only
 * be accessed from within the same VM.
 * </p>
 *
 * @author trungnt
 * @see org.opencps.statisticsmgt.service.base.GovagencyLevelLocalServiceBaseImpl
 * @see org.opencps.statisticsmgt.service.GovagencyLevelLocalServiceUtil
 */
public class GovagencyLevelLocalServiceImpl extends
		GovagencyLevelLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 * 
	 * Never reference this interface directly. Always use {@link
	 * org.opencps.statisticsmgt.service.GovagencyLevelLocalServiceUtil} to
	 * access the govagency level local service.
	 */

	public GovagencyLevel addGovagencyLevel(long companyId, long groupId,
			long userId, String govAgencyCode, int administrationLevel)
			throws SystemException {
		long govagencyLevelId = counterLocalService
				.increment(GovagencyLevel.class.getName());

		Date now = new Date();
		GovagencyLevel govagencyLevel = govagencyLevelPersistence
				.create(govagencyLevelId);
		govagencyLevel.setAdministrationLevel(administrationLevel);
		govagencyLevel.setGovAgencyCode(govAgencyCode);
		govagencyLevel.setCompanyId(companyId);
		govagencyLevel.setCreateDate(now);
		govagencyLevel.setGroupId(groupId);
		govagencyLevel.setModifiedDate(now);
		govagencyLevel.setUserId(userId);
		return govagencyLevelPersistence.update(govagencyLevel);
	}

	public GovagencyLevel updateGovagencyLevel(long companyId, long groupId,
			long userId, String govAgencyCode, int administrationLevel)
			throws SystemException {
		GovagencyLevel govagencyLevel = null;
		try {
			govagencyLevel = govagencyLevelPersistence.findByG_GC(groupId,
					govAgencyCode);
		} catch (Exception e) {
		
		}
		Date now = new Date();
		if (govagencyLevel == null) {
			long govagencyLevelId = counterLocalService
					.increment(GovagencyLevel.class.getName());
			govagencyLevel = govagencyLevelPersistence.create(govagencyLevelId);

			govagencyLevel.setGovAgencyCode(govAgencyCode);
			govagencyLevel.setCompanyId(companyId);
			govagencyLevel.setCreateDate(now);
			govagencyLevel.setGroupId(groupId);

			govagencyLevel.setUserId(userId);
		}
		govagencyLevel.setModifiedDate(now);
		govagencyLevel.setAdministrationLevel(administrationLevel);

		return govagencyLevelPersistence.update(govagencyLevel);
	}
	
	/**
	 * @param groupId
	 * @param govAgencyCode
	 * @return
	 * @throws NoSuchGovagencyLevelException
	 * @throws SystemException
	 */
	public GovagencyLevel getGovagencyLevelByG_GC(long groupId, String govAgencyCode)
			throws NoSuchGovagencyLevelException, SystemException{
		return govagencyLevelPersistence.findByG_GC(groupId, govAgencyCode);
	}
}