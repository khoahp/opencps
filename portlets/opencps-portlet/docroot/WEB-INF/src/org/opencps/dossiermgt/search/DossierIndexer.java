/**
 * OpenCPS is the open source Core Public Services software
 * Copyright (C) 2016-present OpenCPS community

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>
 */
package org.opencps.dossiermgt.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.portlet.PortletURL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencps.dossiermgt.model.Dossier;
import org.opencps.dossiermgt.model.DossierFile;
import org.opencps.dossiermgt.model.FileGroup;
import org.opencps.dossiermgt.permissions.DossierPermission;
import org.opencps.dossiermgt.service.DossierFileLocalServiceUtil;
import org.opencps.dossiermgt.service.DossierLocalServiceUtil;
import org.opencps.dossiermgt.service.FileGroupLocalServiceUtil;
import org.opencps.dossiermgt.service.persistence.DossierActionableDynamicQuery;
import org.opencps.dossiermgt.util.PortletKeys;
import org.opencps.processmgt.model.ProcessOrder;
import org.opencps.processmgt.service.ProcessOrderLocalServiceUtil;
import org.opencps.util.ActionKeys;

import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BaseIndexer;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchEngineUtil;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.permission.PermissionChecker;

/**
 * @author trungdk
 */
public class DossierIndexer extends BaseIndexer {
	public static final String[] CLASS_NAMES = { Dossier.class.getName() };

	public static final String PORTLET_ID = PortletKeys.PORTLET_19;

	@Override
	public String[] getClassNames() {
		// TODO Auto-generated method stub
		return CLASS_NAMES;
	}

	@Override
	public String getPortletId() {
		// TODO Auto-generated method stub
		return PORTLET_ID;
	}

	@Override
	public boolean hasPermission(PermissionChecker permissionChecker,
			String dossierClassName, long dossierClassPK, String actionId)
			throws Exception {

		return DossierPermission.contains(permissionChecker, dossierClassPK,
				ActionKeys.VIEW);
	}

	@Override
	protected void doDelete(Object obj) throws Exception {
		// TODO Auto-generated method stub
		Dossier dossier = (Dossier) obj;

		deleteDocument(dossier.getCompanyId(), dossier.getDossierId());
	}

