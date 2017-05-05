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

package org.opencps.integrate.dao.service.impl;

import java.util.Date;

import org.opencps.integrate.dao.model.IntegrateAPI;
import org.opencps.integrate.dao.service.base.IntegrateAPILocalServiceBaseImpl;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.CompanyConstants;
import com.liferay.portal.model.User;

/**
 * The implementation of the integrate a p i local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are
 * added, rerun ServiceBuilder to copy their definitions into the
 * {@link org.opencps.integrate.dao.service.IntegrateAPILocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security
 * checks based on the propagated JAAS credentials because this service can only
 * be accessed from within the same VM.
 * </p>
 *
 * @author khoavd
 * @see org.opencps.integrate.dao.service.base.IntegrateAPILocalServiceBaseImpl
 * @see org.opencps.integrate.dao.service.IntegrateAPILocalServiceUtil
 */
public class IntegrateAPILocalServiceImpl extends
		IntegrateAPILocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 * 
	 * Never reference this interface directly. Always use {@link
	 * org.opencps.integrate.dao.service.IntegrateAPILocalServiceUtil} to access
	 * the integrate a p i local service.
	 */

	/**
	 * @param userId
	 * @param agency
	 * @param apiKey
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 */
	public IntegrateAPI addAPIKey(long userId, String agency, String apiKey)
			throws PortalException, SystemException {
		
		Date now = new Date();
		
		long apiId = counterLocalService
				.increment(IntegrateAPI.class.getName());
		
		IntegrateAPI api = integrateAPIPersistence.create(apiId);
		
		api.setUserId(userId);
		api.getAgency();
		api.setApiKey(apiKey);
		api.setCreateDate(now);
		api.setModifiedDate(now);
		
		integrateAPIPersistence.update(api);
		
		return api;
	}
	

	public IntegrateAPI getAPIByUserId(long userId) throws PortalException,
			SystemException {
		return integrateAPIPersistence.fetchByuserName(userId);
	}	

	public IntegrateAPI getAPIByAPIKey(String apiKey) throws PortalException,
			SystemException {
		return integrateAPIPersistence.fetchByapiKey(apiKey);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opencps.integrate.dao.service.IntegrateAPILocalService#basicLogin
	 * (long, java.lang.String, java.lang.String)
	 */
	public User basicLogin(long companyId, String username, String password)
			throws PortalException, SystemException {

		User user = null;

		long userId = userLocalService.authenticateForBasic(companyId,
				CompanyConstants.AUTH_TYPE_EA, username, password);

		if (userId != 0) {
			user = userLocalService.getUserById(userId);
		}

		return user;

	}

}