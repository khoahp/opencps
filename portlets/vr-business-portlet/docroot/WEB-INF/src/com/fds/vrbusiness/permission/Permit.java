package com.fds.vrbusiness.permission;

import org.opencps.dossiermgt.NoSuchDossierException;
import org.opencps.dossiermgt.model.Dossier;
import org.opencps.dossiermgt.model.DossierFile;
import org.opencps.dossiermgt.service.DossierFileLocalServiceUtil;
import org.opencps.dossiermgt.service.DossierLocalServiceUtil;

import com.fds.vrbusiness.NotAuthException;
import com.fds.vrbusiness.OutOfScopeDataException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

public class Permit {
	public boolean isPermitDossier(long dossierId, long userLoginId)
			throws OutOfScopeDataException, NoSuchDossierException {
		
		boolean isPermit = false;
		
		try {
			Dossier dossier = DossierLocalServiceUtil.getDossier(dossierId);
			
			if (dossier.getUserId() == userLoginId) {
				isPermit = true;
			} else {
				throw new NotAuthException();
			}
			
		} catch (PortalException | SystemException e) {
			throw new NoSuchDossierException();
		} 
		
		return isPermit;
	}
	
	public boolean isPermitDossierFile(long dossierId, long dossierFileId,
			long userLoginId) throws OutOfScopeDataException {

		boolean isPermit = false;

		try {
			Dossier dossier = DossierLocalServiceUtil.getDossier(dossierId);

			DossierFile dossierFile = DossierFileLocalServiceUtil
					.getDossierFile(dossierFileId);

			if ((dossierFile.getDossierId() == dossierId)
					&& (dossier.getUserId() == userLoginId)) {
				isPermit = true;
			}

		} catch (Exception e) {
			throw new OutOfScopeDataException();
		}

		return isPermit;
	}
}