	@Override
	protected Document doGetDocument(Object obj) throws Exception {
		// TODO Auto-generated method stub
		Dossier dossier = (Dossier) obj;

		Document document = getBaseModelDocument(PORTLET_ID, dossier);

		if (dossier.getReceptionNo() != null
				&& !Validator.isBlank(dossier.getReceptionNo())) {
			Field field = new Field(DossierDisplayTerms.RECEPTION_NO,
					dossier.getReceptionNo());
			field.setBoost(5);
			document.add(field);
		}
		if (dossier.getModifiedDate() != null) {
			document.addDate(Field.MODIFIED_DATE, dossier.getModifiedDate());
		}
		if (dossier.getCityName() != null
				&& !Validator.isBlank(dossier.getCityName())) {
			document.addText(DossierDisplayTerms.CITY_NAME, dossier
					.getCityName().toLowerCase().split("\\s+"));
		}
		if (dossier.getExternalRefNo() != null
				&& !Validator.isBlank(dossier.getExternalRefNo())) {
			document.addText(DossierDisplayTerms.EXTERNALREF_NO,
					dossier.getExternalRefNo());
		}
		if (dossier.getExternalRefUrl() != null
				&& !Validator.isBlank(dossier.getExternalRefUrl())) {
			document.addText(DossierDisplayTerms.EXTERNALREF_URL,
					dossier.getExternalRefUrl());
		}
		if (dossier.getGovAgencyName() != null
				&& !Validator.isBlank(dossier.getGovAgencyName())) {
			document.addText(DossierDisplayTerms.GOVAGENCY_NAME, dossier
					.getGovAgencyName().toLowerCase().split("\\s+"));
		}
		if (dossier.getSubjectName() != null
				&& !Validator.isBlank(dossier.getSubjectName())) {
			document.addText(DossierDisplayTerms.SUBJECT_NAME, dossier
					.getSubjectName().toLowerCase().split("\\s+"));
		}
		if (dossier.getAddress() != null
				&& !Validator.isBlank(dossier.getAddress())) {
			document.addText(DossierDisplayTerms.ADDRESS, dossier.getAddress()
					.toLowerCase().split("\\s+"));
		}
		if (dossier.getCityCode() != null
				&& !Validator.isBlank(dossier.getCityCode())) {
			document.addText(DossierDisplayTerms.CITY_CODE,
					dossier.getCityCode());
		}
		if (dossier.getDistrictCode() != null
				&& !Validator.isBlank(dossier.getDistrictCode())) {
			document.addText(DossierDisplayTerms.DISTRICT_CODE,
					dossier.getDistrictCode());
		}
		if (dossier.getDistrictName() != null
				&& !Validator.isBlank(dossier.getDistrictName())) {
			document.addText(DossierDisplayTerms.DISTRICT_NAME, dossier
					.getDistrictName().toLowerCase().split("\\s+"));
		}
		if (dossier.getWardCode() != null
				&& !Validator.isBlank(dossier.getWardCode())) {
			document.addText(DossierDisplayTerms.WARD_CODE,
					dossier.getWardCode());
		}
		if (dossier.getWardName() != null
				&& !Validator.isBlank(dossier.getWardName())) {
			document.addText(DossierDisplayTerms.WARD_NAME, dossier
					.getWardName().toLowerCase().split("\\s+"));
		}
		if (dossier.getContactName() != null
				&& !Validator.isBlank(dossier.getContactName())) {
			document.addText(DossierDisplayTerms.CONTACT_NAME, dossier
					.getContactName().toLowerCase().split("\\s+"));
		}
		if (dossier.getContactTelNo() != null
				&& !Validator.isBlank(dossier.getContactTelNo())) {
			document.addText(DossierDisplayTerms.CONTACT_TEL_NO,
					dossier.getContactTelNo());
		}
		if (dossier.getContactEmail() != null
				&& !Validator.isBlank(dossier.getContactEmail())) {
			document.addText(DossierDisplayTerms.CONTACT_EMAIL,
					dossier.getContactEmail());
		}
		if (dossier.getNote() != null && !Validator.isBlank(dossier.getNote())) {
			document.addText(DossierDisplayTerms.NOTE, dossier.getNote()
					.toLowerCase().split("\\s+"));
		}
		document.addNumber(DossierDisplayTerms.DOSSIER_ID,
				dossier.getDossierId());

		document.addKeyword(Field.GROUP_ID,
				getSiteGroupId(dossier.getGroupId()));
		document.addKeyword(Field.SCOPE_GROUP_ID, dossier.getGroupId());

		// add by trungnt
		document.addDate(DossierDisplayTerms.SUBMIT_DATETIME,
				dossier.getSubmitDatetime());
		document.addDate(DossierDisplayTerms.ESTIMATE_DATETIME,
				dossier.getEstimateDatetime());
		document.addDate(DossierDisplayTerms.FINISH_DATETIME,
				dossier.getFinishDatetime());

		document.addNumber(DossierDisplayTerms.OWNERORGANIZATION_ID,
				dossier.getOwnerOrganizationId());

		document.addNumber(DossierDisplayTerms.USER_ID, dossier.getUserId());

		document.addKeyword(DossierDisplayTerms.DOSSIER_STATUS,
				dossier.getDossierStatus());

		// Thanh phan ho so chinh dang su dung (fileGroupId = 0, removed = 0)

		List<DossierFile> dossierFiles = new ArrayList<DossierFile>();
		try {
			dossierFiles = DossierFileLocalServiceUtil
					.getDossierFileByDID_GFID_R(dossier.getDossierId(), 0, 0);

		} catch (Exception e) {
			_log.info("No found dossierfile list width dossierId = "
					+ dossier.getDossierId() + " : Cause " + e.getCause());
		}

		if (dossierFiles != null) {

			List<Object[]> keyValues = new ArrayList<Object[]>();

			keyValues = getKeyValues(dossierFiles, keyValues);

			if (keyValues != null) {
				for (Object[] keyValue : keyValues) {
					document.addKeyword(keyValue[0].toString(),
							keyValue[1].toString());
				}
			}

		}

		// Thanh phan ho so rieng

		List<FileGroup> fileGroups = new ArrayList<FileGroup>();
		try {
			fileGroups = FileGroupLocalServiceUtil
					.getFileGroupByDossierId(dossier.getDossierId());

		} catch (Exception e) {
			_log.info("No found fileGroups list width dossierId = "
					+ dossier.getDossierId() + " : Cause " + e.getCause());
		}

		if (fileGroups != null) {
			for (FileGroup fileGroup : fileGroups) {
				try {
					List<DossierFile> dossierFilesTemp = DossierFileLocalServiceUtil
							.getDossierFileByDID_GFID_R(dossier.getDossierId(),
									fileGroup.getFileGroupId(), 0);

					if (dossierFilesTemp != null) {

						List<Object[]> keyValues = new ArrayList<Object[]>();

						keyValues = getKeyValues(dossierFilesTemp, keyValues);

						if (keyValues != null) {
							for (Object[] keyValue : keyValues) {
								document.addKeyword(keyValue[0].toString(),
										keyValue[1].toString());
							}
						}

					}

					// get ProcessOrder

					// ProcessOrder processOrder = ProcessOrderLocalServiceUtil
					// .getProcessOrder(dossier.getDossierId(),
					// fileGroup.getFileGroupId());

				} catch (Exception e) {
					continue;
				}
			}
		}

		/*
		 * try { long fileGroupId = 0;// fileGroupId > 0 => ho so rieng
		 * processOrder = ProcessOrderLocalServiceUtil.getProcessOrder(
		 * dossier.getDossierId(), fileGroupId); } catch (Exception e) { // //
		 * TODO: handle exception }
		 */

		return document;
	}

