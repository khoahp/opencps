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

package org.opencps.dossiermgt.search;

import java.util.Date;

import javax.portlet.PortletRequest;

import org.opencps.util.DateTimeUtil;

import com.liferay.portal.kernel.dao.search.DAOParamUtil;
import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;

/**
 * @author trungnt
 */
public class DossierDisplayTerms extends DisplayTerms {

	public static final String COMPANY_ID = "companyId";
	public static final String CREATE_DATE = "createDate";
	public static final String GROUP_ID = "groupId";
	public static final String MODIFIED_DATE = "modifiedDate";
	public static final String SERVICE_MODE = "serviceMode";
	public static final String COUNTER = "counter";
	public static final String RECEPTION_NO = "receptionNo";
	public static final String DOSSIER_STATUS = "dossierStatus";
	public static final String DOSSIER_SOURCE = "dossierSource";
	public static final String DOSSIER_ID = "dossierId";
	public static final String FILE_GROUP_ID = "fileGroupId";
	public static final String OWNERORGANIZATION_ID = "ownerOrganizationId";
	public static final String SERVICE_CONFIG_ID = "serviceConfigId";
	public static final String SERVICE_INFO_ID = "serviceInfoId";
	public static final String DOSSIER_TEMPLATE_ID = "dossierTemplateId";
	public static final String TEMPLATE_FILE_NO = "templateFileNo";
	public static final String GOVAGENCY_ORGANIZATION_ID =
		"govAgencyOrganizationId";
	public static final String SUBJECT_ID = "subjectId";
	public static final String EXTERNALREF_NO = "externalRefNo";
	public static final String EXTERNALREF_URL = "externalRefUrl";
	public static final String SERVICE_DOMAIN_INDEX = "serviceDomainIndex";
	public static final String SERVICE_DOMAIN_CODE = "serviceDomainCode";
	public static final String SERVICE_ADMINISTRATION_INDEX =
		"serviceAdministrationIndex";
	public static final String GOVAGENCY_CODE = "govAgencyCode";
	public static final String GOVAGENCY_NAME = "govAgencyName";
	public static final String SUBJECT_NAME = "subjectName";
	public static final String ADDRESS = "address";
	public static final String CITY_CODE = "cityCode";
	public static final String CITY_NAME = "cityName";
	public static final String CITY_ID = "cityId";
	public static final String DISTRICT_CODE = "districtCode";
	public static final String DISTRICT_NAME = "districtName";
	public static final String DISTRICT_ID = "districtId";
	public static final String WARD_CODE = "wardCode";
	public static final String WARD_NAME = "wardName";
	public static final String WARD_ID = "wardId";
	public static final String CONTACT_NAME = "contactName";
	public static final String CONTACT_TEL_NO = "contactTelNo";
	public static final String CONTACT_EMAIL = "contactEmail";
	public static final String NOTE = "note";
	public static final String SUBMIT_DATETIME = "submitDatetime";
	public static final String RECEIVE_DATETIME = "receiveDatetime";
	public static final String ESTIMATE_DATETIME = "estimateDatetime";
	public static final String FINISH_DATETIME = "finishDatetime";
	public static final String SERVICE_NAME = "serviceName";
	public static final String SERVICE_NO = "serviceNo";
	public static final String ACCOUNT_TYPE = "accountType";
	public static final String REDIRECT_PAYMENT_URL = "redirectPaymentURL";
	public static final String SERVICE_DOMAIN_ID = "serviceDomainId";
	public static final String SERVICE_INFO_NO = "serviceInfoNo";
	public static final String SUBMIT_DATETIME_FROM = "submitDatetimeFrom";
	public static final String SUBMIT_DATETIME_TO = "submitDatetimeTo";
	
	public static final String USER_ID = "userId";

