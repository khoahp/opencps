package org.opencps.integrate.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.opencps.accountmgt.model.Citizen;
import org.opencps.accountmgt.service.CitizenLocalServiceUtil;
import org.opencps.dossiermgt.model.Dossier;
import org.opencps.dossiermgt.model.DossierFile;
import org.opencps.dossiermgt.model.DossierLog;
import org.opencps.dossiermgt.model.ServiceConfig;
import org.opencps.dossiermgt.service.DossierLocalServiceUtil;
import org.opencps.dossiermgt.service.DossierLogLocalServiceUtil;
import org.opencps.dossiermgt.service.ServiceConfigLocalServiceUtil;
import org.opencps.integrate.dao.model.IntegrateAPI;
import org.opencps.integrate.dao.service.IntegrateAPILocalServiceUtil;
import org.opencps.integrate.utils.APIUtils;
import org.opencps.integrate.utils.DossierUtils;
import org.opencps.integrate.utils.PortletUtil.SplitDate;
import org.opencps.paymentmgt.model.PaymentFile;
import org.opencps.servicemgt.model.ServiceInfo;
import org.opencps.servicemgt.service.ServiceInfoLocalServiceUtil;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.PortletConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.service.persistence.PortletUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.service.persistence.DLFolderUtil;

@Path("/api")
public class OCPSController {

