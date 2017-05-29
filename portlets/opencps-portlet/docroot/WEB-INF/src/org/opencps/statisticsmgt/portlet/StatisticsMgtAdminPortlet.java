
package org.opencps.statisticsmgt.portlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.opencps.datamgt.model.DictCollection;
import org.opencps.datamgt.model.DictItem;
import org.opencps.datamgt.service.DictCollectionLocalServiceUtil;
import org.opencps.datamgt.util.DataMgtUtil;
import org.opencps.statisticsmgt.bean.DossierStatisticsBean;
import org.opencps.statisticsmgt.model.DossiersStatistics;
import org.opencps.statisticsmgt.service.DossiersStatisticsLocalServiceUtil;
import org.opencps.statisticsmgt.service.GovagencyLevelLocalServiceUtil;
import org.opencps.statisticsmgt.util.StatisticsUtil;
import org.opencps.statisticsmgt.util.StatisticsUtil.StatisticsFieldNumber;
import org.opencps.util.PortletConstants;
import org.opencps.util.PortletPropsValues;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class StatisticsPortlet
 */
/**
 * @author trungnt
 */
public class StatisticsMgtAdminPortlet extends MVCPortlet {

	public void doStatistics(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		long groupId = ParamUtil.getLong(actionRequest, "groupId");
		int month = ParamUtil.getInteger(actionRequest, "month");
		int year = ParamUtil.getInteger(actionRequest, "year");

		// int currentMonth = ParamUtil.getInteger(actionRequest,
		// "currentMonth");
		// int currentYear = ParamUtil.getInteger(actionRequest, "currentYear");

		int firstMonth = month;
		int lastMonth = month;
		if (month == 0) {
			firstMonth = 1;
			lastMonth = 12;
		}

		_log.info("firstMonth " + firstMonth + "|" + "lastMonth " + lastMonth +
			"|");

		List<Integer> months =
			DossiersStatisticsLocalServiceUtil.getMonths(groupId, year);

		_log.info("########################## " + months.size());

		_log.info("########################## " + StringUtil.merge(months));

		List total = new ArrayList<Object>();

		try {
			for (int m = firstMonth; m <= lastMonth; m++) {

				if ((months.contains(m) && m <= lastMonth)) {
					continue;
				}

				List receiveds1 =
					DossiersStatisticsLocalServiceUtil.generalStatistics(
						groupId, m, year,
						StatisticsFieldNumber.ReceivedNumber.toString(), -1);
				List ontimes1 =
					DossiersStatisticsLocalServiceUtil.generalStatistics(
						groupId, m, year,
						StatisticsFieldNumber.OntimeNumber.toString(),
						PortletConstants.DOSSIER_DELAY_STATUS_ONTIME);
				List overtimes1 =
					DossiersStatisticsLocalServiceUtil.generalStatistics(
						groupId, m, year,
						StatisticsFieldNumber.OvertimeNumber.toString(),
						PortletConstants.DOSSIER_DELAY_STATUS_LATE);
				List processings1 =
					DossiersStatisticsLocalServiceUtil.generalStatistics(
						groupId, m, year,
						StatisticsFieldNumber.ProcessingNumber.toString(),
						PortletConstants.DOSSIER_DELAY_STATUS_UNEXPIRED);
				List delayings1 =
					DossiersStatisticsLocalServiceUtil.generalStatistics(
						groupId, m, year,
						StatisticsFieldNumber.DelayingNumber.toString(),
						PortletConstants.DOSSIER_DELAY_STATUS_EXPIRED);

				if (receiveds1 != null) {
					total.addAll(receiveds1);
				}

				if (ontimes1 != null) {
					total.addAll(ontimes1);
				}

				if (overtimes1 != null) {
					total.addAll(overtimes1);
				}

				if (processings1 != null) {
					total.addAll(processings1);
				}

				if (delayings1 != null) {
					total.addAll(delayings1);
				}

				List receiveds2 =
					DossiersStatisticsLocalServiceUtil.statisticsByDomain(
						groupId, m, year,
						StatisticsFieldNumber.ReceivedNumber.toString(), -1);
				List ontimes2 =
					DossiersStatisticsLocalServiceUtil.statisticsByDomain(
						groupId, m, year,
						StatisticsFieldNumber.OntimeNumber.toString(),
						PortletConstants.DOSSIER_DELAY_STATUS_ONTIME);
				List overtimes2 =
					DossiersStatisticsLocalServiceUtil.statisticsByDomain(
						groupId, m, year,
						StatisticsFieldNumber.OvertimeNumber.toString(),
						PortletConstants.DOSSIER_DELAY_STATUS_LATE);
				List processings2 =
					DossiersStatisticsLocalServiceUtil.statisticsByDomain(
						groupId, m, year,
						StatisticsFieldNumber.ProcessingNumber.toString(),
						PortletConstants.DOSSIER_DELAY_STATUS_UNEXPIRED);
				List delayings2 =
					DossiersStatisticsLocalServiceUtil.statisticsByDomain(
						groupId, m, year,
						StatisticsFieldNumber.DelayingNumber.toString(),
						PortletConstants.DOSSIER_DELAY_STATUS_EXPIRED);

				if (receiveds2 != null) {
					appendData(total, receiveds2);
				}

				if (ontimes2 != null) {
					appendData(total, ontimes2);
				}

				if (overtimes2 != null) {
					appendData(total, overtimes2);
				}

				if (processings2 != null) {
					appendData(total, processings2);
				}

				if (delayings2 != null) {
					appendData(total, delayings2);
				}

				List receiveds3 =
					DossiersStatisticsLocalServiceUtil.statisticsByGovAgency(
						groupId, m, year,
						StatisticsFieldNumber.ReceivedNumber.toString(), -1);
				List ontimes3 =
					DossiersStatisticsLocalServiceUtil.statisticsByGovAgency(
						groupId, m, year,
						StatisticsFieldNumber.OntimeNumber.toString(),
						PortletConstants.DOSSIER_DELAY_STATUS_ONTIME);
				List overtimes3 =
					DossiersStatisticsLocalServiceUtil.statisticsByGovAgency(
						groupId, m, year,
						StatisticsFieldNumber.OvertimeNumber.toString(),
						PortletConstants.DOSSIER_DELAY_STATUS_LATE);
				List processings3 =
					DossiersStatisticsLocalServiceUtil.statisticsByGovAgency(
						groupId, m, year,
						StatisticsFieldNumber.ProcessingNumber.toString(),
						PortletConstants.DOSSIER_DELAY_STATUS_UNEXPIRED);
				List delayings3 =
					DossiersStatisticsLocalServiceUtil.statisticsByGovAgency(
						groupId, m, year,
						StatisticsFieldNumber.DelayingNumber.toString(),
						PortletConstants.DOSSIER_DELAY_STATUS_EXPIRED);

				if (receiveds3 != null) {
					total.addAll(receiveds3);
				}

				if (ontimes3 != null) {
					total.addAll(ontimes3);
				}

				if (overtimes3 != null) {
					total.addAll(overtimes3);
				}

				if (processings3 != null) {
					total.addAll(processings3);
				}

				if (delayings3 != null) {
					total.addAll(delayings3);
				}

			}
		}
		catch (Exception e) {
			_log.error(e);
		}

		if (total != null && !total.isEmpty()) {
			StatisticsUtil.getDossiersStatistics(total);
			// List fakeData = StatisticsUtil.fakeData();
			// StatisticsUtil.getDossiersStatistics(fakeData);
		}
	}

