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

package org.opencps.backend.scheduler;

import java.util.ArrayList;
import java.util.List;

import org.opencps.postal.model.PostalOrder;
import org.opencps.postal.service.PostalOrderLocalServiceUtil;
import org.opencps.postal.utils.PostalKeys;
import org.opencps.postal.utils.PostalOrderGenerator;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.messaging.MessageListenerException;

/**
 * @author nhanhoang
 */
public class CheckPostalOrderStatus implements MessageListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.liferay.portal.kernel.messaging.MessageListener#receive(com.liferay
	 * .portal.kernel.messaging.Message)
	 */
	@Override
	public void receive(Message message) throws MessageListenerException {

		List<PostalOrder> postalOrders = new ArrayList<PostalOrder>();

		String postalOrderStatus = PostalKeys.DOSSIER_COLLECT;

		try {

			postalOrders = PostalOrderLocalServiceUtil.getPostalOrderByStatus(
					postalOrderStatus, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			if (postalOrders.size() > 0) {

				for (PostalOrder postalOrder : postalOrders) {

					PostalOrderGenerator.sendCheckStatusMessage(
							postalOrder.getPostalOrderId(),
							PostalKeys.API_COLLECT_STATUS, postalOrderStatus);

				}

			}

			// /////////////////////////////

			postalOrderStatus = PostalKeys.DOSSIER_DELIVERY;

			postalOrders = PostalOrderLocalServiceUtil.getPostalOrderByStatus(
					postalOrderStatus, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			if (postalOrders.size() > 0) {

				for (PostalOrder postalOrder : postalOrders) {

					PostalOrderGenerator.sendCheckStatusMessage(
							postalOrder.getPostalOrderId(),
							PostalKeys.API_DELIVERY_STATUS, postalOrderStatus);

				}

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private Log _log = LogFactoryUtil.getLog(CheckPostalOrderStatus.class);

}
