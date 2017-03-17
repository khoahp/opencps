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

package org.opencps.postal.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.opencps.postal.NoSuchPostalConfigException;
import org.opencps.postal.model.PostalConfig;
import org.opencps.postal.service.base.PostalConfigLocalServiceBaseImpl;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.Validator;

/**
 * The implementation of the postal config local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are
 * added, rerun ServiceBuilder to copy their definitions into the
 * {@link org.opencps.postal.service.PostalConfigLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security
 * checks based on the propagated JAAS credentials because this service can only
 * be accessed from within the same VM.
 * </p>
 *
 * @author nhanhlt
 * @see org.opencps.postal.service.base.PostalConfigLocalServiceBaseImpl
 * @see org.opencps.postal.service.PostalConfigLocalServiceUtil
 */
public class PostalConfigLocalServiceImpl extends
		PostalConfigLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 * 
	 * Never reference this interface directly. Always use {@link
	 * org.opencps.postal.service.PostalConfigLocalServiceUtil} to access the
	 * postal config local service.
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opencps.postal.service.PostalConfigLocalService#getPostalConfigBy
	 * (long, java.lang.String, boolean)
	 */
	public PostalConfig getPostalConfigBy(long govAgencyOrganizationId,
			String postalGateType, boolean status)
			throws NoSuchPostalConfigException, SystemException {

		return postalConfigPersistence.findByO_T_S(govAgencyOrganizationId,
				postalGateType, status);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opencps.postal.service.PostalConfigLocalService#getPostalConfigBy
	 * (long, boolean)
	 */
	public PostalConfig getPostalConfigBy(long govAgencyOrganizationId,
			boolean status) throws NoSuchPostalConfigException, SystemException {

		return postalConfigPersistence.findByO_S(govAgencyOrganizationId,
				status);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opencps.postal.service.PostalConfigLocalService#getPostalConfigByOrgId
	 * (long)
	 */
	public List<PostalConfig> getPostalConfigByOrgId(
			long govAgencyOrganizationId) {

		try {
			return postalConfigPersistence.findByOrgId(govAgencyOrganizationId);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new ArrayList<PostalConfig>();
	}

	/**
	 * @param postalConfigId
	 * @param govAgencyOrganizationId
	 * @param postalCustomerCode
	 * @param postalTokenCode
	 * @param postalDomain
	 * @param postalGateType
	 * @param status
	 * @return
	 * @throws SystemException
	 */
	public PostalConfig updateConfig(long postalConfigId,
			long govAgencyOrganizationId, String postalCustomerCode,
			String postalTokenCode, String postalDomain, String postalGateType,
			boolean status) throws SystemException {

		PostalConfig postalConfig = null;

		try {

			if (postalConfigId > 0) {
				postalConfig = postalConfigPersistence
						.fetchByPrimaryKey(postalConfigId);
			}

			if (Validator.isNull(postalConfig)) {

				postalConfigId = counterLocalService
						.increment(PostalConfig.class.getName());

				postalConfig = postalConfigPersistence.create(postalConfigId);

				postalConfig.setCreateDate(new Date());

			}

			postalConfig.setModifiedDate(new Date());

			postalConfig.setGovAgencyOrganizationId(govAgencyOrganizationId);
			postalConfig.setPostalCustomerCode(postalCustomerCode);
			postalConfig.setPostalTokenCode(postalTokenCode);
			postalConfig.setPostalDomain(postalDomain);
			postalConfig.setPostalGateType(postalGateType);
			postalConfig.setStatus(status);
		} catch (Exception e) {

		}

		return updatePostalConfig(postalConfig);
	}
	
	public PostalConfig getPostalConfigBy(
			long govAgencyOrganizationId,String postalGateType) {

		try {
			return postalConfigPersistence.findByO_T(govAgencyOrganizationId, postalGateType);
		} catch (NoSuchPostalConfigException | SystemException e) {
			
		}

		return null;
	}
}