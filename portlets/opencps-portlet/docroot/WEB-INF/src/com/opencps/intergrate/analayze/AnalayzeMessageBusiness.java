package com.opencps.intergrate.analayze;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.opencps.backend.message.UserActionMsg;
import org.opencps.dossiermgt.model.Dossier;
import org.opencps.dossiermgt.model.DossierFile;
import org.opencps.dossiermgt.model.DossierLog;
import org.opencps.dossiermgt.model.DossierPart;
import org.opencps.dossiermgt.model.FileGroup;
import org.opencps.dossiermgt.model.impl.DossierFileImpl;
import org.opencps.dossiermgt.model.impl.DossierLogImpl;
import org.opencps.dossiermgt.model.impl.DossierPartImpl;
import org.opencps.dossiermgt.model.impl.FileGroupImpl;
import org.opencps.dossiermgt.service.DossierFileLocalServiceUtil;
import org.opencps.dossiermgt.service.DossierPartLocalServiceUtil;
import org.opencps.jms.message.body.DossierFileMsgBody;
import org.opencps.jms.message.body.DossierMsgBody;
import org.opencps.jms.message.body.SyncFromBackOfficeMsgBody;
import org.opencps.paymentmgt.model.PaymentFile;
import org.opencps.paymentmgt.model.impl.PaymentFileImpl;
import org.opencps.processmgt.model.ActionHistory;
import org.opencps.processmgt.model.impl.ActionHistoryImpl;
import org.opencps.servicemgt.model.ServiceInfo;
import org.opencps.util.DLFileEntryUtil;
import org.opencps.util.PortletConstants;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.liferay.counter.service.CounterLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.management.jmx.ListDomainsAction;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.documentlibrary.util.DLUtil;

public class AnalayzeMessageBusiness {
	private Log _log = LogFactoryUtil.getLog(AnalayzeMessageBusiness.class);
	
	
	public String getMessageFromBussiness(Dossier dossier,ServiceContext serviceContext, 
			ServiceInfo serviceInfo, User user) throws PortalException, SystemException {
		MessageBodyBean messageBodyBean = new MessageBodyBean();
		List<DossierFile> dossierFiles = new ArrayList<DossierFile>();

		List<AttachFile> attachFiles = new ArrayList<AttachFile>();
		
		List<DossierFile> dossierFileList = DossierFileLocalServiceUtil.getDossierFileByDossierId(dossier.getDossierId());
		
		if(dossierFileList.size() > 0) {
			for(DossierFile dossierFile : dossierFileList) {
				AttachFile attachFile = new AttachFile();

				FileEntry fileEntry = null;
				
				fileEntry = DLFileEntryUtil.getFileEntry(dossierFile.getFileEntryId());
				String url = StringPool.BLANK;
				if (fileEntry != null) {
					attachFile.setFileName(fileEntry.getTitle());
					attachFile.setFileExtension(fileEntry.getExtension());
					url = IntergrateUtil.getFileURL(fileEntry, serviceContext, user);
				}

				DossierPart dossierPart = DossierPartLocalServiceUtil.getDossierPart(dossierFile.getDossierPartId());
				
				if (Validator.isNotNull(dossierPart.getFormScript())) {
					attachFile.setCode(StringUtil
							.lowerCase("ToKhai_Except"));
				} else {
					attachFile.setCode(dossierPart.getPartNo());
				}

				attachFile.setFileUrl(url);
				// _log.info("url________________________" + url);
				attachFiles.add(attachFile);
				
				if (Validator.isNotNull(dossierFile.getFormData())) {
					dossierFiles.add(dossierFile);
				}
			}
		}
		

		// add attach files

		messageBodyBean.setAttachFiles(attachFiles);

		// add json content

		if (dossierFiles.size() > 0) {
			DossierFile dossierFile = IntergrateUtil
					.latestDossierFile(dossierFiles);
			if (Validator.isNotNull(dossierFile)
					&& Validator.isNotNull(dossierFile.getFormData())) {

				/*_log.info("dossierFile.getFormData()________"
						+ dossierFile.getFormData());*/
				
				Gson gson = new GsonBuilder().setDateFormat(
						"dd/MM/yyyy HH:mm:ss").create();
				JsonElement jsonElement = new JsonParser().parse(dossierFile
						.getFormData());
				JsonObject jsonObject = gson.fromJson(jsonElement,
						JsonObject.class);

				messageBodyBean.setDocumentContenJson(jsonObject);

			}

		}

		// user infor

			String dossierStatus = dossier.getDossierStatus();

			if(Validator.isNotNull(dossier.getReceptionNo()) 
					&& dossier.getDossierStatus().equalsIgnoreCase(PortletConstants.DOSSIER_STATUS_SYSTEM)) {
				dossierStatus = PortletConstants.DOSSIER_STATUS_WAITING;
			}

			messageBodyBean.setDossierId(dossier.getDossierId());
			messageBodyBean.setNguoiDuaDonID(dossier.getUserId());
			messageBodyBean.setNguoiDuaDon_Address(dossier.getAddress());
			messageBodyBean.setNguoiDuaDon_Email(dossier.getContactEmail());
			messageBodyBean.setNguoiDuaDon_ID_Passport(dossier.getSubjectId());
			messageBodyBean.setNguoiDuaDon_Name(dossier.getContactName());
			messageBodyBean.setNguoiDuaDon_Tel(dossier.getContactTelNo());
			messageBodyBean.setDocumentCode(dossier.getOid());
			messageBodyBean.setDossierStatus(dossierStatus);

		// document type code

		if (Validator.isNotNull(serviceInfo)) {
			messageBodyBean.setDocumentTypeCode(serviceInfo.getServiceNo());
		}

		Gson gson = new Gson();
		String jsonFromObj = gson.toJson(messageBodyBean);
		
		return jsonFromObj;
	}

