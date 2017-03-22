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

package org.opencps.postal.service.impl;

import java.util.Date;

import org.opencps.postal.model.PostalOrderLog;
import org.opencps.postal.service.base.PostalOrderLogLocalServiceBaseImpl;

import com.liferay.counter.service.CounterLocalServiceUtil;

/**
 * The implementation of the postal order log local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link org.opencps.postal.service.PostalOrderLogLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author nhanhlt
 * @see org.opencps.postal.service.base.PostalOrderLogLocalServiceBaseImpl
 * @see org.opencps.postal.service.PostalOrderLogLocalServiceUtil
 */
public class PostalOrderLogLocalServiceImpl
	extends PostalOrderLogLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this interface directly. Always use {@link org.opencps.postal.service.PostalOrderLogLocalServiceUtil} to access the postal order log local service.
	 */
	
	/**
	 * @param postalOrderId
	 * @param postalOrderStatus
	 * @param postalOrderContent
	 * @return PostalOrderLog
	 */
	public PostalOrderLog addLog(long postalOrderId, String postalOrderStatus,
			String postalOrderContent) {

		try {

			PostalOrderLog postalOrderLog = null;
			
			long postalOrderLogId = counterLocalService
					.increment(PostalOrderLog.class.getName());

			postalOrderLog = postalOrderLogPersistence
					.create(postalOrderLogId);

			postalOrderLog.setCreateDate(new Date());
			postalOrderLog.setPostalOrderId(postalOrderId);
			//postalOrderLog.setPostalOrderStatus(postalOrderStatus);
			postalOrderLog.setPostalOrderContent(postalOrderContent);

			addPostalOrderLog(postalOrderLog);
			
			return postalOrderLog;
			
		} catch (Exception e) {

		}

		return null;
	}
}