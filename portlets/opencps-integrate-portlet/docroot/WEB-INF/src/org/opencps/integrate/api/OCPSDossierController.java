package org.opencps.integrate.api;

import java.util.Calendar;
import java.util.List;

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
import org.opencps.dossiermgt.model.Dossier;
import org.opencps.dossiermgt.model.DossierPart;
import org.opencps.dossiermgt.model.DossierTemplate;
import org.opencps.dossiermgt.model.ServiceConfig;
import org.opencps.dossiermgt.service.DossierLocalServiceUtil;
import org.opencps.dossiermgt.service.DossierPartLocalServiceUtil;
import org.opencps.dossiermgt.service.DossierTemplateLocalServiceUtil;
import org.opencps.dossiermgt.service.ServiceConfigLocalServiceUtil;
import org.opencps.integrate.dao.model.IntegrateAPI;
import org.opencps.integrate.utils.APIUtils;
import org.opencps.integrate.utils.AccountModel;
import org.opencps.integrate.utils.DLFolderUtil;
import org.opencps.integrate.utils.DossierModel;
import org.opencps.integrate.utils.DossierUtils;
import org.opencps.servicemgt.model.ServiceInfo;
import org.opencps.servicemgt.service.ServiceInfoLocalServiceUtil;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portlet.documentlibrary.model.DLFolder;

@Path("/api")
public class OCPSDossierController {
	
	@POST
	@Path("/dossiers/{dossierid}/actions")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response submitDossier(@HeaderParam("apikey") String apikey,
			@Context HttpServletRequest request,
			@PathParam("dossierid") long dossierid, String body) {

		JSONObject resp = JSONFactoryUtil.createJSONObject();

		OCPSPermission permit = new OCPSPermission();

		OCPSAuth auth = new OCPSAuth();

		IntegrateAPI api = auth.auth(apikey);
		

		boolean isPermit = permit.isDossierPermission(apikey)
				&& permit.isSendValidatorDossier(dossierid)
				&& permit.isDossierDetailPermission(apikey, dossierid);

		if (Validator.isNotNull(api)) {
			if (isPermit) {
				
				try {
					
					User user = UserLocalServiceUtil.getUser(api.getUserId());
					
					JSONObject jsonMgs = JSONFactoryUtil.createJSONObject();
					
					Dossier dossier = DossierLocalServiceUtil.getDossier(dossierid);

					jsonMgs.put("action", DossierModel.ACTION_SUBMIT_VALUE);
					jsonMgs.put("dossierId", dossier.getDossierId());
					jsonMgs.put("fileGroupId", 0);
					jsonMgs.put("userId", user.getUserId());
					jsonMgs.put("companyId", user.getCompanyId());
					jsonMgs.put("groupId", APIUtils.GROUPID);
					jsonMgs.put("dossierOId", dossier.getOid());
					jsonMgs.put("govAgencyCode", dossier.getGovAgencyCode());
					jsonMgs.put("dossierStatus", DossierModel.DOSSIER_STATUS_NEW);

					Message message = new Message();
					
					message.put("jsonMsg", jsonMgs.toString());
					message.put("msgFrom", "outside");
					
					MessageBusUtil.sendMessage(
							"opencps/frontoffice/out/destination", message);

					return Response.status(200).entity(resp.toString()).build();
				} catch (Exception e) {
					return Response.status(404).entity(resp.toString()).build();
				}
			} else {
				// Not access resources
				return Response.status(403).entity(resp.toString()).build();
			}
		} else {
			// Not validate
			return Response.status(401).entity(resp.toString()).build();
		}
	}

	@GET
	@Path("/dossierparts/servicecode/{serviceid: .*}/govagencycode/{govagencycode: .*}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response getDossierParts(@HeaderParam("apikey") String apikey,
			@Context HttpServletRequest request,
			@PathParam("serviceid") long serviceid,
			@PathParam("govagencycode") String govagencycode) {

		JSONObject resp = JSONFactoryUtil.createJSONObject();

		OCPSPermission permit = new OCPSPermission();

		OCPSAuth auth = new OCPSAuth();

		IntegrateAPI api = auth.auth(apikey);

		// boolean isPermit = permit.isDossierActionPermission(apikey,
		// dossierId);
		boolean isPermit = permit.isDossierPermission(apikey);

		if (Validator.isNotNull(api)) {
			if (isPermit) {

				ServiceConfig sc = getServiceConfig(serviceid, govagencycode);

				if (Validator.isNotNull(sc)) {

					JSONArray dossierTamplates = getDossierTemplates(serviceid,
							govagencycode);

					resp.put("Total", dossierTamplates.length());
					resp.put("Dossiertemplates", dossierTamplates);

					return Response.status(200).entity(resp.toString()).build();

				} else {
					return Response.status(404).entity(resp.toString()).build();

				}

			} else {
				// Not access resources
				return Response.status(403).entity(resp.toString()).build();
			}
		} else {
			// Not validate
			return Response.status(401).entity(resp.toString()).build();
		}
	}

