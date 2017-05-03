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

package org.opencps.taglib.accountmgt;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.opencps.accountmgt.model.Business;
import org.opencps.accountmgt.model.Citizen;
import org.opencps.dossiermgt.bean.AccountBean;
import org.opencps.usermgt.model.Employee;
import org.opencps.util.AccountUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.Role;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.taglib.util.IncludeTag;

/**
 * @author trungnt
 */
public class DefineObjectsTag extends IncludeTag {

	@Override
	public int doStartTag() {

		HttpServletRequest request = (HttpServletRequest) pageContext
				.getRequest();

		ThemeDisplay themeDisplay = (ThemeDisplay) request
				.getAttribute(WebKeys.THEME_DISPLAY);

		if (themeDisplay == null) {
			return SKIP_BODY;
		}

		HttpSession session = request.getSession();

		Object accountInstance = null;

		AccountBean accountBean = null;

		Citizen citizen = null;

		Business business = null;

		Employee employee = null;

		try {
			accountBean = (AccountBean) session
					.getAttribute(org.opencps.util.WebKeys.ACCOUNT_BEAN);

			citizen = (Citizen) session
					.getAttribute(org.opencps.util.WebKeys.CITIZEN_ENTRY);

			business = (Business) session
					.getAttribute(org.opencps.util.WebKeys.BUSINESS_ENTRY);

			employee = (Employee) session
					.getAttribute(org.opencps.util.WebKeys.EMPLOYEE_ENTRY);
		} catch (Exception e) {
			// TODO: handle exception
		}

		String accountType = GetterUtil.getString(session
				.getAttribute(org.opencps.util.WebKeys.ACCOUNT_TYPE));
		ArrayList<Role> accountRoles = (ArrayList<Role>) session
				.getAttribute(org.opencps.util.WebKeys.ACCOUNT_ROLES);

		ArrayList<Organization> accountOrgs = (ArrayList<Organization>) session
				.getAttribute(org.opencps.util.WebKeys.ACCOUNT_ORGANIZATION);

		long ownerUserId = GetterUtil
				.getLong(
						session.getAttribute(org.opencps.util.WebKeys.ACCOUNT_OWNERUSERID),
						0L);

		long ownerOrganizationId = GetterUtil
				.getLong(
						session.getAttribute(org.opencps.util.WebKeys.ACCOUNT_OWNERORGANIZATIONID),
						0L);

		if (themeDisplay.isSignedIn() && Validator.isNull(accountBean)) {

			try {

				// Clean account bean
				AccountUtil.destroy(request, false);

				ServiceContext serviceContext = ServiceContextFactory
						.getInstance(request);

				accountBean = AccountUtil.getAccountBean(
						themeDisplay.getUserId(),
						themeDisplay.getScopeGroupId(), serviceContext);

				if (accountBean != null) {
					accountType = accountBean.getAccountType();
					if (accountBean.isBusiness()) {
						business = (Business) accountBean.getAccountInstance();
						accountInstance = business;
					} else if (accountBean.isCitizen()) {
						citizen = (Citizen) accountBean.getAccountInstance();
						accountInstance = citizen;
					} else if (accountBean.isEmployee()) {
						employee = (Employee) accountBean.getAccountInstance();
						accountInstance = employee;
					}

					ownerOrganizationId = accountBean.getOwnerOrganizationId();
					ownerUserId = accountBean.getOwnerUserId();

					accountOrgs = accountBean.getAccountOrgs();
					accountRoles = accountBean.getAccountRoles();

					request.setAttribute(org.opencps.util.WebKeys.ACCOUNT_TYPE,
							accountType);
					request.setAttribute(
							org.opencps.util.WebKeys.CITIZEN_ENTRY, citizen);
					request.setAttribute(
							org.opencps.util.WebKeys.BUSINESS_ENTRY, business);

					request.setAttribute(
							org.opencps.util.WebKeys.EMPLOYEE_ENTRY, employee);

					request.setAttribute(
							org.opencps.util.WebKeys.ACCOUNT_OWNERORGANIZATIONID,
							ownerOrganizationId);

					request.setAttribute(
							org.opencps.util.WebKeys.ACCOUNT_OWNERUSERID,
							ownerUserId);

					request.setAttribute(
							org.opencps.util.WebKeys.ACCOUNT_ROLES,
							accountRoles);

					request.setAttribute(
							org.opencps.util.WebKeys.ACCOUNT_ORGANIZATION,
							accountOrgs);

					// Store session

					session.setAttribute(org.opencps.util.WebKeys.ACCOUNT_TYPE,
							accountType);
					session.setAttribute(
							org.opencps.util.WebKeys.CITIZEN_ENTRY, citizen);
					session.setAttribute(
							org.opencps.util.WebKeys.BUSINESS_ENTRY, business);

					session.setAttribute(
							org.opencps.util.WebKeys.EMPLOYEE_ENTRY, employee);

					session.setAttribute(
							org.opencps.util.WebKeys.ACCOUNT_OWNERORGANIZATIONID,
							ownerOrganizationId);

					session.setAttribute(
							org.opencps.util.WebKeys.ACCOUNT_OWNERUSERID,
							ownerUserId);

					session.setAttribute(
							org.opencps.util.WebKeys.ACCOUNT_ROLES,
							accountRoles);

					session.setAttribute(
							org.opencps.util.WebKeys.ACCOUNT_ORGANIZATION,
							accountOrgs);

				} else {
					_log.info(DefineObjectsTag.class.getName()
							+ ": ##########################: AccountBean is null");
				}

			} catch (Exception e) {
				_log.error(e);
			} finally {
				accountBean = new AccountBean(accountInstance, accountType,
						accountRoles, accountOrgs, ownerUserId,
						ownerOrganizationId);
				session.setAttribute(org.opencps.util.WebKeys.ACCOUNT_BEAN,
						accountBean);
			}

		}

		return SKIP_BODY;
	}

	private Log _log = LogFactoryUtil.getLog(DefineObjectsTag.class.getName());
}
