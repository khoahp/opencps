package org.opencps.integrate.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.opencps.datamgt.model.DictItem;
import org.opencps.datamgt.service.DictItemLocalServiceUtil;
import org.opencps.dossiermgt.model.Dossier;
import org.opencps.dossiermgt.model.DossierFile;
import org.opencps.dossiermgt.model.DossierPart;
import org.opencps.dossiermgt.service.DossierFileLocalServiceUtil;
import org.opencps.dossiermgt.service.DossierLocalServiceUtil;
import org.opencps.dossiermgt.service.DossierLogLocalServiceUtil;
import org.opencps.dossiermgt.service.DossierPartLocalServiceUtil;
import org.opencps.paymentmgt.model.PaymentFile;
import org.opencps.paymentmgt.service.PaymentFileLocalServiceUtil;
import org.opencps.processmgt.model.ProcessOrder;
import org.opencps.processmgt.model.ProcessStep;
import org.opencps.processmgt.model.ProcessWorkflow;
import org.opencps.processmgt.service.ProcessOrderLocalServiceUtil;
import org.opencps.processmgt.service.ProcessStepLocalServiceUtil;
import org.opencps.processmgt.service.ProcessWorkflowLocalServiceUtil;
import org.opencps.servicemgt.model.ServiceInfo;
import org.opencps.servicemgt.service.ServiceInfoLocalServiceUtil;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.service.DLAppLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.util.DLUtil;

public class APIUtils {
	
	public static final long GROUPID = 20182;
	
	public static ProcessWorkflow getProcessWorkflow(long dossierId, String actionCode) {

		Dossier dossier = null;

		ProcessWorkflow processWorkflow = null;

		ProcessOrder processOrder = null;

			try {
				dossier = DossierLocalServiceUtil.getDossier(dossierId);


					processOrder = ProcessOrderLocalServiceUtil
							.getProcessOrder(dossier.getDossierId(), 0);

					processWorkflow = ProcessWorkflowLocalServiceUtil
							.getProcessWorkflowByActionCodeAndPreStep(
									actionCode, processOrder.getProcessStepId());
			

			} catch (Exception e) {

			}
		
		return processWorkflow;

	}
	
	public static String getPostDossierStatus(ProcessWorkflow processWorkflow) {

		String nextDossierStatus = StringPool.BLANK;

		if (Validator.isNotNull(processWorkflow)) {
			long postStepId = processWorkflow.getPostProcessStepId();

			if (postStepId != 0) {
				try {

					ProcessStep processStep = ProcessStepLocalServiceUtil
							.getProcessStep(postStepId);

					nextDossierStatus = processStep.getDossierStatus();

				} catch (Exception e) {
					_log.info("NoNextStepFound!");
				}
			}

		}

		return nextDossierStatus;
	}
	

	/**
	 * @param dictItemCode
	 * @return
	 */
	public static String getDictItemName(String dictItemCode) {

		String name = StringPool.BLANK;

		DictItem di = null;

		Locale locale = new Locale("vi", "VN");

		try {
			di = DictItemLocalServiceUtil.getDictItemByCode(dictItemCode);

			name = di.getItemName(locale);
		} catch (Exception e) {
			_log.debug(e);
		}

		return name;
	}

	/**
	 * @param companyId
	 * @param email
	 * @return
	 */
	public static User getUserByEmail(long companyId, String email) {
		User user = null;

		try {
			user = UserLocalServiceUtil.getUserByEmailAddress(companyId, email);
		} catch (Exception e) {
			_log.error(e);
		}

		return user;
	}

	/**
	 * Get DossierFileSize
	 * 
	 * @param dossierFileId
	 * @return file size in Byte
	 */
	public static long getFileSize(long dossierFileId) {
		long fileSize = 0;

		try {
			DossierFile dossierFile = DossierFileLocalServiceUtil
					.getDossierFile(dossierFileId);

			long fileEntryId = dossierFile.getFileEntryId();

			DLFileEntry fileEntry = DLFileEntryLocalServiceUtil
					.getDLFileEntry(fileEntryId);

			fileSize = fileEntry.getSize();

		} catch (Exception e) {
			_log.debug(e);
		}

		return fileSize;
	}

