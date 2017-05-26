/**
 * OpenCPS is the open source Core Public Services software
 * Copyright (C) 2016-present OpenCPS community
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>
 */

package org.opencps.processmgt.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.portlet.PortletModeException;
import javax.portlet.RenderRequest;
import javax.portlet.WindowStateException;

import org.opencps.datamgt.model.DictItem;
import org.opencps.datamgt.service.DictItemLocalServiceUtil;
import org.opencps.dossiermgt.bean.ProcessOrderBean;
import org.opencps.dossiermgt.model.Dossier;
import org.opencps.dossiermgt.model.impl.DossierImpl;
import org.opencps.dossiermgt.service.DossierLocalServiceUtil;
import org.opencps.processmgt.model.ProcessOrder;
import org.opencps.processmgt.model.ProcessStep;
import org.opencps.processmgt.model.StepAllowance;
import org.opencps.processmgt.search.ProcessOrderDisplayTerms;
import org.opencps.processmgt.service.ProcessStepLocalServiceUtil;
import org.opencps.processmgt.service.StepAllowanceLocalServiceUtil;
import org.opencps.processmgt.util.comparator.ProcessOrderModifiedDateComparator;
import org.opencps.util.PortletUtil;
//import org.opencps.processmgt.util.comparator.BuocXuLyComparator;
//import org.opencps.processmgt.util.comparator.ChuHoSoComparator;
//import org.opencps.processmgt.util.comparator.MaTiepNhanComparator;
//import org.opencps.processmgt.util.comparator.NguoiPhuTrachComparator;
//import org.opencps.processmgt.util.comparator.ThuTucComparator;
import org.opencps.util.WebKeys;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;

/**
 * @author binhth
 */
/**
 * @author khoavd
 */
public class ProcessOrderUtils {

	public static final String TOP_TABS_PROCESSORDER_TODO = "top_tabs_processorder_todo";
	public static final String TOP_TABS_PROCESSORDER_DONE = "top_tabs_processorder_done";

	public static OrderByComparator getProcessOrderByComparator(
			String orderByCol, String orderByType) {

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		OrderByComparator orderByComparator = null;

		if (orderByCol.equals(ProcessOrderDisplayTerms.MODIFIEDDATE)) {
			orderByComparator = new ProcessOrderModifiedDateComparator(
					orderByAsc);
		}

		// if(orderByCol.equals(ProcessOrderDisplayTerms.MA_TIEP_NHAN)) {
		// orderByComparator = new MaTiepNhanComparator(orderByAsc);
		// } else if(orderByCol.equals(ProcessOrderDisplayTerms.CHU_HO_SO)) {
		// orderByComparator = new ChuHoSoComparator(orderByAsc);
		// } else if(orderByCol.equals(ProcessOrderDisplayTerms.THU_TUC)) {
		// orderByComparator = new ThuTucComparator(orderByAsc);
		// } else if(orderByCol.equals(ProcessOrderDisplayTerms.BUOC_XU_LY)) {
		// orderByComparator = new BuocXuLyComparator(orderByAsc);
		// } else
		// if(orderByCol.equals(ProcessOrderDisplayTerms.NGUOI_PHU_TRACH)) {
		// orderByComparator = new NguoiPhuTrachComparator(orderByAsc);
		// }

		return orderByComparator;
	}

	// custom display process order
	public static class CustomDisPlay {

		private long id;
		private String maTiepNhan;
		private String chuHoSo;
		private String thuTuc;
		private String buocXuLy;
		private String nguoiPhuTrach;
		private String hanXuLy;
		private String thaoTac;
		private String ngayThucHien;
		private String trangThaiHoSo;

		public CustomDisPlay() {

		}

		public CustomDisPlay(long id, String maTiepNhan, String chuHoSo,
				String thuTuc, String buocXuLy, String nguoiPhuTrach,
				String hanXuLy, String thaoTac, String ngayThucHien,
				String trangThaiHoSo) {

			super();
			this.id = id;
			this.maTiepNhan = maTiepNhan;
			this.chuHoSo = chuHoSo;
			this.thuTuc = thuTuc;
			this.buocXuLy = buocXuLy;
			this.nguoiPhuTrach = nguoiPhuTrach;
			this.hanXuLy = hanXuLy;
			this.thaoTac = thaoTac;
			this.ngayThucHien = ngayThucHien;
			this.trangThaiHoSo = trangThaiHoSo;
		}

