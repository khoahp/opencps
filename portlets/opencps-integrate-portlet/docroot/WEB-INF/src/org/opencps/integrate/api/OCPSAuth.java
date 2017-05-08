package org.opencps.integrate.api;

import org.opencps.integrate.dao.model.IntegrateAPI;
import org.opencps.integrate.dao.service.IntegrateAPILocalServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

public class OCPSAuth {
	
	/** Check actor login
	 * 
	 * @param apiKey
	 * @return
	 */
	public IntegrateAPI auth(String apiKey) {

		IntegrateAPI api = null;

		try {
			api = IntegrateAPILocalServiceUtil.getAPIByAPIKey(apiKey);

		} catch (Exception e) {
			_log.error(e);
		}

		return api;
	}

	/** Check isUser
	 * 
	 * @param apiKey
	 * @return
	 */
	public boolean isUser(String apiKey) {

		boolean isUser = false;

		IntegrateAPI api = auth(apiKey);

		if (Validator.isNotNull(api) && api.getUserId() != 0) {
			isUser = true;
		}

		return isUser;

	}
	
	/** Check isAgency
	 * 
	 * @param apiKey
	 * @return
	 */
	public boolean isAgency(String apiKey) {

		boolean isAgency = false;

		IntegrateAPI api = auth(apiKey);
		
		//System.out.println(api.getAgency().trim().length());
		
		if (Validator.isNotNull(api) && api.getUserId() == 0
				&& Validator.isNotNull(api.getAgency())) {
			isAgency = true;
		}

		return isAgency;

	}

	private Log _log = LogFactoryUtil.getLog(OCPSAuth.class);
}
