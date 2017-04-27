package org.opencps.integrate.utils;

import javax.servlet.http.HttpServletRequest;

import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.util.PortalUtil;

public class DossierUtils {
	
	public static ServiceContext getServletContext(HttpServletRequest req) {
		ServiceContext context = new ServiceContext();
		
		try {
			context.setScopeGroupId(PortalUtil.getScopeGroupId(req));
			context.setCompanyId(PortalUtil.getCompanyId(req));			
		} catch (Exception e) {
			
		}
		
		return context;
	}
}