		public String getMaTiepNhan() {

			return maTiepNhan;
		}

		public void setMaTiepNhan(String maTiepNhan) {

			this.maTiepNhan = maTiepNhan;
		}

		public String getChuHoSo() {

			return chuHoSo;
		}

		public void setChuHoSo(String chuHoSo) {

			this.chuHoSo = chuHoSo;
		}

		public String getThuTuc() {

			return thuTuc;
		}

		public void setThuTuc(String thuTuc) {

			this.thuTuc = thuTuc;
		}

		public String getBuocXuLy() {

			return buocXuLy;
		}

		public void setBuocXuLy(String buocXuLy) {

			this.buocXuLy = buocXuLy;
		}

		public String getNguoiPhuTrach() {

			return nguoiPhuTrach;
		}

		public void setNguoiPhuTrach(String nguoiPhuTrach) {

			this.nguoiPhuTrach = nguoiPhuTrach;
		}

		public String getHanXuLy() {

			return hanXuLy;
		}

		public void setHanXuLy(String hanXuLy) {

			this.hanXuLy = hanXuLy;
		}

		public String getThaoTac() {

			return thaoTac;
		}

		public void setThaoTac(String thaoTac) {

			this.thaoTac = thaoTac;
		}

		public String getNgayThucHien() {

			return ngayThucHien;
		}

		public void setNgayThucHien(String ngayThucHien) {

			this.ngayThucHien = ngayThucHien;
		}

		public String getTrangThaiHoSo() {

			return trangThaiHoSo;
		}

		public void setTrangThaiHoSo(String trangThaiHoSo) {

			this.trangThaiHoSo = trangThaiHoSo;
		}

		public long getId() {

			return id;
		}

		public void setId(long id) {

			this.id = id;
		}

	}

	/**
	 * @param roleIds
	 * @return
	 */
	public static List<ProcessStep> getProcessSteps(long groupId, long[] roleIds) {

		List<ProcessStep> results = null;

		try {
			List<StepAllowance> listStepAllowance = StepAllowanceLocalServiceUtil
					.findByRoleIds(roleIds);

			long[] processStepIds = new long[listStepAllowance.size()];

			int index = 0;

			for (StepAllowance stepAllowance : listStepAllowance) {

				processStepIds[index++] = stepAllowance.getStepAllowanceId();

			}

			results = ProcessStepLocalServiceUtil.findByProcessStepIds(groupId,
					processStepIds);

		}

		catch (Exception e) {
			return new ArrayList<ProcessStep>();
		}
		return results;
	}

	/**
	 * @param roleIds
	 * @return
	 */
	/**
	 * @param roleIds
	 * @return
	 * @throws WindowStateException
	 * @throws PortletModeException
	 */
	public static String generateMenuBuocXuLy(RenderRequest renderRequest,
			long[] roleIds, String active, boolean counter, String renderURL)
			throws WindowStateException, PortletModeException {

		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest
				.getAttribute(WebKeys.THEME_DISPLAY);

		long groupId = themeDisplay.getScopeGroupId();

		// now read your parameters, e.g. like this:
		// long someParameter = ParamUtil.getLong(request, "someParameter");

		StringBuilder sbHtml = new StringBuilder();

		sbHtml.append("<ul class=\"menu-opencps\">");

		for (ProcessStep ett : getProcessSteps(groupId, roleIds)) {
			String mnClass = (Validator.isNotNull(active)
					&& active.equalsIgnoreCase(String.valueOf(ett
							.getProcessStepId())) ? "active-menu" : "");

			sbHtml.append("<li class=\"menu-opencps-li " + mnClass
					+ "\" onclick=\"openCPS_menu_submit('" + renderURL + "','"
					+ ett.getProcessStepId() + "')\" >");

			sbHtml.append("<a>");

			if (counter)
				sbHtml.append("<span id=\"" + "badge_" + ett.getProcessStepId()
						+ "\" class=\"badge\">0</span>");

			sbHtml.append(HtmlUtil.escape(ett.getStepName()));

			sbHtml.append("</a>");

			sbHtml.append("</li>");

		}

		sbHtml.append("</ul>");

		return sbHtml.toString();
	}

