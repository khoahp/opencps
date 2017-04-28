package org.opencps.integrate.utils;

import org.opencps.accountmgt.model.Business;
import org.opencps.accountmgt.model.Citizen;
import org.opencps.accountmgt.service.BusinessLocalServiceUtil;
import org.opencps.accountmgt.service.CitizenLocalServiceUtil;

import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;

public class UserUtils {

	public static final String APPLICANT_TYPE_CITY = "CMT";
	public static final String APPLICANT_TYPE_BUSINESS = "MST";
	
	
	/**
	 * @param companyId
	 * @param email
	 * @return
	 */
	public static User getUser(long companyId, String email) {
		
		User user = null;
		
		try {
			user = UserLocalServiceUtil.getUserByEmailAddress(companyId, email);
		} catch (Exception e) {
			
		}
		
		return user;
	}

	public AccountModel getAccountModel(long userId) {

		AccountModel am = new AccountModel();

		Citizen ctz = getCitizen(userId);
		Business bsn = getBusiness(userId);

		User user = getUserId(userId);

		if (Validator.isNotNull(user)) {

			am.setScreenName(user.getScreenName());

			if (Validator.isNotNull(ctz)) {
				am.setApplicantName(ctz.getFullName());
				am.setApplicantIdType(APPLICANT_TYPE_CITY);
				am.setApplicantIdNo(ctz.getPersonalId());
				am.setApplicantIdDate(APIUtils.formatDateTime(ctz
						.getCreateDate()));
				am.setCityCode(ctz.getCityCode());
				am.setCityName(APIUtils.getDictItemName(ctz.getCityCode()));
				am.setDistrictCode(ctz.getDistrictCode());
				am.setDistrictName(APIUtils.getDictItemName(ctz
						.getDistrictCode()));
				am.setWardCode(ctz.getWardCode());
				am.setWardName(APIUtils.getDictItemName(ctz.getWardCode()));
				am.setContactTelNo(ctz.getTelNo());
				am.setContactEmail(ctz.getEmail());
			}

			if (Validator.isNotNull(bsn)) {
				am.setApplicantName(bsn.getName());
				am.setApplicantIdType(APPLICANT_TYPE_BUSINESS);
				am.setApplicantIdNo(bsn.getIdNumber());
				am.setApplicantIdDate(APIUtils.formatDateTime(bsn
						.getCreateDate()));
				am.setCityCode(bsn.getCityCode());
				am.setCityName(APIUtils.getDictItemName(bsn.getCityCode()));
				am.setDistrictCode(bsn.getDistrictCode());
				am.setDistrictName(APIUtils.getDictItemName(bsn
						.getDistrictCode()));
				am.setWardCode(bsn.getWardCode());
				am.setWardName(APIUtils.getDictItemName(bsn.getWardCode()));
				am.setContactTelNo(bsn.getTelNo());
				am.setContactEmail(bsn.getEmail());
			}

			if (Validator.isNull(ctz) && Validator.isNull(bsn)) {
				am = null;
			}

		} else {
			am = null;
		}

		return am;
	}

	private User getUserId(long userId) {

		User user = null;

		try {
			user = UserLocalServiceUtil.getUser(userId);
		} catch (Exception e) {

		}

		return user;
	}

	private Citizen getCitizen(long userId) {
		Citizen ctz = null;

		try {
			ctz = CitizenLocalServiceUtil.getByMappingUserId(userId);
		} catch (Exception e) {

		}

		return ctz;
	}

	private Business getBusiness(long userId) {
		Business bsn = null;

		try {
			bsn = BusinessLocalServiceUtil.getBusiness(userId);
		} catch (Exception e) {

		}

		return bsn;
	}

}
