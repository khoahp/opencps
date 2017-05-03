/**
 * OpenCPS is the open source Core Public Services software Copyright (C)
 * 2016-present OpenCPS community This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3
 * of the License, or any later version. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details. You should have received a
 * copy of the GNU Affero General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>
 */

package org.opencps.paymentmgt.portlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.PortletURL;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;

import org.opencps.accountmgt.model.Citizen;
import org.opencps.jasperreport.util.JRReportUtil;
import org.opencps.paymentmgt.NoSuchPaymentConfigException;
import org.opencps.paymentmgt.model.PaymentConfig;
import org.opencps.paymentmgt.search.PaymentConfigDisplayTerms;
import org.opencps.paymentmgt.service.PaymentConfigLocalServiceUtil;
import org.opencps.util.DLFolderUtil;
import org.opencps.util.PortletPropsValues;
import org.opencps.util.PortletUtil;
import org.opencps.util.WebKeys;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.WindowStateFactory;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.PortletURLFactoryUtil;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.service.DLAppServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * @author trungdk
 */
public class PaymentMgtPaymentConfigPortlet extends MVCPortlet {

	/**
	 * @param jrxmlTemplate
	 * @param formData
	 * @param map
	 * @param outputDestination
	 * @param fileName
	 * @return
	 */
	protected String exportToPDFFile(
		String jrxmlTemplate, String formData, Map<String, Object> map, String outputDestination,
		String fileName) {

		return JRReportUtil.createReportPDFFile(
			jrxmlTemplate, formData, map, outputDestination, fileName);
	}