	public static List<CustomDisPlay> searchProcessOrder(List list) {

		List<CustomDisPlay> customDisPlays = new ArrayList<ProcessOrderUtils.CustomDisPlay>();

		Iterator<Object[]> itr = list.iterator();
		while (itr.hasNext()) {

			Object[] object = (Object[]) itr.next();
			ProcessOrder processOrder = (ProcessOrder) object[0];
			String tenThuTuc = (String) object[1];
			Dossier dossier = (Dossier) object[2];
			String stepName = (String) object[3];
			String actionName = (String) object[4];
			long id = processOrder.getProcessOrderId();
			String maTiepNhan = StringPool.BLANK + dossier.getReceptionNo();
			String chuHoSo = StringPool.BLANK + dossier.getSubjectName();
			String thuTuc = StringPool.BLANK + tenThuTuc;
			String buocXuLy = StringPool.BLANK + stepName;
			String nguoiPhuTrach = StringPool.BLANK;
			try {
				nguoiPhuTrach = UserLocalServiceUtil.fetchUser(
						processOrder.getAssignToUserId()).getFullName();
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			long diff = 0;
			if (Validator.isNotNull(dossier.getFinishDatetime())) {
				diff = dossier.getFinishDatetime().getTime()
						- new Date().getTime();
			}

			String hanXuLy = StringPool.BLANK
					+ TimeUnit.MILLISECONDS.toDays(diff);
			String thaoTac = StringPool.BLANK + actionName;
			String ngayThucHien = StringPool.BLANK
					+ processOrder.getActionDatetime();
			String trangThaiHoSo = StringPool.BLANK
					+ dossier.getDossierStatus();
			CustomDisPlay customDisPlay = new CustomDisPlay(id, maTiepNhan,
					chuHoSo, thuTuc, buocXuLy, nguoiPhuTrach, hanXuLy, thaoTac,
					ngayThucHien, trangThaiHoSo);
			customDisPlays.add(customDisPlay);
		}

		return customDisPlays;

	}

	public static String generateTreeView(String collectionCode,
			String itemCode, String myLabel, int level, String type,
			boolean isCode, RenderRequest renderRequest, 
			String[] showedCodesArray)
			throws SystemException, PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest
				.getAttribute(WebKeys.THEME_DISPLAY);

		long groupId = themeDisplay.getScopeGroupId();

		// get chirentDataSource
		List<DictItem> result = PortletUtil.getDictItemInUseByCode(groupId,
				collectionCode, itemCode);

		JSONArray jsonArrayRoot = JSONFactoryUtil.createJSONArray();
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		JSONObject jsonObjectRoot = JSONFactoryUtil.createJSONObject();
		JSONObject jsonObject = null;

		int countPeriod = 0;

		for (DictItem dictItem : result) {

			boolean isShowedCode = false;
			if(showedCodesArray.length == 0){
				isShowedCode = true;
			} else {
				for (String showedCode : showedCodesArray) {
					if(Validator.equals(showedCode, dictItem.getItemCode())){
						isShowedCode = true;
						break;
					}
				}
			}
			
			if (isShowedCode){
				
				jsonObject = JSONFactoryUtil.createJSONObject();
				String[] treeIn = dictItem.getTreeIndex().split(
						StringPool.BACK_SLASH + StringPool.PERIOD);
				
				countPeriod = StringUtil.count(dictItem.getTreeIndex(),
						StringPool.PERIOD);
				
				if (countPeriod <= level) {
					
					jsonObject.put("label",
							dictItem.getItemName(themeDisplay.getLocale()));
					
					jsonObject.put("type", type);
					
					if (isCode) {
						jsonObject.put("id", dictItem.getItemCode());
					} else {
						jsonObject.put("id",
								StringUtil.valueOf(dictItem.getDictItemId()));
					}
					
					jsonObject.put("expanded", true);
					
					if (countPeriod < level) {
						
						jsonObject.put("leaf", false);
						
					} else {
						jsonObject.put("leaf", true);
					}
					
					jsonObject.put("children", JSONFactoryUtil.createJSONArray());
					
					if (countPeriod > 0) {
						
						jsonObject.put("parentId",
								StringUtil.valueOf(treeIn[countPeriod - 1]));
						
						for (int y = 0; y < jsonArray.length(); y++) {
							
							buildChildJsonTreeData(jsonObject, 0,
									jsonArray.getJSONObject(y));
							
						}
					} else {
						
						jsonArray.put(jsonObject);
						
					}
					
				}
			}
		}

		jsonObjectRoot.put("children", jsonArray);

		jsonObjectRoot.put("expanded", true);

		jsonObjectRoot.put("label", myLabel);

		jsonArrayRoot.put(jsonObjectRoot);

		return jsonArrayRoot.toString();
	}

