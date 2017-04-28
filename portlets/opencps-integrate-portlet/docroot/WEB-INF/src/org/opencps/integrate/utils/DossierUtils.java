package org.opencps.integrate.utils;

import javax.servlet.http.HttpServletRequest;

import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;

public class DossierUtils {
	
	public static ServiceContext getServletContext(HttpServletRequest req) {
		ServiceContext context = new ServiceContext();
		
		try {
			
			context = ServiceContextFactory.getInstance(req);
			
		} catch (Exception e) {
			
		}
		
		return context;
	}
}