	public DossierDisplayTerms(PortletRequest portletRequest) {
		super(portletRequest);

		createDate = ParamUtil
			.getDate(portletRequest, CREATE_DATE, DateTimeUtil
				.getDateTimeFormat(DateTimeUtil._VN_DATE_TIME_FORMAT));

		dossierId = ParamUtil
			.getLong(portletRequest, DOSSIER_ID, 0L);

		govAgencyName = ParamUtil
			.getString(portletRequest, GOVAGENCY_NAME);

		modifiedDate = ParamUtil
			.getDate(portletRequest, MODIFIED_DATE, DateTimeUtil
				.getDateTimeFormat(DateTimeUtil._VN_DATE_TIME_FORMAT));

		serviceInfoId = ParamUtil
			.getLong(portletRequest, SERVICE_INFO_ID);

		serviceName = ParamUtil
			.getString(portletRequest, SERVICE_NAME);

		serviceNo = ParamUtil
			.getString(portletRequest, SERVICE_NO);

		receiveDatetime = ParamUtil
			.getDate(portletRequest, RECEIVE_DATETIME, DateTimeUtil
				.getDateTimeFormat(DateTimeUtil._VN_DATE_TIME_FORMAT));
		receptionNo = ParamUtil
			.getString(portletRequest, RECEPTION_NO);

		dossierStatus = ParamUtil
			.getString(portletRequest, DOSSIER_STATUS,
				StringPool.BLANK);

		serviceDomainCode = ParamUtil
			.getString(portletRequest, SERVICE_DOMAIN_CODE);

		userId = ParamUtil
			.getLong(portletRequest, USER_ID);

		groupId = setGroupId(portletRequest);
		
		serviceDomainIndex = ParamUtil
				.getString(portletRequest, SERVICE_DOMAIN_INDEX);
		
		serviceInfoNo = ParamUtil
				.getString(portletRequest, SERVICE_INFO_NO);
		
		setSubmitDateTimeFrom(ParamUtil
				.getString(portletRequest, SUBMIT_DATETIME_FROM));
		
		setSubmitDateTimeTo(ParamUtil
				.getString(portletRequest, SUBMIT_DATETIME_TO));
		
		//if(Validator.isNotNull(submitDateTimeFrom)) {
			Date submitDateTimeFromDate = GetterUtil.getDate(getSubmitDateTimeFrom(), DateTimeUtil.getDateTimeFormat("MM/dd/yyyy"));
			
			if(submitDateTimeFromDate != null) {
				indexerSubmitDateTimeFrom = DateTimeUtil.indexerDateFormat.format(submitDateTimeFromDate);
			}
		//}
		
		//if(Validator.isNotNull(submitDateTimeTo)) {
			Date submitDateTimeToDate = GetterUtil.getDate(getSubmitDateTimeTo(), DateTimeUtil.getDateTimeFormat("MM/dd/yyyy"));
			
			if(submitDateTimeToDate != null) {
				indexerSubmitDateTimeTo = DateTimeUtil.indexerDateFormat.format(submitDateTimeToDate);
			}
		//}
		
		setDomainCode(DAOParamUtil
			.getString(portletRequest, SERVICE_DOMAIN_CODE));

		govAgencyName = DAOParamUtil
			.getString(portletRequest, GOVAGENCY_NAME);
		
		setServiceDomainId(DAOParamUtil
				.getString(portletRequest, "serviceDomainId"));
	}

