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

package org.opencps.dossiermgt.service.impl;

import java.util.Date;

import org.opencps.dossiermgt.model.BusinessRegister;
import org.opencps.dossiermgt.service.base.BusinessRegisterLocalServiceBaseImpl;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.service.ServiceContext;

/**
 * The implementation of the business register local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are
 * added, rerun ServiceBuilder to copy their definitions into the
 * {@link org.opencps.dossiermgt.service.BusinessRegisterLocalService}
 * interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security
 * checks based on the propagated JAAS credentials because this service can only
 * be accessed from within the same VM.
 * </p>
 *
 * @author khoavd
 * @see org.opencps.dossiermgt.service.base.BusinessRegisterLocalServiceBaseImpl
 * @see org.opencps.dossiermgt.service.BusinessRegisterLocalServiceUtil
 */
public class BusinessRegisterLocalServiceImpl extends
		BusinessRegisterLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 * 
	 * Never reference this interface directly. Always use {@link
	 * org.opencps.dossiermgt.service.BusinessRegisterLocalServiceUtil} to
	 * access the business register local service.
	 */

	/**
	 * @param ownerOrganizationId
	 * @param dossierTemplateId
	 * @param serviceProcessId
	 * @param dossierId
	 * @param validStatus
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 */
	public BusinessRegister addBusinessRegister(long ownerOrganizationId,
			long dossierTemplateId, long serviceProcessId, long dossierId,
			int validStatus, ServiceContext context) throws PortalException,
			SystemException {

		BusinessRegister businessRegister = null;

		long businessRegisterId = counterLocalService.increment();

		businessRegister = businessRegisterPersistence
				.create(businessRegisterId);

		Date now = new Date();

		long userId = context.getUserId();
		long groupId = context.getScopeGroupId();
		long companyId = context.getCompanyId();

		businessRegister.setCompanyId(companyId);
		businessRegister.setGroupId(groupId);
		businessRegister.setUserId(userId);
		businessRegister.setCreateDate(now);
		businessRegister.setCompanyId(companyId);

		businessRegister.setOwnerOrganizationId(ownerOrganizationId);
		businessRegister.setDossierTemplateId(dossierTemplateId);
		businessRegister.setServiceProcessId(serviceProcessId);
		businessRegister.setDossierId(dossierId);
		businessRegister.setValidStatus(validStatus);

		businessRegisterPersistence.update(businessRegister);

		return businessRegister;
	}

	public BusinessRegister getBusinessRegisterByOrgId(long ownerOrganizationId)
			throws PortalException, SystemException {
		return businessRegisterPersistence.findByOOI(ownerOrganizationId);
	}

}