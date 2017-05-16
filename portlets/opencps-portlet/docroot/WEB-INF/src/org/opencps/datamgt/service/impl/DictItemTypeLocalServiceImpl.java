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

import org.opencps.datamgt.NoSuchDictItemTypeException;
import org.opencps.datamgt.model.DictItem;
import org.opencps.datamgt.model.DictItemType;
import org.opencps.datamgt.service.base.DictItemTypeLocalServiceBaseImpl;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.service.ServiceContext;

/**
 * The implementation of the dict item type local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are
 * added, rerun ServiceBuilder to copy their definitions into the
 * {@link org.opencps.datamgt.service.DictItemTypeLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security
 * checks based on the propagated JAAS credentials because this service can only
 * be accessed from within the same VM.
 * </p>
 *
 * @author khoavd
 * @see org.opencps.datamgt.service.base.DictItemTypeLocalServiceBaseImpl
 * @see org.opencps.datamgt.service.DictItemTypeLocalServiceUtil
 */
public class DictItemTypeLocalServiceImpl extends
		DictItemTypeLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 * 
	 * Never reference this interface directly. Always use {@link
	 * org.opencps.datamgt.service.DictItemTypeLocalServiceUtil} to access the
	 * dict item type local service.
	 */

	public DictItemType addDictItemType(long dictItemId, long dictItemLinkedId,
			long sequenceNo, ServiceContext serviceContext)
			throws SystemException {

		long dictItemTypeId = counterLocalService.increment(DictItemType.class
				.getName());
		DictItemType dictItemType = dictItemTypePersistence
				.create(dictItemTypeId);

		DictItem dictItem = dictItemPersistence.fetchByPrimaryKey(dictItemId);
		DictItem dictItemLinked = dictItemPersistence
				.fetchByPrimaryKey(dictItemLinkedId);

		dictItemType.setCompanyId(serviceContext.getCompanyId());
		dictItemType.setGroupId(serviceContext.getScopeGroupId());
		dictItemType.setUserId(serviceContext.getUserId());
		dictItemType.setCreateDate(new Date());
		dictItemType.setModifiedDate(new Date());

		dictItemType.setDictItemId(dictItemId);
		dictItemType.setDictCollectionId(dictItem.getDictCollectionId());
		
		dictItemType.setDictItemLinkedId(dictItemLinkedId);
		dictItemType.setDictItemLinkedName(dictItemLinked.getItemName());
		dictItemType.setDictItemLinkedCode(dictItemLinked.getItemCode());
		dictItemType.setDictCollectionLinkedId(dictItemLinked
				.getDictCollectionId());
		
		dictItemType.setSequenceNo(sequenceNo);

		return dictItemTypePersistence.update(dictItemType);
	}

	public List<DictItemType> getByDictItemId(long dictItemId)
			throws SystemException {
		return dictItemTypePersistence.findByDictItemId(dictItemId);
	}

	public DictItemType deleteDictItemType(long dictItemTypeId)
			throws SystemException, NoSuchDictItemTypeException {
		return dictItemTypePersistence.remove(dictItemTypeId);
	}

	public List<DictItemType> getBy_IC_CI_CLI(String dictItemLinkedCode,
			long dictCollectionId, long dictCollectionLinkedId)
			throws NoSuchDictItemTypeException, SystemException {
		return dictItemTypePersistence.findByIC_CI_CLI(dictItemLinkedCode,
				dictCollectionId, dictCollectionLinkedId);
	}
}