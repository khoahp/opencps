package org.opencps.statisticsmgt.dashboard.template;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.opencps.statisticsmgt.model.DossiersStatistics;
import org.opencps.statisticsmgt.service.DossiersStatisticsLocalService;
import org.opencps.statisticsmgt.service.DossiersStatisticsService;
import org.opencps.util.WebKeys;

import com.liferay.portal.kernel.portletdisplaytemplate.BasePortletDisplayTemplateHandler;
import com.liferay.portal.kernel.template.TemplateVariableGroup;
import com.liferay.portlet.portletdisplaytemplate.util.PortletDisplayTemplateConstants;

public class DetailDashBoardPortletDisplayTemplateHandler extends
		BasePortletDisplayTemplateHandler {

	@Override
	public String getClassName() {
		// TODO Auto-generated method stub
		return DossiersStatistics.class.getName() + "#DETAIL";
	}

	@Override
	public String getName(Locale arg0) {
		// TODO Auto-generated method stub
		return "Detail DashBoard";
	}

	@Override
	public String getResourceName() {
		return WebKeys.DETAIL_DASHBOARD_PORTLET;
	}

	@Override
	public Map<String, TemplateVariableGroup> getTemplateVariableGroups(
			long classPK, String language, Locale locale) throws Exception {

		Map<String, TemplateVariableGroup> templateVariableGroups = super
				.getTemplateVariableGroups(classPK, language, locale);

		TemplateVariableGroup templateVariableGroup = templateVariableGroups
				.get("fields");

		templateVariableGroup.empty();

		templateVariableGroup.addCollectionVariable("dossiersStatistics",
				List.class, PortletDisplayTemplateConstants.ENTRIES,
				"dossierStatistics", DossiersStatistics.class,
				"curDossierStatistics", "month");

		templateVariableGroup.addCollectionVariable("dossiersStatistics",
				List.class, PortletDisplayTemplateConstants.ENTRIES,
				"dossierStatistics", DossiersStatistics.class,
				"curDossierStatistics", "year");
		
		templateVariableGroup.addVariable(
			"json-data", String.class, "jsonData");

		templateVariableGroup
				.addVariable("dossierStatistics", List.class,
						PortletDisplayTemplateConstants.ENTRIES,
						"curDossierStatistics");

		String[] restrictedVariables = getRestrictedVariables(language);
		
		TemplateVariableGroup statisticsServicesTemplateVariableGroup = new TemplateVariableGroup(
				"statistic-services", restrictedVariables);

		statisticsServicesTemplateVariableGroup.setAutocompleteEnabled(true);

		statisticsServicesTemplateVariableGroup.addServiceLocatorVariables(
				DossiersStatisticsLocalService.class,
				DossiersStatisticsService.class);

		templateVariableGroups.put(
				statisticsServicesTemplateVariableGroup.getLabel(),
				statisticsServicesTemplateVariableGroup);

		return templateVariableGroups;
	}
}
