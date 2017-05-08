package org.opencps.integrate.api;

import org.opencps.dossiermgt.model.Dossier;
import org.opencps.dossiermgt.service.DossierLocalServiceUtil;
import org.opencps.integrate.utils.APIUtils;

import com.liferay.portal.kernel.util.Validator;

public class OCPSPermission {
	
	public boolean isSendValidatorDossier(long dossierId) {

		boolean isPermit = false;

		Dossier dossier = null;

		try {
			dossier = DossierLocalServiceUtil.getDossier(dossierId);
		} catch (Exception e) {

		}

		if (Validator.isNotNull(dossier)
				&& dossier.getDossierStatus().equals("new")) {
			isPermit = true;
		}

		return isPermit;
	}
	
	/** Permission for get all Dossiers
	 * 
	 * @param apiKey
	 * @return
	 */
	public boolean isDossierPermission(String apiKey) {
		
		boolean isPermit = false;
		
		OCPSAuth auth = new OCPSAuth();
		
		boolean isUserAuth = auth.isUser(apiKey);
		boolean isAgencyAuth = auth.isAgency(apiKey);
		
		if (isAgencyAuth || isUserAuth) {
			isPermit = true;
		}
		
		return isPermit;
	}
	
	/** Permission for get Dossier detail
	 * @param apiKey
	 * @param dossierId
	 * @return
	 */
	public boolean isDossierDetailPermission(String apiKey, long dossierId) {
		boolean isPermit = false;

		OCPSAuth auth = new OCPSAuth();

		boolean isAuthDossier = isDossierPermission(apiKey);

		if (isAuthDossier) {

			Dossier dossier = APIUtils.getDossierById(dossierId);
			
			if (Validator.isNotNull(dossier)) {
				
				
				if (auth.isUser(apiKey)) {
					
					if (dossier.getUserId() == auth.auth(apiKey).getUserId()) {
						isPermit = true;
					}
				}
				
				if (auth.isAgency(apiKey)) {
					
					if (dossier.getGovAgencyCode().contentEquals(
							auth.auth(apiKey).getAgency())) {
						
						isPermit = true;
					}
				}
			}
		}

		return isPermit;
	}

	public boolean isDossierActionPermission(String apiKey, long dossierId) {
		return isDossierDetailPermission(apiKey, dossierId);
	}
	
	public boolean isDossierFilePermission(String apiKey, long dossierId) {
		return isDossierDetailPermission(apiKey, dossierId);
	}
	
	public boolean isPaymentFilePermission(String apiKey, long dossierId) {
		return isDossierDetailPermission(apiKey, dossierId);
	}
	
	public boolean isAddDossierPermission(String apiKey) {
		return isDossierPermission(apiKey);
	}
	
	public boolean isUpdateDossierPermission(String apiKey) {
		// TODO: Implement here
		return true;
	}
	
	public boolean isDelDossierPermission(String apiKey) {
		// TODO: Implement here
		return true;
	}
	
	public boolean isAddDossierFilePermission(String apiKey) {
		// TODO: Implement here
		return true;
	}
	
	public boolean isUpdateDossierFilePermission(String apiKey) {
		// TODO: Implement here
		return true;
	}
	
	public boolean isDelDossierFilePermission(String apiKey) {
		// TODO: Implement here
		return true;
	}
	
	public boolean isAddDossierLogPermission(String apiKey) {
		// TODO: Implement here
		return true;
	}
	public boolean isUpdateDossierLogPermission(String apiKey) {
		// TODO: Implement here
		return true;
	}
	public boolean isDelDossierLogPermission(String apiKey) {
		// TODO: Implement here
		return true;
	}
	
	public boolean isAddDossierPaymentFilePermission(String apikey) {
		// TODO: Implement here
		return true;
	}
	public boolean isUpdateDossierPaymentFilePermission(String apikey) {
		// TODO: Implement here
		return true;
	}
	public boolean isDelDossierPaymentFilePermission(String apikey) {
		// TODO: Implement here
		return true;
	}
}