	public String receiveDossierMsgBodyLocal(UserActionMsg userActionMsg ,DossierMsgBody dossierMsgBody,
			ThemeDisplay themeDisplay) {
		MessageBodyBean messageBodyBean = new MessageBodyBean();
		Dossier dossier = dossierMsgBody.getDossier();
		ServiceInfo serviceInfo = dossierMsgBody.getServiceInfo();
		List<DossierFileMsgBody> dossierFileMsgBodies = new ArrayList<DossierFileMsgBody>();
		dossierFileMsgBodies = dossierMsgBody.getLstDossierFileMsgBody();
		List<DossierFile> dossierFiles = new ArrayList<DossierFile>();

		List<AttachFile> attachFiles = new ArrayList<AttachFile>();

		if (dossierFileMsgBodies.size() > 0) {
			for (DossierFileMsgBody dossierFileMsgBody : dossierFileMsgBodies) {

				if (Validator.isNotNull(dossierFileMsgBody.getBytes())) {

					AttachFile attachFile = new AttachFile();

					FileEntry fileEntry = null;

					fileEntry = DLFileEntryUtil.getFileEntry(dossierFileMsgBody
							.getDossierFile().getFileEntryId());
					String url = StringPool.BLANK;
					if (fileEntry != null) {
						attachFile.setFileName(fileEntry.getTitle());
						attachFile.setFileExtension(fileEntry.getExtension());

						try {
							url = DLUtil.getPreviewURL(fileEntry,
									fileEntry.getFileVersion(), themeDisplay,
									StringPool.BLANK);
						} catch (Exception e) {
							_log.error(e);
						}
					}

					if (Validator.isNotNull(dossierFileMsgBody.getDossierPart()
							.getFormScript())) {
						attachFile.setCode(StringUtil
								.lowerCase("ToKhai_Except"));
					} else {
						attachFile.setCode(dossierFileMsgBody.getDossierPart()
								.getPartNo());
					}

					attachFile.setFileUrl(url);
					_log.info("url________________________" + url);
					attachFiles.add(attachFile);

				}

				if (Validator.isNotNull(dossierFileMsgBody.getDossierFile()
						.getFormData())) {
					dossierFiles.add(dossierFileMsgBody.getDossierFile());
				}
			}
		}

		// add attach files

		messageBodyBean.setAttachFiles(attachFiles);

		// add json content

		if (dossierFiles.size() > 0) {
			DossierFile dossierFile = IntergrateUtil
					.latestDossierFile(dossierFiles);
			if (Validator.isNotNull(dossierFile)
					&& Validator.isNotNull(dossierFile.getFormData())) {

				Gson gson = new GsonBuilder().setDateFormat(
						"dd/MM/yyyy HH:mm:ss").create();
				JsonElement jsonElement = new JsonParser().parse(dossierFile
						.getFormData());
				JsonObject jsonObject = gson.fromJson(jsonElement,
						JsonObject.class);

				messageBodyBean.setDocumentContenJson(jsonObject);

			}

		}

		// user infor
		if (Validator.isNotNull(userActionMsg)) {

			String dossierStatus = dossier.getDossierStatus();

			if(userActionMsg.getDossierStatus().equalsIgnoreCase(PortletConstants.DOSSIER_STATUS_WAITING)) {
				dossierStatus = userActionMsg.getDossierStatus();
			}

			messageBodyBean.setDossierId(dossier.getDossierId());
			messageBodyBean.setNguoiDuaDonID(dossier.getUserId());
			messageBodyBean.setNguoiDuaDon_Address(dossier.getAddress());
			messageBodyBean.setNguoiDuaDon_Email(dossier.getContactEmail());
			messageBodyBean.setNguoiDuaDon_ID_Passport(dossier.getSubjectId());
			messageBodyBean.setNguoiDuaDon_Name(dossier.getContactName());
			messageBodyBean.setNguoiDuaDon_Tel(dossier.getContactTelNo());
			messageBodyBean.setDocumentCode(dossier.getOid());
			messageBodyBean.setDossierStatus(dossierStatus);

		}

		// document type code

		if (Validator.isNotNull(serviceInfo)) {
			messageBodyBean.setDocumentTypeCode(serviceInfo.getServiceNo());
		}

		Gson gson = new Gson();
		String jsonFromObj = gson.toJson(messageBodyBean);

		return jsonFromObj;
	}