	public void previewReport(ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

		File file = null;

		InputStream inputStream = null;

		JSONObject responseJSON = JSONFactoryUtil.createJSONObject();

		String fileExportDir = StringPool.BLANK;
		String urlFileDowLoad = StringPool.BLANK;
		PortletSession portletSession = actionRequest.getPortletSession();
		String reportTemplate =
			(String) portletSession.getAttribute(PaymentConfigDisplayTerms.REPORT_TEMPLATE);
		portletSession.removeAttribute(PaymentConfigDisplayTerms.REPORT_TEMPLATE);
		try {
			ServiceContext serviceContext = ServiceContextFactory.getInstance(actionRequest);
			serviceContext.setAddGroupPermissions(true);
			serviceContext.setAddGuestPermissions(true);
			// Get PaymentFile
			
			long userId = themeDisplay.getUserId();
			
				ParamUtil.getLong(actionRequest, PaymentConfigDisplayTerms.PAYMENT_CONFIG_ID);
			// Get account folder
			String dossierDestinationFolder =
				PortletUtil.getEmployeeDestinationFolder(themeDisplay.getScopeGroupId(), userId);
			DLFolder accountForlder =
				DLFolderUtil.getTargetFolder(
					themeDisplay.getUserId(), themeDisplay.getScopeGroupId(),
					themeDisplay.getScopeGroupId(), false, 0, dossierDestinationFolder,
					StringPool.BLANK, false, serviceContext);;

			// Get user folder
			DLFolder paymentFolder =
				DLFolderUtil.addFolder(
					themeDisplay.getUserId(), themeDisplay.getScopeGroupId(),
					themeDisplay.getScopeGroupId(), false, accountForlder.getFolderId(),
					String.valueOf(userId), StringPool.BLANK, false, serviceContext);

			String formData = StringPool.BLANK;
			JSONObject payloadJSON = JSONFactoryUtil.createJSONObject();
			JSONObject resultJSON = JSONFactoryUtil.createJSONObject();
			payloadJSON.put("paymentFileId", "");
			payloadJSON.put("dossierId", "");
			payloadJSON.put("fileGroupId", "");
			payloadJSON.put("ownerUserId", "");
			payloadJSON.put("ownerOrganizationId", "");

			payloadJSON.put("ownerOrganizationName", "");
			payloadJSON.put("ownerOrganizationAddress", "");

			payloadJSON.put("govAgencyOrganizationId", "");
			payloadJSON.put("paymentName", "");
			payloadJSON.put("requestDatetime", "");
			payloadJSON.put("amount", "");
			payloadJSON.put("amountNumber", "");
			payloadJSON.put("amountString", "");

			payloadJSON.put("requestNote", "");
			payloadJSON.put("keypayUrl", "");
			payloadJSON.put("keypayTransactionId", "");
			payloadJSON.put("keypayGoodCode", "");
			payloadJSON.put("keypayMerchantCode", "");
			payloadJSON.put("bankInfo", "");
			payloadJSON.put("placeInfo", "");
			payloadJSON.put("paymentStatus", "");
			payloadJSON.put("paymentMethod", "");

			payloadJSON.put("confirmDatetime", "");
			payloadJSON.put("confirmFileEntryId", "");
			payloadJSON.put("approveDatetime", "");
			payloadJSON.put("accountUserName", "");
			payloadJSON.put("approveNote", "");
			payloadJSON.put("govAgencyTaxNo", "");
			payloadJSON.put("invoiceTemplateNo", "");
			payloadJSON.put("invoiceIssueNo", "");
			payloadJSON.put("invoiceNo", "");

			payloadJSON.put("cf_paymentConfigId", "");
			payloadJSON.put("cf_govAgencyOrganizationId", "");
			payloadJSON.put("cf_govAgencyName", "");
			payloadJSON.put("cf_govAgencyTaxNo", "");
			payloadJSON.put("cf_invoiceTemplateNo", "");
			payloadJSON.put("cf_invoiceIssueNo", "");
			payloadJSON.put("cf_invoiceLastNo", "");
			payloadJSON.put("cf_bankInfo", "");
			payloadJSON.put("cf_placeInfo", "");
			payloadJSON.put("cf_keypayDomain", "");
			payloadJSON.put("cf_keypayVersion", "");
			payloadJSON.put("cf_keypayMerchantCode", "");
			payloadJSON.put("cf_keypaySecureKey", "");
			resultJSON.put("opencps", payloadJSON);
			System.out.println("PaymentMgtBackOfficePortlet.createReport()" + resultJSON.toString());

			String jrxmlTemplate = reportTemplate;

			// Validate json string
			formData = resultJSON.toString();
			// JSONFactoryUtil
			// .createJSONObject(formData);

			String outputDestination = PortletPropsValues.OPENCPS_FILE_SYSTEM_TEMP_DIR;
			String fileName = System.currentTimeMillis() + StringPool.DASH + "preview.pdf";

			fileExportDir =
				exportToPDFFile(jrxmlTemplate, formData, null, outputDestination, fileName);

			if (Validator.isNotNull(fileExportDir)) {

				file = new File(fileExportDir);
				inputStream = new FileInputStream(file);
				if (inputStream != null) {
					String sourceFileName =
						fileExportDir.substring(
							fileExportDir.lastIndexOf(StringPool.SLASH) + 1, fileExportDir.length());

					String mimeType = MimeTypesUtil.getContentType(file);

					FileEntry fileEntry =
						DLAppServiceUtil.addFileEntry(
							serviceContext.getScopeGroupId(), paymentFolder.getFolderId(),
							sourceFileName, mimeType, fileName, StringPool.BLANK, StringPool.BLANK,
							inputStream, file.length(), serviceContext);
					fileExportDir = getURL(fileEntry);
					//String tenFileExport = "defaultPDF.pdfs";
					if (fileExportDir.contains(".pdfs")) {
						urlFileDowLoad =
							fileExportDir.replace(".pdfs", ".pdf") +
								"#view=FitH&scrollbar=0&page=1&toolbar=0&statusbar=0&messages=0&navpanes=0";
					}
					else if (fileExportDir.contains(".doc")) {
						urlFileDowLoad =
							"https://docs.google.com/viewer?url=" +
								PortalUtil.getPortalURL(actionRequest) + fileExportDir +
								"&embedded=true";
					}
					else {
						urlFileDowLoad =
							fileExportDir +
								"#view=FitH&scrollbar=0&page=1&toolbar=0&statusbar=0&messages=0&navpanes=0";
					}
				}
			}
		}
		catch (Exception e) {
			_log.error(e);
		}
		finally {
			responseJSON.put("fileExportDir", urlFileDowLoad);
			PortletUtil.writeJSON(actionRequest, actionResponse, responseJSON);
			inputStream.close();
			file.delete();

		}
	}

