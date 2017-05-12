package org.opencps.integrate.utils;

import org.opencps.accountmgt.model.Business;
import org.opencps.accountmgt.model.Citizen;
import org.opencps.accountmgt.service.BusinessLocalServiceUtil;
import org.opencps.accountmgt.service.CitizenLocalServiceUtil;
import org.opencps.integrate.dao.model.ForgotPass;
import org.opencps.integrate.dao.service.ForgotPassLocalServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.util.PwdGenerator;

public class UserUtils {

	public static final String APPLICANT_TYPE_CITY = "CMT";
	public static final String APPLICANT_TYPE_BUSINESS = "MST";
	private static Log _log = LogFactoryUtil.getLog(UserUtils.class);
	
	public static ForgotPass createVerifyCode(String verifyCode, long userid) {
		ForgotPass fg = null;

		try {
			
			//reset all AuthCode
			
			ForgotPassLocalServiceUtil.invalidCode(userid);
			
			while (Validator.isNull(fg)) {

				ForgotPass check = null;
				
				try {
					check = ForgotPassLocalServiceUtil
							.getByVerifyCode(verifyCode);
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				if (Validator.isNull(check)) {
					fg = ForgotPassLocalServiceUtil.addVerifyCode(verifyCode, userid);
				}
				
				_log.info("FORGOTPASS ** CODE : " + verifyCode);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return fg;
	}
	
	public static String generatorVerifyCode() {
		return PwdGenerator.getPassword(10);
		
	}
	
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
				am.setAddress(ctz.getAddress());
				
			}else if (Validator.isNotNull(bsn)) {
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
				am.setAddress(bsn.getAddress());
			}else if(Validator.isNotNull(user)) {
				am.setApplicantName(user.getScreenName());
				am.setApplicantIdDate(APIUtils.formatDateTime(user
						.getCreateDate()));
				am.setContactEmail(user.getEmailAddress());
				
			}else{
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
