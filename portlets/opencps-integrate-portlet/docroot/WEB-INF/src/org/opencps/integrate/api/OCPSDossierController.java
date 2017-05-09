package org.opencps.integrate.api;

import java.util.Calendar;
import java.util.List;

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

import org.opencps.accountmgt.model.Citizen;
import org.opencps.accountmgt.service.CitizenLocalServiceUtil;
import org.opencps.dossiermgt.model.Dossier;
import org.opencps.dossiermgt.model.DossierFile;
import org.opencps.dossiermgt.model.DossierPart;
import org.opencps.dossiermgt.model.DossierTemplate;
import org.opencps.dossiermgt.model.ServiceConfig;
import org.opencps.dossiermgt.service.DossierFileLocalServiceUtil;
import org.opencps.dossiermgt.service.DossierLocalServiceUtil;
import org.opencps.dossiermgt.service.DossierPartLocalServiceUtil;
import org.opencps.dossiermgt.service.DossierTemplateLocalServiceUtil;
import org.opencps.dossiermgt.service.ServiceConfigLocalServiceUtil;
import org.opencps.integrate.dao.model.IntegrateAPI;
import org.opencps.integrate.utils.APIUtils;
import org.opencps.integrate.utils.AccountModel;
import org.opencps.integrate.utils.ActionModel;
import org.opencps.integrate.utils.DLFolderUtil;
import org.opencps.integrate.utils.DossierFilesModel;
import org.opencps.integrate.utils.DossierModel;
import org.opencps.integrate.utils.DossierUtils;
import org.opencps.integrate.utils.PortletUtil;
import org.opencps.processmgt.model.ProcessOrder;
import org.opencps.processmgt.model.ProcessWorkflow;
import org.opencps.processmgt.service.ProcessOrderLocalServiceUtil;
import org.opencps.processmgt.service.ProcessWorkflowLocalServiceUtil;
import org.opencps.servicemgt.model.ServiceInfo;
import org.opencps.servicemgt.service.ServiceInfoLocalServiceUtil;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portlet.documentlibrary.model.DLFolder;

@Path("/api")
public class OCPSDossierController {
	
	
	
