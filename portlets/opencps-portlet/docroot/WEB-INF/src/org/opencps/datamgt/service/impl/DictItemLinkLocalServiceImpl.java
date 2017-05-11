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

import org.opencps.datamgt.NoSuchDictItemLinkException;
import org.opencps.datamgt.model.DictItem;
import org.opencps.datamgt.model.DictItemLink;
import org.opencps.datamgt.service.base.DictItemLinkLocalServiceBaseImpl;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.service.ServiceContext;

/**
 * The implementation of the dict item link local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are
 * added, rerun ServiceBuilder to copy their definitions into the
 * {@link org.opencps.datamgt.service.DictItemLinkLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security
 * checks based on the propagated JAAS credentials because this service can only
 * be accessed from within the same VM.
 * </p>
 *
 * @author khoavd
 * @see org.opencps.datamgt.service.base.DictItemLinkLocalServiceBaseImpl
 * @see org.opencps.datamgt.service.DictItemLinkLocalServiceUtil
 */
public class DictItemLinkLocalServiceImpl extends
		DictItemLinkLocalServiceBaseImpl {

	public DictItemLink addDictItemLink(long dictItemId, long dictItemLinkedId,
			long sequenceNo, ServiceContext serviceContext)
			throws SystemException {

		long dictItemLinkId = counterLocalService.increment(DictItemLink.class
				.getName());
		DictItemLink dictItemLink = dictItemLinkPersistence
				.create(dictItemLinkId);

		DictItem dictItem = dictItemPersistence.fetchByPrimaryKey(dictItemId);

		dictItemLink.setCompanyId(serviceContext.getCompanyId());
		dictItemLink.setGroupId(serviceContext.getScopeGroupId());
		dictItemLink.setUserId(serviceContext.getUserId());
		dictItemLink.setCreateDate(new Date());
		dictItemLink.setModifiedDate(new Date());

		dictItemLink.setDictItemId(dictItemId);
		dictItemLink.setDictItemLinkedId(dictItemLinkedId);
		dictItemLink.setSequenceNo(sequenceNo);
		dictItemLink.setDictItemLinkedName(dictItem.getItemName());
		dictItemLink.setDictItemLinkedCode(dictItem.getItemCode());

		return dictItemLinkPersistence.update(dictItemLink);
	}

	public List<DictItemLink> getByDictItemId(long dictItemId)
			throws SystemException {
		return dictItemLinkPersistence.findByDictItemId(dictItemId);
	}

	public DictItemLink deleteDictItemLink(long dictItemLinkId)
			throws NoSuchDictItemLinkException, SystemException {
		return dictItemLinkPersistence.remove(dictItemLinkId);
	}
}