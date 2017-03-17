package org.opencps.backend.message;

public class PaymentFileObj {
	
	public int getTotalPayment() {
		return totalPayment;
	}
	public void setTotalPayment(int totalPayment) {
		this.totalPayment = totalPayment;
	}
	public String getPaymentMethods() {
		return paymentMethods;
	}
	public void setPaymentMethods(String paymentMethods) {
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
	protected int totalPayment;
	protected String paymentMethods;
	protected String paymentMessages;
	protected String paymentName;
	
}
