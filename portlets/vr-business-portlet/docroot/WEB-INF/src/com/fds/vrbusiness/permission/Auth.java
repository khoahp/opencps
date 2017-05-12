package com.fds.vrbusiness.permission;

import com.fds.vrbusiness.NotAuthException;
import com.fds.vrbusiness.model.APIKeys;
import com.fds.vrbusiness.service.APIKeysLocalServiceUtil;

public class Auth {
	
	public APIKeys auth(String apikey) throws NotAuthException{
		APIKeys api = null;
		
		try {
			api = APIKeysLocalServiceUtil.getByKeys(apikey);
		} catch (Exception e) {
			throw new NotAuthException();
		}
		
		return api;
	}
	
}