	@PUT
	@Path("/dossiers/{dossierid: .*}/dossierfiles/{dossierfileuid: .*}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response updateDossierFile(@HeaderParam("apikey") String apikey,
			@Context HttpServletRequest request,
			@PathParam("dossierid") long dossierid,
			@PathParam("dossierfileuid") String dossierfileuid, String body) {

		JSONObject resp = JSONFactoryUtil.createJSONObject();

		OCPSPermission permit = new OCPSPermission();

		OCPSAuth auth = new OCPSAuth();

		IntegrateAPI api = auth.auth(apikey);

		boolean isPermit = permit.isDossierPermission(apikey)
				&& permit.isDossierDetailPermission(apikey, dossierid);

		if (Validator.isNotNull(api)) {
			if (isPermit) {
				try {

					DossierFilesModel dfm = DossierUtils.getDossierFiles(body);

					DossierFile dossierFile = DossierUtils
							.getDossierFileByOid(dossierfileuid);

					Dossier dossier = DossierLocalServiceUtil
							.getDossier(dossierid);

					DossierPart dossierPart = DossierPartLocalServiceUtil
							.getDossierPart(dossierFile.getDossierPartId());

					ServiceContext serviceContext = DossierUtils
							.getServletContext(request);

					if (Validator.isNotNull(dossierFile)) {

						long dossierFileId = dossierFile.getDossierFileId();

						// remove file-older

						if (dossierFileId > 0 && dossierPart.getPartType() != 2) {

							if (dossierFile.getSyncStatus() != 2) {
								DossierFileLocalServiceUtil.deleteDossierFile(
										dossierFileId,
										dossierFile.getFileEntryId());
							} else {
								DossierFileLocalServiceUtil
										.removeDossierFile(dossierFileId);
							}

						} else {

							DossierFileLocalServiceUtil
									.deleteDossierFile(dossierFileId,
											dossierFile.getFileEntryId());
						}

						// add new dossierFile

						String sourceFileName = dfm.getAttachmentFileName();

						String extension = FileUtil
								.getExtension(sourceFileName);

						String mimeType = MimeTypesUtil
								.getExtensionContentType(extension);

						serviceContext.setUserId(dossier.getUserId());

						byte[] bytes = Base64.decode(dfm
								.getAttachmentFileData());

						DLFolder dossierFileFolder = PortletUtil
								.getDossierFolder(
										serviceContext.getScopeGroupId(), null,
										dossier.getOid(), serviceContext);

						if (!(dfm.getDossierFileContent().contentEquals("") || dfm
								.getDossierFileContent().contentEquals("{}"))) {
							mimeType = dfm.getDossierFileContent();
						}

						dossierFile = DossierFileLocalServiceUtil
								.addDossierFile(
										dossier.getUserId(),
										dossier.getDossierId(),
										dossierPart.getDossierpartId(),
										dossierPart.getTemplateFileNo(),
										StringPool.BLANK,
										0L,
										0L,
										dossier.getUserId(),
										dossier.getOwnerOrganizationId(),
										dfm.getAttachmentFileName(),
										mimeType,
										PortletUtil.DOSSIER_FILE_MARK_UNKNOW,
										2,
										StringPool.BLANK,
										dfm.getCreateDate(),
										1,
										PortletUtil.DOSSIER_FILE_SYNC_STATUS_SYNCSUCCESS,
										dossierFileFolder.getFolderId(),
										sourceFileName, mimeType,
										dfm.getAttachmentFileName(),
										StringPool.BLANK, StringPool.BLANK,
										bytes, serviceContext);

						dossierFile.setOid(dfm.getDossierFileUid());

						DossierFileLocalServiceUtil
								.updateDossierFile(dossierFile);

						resp.put("Result", "Update");
						resp.put("DossierId", dossier.getDossierId());
						resp.put("DossierFileUid", dossierFile.getOid());

						return Response.status(200).entity(resp.toString())
								.build();
					} else {
						resp.put("Result", "Error");
						resp.put("ErrorMessage", APIUtils
								.getLanguageValue("no-dossier-file-with-oid"));

						return Response.status(409).entity(resp.toString())
								.build();
					}

				} catch (Exception e) {
					resp.put("Result", "Error");
					resp.put("ErrorMessage",
							APIUtils.getLanguageValue("invalid-body-input"));

					return Response.status(404).entity(resp.toString()).build();
				}
			} else {
				resp.put("Result", "Error");
				resp.put(
						"ErrorMessage",
						APIUtils.getLanguageValue("you-dont-have-permit-to-accecss-resources"));

				// Not access resources
				return Response.status(403).entity(resp.toString()).build();
			}
		} else {
			resp.put("Result", "Error");
			resp.put("ErrorMessage",
					APIUtils.getLanguageValue("you-dont-have-auth"));

			// Not validate
			return Response.status(401).entity(resp.toString()).build();
		}
	}

