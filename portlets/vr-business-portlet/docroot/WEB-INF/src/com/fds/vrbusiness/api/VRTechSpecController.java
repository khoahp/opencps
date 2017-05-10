package com.fds.vrbusiness.api;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fds.vrbusiness.NotAuthException;
import com.fds.vrbusiness.OutOfScopeDataException;
import com.fds.vrbusiness.model.APIKeys;
import com.fds.vrbusiness.permission.Auth;
import com.fds.vrbusiness.permission.Permit;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;

@Path("/vr")
public class VRTechSpecController {
	
	@GET
	@Path("/techspecs/vehicletype/{vehicletype: .*}/formulatype/{formulatype: .*}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response getTecSpecs(@HeaderParam("apikey") String apikey,
			@HeaderParam("module") String module,
			@HeaderParam("dossierId") long dossierId,
			@HeaderParam("dossierFileId") long dossierFileId,
			@PathParam("vehicletype") String vehicletype,
			@PathParam("formulatype") String formulatype) {
		
		JSONObject resp = JSONFactoryUtil.createJSONObject();
		
		try {
			Auth auth = new Auth();
			
			APIKeys api = auth.auth(apikey);
			
			Permit permit = new Permit();
			
			boolean isDossierPermit = permit.isPermitDossier(dossierId, api.getUserId());

			boolean isDossierFilePermit = permit.isPermitDossierFile(dossierId, dossierFileId, api.getUserId());
			
			
			return Response.status(200).entity(resp.toString()).build();
			
		} catch (Exception e) {
			if (e instanceof NotAuthException) {
				return Response.status(401).entity(resp.toString()).build();
			} else if (e instanceof OutOfScopeDataException) {
				return Response.status(401).entity(resp.toString()).build();
			} else {
				return Response.status(404).entity(resp.toString()).build();
			}
		}
		
	}
}
