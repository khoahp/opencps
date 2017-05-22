package com.fds.vrbusiness.api;

import java.util.List;
import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.opencps.datamgt.model.DictItem;
import org.opencps.datamgt.service.DictItemLocalServiceUtil;

import com.fds.vrbusiness.NotAuthException;
import com.fds.vrbusiness.OutOfScopeDataException;
import com.fds.vrbusiness.model.VRConfigTechSpec;
import com.fds.vrbusiness.service.VRConfigTechSpecLocalServiceUtil;
import com.fds.vrbusiness.utils.DictItemsUtils;
import com.fds.vrbusiness.utils.VRConstants;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;

@Path("vr")
public class VRController {
	@GET
	@Path("/techspecs/{vehicletype: .*}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response getTecSpecs(@HeaderParam("apikey") String apikey,
			@HeaderParam("module") String module,
			@HeaderParam("dossierId") long dossierId,
			@HeaderParam("dossierFileId") long dossierFileId,
			@PathParam("vehicletype") String vehicletype) {

		JSONObject resp = JSONFactoryUtil.createJSONObject();

		JSONArray respArray = JSONFactoryUtil.createJSONArray();

		try {

			/*
			 * Auth auth = new Auth(); APIKeys api = auth.auth(apikey);
			 * 
			 * Permit permit = new Permit();
			 * 
			 * boolean isDossierPermit = permit.isPermitDossier(dossierId,
			 * api.getUserId());
			 * 
			 * boolean isDossierFilePermit =
			 * permit.isPermitDossierFile(dossierId, dossierFileId,
			 * api.getUserId());
			 */

			long dictCollectionId = DictItemsUtils
					.getDictCollectionId(VRConstants.VR_TYPE_TECH_SPECH);

			long dictCollectionLinkedId = DictItemsUtils
					.getDictCollectionId(VRConstants.VR_VEHICLECLASS);

			List<DictItem> ditems = DictItemsUtils.getDictItems(vehicletype,
					dictCollectionId, dictCollectionLinkedId);

			Locale locale = new Locale("vi", "VN");

			for (DictItem di : ditems) {

				JSONObject jsonTechSpec = JSONFactoryUtil.createJSONObject();

				List<VRConfigTechSpec> vrConfigTechs = VRConfigTechSpecLocalServiceUtil
						.getByVCSC(vehicletype, di.getItemCode());

				jsonTechSpec.put("key", di.getItemCode());
				jsonTechSpec.put("type", "label");
				jsonTechSpec.put("title", di.getItemName(locale));
				jsonTechSpec.put("required", false);
				jsonTechSpec.put("Reference", false);
				jsonTechSpec.put("placeholder", di.getItemName(locale));
				jsonTechSpec.put("datasource", StringPool.BLANK);

				JSONArray items = JSONFactoryUtil.createJSONArray();

				for (VRConfigTechSpec vrConfig : vrConfigTechs) {
					JSONObject techspec = JSONFactoryUtil.createJSONObject();

					techspec.put("key", vrConfig.getSpecificationCode());
					techspec.put("type", Validator.isNull(vrConfig
							.getSpecificationDataCollectionId()) ? "select"
							: "text");
					techspec.put("title",
							vrConfig.getSpecificationDisplayName());
					techspec.put("required", true);
					techspec.put("Reference", false);
					techspec.put("value", StringPool.SPACE);

					techspec.put("placeholder",
							vrConfig.getSpecificationDisplayName());
					if (Validator.isNotNull(vrConfig
							.getSpecificationDataCollectionId())) {
						techspec.put("datasource", getDataSource(vrConfig
								.getSpecificationDataCollectionId()));
					}

					items.put(techspec);
				}

				jsonTechSpec.put("items", items);

				respArray.put(jsonTechSpec);

			}

			return Response.status(200).entity(respArray.toString()).build();

		} catch (Exception e) {

			resp.put("ErrorMessage", e.toString());

			if (e instanceof NotAuthException) {
				return Response.status(401).entity(resp.toString()).build();
			} else if (e instanceof OutOfScopeDataException) {
				return Response.status(401).entity(resp.toString()).build();
			} else {
				return Response.status(404).entity(resp.toString()).build();
			}
		}

	}

	@GET
	@Path("/status")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response checkService() {
		
		JSONObject resp = JSONFactoryUtil.createJSONObject();
		
		resp.put("Status", "Online");
		
		return Response.status(200).entity(resp.toString()).build();
	}
	
	private JSONArray getDataSource(String dataSourceCode) {
		JSONArray datasource = JSONFactoryUtil.createJSONArray();

		long dictCollectionId = DictItemsUtils
				.getDictCollectionId(dataSourceCode);

		try {
			if (dictCollectionId != 0) {
				List<DictItem> lsDictItems = DictItemLocalServiceUtil
						.getDictItemsByDictCollectionId(dictCollectionId);

				Locale locale = new Locale("vi", "VN");

				for (DictItem di : lsDictItems) {
					JSONObject diObject = JSONFactoryUtil.createJSONObject();

					diObject.put("value", di.getItemCode());
					diObject.put("text", di.getItemName(locale));

					datasource.put(diObject);
				}
			}
		} catch (Exception e) {
			_log.error(e);
		}

		return datasource;
	}
	
	private Log _log = LogFactoryUtil.getLog(VRController.class);
}
