package org.opencps.integrate.utils;

import javax.servlet.http.HttpServletRequest;

import org.opencps.dossiermgt.model.Dossier;
import org.opencps.dossiermgt.service.DossierLocalServiceUtil;
import org.opencps.integrate.dao.InvalidMessageContentException;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.util.PortalUtil;

public class DossierUtils {
	
	/**
	 * @param input
	 * @return
	 * @throws InvalidMessageContentException
	 */
	public static ActionModel getActionModel(String input)
			throws InvalidMessageContentException {
		ActionModel am = new ActionModel();

		try {
			JSONObject jsInput = JSONFactoryUtil.createJSONObject(input);

			am.setActionCode(jsInput.getString("ActionCode"));
			am.setActionName(jsInput.getString("ActionName"));
			am.setActionNote(jsInput.getString("ActionNote"));
			am.setModifiedDate(APIUtils.convertDateTime(jsInput
					.getString("ModifiedDate")));
			am.setReceviceDate(APIUtils.convertDateTime(jsInput
					.getString("ReceiveDate")));
			am.setDossierNo(jsInput.getString("DossierNo"));
			am.setDueDate(APIUtils.convertDateTime(jsInput.getString("DueDate")));
			am.setFinishedDate(APIUtils.convertDateTime(jsInput
					.getString("FinishDate")));

		} catch (Exception e) {
			throw new InvalidMessageContentException();
		}

		return am;
	}
	
	public static Dossier getDossierIdByOid(String oid) {
		Dossier dossier = null;
		
		try {
			dossier = DossierLocalServiceUtil.getDossierByOId(oid);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return dossier;
	}

	/**
	 * @param groupId
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public static String getDossierDestinationFolder(long groupId, int year,
			int month, int day) {

		return String.valueOf(groupId) + StringPool.SLASH + "Dossiers"
				+ StringPool.SLASH + String.valueOf(year) + StringPool.SLASH
				+ String.valueOf(month) + StringPool.SLASH
				+ String.valueOf(day);
	}

	/**
	 * @param req
	 * @return
	 */
	public static ServiceContext getServletContext(HttpServletRequest req) {
		ServiceContext context = new ServiceContext();

		try {
			context.setScopeGroupId(PortalUtil.getScopeGroupId(req));
			context.setCompanyId(PortalUtil.getCompanyId(req));
		} catch (Exception e) {

		}

		return context;
	}

	/**
	 * @param input
	 * @return
	 */
	public static DossierModel getDossierModel(JSONObject input) {

		DossierModel dm = new DossierModel();

		try {
			dm.setReferenceUid(input.getString("ReferenceUid"));
			dm.setServiceCode(input.getString("ServiceCode"));
			dm.setServiceName(input.getString("ServiceName"));
			dm.setAgencyCode(input.getString("AgencyCode"));
			dm.setAgencyName(input.getString("AgencyName"));
			dm.setApplicantName(input.getString("ApplicantName"));
			dm.setApplicantIdNo(input.getString("ApplicantIdNo"));
			dm.setApplicantIdType(input.getString("ApplicantIdType"));
			dm.setAddress(input.getString("Address"));
			dm.setCityCode(input.getString("CityCode"));
			dm.setCityName(input.getString("CityName"));
			dm.setDistrictCode(input.getString("DistrictCode"));
			dm.setDistrictName(input.getString("DistrictName"));
			dm.setWardCode(input.getString("WardCode"));
			dm.setWardName(input.getString("WardName"));
			dm.setContactTelNo(input.getString("ContactTelNo"));
			dm.setContactEmail(input.getString("ContactEmail"));
			dm.setDossierNote(input.getString("DossierNote"));
			dm.setSubmitDate(APIUtils.convertDateTime(input
					.getString("SubmitDate")));
			dm.setReceiveDate(APIUtils.convertDateTime(input
					.getString("ReceiveDate")));
			dm.setdDossierNo(input.getString("DossierNo"));
			dm.setDueDate(input.getString("DueDate"));
			dm.setFinishDate(APIUtils.convertDateTime(input
					.getString("FinishDate")));
			dm.setCreateDate(APIUtils.convertDateTime(input
					.getString("CreateDate")));
			dm.setModifiedDate(APIUtils.convertDateTime(input
					.getString("ModifiedDate")));
			dm.setDossierStatus(input.getString("DossierStatus"));
			dm.setStatusText(input.getString("StatusText"));
		} catch (Exception e) {
			_log.debug(e);
		}

		return dm;
	}

	private static Log _log = LogFactoryUtil.getLog(DossierUtils.class);
}
