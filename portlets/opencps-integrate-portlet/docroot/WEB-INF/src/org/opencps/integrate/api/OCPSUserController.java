package org.opencps.integrate.api;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.opencps.accountmgt.model.Business;
import org.opencps.accountmgt.model.Citizen;
import org.opencps.accountmgt.service.BusinessLocalServiceUtil;
import org.opencps.accountmgt.service.CitizenLocalServiceUtil;
import org.opencps.integrate.dao.InvalidOldPassException;
import org.opencps.integrate.dao.model.ForgotPass;
import org.opencps.integrate.dao.model.IntegrateAPI;
import org.opencps.integrate.dao.service.ForgotPassLocalServiceUtil;
import org.opencps.integrate.dao.service.IntegrateAPILocalServiceUtil;
import org.opencps.integrate.utils.APIUtils;
import org.opencps.integrate.utils.AccountModel;
import org.opencps.integrate.utils.DossierUtils;
import org.opencps.integrate.utils.MessageBusUtil;
import org.opencps.integrate.utils.UserUtils;

import com.liferay.mail.service.MailServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.mail.MailMessage;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;


@Path("/api")
public class OCPSUserController {
	
	public static int GROUPID = 20182;
	public static final String PORTAL_URL = "http://202.151.168.104:2180";
	
	@GET
	@Path("/forgot/user/{authcode: .*}/verify")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response verifyAuthCode(@PathParam("authcode") String authcode) {

		JSONObject resp = JSONFactoryUtil.createJSONObject();

		try {
			
			boolean valid = validateAuthCode(authcode);
			
			if (valid) {
				resp.put("Result", "Validate");

				return Response.status(200).entity(resp.toString()).build();

			} else {
				resp.put("Result", "ExpriedDate");

				return Response.status(404).entity(resp.toString()).build();

			}
			

		} catch (Exception e) {

			resp.put("Result", "Fail");

			return Response.status(404).entity(resp.toString()).build();
		}

	}

	@PUT
	@Path("/users/{userid: .*}/password")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response changePassword(@HeaderParam("apikey") String apikey,
			@Context HttpServletRequest request, String body,
			@PathParam("userid") long userid) {

		JSONObject resp = JSONFactoryUtil.createJSONObject();
		
		
		ServiceContext context = DossierUtils.getServletContext(request);

		OCPSAuth auth = new OCPSAuth();
		
		IntegrateAPI api = auth.auth(apikey);

		if (Validator.isNotNull(api)) {

			try {
				JSONObject input = JSONFactoryUtil.createJSONObject(body);

				User user = UserLocalServiceUtil.getUser(userid);
				
				String oldpass = input.getString("oldpass");
				
				String newpass = input.getString("newpass");
				
				User basicLogin = IntegrateAPILocalServiceUtil.basicLogin(
						context.getCompanyId(), user.getEmailAddress(),
						oldpass);
				
				if (Validator.isNull(basicLogin)) {
					throw new InvalidOldPassException();
				}
				
				UserLocalServiceUtil.updatePassword(userid, newpass, newpass, false);
				
				//UserServiceUtil.updatePassword(userid, newpass, newpass, false);
				
				resp.put("Result", "Update");
				
				return Response.status(200).entity(resp.toString()).build();
			} catch (Exception e) {
				
				resp.put("Result", "Error");
				
				if (e instanceof InvalidOldPassException) {
					resp.put("ErrorMessage",
							APIUtils.getLanguageValue("invalid-old-pass-input"));

				} else {
					resp.put("ErrorMessage",
							APIUtils.getLanguageValue("invalid-userid"));

				}
				
				_log.info(e);
				
				return Response.status(404).entity(resp.toString()).build();
			}

		} else {
			resp.put("Result", "Error");
			resp.put("ErrorMessage",
					APIUtils.getLanguageValue("you-dont-have-auth"));
			// Not validate
			return Response.status(401).entity(resp.toString()).build();
		}
	}

