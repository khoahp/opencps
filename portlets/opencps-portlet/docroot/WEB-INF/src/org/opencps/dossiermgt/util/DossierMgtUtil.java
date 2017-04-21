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

package org.opencps.dossiermgt.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

import org.apache.commons.io.IOUtils;
import org.opencps.accountmgt.model.Business;
import org.opencps.accountmgt.model.Citizen;
import org.opencps.dossiermgt.comparator.DossierSubmitDateComparator;
import org.opencps.dossiermgt.comparator.DossierTemplateNameComparator;
import org.opencps.dossiermgt.comparator.DossierTemplateNoComparator;
import org.opencps.dossiermgt.model.Dossier;
import org.opencps.dossiermgt.model.DossierFile;
import org.opencps.dossiermgt.model.DossierFileLog;
import org.opencps.dossiermgt.model.DossierPart;
import org.opencps.dossiermgt.model.ServiceConfig;
import org.opencps.dossiermgt.search.DossierDisplayTerms;
import org.opencps.dossiermgt.search.DossierTemplateDisplayTerms;
import org.opencps.dossiermgt.service.DossierFileLocalServiceUtil;
import org.opencps.dossiermgt.service.DossierFileLogLocalServiceUtil;
import org.opencps.dossiermgt.service.DossierLocalServiceUtil;
import org.opencps.dossiermgt.service.DossierPartLocalServiceUtil;
import org.opencps.dossiermgt.service.ServiceConfigLocalServiceUtil;
import org.opencps.processmgt.model.ProcessOrder;
import org.opencps.processmgt.model.impl.ProcessOrderImpl;
import org.opencps.processmgt.service.ProcessOrderLocalServiceUtil;
import org.opencps.servicemgt.model.ServiceInfo;
import org.opencps.servicemgt.model.TemplateFile;
import org.opencps.servicemgt.service.ServiceInfoLocalServiceUtil;
import org.opencps.servicemgt.service.TemplateFileLocalServiceUtil;
import org.opencps.util.PortletConstants;
import org.opencps.util.WebKeys;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;

/**
 * @author trungnt
 */
public class DossierMgtUtil {

	public static final String TOP_TABS_DOSSIER_TEMPLATE =
		"top_tabs_dossier_template";
	public static final String TOP_TABS_DOSSIER_PART = "top_tabs_dossier_part";
	public static final String TOP_TABS_SERVICE_CONFIG =
		"top_tabs_service_config";
	public static final String DOSSIER_PART_TOOLBAR = "dossierPartToolBar";
	public static final String SERVICE_CONFIG_TOOLBAR = "serviceConfigToolBar";

	public static final String TOP_TABS_DOSSIER = "dossier";
	public static final String TOP_TABS_DOSSIER_FILE = "dossier-file";
	public static final String TOP_TABS_EXTERNAL_DOSSIER = "external-dossier";

	public static final int DOSSIERFILETYPE_INPUT = 1;
	public static final int DOSSIERFILETYPE_OUTPUT = 2;
	public static final int DOSSIERFILETYPE_ALL = 0;

	public static final int DOSSIERFILEMARK_BAN_CHINH = 1;
	public static final int DOSSIERFILEMARK_BAN_CONG_CHUNG = 2;
	public static final int DOSSIERFILEMARK_BAN_CHUP = 3;

	public static final String TOP_TABS_DOSSIER_MONITORING_SEARCH =
		"dossier-monitoring-search";
	public static final String TOP_TABS_DOSSIER_MONITORING_DOSSIER_FILE_LIST =
		"dossier-monitoring-dossier-file-list";
	public static final String TOP_TABS_DOSSIER_MONITORING_SERVICE =
		"dossier-monitoring-service";

	public static String[] _DOSSIER_CATEGORY_NAMES = {
		"update-dossier-info"
	};

