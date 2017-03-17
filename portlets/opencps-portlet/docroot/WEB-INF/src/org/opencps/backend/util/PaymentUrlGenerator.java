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

package org.opencps.backend.util;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.opencps.dossiermgt.model.Dossier;
import org.opencps.dossiermgt.service.DossierLocalServiceUtil;
import org.opencps.paymentmgt.NoSuchPaymentFileException;
import org.opencps.paymentmgt.keypay.model.KeyPay;
import org.opencps.paymentmgt.model.PaymentConfig;
import org.opencps.paymentmgt.model.PaymentFile;
import org.opencps.paymentmgt.model.PaymentGateConfig;
import org.opencps.paymentmgt.service.PaymentConfigLocalServiceUtil;
import org.opencps.paymentmgt.service.PaymentFileLocalServiceUtil;
import org.opencps.paymentmgt.service.PaymentGateConfigLocalServiceUtil;
import org.opencps.paymentmgt.util.PaymentMgtUtil;
import org.opencps.paymentmgt.vtcpay.model.VTCPay;
import org.opencps.util.PortletPropsValues;

import com.liferay.counter.service.CounterLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author khoavd
 */
public class PaymentUrlGenerator {

	public static PaymentFile generatorPayURL(
		long groupId, long govAgencyOrganizationId, long paymentFileId,
		String pattern, long dossierId)
		throws IOException {

		PaymentFile paymentFile = null;
		try {

			paymentFile =
				PaymentFileLocalServiceUtil.getPaymentFile(paymentFileId);
		}
		catch (NoSuchPaymentFileException e) {
			_log.error(e);
		}
		catch (Exception e) {
			_log.error(e);
		}
		PaymentConfig paymentConfig = null;

		if (Validator.isNotNull(paymentFile))

			paymentConfig =
				PaymentMgtUtil.validatePaymentConfig(
					groupId, paymentFile.getGovAgencyOrganizationId());

		Dossier dossier = null;
		String url_redirect = StringPool.BLANK;

		if (Validator.isNotNull(paymentConfig)) {

			List<PaymentGateConfig> paymentGateConfigList =
				new ArrayList<PaymentGateConfig>();
			try {
				paymentGateConfigList =
					PaymentGateConfigLocalServiceUtil.getPaymentGateConfigs(
						QueryUtil.ALL_POS, QueryUtil.ALL_POS);
			}
			catch (SystemException e1) {
				// TODO Auto-generated catch block
				_log.error(e1);
			}

			for (PaymentGateConfig paymentGateConfig : paymentGateConfigList) {

				if (paymentGateConfig.getPaymentGateId() == paymentConfig.getPaymentGateType() &&
					paymentGateConfig.getPaymentGateName().equals("VTCPAY")) {

					// set du lieu cho request
					String website_id = StringPool.BLANK;
					String receiver_account = StringPool.BLANK;
					String language = StringPool.BLANK;
					String url_return = StringPool.BLANK;
					String secret_key = StringPool.BLANK;
					String reference_number = StringPool.BLANK;
					String amount = StringPool.BLANK;
					String currency = StringPool.BLANK;
					String request_url = StringPool.BLANK;
					String trans_ref_no = StringPool.BLANK;
					String status = StringPool.BLANK;
					String data = StringPool.BLANK;
					String signature = StringPool.BLANK;

					request_url =
						paymentConfig.getKeypayDomain() + StringPool.QUESTION;
					website_id = paymentConfig.getKeypayMerchantCode();
					receiver_account = paymentConfig.getBankInfo();
					secret_key = paymentConfig.getKeypaySecureKey();

					reference_number =
						String.valueOf(_genetatorTransactionId());

					Double amountDouble = paymentFile.getAmount();
					int amountInt = amountDouble.intValue();
					amount = String.valueOf(amountInt);

					currency = "VND";

					dossier = _getDossier(dossierId);

					url_return = paymentConfig.getReturnUrl();

					VTCPay vtcPay =
						new VTCPay(
							website_id, receiver_account, language, url_return,
							secret_key, reference_number, amount, currency,
							request_url, trans_ref_no, status, data,
							paymentConfig.getKeypaySecureKey());

					StringBuffer param = new StringBuffer();
					param.append("amount=").append(
						URLEncoder.encode(vtcPay.getAmount(), "UTF-8")).append(
						StringPool.AMPERSAND);
					param.append("currency=").append(
						URLEncoder.encode(vtcPay.getCurrency(), "UTF-8")).append(
						StringPool.AMPERSAND);
					param.append("receiver_account=").append(
						URLEncoder.encode(vtcPay.getReceiver_account(), "UTF-8")).append(
						StringPool.AMPERSAND);
					param.append("reference_number=").append(
						URLEncoder.encode(vtcPay.getReference_number(), "UTF-8")).append(
						StringPool.AMPERSAND);
					param.append("url_return=").append(
						URLEncoder.encode(url_return, "UTF-8")).append(
						StringPool.AMPERSAND);
					param.append("website_id=").append(
						URLEncoder.encode(vtcPay.getWebsite_id(), "UTF-8")).append(
						StringPool.AMPERSAND);
					param.append("signature=").append(
						VTCPay.getSecureHashCodeRequest(vtcPay));

					url_redirect = vtcPay.getRequest_url() + param.toString();

					try {
						paymentFile =
							PaymentFileLocalServiceUtil.updatePaymentFile(
								paymentFileId, url_redirect,
								Long.parseLong(vtcPay.getReference_number()),
								StringPool.BLANK, vtcPay.getWebsite_id());

						paymentFile.setPaymentConfig(paymentConfig.getPaymentConfigId());

						PaymentFileLocalServiceUtil.updatePaymentFile(paymentFile);

					}
					catch (Exception e) {
						_log.error(e);
					}
				}
				else if (paymentGateConfig.getPaymentGateId() == paymentConfig.getPaymentGateType() &&
					paymentGateConfig.getPaymentGateName().equals("KEYPAY")) {

					List<String> lsMessages = _putPaymentMessage(pattern);

					long merchant_trans_id = _genetatorTransactionId();

					String merchant_code =
						paymentConfig.getKeypayMerchantCode();

					String good_code = generatorGoodCode(10);

					String net_cost =
						String.valueOf((int) paymentFile.getAmount());
					String ship_fee = "0";
					String tax = "0";

					String bank_code = StringPool.BLANK;

					String service_code =
						PortletPropsValues.OPENCPS_KEYPAY_SERVICE_CODE;
					String version = paymentConfig.getKeypayVersion();
					String command = PortletPropsValues.OPENCPS_KEYPAY_COMMAND;
					String currency_code =
						PortletPropsValues.OPENCPS_KEYPAY_CURRENCY_CODE;

					String desc_1 = StringPool.BLANK;
					String desc_2 = StringPool.BLANK;
					String desc_3 = StringPool.BLANK;
					String desc_4 = StringPool.BLANK;
					String desc_5 = StringPool.BLANK;

					if (lsMessages.size() > 0) {
						desc_1 = lsMessages.get(0);
						desc_2 = lsMessages.get(1);
						desc_3 = lsMessages.get(2);
						desc_4 = lsMessages.get(3);
						desc_5 = lsMessages.get(4);

						if (desc_1.length() >= 20) {
							desc_1 = desc_1.substring(0, 19);
						}
						if (desc_2.length() >= 30) {
							desc_2 = desc_2.substring(0, 29);
						}
						if (desc_3.length() >= 40) {
							desc_3 = desc_3.substring(0, 39);
						}
						if (desc_4.length() >= 100) {
							desc_4 = desc_4.substring(0, 89);
						}
						if (desc_5.length() > 15) {
							desc_5 = desc_5.substring(0, 15);

							if (!Validator.isDigit(desc_5)) {
								desc_5 = StringPool.BLANK;
							}
						}
					}

					String xml_description = StringPool.BLANK;
					String current_locale =
						PortletPropsValues.OPENCPS_KEYPAY_CURRENT_LOCATE;
					String country_code =
						PortletPropsValues.OPENCPS_KEYPAY_COUNTRY_CODE;
					String internal_bank =
						PortletPropsValues.OPENCPS_KEYPAY_INTERNAL_BANK;

					String merchant_secure_key =
						paymentConfig.getKeypaySecureKey();

					// dossier = _getDossier(dossierId);

					// TODO : update returnURL keyPay

					String return_url = StringPool.BLANK;
					return_url = paymentConfig.getReturnUrl();

					KeyPay keypay =
						new KeyPay(
							String.valueOf(merchant_trans_id), merchant_code,
							good_code, net_cost, ship_fee, tax, bank_code,
							service_code, version, command, currency_code,
							desc_1, desc_2, desc_3, desc_4, desc_5,
							xml_description, current_locale, country_code,
							return_url, internal_bank, merchant_secure_key);

					// keypay.setKeypay_url(paymentConfig.getKeypayDomain());

					StringBuffer param = new StringBuffer();
					param.append("merchant_code=").append(
						URLEncoder.encode(keypay.getMerchant_code(), "UTF-8")).append(
						StringPool.AMPERSAND);
					param.append("merchant_secure_key=").append(
						URLEncoder.encode(
							keypay.getMerchant_secure_key(), "UTF-8")).append(
						StringPool.AMPERSAND);
					param.append("bank_code=").append(
						URLEncoder.encode(keypay.getBank_code(), "UTF-8")).append(
						StringPool.AMPERSAND);
					param.append("internal_bank=").append(
						URLEncoder.encode(keypay.getInternal_bank(), "UTF-8")).append(
						StringPool.AMPERSAND);
					param.append("merchant_trans_id=").append(
						URLEncoder.encode(
							keypay.getMerchant_trans_id(), "UTF-8")).append(
						StringPool.AMPERSAND);
					param.append("good_code=").append(
						URLEncoder.encode(keypay.getGood_code(), "UTF-8")).append(
						StringPool.AMPERSAND);
					param.append("net_cost=").append(
						URLEncoder.encode(keypay.getNet_cost(), "UTF-8")).append(
						StringPool.AMPERSAND);
					param.append("ship_fee=").append(
						URLEncoder.encode(keypay.getShip_fee(), "UTF-8")).append(
						StringPool.AMPERSAND);
					param.append("tax=").append(
						URLEncoder.encode(keypay.getTax(), "UTF-8")).append(
						StringPool.AMPERSAND);
					param.append("return_url=").append(
						URLEncoder.encode(keypay.getReturn_url(), "UTF-8")).append(
						StringPool.AMPERSAND);
					param.append("version=").append(
						URLEncoder.encode(keypay.getVersion(), "UTF-8")).append(
						StringPool.AMPERSAND);
					param.append("command=").append(
						URLEncoder.encode(keypay.getCommand(), "UTF-8")).append(
						StringPool.AMPERSAND);
					param.append("current_locale=").append(
						URLEncoder.encode(keypay.getCurrent_locale(), "UTF-8")).append(
						StringPool.AMPERSAND);
					param.append("currency_code=").append(
						URLEncoder.encode(keypay.getCurrency_code(), "UTF-8")).append(
						StringPool.AMPERSAND);
					param.append("service_code=").append(
						URLEncoder.encode(keypay.getService_code(), "UTF-8")).append(
						StringPool.AMPERSAND);
					param.append("country_code=").append(
						URLEncoder.encode(keypay.getCountry_code(), "UTF-8")).append(
						StringPool.AMPERSAND);
					// param.append("desc_1=").append(URLEncoder.encode(keypay.getDesc_1(),
					// "UTF-8")).append(StringPool.AMPERSAND);
					// param.append("desc_2=").append(URLEncoder.encode(keypay.getDesc_2(),
					// "UTF-8")).append(StringPool.AMPERSAND);
					// param.append("desc_3=").append(URLEncoder.encode(keypay.getDesc_3(),
					// "UTF-8")).append(StringPool.AMPERSAND);
					// param.append("desc_4=").append(URLEncoder.encode(keypay.getDesc_4(),
					// "UTF-8")).append(StringPool.AMPERSAND);
					// param.append("desc_5=").append(URLEncoder.encode(keypay.getDesc_5(),
					// "UTF-8")).append(StringPool.AMPERSAND);
					param.append("desc_1=&");
					param.append("desc_2=&");
					param.append("desc_3=&");
					param.append("desc_4=&");
					param.append("desc_5=&");
					param.append("xml_description=").append(
						URLEncoder.encode(keypay.getXml_description(), "UTF-8")).append(
						StringPool.AMPERSAND);
					param.append("secure_hash=").append(keypay.getSecure_hash());

					url_redirect =
						paymentConfig.getKeypayDomain() + StringPool.QUESTION +
							param.toString();

					try {
						paymentFile =
							PaymentFileLocalServiceUtil.updatePaymentFile(
								paymentFileId, url_redirect,
								GetterUtil.getLong(merchant_trans_id, 0),
								good_code,
								paymentConfig.getKeypayMerchantCode());

						paymentFile.setPaymentConfig(paymentConfig.getPaymentConfigId());

						PaymentFileLocalServiceUtil.updatePaymentFile(paymentFile);

					}
					catch (Exception e) {
						_log.error(e);
					}

				}
			}
		}

		return paymentFile;
	}

