package org.opencps.integrate.utils;

import org.opencps.integrate.dao.InvalidMessageContentException;
import org.opencps.paymentmgt.model.PaymentFile;
import org.opencps.paymentmgt.service.PaymentFileLocalServiceUtil;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;

public class PaymentFileUtils {
	
	/**
	 * @param oid
	 * @return
	 */
	public static PaymentFile getPaymentFileByIod(String oid) {
		PaymentFile paymentFile = null;
		
		try {
			paymentFile = PaymentFileLocalServiceUtil.getPaymentFileByOID(oid);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return paymentFile;
	}
	
	/**
	 * @param body
	 * @return
	 * @throws InvalidMessageContentException
	 */
	public static PaymentFileModel getPaymentFile(String body)
			throws InvalidMessageContentException {

		PaymentFileModel paymentFileModel = new PaymentFileModel();

		try {
			JSONObject jsInput = JSONFactoryUtil.createJSONObject(body);

			paymentFileModel.setPaymentFileUid(jsInput
					.getString("PaymentFileUid"));
			paymentFileModel.setPaymentFee(jsInput.getString("PaymentFee"));
			paymentFileModel.setPaymentAmount(jsInput
					.getDouble("PaymentAmount"));
			paymentFileModel.setPaymentStatus(jsInput.getInt("PaymentStatus"));
			paymentFileModel.setPaymentNote(jsInput.getString("PaymentNote"));
			paymentFileModel.setPaymentMethod(jsInput.getInt("PaymentMethod"));
			paymentFileModel.setInvoiceNo(jsInput.getString("InvoiceNo"));
			paymentFileModel.setCreateDate(APIUtils.convertDateTime(jsInput
					.getString("CreateDate")));

		} catch (Exception e) {
			throw new InvalidMessageContentException();
		}

		return paymentFileModel;
	}
}
