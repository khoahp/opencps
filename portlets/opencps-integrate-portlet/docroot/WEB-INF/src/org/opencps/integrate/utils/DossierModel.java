package org.opencps.integrate.utils;

import java.util.Date;

public class DossierModel {
	
	public static final int DOSSIER_SOURCE_DIRECT = 0;
	public static final int DOSSIER_SOURCE_INDIRECT = 1;

	public static final String DOSSIER_STATUS_NEW = "new";
	public static final String DOSSIER_STATUS_RECEIVING = "receiving";
	public static final String DOSSIER_STATUS_WAITING = "waiting";
	public static final String DOSSIER_STATUS_PAYING = "paying";
	public static final String DOSSIER_STATUS_DENIED = "denied";
	public static final String DOSSIER_STATUS_RECEIVED = "received";
	public static final String DOSSIER_STATUS_PROCESSING = "processing";
	public static final String DOSSIER_STATUS_CANCELED = "canceled";
	public static final String DOSSIER_STATUS_DONE = "done";
	public static final String DOSSIER_STATUS_ARCHIVED = "archived";
	public static final String DOSSIER_STATUS_SYSTEM = "system";
	public static final String DOSSIER_STATUS_ENDED = "ended";
	public static final String DOSSIER_STATUS_ERROR = "error";
	public static final String ACTION_SUBMIT_VALUE = "submit";


	public String getReferenceUid() {
		return referenceUid;
	}
	public void setReferenceUid(String referenceUid) {
		this.referenceUid = referenceUid;
	}
	public String getServiceCode() {
		return serviceCode;
	}
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getAgencyCode() {
		return agencyCode;
	}
	public void setAgencyCode(String agencyCode) {
		this.agencyCode = agencyCode;
	}
	public String getAgencyName() {
		return agencyName;
	}
	public void setAgencyName(String agencyName) {
		this.agencyName = agencyName;
	}
	public String getApplicantName() {
		return applicantName;
	}
	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
	}
	public String getApplicantIdType() {
		return applicantIdType;
	}
	public void setApplicantIdType(String applicantIdType) {
		this.applicantIdType = applicantIdType;
	}
	public String getApplicantIdNo() {
		return applicantIdNo;
	}
	public void setApplicantIdNo(String applicantIdNo) {
		this.applicantIdNo = applicantIdNo;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCityCode() {
		return cityCode;
	}
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getDistrictCode() {
		return districtCode;
	}
	public void setDistrictCode(String districtCode) {
		this.districtCode = districtCode;
	}
	public String getDistrictName() {
		return districtName;
	}
	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}
	public String getWardCode() {
		return wardCode;
	}
	public void setWardCode(String wardCode) {
		this.wardCode = wardCode;
	}
	public String getWardName() {
		return wardName;
	}
	public void setWardName(String wardName) {
		this.wardName = wardName;
	}
	public String getContactTelNo() {
		return contactTelNo;
	}
	public void setContactTelNo(String contactTelNo) {
		this.contactTelNo = contactTelNo;
	}
	public String getContactEmail() {
		return contactEmail;
	}
	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}
	public String getDossierNote() {
		return dossierNote;
	}
	public void setDossierNote(String dossierNote) {
		this.dossierNote = dossierNote;
	}
	public Date getSubmitDate() {
		return submitDate;
	}
	public void setSubmitDate(Date submitDate) {
		this.submitDate = submitDate;
	}
	public Date getReceiveDate() {
		return receiveDate;
	}
	public void setReceiveDate(Date receiveDate) {
		this.receiveDate = receiveDate;
	}
	public String getDossierNo() {
		return dossierNo;
	}
	public void setdDossierNo(String dossierNo) {
		this.dossierNo = dossierNo;
	}
	public String getDueDate() {
		return dueDate;
	}
	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}
	public Date getFinishDate() {
		return finishDate;
	}
	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public String getDossierStatus() {
		return dossierStatus;
	}
	public void setDossierStatus(String dossierStatus) {
		this.dossierStatus = dossierStatus;
	}
	public String getStatusText() {
		return statusText;
	}
	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}
	public String referenceUid;
	public String serviceCode;
	public String serviceName;
	public String agencyCode;
	public String agencyName;
	public String applicantName;
	public String applicantIdType;
	public String applicantIdNo;
	public String address;
	public String cityCode;
	public String cityName;
	public String districtCode;
	public String districtName;
	public String wardCode;
	public String wardName;
	public String contactTelNo;
	public String contactEmail;
	public String dossierNote;
	public Date submitDate;
	public Date receiveDate;
	public String dossierNo;
	public String dueDate;
	public Date finishDate;
	public Date createDate;
	public Date modifiedDate;
	public String dossierStatus;
	public String statusText;
}