	/**
	 * @param dossierId
	 * @return
	 */
	private static Dossier _getDossier(long dossierId) {

		Dossier dossier = null;

		try {
			dossier = DossierLocalServiceUtil.fetchDossier(dossierId);
		}
		catch (Exception e) {
			_log.error(e);
		}

		return dossier;
	}

	private static List<String> _putPaymentMessage(String pattern) {

		List<String> lsDesc = new ArrayList<String>();

		lsDesc.add(0, StringPool.BLANK);
		lsDesc.add(1, StringPool.BLANK);
		lsDesc.add(2, StringPool.BLANK);
		lsDesc.add(3, StringPool.BLANK);
		lsDesc.add(4, StringPool.BLANK);

		List<String> lsMsg = PaymentRequestGenerator.getMessagePayment(pattern);

		for (int i = 0; i < lsMsg.size(); i++) {
			lsDesc.set(1, lsMsg.get(i));
		}

		return lsDesc;
	}

	/**
	 * Generator PaymentFile
	 * 
	 * @param paymentFile
	 * @return
	 */
	private static long _genetatorTransactionId() {

		long transactionId = 0;
		try {
			transactionId =
				CounterLocalServiceUtil.increment(PaymentFile.class.getName() +
					".genetatorTransactionId");
		}
		catch (SystemException e) {
			_log.error(e);
		}
		return transactionId;
	}

