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

import org.opencps.datamgt.NoSuchDictCollectionLinkException;
import org.opencps.datamgt.model.DictCollection;
import org.opencps.datamgt.model.DictCollectionLink;
import org.opencps.datamgt.service.base.DictCollectionLinkLocalServiceBaseImpl;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.service.ServiceContext;

/**
 * The implementation of the dict collection link local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are
 * added, rerun ServiceBuilder to copy their definitions into the
 * {@link org.opencps.datamgt.service.DictCollectionLinkLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security
 * checks based on the propagated JAAS credentials because this service can only
 * be accessed from within the same VM.
 * </p>
 *
 * @author khoavd
 * @see org.opencps.datamgt.service.base.DictCollectionLinkLocalServiceBaseImpl
 * @see org.opencps.datamgt.service.DictCollectionLinkLocalServiceUtil
 */
public class DictCollectionLinkLocalServiceImpl extends
		DictCollectionLinkLocalServiceBaseImpl {

	public DictCollectionLink addDictCollectionLink(long dictCollectionId,
			long dictCollectionLinkedId, long sequenceNo,
			ServiceContext serviceContext) throws SystemException {
		long dictCollectionLinkId = counterLocalService
				.increment(DictCollectionLink.class.getName());
		DictCollectionLink dictCollectionLink = dictCollectionLinkPersistence
				.create(dictCollectionLinkId);

		DictCollection collection = dictCollectionPersistence
				.fetchByPrimaryKey(dictCollectionLinkedId);

		dictCollectionLink.setCompanyId(serviceContext.getCompanyId());
		dictCollectionLink.setGroupId(serviceContext.getScopeGroupId());
		dictCollectionLink.setUserId(serviceContext.getUserId());
		dictCollectionLink.setCreateDate(new Date());
		dictCollectionLink.setModifiedDate(new Date());

		dictCollectionLink.setDictCollectionId(dictCollectionId);
		dictCollectionLink.setDictCollectionLinkedId(dictCollectionLinkedId);
		dictCollectionLink.setSequenceNo(sequenceNo);
		dictCollectionLink.setDictCollectionLinkedName(collection
				.getCollectionName());

		return dictCollectionLinkPersistence.update(dictCollectionLink);
	}

	public List<DictCollectionLink> getByDictCollectionId(long dictCollectionId)
			throws SystemException {
		return dictCollectionLinkPersistence
				.findByDictCollectionId(dictCollectionId);
	}

	public DictCollectionLink deleteDictCollectionLink(long dictCollectionLinkId)
			throws NoSuchDictCollectionLinkException, SystemException {
		return dictCollectionLinkPersistence.remove(dictCollectionLinkId);
	}
}