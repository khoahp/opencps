/**
* OpenCPS is the open source Core Public Services software
* Copyright (C) 2016-present OpenCPS community

* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* any later version.

* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>
*/

package org.opencps.struts.action;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.opencps.accountmgt.model.Business;
import org.opencps.accountmgt.model.Citizen;
import org.opencps.accountmgt.service.BusinessLocalServiceUtil;
import org.opencps.accountmgt.service.CitizenLocalServiceUtil;
import org.opencps.util.MessageBusUtil;
import org.opencps.util.PortletConstants;
import org.opencps.util.PortletPropsValues;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.util.PwdGenerator;

/**
 * @author trungnt
 */
public class VerifyEmailAddressAction extends Action {

	@Override
	public ActionForward execute(
	    ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response)
	    throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay) request
		    .getAttribute(org.opencps.util.WebKeys.THEME_DISPLAY);

		ServiceContext serviceContext = ServiceContextFactory
		    .getInstance(request);

		String password = PwdGenerator
		    .getPassword();
		try {
			verifyEmailAddress(request, response, themeDisplay);
			
			String token = request
			    .getParameter("token");
			
			String userType = request
			    .getParameter("type");
			
			String emailConfigStep = request.getParameter("emailConfigStep");
			
			String emailConfirmToAdmin = request.getParameter("emailConfirmToAdmin");
			
			if (userType
			    .equals(PortletPropsValues.USERMGT_USERGROUP_NAME_CITIZEN)) {
				Citizen citizen = CitizenLocalServiceUtil
				    .getCitizenByUUID(token);
				if (citizen != null && (citizen
				    .getAccountStatus() == PortletConstants.ACCOUNT_STATUS_REGISTERED ||
				    citizen.getAccountStatus() == PortletConstants.ACCOUNT_STATUS_APPROVED)) {

					//Neu cau hinh = 2 thi cap nhat mat khau va trang thai gui email thong tin user cho nguoi dung
					//Neu cau hinh = 3 thi cap nhat trang thai thanh confirm de quan tri vao xac nhan
					if(PortletConstants.EMAIL_CONFIG_2_STEP.equals(emailConfigStep)){
						User mappingUser = UserLocalServiceUtil.updatePassword(citizen.getMappingUserId(), password, password, true);
						
						citizen.setAccountStatus(PortletConstants.ACCOUNT_STATUS_APPROVED);
						
						citizen.setModifiedDate(new Date());
						
						CitizenLocalServiceUtil.updateCitizen(citizen);
						
						MessageBusUtil.sendEmailActiveAccount(mappingUser, password, serviceContext);
						
					}else{
						
						User mappingUser = UserLocalServiceUtil.getUser(citizen.getMappingUserId());
						
						citizen.setAccountStatus(PortletConstants.ACCOUNT_STATUS_CONFIRMED);
						
						CitizenLocalServiceUtil.updateCitizen(citizen);
						
						// Gui email thong bao toi quan tri sau khi thuc hien dang ky thanh cong
						MessageBusUtil.sendEmailConfirmToAdmin(citizen.getUuid(),
								mappingUser, mappingUser.getEmailAddress(), emailConfirmToAdmin,
								null, citizen, serviceContext);
						// Gui email thong bao toi quan tri sau khi thuc hien dang ky thanh cong -----END-----
					}
				}
				else {
					return mapping
					    .findForward("verify.email.error");
				}
			}
			else if (userType
			    .equals(PortletPropsValues.USERMGT_USERGROUP_NAME_BUSINESS)) {
				Business business = BusinessLocalServiceUtil
				    .getBusinessByUUID(token);
				if (business != null && ( business
				    .getAccountStatus() == PortletConstants.ACCOUNT_STATUS_REGISTERED ||
				    business.getAccountStatus() == PortletConstants.ACCOUNT_STATUS_APPROVED)) {
					
					business
				    .setModifiedDate(new Date());
					
					if(PortletConstants.EMAIL_CONFIG_2_STEP.equals(emailConfigStep)){
						
						User mappingUser = UserLocalServiceUtil.updatePassword(business
							        .getMappingUserId(), password, password, true);
						
						business.setAccountStatus(PortletConstants.ACCOUNT_STATUS_APPROVED);
						
						MessageBusUtil.sendEmailActiveAccount(mappingUser, password, serviceContext);
						
						BusinessLocalServiceUtil.updateBusiness(business);
						
					}else{
						
						User mappingUser = UserLocalServiceUtil.getUser(business.getMappingUserId());
						
						business.setAccountStatus(PortletConstants.ACCOUNT_STATUS_CONFIRMED);
						
						BusinessLocalServiceUtil.updateBusiness(business);
						
						// Gui email thong bao toi quan tri sau khi thuc hien dang ky thanh cong
						MessageBusUtil.sendEmailConfirmToAdmin(business.getUuid(),
								mappingUser, mappingUser.getEmailAddress(), emailConfirmToAdmin,
								business, null, serviceContext);
						// Gui email thong bao toi quan tri sau khi thuc hien dang ky thanh cong -----END-----
					}
				}
				else {
					return mapping
					    .findForward("verify.email.error");
				}
			}
			else {
				return mapping
				    .findForward("verify.email.error");
			}
			return mapping
			    .findForward("verify.email.success");
		}
		catch (Exception e) {
			return mapping
			    .findForward("verify.email.error");

		}

	}

	protected void verifyEmailAddress(
	    HttpServletRequest request, HttpServletResponse response,
	    ThemeDisplay themeDisplay)
	    throws Exception {

		/*
		 * AuthTokenUtil .checkCSRFToken(request, VerifyEmailAddressAction.class
		 * .getName());
		 */

		String ticketKey = ParamUtil
		    .getString(request, "ticketKey");

		UserLocalServiceUtil
		    .verifyEmailAddress(ticketKey);
	}
}
