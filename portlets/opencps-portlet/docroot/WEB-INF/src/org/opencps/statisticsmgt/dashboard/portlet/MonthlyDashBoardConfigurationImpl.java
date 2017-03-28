
package org.opencps.statisticsmgt.dashboard.portlet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;

import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portlet.PortletPreferencesFactoryUtil;

/**
 * @author trungnt
 */
public class MonthlyDashBoardConfigurationImpl
	extends DefaultConfigurationAction {

	@Override
	public void processAction(
		PortletConfig portletConfig, ActionRequest actionRequest,
		ActionResponse actionResponse)
		throws Exception {

		// TODO Auto-generated method stub
		String portletResource =
			ParamUtil.getString(actionRequest, "portletResource");

		PortletPreferences preferences =
			PortletPreferencesFactoryUtil.getPortletSetup(
				actionRequest, portletResource);
		String displayStyle =
			ParamUtil.getString(actionRequest, "displayStyle");

		String chartTitle = ParamUtil.getString(actionRequest, "chartTitle");

		String xaxisUnit = ParamUtil.getString(actionRequest, "xaxisUnit");

		String yaxisUnit = ParamUtil.getString(actionRequest, "yaxisUnit");

		String govCode = ParamUtil.getString(actionRequest, "govCode");

		String domainCode = ParamUtil.getString(actionRequest, "domainCode");

		int startMonth = ParamUtil.getInteger(actionRequest, "startMonth");

		int startYear = ParamUtil.getInteger(actionRequest, "startYear");

		int period = ParamUtil.getInteger(actionRequest, "period");

		int[] fieldsIndexes =
			StringUtil.split(
				ParamUtil.getString(actionRequest, "fieldsIndexes"), 0);

		if (fieldsIndexes != null && fieldsIndexes.length > 0) {

			String[] fieldLabels = new String[fieldsIndexes.length];
			String[] fieldKeys = new String[fieldsIndexes.length];
			String[] fieldFormulas = new String[fieldsIndexes.length];
			for (int f = 0; f < fieldsIndexes.length; f++) {

				String fieldLabel =
					ParamUtil.getString(actionRequest, "fieldLabel" +
						fieldsIndexes[f]);
				String fieldKey =
					ParamUtil.getString(actionRequest, "fieldKey" +
						fieldsIndexes[f]);
				String fieldFormula =
					ParamUtil.getString(actionRequest, "fieldFormula" +
						fieldsIndexes[f]);

				fieldLabels[f] = fieldLabel;
				fieldKeys[f] = fieldKey;
				fieldFormulas[f] = fieldFormula;

				System.out.println(fieldLabel + "-----" + fieldKey + "-----" +
					fieldFormula);
			}

			preferences.setValues("fieldLabels", fieldLabels);
			preferences.setValues("fieldKeys", fieldKeys);
			preferences.setValues("fieldFormulas", fieldFormulas);
		}

		preferences.setValue("chartTitle", chartTitle);

		preferences.setValue("xaxisUnit", xaxisUnit);
		preferences.setValue("yaxisUnit", yaxisUnit);
		preferences.setValue("govCode", govCode);
		preferences.setValue("domainCode", domainCode);
		preferences.setValue("startMonth", String.valueOf(startMonth));
		preferences.setValue("startYear", String.valueOf(startYear));
		preferences.setValue("period", String.valueOf(period));
		preferences.setValue("displayStyle", displayStyle);
		preferences.store();

		super.processAction(portletConfig, actionRequest, actionResponse);
	}
}
