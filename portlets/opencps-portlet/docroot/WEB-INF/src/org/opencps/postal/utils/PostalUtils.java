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

/**
 * @author nhanhlt
 * */
package org.opencps.postal.utils;

import org.opencps.accountmgt.model.Business;
import org.opencps.accountmgt.model.Citizen;
import org.opencps.datamgt.model.DictItem;
import org.opencps.datamgt.service.DictItemLocalServiceUtil;
import org.opencps.dossiermgt.bean.AccountBean;
import org.opencps.dossiermgt.model.Dossier;
import org.opencps.dossiermgt.service.DossierLocalServiceUtil;
import org.opencps.postal.model.PostOfficeMapping;
import org.opencps.postal.model.PostalConfig;
import org.opencps.postal.model.PostalOrder;
import org.opencps.postal.model.VnPostal;
import org.opencps.postal.model.impl.VnPostalImpl;
import org.opencps.postal.service.PostOfficeMappingLocalServiceUtil;
import org.opencps.postal.service.PostalConfigLocalServiceUtil;
import org.opencps.postal.service.PostalOrderLocalServiceUtil;
import org.opencps.usermgt.model.WorkingUnit;
import org.opencps.usermgt.service.WorkingUnitLocalServiceUtil;
import org.opencps.util.AccountUtil;

import com.liferay.counter.service.CounterLocalServiceUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONSerializer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;

public class PostalUtils {

	private static Log _log = LogFactoryUtil.getLog(PostalUtils.class);

	/**
	 * @param dossierId
	 * @param postalOrderStatus
	 * @param groupId
	 */
	public void updatePostalOrder(long dossierId, String postalOrderStatus,
			long groupId, String noiDungHang, String ghiChu, Double soTienCOD) {

		try {

			String maKhachHang = StringPool.BLANK;

			int postIdThuGom = 0;
			String diaChiNguoiGui = StringPool.BLANK;
			String tenNguoiGui = StringPool.BLANK;
			String emailNguoiGui = StringPool.BLANK;
			String dienThoaiNguoiGui = StringPool.BLANK;

			// String noiDungHang = StringPool.BLANK;
			// Double soTienCOD = 0.0;
			// String ghiChu = StringPool.BLANK;

			int posIdNhanTin = 0;
			String tenNguoiNhan = StringPool.BLANK;
			String diaChiNguoiNhan = StringPool.BLANK;
			String dienThoaiNguoiNhan = StringPool.BLANK;
			String maBuuGui = StringPool.BLANK;
			String emailNguoiNhan = StringPool.BLANK;

			/*
			 * Nhung thong tin khong can lay tai buoc nay
			 */
			int maTinhGui = 0;
			int maHuyenGui = 0;
			String soDonHang = StringPool.BLANK;
			int maTinhNhan = 0;
			int maHuyenNhan = 0;
			String ngayNhap = StringPool.BLANK;

			// ///////////////////////////////////

			if (dossierId > 0 && postalOrderStatus.trim().length() > 0) {

				Dossier dossier = DossierLocalServiceUtil
						.fetchDossier(dossierId);

				if (Validator.isNotNull(dossier)) {

					/*
					 * Lay thong tin nguoi gui
					 */

					postIdThuGom = getCityCodeMapping(Long.valueOf(dossier
							.getCityCode()));

					WorkingUnit workingUnit = null;

					workingUnit = WorkingUnitLocalServiceUtil.getWorkingUnit(
							groupId, dossier.getGovAgencyCode());
					diaChiNguoiGui = workingUnit.getAddress();
					tenNguoiGui = workingUnit.getName();
					emailNguoiGui = workingUnit.getEmail();
					dienThoaiNguoiGui = workingUnit.getTelNo();

					/*
					 * Lay thong tin nguoi nhan
					 */

					AccountBean accountBean = AccountUtil.getAccountBean(
							dossier.getUserId(), groupId, null);

					Citizen citizen = null;
					Business bussines = null;
					long dictItemId = 0;

					if (accountBean.isCitizen()) {
						citizen = (Citizen) accountBean.getAccountInstance();

						dictItemId = Long.valueOf(citizen.getCityCode());

						tenNguoiNhan = citizen.getFullName();
						diaChiNguoiNhan = citizen.getAddress();
						dienThoaiNguoiNhan = citizen.getTelNo();
						emailNguoiNhan = citizen.getEmail();

					} else if (accountBean.isBusiness()) {
						bussines = (Business) accountBean.getAccountInstance();

						dictItemId = Long.valueOf(bussines.getCityCode());

						tenNguoiNhan = bussines.getName();
						diaChiNguoiNhan = bussines.getAddress();
						dienThoaiNguoiNhan = bussines.getTelNo();
						emailNguoiNhan = bussines.getEmail();
					}
					posIdNhanTin = getCityCodeMapping(dictItemId);

					/*
					 * lay maKhachHang duoc cau hinh trong table PostalConfig
					 */
					PostalConfig postalConfig = null;
					try {
						postalConfig = PostalConfigLocalServiceUtil
								.getPostalConfigBy(
										dossier.getGovAgencyOrganizationId(),
										PostalKeys.ACTIVE);

					} catch (Exception e) {

					}

					if (Validator.isNotNull(postalConfig)) {

						maKhachHang = postalConfig.getPostalCustomerCode();
					}

					String postalOrderContent = createJsonPostalOrderContent(
							postIdThuGom, maTinhGui, maHuyenGui, maKhachHang,
							soDonHang, diaChiNguoiGui, tenNguoiGui,
							emailNguoiGui, dienThoaiNguoiGui, noiDungHang,
							soTienCOD, ghiChu, ngayNhap, posIdNhanTin,
							tenNguoiNhan, diaChiNguoiNhan, dienThoaiNguoiNhan,
							maBuuGui, maTinhNhan, maHuyenNhan, emailNguoiNhan);

					PostalOrderLocalServiceUtil.updatePosOrder(
							0,
							postalOrderStatus,
							postalOrderContent,
							Validator.isNotNull(postalConfig) ? postalConfig
									.getGovAgencyOrganizationId() : 0);

				}
			}
		} catch (Exception e) {
			_log.error(e);
		}
	}

