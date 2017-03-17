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

import org.opencps.postal.model.PostOfficeMapping;
import org.opencps.postal.service.base.PostOfficeMappingLocalServiceBaseImpl;

import com.liferay.portal.kernel.exception.SystemException;

/**
 * The implementation of the post office mapping local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link org.opencps.postal.service.PostOfficeMappingLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author nhanhlt
 * @see org.opencps.postal.service.base.PostOfficeMappingLocalServiceBaseImpl
 * @see org.opencps.postal.service.PostOfficeMappingLocalServiceUtil
 */
public class PostOfficeMappingLocalServiceImpl
	extends PostOfficeMappingLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this interface directly. Always use {@link org.opencps.postal.service.PostOfficeMappingLocalServiceUtil} to access the post office mapping local service.
	 */
	
	public PostOfficeMapping getMappingBy(String opencpsCityCode)
			throws SystemException {

		return postOfficeMappingPersistence
				.fetchByOpencpsCityCode(opencpsCityCode);

	}
}