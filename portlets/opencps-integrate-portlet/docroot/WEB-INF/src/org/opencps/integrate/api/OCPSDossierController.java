package org.opencps.integrate.api;

import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.opencps.accountmgt.model.Citizen;
import org.opencps.accountmgt.service.CitizenLocalServiceUtil;
import org.opencps.dossiermgt.model.DossierPart;
import org.opencps.dossiermgt.model.DossierTemplate;
import org.opencps.dossiermgt.model.ServiceConfig;
import org.opencps.dossiermgt.service.DossierPartLocalServiceUtil;
import org.opencps.dossiermgt.service.DossierTemplateLocalServiceUtil;
import org.opencps.dossiermgt.service.ServiceConfigLocalServiceUtil;
import org.opencps.integrate.dao.model.IntegrateAPI;
import org.opencps.integrate.utils.APIUtils;
import org.opencps.integrate.utils.DossierUtils;
import org.opencps.servicemgt.model.ServiceInfo;
import org.opencps.servicemgt.service.ServiceInfoLocalServiceUtil;

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

@Path("/api")
public class OCPSDossierController {

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

					/*
					 * DLFolder dossierFolder = DLFolderUtil.getTargetFolder(
					 * context.getUserId(), context.getScopeGroupId(),
					 * context.getScopeGroupId(), false, 0,
					 * dossierDestinationFolder, StringPool.BLANK, false,
					 * context);
					 */
					/*
					 * Dossier dossier = DossierLocalServiceUtil.addDossier(
					 * context.getUserId(), ownerOrganizationId,
					 * dossierTemplateId, templateFileNo, serviceConfigId,
					 * serviceInfoId, serviceDomainIndex,
					 * govAgencyOrganizationId, agencycode, agencyname,
					 * serviceMode, serviceAdministrationIndex, citycode,
					 * cityname, districtcode, districtname, wardname, wardcode,
					 * applicantname, applicantidno, address, applicantname,
					 * contacttelno, contactemail, dossiernote,
					 * PortletConstants.DOSSIER_SOURCE_DIRECT,
					 * PortletConstants.DOSSIER_STATUS_NEW,
					 * dossierFolder.getFolderId(), StringPool.BLANK, context);
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

	private Log _log = LogFactoryUtil.getLog(OCPSDossierController.class);
}
