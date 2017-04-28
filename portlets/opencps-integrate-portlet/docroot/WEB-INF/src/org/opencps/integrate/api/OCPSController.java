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
import org.opencps.dossiermgt.service.DossierLogLocalServiceUtil;
import org.opencps.dossiermgt.service.ServiceConfigLocalServiceUtil;
import org.opencps.integrate.dao.model.IntegrateAPI;
import org.opencps.integrate.dao.service.IntegrateAPILocalServiceUtil;
import org.opencps.integrate.utils.APIUtils;
import org.opencps.integrate.utils.DossierUtils;
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
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;

@Path("/api")
public class OCPSController {


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


	private Log _log = LogFactoryUtil.getLog(OCPSController.class);

}