	public SyncFromBackOfficeMsgBody receiveSyncFromBackOfficeMsgBodyLocal(
			TTHCTransferOutObject tthcTransferOutObject, String messRev) {

		_log.info("RECEIVE BO SUCC  "
				+ tthcTransferOutObject.get_dossierStatus());

		SyncFromBackOfficeMsgBody syncFromBackOfficeMsgBody = new SyncFromBackOfficeMsgBody();

		syncFromBackOfficeMsgBody
				.setActionInfo(Validator.isNotNull(tthcTransferOutObject
						.get_actionInfo()) ? tthcTransferOutObject
						.get_actionInfo() : StringPool.BLANK);

		syncFromBackOfficeMsgBody.setDossierId(tthcTransferOutObject
				.get_dossierId());

		syncFromBackOfficeMsgBody
				.setDossierStatus(Validator.isNotNull(tthcTransferOutObject
						.get_dossierStatus()) ? tthcTransferOutObject
						.get_dossierStatus() : StringPool.BLANK);

		syncFromBackOfficeMsgBody.setEstimateDatetime(tthcTransferOutObject
				.get_estimateDatetime());

		syncFromBackOfficeMsgBody.setFinishDatetime(tthcTransferOutObject
				.get_finishDatetime());

		syncFromBackOfficeMsgBody
				.setMessageInfo(Validator.isNotNull(tthcTransferOutObject
						.get_messageInfo()) ? tthcTransferOutObject
						.get_messageInfo() : StringPool.BLANK);

		syncFromBackOfficeMsgBody
				.setOid(Validator.isNotNull(tthcTransferOutObject.get_oid()) ? tthcTransferOutObject
						.get_oid() : StringPool.BLANK);

		syncFromBackOfficeMsgBody.setReceiveDatetime(tthcTransferOutObject
				.get_receiveDatetime());

		syncFromBackOfficeMsgBody
				.setReceptionNo(Validator.isNotNull(tthcTransferOutObject
						.get_receptionNo()) ? tthcTransferOutObject
						.get_receptionNo() : StringPool.BLANK);

		syncFromBackOfficeMsgBody
				.setRequestCommand(Validator.isNotNull(tthcTransferOutObject
						.get_requestCommand()) ? tthcTransferOutObject
						.get_requestCommand() : StringPool.BLANK);

		ServiceContext serviceContext = new ServiceContext();
		// hardcode
		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);
		serviceContext.setScopeGroupId(20182);
		serviceContext.setCompanyId(20155);
		serviceContext.setUserId(25718);
		serviceContext.setUuid(PortalUUIDUtil.generate());
		serviceContext.setCreateDate(new Date());
		serviceContext.setModifiedDate(new Date());
		serviceContext.setDeriveDefaultPermissions(true);
		// end
		syncFromBackOfficeMsgBody.setServiceContext(serviceContext);

		syncFromBackOfficeMsgBody.setSubmitDateTime(tthcTransferOutObject
				.get_submitDateTime());