	@PUT
	@Path("/users/{authcode: .*}/reset")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response resetPassword(@Context HttpServletRequest request, String body,
			@PathParam("authcode") String authcode) {
		
		JSONObject resp = JSONFactoryUtil.createJSONObject();
		
		boolean valid = validateAuthCode(authcode);
		
		if (valid) {
			
			try {
				
				JSONObject obj = JSONFactoryUtil.createJSONObject(body);

				ForgotPass forgot = ForgotPassLocalServiceUtil
						.getByVerifyCode(authcode);

				UserLocalServiceUtil.updatePassword(forgot.getUserid(),
						obj.getString("newpass"), obj.getString("newpass"),
						false);
				
				ForgotPassLocalServiceUtil.inuse(authcode);
				
				resp.put("Result", "Update");
				
				return Response.status(200).entity(resp.toString()).build();

			} catch (Exception e) {
				resp.put("Result", "Update");
				
				return Response.status(200).entity(resp.toString()).build();

			}
			

		} else {
			resp.put("Result", "InvalidAuthCode");
			
			return Response.status(200).entity(resp.toString()).build();
		}
 		
	}


	@PUT
	@Path("/users/{userid: .*}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response updateUser(@HeaderParam("apikey") String apikey,
			@Context HttpServletRequest request, String body,
			@PathParam("userid") long userid) {

		JSONObject resp = JSONFactoryUtil.createJSONObject();

		OCPSAuth auth = new OCPSAuth();

		IntegrateAPI api = auth.auth(apikey);

		if (Validator.isNotNull(api)) {

			try {
				JSONObject jsInput = JSONFactoryUtil.createJSONObject(body);
				AccountModel acc = getAccountModelInput(jsInput);

				User userLogin = UserLocalServiceUtil.getUser(api.getUserId());

				if (userLogin.getEmailAddress().contentEquals(
						acc.getContactEmail())) {

					if (acc.getApplicantIdType().equalsIgnoreCase(
							UserUtils.APPLICANT_TYPE_CITY)) {
						Citizen citizen = CitizenLocalServiceUtil
								.getByMappingUserId(api.getUserId());

						citizen.setFullName(acc.getApplicantName());
						citizen.setPersonalId(acc.getApplicantIdNo());
						citizen.setAddress(acc.getAddress());
						citizen.setCityCode(acc.getCityCode());
						citizen.setDistrictCode(acc.getDistrictCode());
						citizen.setWardCode(acc.getWardCode());
						citizen.setTelNo(acc.getContactTelNo());
						citizen.setModifiedDate(new Date());
						
						CitizenLocalServiceUtil.updateCitizen(citizen);
						
						
					} else {
						Business business = BusinessLocalServiceUtil
								.getBusiness(api.getUserId());
						business.setName(acc.getApplicantName());
						business.setIdNumber(acc.getApplicantIdNo());
						business.setAddress(acc.getAddress());
						business.setCityCode(acc.getCityCode());
						business.setDistrictCode(acc.getDistrictCode());
						business.setWardCode(acc.getWardCode());
						business.setTelNo(acc.getContactTelNo());
						business.setModifiedDate(new Date());

					}
					
					resp.put("ApiKey", getAPI(api.getUserId()));
					resp.put("UserId", api.getUserId());
					resp.put("ScreenName", acc.getScreenName());
					resp.put("ApplicantName", acc.getApplicantName());
					resp.put("ApplicantIdType", acc.getApplicantIdType());
					resp.put("ApplicantIdNo", acc.getApplicantIdNo());
					resp.put("ApplicantIdDate", acc.getApplicantIdDate());
					resp.put("CityCode", acc.getCityCode());
					resp.put("CityName", acc.getCityName());
					resp.put("DistrictCode", acc.getDistrictCode());
					resp.put("DistrictName", acc.getDistrictName());
					resp.put("WardCode", acc.getWardCode());
					resp.put("WardName", acc.getWardName());
					resp.put("ContactTelNo", acc.getContactTelNo());
					resp.put("ContactEmail", acc.getContactEmail());

					return Response.status(200).entity(resp.toString()).build();
				} else {
					
					resp.put("Result", "Error");
					resp.put("ErrorMessage",
							APIUtils.getLanguageValue("you-dont-have-permit"));
					return Response.status(404).entity(resp.toString()).build();
				}

			} catch (Exception e) {
				resp.put("Result", "Error");
				resp.put("ErrorMessage",
						APIUtils.getLanguageValue("invalid-body-input"));
				return Response.status(404).entity(resp.toString()).build();
			}

		} else {
			resp.put("Result", "Error");
			resp.put("ErrorMessage",
					APIUtils.getLanguageValue("you-dont-have-auth"));

			// Not validate
			return Response.status(401).entity(resp.toString()).build();
		}
	}