	/**
	 * Get paymentFile by id
	 * 
	 * @param paymentFileId
	 * @return
	 */
	private static PaymentFile _getPaymentFileById(long paymentFileId) {

		PaymentFile paymentFile = null;

		try {
			paymentFile =
				PaymentFileLocalServiceUtil.fetchPaymentFile(paymentFileId);
		}
		catch (Exception e) {
			paymentFile = null;
		}

		return paymentFile;
	}

	/**
	 * @param groupId
	 * @param govAgencyOrganizationId
	 * @return
	 */
	private static PaymentConfig _getPaymentConfig(
		long groupId, long govAgencyOrganizationId) {

		PaymentConfig paymentConfig = null;

		try {
			paymentConfig =
				PaymentConfigLocalServiceUtil.getPaymentConfigByGovAgency(
					groupId, govAgencyOrganizationId, true);
		}
		catch (Exception e) {
			paymentConfig = null;
		}

		return paymentConfig;
	}

	/**
	 * @param length
	 * @return
	 */
	public static String generatorGoodCode(int length) {

		String tempGoodCode = _generatorUniqueString(length);

		String goodCode = StringPool.BLANK;

		while (_checkContainsGoodCode(tempGoodCode)) {
			tempGoodCode = _generatorUniqueString(length);
		}

		/*
		 * while(_testCheck(tempGoodCode)) { tempGoodCode =
		 * _generatorUniqueString(length); }
		 */
		goodCode = tempGoodCode;

		return goodCode;
	}

