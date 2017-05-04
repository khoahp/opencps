package org.opencps.backend.message;

import java.util.Date;

public class PaymentFileObj {
	
	public int getTotalPayment() {
		return totalPayment;
	}
	public void setTotalPayment(int totalPayment) {
		this.totalPayment = totalPayment;
	}
	public int getPaymentMethods() {
		return paymentMethods;
	}
	public void setPaymentMethods(int paymentMethods) {
		this.paymentMethods = paymentMethods;
	}
	public String getPaymentMessages() {
		return paymentMessages;
	}
	public void setPaymentMessages(String paymentMessages) {
		this.paymentMessages = paymentMessages;
	}
	public String getPaymentName() {
		return paymentName;
	}
	public void setPaymentName(String paymentName) {
		this.paymentName = paymentName;
	}
	public String getPaymentOid() {
		return paymentOid;
	}
	public void setPaymentOid(String paymentOid) {
		this.paymentOid = paymentOid;
	}
	public Date getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}
	public String getPaymentOption() {
		return paymentOption;
	}
	public void setPaymentOption(String paymentOption) {
		this.paymentOption = paymentOption;
	}
	protected int totalPayment;
	protected int paymentMethods;
	protected String paymentMessages;
	protected String paymentName;
	protected String paymentOid;
	protected Date paymentDate;
	protected String paymentOption;
}