	@GET
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response login(@Context HttpServletRequest request,
			@HeaderParam("username") String username,
			@HeaderParam("password") String password) {

		JSONObject resp = JSONFactoryUtil.createJSONObject();

		long companyId = PortalUtil.getCompanyId(request);

		User user = login(companyId, username, password);

		if (Validator.isNull(user)) {
			return Response.status(401).entity(resp.toString()).build();
		} else {

			UserUtils userUtil = new UserUtils();

			AccountModel am = userUtil.getAccountModel(user.getUserId());

			if (Validator.isNotNull(am)) {
				
				resp.put("ApiKey", getAPI(user.getUserId()));
				resp.put("UserId", user.getUserId());
				resp.put("ScreenName", am.getScreenName());
				resp.put("ApplicantName", am.getApplicantName());
				resp.put("ApplicantIdType", am.getApplicantIdType());
				resp.put("ApplicantIdNo", am.getApplicantIdNo());
				resp.put("ApplicantIdDate", am.getApplicantIdDate());
				resp.put("CityCode", am.getCityCode());
				resp.put("CityName", am.getCityName());
				resp.put("DistrictCode", am.getDistrictCode());
				resp.put("DistrictName", am.getDistrictName());
				resp.put("WardCode", am.getWardCode());
				resp.put("WardName", am.getWardName());
				resp.put("ContactTelNo", am.getContactTelNo());
				resp.put("ContactEmail", am.getContactEmail());
				resp.put("Address", am.getAddress());

				return Response.status(200).entity(resp.toString()).build();

			} else {
				return Response.status(404).entity(resp.toString()).build();
			}

		}
	}

	
	
	/**
	 * @param companyId
	 * @param username
	 * @param password
	 * @return
	 */
	private User login(long companyId, String username, String password) {
		User user = null;
		try {
			user = IntegrateAPILocalServiceUtil.basicLogin(companyId, username,
					password);
		} catch (Exception e) {
			_log.error("Login Fail");
		}
		return user;
	}

	/**
	 * @param userId
	 * @return
	 */
	private String getAPI(long userId) {

		IntegrateAPI api = null;

		String tokenKey = StringPool.BLANK;

		try {
			api = IntegrateAPILocalServiceUtil.getAPIByUserId(userId);

			tokenKey = api.getApiKey();

		} catch (Exception e) {
			_log.debug("NoAPIKeyWithUserID" + new Date());
		}

		if (Validator.isNull(api)) {
			try {

				tokenKey = UUID.randomUUID().toString() + "-"
						+ Long.toString(userId);

				api = IntegrateAPILocalServiceUtil.addAPIKey(userId,
						StringPool.BLANK, tokenKey);
			} catch (Exception e) {
				_log.debug(e);
			}
		}

		return tokenKey;

	}
	