	/**
	 * @param orderByCol
	 * @param orderByType
	 * @return
	 */
	public static OrderByComparator getDossierTemplateOrderByComparator(
		String orderByCol, String orderByType) {

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		OrderByComparator orderByComparator = null;

		if (orderByCol.equals(DossierTemplateDisplayTerms.DOSSIERTEMPLATE_TEMPLATENO)) {
			orderByComparator = new DossierTemplateNoComparator(orderByAsc);
		}
		else if (orderByCol.equals(DossierTemplateDisplayTerms.DOSSIERTEMPLATE_TEMPLATENAME)) {
			orderByComparator = new DossierTemplateNameComparator(orderByAsc);
		}

		return orderByComparator;
	}

	/**
	 * @param orderByCol
	 * @param orderByType
	 * @return
	 */
	public static OrderByComparator getDossierOrderByComparator(
		String orderByCol, String orderByType) {

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		OrderByComparator orderByComparator = null;

		if (orderByCol.equals(DossierDisplayTerms.SUBMIT_DATETIME)) {
			orderByComparator = new DossierSubmitDateComparator(orderByAsc);
		}

		return orderByComparator;
	}

	/**
	 * @param orderByType
	 * @param dossierFiles
	 * @return
	 */
	public static List<DossierFile> orderDossierFileByDossierFileDate(
		String orderByType, List<DossierFile> dossierFiles) {

		int value = 0;
		DossierFile dossierFileTemp = null;

		if (orderByType.equals(WebKeys.ORDER_BY_ASC)) {
			for (int i = 0; i < dossierFiles.size() - 1; i++) {
				for (int j = i + 1; j < dossierFiles.size(); j++) {
					value =
						dossierFiles.get(i).getDossierFileDate().compareTo(
							dossierFiles.get(j).getDossierFileDate());
					if (value >= 0) {
						dossierFileTemp = dossierFiles.get(i);
						dossierFiles.set(i, dossierFiles.get(j));
						dossierFiles.set(j, dossierFileTemp);
					}
				}
			}
			return dossierFiles;
		}
		else if (orderByType.equals(WebKeys.ORDER_BY_DESC)) {
			for (int i = 0; i < dossierFiles.size() - 1; i++) {
				for (int j = i + 1; j < dossierFiles.size(); j++) {
					value =
						dossierFiles.get(i).getDossierFileDate().compareTo(
							dossierFiles.get(j).getDossierFileDate());
					if (value < 0) {
						dossierFileTemp = dossierFiles.get(i);
						dossierFiles.set(i, dossierFiles.get(j));
						dossierFiles.set(j, dossierFileTemp);
					}
				}
			}
			return dossierFiles;
		}

		return dossierFiles;
	}

	/**
	 * @param partType
	 * @param locale
	 * @return
	 */
	public static String getNameOfPartType(int partType, Locale locale) {

		String partTypeName = StringPool.BLANK;
		switch (partType) {
		case 1:
			partTypeName = LanguageUtil.get(locale, "paper-submited");
			break;
		case 2:
			partTypeName = LanguageUtil.get(locale, "other-papers-group");
			break;
		case 3:
			partTypeName = LanguageUtil.get(locale, "groups-optional");
			break;
		case 4:
			partTypeName = LanguageUtil.get(locale, "own-records");
			break;
		case 5:
			partTypeName = LanguageUtil.get(locale, "papers-results");
			break;
		case 6:
			partTypeName = LanguageUtil.get(locale, "multy-papers-results");
			break;
		default:
			partTypeName = LanguageUtil.get(locale, StringPool.BLANK);
			break;
		}

		return partTypeName;
	}

	public static String getSynchStatus(int synchStatus, Locale locale) {

		String synchStatusName = StringPool.BLANK;
		switch (synchStatus) {
		case 0:
			synchStatusName = LanguageUtil.get(locale, "no-need-to-synch");
			break;
		case 1:
			synchStatusName = LanguageUtil.get(locale, "need-to-synch");
			break;
		case 2:
			synchStatusName = LanguageUtil.get(locale, "synch-success");
			break;
		case 3:
			synchStatusName = LanguageUtil.get(locale, "synch-error");
			break;
		default:
			synchStatusName = LanguageUtil.get(locale, StringPool.BLANK);
			break;
		}

		return synchStatusName;
	}