	/**
	 * @param PostIdThuGom
	 * @param maTinhGui
	 * @param maHuyenGui
	 * @param maKhachHang
	 * @param soDonHang
	 * @param diaChiNguoiGui
	 * @param tenNguoiGui
	 * @param emailNguoiGui
	 * @param dienThoaiNguoiGui
	 * @param noiDungHang
	 * @param soTienCOD
	 * @param ghiChu
	 * @param ngayNhap
	 * @param posIdNhanTin
	 * @param tenNguoiNhan
	 * @param diaChiNguoiNhan
	 * @param dienThoaiNguoiNhan
	 * @param maBuuGui
	 * @param maTinhNhan
	 * @param maHuyenNhan
	 * @param emailNguoiNhan
	 * @return jsonData
	 */
	public String createJsonPostalOrderContent(int PostIdThuGom, int maTinhGui,
			int maHuyenGui, String maKhachHang, String soDonHang,
			String diaChiNguoiGui, String tenNguoiGui, String emailNguoiGui,
			String dienThoaiNguoiGui, String noiDungHang, Double soTienCOD,
			String ghiChu, String ngayNhap, int posIdNhanTin,
			String tenNguoiNhan, String diaChiNguoiNhan,
			String dienThoaiNguoiNhan, String maBuuGui, int maTinhNhan,
			int maHuyenNhan, String emailNguoiNhan) {

		VnPostal vnPost = new VnPostalImpl();

		vnPost.setPosIdThuGom(PostIdThuGom);
		// vnPost.setMaTinhGui(maTinhGui);
		// vnPost.setMaHuyenGui(maHuyenGui);
		vnPost.setSoDonHang(soDonHang);
		vnPost.setMaKhachHang(maKhachHang);
		vnPost.setDiaChiNguoiGui(diaChiNguoiGui);
		vnPost.setTenNguoiGui(tenNguoiGui);
		vnPost.setEmailNguoiGui(emailNguoiGui);
		vnPost.setDienThoaiNguoiGui(dienThoaiNguoiGui);
		vnPost.setNoiDungHang(noiDungHang);
		vnPost.setSoTienCOD(soTienCOD);
		vnPost.setGhiChu(ghiChu);
		vnPost.setNgayNhap(ngayNhap);

		vnPost.setPosIdNhanTin(posIdNhanTin);
		vnPost.setTenNguoiNhan(tenNguoiNhan);
		vnPost.setDiaChiNguoiNhan(diaChiNguoiNhan);
		vnPost.setDienThoaiNguoiNhan(dienThoaiNguoiNhan);
		// vnPost.setMaBuuGui(maBuuGui);
		// vnPost.setTrongLuong(trongLuong);
		// vnPost.setCuocChinh(cuocChinh);
		// vnPost.setCuocCOD(cuocCOD);
		vnPost.setDonHangNoiHuyen(false);
		// vnPost.setChieuRong(chieuRong);
		// vnPost.setChieuDai(chieuDai);
		vnPost.setMaTinhNhan(maTinhNhan);
		// vnPost.setMaHuyenNhan(maHuyenNhan);
		vnPost.setEmailNguoiNhan(emailNguoiNhan);

		JSONSerializer jsonSerializer = JSONFactoryUtil.createJSONSerializer();

		String jsonData = jsonSerializer.serialize(vnPost);

		return jsonData;

	}

