package org.opencps.integrate.api;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.opencps.accountmgt.model.Citizen;
import org.opencps.accountmgt.service.CitizenLocalServiceUtil;
import org.opencps.integrate.dao.model.IntegrateAPI;
import org.opencps.integrate.dao.service.IntegrateAPILocalServiceUtil;
import org.opencps.integrate.utils.APIUtils;
import org.opencps.integrate.utils.AccountModel;
import org.opencps.integrate.utils.DossierUtils;
import org.opencps.integrate.utils.MessageBusUtil;
import org.opencps.integrate.utils.UserUtils;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
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
			
			_log.info(body);

			AccountModel acc = getAccountModelInput(input);
			
			String regEmail = acc.getContactEmail();

			if (acc.getApplicantIdType().equalsIgnoreCase(
					UserUtils.APPLICANT_TYPE_CITY)) {

				Calendar cal = Calendar.getInstance();
				int birthDateDay = cal.get(Calendar.DAY_OF_MONTH);
				int birthDateMonth = cal.get(Calendar.MONTH);
				int birthDateYear = cal.get(Calendar.YEAR);

				Citizen citizen = CitizenLocalServiceUtil.addCitizen(
						acc.getApplicantName(), StringPool.BLANK, 0,
						birthDateDay, birthDateMonth, birthDateYear,
						acc.getAddress(), acc.getCityCode(),
						acc.getDistrictCode(), acc.getWardCode(),
						APIUtils.getDictItemName(acc.getCityCode()),
						APIUtils.getDictItemName(acc.getDistrictCode()),
						APIUtils.getDictItemName(acc.getWardCode()),
						acc.getContactEmail(), StringPool.BLANK, GROUPID,
						StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
						null, 0, context);

				if (citizen != null) {
					User mappingUser = UserLocalServiceUtil.getUser(citizen
							.getMappingUserId());
					
					MessageBusUtil.sendEmailAddressVerification(
							citizen.getUuid(), mappingUser,
							acc.getContactEmail(), "CITIZEN",
							"2", "khoavd@gmail.com", context);

					CitizenLocalServiceUtil.updateStatus(
							citizen.getCitizenId(), context.getUserId(),
							AccountModel.ACCOUNT_STATUS_APPROVED);
				}
				
				resp.put("Result", "New");
				resp.put("UserId", citizen.getMappingUserId());
				resp.put("ErrorMessage", APIUtils.getLanguageValue("create-new"));
			}

			if (acc.getApplicantIdType().equals(
					UserUtils.APPLICANT_TYPE_BUSINESS)) {
				
			}
			
			

			return Response.status(200).entity(resp.toString()).build();

		} catch (Exception e) {

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

				resp.put("Result", "NoUserExistWithThisEmail "+email);

				return Response.status(401).entity(resp.toString()).build();
			} else {

				UserUtils userUtil = new UserUtils();

				AccountModel acc = userUtil.getAccountModel(user.getUserId());

				if (Validator.isNotNull(acc)) {

					String fromName = PrefsPropsUtil.getString(
							user.getCompanyId(),
							PropsKeys.ADMIN_EMAIL_FROM_NAME);

					String fromAddress = PrefsPropsUtil.getString(
							user.getCompanyId(),
							PropsKeys.ADMIN_EMAIL_FROM_ADDRESS);

					UserLocalServiceUtil.sendPassword(context.getCompanyId(),
							user.getEmailAddress(), fromName, fromAddress,
							StringPool.BLANK, StringPool.BLANK, context);

					resp.put("Result", "Success");

					return Response.status(200).entity(resp.toString()).build();
				} else {
					resp.put("Result", "NoUserExistIsBussinessOrCitizenWithEmail "+email);

					return Response.status(401).entity(resp.toString()).build();
				}

			}
		} catch (PortalException | SystemException e) {

			resp.put("Result", "SystemError");
			return Response.status(404).entity(resp.toString()).build();
		}
	}

	private Log _log = LogFactoryUtil.getLog(OCPSUserController.class);

}
