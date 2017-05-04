package org.opencps.integrate.api;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.opencps.integrate.dao.model.IntegrateAPI;
import org.opencps.integrate.utils.APIUtils;
import org.opencps.integrate.utils.NotificationUtils;

import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.User;
import com.liferay.portal.model.UserNotificationEvent;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.service.UserNotificationEventLocalServiceUtil;

@Path("/api")
public class OCPSNotificationController {

	@POST
	@Path("/getNotifications")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response getNotifications(@HeaderParam("apiKey") String apikey,
			@Context HttpServletRequest request, String body) {

		OCPSAuth auth = new OCPSAuth();

		IntegrateAPI api = auth.auth(apikey);

		JSONObject req = JSONFactoryUtil.createJSONObject();

		if (Validator.isNotNull(api)) {

			try {

				req = JSONFactoryUtil.createJSONObject(body);

				String email = req.getString("email", StringPool.BLANK);
				int start = req.getInt("start", QueryUtil.ALL_POS);
				int end = req.getInt("end", QueryUtil.ALL_POS);

				ServiceContext context = ServiceContextFactory
						.getInstance(request);

				User user = null;

				try {
					user = UserLocalServiceUtil.getUserByEmailAddress(
							context.getCompanyId(), email);
				} catch (NoSuchUserException e) {

				}

				if (Validator.isNull(user)) {

					req.put("Result", "NoExist ");

					return Response.status(401).entity(req.toString()).build();

				} else {

					JSONObject res = JSONFactoryUtil.createJSONObject();

					List<UserNotificationEvent> userNotificationEvents = new ArrayList<UserNotificationEvent>();

					int totalSize = 0;

					userNotificationEvents = UserNotificationEventLocalServiceUtil
							.getArchivedUserNotificationEvents(
									user.getUserId(), false, start, end);

					totalSize = UserNotificationEventLocalServiceUtil
							.getArchivedUserNotificationEventsCount(
									user.getUserId(), false);

					JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

					if (userNotificationEvents.size() > 0) {

						for (UserNotificationEvent userNotificationEvent : userNotificationEvents) {

							JSONObject jsonObject = JSONFactoryUtil
									.createJSONObject(userNotificationEvent
											.getPayload());

							JSONObject userNoticeEvent = JSONFactoryUtil
									.createJSONObject();
							userNoticeEvent.put("title",
									jsonObject.getString("title"));
							userNoticeEvent.put("content",
									jsonObject.getString("notificationText"));
							userNoticeEvent.put("userNotificationEventId",
									userNotificationEvent
											.getUserNotificationEventId());
							userNoticeEvent.put("timeStamp",
									userNotificationEvent.getTimestamp());

							jsonArray.put(userNoticeEvent);
						}
					}

					res.put("total", totalSize);
					res.put("userNotificationEvents", jsonArray);

					return Response.status(200).entity(res.toString()).build();
				}
			} catch (Exception e) {

				_log.error(e);

				req.put("Result", "Error");
				return Response.status(404).entity(req.toString()).build();
			}
		}

		req.put("Result", "Error");
		req.put("ErrorMessage", APIUtils.getLanguageValue("you-dont-have-auth"));

		// Not validate
		return Response.status(401).entity(req.toString()).build();
	}

	@POST
	@Path("/markNotificationAsReaded")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response markNotificationAsReaded(
			@HeaderParam("apiKey") String apiKey,
			@Context HttpServletRequest request, String body) {

		OCPSAuth auth = new OCPSAuth();

		IntegrateAPI api = auth.auth(apiKey);

		JSONObject req = JSONFactoryUtil.createJSONObject();

		boolean arrchived = false;

		if (Validator.isNotNull(api)) {

			try {

				JSONArray jsonArray = JSONFactoryUtil.createJSONArray(body);

				JSONArray resp = JSONFactoryUtil.createJSONArray();
				JSONObject respOb = JSONFactoryUtil.createJSONObject();

				if (jsonArray.length() > 0) {

					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);

						try {

							arrchived = NotificationUtils
									.updateArchived(jsonObject.getLong("id"));
						} catch (Exception e) {

						}
						respOb.put("id", jsonObject.getString("id"));
						respOb.put("archived", arrchived);

						resp.put(respOb);
					}

				}

				return Response.status(200).entity(resp.toString()).build();

			} catch (Exception e) {

				_log.error(e);

				req.put("Result", "Error");
				return Response.status(404).entity(req.toString()).build();
			}
		}

		req.put("Result", "Error");
		req.put("ErrorMessage", APIUtils.getLanguageValue("you-dont-have-auth"));

		// Not validate
		return Response.status(401).entity(req.toString()).build();
	}

	private Log _log = LogFactoryUtil.getLog(OCPSNotificationController.class);
}
