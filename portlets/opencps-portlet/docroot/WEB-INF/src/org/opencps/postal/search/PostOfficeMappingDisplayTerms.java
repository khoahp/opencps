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

public class PostOfficeMappingDisplayTerms extends DisplayTerms {
	public static final String COMPANY_ID = "companyId";
	public static final String CREATE_DATE = "createDate";
	public static final String GROUP_ID = "groupId";
	public static final String MODIFIED_DATE = "modifiedDate";
	public static final String USER_ID = "userId";

	public static final String POSTOFFICEMAPPING_ID = "postOfficeMappingId";
	public static final String POSTOFFICE_CODE = "postOfficeCode";
	public static final String POSTOFFICE_NAME = "postOfficeName";
	public static final String OPENCPS_CITY_CODE = "opencpsCityCode";
	
	public static final String POSTOFFICE_CODE_TXT = "txt_postOfficeCode";
	public static final String POSTOFFICE_NAME_TXT = "txt_postOfficeName";
	public static final String OPENCPS_CITY_CODE_TXT = "txt_opencpsCityCode";
	

	public PostOfficeMappingDisplayTerms(PortletRequest portletRequest) {

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

	protected int postOfficeMappingId;
	protected int postOfficeCode;
	protected String postOfficeName;
	protected int opencpsCityCode;

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

	public int getPostOfficeMappingId() {
		return postOfficeMappingId;
	}

	public void setPostOfficeMappingId(int postOfficeMappingId) {
		this.postOfficeMappingId = postOfficeMappingId;
	}

	public int getPostOfficeCode() {
		return postOfficeCode;
	}

	public void setPostOfficeCode(int postOfficeCode) {
		this.postOfficeCode = postOfficeCode;
	}

	public String getPostOfficeName() {
		return postOfficeName;
	}

	public void setPostOfficeName(String postOfficeName) {
		this.postOfficeName = postOfficeName;
	}

	public int getOpencpsCityCode() {
		return opencpsCityCode;
	}

	public void setOpencpsCityCode(int opencpsCityCode) {
		this.opencpsCityCode = opencpsCityCode;
	}

	

}