	public void doStatistics2(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		long groupId = ParamUtil.getLong(actionRequest, "groupId");
		int month = ParamUtil.getInteger(actionRequest, "month");
		int year = ParamUtil.getInteger(actionRequest, "year");
		long companyId = ParamUtil.getLong(actionRequest, "companyId");

		DictCollection serviceDomainDictCollection = null;
		List<DictItem> serviceDomainTree = new ArrayList<DictItem>();

		DictCollection govAgencyDictCollection = null;
		List<DictItem> govAgencyTree = new ArrayList<DictItem>();

		try {
			serviceDomainDictCollection =
				DictCollectionLocalServiceUtil.getDictCollection(
					groupId,
					PortletPropsValues.DATAMGT_MASTERDATA_SERVICE_DOMAIN);
		}
		catch (Exception e) {
			_log.error(e);

		}

		if (serviceDomainDictCollection != null) {
			DataMgtUtil.getDictItemTree(
				serviceDomainTree,
				serviceDomainDictCollection.getDictCollectionId(), 0);
		}

		if (serviceDomainTree != null) {
			doStatisticByServiceDomain(
				companyId, groupId, month, year, serviceDomainTree);
		}

		try {
			govAgencyDictCollection =
				DictCollectionLocalServiceUtil.getDictCollection(
					groupId,
					PortletPropsValues.DATAMGT_MASTERDATA_GOVERNMENT_AGENCY);
		}
		catch (Exception e) {
			_log.error(e);

		}

		if (govAgencyDictCollection != null) {
			DataMgtUtil.getDictItemTree(
				govAgencyTree, govAgencyDictCollection.getDictCollectionId(), 0);
		}

		if (govAgencyTree != null) {
			doStatisticByGovAgency(
				companyId, groupId, month, year, govAgencyTree);
		}

		doStatisticByTime(companyId, groupId, month, year);
	}