	/**
	 * @param serviceId
	 * @param govAgencyCode
	 * @return
	 */
	private ServiceConfig getServiceConfig(long serviceId, String govAgencyCode) {

		ServiceConfig sc = null;

		try {
			sc = ServiceConfigLocalServiceUtil.getServiceConfigByG_S_G(
					APIUtils.GROUPID, serviceId, govAgencyCode);

		} catch (Exception e) {
			_log.debug(e);
		}

		return sc;
	}

	/**
	 * @param serviceId
	 * @param govAgencyCode
	 * @return
	 */
	private JSONArray getDossierTemplates(long serviceId, String govAgencyCode) {

		ServiceConfig sc = getServiceConfig(serviceId, govAgencyCode);

		JSONArray jsDossierTmps = JSONFactoryUtil.createJSONArray();

		JSONObject jsDossierTmpObj = JSONFactoryUtil.createJSONObject();

		try {

			DossierTemplate dt = DossierTemplateLocalServiceUtil
					.getDossierTemplate(sc.getDossierTemplateId());

			jsDossierTmpObj.put("templateName", dt.getTemplateName());
			jsDossierTmpObj.put("templateCode", dt.getTemplateNo());
			jsDossierTmpObj.put("templateDesc", dt.getDescription());
			jsDossierTmpObj.put("dossierParts",
					getDossierParts(dt.getDossierTemplateId()));

			jsDossierTmps.put(jsDossierTmpObj);

		} catch (Exception e) {
			_log.debug(e);
		}

		return jsDossierTmps;
	}

	/**
	 * @param dossierTempalateId
	 * @return
	 */
	private JSONArray getDossierParts(long dossierTempalateId) {

		JSONArray jsDossierParts = JSONFactoryUtil.createJSONArray();

		try {

			List<DossierPart> dps = DossierPartLocalServiceUtil
					.getDossierParts(dossierTempalateId);

			for (DossierPart dp : dps) {
				JSONObject jsDossiermpObj = JSONFactoryUtil.createJSONObject();

				jsDossiermpObj.put("partNo", dp.getPartNo());
				jsDossiermpObj.put("partName", dp.getPartName());
				jsDossiermpObj.put("partTip", dp.getPartTip());
				jsDossiermpObj.put("sibling", dp.getSibling());
				jsDossiermpObj.put("treeIndex", dp.getTreeIndex());
				jsDossiermpObj.put("formScript", dp.getFormScript());
				jsDossiermpObj.put("formReport", dp.getFormReport());
				jsDossiermpObj.put("sampleData", dp.getSampleData());
				jsDossiermpObj.put("required", dp.getRequired());
				jsDossiermpObj.put("templateFileNo", dp.getTemplateFileNo());

				jsDossierParts.put(jsDossiermpObj);

			}

		} catch (Exception e) {
			_log.debug(e);
		}

		return jsDossierParts;
	}
	
	