	/**
	 * Get dossierFileURL
	 * 
	 * @param fileEntryId
	 * @return
	 */
	public static String getFileURL(long fileEntryId) {
		FileEntry file = null;

		String fileURL = StringPool.BLANK;

		try {
			file = DLAppLocalServiceUtil.getFileEntry(fileEntryId);

			fileURL = DLUtil.getPreviewURL(file, file.getFileVersion(), null,
					"");

		} catch (Exception e) {
			_log.debug(e);
		}

		return fileURL;
	}

	/**
	 * @param fileEntryId
	 * @return
	 */
	public static String getFileType(long fileEntryId) {
		FileEntry file = null;

		String fileTypes = StringPool.BLANK;

		try {
			file = DLAppLocalServiceUtil.getFileEntry(fileEntryId);

			fileTypes = file.getExtension();

		} catch (Exception e) {
			_log.debug(e);
		}

		return fileTypes;
	}

	/**
	 * Get DossierFileContent
	 * 
	 * @param dossierFileId
	 * @return
	 */
	public static String getDossierContent(long dossierFileId) {

		String dossierContent = StringPool.BLANK;

		try {
			DossierFile file = DossierFileLocalServiceUtil
					.getDossierFile(dossierFileId);

			long dossierPartId = file.getDossierPartId();

			DossierPart dossierPart = DossierPartLocalServiceUtil
					.getDossierPart(dossierPartId);

			boolean isEForm = isEForm(dossierPart);

			if (isEForm) {
				dossierContent = file.getFormData();
			}

		} catch (Exception e) {
			_log.debug(e);
		}

		return dossierContent;
	}

	private static boolean isEForm(DossierPart dossierPart) {
		boolean isEForm = false;

		if (Validator.isNotNull(dossierPart.getFormScript().trim())) {
			isEForm = true;
		}

		return isEForm;
	}

	/**
	 * Get DossierPartNo
	 * 
	 * @param dossierFileId
	 * @return
	 */
	public static String getDossierPartNo(long dossierFileId) {
		String dossierPartNo = StringPool.BLANK;

		try {
			DossierFile file = DossierFileLocalServiceUtil
					.getDossierFile(dossierFileId);

			long dossierPartId = file.getDossierPartId();

			DossierPart dossierPart = DossierPartLocalServiceUtil
					.getDossierPart(dossierPartId);

			dossierPartNo = dossierPart.getPartNo();
		} catch (Exception e) {
			_log.debug(e);
		}

		return dossierPartNo;
	}

	/**
	 * @param dossierId
	 * @return
	 */
	public static List<DossierFile> getDossierFile(long dossierId) {
		List<DossierFile> dossierFiles = new ArrayList<>();

		try {
			dossierFiles = DossierFileLocalServiceUtil
					.getDossierFileByDossierId(dossierId);
		} catch (Exception e) {
			_log.debug(e);
		}

		return dossierFiles;
	}

	/**
	 * @param dossierId
	 * @return
	 */
	public static List<PaymentFile> getPaymentFiles(long dossierId) {
		List<PaymentFile> paymentFiles = new ArrayList<>();

		try {
			paymentFiles = PaymentFileLocalServiceUtil
					.getPaymentFileByD_(dossierId);
		} catch (Exception e) {
			_log.debug(e);
		}

		return paymentFiles;
	}

	/**
	 * Count DossierFile
	 * 
	 * @param dossierId
	 * @return
	 */
	public static int countDossierFile(long dossierId) {

		int count = 0;
		try {
			count = DossierFileLocalServiceUtil.getDossierFileByDossierId(
					dossierId).size();
		} catch (Exception e) {
			_log.debug(e);
		}

		return count;
	}

	/**
	 * Count Payment files
	 * 
	 * @param dossierId
	 * @return
	 */
	public static int countPaymentFile(long dossierId) {

		int count = 0;
		try {
			count = PaymentFileLocalServiceUtil.countAllPaymentFile(dossierId);
		} catch (Exception e) {
			_log.debug(e);
		}

		return count;
	}

