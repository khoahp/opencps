package com.opencps.intergrate.analayze;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.opencps.api.DossierStatusException;
import org.opencps.dossiermgt.model.Dossier;
import org.opencps.dossiermgt.model.DossierFile;
import org.opencps.dossiermgt.service.DossierLocalServiceUtil;
import org.opencps.util.PortletConstants;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.User;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.util.DLUtil;

public class IntergrateUtil {

	private static Log _log = LogFactoryUtil.getLog(IntergrateUtil.class);

	public static List<Dossier> getDossierByStatusApi(String govAgencyCode,
			String dossierStatus) throws SystemException, JSONException,
			DossierStatusException {

		List<Dossier> dossiers = new ArrayList<Dossier>();

		JSONArray jsonArrayDossierStatus = JSONFactoryUtil
				.createJSONArray(dossierStatus);

		for (int i = 0; i < jsonArrayDossierStatus.length(); i++) {

			JSONObject dossierStatusValueJSONObject = jsonArrayDossierStatus
					.getJSONObject(i);

			String dossierStatusValue = dossierStatusValueJSONObject
					.getString("value");

			// validateDossierStatus(dossierStatusValue);
			
			dossiers.addAll(DossierLocalServiceUtil.getByGovAndStatusAPI(
					govAgencyCode, dossierStatusValue, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS));
		}

		
		return dossiers;
	}

	private static void validateDossierStatus(String dossierStatusValue)
			throws DossierStatusException {
		if (!dossierStatusValue
				.equalsIgnoreCase(PortletConstants.DOSSIER_STATUS_SYSTEM)
				&& !dossierStatusValue
						.equalsIgnoreCase(PortletConstants.DOSSIER_STATUS_PAYING)) {
			throw new DossierStatusException();
		}
	}

	@SuppressWarnings("resource")
	public static byte[] getFileAsBytesFromUrl(AttachFile attachFile) {

		final byte[] fileByes = new byte[8192];

		byte[] data = new byte[8192];

		BufferedInputStream in = null;
		// FileOutputStream fout = null;
		try {
			in = new BufferedInputStream(
					new URL(attachFile.getFileUrl()).openStream());

			// fout = new FileOutputStream(attachFile.getFileName());

			ByteArrayOutputStream output = new ByteArrayOutputStream();

			int count;

			// byte[] bytes = IOUtils.toByteArray(is);

			/*
			 * while ((count = in.read(fileByes, 0, 8192)) != -1) {
			 * fout.write(fileByes, 0, count); }
			 */
			while ((count = in.read(fileByes)) != -1) {
				output.write(fileByes, 0, count);
			}

			data = output.toByteArray();

			return data;

		} catch (Exception e) {
			_log.error("get file from URL error");
			e.printStackTrace();

		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;

	}

	public static DossierFile latestDossierFile(
			List<DossierFile> listDossierFile) {

		DossierFile dossierFileTemp = listDossierFile.get(0);
		for (DossierFile dossierFile : listDossierFile) {
			if (Validator.isNotNull(dossierFileTemp.getModifiedDate())
					&& Validator.isNotNull(dossierFile.getModifiedDate())) {
				if (dossierFileTemp.getModifiedDate().compareTo(
						dossierFile.getModifiedDate()) < 0) {
					dossierFileTemp = dossierFile;
				}
			}

		}

		return dossierFileTemp;
	}

	public static boolean hasDossier(long dossierId) {

		boolean hasDossierFO = false;

		try {
			Dossier dossierClone = DossierLocalServiceUtil
					.fetchDossier(dossierId);
			if (Validator.isNotNull(dossierClone)) {
				hasDossierFO = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return hasDossierFO;
	}

	public static String getFileURL(FileEntry fileEntry,
			ServiceContext serviceContext, User user) throws PortalException,
			SystemException {

		String portalURL = serviceContext.getPortalURL();

		if (Validator.isNull(portalURL)) {
			Company company = CompanyLocalServiceUtil.getCompany(user
					.getCompanyId());

			portalURL = PortalUtil.getPortalURL(company.getVirtualHostname(),
					PortalUtil.getPortalPort(false), false);
		}

		String fileURL = portalURL
				+ DLUtil.getPreviewURL(fileEntry, fileEntry.getFileVersion(),
						null, "");

		return fileURL;
	}

}
