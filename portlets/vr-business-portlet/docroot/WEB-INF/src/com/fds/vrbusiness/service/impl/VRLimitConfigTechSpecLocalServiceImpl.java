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

package com.fds.vrbusiness.service.impl;

import java.util.List;

import com.fds.vrbusiness.model.VRLimitConfigTechSpec;
import com.fds.vrbusiness.service.base.VRLimitConfigTechSpecLocalServiceBaseImpl;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

/**
 * The implementation of the v r limit config tech spec local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.fds.vrbusiness.service.VRLimitConfigTechSpecLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author khoavd
 * @see com.fds.vrbusiness.service.base.VRLimitConfigTechSpecLocalServiceBaseImpl
 * @see com.fds.vrbusiness.service.VRLimitConfigTechSpecLocalServiceUtil
 */
public class VRLimitConfigTechSpecLocalServiceImpl
	extends VRLimitConfigTechSpecLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this interface directly. Always use {@link com.fds.vrbusiness.service.VRLimitConfigTechSpecLocalServiceUtil} to access the v r limit config tech spec local service.
	 */
	
	public List<VRLimitConfigTechSpec> getLimitConfigs(String vehicleType,
			String markupSMRM, long searchingDriveConfig)
			throws PortalException, SystemException {
		return vrLimitConfigTechSpecPersistence.findByVT_MU_DC(vehicleType, markupSMRM, searchingDriveConfig);
	}
}