	/**
	 * Get Language
	 * 
	 * @param key
	 * @return
	 */
	public static String getLanguageValue(String key) {

		Locale locale = new Locale("vi", "VN");

		return LanguageUtil.get(locale, key);
	}

	/**
	 * Count DossierLog
	 * 
	 * @param dossierId
	 * @return
	 */
	public static int countDossierLogs(long dossierId) {
		int count = 0;

		try {
			count = DossierLogLocalServiceUtil.countDossierLog(1, dossierId);
		} catch (Exception e) {
			_log.debug(e);
		}

		return count;
	}

	/**
	 * @param serviceInfoId
	 * @return
	 */
	public static String getApplicantIdType(long dossierId) {

		String applicationType = StringPool.BLANK;

		return applicationType;
	}

	/**
	 * @param dossierId
	 * @return
	 */
	public static String getApplicantIdNo(long dossierId) {

		String applicationNo = StringPool.BLANK;

		return applicationNo;
	}

	/**
	 * @param serviceInfoId
	 * @return
	 */
	public static String getServiceName(long serviceInfoId) {
		String serviceName = StringPool.BLANK;

		try {
			ServiceInfo si = ServiceInfoLocalServiceUtil
					.getServiceInfo(serviceInfoId);

			serviceName = si.getServiceName();

		} catch (Exception e) {
			_log.debug(e);
		}

		return serviceName;
	}

	/**
	 * @param serviceInfoId
	 * @return
	 */
	public static String getServiceCode(long serviceInfoId) {
		String serviceCode = StringPool.BLANK;

		try {
			ServiceInfo si = ServiceInfoLocalServiceUtil
					.getServiceInfo(serviceInfoId);

			serviceCode = si.getServiceNo();

		} catch (Exception e) {
			_log.debug(e);
		}

		return serviceCode;
	}

	/**
	 * Get Dossier by dossierId
	 * 
	 * @param dossierId
	 * @return
	 */
	public static Dossier getDossierById(long dossierId) {
		Dossier dossier = null;

		try {
			dossier = DossierLocalServiceUtil.getDossier(dossierId);
		} catch (Exception e) {
			_log.debug(e);
		}

		return dossier;
	}

	/**
	 * Search Dossier
	 * 
	 * @param processNo
	 * @param processStepNo
	 * @param userId
	 * @param govAgencyCode
	 * @param dossierStatus
	 * @param start
	 * @param end
	 * @return
	 */
	public static List<Dossier> searchDossierAPI(String processNo,
			String processStepNo, long userId, String govAgencyCode,
			String dossierStatus, int start, int end) {
		List<Dossier> ls = new ArrayList<>();
		try {
			ls = DossierLocalServiceUtil.searchDossierAPI(processNo,
					processStepNo, userId, govAgencyCode, dossierStatus, start,
					end);
		} catch (Exception e) {
			_log.debug(e);
		}

		return ls;
	}

	/**
	 * Count Dossier
	 * 
	 * @param processNo
	 * @param processStepNo
	 * @param userId
	 * @param govAgencyCode
	 * @param dossierStatus
	 * @return
	 */
	public static int countDossierAPI(String processNo, String processStepNo,
			long userId, String govAgencyCode, String dossierStatus) {

		int count = 0;

		try {
			count = DossierLocalServiceUtil.countDossierAPI(processNo,
					processStepNo, userId, govAgencyCode, dossierStatus);
		} catch (Exception e) {
			_log.debug(e);
		}

		return count;
	}

	/**
	 * Format DateTime
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDateTime(Date date) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		if (Validator.isNotNull(date)) {
			return sdf.format(date);
		} else {
			return StringPool.BLANK;
		}
	}
	
	public static Date convertDateTime(String strDate) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		Date date = null;

		try {
			date = sdf.parse(strDate);

		} catch (Exception e) {
		}
		return date;
	}

	public static Log _log = LogFactoryUtil.getLog(APIUtils.class);
}
