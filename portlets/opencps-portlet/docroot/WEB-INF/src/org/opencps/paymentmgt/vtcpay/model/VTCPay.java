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

package org.opencps.paymentmgt.vtcpay.model;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.opencps.paymentmgt.model.PaymentConfig;
import org.opencps.paymentmgt.model.PaymentFile;
import org.opencps.paymentmgt.service.PaymentConfigLocalServiceUtil;
import org.opencps.paymentmgt.service.PaymentFileLocalServiceUtil;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

public class VTCPay {

	// Cac tham so gui di

	protected String website_id;
	protected String receiver_account;
	protected String language;
	protected String url_return;

	protected String reference_number;
	protected String amount;
	protected String currency;
	protected String bill_to_email;
	protected String bill_to_phone;
	protected String bill_to_address;
	protected String bill_to_address_city;
	protected String bill_to_surname;
	protected String bill_to_forename;
	protected String paymentType;
	// secure key
	protected String secret_key;
	// pay url
	protected String request_url;
	// các tham số trả về từ VTCPay
	protected String trans_ref_no;
	protected String message;
	protected String status;

	//
	protected String data;
	protected String signature;
	
	//Tham so bo sung dung de kiem tra trang thai don hang
	protected String order_code;
	protected String receiver_acc;
	protected String sign;
	
	protected String responsecode;

	private static Log _log = LogFactoryUtil.getLog(VTCPay.class);

	public VTCPay(
		String website_id, String receiver_account, String language, String url_return,
		String secret_key, String reference_number, String amount, String currency,
		String request_url, String trans_ref_no, String status, String data, String signature) {

		this.website_id = website_id;
		this.receiver_account = receiver_account;
		this.language = language;
		this.url_return = url_return;
		this.secret_key = secret_key;
		this.reference_number = reference_number;
		this.amount = amount;
		this.currency = currency;
		this.request_url = request_url;
		this.trans_ref_no = trans_ref_no;
		this.status = status;
		this.data = data;
		this.signature = signature;
	}
	
	public VTCPay(){
		
	}
	
	public VTCPay(String website_id,String order_code,String receiver_acc,String secret_key){
		
		this.website_id = website_id;
		this.order_code = order_code;
		this.receiver_acc = receiver_acc;
		this.secret_key = secret_key;
		
	}

	public VTCPay(String data) {

		String[] dataArrays = StringUtil.split(data, "|");

		if (dataArrays.length > 0) {

			List<String> dataList = Arrays.asList(dataArrays);

			if (dataList.size() > 0) {
				this.amount = dataList.get(0);
				this.message = dataList.get(1);
				this.paymentType = dataList.get(2);
				this.reference_number = dataList.get(3);
				this.status = dataList.get(4);
				this.trans_ref_no = dataList.get(5);
				this.website_id = dataList.get(6);
				this.secret_key = dataList.get(7);
			}
		}

	}

	public static String getSecureHashCodeResponse(VTCPay vtcPay) {

		PaymentFile paymentFile = null;
		PaymentConfig paymentConfig = null;

		_log.info("=====vtcPay.getReference_number():" + vtcPay.getReference_number());

		try {

			if (vtcPay.getReference_number().trim().length() > 0) {
				paymentFile =
					PaymentFileLocalServiceUtil.getByTransactionId(Long.parseLong(vtcPay.getReference_number()));
			}

			if (Validator.isNotNull(paymentFile)) {

				paymentConfig =
					PaymentConfigLocalServiceUtil.getPaymentConfig(paymentFile.getPaymentConfig());
				
				StringBuffer merchantSignBuffer = new StringBuffer();
				merchantSignBuffer.append(vtcPay.getAmount());

				merchantSignBuffer.append("|").append(vtcPay.getMessage());

				merchantSignBuffer.append("|").append(vtcPay.getPaymentType());

				merchantSignBuffer.append("|").append(vtcPay.getReference_number());

				merchantSignBuffer.append("|").append(vtcPay.getStatus());

				merchantSignBuffer.append("|").append(vtcPay.getTrans_ref_no());

				merchantSignBuffer.append("|").append(vtcPay.getWebsite_id());

				merchantSignBuffer.append("|").append(
					Validator.isNotNull(paymentConfig)
						? paymentConfig.getKeypaySecureKey() : StringPool.BLANK);

				String merchantSign = StringPool.BLANK;
				merchantSign = merchantSignBuffer.toString();

				merchantSign = VTCPay.sha256(merchantSign);
				
				return merchantSign;
			}

		}
		catch (NumberFormatException | PortalException | SystemException e1) {
			// TODO Auto-generated catch block
			_log.error(e1);
		}

		return StringPool.BLANK;
	}

