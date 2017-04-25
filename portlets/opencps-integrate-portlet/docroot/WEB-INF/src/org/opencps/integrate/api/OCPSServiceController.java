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

	@GET
	@Path("/services")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response getServices(@QueryParam("keyword") String keyword,
			@QueryParam("admcode") String admcode,
			@QueryParam("domaincode") String domaincode,
			@QueryParam("level") Integer level, @QueryParam("from") int from,
			@QueryParam("max") int max) {

		JSONObject resp = JSONFactoryUtil.createJSONObject();
		
		if (Validator.isNull(level)) {
			level = null;
		}

		if (Validator.isNull(admcode)) {
			admcode = "0";
		}

		if (Validator.isNull(domaincode)) {
			domaincode = "0";
		}

		// TODO: Hard code groupId = 20182, will be update
		long scopeGroupId = 20182;

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

			int total = ServiceInfoLocalServiceUtil.countServiceAPI(scopeGroupId,
					keyword, admcode, domaincode, level);

			List<ServiceInfo> results = ServiceInfoLocalServiceUtil
					.searchServiceAPI(scopeGroupId, keyword, admcode, domaincode,
							start, end, level);

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
			resp.put("ServiceLevel", getServiceLevel(serviceid));
			resp.put("ServiceName", si.getServiceName());
			resp.put("AdmServiceCode", si.getAdministrationCode());
			resp.put("AdmServiceName", getItemName(si.getAdministrationCode()));
			resp.put("DomainServiceCode", si.getDomainCode());
			resp.put("DomainServiceName", getItemName(si.getDomainCode()));
			resp.put("ServiceProcess", si.getServiceProcess());
			resp.put("ServiceMethod", si.getServiceMethod());
			resp.put("ServiceDossier", si.getServiceDossier());
			resp.put("ServiceCondition", si.getServiceCondition());
			resp.put("ServiceDuration", si.getServiceDuration());
			resp.put("ServiceActors", si.getServiceActors());
			resp.put("ServiceResults", si.getServiceResults());
			resp.put("ServiceRecords", si.getServiceRecords());
			resp.put("ServiceFee", si.getServiceFee());
			resp.put("ActiveStatus", si.getActiveStatus());
			resp.put("TemplateFiles", getFileTemplates(serviceid));

			return Response.status(200).entity(resp.toString()).build();
		} catch (Exception e) {
			return Response.status(404).entity(resp.toString()).build();
		}

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

				jstf.put("FileName", tf.getFileName());
				jstf.put("FileNo", tf.getFileNo());
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
			input.put("ServiceLevel", getServiceLevel(service.getServiceinfoId()));
			input.put("ServiceName", service.getServiceName());
			input.put("AdmServiceCode", service.getAdministrationCode());
			input.put("AdmServiceName",
					getItemName(service.getAdministrationCode()));
			input.put("DomainServiceCode", service.getDomainCode());
			input.put("DomainServiceName", getItemName(service.getDomainCode()));

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
				// TODO: implement 
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
