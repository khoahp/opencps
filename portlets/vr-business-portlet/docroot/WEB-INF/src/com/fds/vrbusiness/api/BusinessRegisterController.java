package com.fds.vrbusiness.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.opencps.dossiermgt.model.BusinessRegister;
import org.opencps.dossiermgt.model.ServiceOption;
import org.opencps.dossiermgt.service.BusinessRegisterLocalServiceUtil;
import org.opencps.dossiermgt.service.ServiceOptionLocalServiceUtil;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.Validator;

@Path("/businesreg")

public class BusinessRegisterController {
	
	public static final String BUSINESS_REGISTER = "BUSINESS_REGISTER";
	
	@GET
	@Path("/status/groupid/{groupid: .*}/orgid/{orgid: .*}/serviceconfigid/{serviceconfigid: .*}/dossiertemplate/{dossiertemplateid: .*}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response getTecSpecs(@PathParam("groupid") long groupid,
			@PathParam("orgid") long orgid,
			@PathParam("serviceconfigid") long serviceconfigid,
			@PathParam("dossiertemplateid") long dossiertemplateid) {

		JSONObject resp = JSONFactoryUtil.createJSONObject();

		try {

			ServiceOption serviceOption = ServiceOptionLocalServiceUtil
					.getServiceOptionByG_SCID_DTID(groupid, serviceconfigid,
							dossiertemplateid);
			
			boolean isRequire = false;
			
			boolean isValid = false;
			
			String autoSelect = serviceOption.getAutoSelect();
			
			int regStatus = checkRegisterStatus(orgid);
			
			
			if (autoSelect.contains(BUSINESS_REGISTER)) {
				isRequire = true;
			}
			
			if (isRequire ) {
				if (regStatus == 1) {
					
					resp.put("StatusCode", "REG_DONE");
					isValid = true;
				} else if (regStatus == 0){
					resp.put("StatusCode", "REG_VERIFYING");
					
					isValid = false;
				} else {
					resp.put("StatusCode", "REG_REQUIRE");
					
					isValid = false;
				}
			} else {
				resp.put("StatusCode", "BY_PASS");

				isValid = true;
			}
			
			resp.put("IsValid", isValid);
			
			return Response.status(200).entity(resp.toString()).build();

		} catch (Exception e) {

			resp.put("ErrorMessage", e.toString());
			return Response.status(404).entity(resp.toString()).build();

		}

	}
	
	private BusinessRegister getBusinessReg(long orgId) {
		BusinessRegister br = null;
		
		try {
			br = BusinessRegisterLocalServiceUtil.getBusinessRegisterByOrgId(orgId);
		} catch (Exception e) {
			// Nothing to do
		}
		
		return br;
	}
	
	private int checkRegisterStatus(long orgId) {
		int status = 0;
		
		BusinessRegister br = getBusinessReg(orgId);
		
		if (Validator.isNull(br)) {
			//Hasn't been registered
			status = -1;
		} else {
			status = br.getValidStatus();
		}
		
		return status;
	}

}