	@SuppressWarnings("unused")
	private static boolean _testCheck(String keyCode) {

		boolean isContains = false;

		List<String> ls = new ArrayList<String>();

		ls.add("0");
		ls.add("1");
		ls.add("2");
		ls.add("3");
		ls.add("4");
		ls.add("5");
		ls.add("6");
		ls.add("7");
		ls.add("9");

		if (ls.contains(keyCode)) {
			isContains = true;
		}

		return isContains;
	}

	/**
	 * @param keypayGoodCode
	 * @return
	 */
	private static boolean _checkContainsGoodCode(String keypayGoodCode) {

		boolean isContains = false;

		try {
			PaymentFile paymentFile =
				PaymentFileLocalServiceUtil.getByGoodCode(keypayGoodCode);

			if (Validator.isNotNull(paymentFile)) {
				isContains = true;
			}
		}
		catch (Exception e) {
			isContains = true;
		}

		return isContains;

	}

	/**
	 * @param pattern
	 * @param lenght
	 * @return
	 */
	private static String _generatorUniqueString(int lenght) {

		char[] chars = "0123456789".toCharArray();

		StringBuilder sb = new StringBuilder();

		Random random = new Random();

		for (int i = 0; i < lenght; i++) {
			char c = chars[random.nextInt(chars.length)];
			sb.append(c);
		}

		return sb.toString();

	}

	private static Log _log = LogFactoryUtil.getLog(PaymentUrlGenerator.class);
}
