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
import java.util.List;

import org.opencps.postal.NoSuchPostalOrderException;
import org.opencps.postal.model.PostalOrder;
import org.opencps.postal.service.base.PostalOrderLocalServiceBaseImpl;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.service.ServiceContext;

/**
 * The implementation of the postal order local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link org.opencps.postal.service.PostalOrderLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author nhanhlt
 * @see org.opencps.postal.service.base.PostalOrderLocalServiceBaseImpl
 * @see org.opencps.postal.service.PostalOrderLocalServiceUtil
 */
public class PostalOrderLocalServiceImpl extends PostalOrderLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this interface directly. Always use {@link org.opencps.postal.service.PostalOrderLocalServiceUtil} to access the postal order local service.
	 */
	
	public PostalOrder getPostalOrderBy(long dossierId)
			throws NoSuchPostalOrderException, SystemException {

		return postalOrderPersistence.findBydossierId(dossierId);

	}
	
	public PostalOrder getPostalOrderBy(long dossierId,String postalOrderStatus)
			throws NoSuchPostalOrderException, SystemException {

		return postalOrderPersistence.fetchByD_S(dossierId, postalOrderStatus);

	}

	public List<PostalOrder> getPostalOrderByStatus(String postalOrderStatus,
			int start, int end) throws SystemException {

		return postalOrderPersistence.findBypostalOrderStatus(
				postalOrderStatus, start, end);

	}

	public PostalOrder updatePosOrder(long postalOrderId,
			String postalOrderStatus, String postalOrderContent,long postalConfigId)
			throws SystemException {

		PostalOrder postalOrder = null;

		if (postalOrderId > 0) {

			try {
				postalOrder = postalOrderPersistence
						.findBydossierId(postalOrderId);
			} catch (NoSuchPostalOrderException | SystemException e) {

			}
		}


		if (Validator.isNotNull(postalOrder)) {

			postalOrder.setPostalOrderContent(postalOrderContent);
			postalOrder.setPostalOrderStatus(postalOrderStatus);
			postalOrder.setModifiedDate(new Date());
		} else {
			postalOrderId = counterLocalService.increment(PostalOrder.class
					.getName());

			postalOrder = postalOrderPersistence.create(postalOrderId);

			postalOrder.setCreateDate(new Date());
			postalOrder.setModifiedDate(new Date());

			postalOrder.setPostalOrderContent(postalOrderContent);
			postalOrder.setPostalOrderStatus(postalOrderStatus);

		}
		
		postalOrder.setPostalConfigId(postalConfigId);

		return updatePostalOrder(postalOrder);
	}
}