	public static String getSecureHashCodeRequest(VTCPay vtcPay) {

		StringBuffer merchantSignBuffer = new StringBuffer();

		
		merchantSignBuffer.append(vtcPay.getAmount());
		merchantSignBuffer.append("|").append(vtcPay.getCurrency());
		merchantSignBuffer.append("|").append(vtcPay.getReceiver_account());
		merchantSignBuffer.append("|").append(vtcPay.getReference_number());
		merchantSignBuffer.append("|").append(vtcPay.getUrl_return());
		merchantSignBuffer.append("|").append(vtcPay.getWebsite_id());
		merchantSignBuffer.append("|").append(vtcPay.getSecret_key());

		String merchantSign = StringPool.BLANK;
		merchantSign = merchantSignBuffer.toString();

		merchantSign = VTCPay.sha256(merchantSign);

		return merchantSign;

	}
	public static String getSecureHashCodeCheckRequest(VTCPay vtcPay){
		
		StringBuffer merchantSignBuffer = new StringBuffer();
		
		merchantSignBuffer.append(vtcPay.getWebsite_id());
		merchantSignBuffer.append("-").append(vtcPay.getOrder_code());
		merchantSignBuffer.append("-").append(vtcPay.getReceiver_acc());
		merchantSignBuffer.append("-").append(vtcPay.getSecret_key());
		
		String merchantSign = StringPool.BLANK;
		merchantSign = merchantSignBuffer.toString();
		
		

		merchantSign = VTCPay.sha256(merchantSign);
		merchantSign = merchantSign.toUpperCase();
		_log.info("merchantSign:"+merchantSign);
		
		return merchantSign;
		
	}
	
	public static VTCPay getSecureHashCodeCheckResponse(String data){
		
		String[] dataArrays = StringUtil.split(data, "|");
		VTCPay vtcPay = new VTCPay();

		if (dataArrays.length > 0) {

			List<String> dataList = Arrays.asList(dataArrays);

			if (dataList.size() > 0) {
				vtcPay.setResponsecode(dataList.get(0));
				vtcPay.setOrder_code(dataList.get(1));
				vtcPay.setAmount(dataList.get(2));
				vtcPay.setAmount(dataList.get(3));
			}
		}
		
		return vtcPay;
		
	}

	public static boolean validateSign(VTCPay vtcPay) {

		String merchantSig = VTCPay.getSecureHashCodeResponse(vtcPay);

		merchantSig = merchantSig.toUpperCase();


		String signature = vtcPay.getSignature();


		if (merchantSig.contains(signature)) {
			return true;
		}
		else {
			return false;
		}

	}

	public static String sha256(String base) {

		try {

			if (base.trim().length() > 0) {
				MessageDigest digest = MessageDigest.getInstance("SHA-256");
				byte[] hash = digest.digest(base.getBytes("UTF-8"));
				StringBuffer hexString = new StringBuffer();

				for (int i = 0; i < hash.length; i++) {
					String hex = Integer.toHexString(0xff & hash[i]);
					if (hex.length() == 1)
						hexString.append('0');
					hexString.append(hex);
				}

				return hexString.toString();
			}
			else {
				return StringPool.BLANK;
			}
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}

	}

	/**
	 * Constructor - Lay du lieu tra ve tu VTCPay
	 *
	 * @param request
	 */
	public static VTCPay getVTCPayDataPost(HttpServletRequest request) {
		
		VTCPay vtcPay = new VTCPay();
		try {
			
			vtcPay.setData(request.getParameter("data"));
			
			String[] dataArray = StringUtil.split(vtcPay.getData(),"|");
			List<String> dataList = Arrays.asList(dataArray);
			
			vtcPay.setAmount(dataList.get(0));
			vtcPay.setMessage(dataList.get(1));
			vtcPay.setPaymentType(dataList.get(2));
			vtcPay.setReference_number(dataList.get(3));
			vtcPay.setStatus(dataList.get(4));
			vtcPay.setTrans_ref_no(dataList.get(5));
			vtcPay.setWebsite_id(dataList.get(6));
			vtcPay.setSignature(request.getParameter("signature"));

		}
		catch (Exception e) {
			_log.info("ERROR get data VTCPay return");
		}
		return vtcPay;
	}
	
