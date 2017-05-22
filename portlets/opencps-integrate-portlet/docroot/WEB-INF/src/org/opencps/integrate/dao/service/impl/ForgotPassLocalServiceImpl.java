/**
 * OpenCPS is the open source Core Public Services software
 * Copyright (C) 2016-present OpenCPS community
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>
 */

package org.opencps.integrate.dao.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.opencps.integrate.dao.model.ForgotPass;
import org.opencps.integrate.dao.service.base.ForgotPassLocalServiceBaseImpl;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

/**
 * The implementation of the forgot pass local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link org.opencps.integrate.dao.service.ForgotPassLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author khoavd
 * @see org.opencps.integrate.dao.service.base.ForgotPassLocalServiceBaseImpl
 * @see org.opencps.integrate.dao.service.ForgotPassLocalServiceUtil
 */
public class ForgotPassLocalServiceImpl extends ForgotPassLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this interface directly. Always use {@link org.opencps.integrate.dao.service.ForgotPassLocalServiceUtil} to access the forgot pass local service.
	 */
	
	public ForgotPass addVerifyCode(String code, long userId) throws PortalException, SystemException{
		
		long id = counterLocalService.increment(ForgotPass.class.getName());
		
		ForgotPass forgotPass = forgotPassPersistence.create(id);
		
		Date now = new Date();
		
		Calendar cal = Calendar.getInstance();
		
		cal.setTime(now);
		
		cal.add(Calendar.HOUR, 1);
		
		Date expriedDate = cal.getTime();
		
		forgotPass.setVerifyCode(code);
		
		forgotPass.setCreateDate(now);
		forgotPass.setExpiredDate(expriedDate);
		forgotPass.setUserid(userId);
		forgotPass.setInused(true);
		
		forgotPassPersistence.update(forgotPass);
		
		return forgotPass;
	}
	
	public ForgotPass inuse(String authCode) throws PortalException, SystemException {
		
		ForgotPass forgot = forgotPassPersistence.fetchByV_C(authCode);
		
		forgot.setInused(false);
		
		forgotPassPersistence.update(forgot);
		
		return forgot;
	}
	
	public void invalidCode(long userId) throws PortalException, SystemException{
		
		List<ForgotPass> ls = forgotPassPersistence.findByU_ID(userId);
		
		for (ForgotPass fp : ls) {
			fp.setInused(false);
			forgotPassPersistence.update(fp);
		}
	}
	
	public ForgotPass getByVerifyCode(String verifyCode) throws PortalException, SystemException {
		return forgotPassPersistence.fetchByV_C(verifyCode);
	}
	
}