	private String getURL(FileEntry fileEntry) {

		try {
			String url =
				"/documents/" + fileEntry.getGroupId() + StringPool.SLASH +
					fileEntry.getFolderId() + StringPool.SLASH + fileEntry.getTitle() +
					"?version=" + fileEntry.getVersion();
			return url;
		}
		catch (Exception e) {
			_log.error(e);
		}

		return "";
	}

	@Override
	public void serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IOException, PortletException {

		long govAgencyOrganizationId =
			ParamUtil.getLong(resourceRequest, PaymentConfigDisplayTerms.GOV_AGENCY_ORGANIZATION_ID);
		
		long paymentGateType =
			ParamUtil.getLong(resourceRequest, PaymentConfigDisplayTerms.PAYMENT_GATE_TYPE, 0);
		PaymentConfig pc = null;
		ThemeDisplay themeDisplay =
			(ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);

		long groupId = themeDisplay.getScopeGroupId();

		try {
			pc =
				PaymentConfigLocalServiceUtil.getPaymentConfigByGovAgency(
					groupId, govAgencyOrganizationId, paymentGateType);
		}
		catch (SystemException e) {
			
			_log.error(e);
		}

		PrintWriter writer = resourceResponse.getWriter();
		JSONArray paymentConfigsJsonArray = JSONFactoryUtil.createJSONArray();

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
		if (pc != null) {
			jsonObject.put(PaymentConfigDisplayTerms.PAYMENT_CONFIG_ID, pc.getPaymentConfigId());
			jsonObject.put(PaymentConfigDisplayTerms.BANK_INFO, pc.getBankInfo());
			jsonObject.put(PaymentConfigDisplayTerms.PLACE_INFO, pc.getPlaceInfo());
			jsonObject.put(PaymentConfigDisplayTerms.KEYPAY_DOMAIN, pc.getKeypayDomain());
			jsonObject.put(PaymentConfigDisplayTerms.KEYPAY_VERSION, pc.getKeypayVersion());
			jsonObject.put(
				PaymentConfigDisplayTerms.KEYPAY_MERCHANT_CODE, pc.getKeypayMerchantCode());
			jsonObject.put(PaymentConfigDisplayTerms.KEYPAY_SECURE_KEY, pc.getKeypaySecureKey());
			jsonObject.put(PaymentConfigDisplayTerms.GOV_AGENCY_TAX_NO, pc.getGovAgencyTaxNo());
			jsonObject.put(PaymentConfigDisplayTerms.INVOICE_TEMPLATE_NO, pc.getInvoiceTemplateNo());
			jsonObject.put(PaymentConfigDisplayTerms.INVOICE_ISSUE_NO, pc.getInvoiceIssueNo());
			jsonObject.put(PaymentConfigDisplayTerms.INVOICE_LAST_NO, pc.getInvoiceLastNo());
			jsonObject.put(PaymentConfigDisplayTerms.REPORT_TEMPLATE, pc.getReportTemplate());
			jsonObject.put(PaymentConfigDisplayTerms.PAYMENT_GATE_TYPE, pc.getPaymentGateType());
			jsonObject.put(PaymentConfigDisplayTerms.PAYMENT_STATUS, pc.getStatus());
			jsonObject.put(PaymentConfigDisplayTerms.RETURN_URL, pc.getReturnUrl());
		}
		paymentConfigsJsonArray.put(jsonObject);

		writer.print(paymentConfigsJsonArray.toString());
		writer.flush();
		writer.close();

		super.serveResource(resourceRequest, resourceResponse);
	}