	public VTCPay(HttpServletRequest request) {

		try {
			this.amount = request.getParameter("amount");
			this.message = request.getParameter("message");
			this.paymentType = request.getParameter("payment_type");
			this.reference_number = request.getParameter("reference_number");
			this.status = request.getParameter("status");
			this.trans_ref_no = request.getParameter("trans_ref_no");
			this.website_id = request.getParameter("website_id");
			this.signature = request.getParameter("signature");

		}
		catch (Exception e) {
			_log.info("ERROE get data KeyPay return");
		}
	}
	public String getWebsite_id() {
	
		return website_id;
	}

	
	public void setWebsite_id(String website_id) {
	
		this.website_id = website_id;
	}

	
	public String getReceiver_account() {
	
		return receiver_account;
	}

	
	public void setReceiver_account(String receiver_account) {
	
		this.receiver_account = receiver_account;
	}

	
	public String getLanguage() {
	
		return language;
	}

	
	public void setLanguage(String language) {
	
		this.language = language;
	}

	
	public String getUrl_return() {
	
		return url_return;
	}

	
	public void setUrl_return(String url_return) {
	
		this.url_return = url_return;
	}

	
	public String getReference_number() {
	
		return reference_number;
	}

	
	public void setReference_number(String reference_number) {
	
		this.reference_number = reference_number;
	}

	
	public String getAmount() {
	
		return amount;
	}

	
	public void setAmount(String amount) {
	
		this.amount = amount;
	}

	
	public String getCurrency() {
	
		return currency;
	}

	
	public void setCurrency(String currency) {
	
		this.currency = currency;
	}

	
	public String getBill_to_email() {
	
		return bill_to_email;
	}

	
	public void setBill_to_email(String bill_to_email) {
	
		this.bill_to_email = bill_to_email;
	}

	
	public String getBill_to_phone() {
	
		return bill_to_phone;
	}

	
	public void setBill_to_phone(String bill_to_phone) {
	
		this.bill_to_phone = bill_to_phone;
	}

	
	public String getBill_to_address() {
	
		return bill_to_address;
	}

	
	public void setBill_to_address(String bill_to_address) {
	
		this.bill_to_address = bill_to_address;
	}

	
	public String getBill_to_address_city() {
	
		return bill_to_address_city;
	}

	
	public void setBill_to_address_city(String bill_to_address_city) {
	
		this.bill_to_address_city = bill_to_address_city;
	}

	
	public String getBill_to_surname() {
	
		return bill_to_surname;
	}

	
	public void setBill_to_surname(String bill_to_surname) {
	
		this.bill_to_surname = bill_to_surname;
	}

	
	public String getBill_to_forename() {
	
		return bill_to_forename;
	}

	
	public void setBill_to_forename(String bill_to_forename) {
	
		this.bill_to_forename = bill_to_forename;
	}

	
	public String getPaymentType() {
	
		return paymentType;
	}

	
	public void setPaymentType(String paymentType) {
	
		this.paymentType = paymentType;
	}

	
	public String getSecret_key() {
	
		return secret_key;
	}

	
	public void setSecret_key(String secret_key) {
	
		this.secret_key = secret_key;
	}

	
	public String getRequest_url() {
	
		return request_url;
	}

	
	public void setRequest_url(String request_url) {
	
		this.request_url = request_url;
	}

	
	public String getTrans_ref_no() {
	
		return trans_ref_no;
	}

	
	public void setTrans_ref_no(String trans_ref_no) {
	
		this.trans_ref_no = trans_ref_no;
	}

	
	public String getMessage() {
	
		return message;
	}

	
	public void setMessage(String message) {
	
		this.message = message;
	}

	
	public String getStatus() {
	
		return status;
	}

	
	public void setStatus(String status) {
	
		this.status = status;
	}

	
	public String getData() {
	
		return data;
	}

	
	public void setData(String data) {
	
		this.data = data;
	}

	
	public String getSignature() {
	
		return signature;
	}

	
	public void setSignature(String signature) {
	
		this.signature = signature;
	}

	
	public String getOrder_code() {
	
		return order_code;
	}

	
	public void setOrder_code(String order_code) {
	
		this.order_code = order_code;
	}

	
	public String getReceiver_acc() {
	
		return receiver_acc;
	}

	
	public void setReceiver_acc(String receiver_acc) {
	
		this.receiver_acc = receiver_acc;
	}

	
	public String getSign() {
	
		return sign;
	}

	
	public void setSign(String sign) {
	
		this.sign = sign;
	}

	
	public String getResponsecode() {
	
		return responsecode;
	}

	
	public void setResponsecode(String responsecode) {
	
		this.responsecode = responsecode;
	}
	

}
