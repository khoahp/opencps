/*******************************************************************************
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/

package org.opencps.paymentmgt.search;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;

import org.opencps.datamgt.model.DictItem;
import org.opencps.util.DateTimeUtil;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * @author nhanhoang
 *
 */
public class PaymentConfigSearch extends SearchContainer<DictItem> {
	static List<String> headerNames = new ArrayList<String>();
	static {
		headerNames.add("row-index");
		headerNames.add("create-date");
		headerNames.add("modified-date");
		headerNames.add("payment-config-no");
		headerNames.add("payment-gate-type");
		headerNames.add("action");
	}

	public static final String EMPTY_RESULTS_MESSAGE = "no-payment-config-where-found";

	public PaymentConfigSearch(PortletRequest portletRequest, int delta,
			PortletURL iteratorURL) {

		super(portletRequest,
				new PaymentConfigDisplayTerms(portletRequest),
				new PaymentConfigSearchTerms(portletRequest),
				DEFAULT_CUR_PARAM, delta, iteratorURL, headerNames,
				EMPTY_RESULTS_MESSAGE);

		PaymentConfigDisplayTerms displayTerms = (PaymentConfigDisplayTerms) getDisplayTerms();

		iteratorURL.setParameter(PaymentConfigDisplayTerms.CREATE_DATE,
				DateTimeUtil.convertDateToString(displayTerms.getCreateDate(),
						DateTimeUtil._VN_DATE_TIME_FORMAT));
		iteratorURL.setParameter(PaymentConfigDisplayTerms.MODIFIED_DATE,
				DateTimeUtil.convertDateToString(
						displayTerms.getModifiedDate(),
						DateTimeUtil._VN_DATE_TIME_FORMAT));
		iteratorURL.setParameter(PaymentConfigDisplayTerms.USER_ID,
				String.valueOf(displayTerms.getUserId()));

		iteratorURL.setParameter(
				PaymentConfigDisplayTerms.PAYMENT_CONFIG_ID,
				String.valueOf(displayTerms.getPaymentConfigId()));
		iteratorURL.setParameter(
				PaymentConfigDisplayTerms.PAYMENT_CONFIG_NO,
				String.valueOf(displayTerms.getPaymentConfigNo()));
		iteratorURL.setParameter(
				PaymentConfigDisplayTerms.PAYMENT_GATE_TYPE,
				String.valueOf(displayTerms.getPaymentGateTypes()));
		iteratorURL.setParameter(
				PaymentConfigDisplayTerms.PAYMENT_STATUS,
				String.valueOf(displayTerms.getStatus()));

	}

	public PaymentConfigSearch(PortletRequest portletRequest,
			PortletURL iteratorURL) {

		this(portletRequest, DEFAULT_DELTA, iteratorURL);
	}

	private static Log _log = LogFactoryUtil
			.getLog(PaymentConfigSearch.class);

}