	@Override
	protected Summary doGetSummary(Document document, Locale locale,
			String snippet, PortletURL portletURL) throws Exception {
		// TODO Auto-generated method stub
		Summary summary = createSummary(document);

		summary.setMaxContentLength(200);

		return summary;
	}

	@Override
	protected void doReindex(Object obj) throws Exception {
		// TODO Auto-generated method stub
		Dossier dossier = (Dossier) obj;

		Document document = getDocument(dossier);

		SearchEngineUtil.updateDocument(getSearchEngineId(),
				dossier.getCompanyId(), document);
	}

	@Override
	protected void doReindex(String[] ids) throws Exception {
		// TODO Auto-generated method stub
		long companyId = GetterUtil.getLong(ids[0]);

		reindexEntries(companyId);
	}

	protected List<Object[]> getKeyValues(List<DossierFile> dossierFiles,
			List<Object[]> keyValues) {

		for (DossierFile dossierFile : dossierFiles) {

			if (Validator.isNotNull(dossierFile.getFormData())) {
				try {
					JSONObject jsonObject = new JSONObject(
							dossierFile.getFormData());
					keyValues = parseJSONObject(keyValues, jsonObject);
				} catch (Exception e) {
					_log.info("Can not parse json object from FormData: =>"

					+ " dossierFileId=" + dossierFile.getDossierFileId()
							+ " : Cause " + e.getCause());
					continue;
				}
			}

		}
		return keyValues;

	}

	/**
	 * @param json
	 * @return
	 */
	protected List<Object[]> parseJSONObject(List<Object[]> keyValues,
			JSONObject json) {
		List<Object[]> objects = new ArrayList<Object[]>();
		try {

			Iterator<String> itr = json.keys();
			while (itr.hasNext()) {
				String key = itr.next();
				Object object = json.get(key);
				if (object instanceof JSONObject) {
					// Tinh chung cho key cha.
					Object[] keyValue = new Object[2];
					keyValue[0] = key;
					keyValue[1] = object.toString();
					keyValues.add(keyValue);
					parseJSONObject(keyValues, json.getJSONObject(key));
				} else if (object instanceof JSONArray) {
					JSONArray jsonArray = json.getJSONArray(key);
					Object[] keyValue = new Object[2];
					// Tinh chung cho key cha
					keyValue[0] = key;
					keyValue[1] = jsonArray.toString();
					keyValues.add(keyValue);
					parseJSONObject(keyValues, jsonArray);
				} else {
					Object[] keyValue = new Object[2];
					keyValue[0] = key;
					keyValue[1] = object.toString();
					keyValues.add(keyValue);
				}
			}

		} catch (JSONException e) {
			_log.error(e);
		}

		return objects;
	}

	/**
	 * @param keyValues
	 * @param jsonArray
	 * @return
	 * @throws JSONException
	 */
	protected List<Object[]> parseJSONObject(List<Object[]> keyValues,
			JSONArray jsonArray) throws JSONException {
		if (jsonArray != null && jsonArray.length() > 0) {
			for (int i = 0; i < jsonArray.length(); i++) {
				Object tempObject = jsonArray.get(i);
				if (tempObject instanceof JSONObject) {
					parseJSONObject(keyValues, (JSONObject) tempObject);
				} else if (tempObject instanceof JSONArray) {
					parseJSONObject(keyValues, (JSONArray) tempObject);
				} else {
					// Tinh chung cho key cha.
				}
			}
		}

		return keyValues;
	}

	protected void reindexEntries(long companyId) throws PortalException,
			SystemException {

		final Collection<Document> documents = new ArrayList<Document>();

		ActionableDynamicQuery actionableDynamicQuery = new DossierActionableDynamicQuery() {

			@Override
			protected void addCriteria(DynamicQuery dynamicQuery) {
			}

			@Override
			protected void performAction(Object object) throws PortalException {
				Dossier dossier = (Dossier) object;

				Document document = getDocument(dossier);

				documents.add(document);
			}

		};

		actionableDynamicQuery.setCompanyId(companyId);

		actionableDynamicQuery.performActions();

		SearchEngineUtil.updateDocuments(getSearchEngineId(), companyId,
				documents);
	}

	@Override
	protected void doReindex(String className, long classPK) throws Exception {
		// TODO Auto-generated method stub
		Dossier dossier = DossierLocalServiceUtil.getDossier(classPK);

		doReindex(dossier);
	}

	@Override
	protected String getPortletId(SearchContext searchContext) {
		// TODO Auto-generated method stub
		return PORTLET_ID;
	}

	private Log _log = LogFactoryUtil.getLog(DossierIndexer.class.getName());
}