	@POST
	@Path("/register")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response addUser(@Context HttpServletRequest request, String body) {

		JSONObject resp = JSONFactoryUtil.createJSONObject();

		ServiceContext context = DossierUtils.getServletContext(request);

		context.setPortalURL(PORTAL_URL);

		try {

			JSONObject input = JSONFactoryUtil.createJSONObject(body);

			AccountModel acc = getAccountModelInput(input);

			String regEmail = acc.getContactEmail();

			User user = UserUtils.getUser(context.getCompanyId(), regEmail);

			if (Validator.isNull(user)) {

				Calendar cal = Calendar.getInstance();
				int birthDateDay = cal.get(Calendar.DAY_OF_MONTH);
				int birthDateMonth = cal.get(Calendar.MONTH);
				int birthDateYear = cal.get(Calendar.YEAR);

				if (acc.getApplicantIdType().equalsIgnoreCase(
						UserUtils.APPLICANT_TYPE_CITY)) {

					Citizen citizen = CitizenLocalServiceUtil.addCitizen(
							acc.getApplicantName(), StringPool.BLANK, 0,
							birthDateDay, birthDateMonth, birthDateYear,
							acc.getAddress(), acc.getCityCode(),
							acc.getDistrictCode(), acc.getWardCode(),
							APIUtils.getDictItemName(acc.getCityCode()),
							APIUtils.getDictItemName(acc.getDistrictCode()),
							APIUtils.getDictItemName(acc.getWardCode()),
							acc.getContactEmail(), StringPool.BLANK, GROUPID,
							StringPool.BLANK, StringPool.BLANK,
							StringPool.BLANK, null, 0, context);

					if (citizen != null) {
						User mappingUser = UserLocalServiceUtil.getUser(citizen
								.getMappingUserId());

						MessageBusUtil.sendEmailAddressVerification(
								citizen.getUuid(), mappingUser,
								acc.getContactEmail(),
								AccountModel.ACCOUNT_TYPE_CITIZEN,
								AccountModel.ACCOUNT_REG_TWO_STEP,
								"khoavd.it@gmail.com", context);

						CitizenLocalServiceUtil.updateStatus(
								citizen.getCitizenId(), context.getUserId(),
								AccountModel.ACCOUNT_STATUS_APPROVED);
					}

					resp.put("UserId", citizen.getMappingUserId());
				}

				if (acc.getApplicantIdType().equals(
						UserUtils.APPLICANT_TYPE_BUSINESS)) {

					Business business = BusinessLocalServiceUtil.addBusiness(
							acc.getApplicantName(), acc.getApplicantName(),
							acc.getApplicantName(), StringPool.BLANK,
							acc.getApplicantIdNo(), acc.getAddress(),
							acc.getCityCode(), acc.getDistrictCode(),
							acc.getWardCode(),
							APIUtils.getDictItemName(acc.getCityCode()),
							APIUtils.getDictItemName(acc.getDistrictCode()),
							APIUtils.getDictItemName(acc.getWardCode()),
							acc.getContactTelNo(), acc.getContactEmail(),
							acc.getApplicantName(), StringPool.BLANK,
							new String[] {}, birthDateDay, birthDateMonth,
							birthDateYear, 0, null, StringPool.BLANK,
							StringPool.BLANK, null, 0, context);

					if (business != null) {
						User mappingUser = UserLocalServiceUtil
								.getUser(business.getMappingUserId());

						MessageBusUtil.sendEmailAddressVerification(
								business.getUuid(), mappingUser,
								acc.getContactEmail(),
								AccountModel.ACCOUNT_STATUS_BUSINESS,
								AccountModel.ACCOUNT_REG_TWO_STEP,
								"khoavd.it@gmail.com", context);

						BusinessLocalServiceUtil.updateStatus(
								business.getBusinessId(), context.getUserId(),
								AccountModel.ACCOUNT_STATUS_APPROVED);
					}

					resp.put("UserId", business.getMappingUserId());

				}

				resp.put("Result", "New");

				return Response.status(200).entity(resp.toString()).build();

			} else {

				resp.put("Result", "Exist");
				resp.put("UserId", user.getUserId());
				resp.put("ErrorMessage",
						APIUtils.getLanguageValue("email-reg-exist"));

				return Response.status(408).entity(resp.toString()).build();
			}

		} catch (Exception e) {

			resp.put("Result", "Error");

			resp.put("ErrorMessage", APIUtils.getLanguageValue("register-fail"));

			return Response.status(404).entity(resp.toString()).build();
		}

	}

	private AccountModel getAccountModelInput(JSONObject input) {
		AccountModel acc = new AccountModel();
		
		try {
			acc.setScreenName(input.getString("ScreenName"));
			acc.setApplicantName(input.getString("ApplicantName"));
			acc.setApplicantIdType(input.getString("ApplicantIdType"));
			acc.setApplicantIdNo(input.getString("ApplicantIdNo"));
			acc.setAddress(input.getString("Address"));
			acc.setCityCode(input.getString("CityCode"));
			acc.setDistrictCode(input.getString("DistrictCode"));
			acc.setWardCode(input.getString("WardCode"));
			acc.setContactTelNo(input.getString("ContactTelNo"));
			acc.setContactEmail(input.getString("ContactEmail"));
		} catch (Exception e) {
			acc = null;
		}
		
		return acc;
	}
	