	@POST
	@Path("/dossiers")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response addDossiers(@HeaderParam("apikey") String apikey,
			@Context HttpServletRequest request, String body) {

		JSONObject resp = JSONFactoryUtil.createJSONObject();

		OCPSPermission permit = new OCPSPermission();

		OCPSAuth auth = new OCPSAuth();

		IntegrateAPI api = auth.auth(apikey);

		boolean isPermit = permit.isAddDossierPermission(apikey);

		if (Validator.isNotNull(api)) {
			if (isPermit) {

				ServiceContext context = DossierUtils
						.getServletContext(request);

				String referenceuid = resp.getString("ReferenceUid");
				String servicecode = resp.getString("ServiceCode");
				String servicename = resp.getString("ServiceName");
				String agencycode = resp.getString("AgencyCode");
				String agencyname = resp.getString("AgencyName");
				String applicantname = resp.getString("ApplicantName");
				String applicantidtype = resp.getString("ApplicantIdType");
				String applicantidno = resp.getString("ApplicantIdNo");
				String address = resp.getString("Address");
				String citycode = resp.getString("CityCode");
				String cityname = resp.getString("CityName");
				String districtcode = resp.getString("DistrictCode");
				String districtname = resp.getString("DistrictName");
				String wardcode = resp.getString("WardCode");
				String wardname = resp.getString("WardName");
				String contacttelno = resp.getString("ContactTelNo");
				String contactemail = resp.getString("ContactEmail");
				String dossiernote = resp.getString("DossierNote");
				String submitdate = resp.getString("SubmitDate");
				String receivedate = resp.getString("ReceiveDate");
				String dossierno = resp.getString("DossierNo");
				String duedate = resp.getString("DueDate");
				String finishdate = resp.getString("FinishDate");
				String createdate = resp.getString("CreateDate");
				String modifieddate = resp.getString("ModifiedDate");
				String dossierstatus = resp.getString("DossierStatus");
				String statustext = resp.getString("StatusText");

				// If actor is Mobile
				if (auth.isUser(apikey)) {
					context.setUserId(api.getUserId());
				}

				if (auth.isAgency(apikey)) {
					// Create onlineUser
					if (Validator.isNotNull(contactemail)) {

						User userOfDossierOnline = APIUtils.getUserByEmail(
								context.getCompanyId(), contactemail);

						long userId = 0;

						if (userOfDossierOnline == null) {
							// create user citizen
							Calendar cal = Calendar.getInstance();
							int birthDateDay = cal.get(Calendar.DAY_OF_MONTH);
							int birthDateMonth = cal.get(Calendar.MONTH);
							int birthDateYear = cal.get(Calendar.YEAR);

							try {
								Citizen citizen = CitizenLocalServiceUtil
										.addCitizen(applicantname,
												StringPool.BLANK, 0,
												birthDateDay, birthDateMonth,
												birthDateYear, address,
												citycode, districtcode,
												wardcode, cityname,
												districtname, wardname,
												contactemail, StringPool.BLANK,
												context.getScopeGroupId(),
												StringPool.BLANK,
												StringPool.BLANK,
												StringPool.BLANK, null, 0,
												context);
								userId = citizen.getMappingUserId();

								if (citizen != null) {
									CitizenLocalServiceUtil
											.updateStatus(
													citizen.getCitizenId(),
													citizen.getUserId(),
													APIConstants.ACCOUNT_STATUS_APPROVED);
								}
								// update default passoword is telNo
								UserLocalServiceUtil.updatePassword(userId,
										contacttelno, contacttelno, false);

							} catch (Exception e) {
								_log.error(e);
							}

						} else {
							userId = userOfDossierOnline.getUserId();
						}
					}
				}

				try {
					// Create Dossier
					ServiceInfo serviceInfo = ServiceInfoLocalServiceUtil
							.getServiceInfoByServiceNo(servicecode);

					ServiceConfig serviceConfig = ServiceConfigLocalServiceUtil
							.getServiceConfigByG_S_G(context.getScopeGroupId(),
									serviceInfo.getServiceinfoId(), agencycode);

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
					String govAgencyName = serviceConfig.getGovAgencyName();
					int serviceMode = 1;
					
					String serviceAdministrationIndex = serviceConfig
							.getServiceAdministrationIndex();
					
/*					DLFolder dossierFolder = DLFolderUtil.getTargetFolder(
							context.getUserId(),
							context.getScopeGroupId(),
							context.getScopeGroupId(), false, 0,
							dossierDestinationFolder, StringPool.BLANK, false,
							context);
*/
/*					Dossier dossier = DossierLocalServiceUtil.addDossier(
							context.getUserId(), ownerOrganizationId,
							dossierTemplateId, templateFileNo, serviceConfigId,
							serviceInfoId, serviceDomainIndex,
							govAgencyOrganizationId, agencycode, agencyname,
							serviceMode, serviceAdministrationIndex, citycode,
							cityname, districtcode, districtname, wardname,
							wardcode, applicantname, applicantidno, address,
							applicantname, contacttelno, contactemail,
							dossiernote,
							PortletConstants.DOSSIER_SOURCE_DIRECT,
							PortletConstants.DOSSIER_STATUS_NEW,
							dossierFolder.getFolderId(), StringPool.BLANK,
							context);
*/
				} catch (Exception e) {
					return Response.status(404).entity(resp.toString()).build();
				}

				return Response.status(200).entity(resp.toString()).build();

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
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response alo(@Context HttpServletRequest request,
			@HeaderParam("username") String username,
			@HeaderParam("password") String password) {

		JSONObject resp = JSONFactoryUtil.createJSONObject();

		long companyId = PortalUtil.getCompanyId(request);

		User user = login(companyId, username, password);

		if (Validator.isNull(user)) {
			return Response.status(401).entity(resp.toString()).build();
		} else {

			resp.put("ApiKey", getAPI(user.getUserId()));
			resp.put("UserId", user.getUserId());
			resp.put("ScreenName", user.getScreenName());
			resp.put("FullName", user.getFullName());
			resp.put("UserEmail", user.getEmailAddress());

			return Response.status(200).entity(resp.toString()).build();
		}
	}

	/**
	 * GET /dossiers?...
	 * 
	 * @param apikey
	 * @param step
	 * @param agency
	 * @param userId
	 * @param status
	 * @param from
	 * @param max
	 * @return
	 */
	@GET
	@Path("/dossiers")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response getDossiers(@HeaderParam("apikey") String apikey,
			@QueryParam("step") int step, @QueryParam("agency") String agency,
			@QueryParam("user") long userId,
			@QueryParam("status") String status, @QueryParam("from") int from,
			@QueryParam("max") int max) {

		JSONObject resp = JSONFactoryUtil.createJSONObject();

		OCPSPermission permit = new OCPSPermission();

		OCPSAuth auth = new OCPSAuth();

		IntegrateAPI api = auth.auth(apikey);

		boolean isPermit = permit.isDossierPermission(apikey);

		if (Validator.isNotNull(api)) {
			if (isPermit) {
				String processNo = StringPool.BLANK;

				String stepCode = StringPool.BLANK;

				if (step != 0) {
					stepCode = Long.toString(step);
				}

				int start, end = 0;

				if (from <= 0 && max <= 0) {
					start = QueryUtil.ALL_POS;
					end = QueryUtil.ALL_POS;

				} else {
					start = from;
					end = from + max;
				}

				int count = 0;

				if (auth.isUser(apikey)) {

					int totalDossiers = APIUtils.countDossierAPI(processNo,
							stepCode, api.getUserId(), agency, status);

					List<Dossier> dossiers = APIUtils.searchDossierAPI(
							processNo, stepCode, api.getUserId(), agency,
							status, start, end);

					if (from <= 0 && max <= 0) {
						count = totalDossiers;
					} else {
						count = totalDossiers > (max + from) ? max
								: totalDossiers - from;
					}

					resp.put("Total", totalDossiers);
					resp.put("Count", count);
					resp.put("Dossiers", getDossiers(dossiers));

				}

				if (auth.isAgency(apikey)) {
					int totalDossiers = APIUtils.countDossierAPI(processNo,
							stepCode, 0, api.getAgency(), status);

					List<Dossier> dossiers = APIUtils.searchDossierAPI(
							processNo, stepCode, 0, api.getAgency(), status,
							start, end);

					if (from <= 0 && max <= 0) {
						count = totalDossiers;
					} else {
						count = totalDossiers > (max + from) ? max
								: totalDossiers - from;
					}

					resp.put("Total", totalDossiers);
					resp.put("Count", count);
					resp.put("Dossiers", getDossiers(dossiers));

				}

				return Response.status(200).entity(resp.toString()).build();

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
	 * GET /dossiers/{dossierId}
	 * 
	 * @param apikey
	 * @param dossierId
	 * @return
	 */

	@GET
	@Path("/dossiers/{dossierId: .*}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response getDossierDetail(@HeaderParam("apikey") String apikey,
			@PathParam("dossierId") long dossierId) {

		JSONObject resp = JSONFactoryUtil.createJSONObject();

		OCPSPermission permit = new OCPSPermission();

		OCPSAuth auth = new OCPSAuth();

		IntegrateAPI api = auth.auth(apikey);

		boolean isPermit = permit.isDossierDetailPermission(apikey, dossierId);

		if (Validator.isNotNull(api)) {
			if (isPermit) {
				Dossier dossier = APIUtils.getDossierById(dossierId);

				resp.put("DossierId", dossier.getDossierId());
				resp.put("ReferenceUid", dossier.getOid());
				resp.put("ServiceCode",
						APIUtils.getServiceCode(dossier.getServiceInfoId()));
				resp.put("ServiceName",
						APIUtils.getServiceName(dossier.getServiceInfoId()));
				resp.put("AgencyCode", dossier.getGovAgencyCode());
				resp.put("AgencyName", dossier.getGovAgencyName());
				resp.put("ApplicantName", dossier.getContactName());
				resp.put("ApplicantIdType",
						APIUtils.getApplicantIdType(dossier.getDossierId()));
				resp.put("ApplicantIdNo",
						APIUtils.getApplicantIdNo(dossier.getDossierId()));
				resp.put("Address", dossier.getAddress());
				resp.put("CityCode", dossier.getCityCode());
				resp.put("CityName", dossier.getCityName());
				resp.put("DistrictCode", dossier.getDistrictCode());
				resp.put("DistrictName", dossier.getDistrictName());
				resp.put("WardCode", dossier.getWardCode());
				resp.put("WardName", dossier.getWardName());
				resp.put("ContactTelNo", dossier.getContactTelNo());
				resp.put("ContactEmail", dossier.getContactEmail());
				resp.put("DossierNote", dossier.getNote());
				resp.put("SubmitDate",
						APIUtils.formatDateTime(dossier.getSubmitDatetime()));
				resp.put("ReceiveDate",
						APIUtils.formatDateTime(dossier.getReceiveDatetime()));
				resp.put("DossierNo", dossier.getReceptionNo());
				resp.put("DueDate",
						APIUtils.formatDateTime(dossier.getEstimateDatetime()));
				resp.put("FinishDate",
						APIUtils.formatDateTime(dossier.getFinishDatetime()));
				resp.put("CreateDate",
						APIUtils.formatDateTime(dossier.getCreateDate()));
				resp.put("ModifiedDate",
						APIUtils.formatDateTime(dossier.getModifiedDate()));
				resp.put("DossierStatus", dossier.getDossierStatus());
				resp.put("StatusText", dossier.getDossierStatus());

				return Response.status(200).entity(resp.toString()).build();

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
	 * GET /dossiers/{dossierId}/actions
	 * 
	 * @param apikey
	 * @param dossierId
	 * @return
	 */
	@GET
	@Path("/dossiers/{dossierId: .*}/actions")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response getDossierAction(@HeaderParam("apikey") String apikey,
			@PathParam("dossierId") long dossierId) {

		JSONObject resp = JSONFactoryUtil.createJSONObject();

		OCPSPermission permit = new OCPSPermission();

		OCPSAuth auth = new OCPSAuth();

		IntegrateAPI api = auth.auth(apikey);

		boolean isPermit = permit.isDossierActionPermission(apikey, dossierId);

		if (Validator.isNotNull(api)) {
			if (isPermit) {
				Dossier dossier = APIUtils.getDossierById(dossierId);
				resp.put("DossierId", dossier.getDossierId());
				resp.put("Count", APIUtils.countDossierLogs(dossierId));
				resp.put("Actions", getDossierLogs(1, dossierId));

				return Response.status(200).entity(resp.toString()).build();

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
	 * GET /dossiers/{dossierId}/dossierfiles
	 * 
	 * @param apikey
	 * @param dossierId
	 * @return
	 */
	@GET
	@Path("/dossiers/{dossierId: .*}/dossierfiles")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response getDossierFiles(@HeaderParam("apikey") String apikey,
			@PathParam("dossierId") long dossierId) {

		JSONObject resp = JSONFactoryUtil.createJSONObject();
		OCPSPermission permit = new OCPSPermission();

		OCPSAuth auth = new OCPSAuth();

		IntegrateAPI api = auth.auth(apikey);

		boolean isPermit = permit.isDossierActionPermission(apikey, dossierId);

		if (Validator.isNotNull(api)) {
			if (isPermit) {
				Dossier dossier = APIUtils.getDossierById(dossierId);
				resp.put("DossierId", dossier.getDossierId());
				resp.put("Count", APIUtils.countDossierFile(dossierId));
				resp.put("DossierFiles", getDossierFiles(dossierId));

				return Response.status(200).entity(resp.toString()).build();

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
	@Path("/dossiers/{dossierId: .*}/paymentfiles")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response getPaymentFiles(@HeaderParam("apikey") String apikey,
			@PathParam("dossierId") long dossierId) {

		JSONObject resp = JSONFactoryUtil.createJSONObject();

		OCPSPermission permit = new OCPSPermission();

		OCPSAuth auth = new OCPSAuth();

		IntegrateAPI api = auth.auth(apikey);

		boolean isPermit = permit.isPaymentFilePermission(apikey, dossierId);

		if (Validator.isNotNull(api)) {
			if (isPermit) {

				Dossier dossier = APIUtils.getDossierById(dossierId);

				resp.put("DossierId", dossier.getDossierId());
				resp.put("Count", APIUtils.countPaymentFile(dossierId));
				resp.put("PaymentFiles", getPaymentFiles(dossierId));

				return Response.status(200).entity(resp.toString()).build();

			} else {
				return Response.status(403).entity(resp.toString()).build();
			}
		} else {
			return Response.status(401).entity(resp.toString()).build();
		}
	}

	/**
	 * @param dossierId
	 * @return
	 */
	private JSONArray getPaymentFiles(long dossierId) {

		JSONArray results = JSONFactoryUtil.createJSONArray();

		List<PaymentFile> paymentFiles = APIUtils.getPaymentFiles(dossierId);

		for (PaymentFile file : paymentFiles) {

			JSONObject jsonFile = JSONFactoryUtil.createJSONObject();

			jsonFile.put("PaymentFileUid", file.getOid());
			jsonFile.put("PaymentFee", file.getPaymentName());
			jsonFile.put("PaymentAmount", file.getAmount());
			jsonFile.put("PaymentStatus", file.getPaymentStatus());
			jsonFile.put("PaymentNote", file.getApproveNote());
			jsonFile.put("PaymentMethod", file.getPaymentMethod());
			jsonFile.put("PaymentVoucherUrl",
					APIUtils.getFileURL(file.getConfirmFileEntryId()));
			jsonFile.put("InvoiceNo", file.getInvoiceNo());
			jsonFile.put("PaymentRequestDate",
					APIUtils.formatDateTime(file.getRequestDatetime()));

			results.put(jsonFile);
		}

		return results;
	}

	/**
	 * Get DossierFiles
	 * 
	 * @param dossierId
	 * @return
	 */
	private JSONArray getDossierFiles(long dossierId) {

		JSONArray results = JSONFactoryUtil.createJSONArray();

		List<DossierFile> dossierFiles = APIUtils.getDossierFile(dossierId);

		for (DossierFile file : dossierFiles) {
			JSONObject jsonFile = JSONFactoryUtil.createJSONObject();

			jsonFile.put("DossierFileUid", file.getOid());
			jsonFile.put("DossierPartNo",
					APIUtils.getDossierPartNo(file.getDossierFileId()));
			jsonFile.put("DossierFileName", file.getDisplayName());
			jsonFile.put("DossierFileContent",
					APIUtils.getDossierContent(file.getDossierFileId()));
			jsonFile.put("DossierFileNo", file.getTemplateFileNo());
			jsonFile.put("DossierFileDate",
					APIUtils.formatDateTime(file.getDossierFileDate()));
			jsonFile.put("DossierFileVersion", file.getVersion());
			jsonFile.put("AttachmentFileUrl",
					APIUtils.getFileURL(file.getFileEntryId()));
			jsonFile.put("AttachmentFileType", file.getDossierFileType());
			jsonFile.put("AttachmentFileSize",
					APIUtils.getFileSize(file.getDossierFileId()));
			jsonFile.put("CreateDate",
					APIUtils.formatDateTime(file.getCreateDate()));
			jsonFile.put("ModifiedDate",
					APIUtils.formatDateTime(file.getModifiedDate()));

			results.put(jsonFile);
		}

		return results;
	}

	/**
	 * @param typeLog
	 * @param dossierId
	 * @return
	 */
	private JSONArray getDossierLogs(int typeLog, long dossierId) {

		JSONArray results = JSONFactoryUtil.createJSONArray();

		List<DossierLog> dossierLogs = new ArrayList<>();

		try {
			dossierLogs = DossierLogLocalServiceUtil.findDossierLog(1,
					dossierId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			for (DossierLog log : dossierLogs) {

				JSONObject jsonLog = JSONFactoryUtil.createJSONObject();

				jsonLog.put("ActionUid", log.getOId());
				jsonLog.put("ActionCode", StringPool.BLANK);
				jsonLog.put("ActionName",
						APIUtils.getLanguageValue(log.getActionInfo()));
				jsonLog.put("ActionNote",
						APIUtils.getLanguageValue(log.getMessageInfo()));
				jsonLog.put("DossierStatus",
						APIUtils.getLanguageValue(log.getDossierStatus()));
				jsonLog.put("ModifiedDate",
						APIUtils.formatDateTime(log.getModifiedDate()));

				results.put(jsonLog);
			}

		} catch (Exception e) {
			_log.debug(e);
		}

		return results;
	}

	/**
	 * @param dossiers
	 * @return
	 */
	private JSONArray getDossiers(List<Dossier> dossiers) {
		JSONArray results = JSONFactoryUtil.createJSONArray();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		for (Dossier dossier : dossiers) {

			JSONObject obj = JSONFactoryUtil.createJSONObject();

			obj.put("DossierId", dossier.getDossierId());
			obj.put("ModifiedDate", sdf.format(dossier.getModifiedDate()));
			obj.put("ServiceName",
					APIUtils.getServiceName(dossier.getServiceInfoId()));
			obj.put("AgencyName", dossier.getGovAgencyName());
			obj.put("DossierNo", dossier.getReceptionNo());
			obj.put("DossierStatus", dossier.getDossierStatus());

			results.put(obj);
		}

		return results;

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

	private Log _log = LogFactoryUtil.getLog(OCPSController.class);

}
