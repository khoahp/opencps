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

package org.opencps.statisticsmgt.service.impl;

import org.opencps.statisticsmgt.service.base.DossiersStatisticsServiceBaseImpl;

import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.security.ac.AccessControlled;

/**
 * The implementation of the dossiers statistics remote service. <p> All custom
 * service methods should be put in this class. Whenever methods are added,
 * rerun ServiceBuilder to copy their definitions into the
 * {@link org.opencps.statisticsmgt.service.DossiersStatisticsService}
 * interface. <p> This is a remote service. Methods of this service are expected
 * to have security checks based on the propagated JAAS credentials because this
 * service can be accessed remotely. </p>
 *
 * @author trungnt
 * @see org.opencps.statisticsmgt.service.base.DossiersStatisticsServiceBaseImpl
 * @see org.opencps.statisticsmgt.service.DossiersStatisticsServiceUtil
 */
/**
 * @author trungnt
 */
public class DossiersStatisticsServiceImpl
	extends DossiersStatisticsServiceBaseImpl {

	/*
	 * NOTE FOR DEVELOPERS: Never reference this interface directly. Always use
	 * {@link org.opencps.statisticsmgt.service.DossiersStatisticsServiceUtil}
	 * to access the dossiers statistics remote service.
	 */

	/*
	 * (non-Javadoc)
	 * @see
	 * org.opencps.statisticsmgt.service.DossiersStatisticsService#updateStatistic
	 * (java.lang.String, java.lang.String, int, int, int, int, int, int, int,
	 * int, java.lang.String)
	 */
	@JSONWebService(value = "push-stats", method = "POST")
	@AccessControlled(guestAccessEnabled = true)
	public JSONObject updateStatistic(
		String govCode, String domainCode, int remainingNumber,
		int receivedNumber, int ontimeNumber, int overtimeNumber,
		int processingNumber, int delayingNumber, int month, int year,
		String source)
		throws JSONException {

		JSONObject msg =
			dossiersStatisticsLocalService.updateStatistic(
				0, govCode, domainCode, remainingNumber, receivedNumber,
				ontimeNumber, overtimeNumber, processingNumber, delayingNumber,
				month, year, source);

		return msg;
	}
}