	@GET
	@Path("/forgot/user/{email}")
	public Response forgotPassword(@Context HttpServletRequest request,
			@PathParam("email") String email) {

		JSONObject resp = JSONFactoryUtil.createJSONObject();

		try {

			ServiceContext context = ServiceContextFactory.getInstance(request);

			User user = null;

			try {
				user = UserLocalServiceUtil.getUserByEmailAddress(
						context.getCompanyId(), email);
			} catch (PortalException e) {

			}

			if (Validator.isNull(user)) {

				resp.put("Result", "NoExist ");

				return Response.status(404).entity(resp.toString()).build();
			} else {

				UserUtils userUtil = new UserUtils();

				AccountModel acc = userUtil.getAccountModel(user.getUserId());

				if (Validator.isNotNull(acc)) {

					/*
					 * String fromName = PrefsPropsUtil.getString(
							user.getCompanyId(),
							PropsKeys.ADMIN_EMAIL_FROM_NAME);

					*/
					String fromAddress = PrefsPropsUtil.getString(
							user.getCompanyId(),
							PropsKeys.ADMIN_EMAIL_FROM_ADDRESS);

					String verifyCode = UserUtils.generatorVerifyCode();
					
					ForgotPass forgot = UserUtils.createVerifyCode(verifyCode, user.getUserId());
/*
					UserLocalServiceUtil.sendPassword(context.getCompanyId(),
							user.getEmailAddress(), fromName, fromAddress,
							StringPool.BLANK, StringPool.BLANK, context);
*/
					if (Validator.isNotNull(forgot)) {
						try {
							InternetAddress toAdd = new InternetAddress(email);
							InternetAddress fromAdd = new InternetAddress(fromAddress);
							
							MailMessage mailMessage = new MailMessage();
							
							mailMessage.setTo(toAdd);
							mailMessage.setFrom(fromAdd);
							mailMessage.setSubject(APIUtils.getLanguageValue("request-to-change-pass"));
							
							StringBuffer sb = new StringBuffer();
							
							sb.append(APIUtils.getLanguageValue("verify-code-to-reset-pass-is"));
							sb.append(" : </br>");
							sb.append(APIUtils.getLanguageValue(forgot.getVerifyCode()));
							sb.append(" : </br>");
							sb.append(APIUtils.getLanguageValue("date-to-expried"));
							sb.append(APIUtils.getLanguageValue(" : "));
							sb.append(APIUtils.formatDateTime(forgot.getExpiredDate()));
							sb.append(" : </br>");
							sb.append(APIUtils.getLanguageValue("Thanks!"));

							mailMessage.setBody(sb.toString());
							
							mailMessage.setHTMLFormat(true);
							
							MailServiceUtil.sendEmail(mailMessage);

							resp.put("Result", "Success");

							return Response.status(200).entity(resp.toString()).build();
							
						} catch (Exception e) {
							resp.put("Result", "Invalid-email");

							return Response.status(200).entity(resp.toString()).build();

						}

					} else {
						resp.put("Result", "Error");

						return Response.status(200).entity(resp.toString()).build();

					}
					
				} else {
					resp.put("Result", "NoExist ");

					return Response.status(401).entity(resp.toString()).build();
				}

			}
		} catch (PortalException | SystemException e) {

			resp.put("Result", "Error");
			return Response.status(404).entity(resp.toString()).build();
		}
	}
	
	private boolean validateAuthCode(String authCode) {
		boolean valid = false;
		try {
			ForgotPass forgot = ForgotPassLocalServiceUtil.getByVerifyCode(authCode);
			
			Date now = new Date();
			
			if (forgot.getInused() && now.before(forgot.getExpiredDate())) {
				valid = true;
			} 

		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return valid;
	}

	private Log _log = LogFactoryUtil.getLog(OCPSUserController.class);

}
