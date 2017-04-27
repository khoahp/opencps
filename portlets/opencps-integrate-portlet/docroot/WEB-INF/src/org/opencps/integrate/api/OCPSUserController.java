package org.opencps.integrate.api;

import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.opencps.integrate.dao.model.IntegrateAPI;
import org.opencps.integrate.dao.service.IntegrateAPILocalServiceUtil;
import org.opencps.integrate.utils.AccountModel;
import org.opencps.integrate.utils.UserUtils;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;

@Path("/api")
public class OCPSUserController {

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

	private Log _log = LogFactoryUtil.getLog(OCPSUserController.class);

}
