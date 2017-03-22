/**
 * OpenCPS is the open source Core Public Services software Copyright (C)
 * 2016-present OpenCPS community This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3
 * of the License, or any later version. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details. You should have received a
 * copy of the GNU Affero General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>
 */
package org.opencps.postal.search;

import java.util.Date;

import javax.portlet.PortletRequest;

import org.opencps.util.DateTimeUtil;

import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.util.ParamUtil;

public class PostalConfigDisplayTerms extends DisplayTerms {
	public static final String COMPANY_ID = "companyId";
	public static final String CREATE_DATE = "createDate";
	public static final String GROUP_ID = "groupId";
	public static final String MODIFIED_DATE = "modifiedDate";
	public static final String USER_ID = "userId";

	public static final String POSTAL_CONFIG_ID = "postalConfigId";
	public static final String GOV_AGENCY_ORGANIZATION_ID = "govAgencyOrganizationId";
	public static final String POSTAL_DOMAIN = "postalDomain";
	public static final String POSTAL_CUSTOMERCODE = "postalCustomerCode";
	public static final String POSTAL_TOKENCODE = "postalTokenCode";
	public static final String POSTAL_GATETYPE = "postalGateType";
	public static final String STATUS = "status";
	public static final String ACTIVE = "1";
	public static final String DISABLE = "0";
	public static final String CHECKBOX = "Checkbox";

	public PostalConfigDisplayTerms(PortletRequest portletRequest) {

		super(portletRequest);

		createDate = ParamUtil.getDate(portletRequest, CREATE_DATE,
				DateTimeUtil
						.getDateTimeFormat(DateTimeUtil._VN_DATE_TIME_FORMAT));

		modifiedDate = ParamUtil.getDate(portletRequest, MODIFIED_DATE,
				DateTimeUtil
						.getDateTimeFormat(DateTimeUtil._VN_DATE_TIME_FORMAT));
		userId = ParamUtil.getLong(portletRequest, USER_ID);

	}

	protected long userId;
	protected Date createDate;
	protected Date modifiedDate;

	protected long govAgencyOrganizationId;
	protected long postalConfigId;
	protected String postalDomain;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
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

	public long getGovAgencyOrganizationId() {
		return govAgencyOrganizationId;
	}

	public void setGovAgencyOrganizationId(long govAgencyOrganizationId) {
		this.govAgencyOrganizationId = govAgencyOrganizationId;
	}

	public long getPostalConfigId() {
		return postalConfigId;
	}

	public void setPostalConfigId(long postalConfigId) {
		this.postalConfigId = postalConfigId;
	}

	public String getPostalDomain() {
		return postalDomain;
	}

	public void setPostalDomain(String postalDomain) {
		this.postalDomain = postalDomain;
	}

	public String getPostalCustomerCode() {
		return postalCustomerCode;
	}

	public void setPostalCustomerCode(String postalCustomerCode) {
		this.postalCustomerCode = postalCustomerCode;
	}

	public String getPostalTokenCode() {
		return postalTokenCode;
	}

	public void setPostalTokenCode(String postalTokenCode) {
		this.postalTokenCode = postalTokenCode;
	}

	public String getPostalGateType() {
		return postalGateType;
	}

	public void setPostalGateType(String postalGateType) {
		this.postalGateType = postalGateType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	protected String postalCustomerCode;
	protected String postalTokenCode;
	protected String postalGateType;
	protected String status;

}