	/**
	 * @param mode
	 * @param locale
	 * @return
	 */
	public static String getNameOfServiceConfigMode(int mode, Locale locale) {

		String modeName = StringPool.BLANK;
		switch (mode) {
		case 0:
			modeName = LanguageUtil.get(locale, "inactive");
			break;
		case 1:
			modeName = LanguageUtil.get(locale, "front-office");
			break;
		case 2:
			modeName = LanguageUtil.get(locale, "back-office");
			break;
		case 3:
			modeName = LanguageUtil.get(locale, "front-back-office");
			break;
		default:
			modeName = LanguageUtil.get(locale, StringPool.BLANK);
			break;
		}

		return modeName;
	}

	/**
	 * @param dossierpartId
	 * @return
	 */
	@Deprecated
	public static List<DossierPart> getTreeDossierPart(long dossierpartId) {

		List<DossierPart> dossierPartsResult = new ArrayList<DossierPart>();

		Stack<DossierPart> dossierPartsStack = new Stack<DossierPart>();

		try {
			DossierPart dossierPart =
				DossierPartLocalServiceUtil.getDossierPart(dossierpartId);

			dossierPartsStack.add(dossierPart);

			DossierPart dossierPartIndex = null;

			while (!dossierPartsStack.isEmpty()) {
				dossierPartIndex = dossierPartsStack.pop();

				List<DossierPart> dossierPartsChild =
					new ArrayList<DossierPart>();
				dossierPartsChild =
					DossierPartLocalServiceUtil.getDossierPartsByParentId(dossierPartIndex.getDossierpartId());

				if (!dossierPartsChild.isEmpty()) {

					for (int i = dossierPartsChild.size() - 1; i >= 0; i--) {
						dossierPartsStack.add(dossierPartsChild.get(i));
					}

				}

				dossierPartsResult.add(dossierPartIndex);
			}
			return dossierPartsResult;
		}
		catch (Exception e) {
			_log.error(e);
		}

		return new ArrayList<DossierPart>();
	}

	public static String getLoaiGiayToLabel(int value, Locale locale) {

		String statusLabel = StringPool.BLANK;

		switch (value) {
		case DOSSIERFILEMARK_BAN_CHINH:
			statusLabel = LanguageUtil.get(locale, "loai-giay-to-ban-chinh");
			break;
		case DOSSIERFILEMARK_BAN_CONG_CHUNG:
			statusLabel =
				LanguageUtil.get(locale, "loai-giay-to-ban-cong-chung");
			break;
		case DOSSIERFILEMARK_BAN_CHUP:
			statusLabel = LanguageUtil.get(locale, "loai-giay-to-ban-chup");
			break;
		default:
			statusLabel = StringPool.BLANK;
			break;
		}

		return statusLabel;
	}

	public static String getURLDownloadTemplateFile(
		ThemeDisplay themeDisplay, String templateFileNo) {

		String result = StringPool.BLANK;

		DLFileEntry dlFileEntry = null;

		TemplateFile templateFile = null;

		try {

			templateFile =
				TemplateFileLocalServiceUtil.getTemplateFileByNo(
					themeDisplay.getScopeGroupId(), templateFileNo);

			if (Validator.isNotNull(templateFile)) {

				dlFileEntry =
					DLFileEntryLocalServiceUtil.getDLFileEntry(templateFile.getFileEntryId());

				result =
					themeDisplay.getPortalURL() +
						"/c/document_library/get_file?uuid=" +
						dlFileEntry.getUuid() + "&groupId=" +
						themeDisplay.getScopeGroupId();

			}

		}
		catch (Exception e) {

			_log.error(e);

		}

		return result;
	}

	/**
	 * @param requestCommand
	 * @param messageInfo
	 * @return
	 */
	public static String getDossierLogs(
		String requestCommand, String messageInfo) {

		String dossierLog = StringPool.BLANK;

		if (Validator.isNotNull(messageInfo)) {
			if (Validator.equals(
				requestCommand, PortletConstants.REQUEST_COMMAND_PAYMENT)) {

				String[] arrMsgInfo =
					StringUtil.split(messageInfo, StringPool.SEMICOLON);

				if (arrMsgInfo.length != 0) {
					dossierLog = arrMsgInfo[0];
				}

			}
			else {
				dossierLog = messageInfo;
			}

		}

		return dossierLog;
	}

