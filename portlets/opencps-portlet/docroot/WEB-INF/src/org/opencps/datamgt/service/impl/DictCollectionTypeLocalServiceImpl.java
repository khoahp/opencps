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

package org.opencps.datamgt.service.impl;

import java.util.Date;
import java.util.List;

import org.opencps.datamgt.NoSuchDictCollectionTypeException;
import org.opencps.datamgt.model.DictCollection;
import org.opencps.datamgt.model.DictCollectionType;
import org.opencps.datamgt.service.base.DictCollectionTypeLocalServiceBaseImpl;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.service.ServiceContext;

/**
 * The implementation of the dict collection type local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are
 * added, rerun ServiceBuilder to copy their definitions into the
 * {@link org.opencps.datamgt.service.DictCollectionTypeLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security
 * checks based on the propagated JAAS credentials because this service can only
 * be accessed from within the same VM.
 * </p>
 *
 * @author khoavd
 * @see org.opencps.datamgt.service.base.DictCollectionTypeLocalServiceBaseImpl
 * @see org.opencps.datamgt.service.DictCollectionTypeLocalServiceUtil
 */
public class DictCollectionTypeLocalServiceImpl extends
		DictCollectionTypeLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 * 
	 * Never reference this interface directly. Always use {@link
	 * org.opencps.datamgt.service.DictCollectionTypeLocalServiceUtil} to access
	 * the dict collection type local service.
	 */
	public DictCollectionType addDictCollectionType(long dictCollectionId,
			long dictCollectionLinkedId, long sequenceNo,
			ServiceContext serviceContext) throws SystemException {
		long dictCollectionTypeId = counterLocalService
				.increment(DictCollectionType.class.getName());
		DictCollectionType dictCollectionType = dictCollectionTypePersistence
				.create(dictCollectionTypeId);

		DictCollection collection = dictCollectionPersistence
				.fetchByPrimaryKey(dictCollectionLinkedId);

		dictCollectionType.setCompanyId(serviceContext.getCompanyId());
		dictCollectionType.setGroupId(serviceContext.getScopeGroupId());
		dictCollectionType.setUserId(serviceContext.getUserId());
		dictCollectionType.setCreateDate(new Date());
		dictCollectionType.setModifiedDate(new Date());

		dictCollectionType.setDictCollectionId(dictCollectionId);
		dictCollectionType.setDictCollectionLinkedId(dictCollectionLinkedId);
		dictCollectionType.setSequenceNo(sequenceNo);
		dictCollectionType.setDictCollectionLinkedName(collection
				.getCollectionName());

		return dictCollectionTypePersistence.update(dictCollectionType);
	}

	public List<DictCollectionType> getByDictCollectionId(long dictCollectionId)
			throws SystemException {
		return dictCollectionTypePersistence
				.findByDictCollectionId(dictCollectionId);
	}

	public DictCollectionType deleteDictCollectionType(long dictCollectionLinkId)
			throws SystemException, NoSuchDictCollectionTypeException {
		return dictCollectionTypePersistence.remove(dictCollectionLinkId);
	}
}