	public long setGroupId(PortletRequest portletRequest) {

		groupId = ParamUtil
			.getLong(portletRequest, GROUP_ID);

		if (groupId != 0) {
			return groupId;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest
			.getAttribute(WebKeys.THEME_DISPLAY);

		return themeDisplay
			.getScopeGroupId();
	}

	public long getGroupId() {

		return groupId;
	}

	public int getServiceMode() {

		return serviceMode;
	}

	public void setServiceMode(int serviceMode) {

		this.serviceMode = serviceMode;
	}

	public int getCounter() {

		return counter;
	}

	public void setCounter(int counter) {

		this.counter = counter;
	}

	public String getReceptionNo() {

		return receptionNo;
	}

	public void setReceptionNo(String receptionNo) {

		this.receptionNo = receptionNo;
	}

	public String getServiceName() {

		return serviceName;
	}

	public void setServiceName(String serviceName) {

		this.serviceName = serviceName;
	}

	public String getDossierStatus() {

		return dossierStatus;
	}

	public void setDossierStatus(String dossierStatus) {

		this.dossierStatus = dossierStatus;
	}

	public int getDossierSource() {

		return dossierSource;
	}

	public void setDossierSource(int dossierSource) {

		this.dossierSource = dossierSource;
	}

	public long getDossierId() {

		return dossierId;
	}

	public void setDossierId(long dossierId) {

		this.dossierId = dossierId;
	}

	public long getCompanyId() {

		return companyId;
	}

	public void setCompanyId(long companyId) {

		this.companyId = companyId;
	}

	public long getUserId() {

		return userId;
	}

	public void setUserId(long userId) {

		this.userId = userId;
	}

	public long getOwnerOrganizationId() {

		return ownerOrganizationId;
	}

	public void setOwnerOrganizationId(long ownerOrganizationId) {

		this.ownerOrganizationId = ownerOrganizationId;
	}

	public long getServiceConfigId() {

		return serviceConfigId;
	}

	public void setServiceConfigId(long serviceConfigId) {

		this.serviceConfigId = serviceConfigId;
	}

	public long getServiceInfoId() {

		return serviceInfoId;
	}

	public void setServiceInfoId(long serviceInfoId) {

		this.serviceInfoId = serviceInfoId;
	}

	public long getDossierTemplateId() {

		return dossierTemplateId;
	}

	public void setDossierTemplateId(long dossierTemplateId) {

		this.dossierTemplateId = dossierTemplateId;
	}

	public long getGovAgencyOrganizationId() {

		return govAgencyOrganizationId;
	}

	public void setGovAgencyOrganizationId(long govAgencyOrganizationId) {

		this.govAgencyOrganizationId = govAgencyOrganizationId;
	}

	public long getSubjectId() {

		return subjectId;
	}

	public void setSubjectId(long subjectId) {

		this.subjectId = subjectId;
	}

	public String getExternalRefNo() {

		return externalRefNo;
	}

	public void setExternalRefNo(String externalRefNo) {

		this.externalRefNo = externalRefNo;
	}

	public String getExternalRefUrl() {

		return externalRefUrl;
	}

	public void setExternalRefUrl(String externalRefUrl) {

		this.externalRefUrl = externalRefUrl;
	}

	public String getServiceDomainIndex() {

		return serviceDomainIndex;
	}

	public void setServiceDomainIndex(String serviceDomainIndex) {

		this.serviceDomainIndex = serviceDomainIndex;
	}

	public String getServiceAdministrationIndex() {

		return serviceAdministrationIndex;
	}

	public void setServiceAdministrationIndex(
		String serviceAdministrationIndex) {

		this.serviceAdministrationIndex = serviceAdministrationIndex;
	}

	public String getGovAgencyCode() {

		return govAgencyCode;
	}

	public void setGovAgencyCode(String govAgencyCode) {

		this.govAgencyCode = govAgencyCode;
	}

	public String getGovAgencyName() {

		return govAgencyName;
	}

	public void setGovAgencyName(String govAgencyName) {

		this.govAgencyName = govAgencyName;
	}

	public String getSubjectName() {

		return subjectName;
	}

	public void setSubjectName(String subjectName) {

		this.subjectName = subjectName;
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

	public String getContactName() {

		return contactName;
	}

	public void setContactName(String contactName) {

		this.contactName = contactName;
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

	public String getNote() {

		return note;
	}

	public void setNote(String note) {

		this.note = note;
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

	public Date getSubmitDatetime() {

		return submitDatetime;
	}

	public void setSubmitDatetime(Date submitDatetime) {

		this.submitDatetime = submitDatetime;
	}

	public Date getReceiveDatetime() {

		return receiveDatetime;
	}

	public void setReceiveDatetime(Date receiveDatetime) {

		this.receiveDatetime = receiveDatetime;
	}

	public Date getEstimateDatetime() {

		return estimateDatetime;
	}

	public void setEstimateDatetime(Date estimateDatetime) {

		this.estimateDatetime = estimateDatetime;
	}

	public Date getFinishDatetime() {

		return finishDatetime;
	}

	public void setFinishDatetime(Date finishDatetime) {

		this.finishDatetime = finishDatetime;
	}

	public String getServiceNo() {

		return serviceNo;
	}

	public void setServiceNo(String serviceNo) {

		this.serviceNo = serviceNo;
	}

	public String getServiceDomainCode() {

		return serviceDomainCode;
	}

	public void setServiceDomainCode(String serviceDomainCode) {

		this.serviceDomainCode = serviceDomainCode;
	}

	public String getRedirectPaymentURL() {

		return redirectPaymentURL;
	}

	public void setRedirectPaymentURL(String redirectPaymentURL) {

		this.redirectPaymentURL = redirectPaymentURL;
	}

	public String getServiceInfoNo() {
		return serviceInfoNo;
	}

	public void setServiceInfoNo(String serviceInfoNo) {
		this.serviceInfoNo = serviceInfoNo;
	}

	public String getIndexerSubmitDateTimeFrom() {
		return indexerSubmitDateTimeFrom;
	}

	public void setIndexerSubmitDateTimeFrom(String indexerSubmitDateTimeFrom) {
		this.indexerSubmitDateTimeFrom = indexerSubmitDateTimeFrom;
	}

	public String getIndexerSubmitDateTimeTo() {
		return indexerSubmitDateTimeTo;
	}

	public void setIndexerSubmitDateTimeTo(String indexerSubmitDateTimeTo) {
		this.indexerSubmitDateTimeTo = indexerSubmitDateTimeTo;
	}

	public String getDomainCode() {
		return domainCode;
	}

	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}

	public String getServiceDomainId() {
		return serviceDomainId;
	}

	public void setServiceDomainId(String serviceDomainId) {
		this.serviceDomainId = serviceDomainId;
	}

	public String getSubmitDateTimeFrom() {
		return submitDateTimeFrom;
	}

	public void setSubmitDateTimeFrom(String submitDateTimeFrom) {
		this.submitDateTimeFrom = submitDateTimeFrom;
	}

	public String getSubmitDateTimeTo() {
		return submitDateTimeTo;
	}

	public void setSubmitDateTimeTo(String submitDateTimeTo) {
		this.submitDateTimeTo = submitDateTimeTo;
	}

	private int serviceMode;
	private int counter;
	private String dossierStatus;
	private int dossierSource;

	private long dossierId;
	private long groupId;
	private long companyId;
	private long userId;

	private long ownerOrganizationId;
	private long serviceConfigId;
	private long serviceInfoId;
	private long dossierTemplateId;
	private long govAgencyOrganizationId;
	private long subjectId;

	private String receptionNo;
	private String serviceName;
	private String serviceNo;
	private String externalRefNo;
	private String externalRefUrl;
	private String serviceDomainIndex;
	private String serviceDomainCode;
	private String serviceAdministrationIndex;
	private String govAgencyCode;
	private String govAgencyName;
	private String subjectName;
	private String address;
	private String cityCode;
	private String cityName;
	private String districtCode;
	private String districtName;
	private String wardCode;
	private String wardName;
	private String contactName;
	private String contactTelNo;
	private String contactEmail;
	private String note;
	private Date createDate;
	private Date modifiedDate;
	private Date submitDatetime;
	private Date receiveDatetime;
	private Date estimateDatetime;
	private Date finishDatetime;
	private String redirectPaymentURL;
	private String serviceInfoNo;
	private String submitDateTimeFrom;
	private String submitDateTimeTo;
	private String indexerSubmitDateTimeFrom;
	private String indexerSubmitDateTimeTo;
	private String domainCode;
	private String serviceDomainId;
}