	@POST
	@Path("/dossiers/{dossierid: .*}/actions")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response submitDossier(@HeaderParam("apikey") String apikey,
			@Context HttpServletRequest request,
			@PathParam("dossierid") long dossierid, String body) {

		JSONObject resp = JSONFactoryUtil.createJSONObject();

		OCPSPermission permit = new OCPSPermission();

		OCPSAuth auth = new OCPSAuth();

		IntegrateAPI api = auth.auth(apikey);

		boolean isPermit = permit.isDossierPermission(apikey)

		&& permit.isDossierDetailPermission(apikey, dossierid);

		if (Validator.isNotNull(api)) {
			if (isPermit) {

				try {
					ActionModel am = DossierUtils.getActionModel(body);

					User user = UserLocalServiceUtil.getUser(api.getUserId());

					JSONObject jsonMgs = JSONFactoryUtil.createJSONObject();

					Dossier dossier = DossierLocalServiceUtil
							.getDossier(dossierid);

					if (am.getActionCode().contentEquals("1000")) {

						if (permit.isSendValidatorDossier(dossierid)) {
							jsonMgs.put("action",
									DossierModel.ACTION_SUBMIT_VALUE);
							jsonMgs.put("dossierId", dossier.getDossierId());
							jsonMgs.put("fileGroupId", 0);
							jsonMgs.put("userId", user.getUserId());
							jsonMgs.put("companyId", user.getCompanyId());
							jsonMgs.put("groupId", APIUtils.GROUPID);
							jsonMgs.put("dossierOId", dossier.getOid());
							jsonMgs.put("govAgencyCode",
									dossier.getGovAgencyCode());
							jsonMgs.put("dossierStatus",
									DossierModel.DOSSIER_STATUS_NEW);

							Message message = new Message();

							message.put("jsonMsg", jsonMgs.toString());
							message.put("msgFrom", "outside");

							MessageBusUtil.sendMessage(
									"opencps/frontoffice/out/destination",
									message);

						} else {
							// Not access resources
							return Response.status(403).entity(resp.toString())
									.build();
						}

					} else {

						dossier = DossierLocalServiceUtil.getDossier(dossierid);

						ProcessOrder processOrder = ProcessOrderLocalServiceUtil
								.findBy_Dossier(dossierid);

						ProcessWorkflow processWorkflow = ProcessWorkflowLocalServiceUtil
								.findByActionCode(am.getActionCode());

						Message msg = new Message();

						JSONObject msgJSON = JSONFactoryUtil.createJSONObject();

						msgJSON.put("companyId", user.getUserId());
						msgJSON.put("groupId", APIUtils.GROUPID);
						msgJSON.put("actionNote", am.getActionNote());
						msgJSON.put("assignToUserId", 0l);
						msgJSON.put("actionUserId", user.getUserId());
						msgJSON.put("dossierId", dossierid);
						msgJSON.put("fileGroupId", 0l);
						msgJSON.put("paymentValue", GetterUtil.getDouble(0));
						msgJSON.put("processOrderId",
								processOrder.getProcessOrderId());

						msgJSON.put("receptionNo", am.getDossierNo());
						msgJSON.put("signature", 0);
						msgJSON.put("dossierStatus", dossier.getDossierStatus());
						msgJSON.put("actionDatetime", am.getModifiedDate());
						msgJSON.put("receiveDate", am.getReceviceDate());
						msgJSON.put("finishedDate", am.getFinishedDate());
						msgJSON.put("estimateDatetime", am.getFinishedDate());

						if (Validator.isNotNull(processWorkflow.getAutoEvent())) {
							msgJSON.put("event", processWorkflow.getAutoEvent());
						} else {
							msgJSON.put("processWorkflowId",
									processWorkflow.getProcessWorkflowId());
						}

						msg.put("jsonMsg", msgJSON.toString());
						msg.put("msgFrom", "outside");

						MessageBusUtil.sendMessage(
								"opencps/backoffice/engine/destination", msg);
					}

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

	@POST
	@Path("/dossiers/{dossierid: .*}/dossierfiles")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response addDossierFiles(@HeaderParam("apikey") String apikey,
			@Context HttpServletRequest request,
			@PathParam("dossierid") long dossierid, String body) {

		JSONObject resp = JSONFactoryUtil.createJSONObject();

		OCPSPermission permit = new OCPSPermission();

		OCPSAuth auth = new OCPSAuth();

		IntegrateAPI api = auth.auth(apikey);

		boolean isPermit = permit.isDossierPermission(apikey)
				&& permit.isDossierDetailPermission(apikey, dossierid);

		if (Validator.isNotNull(api)) {
			if (isPermit) {
				try {

					DossierFilesModel dfm = DossierUtils.getDossierFiles(body);

					DossierFile dossierFile = DossierUtils
							.getDossierFileByOid(dfm.getDossierFileUid());

					Dossier dossier = DossierLocalServiceUtil
							.getDossier(dossierid);

					if (Validator.isNull(dossierFile)) {
						ServiceContext serviceContext = DossierUtils
								.getServletContext(request);

						String sourceFileName = dfm.getAttachmentFileName();

						String extension = FileUtil
								.getExtension(sourceFileName);

						String mimeType = MimeTypesUtil
								.getExtensionContentType(extension);

						serviceContext.setUserId(dossier.getUserId());

						byte[] bytes = Base64.decode(dfm
								.getAttachmentFileData());

						DLFolder dossierFileFolder = PortletUtil
								.getDossierFolder(
										serviceContext.getScopeGroupId(), null,
										dossier.getOid(), serviceContext);

						DossierPart dossierPart = DossierPartLocalServiceUtil
								.getDossierPartByT_PN(
										dossier.getDossierTemplateId(),
										dfm.getDossierPartNo());
						
						if (!(dfm.getDossierFileContent().contentEquals("")
								|| dfm.getDossierFileContent().contentEquals(
										"{}"))) {
							mimeType = dfm.getDossierFileContent();
						}

						dossierFile = DossierFileLocalServiceUtil
								.addDossierFile(
										dossier.getUserId(),
										dossier.getDossierId(),
										dossierPart.getDossierpartId(),
										dossierPart.getTemplateFileNo(),
										StringPool.BLANK,
										0L,
										0L,
										dossier.getUserId(),
										dossier.getOwnerOrganizationId(),
										dfm.getAttachmentFileName(),
										mimeType,
										PortletUtil.DOSSIER_FILE_MARK_UNKNOW,
										2,
										StringPool.BLANK,
										dfm.getCreateDate(),
										1,
										PortletUtil.DOSSIER_FILE_SYNC_STATUS_SYNCSUCCESS,
										dossierFileFolder.getFolderId(),
										sourceFileName, mimeType,
										dfm.getAttachmentFileName(),
										StringPool.BLANK, StringPool.BLANK,
										bytes, serviceContext);

						dossierFile.setOid(dfm.getDossierFileUid());

						DossierFileLocalServiceUtil
								.updateDossierFile(dossierFile);

						resp.put("Result", "New");
						resp.put("DossierId", dossier.getDossierId());
						resp.put("DossierFileUid", dossierFile.getOid());

						return Response.status(200).entity(resp.toString())
								.build();
					} else {
						resp.put("Result", "Exist");
						resp.put("DossierId", dossier.getDossierId());
						resp.put("DossierFileUid", dossierFile.getOid());
						resp.put("ErrorMessage", APIUtils
								.getLanguageValue("duplicate-dossier-file"));

						return Response.status(408).entity(resp.toString())
								.build();
					}

				} catch (Exception e) {
					resp.put("Result", "Error");
					resp.put("ErrorMessage",
							APIUtils.getLanguageValue("invalid-body-input"));

					return Response.status(404).entity(resp.toString()).build();
				}
			} else {
				resp.put("Result", "Error");
				resp.put(
						"ErrorMessage",
						APIUtils.getLanguageValue("you-dont-have-permit-to-accecss-resources"));

				// Not access resources
				return Response.status(403).entity(resp.toString()).build();
			}
		} else {
			resp.put("Result", "Error");
			resp.put(
					"ErrorMessage",
					APIUtils.getLanguageValue("you-dont-have-auth"));

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

					Dossier dossier = DossierUtils.getDossierIdByOid(dm
							.getReferenceUid());

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
	
	@PUT
	@Path("/dossiers/{dossierid: .*}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response updateDossiers(@HeaderParam("apikey") String apikey,
			@Context HttpServletRequest request, String body,
			@PathParam("dossierid") long dossierid) {

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

				User user = APIUtils.getUserByEmail(context.getCompanyId(),
						dm.getContactEmail());

				// If actor is Mobile
				if (auth.isUser(apikey)) {
					context.setUserId(api.getUserId());
				} else {
					if (Validator.isNotNull(user)) {
						context.setUserId(user.getUserId());
					}
				}

				try {
					// Get Dossier

					Dossier dossier = DossierUtils.getDossierById(dossierid);

					if (Validator.isNotNull(dossier)) {
						
						if (Validator.isNotNull(dm.getApplicantName())) {
							dossier.setContactName(dm.getApplicantName());
						}
						
						if (Validator.isNotNull(dm.getApplicantIdNo())) {
							dossier.setSubjectId(dm.getApplicantIdNo());
						}
						
						if (Validator.isNotNull(dm.getAddress())) {
							dossier.setAddress(dm.getAddress());
						}
						
						if (Validator.isNotNull(dm.getCityName())) {
							dossier.setCityCode(dm.getCityName());
						}
						
						if (Validator.isNotNull(dm.getCityCode())) {
							dossier.setCityCode(dm.getCityCode());
						}

						if (Validator.isNotNull(dm.getDistrictCode())) {
							dossier.setCityCode(dm.getDistrictCode());
						}
						
						if (Validator.isNotNull(dm.getWardCode())) {
							dossier.setCityCode(dm.getWardCode());
						}
						if (Validator.isNotNull(dm.getDistrictName())) {
							dossier.setCityCode(dm.getDistrictName());
						}
						
						if (Validator.isNotNull(dm.getWardName())) {
							dossier.setCityCode(dm.getWardName());
						}
						
						if (Validator.isNotNull(dm.getContactTelNo())) {
							dossier.setContactTelNo(dm.getContactTelNo());
						}
						
						if (Validator.isNotNull(dm.getDossierNote())) {
							dossier.setNote(dm.getDossierNote());
						}
						
						DossierLocalServiceUtil.updateDossier(dossier);
						
						resp.put("Result", "Update");
						resp.put("DossierId", dossier.getDossierId());
						resp.put("ReferenceId", dossier.getOid());
						resp.put("AgencyCode", dossier.getGovAgencyCode());

						return Response.status(200).entity(resp.toString())
								.build();

					} else {
						resp.put("Result", "Error");
						resp.put(
								"ErrorMessage",
								APIUtils.getLanguageValue("no-dossier-exist-with-dossierid"));

						return Response.status(404).entity(resp.toString())
								.build();

					}

				} catch (Exception e) {
					resp.put("Result", "Error");

					resp.put("ErrorMessage",
							APIUtils.getLanguageValue("update-dossier-error"));

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
