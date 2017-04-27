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
import java.util.List;

import org.opencps.dossiermgt.NoSuchServiceOptionException;
import org.opencps.dossiermgt.model.ServiceOption;
import org.opencps.dossiermgt.service.base.ServiceOptionLocalServiceBaseImpl;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.service.ServiceContext;

/**
 * The implementation of the service option local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are
 * added, rerun ServiceBuilder to copy their definitions into the
 * {@link org.opencps.dossiermgt.service.ServiceOptionLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security
 * checks based on the propagated JAAS credentials because this service can only
 * be accessed from within the same VM.
 * </p>
 *
 * @author trungnt
 * @see org.opencps.dossiermgt.service.base.ServiceOptionLocalServiceBaseImpl
 * @see org.opencps.dossiermgt.service.ServiceOptionLocalServiceUtil
 */
/**
 * @author khoavu
 *
 */
/**
 * @author khoavu
 *
 */
/**
 * @author khoavu
 *
 */
public class ServiceOptionLocalServiceImpl extends
		ServiceOptionLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 * 
	 * Never reference this interface directly. Always use {@link
	 * org.opencps.dossiermgt.service.ServiceOptionLocalServiceUtil} to access
	 * the service option local service.
	 */

	/**
	 * @param groupId
	 * @param serviceConfigId
	 * @param dossierTemplateId
	 * @return
	 * @throws NoSuchServiceOptionException
	 * @throws SystemException
	 */
	public ServiceOption getServiceOptionByG_SCID_DTID(long groupId,
			long serviceConfigId, long dossierTemplateId)
			throws NoSuchServiceOptionException, SystemException {
		return serviceOptionPersistence.findByG_SCID_DTID(groupId,
				serviceConfigId, dossierTemplateId);
	}

	/**
	 * @param serviceConfigId
	 * @param optionCode
	 * @param optionName
	 * @param optionOrder
	 * @param autoSelect
	 * @param dossierTemplateId
	 * @param serviceProcessId
	 * @param context
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 */
	public ServiceOption addServiceOption(long serviceConfigId,
			String optionCode, String optionName, int optionOrder,
			String autoSelect, long dossierTemplateId, long serviceProcessId,
			ServiceContext context) throws PortalException, SystemException {

		long serviceOptionId = counterLocalService
				.increment(ServiceOption.class.getName());

		ServiceOption so = serviceOptionPersistence.create(serviceOptionId);

		Date now = new Date();

		so.setCreateDate(now);
		so.setModifiedDate(now);

		so.setUserId(context.getUserId());
		so.setGroupId(context.getScopeGroupId());
		so.setCompanyId(context.getCompanyId());

		so.setOptionCode(optionCode);
		so.setOptionName(optionName);
		so.setOptionOrder(optionOrder);
		so.setAutoSelect(autoSelect);
		so.setServiceConfigId(serviceConfigId);
		so.setDossierTemplateId(dossierTemplateId);

		serviceOptionPersistence.update(so);

		return so;

	}

	/**
	 * @param serviceOptionId
	 * @param serviceConfigId
	 * @param dossierTemplateId
	 * @param optionCode
	 * @param optionName
	 * @param optionOrder
	 * @param autoSelect
	 * @param context
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 */
	public ServiceOption updateServiceOption(long serviceOptionId,
			long serviceConfigId, long dossierTemplateId, String optionCode,
			String optionName, int optionOrder, String autoSelect,
			ServiceContext context) throws PortalException, SystemException {

		ServiceOption so = serviceOptionPersistence
				.fetchByPrimaryKey(serviceOptionId);

		if (Validator.isNotNull(so)
				&& so.getServiceConfigId() == serviceConfigId
				&& so.getDossierTemplateId() == dossierTemplateId) {

			so.setModifiedDate(new Date());
			so.setOptionCode(optionCode);
			so.setOptionName(optionName);
			so.setOptionOrder(optionOrder);
			so.setAutoSelect(autoSelect);

		} else {
			
			serviceOptionId = counterLocalService
					.increment(ServiceOption.class.getName());

			so = serviceOptionPersistence.create(serviceOptionId);

			Date now = new Date();

			so.setCreateDate(now);
			so.setModifiedDate(now);

			so.setUserId(context.getUserId());
			so.setGroupId(context.getScopeGroupId());
			so.setCompanyId(context.getCompanyId());

			so.setOptionCode(optionCode);
			so.setOptionName(optionName);
			so.setOptionOrder(optionOrder);
			so.setAutoSelect(autoSelect);
			so.setServiceConfigId(serviceConfigId);
			so.setDossierTemplateId(dossierTemplateId);

		}
		serviceOptionPersistence.update(so);

		return so;
	}

	/**
	 * @param serviceOptionId
	 * @param serviceProcessId
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 */
	public ServiceOption updateServiceProcess(long serviceOptionId,
			long serviceProcessId) throws PortalException, SystemException {
		
		ServiceOption so = serviceOptionPersistence
				.fetchByPrimaryKey(serviceOptionId);

		if (Validator.isNotNull(so)) {

			so.setModifiedDate(new Date());
			so.setServiceProcessId(serviceProcessId);

			serviceOptionPersistence.update(so);
		}

		return so;

		
	}
	
	/**
	 * @param serviceInfoId
	 * @param govAgencyCode
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 */
	public List<ServiceOption> searchServiceOption(long serviceInfoId,
			String govAgencyCode) throws PortalException, SystemException {
		return serviceOptionFinder.searchServiceOption(serviceInfoId,
				govAgencyCode);
	}
}