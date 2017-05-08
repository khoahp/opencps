package org.opencps.integrate.utils;

import java.util.Date;

public class PaymentFileModel {
	public String getPaymentFileUid() {
		return paymentFileUid;
	}
	public void setPaymentFileUid(String paymentFileUid) {
		this.paymentFileUid = paymentFileUid;
	}
	public String getPaymentFee() {
		return paymentFee;
	}
	public void setPaymentFee(String paymentFee) {
		this.paymentFee = paymentFee;
	}
	public double getPaymentAmount() {
		return paymentAmount;
	}
	public void setPaymentAmount(double paymentAmount) {
		this.paymentAmount = paymentAmount;
	}
	public int getPaymentStatus() {
		return paymentStatus;
	}
	public void setPaymentStatus(int paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	public String getPaymentNote() {
		return paymentNote;
	}
	public void setPaymentNote(String paymentNote) {
		this.paymentNote = paymentNote;
	}
	public int getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(int paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public String getInvoiceNo() {
		return invoiceNo;
	}
	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String paymentFileUid;
	public String paymentFee;
	public double paymentAmount;
	public int paymentStatus;
	public String paymentNote;
	public int paymentMethod;
	public String invoiceNo;
	public Date createDate;
}