	/**
	 * @param actionRequest
	 * @param actionResponse
	 * @throws IOException
	 */
	public void setReportTemplateTemp(ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException {

		String reportTemplate =
			ParamUtil.getString(actionRequest, PaymentConfigDisplayTerms.REPORT_TEMPLATE);
		PortletSession portletSession = actionRequest.getPortletSession();
		portletSession.setAttribute(PaymentConfigDisplayTerms.REPORT_TEMPLATE, reportTemplate);
	}

	/**
	 * @param actionRequest
	 * @param actionResponse
	 * @throws IOException
	 */
	public void updatePaymentConfig(ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException {

		long paymentConfigId =
			ParamUtil.getLong(actionRequest, PaymentConfigDisplayTerms.PAYMENT_CONFIG_ID);
		long govAgencyOrganizationId =
			ParamUtil.getLong(actionRequest, PaymentConfigDisplayTerms.GOV_AGENCY_ORGANIZATION_ID);
		String govAgencyName =
			ParamUtil.getString(actionRequest, PaymentConfigDisplayTerms.GOV_AGENCY_NAME);
		String govAgencyTaxNo =
			ParamUtil.getString(actionRequest, PaymentConfigDisplayTerms.GOV_AGENCY_TAX_NO);
		String invoiceTemplateNo =
			ParamUtil.getString(actionRequest, PaymentConfigDisplayTerms.INVOICE_TEMPLATE_NO);
		String invoiceIssueNo =
			ParamUtil.getString(actionRequest, PaymentConfigDisplayTerms.INVOICE_ISSUE_NO);
		String invoiceLastNo =
			ParamUtil.getString(actionRequest, PaymentConfigDisplayTerms.INVOICE_LAST_NO);
		String bankInfo = ParamUtil.getString(actionRequest, PaymentConfigDisplayTerms.BANK_INFO);
		String placeInfo = ParamUtil.getString(actionRequest, PaymentConfigDisplayTerms.PLACE_INFO);
		String keypayDomain =
			ParamUtil.getString(actionRequest, PaymentConfigDisplayTerms.KEYPAY_DOMAIN);
		String keypayVersion =
			ParamUtil.getString(actionRequest, PaymentConfigDisplayTerms.KEYPAY_VERSION);
		String keypayMerchantCode =
			ParamUtil.getString(actionRequest, PaymentConfigDisplayTerms.KEYPAY_MERCHANT_CODE);
		String keypaySecureKey =
			ParamUtil.getString(actionRequest, PaymentConfigDisplayTerms.KEYPAY_SECURE_KEY);
		String reportTemplate =
			ParamUtil.getString(actionRequest, PaymentConfigDisplayTerms.REPORT_TEMPLATE);
		String returnFromPaymentGateUrl =
						ParamUtil.getString(actionRequest, PaymentConfigDisplayTerms.RETURN_URL);
		long paymentGateType =
			ParamUtil.getLong(actionRequest, PaymentConfigDisplayTerms.PAYMENT_GATE_TYPE);
		boolean paymentStatus =
			ParamUtil.getBoolean(actionRequest, PaymentConfigDisplayTerms.PAYMENT_STATUS, false);

		List<PaymentConfig> paymentConfiglist = new ArrayList<PaymentConfig>();

		try {

			ServiceContext serviceContext = ServiceContextFactory.getInstance(actionRequest);

			if (paymentStatus) {
				paymentConfiglist =
					PaymentConfigLocalServiceUtil.getPaymentConfigListByGovAgency(
						serviceContext.getScopeGroupId(), govAgencyOrganizationId);

				if (paymentConfiglist.size() > 0) {
					for (PaymentConfig paymentConfig : paymentConfiglist) {

						paymentConfig.setStatus(false);
						PaymentConfigLocalServiceUtil.updatePaymentConfig(paymentConfig);

					}
				}
			}
			if (paymentConfigId == 0) {
//				PaymentConfig c =
//					PaymentConfigLocalServiceUtil.addPaymentConfig(
//						govAgencyOrganizationId, govAgencyName, govAgencyTaxNo, invoiceTemplateNo,
//						invoiceIssueNo, invoiceLastNo, bankInfo, placeInfo, keypayDomain,
//						keypayVersion, keypayMerchantCode, keypaySecureKey, reportTemplate,
//						paymentGateType, paymentStatus, serviceContext.getUserId(),returnFromPaymentGateUrl, serviceContext);
//				paymentConfigId = c.getPaymentConfigId();
			}
			else {
//				PaymentConfigLocalServiceUtil.updatePaymentConfig(
//					paymentConfigId, govAgencyOrganizationId, govAgencyName, govAgencyTaxNo,
//					invoiceTemplateNo, invoiceIssueNo, invoiceLastNo, bankInfo, placeInfo,
//					keypayDomain, keypayVersion, keypayMerchantCode, keypaySecureKey,
//					reportTemplate, paymentGateType, paymentStatus, serviceContext.getUserId(),returnFromPaymentGateUrl,
//					serviceContext);
			}

			addProcessActionSuccessMessage = false;
			actionResponse.setRenderParameter(
				PaymentConfigDisplayTerms.PAYMENT_CONFIG_ID, String.valueOf(paymentConfigId));
			SessionMessages.add(actionRequest, "update-payment-config-success");
		}
		catch (Exception e) {
			SessionErrors.add(actionRequest, "update-payment-config-error");
		}

	}
	
	public void changePaymentStatusConfig(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException {

		long paymentConfigId = ParamUtil.getLong(actionRequest,
				PaymentConfigDisplayTerms.PAYMENT_CONFIG_ID, 0);
		boolean paymentStatus = ParamUtil.getBoolean(actionRequest,
				PaymentConfigDisplayTerms.PAYMENT_STATUS, false);
		
		
		String redirectUrl = ParamUtil.getString(actionRequest,
				WebKeys.REDIRECT_URL, StringPool.BLANK);

		PaymentConfig paymentConfig = null;

		try {
			if (paymentConfigId > 0) {

				try {
					paymentConfig = PaymentConfigLocalServiceUtil
							.getPaymentConfig(paymentConfigId);
				} catch (NoSuchPaymentConfigException e) {

				}

				paymentConfig.setStatus(paymentStatus);

				PaymentConfigLocalServiceUtil
						.updatePaymentConfig(paymentConfig);

				SessionMessages.add(actionRequest,
						"update-payment-config-success");
			}

		} catch (Exception e) {
			SessionErrors.add(actionRequest, "update-payment-config-error");
		}finally{
			
			actionResponse.sendRedirect(redirectUrl);
		}

	}
	
	/**
	 * @param actionRequest
	 * @param actionResponse
	 * @throws IOException
	 * @throws WindowStateException 
	 */
	public void updatePaymentConfigWithServiceProcess(ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException, WindowStateException {

		long paymentConfigId =
			ParamUtil.getLong(actionRequest, PaymentConfigDisplayTerms.PAYMENT_CONFIG_ID);
		long govAgencyOrganizationId =
			ParamUtil.getLong(actionRequest, PaymentConfigDisplayTerms.GOV_AGENCY_ORGANIZATION_ID);
		String govAgencyName =
			ParamUtil.getString(actionRequest, PaymentConfigDisplayTerms.GOV_AGENCY_NAME);
		String govAgencyTaxNo =
			ParamUtil.getString(actionRequest, PaymentConfigDisplayTerms.GOV_AGENCY_TAX_NO);
		String invoiceTemplateNo =
			ParamUtil.getString(actionRequest, PaymentConfigDisplayTerms.INVOICE_TEMPLATE_NO);
		String invoiceIssueNo =
			ParamUtil.getString(actionRequest, PaymentConfigDisplayTerms.INVOICE_ISSUE_NO);
		String invoiceLastNo =
			ParamUtil.getString(actionRequest, PaymentConfigDisplayTerms.INVOICE_LAST_NO);
		String bankInfo = ParamUtil.getString(actionRequest, PaymentConfigDisplayTerms.BANK_INFO);
		String placeInfo = ParamUtil.getString(actionRequest, PaymentConfigDisplayTerms.PLACE_INFO);
		String keypayDomain =
			ParamUtil.getString(actionRequest, PaymentConfigDisplayTerms.KEYPAY_DOMAIN);
		String keypayVersion =
			ParamUtil.getString(actionRequest, PaymentConfigDisplayTerms.KEYPAY_VERSION);
		String keypayMerchantCode =
			ParamUtil.getString(actionRequest, PaymentConfigDisplayTerms.KEYPAY_MERCHANT_CODE);
		String keypaySecureKey =
			ParamUtil.getString(actionRequest, PaymentConfigDisplayTerms.KEYPAY_SECURE_KEY);
		String reportTemplate =
			ParamUtil.getString(actionRequest, PaymentConfigDisplayTerms.REPORT_TEMPLATE);
		String returnFromPaymentGateUrl =
						ParamUtil.getString(actionRequest, PaymentConfigDisplayTerms.RETURN_URL);
		long paymentGateType =
			ParamUtil.getLong(actionRequest, PaymentConfigDisplayTerms.PAYMENT_GATE_TYPE);
		boolean paymentStatus =
			ParamUtil.getBoolean(actionRequest, PaymentConfigDisplayTerms.PAYMENT_STATUS, false);
		String paymentConfigNo =
				ParamUtil.getString(actionRequest, PaymentConfigDisplayTerms.PAYMENT_CONFIG_NO);
		
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest
				.getAttribute(WebKeys.THEME_DISPLAY);

		PaymentConfig paymentConfig = null;
		try {

			ServiceContext serviceContext = ServiceContextFactory.getInstance(actionRequest);
			

			if (paymentConfigId <= 0) {
				paymentConfig =
					PaymentConfigLocalServiceUtil.addPaymentConfig(
						govAgencyOrganizationId, govAgencyName, govAgencyTaxNo, invoiceTemplateNo,
						invoiceIssueNo, invoiceLastNo, bankInfo, placeInfo, keypayDomain,
						keypayVersion, keypayMerchantCode, keypaySecureKey, reportTemplate,
						paymentGateType, paymentStatus, serviceContext.getUserId(),returnFromPaymentGateUrl,paymentConfigNo, serviceContext);
				paymentConfigId = paymentConfig.getPaymentConfigId();
			}
			else {
				paymentConfig = PaymentConfigLocalServiceUtil.updatePaymentConfig(
					paymentConfigId, govAgencyOrganizationId, govAgencyName, govAgencyTaxNo,
					invoiceTemplateNo, invoiceIssueNo, invoiceLastNo, bankInfo, placeInfo,
					keypayDomain, keypayVersion, keypayMerchantCode, keypaySecureKey,
					reportTemplate, paymentGateType, paymentStatus, serviceContext.getUserId(),returnFromPaymentGateUrl,paymentConfigNo,
					serviceContext);
			}

			addProcessActionSuccessMessage = false;
			SessionMessages.add(actionRequest, "update-payment-config-success");
		}
		catch (Exception e) {
			SessionErrors.add(actionRequest, "update-payment-config-error");
		}finally{
			
			PortletURL redirectURL = PortletURLFactoryUtil.create(
					PortalUtil.getHttpServletRequest(actionRequest),
					(String) actionRequest.getAttribute(WebKeys.PORTLET_ID),
					themeDisplay.getLayout().getPlid(),
					PortletRequest.RENDER_PHASE);
			
			redirectURL.setParameter(
					PaymentConfigDisplayTerms.PAYMENT_CONFIG_ID,
					String.valueOf(paymentConfig.getPaymentConfigId()));
			
			WindowState state = WindowStateFactory
					.getWindowState(LiferayWindowState.POP_UP.toString());
			
			redirectURL.setWindowState(state);

			redirectURL
					.setParameter(
							"mvcPath",
							"/html/portlets/paymentmgt/paymentconfig/paymentconfig_edit.jsp");
			actionResponse.sendRedirect(redirectURL.toString());
		}

	}
	
	/**
	 * @param actionRequest
	 * @param actionResponse
	 * @throws IOException
	 * @throws WindowStateException 
	 */
	public void removePaymentConfig(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException {

		long paymentConfigId = ParamUtil.getLong(actionRequest,
				PaymentConfigDisplayTerms.PAYMENT_CONFIG_ID);

		String redirectURL = ParamUtil.getString(actionRequest,
				WebKeys.REDIRECT_URL);

		try {

			if (paymentConfigId > 0) {
				PaymentConfigLocalServiceUtil
						.deletePaymentConfig(paymentConfigId);
			}

			SessionMessages.add(actionRequest, "update-payment-config-success");
		} catch (Exception e) {
			SessionErrors.add(actionRequest, "update-payment-config-error");
		} finally {

			actionResponse.sendRedirect(redirectURL.toString());
		}

	}

	private Log _log = LogFactoryUtil.getLog(PaymentMgtPaymentConfigPortlet.class.getName());

}
