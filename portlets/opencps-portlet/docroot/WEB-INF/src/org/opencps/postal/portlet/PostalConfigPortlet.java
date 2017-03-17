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

/**
 * @author nhanhlt
 * */
package org.opencps.postal.portlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.opencps.postal.model.PostalConfig;
import org.opencps.postal.search.PostalConfigDisplayTerms;
import org.opencps.postal.service.PostalConfigLocalServiceUtil;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.util.bridges.mvc.MVCPortlet;

public class PostalConfigPortlet extends MVCPortlet {

	private static Log _log = LogFactoryUtil.getLog(PostalConfigPortlet.class);

	public void updatePostalConfig(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException {

		long postalConfigId = ParamUtil.getLong(actionRequest,
				PostalConfigDisplayTerms.POSTAL_CONFIG_ID, 0);
		long govAgencyOrganizationId = ParamUtil.getLong(actionRequest,
				PostalConfigDisplayTerms.GOV_AGENCY_ORGANIZATION_ID);
		String postalDomain = ParamUtil.getString(actionRequest,
				PostalConfigDisplayTerms.POSTAL_DOMAIN, StringPool.BLANK);
		String postalTokenCode = ParamUtil.getString(actionRequest,
				PostalConfigDisplayTerms.POSTAL_TOKENCODE, StringPool.BLANK);
		String postalCustomerCode = ParamUtil.getString(actionRequest,
				PostalConfigDisplayTerms.POSTAL_CUSTOMERCODE, StringPool.BLANK);
		boolean postalConfigStatus = ParamUtil.getBoolean(actionRequest,
				PostalConfigDisplayTerms.STATUS, true);
		String postalGateType = ParamUtil.getString(actionRequest,
				PostalConfigDisplayTerms.POSTAL_GATETYPE, StringPool.BLANK);

		List<PostalConfig> postalConfigs = new ArrayList<PostalConfig>();

		try {

			postalConfigs = PostalConfigLocalServiceUtil
					.getPostalConfigByOrgId(govAgencyOrganizationId);

			if (postalConfigs.size() > 0) {

				for (PostalConfig postalConfig : postalConfigs) {

					postalConfig.setStatus(false);

					PostalConfigLocalServiceUtil
							.updatePostalConfig(postalConfig);
				}
			}

			PostalConfig postalConfig =  PostalConfigLocalServiceUtil.updateConfig(postalConfigId,
					govAgencyOrganizationId, postalCustomerCode,
					postalTokenCode, postalDomain, postalGateType,
					postalConfigStatus);

			actionResponse.setRenderParameter(
					PostalConfigDisplayTerms.POSTAL_CONFIG_ID,
					String.valueOf(postalConfig.getPostalConfigId()));
			SessionMessages.add(actionRequest, "update-postal-config-success");

		} catch (Exception e) {
			SessionErrors.add(actionRequest, "update-postal-config-error");
		}

	}

	@Override
	public void serveResource(ResourceRequest resourceRequest,
			ResourceResponse resourceResponse) throws IOException,
			PortletException {

		long govAgencyOrganizationId = ParamUtil.getLong(resourceRequest,
				PostalConfigDisplayTerms.GOV_AGENCY_ORGANIZATION_ID);
		boolean postalStatus = ParamUtil.getBoolean(resourceRequest,
				PostalConfigDisplayTerms.STATUS, true);
		String postalGateType = ParamUtil.getString(resourceRequest,
				PostalConfigDisplayTerms.POSTAL_GATETYPE, StringPool.BLANK);
		PostalConfig postalConfig = null;

		try{
			postalConfig = PostalConfigLocalServiceUtil.getPostalConfigBy(
					govAgencyOrganizationId, postalGateType);
		}catch(Exception e){
			
		}
		

		PrintWriter writer = resourceResponse.getWriter();
		JSONArray paymentConfigsJsonArray = JSONFactoryUtil.createJSONArray();

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
		if (Validator.isNotNull(postalConfig)) {
			jsonObject.put(PostalConfigDisplayTerms.POSTAL_CONFIG_ID,
					postalConfig.getPostalConfigId());
			jsonObject.put(PostalConfigDisplayTerms.POSTAL_DOMAIN,
					postalConfig.getPostalDomain());
			jsonObject.put(PostalConfigDisplayTerms.POSTAL_TOKENCODE,
					postalConfig.getPostalTokenCode());
			jsonObject.put(PostalConfigDisplayTerms.POSTAL_CUSTOMERCODE,
					postalConfig.getPostalCustomerCode());

			jsonObject.put(PostalConfigDisplayTerms.POSTAL_GATETYPE,
					postalConfig.getPostalGateType());
			jsonObject.put(PostalConfigDisplayTerms.STATUS,
					postalConfig.getStatus());
		}
		paymentConfigsJsonArray.put(jsonObject);

		writer.print(paymentConfigsJsonArray.toString());
		writer.flush();
		writer.close();

		super.serveResource(resourceRequest, resourceResponse);
	}

}