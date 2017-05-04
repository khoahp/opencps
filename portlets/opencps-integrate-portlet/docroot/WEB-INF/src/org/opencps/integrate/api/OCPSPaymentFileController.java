package org.opencps.integrate.api;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.opencps.dossiermgt.model.Dossier;
import org.opencps.dossiermgt.service.DossierLocalServiceUtil;
import org.opencps.integrate.dao.model.IntegrateAPI;
import org.opencps.integrate.utils.APIUtils;
import org.opencps.integrate.utils.PaymentFileModel;
import org.opencps.integrate.utils.PaymentFileUtils;
import org.opencps.paymentmgt.model.PaymentFile;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.Validator;

@Path("/api")
public class OCPSPaymentFileController {
	
	@POST
	@Path("/dossiers/{dossierid: .*}/paymentfiles")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response addPaymentFile(@HeaderParam("apikey") String apikey,
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
					
					PaymentFileModel pfm = PaymentFileUtils
							.getPaymentFile(body);

					PaymentFile paymentFile = PaymentFileUtils
							.getPaymentFileByIod(pfm.getPaymentFileUid());

					Dossier dossier = DossierLocalServiceUtil
							.getDossier(dossierid);

					if (Validator.isNull(paymentFile)) {
						
						/*paymentFile = PaymentFileLocalServiceUtil
								.addPaymentFile(dossierId, fileGroupId,
										ownerUserId, ownerOrganizationId,
										govAgencyOrganizationId, paymentName,
										requestDatetime, amount, requestNote,
										placeInfo, paymentOptions);
						*/
						
						// TODO : Add OnlinePayURL
						
						// TODO : Add notifications for payment
						
						// TODO : Add DossierLog
						
						
						resp.put("Result", "New");
						resp.put("DossierId", dossier.getDossierId());
						resp.put("PaymentFileUid", paymentFile.getOid());

						return Response.status(200).entity(resp.toString())
								.build();
					} else {
						resp.put("Result", "Exist");
						resp.put("DossierId", dossier.getDossierId());
						resp.put("PaymentFileUid", paymentFile.getOid());
						resp.put("ErrorMessage", APIUtils
								.getLanguageValue("duplicate-payments-file"));

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
	
	
	@PUT
	@Path("/dossiers/{dossierid: .*}/paymentfiles/{paymentfileuid .*}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response updatePaymentFile(@HeaderParam("apikey") String apikey,
			@Context HttpServletRequest request,
			@PathParam("dossierid") long dossierid,
			@PathParam("paymentfileuid") String paymentfileuid, String body) {

		JSONObject resp = JSONFactoryUtil.createJSONObject();

		OCPSPermission permit = new OCPSPermission();

		OCPSAuth auth = new OCPSAuth();

		IntegrateAPI api = auth.auth(apikey);

		boolean isPermit = permit.isDossierPermission(apikey)
				&& permit.isDossierDetailPermission(apikey, dossierid);

		if (Validator.isNotNull(api)) {
			if (isPermit) {
				try {
					
					PaymentFileModel pfm = PaymentFileUtils
							.getPaymentFile(body);

					PaymentFile paymentFile = PaymentFileUtils
							.getPaymentFileByIod(paymentfileuid);

					Dossier dossier = DossierLocalServiceUtil
							.getDossier(dossierid);

					if (Validator.isNotNull(paymentFile)) {
						
						// TODO : update paymentStatus
						
						// TODO : Add notifications for payment
						
						// TODO : Add DossierLog
						
						
						resp.put("Result", "New");
						resp.put("DossierId", dossier.getDossierId());
						resp.put("PaymentFileUid", paymentFile.getOid());

						return Response.status(200).entity(resp.toString())
								.build();
					} else {
						resp.put("Result", "Error");
						resp.put("DossierId", dossier.getDossierId());
						resp.put("PaymentFileUid", paymentFile.getOid());
						resp.put("ErrorMessage", APIUtils
								.getLanguageValue("no-paymentfile-this-oid"));

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


}
