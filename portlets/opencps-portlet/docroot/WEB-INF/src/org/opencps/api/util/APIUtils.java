package org.opencps.api.util;

import org.opencps.dossiermgt.NoSuchDossierException;
import org.opencps.dossiermgt.model.Dossier;
import org.opencps.dossiermgt.service.DossierLocalServiceUtil;
import org.opencps.processmgt.NoSuchProcessOrderException;
import org.opencps.processmgt.NoSuchProcessWorkflowException;
import org.opencps.processmgt.model.ProcessOrder;
import org.opencps.processmgt.model.ProcessStep;
import org.opencps.processmgt.model.ProcessWorkflow;
import org.opencps.processmgt.service.ProcessOrderLocalServiceUtil;
import org.opencps.processmgt.service.ProcessStepLocalServiceUtil;
import org.opencps.processmgt.service.ProcessWorkflowLocalServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;

public class APIUtils {
	public static String quoteJSON(String string) {
		if (string == null || string.length() == 0) {
			return "\"\"";
		}

		char c = 0;
		int i;
		int len = string.length();
		StringBuilder sb = new StringBuilder(len + 4);
		String t;

		// sb.append('"');
		for (i = 0; i < len; i += 1) {
			c = string.charAt(i);
			switch (c) {
			case '\\':
				// case '"':
				sb.append('\\');
				sb.append(c);
				break;
			case '/':
				sb.append('\\');
				sb.append(c);
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\r':
				sb.append("\\r");
				break;
			default:
				if (c < ' ') {
					t = "000" + Integer.toHexString(c);
					sb.append("\\u" + t.substring(t.length() - 4));
				} else {
					sb.append(c);
				}
			}
		}
		// sb.append('"');
		return sb.toString();
	}
	
	/**
	 * @param oid
	 * @param currentStatus
	 * @param actionCode
	 * @return
	 */
	public static ProcessWorkflow getProcessWorkflow(String oid,
			String currentStatus, String actionCode) {

		Dossier dossier = null;

		ProcessWorkflow processWorkflow = null;

		ProcessOrder processOrder = null;

		if (Validator.isNotNull(oid) && Validator.isNotNull(currentStatus)
				&& Validator.isNotNull(actionCode)) {
			try {
				dossier = DossierLocalServiceUtil.getDossierByOId(oid);

				if (Validator.equals(dossier.getDossierStatus(), currentStatus)) {

					processOrder = ProcessOrderLocalServiceUtil
							.getProcessOrder(dossier.getDossierId(), 0);

					processWorkflow = ProcessWorkflowLocalServiceUtil
							.getProcessWorkflowByActionCodeAndPreStep(
									actionCode, processOrder.getProcessStepId());
				}

			} catch (Exception e) {
				if (e instanceof NoSuchDossierException) {
					_log.error("No dossier found with iod : " + oid);
				}

				if (e instanceof NoSuchProcessOrderException) {
					_log.error("NoSuchProcessOrderException found with iod : " + oid);
				}

				if (e instanceof NoSuchProcessWorkflowException) {
					_log.error("NoSuchProcessWorkflowException found with iod : " + oid);
				}
			}
		}
		
		return processWorkflow;

	}
	
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
	
	/**
	 * @param processWorkflow
	 * @return
	 */
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
	 * Get ProcessWorkflow
	 * 
	 * @param serviceProcessId
	 * @param event
	 * @param currentStepId
	 * @return {@link ProcessWorkflow}
	 */
	public static ProcessWorkflow getProcessWorkflowByEvent(
			long serviceProcessId, String event, long currentStepId) {
		ProcessWorkflow processWorkflow = null;

		try {
			processWorkflow = ProcessWorkflowLocalServiceUtil
					.getProcessWorkflowByEvent(serviceProcessId, event,
							currentStepId);

		} catch (Exception e) {
			_log.info("NoProcessWorkflowFound!");
		}

		return processWorkflow;
	}

	public static Log _log = LogFactoryUtil.getLog(APIUtils.class.getName());
}