	/**
	 * Get service name by dossierId
	 * 
	 * @param dossierId
	 *            maso ho so
	 * @return (String) serviceName
	 */
	public static String getServiceName(long dossierId) {

		String serviceName = StringPool.BLANK;

		if (Validator.isNotNull(dossierId) && dossierId != 0) {
			try {
				Dossier dossier =
					DossierLocalServiceUtil.fetchDossier(dossierId);

				ServiceConfig serviceConfig =
					ServiceConfigLocalServiceUtil.fetchServiceConfig(dossier.getServiceConfigId());

				ServiceInfo serviceInfo =
					ServiceInfoLocalServiceUtil.fetchServiceInfo(serviceConfig.getServiceInfoId());

				serviceName = serviceInfo.getServiceName();
			}
			catch (Exception e) {
				_log.error("dossierId is not validator");
			}
		}

		return serviceName;
	}

	public static List<DossierFileLog> getFileLogs(
		long dossierLogId, long dossierId) {

		List<DossierFileLog> ls = new ArrayList<DossierFileLog>();

		try {
			ls =
				DossierFileLogLocalServiceUtil.getFileLogs(
					dossierLogId, dossierId);
		}
		catch (Exception e) {
			// TODO: handle exception
		}

		return ls;
	}

	public static ProcessOrder getProcessOrder(long dossierId) {

		ProcessOrder processOrder = new ProcessOrderImpl();

		try {
			processOrder =
				ProcessOrderLocalServiceUtil.getProcessOrder(dossierId, 0);
		}
		catch (Exception e) {
			// TODO: handle exception
		}

		return processOrder;

	}

	public static JSONObject getSignImageAsBase64(long signImageId) {

		JSONObject signImageInfo = JSONFactoryUtil.createJSONObject();
		String signImageAsBase64 = StringPool.BLANK;
		String signImageName = StringPool.BLANK;
		try {
			if (signImageId > 0) {
				DLFileEntry dlFileEntry =
					DLFileEntryLocalServiceUtil.getDLFileEntry(signImageId);

				InputStream is = dlFileEntry.getContentStream();
				signImageName = dlFileEntry.getTitle();
				byte[] bytes = IOUtils.toByteArray(is);
				signImageAsBase64 = Base64.encode(bytes);

				signImageInfo.put("signImageAsBase64", signImageAsBase64);
				signImageInfo.put("signImageName", signImageName);
			}
		}
		catch (Exception e) {
			_log.error(e);
		}

		return signImageInfo;
	}

	public static String base64File(long dossierFileId) {

		String base64FileContent = StringPool.BLANK;
		DossierFile dossierFile = null;
		try {
			if (dossierFileId > 0) {
				dossierFile =
					DossierFileLocalServiceUtil.getDossierFile(dossierFileId);
				DLFileEntry fileEntry =
					DLFileEntryLocalServiceUtil.getDLFileEntry(dossierFile.getFileEntryId());

				InputStream is = fileEntry.getContentStream();

				byte[] bytes = IOUtils.toByteArray(is);

				base64FileContent = Base64.encode(bytes);

				// _log.info("base64FileContent : " + base64FileContent);

			}
		}
		catch (Exception e) {
			_log.error(e);
		}

		return base64FileContent;
	}

	public static String getFileName(long dossierFileId) {

		String fileName = StringPool.BLANK;
		DossierFile dossierFile = null;
		try {
			if (dossierFileId > 0) {
				dossierFile =
					DossierFileLocalServiceUtil.getDossierFile(dossierFileId);
				DLFileEntry fileEntry =
					DLFileEntryLocalServiceUtil.getDLFileEntry(dossierFile.getFileEntryId());
				fileName = fileEntry.getTitle();
			}
		}
		catch (Exception e) {
			_log.error(e);
		}

		return fileName;
	}