	private void doStatisticByServiceDomain(
		long companyId, long groupId, int month, int year,
		List<DictItem> dictItems) {

		int firstMonth = month;
		int lastMonth = month;
		if (month == 0) {
			firstMonth = 1;
			lastMonth = 12;
		}
		// Tao bo A (LV,CQ, #0)
		// Tao bo B (LV, 0, 0) = Tong A
		// Tao bo C (LV, CQ, 0) = Tong A la con cua C
		for (int m = firstMonth; m <= lastMonth; m++) {
			for (DictItem dictItem : dictItems) {

				long dictItemId = dictItem.getDictItemId();
				// Bang service config dang luu sai domainCode = dictItemId
				DossiersStatisticsLocalServiceUtil.doStatsDossierReceivedByServiceDomain(
					companyId, groupId, m, year, 0, String.valueOf(dictItemId));
				// Ho so tra dung han
				DossiersStatisticsLocalServiceUtil.doStatsDossierFinishedByServiceDomain(
					companyId, groupId, m, year,
					PortletConstants.DOSSIER_DELAY_STATUS_ONTIME,
					String.valueOf(dictItemId));
				// Ho so tra tre han
				DossiersStatisticsLocalServiceUtil.doStatsDossierFinishedByServiceDomain(
					companyId, groupId, m, year,
					PortletConstants.DOSSIER_DELAY_STATUS_LATE,
					String.valueOf(dictItemId));

				// Ho so dang xu ly
				DossiersStatisticsLocalServiceUtil.doStatsDossierProcessingByServiceDomain(
					companyId, groupId, m, year,
					PortletConstants.DOSSIER_DELAY_STATUS_UNEXPIRED,
					String.valueOf(dictItemId));
				// Ho so dang xu ly tre han
				DossiersStatisticsLocalServiceUtil.doStatsDossierProcessingByServiceDomain(
					companyId, groupId, m, year,
					PortletConstants.DOSSIER_DELAY_STATUS_EXPIRED,
					String.valueOf(dictItemId));
				// Ho so dang xu ly nhung da hoan thanh o thoi gian khac
				DossiersStatisticsLocalServiceUtil.doStatsDossierProcessingButFinishedAtAnotherTimeByServiceDomain(
					companyId, groupId, m, year, String.valueOf(dictItemId));

			}
		}

		// Tinh so ky truoc chuyen qua
		for (int m = firstMonth; m <= lastMonth; m++) {
			for (DictItem dictItem : dictItems) {
				try {
					List<DossiersStatistics> dossiersStatistics =
						DossiersStatisticsLocalServiceUtil.getDossiersStatisticsByDC_M_Y(
							groupId, dictItem.getItemCode(), m, year);
					for (DossiersStatistics dossiersStatisticsTemp : dossiersStatistics) {
						int remainingNumber =
							(dossiersStatisticsTemp.getProcessingNumber() + dossiersStatisticsTemp.getDelayingNumber()) -
								(dossiersStatisticsTemp.getOntimeNumber() + dossiersStatisticsTemp.getOvertimeNumber()) -
								dossiersStatisticsTemp.getReceivedNumber();

						DossiersStatisticsLocalServiceUtil.updateDossiersStatistics(
							dossiersStatisticsTemp.getDossierStatisticId(),
							remainingNumber, -1, -1, -1, -1, -1);
					}
				}
				catch (Exception e) {
					continue;
				}
			}
		}

		// Tao bo C (LV, CQ, 0) = Tong A la con cua C
		try {
			DictCollection govAgencyDictCollection =
				DictCollectionLocalServiceUtil.getDictCollection(
					groupId,
					PortletPropsValues.DATAMGT_MASTERDATA_GOVERNMENT_AGENCY);

			DictCollection serviceDomainDictCollection =
				DictCollectionLocalServiceUtil.getDictCollection(
					groupId,
					PortletPropsValues.DATAMGT_MASTERDATA_SERVICE_DOMAIN);

			List<DictItem> govAgencyTree = new ArrayList<DictItem>();
			govAgencyTree =
				DataMgtUtil.getDictItemTree(
					govAgencyTree,
					govAgencyDictCollection.getDictCollectionId(), 0);

			List<DictItem> serviceDomainTree = new ArrayList<DictItem>();
			serviceDomainTree =
				DataMgtUtil.getDictItemTree(
					serviceDomainTree,
					serviceDomainDictCollection.getDictCollectionId(), 0);

			for (int m = firstMonth; m <= lastMonth; m++) {
				for (DictItem govAgency : govAgencyTree) {

					for (DictItem serviceDomain : serviceDomainTree) {
						try {
							DossiersStatistics dossiersStatistics =
								DossiersStatisticsLocalServiceUtil.getDossiersStatisticsByG_GC_DC_M_Y_L(
									groupId, govAgency.getItemCode(),
									serviceDomain.getItemCode(), m, year, -1);

							List<DictItem> childrens =
								new ArrayList<DictItem>();
							childrens =
								DataMgtUtil.getDictItemTree(
									childrens,
									govAgencyDictCollection.getDictCollectionId(),
									govAgency.getDictItemId());

							if (childrens != null) {

								List<DossiersStatistics> groupStatisticByGovAgencyChildrens =
									new ArrayList<DossiersStatistics>();
								for (DictItem govAgencyChildren : childrens) {
									try {
										DossiersStatistics dossiersStatisticsChildren =
											DossiersStatisticsLocalServiceUtil.getDossiersStatisticsByG_GC_DC_M_Y_L(
												groupId,
												govAgencyChildren.getItemCode(),
												serviceDomain.getItemCode(), m,
												year, -1);

										groupStatisticByGovAgencyChildrens.add(dossiersStatisticsChildren);
									}
									catch (Exception e) {
										continue;
									}
								}

								if (groupStatisticByGovAgencyChildrens != null) {

									groupStatisticByGovAgencyChildrens.add(dossiersStatistics);

									int receivedNumber = 0;
									int remainingNumber = 0;
									int processingNumber = 0;
									int ontimeNumber = 0;
									int delayingNumber = 0;
									int overtimeNumber = 0;
									for (DossiersStatistics groupStatisticByGovAgencyChildren : groupStatisticByGovAgencyChildrens) {
										receivedNumber +=
											groupStatisticByGovAgencyChildren.getReceivedNumber();
										remainingNumber +=
											groupStatisticByGovAgencyChildren.getRemainingNumber();
										processingNumber +=
											groupStatisticByGovAgencyChildren.getProcessingNumber();
										ontimeNumber +=
											groupStatisticByGovAgencyChildren.getOntimeNumber();
										delayingNumber +=
											groupStatisticByGovAgencyChildren.getDelayingNumber();
										overtimeNumber +=
											groupStatisticByGovAgencyChildren.getOvertimeNumber();
									}

									DossiersStatisticsLocalServiceUtil.addDossiersStatistics(
										groupId, companyId, 0, remainingNumber,
										receivedNumber, ontimeNumber,
										overtimeNumber, processingNumber,
										delayingNumber, m, year,
										govAgency.getItemCode(),
										serviceDomain.getItemCode(), 0);

								}
							}
						}
						catch (Exception e) {
							continue;
						}

					}
				}
			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}

	}

	/**
	 * @param companyId
	 * @param groupId
	 * @param month
	 * @param year
	 * @param dictItems
	 */
	private void doStatisticByGovAgency(
		long companyId, long groupId, int month, int year,
		List<DictItem> dictItems) {

		int firstMonth = month;
		int lastMonth = month;
		if (month == 0) {
			firstMonth = 1;
			lastMonth = 12;
		}
		// Tao bo A (0,CQ, #0)
		// Tao bo B (0, C, 0) = Tong A la con cua

		for (int m = firstMonth; m <= lastMonth; m++) {
			for (DictItem dictItem : dictItems) {

				String govCode = dictItem.getItemCode();
				// Bang service config dang luu sai domainCode = dictItemId
				DossiersStatisticsLocalServiceUtil.doStatsDossierReceivedByGovAgency(
					companyId, groupId, m, year, 0, govCode);
				// Ho so tra dung han
				DossiersStatisticsLocalServiceUtil.doStatsDossierFinishedByGovAgency(
					companyId, groupId, m, year,
					PortletConstants.DOSSIER_DELAY_STATUS_ONTIME, govCode);
				// Ho so tra tre han
				DossiersStatisticsLocalServiceUtil.doStatsDossierFinishedByGovAgency(
					companyId, groupId, m, year,
					PortletConstants.DOSSIER_DELAY_STATUS_LATE, govCode);

				// Ho so dang xu ly
				DossiersStatisticsLocalServiceUtil.doStatsDossierProcessingByGovAgency(
					companyId, groupId, m, year,
					PortletConstants.DOSSIER_DELAY_STATUS_UNEXPIRED, govCode);
				// Ho so dang xu ly tre han
				DossiersStatisticsLocalServiceUtil.doStatsDossierProcessingByGovAgency(
					companyId, groupId, m, year,
					PortletConstants.DOSSIER_DELAY_STATUS_EXPIRED, govCode);
				// Ho so dang xu ly nhung da hoan thanh o thoi gian khac
				DossiersStatisticsLocalServiceUtil.doStatsDossierProcessingButFinishedAtAnotherTimeByGovAgency(
					companyId, groupId, m, year, govCode);

			}
		}

		// Tinh so ky truoc chuyen qua
		for (int m = firstMonth; m <= lastMonth; m++) {
			for (DictItem dictItem : dictItems) {
				try {
					List<DossiersStatistics> dossiersStatistics =
						DossiersStatisticsLocalServiceUtil.getDossiersStatisticsByG_GC_DC_M_Y(
							groupId, dictItem.getItemCode(), StringPool.BLANK,
							m, year);
					for (DossiersStatistics dossiersStatisticsTemp : dossiersStatistics) {
						int remainingNumber =
							(dossiersStatisticsTemp.getProcessingNumber() + dossiersStatisticsTemp.getDelayingNumber()) -
								(dossiersStatisticsTemp.getOntimeNumber() + dossiersStatisticsTemp.getOvertimeNumber()) -
								dossiersStatisticsTemp.getReceivedNumber();

						DossiersStatisticsLocalServiceUtil.updateDossiersStatistics(
							dossiersStatisticsTemp.getDossierStatisticId(),
							remainingNumber, -1, -1, -1, -1, -1);
					}
				}
				catch (Exception e) {
					continue;
				}
			}
		}

		try {
			DictCollection govAgencyDictCollection =
				DictCollectionLocalServiceUtil.getDictCollection(
					groupId,
					PortletPropsValues.DATAMGT_MASTERDATA_GOVERNMENT_AGENCY);

			List<DictItem> govAgencyTree = new ArrayList<DictItem>();

			govAgencyTree =
				DataMgtUtil.getDictItemTree(
					govAgencyTree,
					govAgencyDictCollection.getDictCollectionId(), 0);

			for (int m = firstMonth; m <= lastMonth; m++) {
				for (DictItem govAgency : govAgencyTree) {
					List<DictItem> childrens = new ArrayList<DictItem>();
					childrens =
						DataMgtUtil.getDictItemTree(
							childrens,
							govAgencyDictCollection.getDictCollectionId(),
							govAgency.getDictItemId());

					if (childrens != null) {

						List<DossiersStatistics> groupStatisticByGovAgencyChildrens =
							new ArrayList<DossiersStatistics>();
						for (DictItem govAgencyChildren : childrens) {
							try {
								DossiersStatistics dossiersStatistics =
									DossiersStatisticsLocalServiceUtil.getDossiersStatisticsByG_GC_DC_M_Y_L(
										groupId,
										govAgencyChildren.getItemCode(),
										StringPool.BLANK, m, year, -1);

								groupStatisticByGovAgencyChildrens.add(dossiersStatistics);
							}
							catch (Exception e) {
								continue;
							}
						}

						if (groupStatisticByGovAgencyChildrens != null) {
							DossiersStatistics parent = null;
							try {
								parent =
									DossiersStatisticsLocalServiceUtil.getDossiersStatisticsByG_GC_DC_M_Y_L(
										groupId, govAgency.getItemCode(),
										StringPool.BLANK, m, year, -1);
							}
							catch (Exception e) {
								// TODO: handle exception
							}

							if (parent != null) {
								groupStatisticByGovAgencyChildrens.add(parent);
							}

							int receivedNumber = 0;
							int remainingNumber = 0;
							int processingNumber = 0;
							int ontimeNumber = 0;
							int delayingNumber = 0;
							int overtimeNumber = 0;
							for (DossiersStatistics groupStatisticByGovAgencyChildren : groupStatisticByGovAgencyChildrens) {
								receivedNumber +=
									groupStatisticByGovAgencyChildren.getReceivedNumber();
								remainingNumber +=
									groupStatisticByGovAgencyChildren.getRemainingNumber();
								processingNumber +=
									groupStatisticByGovAgencyChildren.getProcessingNumber();
								ontimeNumber +=
									groupStatisticByGovAgencyChildren.getOntimeNumber();
								delayingNumber +=
									groupStatisticByGovAgencyChildren.getDelayingNumber();
								overtimeNumber +=
									groupStatisticByGovAgencyChildren.getOvertimeNumber();
							}

							DossiersStatisticsLocalServiceUtil.addDossiersStatistics(
								groupId, companyId, 0, remainingNumber,
								receivedNumber, ontimeNumber, overtimeNumber,
								processingNumber, delayingNumber, m, year,
								govAgency.getItemCode(), StringPool.BLANK, 0);

						}
					}

				}
			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}

	}

	private void doStatisticByTime(
		long companyId, long groupId, int month, int year) {

		int firstMonth = month;
		int lastMonth = month;
		if (month == 0) {
			firstMonth = 1;
			lastMonth = 12;
		}

		for (int m = firstMonth; m <= lastMonth; m++) {

			DossiersStatisticsLocalServiceUtil.doStatsDossierReceived(
				companyId, groupId, m, year, 0);
			// Ho so tra dung han
			DossiersStatisticsLocalServiceUtil.doStatsDossierFinished(
				companyId, groupId, m, year,
				PortletConstants.DOSSIER_DELAY_STATUS_ONTIME);
			// Ho so tra tre han
			DossiersStatisticsLocalServiceUtil.doStatsDossierFinished(
				companyId, groupId, m, year,
				PortletConstants.DOSSIER_DELAY_STATUS_LATE);

			// Ho so dang xu ly
			DossiersStatisticsLocalServiceUtil.doStatsDossierProcessing(
				companyId, groupId, m, year,
				PortletConstants.DOSSIER_DELAY_STATUS_UNEXPIRED);
			// Ho so dang xu ly tre han
			DossiersStatisticsLocalServiceUtil.doStatsDossierProcessing(
				companyId, groupId, m, year,
				PortletConstants.DOSSIER_DELAY_STATUS_EXPIRED);
			// Ho so dang xu ly nhung da hoan thanh o thoi gian khac
			DossiersStatisticsLocalServiceUtil.doStatsDossierProcessingButFinishedAtAnotherTime(
				companyId, groupId, m, year);
		}

		// Tinh so ky truoc chuyen qua
		for (int m = firstMonth; m <= lastMonth; m++) {
			try {
				List<DossiersStatistics> dossiersStatistics =
					DossiersStatisticsLocalServiceUtil.getDossiersStatisticsByG_GC_DC_M_Y(
						groupId, StringPool.BLANK, StringPool.BLANK, m, year);
				for (DossiersStatistics dossiersStatisticsTemp : dossiersStatistics) {
					int remainingNumber =
						(dossiersStatisticsTemp.getProcessingNumber() + dossiersStatisticsTemp.getDelayingNumber()) -
							(dossiersStatisticsTemp.getOntimeNumber() + dossiersStatisticsTemp.getOvertimeNumber()) -
							dossiersStatisticsTemp.getReceivedNumber();

					DossiersStatisticsLocalServiceUtil.updateDossiersStatistics(
						dossiersStatisticsTemp.getDossierStatisticId(),
						remainingNumber, -1, -1, -1, -1, -1);
				}
			}
			catch (Exception e) {
				continue;
			}
		}

	}

	/**
	 * @param total
	 * @param dossierStatisticsBeans
	 */
	@Deprecated
	public static void appendData(
		List total, List<DossierStatisticsBean> dossierStatisticsBeans) {

		LinkedHashMap<String, DossierStatisticsBean> beanMap =
			new LinkedHashMap<String, DossierStatisticsBean>();

		for (int i = 0; i < dossierStatisticsBeans.size(); i++) {
			DossierStatisticsBean statisticsBean =
				(DossierStatisticsBean) dossierStatisticsBeans.get(i);

			String key =
				statisticsBean.getDomainTreeIndex() + StringPool.DASH +
					statisticsBean.getGovItemCode() + StringPool.DASH +
					statisticsBean.getMonth() + StringPool.DASH +
					statisticsBean.getYear() + StringPool.DASH +
					statisticsBean.getAdministrationLevel();
			beanMap.put(key, statisticsBean);

			StatisticsUtil.getDossierStatisticsBeanByDomainTreeIndex(
				beanMap, statisticsBean, false);
		}

		if (beanMap.size() == dossierStatisticsBeans.size()) {

			total.addAll(dossierStatisticsBeans);
		}
		else {
			for (Map.Entry<String, DossierStatisticsBean> entry : beanMap.entrySet()) {
				total.add(entry.getValue());
			}
		}

		// total.addAll(dossierStatisticsBeans);
	}

	/**
	 * @param actionRequest
	 * @param actionResponse
	 * @throws IOException
	 */
	public void updateAdministrationLevel(
		ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException {

		String redirectURL = ParamUtil.getString(actionRequest, "redirectURL");
		String[] govCodes =
			ParamUtil.getParameterValues(actionRequest, "govCode");

		String[] levels = ParamUtil.getParameterValues(actionRequest, "level");
		if (govCodes != null && levels != null &&
			levels.length == govCodes.length && govCodes.length > 0) {
			try {
				ServiceContext serviceContext =
					ServiceContextFactory.getInstance(actionRequest);
				for (int i = 0; i < govCodes.length; i++) {
					GovagencyLevelLocalServiceUtil.updateGovagencyLevel(
						serviceContext.getCompanyId(),
						serviceContext.getScopeGroupId(),
						serviceContext.getUserId(), govCodes[i],
						GetterUtil.getInteger(levels[i]));
				}
			}
			catch (Exception e) {
				_log.error(e);
			}
			finally {
				if (Validator.isNotNull(redirectURL)) {
					actionResponse.sendRedirect(redirectURL);
				}
			}

		}
	}

	private Log _log =
		LogFactoryUtil.getLog(StatisticsMgtAdminPortlet.class.getName());
}