	@POST
	@Path("/dossiers")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response addDossiers(@HeaderParam("apikey") String apikey,
			@Context HttpServletRequest request, String body) {

		JSONObject resp = JSONFactoryUtil.createJSONObject();

		JSONObject input = JSONFactoryUtil.createJSONObject();

		try {
			input = JSONFactoryUtil.createJSONObject(body);
		} catch (Exception e) {
			return Response.status(404).entity(resp.toString()).build();
		}

		OCPSPermission permit = new OCPSPermission();

		OCPSAuth auth = new OCPSAuth();

		IntegrateAPI api = auth.auth(apikey);

		boolean isPermit = permit.isAddDossierPermission(apikey);

		if (Validator.isNotNull(api)) {
			if (isPermit) {

				ServiceContext context = DossierUtils
						.getServletContext(request);

				context.setScopeGroupId(APIUtils.GROUPID);

				DossierModel dm = DossierUtils.getDossierModel(input);

				Calendar cal = Calendar.getInstance();
				int birthDateDay = cal.get(Calendar.DAY_OF_MONTH);
				int birthDateMonth = cal.get(Calendar.MONTH);
				int birthDateYear = cal.get(Calendar.YEAR);

				// If actor is Mobile
				if (auth.isUser(apikey)) {
					context.setUserId(api.getUserId());
				}

				// If actor is Agency (WebApp)
				if (auth.isAgency(apikey)) {

					// Create onlineUser
					if (Validator.isNotNull(dm.getContactEmail())) {

						User userOfDossierOnline = APIUtils.getUserByEmail(
								context.getCompanyId(), dm.getContactEmail());

						long userId = 0;

						if (userOfDossierOnline == null) {

							// create user citizen

							try {
								Citizen citizen = CitizenLocalServiceUtil
										.addCitizen(dm.getApplicantName(),
												StringPool.BLANK, 0,
												birthDateDay, birthDateMonth,
												birthDateYear, dm.getAddress(),
												dm.getCityCode(), dm
														.getDistrictCode(), dm
														.getWardCode(),
												APIUtils.getDictItemName(dm
														.getCityCode()),
												APIUtils.getDictItemName(dm
														.getDistrictCode()),
												APIUtils.getDictItemName(dm
														.getWardCode()), dm
														.getContactEmail(),
												StringPool.BLANK,
												APIUtils.GROUPID,
												StringPool.BLANK,
												StringPool.BLANK,
												StringPool.BLANK, null, 0,
												context);

								if (citizen != null) {
									User mappingUser = UserLocalServiceUtil
											.getUser(citizen.getMappingUserId());

									org.opencps.integrate.utils.MessageBusUtil
											.sendEmailAddressVerification(
													citizen.getUuid(),
													mappingUser,
													dm.getContactEmail(),
													AccountModel.ACCOUNT_TYPE_CITIZEN,
													AccountModel.ACCOUNT_REG_TWO_STEP,
													"khoavd.it@gmail.com",
													context);

									CitizenLocalServiceUtil
											.updateStatus(
													citizen.getCitizenId(),
													context.getUserId(),
													AccountModel.ACCOUNT_STATUS_APPROVED);
								}

							} catch (Exception e) {
								_log.error(e);
							}

						} else {
							userId = userOfDossierOnline.getUserId();
						}
						context.setUserId(userId);
					}

				}

				try {
					// Create Dossier

					ServiceInfo serviceInfo = ServiceInfoLocalServiceUtil
							.getServiceInfoByServiceNo(dm.getServiceCode());

					ServiceConfig serviceConfig = ServiceConfigLocalServiceUtil
							.getServiceConfigByG_S_G(context.getScopeGroupId(),
									serviceInfo.getServiceinfoId(),
									dm.getAgencyCode());

					long ownerOrganizationId = 0;
					long dossierTemplateId = serviceConfig
							.getDossierTemplateId();
					String templateFileNo = StringPool.BLANK;
					long serviceConfigId = serviceConfig.getServiceConfigId();
					long serviceInfoId = serviceInfo.getServiceinfoId();
					String serviceDomainIndex = serviceConfig
							.getServiceDomainIndex();
					long govAgencyOrganizationId = serviceConfig
							.getGovAgencyOrganizationId();

					int serviceMode = 1;

					String serviceAdministrationIndex = serviceConfig
							.getServiceAdministrationIndex();
					String dossierDestinationFolder = StringPool.BLANK;

					dossierDestinationFolder = DossierUtils
							.getDossierDestinationFolder(
									context.getScopeGroupId(), birthDateYear,
									birthDateMonth, birthDateDay);

					DLFolder dossierFolder = DLFolderUtil.getTargetFolder(
							context.getUserId(), context.getScopeGroupId(),
							context.getScopeGroupId(), false, 0,
							dossierDestinationFolder, StringPool.BLANK, false,
							context);

					Dossier dossier = DossierUtils.getDossierIdByOid(dm.getReferenceUid());

					if (Validator.isNull(dossier)) {
						
						dossier = DossierLocalServiceUtil.addDossier(
								context.getUserId(), ownerOrganizationId,
								dossierTemplateId, templateFileNo,
								serviceConfigId, serviceInfoId,
								serviceDomainIndex, govAgencyOrganizationId,
								dm.getAgencyCode(), dm.getAgencyName(),
								serviceMode, serviceAdministrationIndex,
								dm.getCityCode(), dm.getCityName(),
								dm.getDistrictCode(), dm.getDistrictName(),
								dm.getWardName(), dm.getWardCode(),
								dm.getApplicantName(), dm.getApplicantIdNo(),
								dm.getAddress(), dm.getApplicantName(),
								dm.getContactTelNo(), dm.getContactEmail(),
								dm.getDossierNote(),
								DossierModel.DOSSIER_SOURCE_DIRECT,
								DossierModel.DOSSIER_STATUS_NEW,
								dossierFolder.getFolderId(), StringPool.BLANK,
								context);
						
						dossier.setOid(dm.getReferenceUid());
						
						DossierLocalServiceUtil.updateDossier(dossier);
						
						resp.put("Result", "New");
						resp.put("DossierId", dossier.getDossierId());
						resp.put("ReferenceId", dossier.getOid());
						resp.put("AgencyCode", dossier.getGovAgencyCode());

						return Response.status(200).entity(resp.toString())
								.build();

					} else {
						resp.put("Result", "Exist");
						resp.put("DossierId", dossier.getDossierId());
						resp.put("ReferenceId", dossier.getOid());
						resp.put("AgencyCode", dossier.getGovAgencyCode());
						resp.put("ErrorMessage",
								APIUtils.getLanguageValue("dossier-exist"));

						return Response.status(408).entity(resp.toString())
								.build();

					}

				} catch (Exception e) {
					resp.put("Result", "Error");

					resp.put("ErrorMessage",
							APIUtils.getLanguageValue("create-dossier-error"));
					
					_log.error(e);
					
					return Response.status(404).entity(resp.toString()).build();
				}

			} else {
				// Not access resources
				return Response.status(403).entity(resp.toString()).build();
			}
		} else {
			// Not validate
			return Response.status(401).entity(resp.toString()).build();
		}
	}

	private Log _log = LogFactoryUtil.getLog(OCPSDossierController.class);
}