	@Deprecated
	public static int getLevelDossierPart(long dossierPartId)
		throws PortalException, SystemException {

		int level = 1;
		long dossierPartParentId = 0;
		DossierPart dossierPart =
			DossierPartLocalServiceUtil.getDossierPart(dossierPartId);
		dossierPartParentId = dossierPart.getParentId();
		if (dossierPartParentId > 0) {
			DossierPart dossierPartParent =
				DossierPartLocalServiceUtil.getDossierPart(dossierPartParentId);
			getLevelDossierPart(dossierPartParent.getDossierpartId());
			level++;
		}

		return level;
	}

	public static double getMaxSibLingDossierPartInDepth(
		long dossierTemplateId, long dossierPartParentId)
		throws SystemException {

		List<DossierPart> dossierParts =
			DossierPartLocalServiceUtil.getDossierPartsByT_P(
				dossierTemplateId, dossierPartParentId);
		double maxSibling = 0;
		for (DossierPart dossierPart : dossierParts) {
			if (dossierPart.getSibling() > maxSibling) {
				maxSibling = dossierPart.getSibling();
			}
		}

		return maxSibling;

	}

	/**
	 * @param dossierPartRoots
	 * @param temp
	 * @return
	 * @throws SystemException
	 */
	public static List<DossierPart> getDossierPartsTreeIndexShort(
		List<DossierPart> dossierPartRoots, List<DossierPart> temp)
		throws SystemException {

		for (DossierPart dossierPart : dossierPartRoots) {
			List<DossierPart> dossierPartChirlds =
				DossierPartLocalServiceUtil.getDossierPartsByT_P_Order(
					dossierPart.getDossierTemplateId(),
					dossierPart.getDossierpartId());
			temp.add(dossierPart);
			getDossierPartsTreeIndexShort(dossierPartChirlds, temp);
		}

		return temp;
	}

	public static List<DossierPart> getTreeDossierPart(
		long dossierPartId, List<DossierPart> temp)
		throws SystemException {

		DossierPart dossierPartRoot =
			DossierPartLocalServiceUtil.fetchDossierPart(dossierPartId);
		if (temp.size() == 0) {
			temp.add(dossierPartRoot);
		}
		if (Validator.isNotNull(dossierPartRoot)) {

			List<DossierPart> dossierPartChirlds =
				DossierPartLocalServiceUtil.getDossierPartsByT_P_Order(
					dossierPartRoot.getDossierTemplateId(),
					dossierPartRoot.getDossierpartId());

			for (DossierPart dossierPart : dossierPartChirlds) {
				temp.add(dossierPart);
				getTreeDossierPart(dossierPart.getDossierpartId(), temp);
			}
		}
		return temp;
	}

	/**
	 * @param receptionNo
	 * @return
	 */
	public static Dossier getDossierByReceptionNo(String receptionNo) {

		Dossier dossier = null;

		if (Validator.isNotNull(receptionNo)) {
			try {
				dossier =
					DossierLocalServiceUtil.getDossierByReceptionNo(receptionNo);
			}
			catch (Exception e) {
				_log.debug(e);
			}
		}

		return dossier;
	}

	/**
	 * @param dossierId
	 * @return
	 */
	public static Dossier getDossierByDossierId(long dossierId) {

		Dossier dossier = null;

		if (dossierId != 0) {
			try {
				dossier = DossierLocalServiceUtil.getDossier(dossierId);
			}
			catch (Exception e) {
				_log.debug(e);
			}
		}

		return dossier;
	}

	public static Dossier searchDossier(String keywords) {

		Dossier dossier = null;

		dossier = getDossierByReceptionNo(keywords);

		if (Validator.isNull(dossier)) {

			long dossierId = convertStringToLong(keywords);

			dossier = getDossierByDossierId(dossierId);

		}

		return dossier;
	}

	private static long convertStringToLong(String input) {

		long output = 0;

		try {
			output = Long.valueOf(input);
		}
		catch (Exception e) {

		}

		return output;
	}

	private static Log _log =
		LogFactoryUtil.getLog(DossierMgtUtil.class.getName());

}