	public static String generateTreeViewMappingAdminCode(
			String collectionCode, String itemCode, String myLabel, int level,
			String type, boolean isCode, RenderRequest renderRequest)
			throws SystemException, PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest
				.getAttribute(WebKeys.THEME_DISPLAY);

		long groupId = themeDisplay.getScopeGroupId();

		// get chirentDataSource
		List<DictItem> result = PortletUtil
				.getDictItemInUseByCodeMappingAdminCode(groupId,
						collectionCode, itemCode);

		JSONArray jsonArrayRoot = JSONFactoryUtil.createJSONArray();
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		JSONObject jsonObjectRoot = JSONFactoryUtil.createJSONObject();
		JSONObject jsonObject = null;

		int countPeriod = 0;

		for (DictItem dictItem : result) {

			jsonObject = JSONFactoryUtil.createJSONObject();
			String[] treeIn = dictItem.getTreeIndex().split(
					StringPool.BACK_SLASH + StringPool.PERIOD);

			countPeriod = StringUtil.count(dictItem.getTreeIndex(),
					StringPool.PERIOD);

			if (countPeriod <= level) {

				jsonObject.put("label",
						dictItem.getItemName(themeDisplay.getLocale()));

				jsonObject.put("type", type);

				if (isCode) {
					jsonObject.put("id", dictItem.getItemCode());
				} else {
					jsonObject.put("id",
							StringUtil.valueOf(dictItem.getDictItemId()));
				}

				jsonObject.put("expanded", true);

				if (countPeriod < level) {

					jsonObject.put("leaf", false);

				} else {
					jsonObject.put("leaf", true);
				}

				jsonObject.put("children", JSONFactoryUtil.createJSONArray());

				if (countPeriod > 0) {

					jsonObject.put("parentId",
							StringUtil.valueOf(treeIn[countPeriod - 1]));

					for (int y = 0; y < jsonArray.length(); y++) {

						buildChildJsonTreeData(jsonObject, 0,
								jsonArray.getJSONObject(y));

					}
				} else {

					jsonArray.put(jsonObject);

				}

			}
		}

		jsonObjectRoot.put("children", jsonArray);

		jsonObjectRoot.put("expanded", true);

		jsonObjectRoot.put("label", myLabel);

		jsonArrayRoot.put(jsonObjectRoot);

