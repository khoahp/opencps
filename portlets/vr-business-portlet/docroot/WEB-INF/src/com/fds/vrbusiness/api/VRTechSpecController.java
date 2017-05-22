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
import com.fds.vrbusiness.model.VRLimitConfigTechSpec;
import com.fds.vrbusiness.service.VRConfigTechSpecLocalServiceUtil;
import com.fds.vrbusiness.service.VRLimitConfigTechSpecLocalServiceUtil;
import com.fds.vrbusiness.utils.DictItemsUtils;
import com.fds.vrbusiness.utils.VRConstants;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;

@Path("/vr-app")
public class VRTechSpecController {

	@GET
	@Path("/techspecs/vehicletype/{vehicletype: .*}")
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
				jsonTechSpec.put("value", StringPool.BLANK);

				JSONArray items = JSONFactoryUtil.createJSONArray();

				for (VRConfigTechSpec vrConfig : vrConfigTechs) {
					JSONObject techspec = JSONFactoryUtil.createJSONObject();

					techspec.put("key", vrConfig.getSpecificationCode());

					/*
					 * techspec.put("type", Validator.isNull(vrConfig
					 * .getSpecificationDataCollectionId()) ? "text" :
					 * "select");
					 */

					techspec.put("type", vrConfig.getSpecificationEntryType());

					techspec.put("title",
							vrConfig.getSpecificationDisplayName());

					techspec.put("required",
							vrConfig.getSpecificationMandatory());

					techspec.put("Reference", false);

					techspec.put("value", StringPool.BLANK);

					techspec.put("standard",
							vrConfig.getSpecificationStandard());

					techspec.put("basicunit",
							vrConfig.getSpecificationBasicUnit());

					techspec.put("placeholder",
							vrConfig.getSpecificationEntryPlaceholder());
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
	@Path("/techspecslimit/vehicleclass/{vehicleclass: .*}/vehicletype/{vehicletype: .*}/formulatype/{formulatype: .*}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response getTecSpecsLimit(@HeaderParam("apikey") String apikey,
			@HeaderParam("module") String module,
			@HeaderParam("dossierId") long dossierId,
			@HeaderParam("dossierFileId") long dossierFileId,
			@PathParam("vehicleclass") String vehicleclass,
			@PathParam("vehicletype") String vehicletype,
			@PathParam("formulatype") long formulatype) {

		JSONObject resp = JSONFactoryUtil.createJSONObject();

		try {

			String markupCode = DictItemsUtils.getDictItemLinkCode(vehicletype,
					vehicleclass);

			List<VRLimitConfigTechSpec> vrLimits = VRLimitConfigTechSpecLocalServiceUtil
					.getLimitConfigs(vehicleclass, markupCode, formulatype);

			JSONArray tectSpecOut = getLimitsConfig(vrLimits);

			resp.put("TechSpecs", tectSpecOut);

			return Response.status(200).entity(resp.toString()).build();

		} catch (Exception e) {

			resp.put("Exception", e.toString());

			return Response.status(404).entity(resp.toString()).build();
		}
	}

	@GET
	@Path("/techspecsreport")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response getTecSpecsReport(@HeaderParam("apikey") String apikey,
			@HeaderParam("module") String module,
			@HeaderParam("dossierId") long dossierId,
			@HeaderParam("templateFileNo") String templateFileNo) {

		JSONObject resp = JSONFactoryUtil.createJSONObject();

		try {

			resp.put("module", module);
			resp.put("dossierId", dossierId);
			resp.put("templateFileNo", templateFileNo);

			return Response.status(200).entity(resp.toString()).build();

		} catch (Exception e) {
			return Response.status(404).entity(resp.toString()).build();
		}
	}

	private JSONArray getLimitsConfig(List<VRLimitConfigTechSpec> vrLimitConfigs) {
		JSONArray arrOut = JSONFactoryUtil.createJSONArray();

		_log.info("LIMIT_SPEC:::");

		for (VRLimitConfigTechSpec vrLimit : vrLimitConfigs) {

			_log.info("LIMIT_SPEC:::" + vrLimit.getSpecificationCode());

			arrOut.put(vrLimit.getSpecificationCode());
		}

		return arrOut;
	}

	/**
	 * @param dataSourceCode
	 * @return
	 */
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

	private Log _log = LogFactoryUtil.getLog(VRTechSpecController.class);
}
