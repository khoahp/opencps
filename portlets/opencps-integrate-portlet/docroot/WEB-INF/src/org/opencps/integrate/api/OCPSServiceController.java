package org.opencps.integrate.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.opencps.datamgt.model.DictItem;
import org.opencps.datamgt.service.DictItemLocalServiceUtil;
import org.opencps.dossiermgt.model.ServiceConfig;
import org.opencps.dossiermgt.service.ServiceConfigLocalServiceUtil;
import org.opencps.integrate.utils.APIUtils;
import org.opencps.servicemgt.model.ServiceFileTemplate;
import org.opencps.servicemgt.model.ServiceInfo;
import org.opencps.servicemgt.model.TemplateFile;
import org.opencps.servicemgt.service.ServiceFileTemplateLocalServiceUtil;
import org.opencps.servicemgt.service.ServiceInfoLocalServiceUtil;
import org.opencps.servicemgt.service.TemplateFileLocalServiceUtil;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;

@Path("/api")
public class OCPSServiceController {

	// TODO: Hard code groupId = 20182, will be update
	public static int GROUPID = 20182;

	@GET
	@Path("/services")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response getServices(@QueryParam("keyword") String keyword,
			@QueryParam("admcode") String admcode,
			@QueryParam("domaincode") String domaincode,
			@QueryParam("from") int from, @QueryParam("max") int max) {

		JSONObject resp = JSONFactoryUtil.createJSONObject();

		if (Validator.isNull(admcode)) {
			admcode = "0";
		}

		if (Validator.isNull(domaincode)) {
			domaincode = "0";
		}

		int start, end = 0;

		try {
			if (from <= 0 && max <= 0) {
				start = QueryUtil.ALL_POS;
				end = QueryUtil.ALL_POS;

			} else {
				start = from;
				end = from + max;
			}

			int count = 0;

			int total = ServiceInfoLocalServiceUtil.countService(GROUPID,
					keyword, admcode, domaincode);

			List<ServiceInfo> results = ServiceInfoLocalServiceUtil
					.searchService(GROUPID, keyword, admcode, domaincode,
							start, end);

			if (from <= 0 && max <= 0) {
				count = total;
			} else {
				count = total > (max + from) ? max : total - from;
			}

			resp.put("Total", total);
			resp.put("Count", count);
			resp.put("Services", getServices(results));

			return Response.status(200).entity(resp.toString()).build();

		} catch (Exception e) {
			return Response.status(404).entity(resp.toString()).build();
		}

	}

	@GET
	@Path("/services/{serviceid}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response getServicesDetail(@PathParam("serviceid") long serviceid) {

		JSONObject resp = JSONFactoryUtil.createJSONObject();

		try {

			ServiceInfo si = ServiceInfoLocalServiceUtil
					.getServiceInfo(serviceid);

			resp.put("ServiceId", serviceid);
			resp.put("ServiceNo", si.getServiceNo());
			resp.put("ServiceMaxLevel", getServiceLevel(serviceid));
			resp.put("ServiceName", si.getServiceName());
			resp.put("FullName", si.getFullName());
			resp.put("AdministrationCode", si.getAdministrationCode());
			resp.put("AdministrationName", getItemName(si.getAdministrationCode()));
			resp.put("DomainCode", si.getDomainCode());
			resp.put("DomainName", getItemName(si.getDomainCode()));
			resp.put("ProcessText", si.getServiceProcess());
			resp.put("MethodText", si.getServiceMethod());
			resp.put("DossierText", si.getServiceDossier());
			resp.put("ConditionText", si.getServiceCondition());
			resp.put("DurationText", si.getServiceDuration());
			resp.put("ApplicantText", si.getServiceActors());
			resp.put("ResultText", si.getServiceResults());
			resp.put("RegularText", si.getServiceRecords());
			resp.put("FeeText", si.getServiceFee());
			resp.put("CreateDate", APIUtils.formatDateTime(si.getCreateDate()));
			resp.put("ModifiedDate", APIUtils.formatDateTime(si.getModifiedDate()));
			resp.put("TemplateForms", getFileTemplates(serviceid));
			resp.put("GovAgencies", getGovAgencies(serviceid));

			return Response.status(200).entity(resp.toString()).build();
		} catch (Exception e) {
			return Response.status(404).entity(resp.toString()).build();
		}

	}
	
