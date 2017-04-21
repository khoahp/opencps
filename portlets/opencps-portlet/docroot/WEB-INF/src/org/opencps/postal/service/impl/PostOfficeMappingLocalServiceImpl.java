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

import java.util.Date;

import org.opencps.postal.model.PostOfficeMapping;
import org.opencps.postal.service.base.PostOfficeMappingLocalServiceBaseImpl;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.Validator;

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
	
	public PostOfficeMapping getMappingByPostOfficeCode(String postOfficeCode)
			throws SystemException {

		return postOfficeMappingPersistence.fetchByPostOfficeCode(postOfficeCode);

	}

	public PostOfficeMapping updatePostOffce(String postOfficeCode,
			String postOfficeName, String opencpsCityCode)
			throws SystemException {

		PostOfficeMapping postOfficeMapping = null;

		postOfficeMapping = postOfficeMappingPersistence
				.fetchByPostOfficeCode(postOfficeCode);

		long postOfficeMappingId = 0;

		if (Validator.isNull(postOfficeMapping)) {

			postOfficeMappingId = counterLocalService
					.increment(PostOfficeMapping.class.getName());

			postOfficeMapping.setPostOfficeMappingId(postOfficeMappingId);
			
			postOfficeMapping.setCreateDate(new Date());

		}
		
		postOfficeMapping.setModifiedDate(new Date());

		postOfficeMapping.setPostOfficeCode(postOfficeCode);
		postOfficeMapping.setPostOfficeName(postOfficeName);
		postOfficeMapping.setOpencpsCityCode(opencpsCityCode);

		postOfficeMappingPersistence.update(postOfficeMapping);

		return postOfficeMapping;

	}
}