package com.fds.vrbusiness.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;

@Path("/dictitem")
public class DictItemController {
	@GET
	@Path("/alo")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response alo() {

		JSONObject resp = JSONFactoryUtil.createJSONObject();

		resp.put("Message", "Hello");

		return Response.status(200).entity(resp.toString()).build();

	}

}