		syncFromBackOfficeMsgBody.setActor(1);

		syncFromBackOfficeMsgBody.setActorId(0);

		// hardcode

		syncFromBackOfficeMsgBody
				.setActorName(Validator.isNotNull(tthcTransferOutObject
						.get_actorName()) ? tthcTransferOutObject
						.get_actorName() : StringPool.BLANK);

		List<DossierFileMsgBody> dossierFileMsgBody = new ArrayList<DossierFileMsgBody>();
		// hardcode

		/*
		 * DossierPart dossierPart =
		 * IntergrateBussinessUtils.createDossierPart(); DossierFile dossierFile
		 * = IntergrateBussinessUtils.createDossierFile();
		 */

		FileGroup fileGroup = new FileGroupImpl();

		syncFromBackOfficeMsgBody.setFileGroupId(fileGroup.getFileGroupId());

		DossierLog dossierLog = new DossierLogImpl();
		ActionHistory actionHistory = new ActionHistoryImpl();
		PaymentFile paymentFile = new PaymentFileImpl();

		// add AttachFile

		if (Validator.isNotNull(tthcTransferOutObject
				.get_lstDossierFileMsgBody())
				&& tthcTransferOutObject.get_lstDossierFileMsgBody().size() > 0) {
			for (AttachFile attachFile : tthcTransferOutObject
					.get_lstDossierFileMsgBody()) {
				DossierFileMsgBody body = new DossierFileMsgBody();

				DossierPart dossierPart = new DossierPartImpl();
				
				long dossierPartId = 0;
				long dossierFileId = 0;
				
				try {
					dossierPartId = CounterLocalServiceUtil
							.increment(DossierPart.class.getName());
					dossierFileId = CounterLocalServiceUtil
							.increment(DossierFile.class.getName());
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				dossierPart.setDossierpartId(dossierPartId);

				if (tthcTransferOutObject.get_dossierStatus().equals(
						"GT_DENIED")) {

					dossierPart.setPartNo("GT_DONE");
				} else {
					dossierPart.setPartNo("GT_DENIED");
				}

				DossierFile dossierFile = new DossierFileImpl();

				dossierFile.setDossierFileId(dossierFileId);
				dossierFile.setOid(PortalUUIDUtil.generate());
				dossierFile.setDisplayName(attachFile.getFileName());
				dossierFile.setDossierPartId(dossierPart.getDossierpartId());
				dossierFile
						.setSyncStatus(PortletConstants.DOSSIER_FILE_SYNC_STATUS_SYNCSUCCESS);
				dossierFile.setDossierFileDate(new Date());

				body.setDossierFile(dossierFile);
				body.setDossierPart(dossierPart);
				body.setFileTitle(attachFile.getFileName());
				body.setFileName(attachFile.getFileName());
				body.setExtension(attachFile.getFileExtension());
				body.setMimeType(attachFile.getFileExtension());
				body.setBytes(IntergrateUtil.getFileAsBytesFromUrl(attachFile));

				dossierFileMsgBody.add(body);

			}
		} else {
			try {
				/*
				 * List<DossierFileClone> clones =
				 * DossierFileCloneLocalServiceUtil
				 * .getDossierFileClonesByDossierId(tthcTransferOutObject
				 * .get_dossierId());
				 */

				List<DossierFile> dossierFiles = DossierFileLocalServiceUtil
						.getDossierFileByDossierId(tthcTransferOutObject
								.get_dossierId());

				for (DossierFile dossierFile : dossierFiles) {

					DossierFileMsgBody body = new DossierFileMsgBody();

					DossierPart dossierPart = DossierPartLocalServiceUtil
							.getDossierPart(dossierFile.getDossierPartId());

					body.setDossierPart(dossierPart);
					body.setDossierFile(dossierFile);

					dossierFileMsgBody.add(body);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/*
		 * body.setDossierPart(dossierPart); body.setDossierFile(dossierFile);
		 */

		syncFromBackOfficeMsgBody.setActionHistory(actionHistory);

		syncFromBackOfficeMsgBody.setPaymentFile(paymentFile);

		syncFromBackOfficeMsgBody.setDossierLog(dossierLog);

		// end

		syncFromBackOfficeMsgBody.setLstDossierFileMsgBody(dossierFileMsgBody);

		return syncFromBackOfficeMsgBody;

	}

}