	/**
	 * @param jsonObject
	 * @return VnPostal
	 */
	public VnPostal convertJsonToVnPostal(JSONObject jsonObject) {

		VnPostal vnPost = new VnPostalImpl();

		vnPost.setPosIdThuGom(jsonObject.getInt("posIdThuGom"));
		// vnPost.setMaTinhGui(maTinhGui);
		// vnPost.setMaHuyenGui(maHuyenGui);
		vnPost.setSoDonHang(jsonObject.getString("soDonHang"));
		vnPost.setMaKhachHang(jsonObject.getString("maKhachHang"));
		vnPost.setDiaChiNguoiGui(jsonObject.getString("diaChiNguoiGui"));
		vnPost.setTenNguoiGui(jsonObject.getString("tenNguoiGui"));
		vnPost.setEmailNguoiGui(jsonObject.getString("emailNguoiGui"));
		vnPost.setDienThoaiNguoiGui(jsonObject.getString("dienThoaiNguoiGui"));
		vnPost.setNoiDungHang(jsonObject.getString("noiDungHang"));
		vnPost.setSoTienCOD(jsonObject.getDouble("soTienCOD"));
		vnPost.setGhiChu(jsonObject.getString("ghiChu"));
		vnPost.setNgayNhap(jsonObject.getString("ngayNhap"));

		vnPost.setPosIdNhanTin(jsonObject.getInt("posIdNhanTin"));
		vnPost.setTenNguoiNhan(jsonObject.getString("tenNguoiNHan"));
		vnPost.setDiaChiNguoiNhan(jsonObject.getString("diaChiNguoiNhan"));
		vnPost.setDienThoaiNguoiNhan(jsonObject.getString("dienThoaiNguoiNhan"));
		vnPost.setMaBuuGui(jsonObject.getString("maBuuGui"));
		vnPost.setDonHangNoiHuyen(false);
		vnPost.setMaTinhNhan(jsonObject.getInt("maTinhNhan"));
		// vnPost.setMaHuyenNhan(maHuyenNhan);
		vnPost.setEmailNguoiNhan(jsonObject.getString("emailNguoiNhan"));
		// vnPost.setExtendData(StringPool.BLANK);
		// vnPost.setFlagConfig(0);

		return vnPost;
	}

	/**
	 * @param vnPostal
	 * @return
	 */
	public JSONObject convertVnPostalToJSon(VnPostal vnPostal) {

		try {

			String jsonData = JSONFactoryUtil.serialize(vnPostal);

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(jsonData);

			return jsonObject;

		} catch (Exception e) {
			_log.error(e);
		}

		return null;
	}

	/**
	 * @return
	 */
	public long _genetatorTransactionCode() {

		long transactionCode = 0;
		try {
			transactionCode = CounterLocalServiceUtil
					.increment(PostalOrder.class.getName()
							+ ".generatorTransactionCode");
		} catch (SystemException e) {
			_log.error(e);
		}
		return transactionCode;
	}

	/**
	 * @param dictItemId
	 * @return
	 */
	public int getCityCodeMapping(long dictItemId) {

		try {

			DictItem dictItem = null;

			if (dictItemId > 0) {

				dictItem = DictItemLocalServiceUtil.getDictItem(dictItemId);

				if (Validator.isNotNull(dictItem)) {

					PostOfficeMapping postOfficeMapping = null;

					postOfficeMapping = PostOfficeMappingLocalServiceUtil
							.getMappingBy(dictItem.getItemCode());

					if (Validator.isNotNull(postOfficeMapping)) {

						return Integer.valueOf(postOfficeMapping
								.getPostOfficeCode());

					}
				}
			}
		} catch (Exception e) {
			_log.error(e);
		}

		return 0;

	}

}