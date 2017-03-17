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

import org.opencps.paymentmgt.NoSuchPaymentConfigException;
import org.opencps.paymentmgt.NoSuchPaymentGateConfigException;
import org.opencps.paymentmgt.model.PaymentConfig;
import org.opencps.paymentmgt.model.PaymentFile;
import org.opencps.paymentmgt.model.PaymentGateConfig;
import org.opencps.paymentmgt.service.PaymentConfigLocalServiceUtil;
import org.opencps.paymentmgt.service.PaymentFileLocalServiceUtil;
import org.opencps.paymentmgt.service.PaymentGateConfigLocalServiceUtil;
import org.opencps.paymentmgt.util.VTCPayEventKeys;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.messaging.MessageListenerException;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author nhanhoang
 */
public class CheckPaymentStatus implements MessageListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.liferay.portal.kernel.messaging.MessageListener#receive(com.liferay
	 * .portal.kernel.messaging.Message)
	 */
	@Override
	public void receive(Message message) throws MessageListenerException {

		int[] paymentGateStatus = VTCPayEventKeys.NEED_CHECK_STATUS;
		int[] recheckStatus = VTCPayEventKeys.RECHECK_STATUS;
		List<PaymentFile> paymentFileList = new ArrayList<PaymentFile>();

		try {
			paymentFileList = PaymentFileLocalServiceUtil
					.getPaymentFileByParam(new int[] {}, paymentGateStatus,
							recheckStatus, true);

			if (paymentFileList.size() > 0) {

				for (PaymentFile paymentFile : paymentFileList) {
					PaymentConfig paymentConfig = null;

					try {
						paymentConfig = PaymentConfigLocalServiceUtil
								.getPaymentConfig(paymentFile
										.getPaymentConfig());
					} catch (NoSuchPaymentConfigException e) {

					}
					if (Validator.isNotNull(paymentConfig)) {

						PaymentGateConfig paymentGateConfig = null;

						try {
							paymentGateConfig = PaymentGateConfigLocalServiceUtil
									.getPaymentGateConfig(paymentConfig
											.getPaymentGateType());
						} catch (NoSuchPaymentGateConfigException e) {

						}

						if (Validator.isNotNull(paymentGateConfig)) {

							if (paymentGateConfig.getPaymentGateName().equals(
									"VTCPAY")) {
								SchedulerUtils schedulerUtils = new SchedulerUtils();
								schedulerUtils.checkVTCpayment(paymentConfig, paymentFile);

							} else if (paymentGateConfig.getPaymentGateName()
									.equals("KEYPAY")) {
								
								SchedulerUtils schedulerUtils = new SchedulerUtils();
								schedulerUtils.checkKEYPAYpayment(paymentConfig, paymentFile);;

							}

						}

					}

				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private Log _log = LogFactoryUtil.getLog(CheckPaymentStatus.class);

}