	/**
	 * @param serviceId
	 * @return
	 */
	private JSONArray getGovAgencies(long serviceId) {
		JSONArray output = JSONFactoryUtil.createJSONArray();

		try {
			List<ServiceConfig> lsSC = ServiceConfigLocalServiceUtil
					.getServiceConfigsByS_G(serviceId, GROUPID);
			
			for (ServiceConfig sc : lsSC) {
				JSONObject in = JSONFactoryUtil.createJSONObject();
				in.put("AgencyCode", sc.getGovAgencyCode());
				in.put("AgencyName", sc.getGovAgencyName());
				in.put("ServiceLevel", sc.getServiceLevel());
				in.put("InstructionText", sc.getServiceInstruction());
				
				output.put(in);
			}
			
		} catch (Exception e) {
			_log.error(e);
		}

		return output;
	}

	/**
	 * @param serviceId
	 * @return
	 */
	private JSONArray getFileTemplates(long serviceId) {

		JSONArray ls = JSONFactoryUtil.createJSONArray();

		List<ServiceFileTemplate> serviceFiles = new ArrayList<ServiceFileTemplate>();

		try {
			serviceFiles = ServiceFileTemplateLocalServiceUtil
					.getServiceFileTemplatesByServiceInfo(serviceId);

			for (ServiceFileTemplate sft : serviceFiles) {
				TemplateFile tf = getTempFile(sft.getTemplatefileId());

				JSONObject jstf = JSONFactoryUtil.createJSONObject();

				jstf.put("FormName", tf.getFileName());
				jstf.put("FormNo", tf.getFileNo());
				jstf.put("FileType", APIUtils.getFileType(tf.getFileEntryId()));
				jstf.put("FileSize", APIUtils.getFileSize(tf.getFileEntryId()));
				jstf.put("FileURL", APIUtils.getFileURL(tf.getFileEntryId()));

				ls.put(jstf);
			}

		} catch (Exception e) {
			_log.debug(e);
		}

		return ls;
	}

	/**
	 * @param templateId
	 * @return
	 */
	private TemplateFile getTempFile(long templateId) {
		TemplateFile tf = null;

		try {
			tf = TemplateFileLocalServiceUtil.getTemplateFile(templateId);
		} catch (Exception e) {
			_log.debug(e);
		}

		return tf;
	}

	private JSONArray getServices(List<ServiceInfo> services) {

		JSONArray ls = JSONFactoryUtil.createJSONArray();

		for (ServiceInfo service : services) {
			JSONObject input = JSONFactoryUtil.createJSONObject();

			input.put("ServiceId", service.getServiceinfoId());
			input.put("ServiceNo", service.getServiceNo());
			input.put("ServiceMaxLevel",
					getServiceLevel(service.getServiceinfoId()));
			input.put("ServiceName", service.getServiceName());
			input.put("AdministrationCode", service.getAdministrationCode());
			input.put("AdministrationName",
					getItemName(service.getAdministrationCode()));
			input.put("DomainCode", service.getDomainCode());
			input.put("DomainName", getItemName(service.getDomainCode()));

			ls.put(input);
		}

		return ls;
	}

	private int getServiceLevel(long serviceInfoId) {

		int level = 2;

		long scopeGroupId = 20182;

		try {
			List<ServiceConfig> scls = ServiceConfigLocalServiceUtil
					.getServiceConfigsByS_G(serviceInfoId, scopeGroupId);

			for (ServiceConfig sc : scls) {
				// TODO: incorrect in case a serviceInfo have more serviceCongif
				level = sc.getServiceLevel();
			}

		} catch (Exception e) {
			_log.error(e);
		}

		return level;
	}

	/**
	 * @param dictItemCode
	 * @return
	 */
	private String getItemName(String dictItemCode) {
		String output = StringPool.BLANK;

		try {

			long dictItemId = GetterUtil.getLong(dictItemCode);

			Locale locale = new Locale("vi", "VN");

			DictItem di = DictItemLocalServiceUtil.getDictItem(dictItemId);
			output = di.getItemName(locale);
		} catch (Exception e) {
			_log.error(e);
		}

		return output;
	}

	private Log _log = LogFactoryUtil.getLog(OCPSServiceController.class);
}