		return jsonArrayRoot.toString();
	}

	public static void buildChildJsonTreeData(JSONObject newJsonObject, int i,
			JSONObject compareJsonObject) {

		JSONObject childObj = compareJsonObject;

		if (Validator.isNotNull(compareJsonObject.getJSONArray("children")
				.getJSONObject(i)) && i > 0) {
			childObj = compareJsonObject.getJSONArray("children")
					.getJSONObject(i);
		}
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		if (childObj != null) {

			jsonArray = childObj.getJSONArray("children");

			if (newJsonObject.getString("parentId").equals(
					childObj.getString("id"))) {

				jsonArray.put(newJsonObject);

				childObj.put("children", jsonArray);
			}

			i++;

			JSONObject objChk = compareJsonObject.getJSONArray("children")
					.getJSONObject(i);

			if (Validator.isNotNull(objChk)) {

				buildChildJsonTreeData(newJsonObject, i, compareJsonObject);
			}

		}
	}

	public static String generateTreeView(List<ProcessOrderBean> dataSource,
			String myLabel, String type) throws SystemException,
			PortalException {

		JSONArray jsonArrayRoot = JSONFactoryUtil.createJSONArray();
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		JSONObject jsonObjectRoot = JSONFactoryUtil.createJSONObject();
		JSONObject jsonObject = null;

		for (ProcessOrderBean item : dataSource) {
			jsonObject = JSONFactoryUtil.createJSONObject();

			if (item.getServiceInfoId() > 0) {

				jsonObject.put("id", String.valueOf(item.getServiceInfoId()));

				jsonObject.put("label", item.getServiceName());

			} else if (item.getProcessStepId() > 0) {

				jsonObject.put("id", String.valueOf(item.getProcessStepId()));

				jsonObject.put("label", item.getStepName());

			}

			jsonObject.put("type", type);

			jsonObject.put("leaf", true);

			jsonArray.put(jsonObject);
		}

		jsonObjectRoot.put("children", jsonArray);

		jsonObjectRoot.put("expanded", true);

		jsonObjectRoot.put("label", myLabel);

		jsonArrayRoot.put(jsonObjectRoot);

		return jsonArrayRoot.toString();
	}

	/**
	 * Get Date of Dossier
	 * 
	 * @param dossierId
	 * @param typeDate
	 *            1: receiveDate, 2: estimateDate, 3: finishedDate, 4:
	 *            modifiedDate
	 * @param patternDate
	 *            Pattern of date output
	 * @return
	 */
	public static String getDossierDate(long dossierId, int typeDate,
			String patternDate) {

		String dossierDate = StringPool.BLANK;

		Dossier dossier = new DossierImpl();

		SimpleDateFormat df = new SimpleDateFormat(patternDate);

		try {
			dossier = DossierLocalServiceUtil.getDossier(dossierId);

			switch (typeDate) {
			case 1: {
				if (Validator.isNotNull(dossier.getReceiveDatetime())) {
					dossierDate = df.format(dossier.getReceiveDatetime());
				}
				break;
			}
			case 2: {
				if (Validator.isNotNull(dossier.getEstimateDatetime())) {
					dossierDate = df.format(dossier.getEstimateDatetime());
				}

				break;

			}
			case 3: {
				if (Validator.isNotNull(dossier.getFinishDatetime())) {
					dossierDate = df.format(dossier.getFinishDatetime());
				}

				break;

			}
			case 4: {
				if (Validator.isNotNull(dossier.getModifiedDate())) {
					dossierDate = df.format(dossier.getModifiedDate());
				}

				break;
			}
			default:
				break;
			}
		} catch (Exception e) {
			_log.error(e);
		}

		return dossierDate;
	}

	/**
	 * @param dossierId
	 * @param processWorkflowId
	 * @param processStepId
	 * @return
	 */
	public static Date getRecevieDate(long dossierId, long processWorkflowId,
			long processStepId) {

		Date recevieDate = null;

		try {

			Dossier dossier = DossierLocalServiceUtil.fetchDossier(dossierId);

			if (Validator.isNotNull(dossier.getReceiveDatetime())) {
				recevieDate = dossier.getReceiveDatetime();
			} else {
				// ProcessWorkflow processWorkflow =
				// ProcessWorkflowLocalServiceUtil.fetchProcessWorkflow(processWorkflowId);
				//
				// ProcessStep currStep =
				// ProcessStepLocalServiceUtil.fetchProcessStep(processWorkflow.getPreProcessStepId());
				//
				// ProcessStep nextStep =
				// ProcessStepLocalServiceUtil.fetchProcessStep(processWorkflow.getPostProcessStepId());
				//
				// if (currStep.getDossierStatus().contains(
				// PortletConstants.DOSSIER_STATUS_RECEIVING) &&
				// nextStep.getDossierStatus().contains(
				// PortletConstants.DOSSIER_STATUS_PROCESSING)) {
				// recevieDate = new Date();
				// }
				recevieDate = new Date();

			}

		} catch (Exception e) {
			_log.error(e);
		}
		return recevieDate;

	}

	public static String generateComboboxTree(String collectionCode,
			String itemCode, int level, boolean isCode,
			RenderRequest renderRequest) throws SystemException,
			PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest
				.getAttribute(WebKeys.THEME_DISPLAY);

		long groupId = themeDisplay.getScopeGroupId();

		// get chirentDataSource
		List<DictItem> result = PortletUtil.getDictItemInUseByCode(groupId,
				collectionCode, itemCode);

		JSONArray jsonArrayRoot = JSONFactoryUtil.createJSONArray();
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		JSONObject jsonObject = null;
		jsonObject = JSONFactoryUtil.createJSONObject();
		jsonObject.put("id", StringPool.BLANK);
		jsonObject.put("text", LanguageUtil.get(themeDisplay.getLocale(),
				"domainCode", "domainCode"));
		jsonObject.put("children", JSONFactoryUtil.createJSONArray());

		jsonObject.put("idCHK", StringPool.BLANK);
		jsonArray.put(jsonObject);
		int countPeriod = 0;

		for (DictItem dictItem : result) {

			jsonObject = JSONFactoryUtil.createJSONObject();
			String[] treeIn = dictItem.getTreeIndex().split(
					StringPool.BACK_SLASH + StringPool.PERIOD);

			countPeriod = StringUtil.count(dictItem.getTreeIndex(),
					StringPool.PERIOD);

			if (countPeriod <= level) {

				if (isCode) {
					jsonObject.put("id", dictItem.getItemCode());
				} else {
					jsonObject.put("id",
							StringUtil.valueOf(dictItem.getDictItemId()));
				}

				jsonObject.put("text",
						dictItem.getItemName(themeDisplay.getLocale()));

				jsonObject.put("children", JSONFactoryUtil.createJSONArray());

				jsonObject.put("idCHK",
						StringUtil.valueOf(dictItem.getDictItemId()));

				if (countPeriod > 0) {

					jsonObject.put("parentId",
							StringUtil.valueOf(treeIn[countPeriod - 1]));

					for (int y = 0; y < jsonArray.length(); y++) {

						buildChildJsonComboboxTreeData(jsonObject, 0,
								jsonArray.getJSONObject(y));

					}
				} else {

					jsonArray.put(jsonObject);

				}

			}
		}

		return jsonArray.toString();
	}

	public static void buildChildJsonComboboxTreeData(JSONObject newJsonObject,
			int i, JSONObject compareJsonObject) {

		JSONObject childObj = compareJsonObject;

		if (Validator.isNotNull(compareJsonObject.getJSONArray("children")
				.getJSONObject(i)) && i > 0) {
			childObj = compareJsonObject.getJSONArray("children")
					.getJSONObject(i);
		}
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		if (childObj != null) {

			jsonArray = childObj.getJSONArray("children");

			if (newJsonObject.getString("parentId").equals(
					childObj.getString("idCHK"))) {

				jsonArray.put(newJsonObject);

				childObj.put("children", jsonArray);
			}

			i++;

			JSONObject objChk = compareJsonObject.getJSONArray("children")
					.getJSONObject(i);

			if (Validator.isNotNull(objChk)) {

				buildChildJsonComboboxTreeData(newJsonObject, i,
						compareJsonObject);
			}

		}
	}
	
	public static String generateTreeView(
			String[] itemCodes, String myLabel, int level, String type,
			boolean isCode, RenderRequest renderRequest)
			throws SystemException, PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest
				.getAttribute(WebKeys.THEME_DISPLAY);

		//long groupId = themeDisplay.getScopeGroupId();

		// get chirentDataSource
		List<DictItem> result = new ArrayList<DictItem>();
		
		if(itemCodes != null){
			for(int i = 0; i < itemCodes.length; i++){
				DictItem dictItem = DictItemLocalServiceUtil.getDictItemByCode(itemCodes[i]);
				result.add(dictItem);
			}
		}

		JSONArray jsonArrayRoot = JSONFactoryUtil.createJSONArray();
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		JSONObject jsonObjectRoot = JSONFactoryUtil.createJSONObject();
		JSONObject jsonObject = null;

		int countPeriod = 0;

		for (DictItem dictItem : result) {

			jsonObject = JSONFactoryUtil.createJSONObject();
			String[] treeIn = dictItem.getTreeIndex().split(
					StringPool.BACK_SLASH + StringPool.PERIOD);

			countPeriod = StringUtil.count(dictItem.getTreeIndex(),
					StringPool.PERIOD);

			if (countPeriod <= level) {

				jsonObject.put("label",
						dictItem.getItemName(themeDisplay.getLocale()));

				jsonObject.put("type", type);

				if (isCode) {
					jsonObject.put("id", dictItem.getItemCode());
				} else {
					jsonObject.put("id",
							StringUtil.valueOf(dictItem.getDictItemId()));
				}

				jsonObject.put("expanded", true);

				if (countPeriod < level) {

					jsonObject.put("leaf", false);

				} else {
					jsonObject.put("leaf", true);
				}

				jsonObject.put("children", JSONFactoryUtil.createJSONArray());

				if (countPeriod > 0) {

					jsonObject.put("parentId",
							StringUtil.valueOf(treeIn[countPeriod - 1]));

					for (int y = 0; y < jsonArray.length(); y++) {

						buildChildJsonTreeData(jsonObject, 0,
								jsonArray.getJSONObject(y));

					}
				} else {

					jsonArray.put(jsonObject);

				}

			}
		}

		jsonObjectRoot.put("children", jsonArray);

		jsonObjectRoot.put("expanded", true);

		jsonObjectRoot.put("label", myLabel);

		jsonArrayRoot.put(jsonObjectRoot);

		return jsonArrayRoot.toString();
	}

	private static Log _log = LogFactoryUtil.getLog(ProcessOrderUtils.class
			.getName());
}
