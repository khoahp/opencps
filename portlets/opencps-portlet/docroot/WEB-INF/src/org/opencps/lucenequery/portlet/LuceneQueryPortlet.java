package org.opencps.lucenequery.portlet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.opencps.lucenequery.model.LuceneQueryPattern;
import org.opencps.lucenequery.service.LuceneQueryPatternLocalServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class LuceneQueryPortlet
 */
public class LuceneQueryPortlet extends MVCPortlet {

	public void deleteLuceneQueryPattern(ActionRequest actionRequest,
			ActionResponse actionResponse) {
		long patternId = ParamUtil.getLong(actionRequest, "patternId");
		try {
			if (patternId > 0) {
				LuceneQueryPatternLocalServiceUtil
						.deleteLuceneQueryPattern(patternId);
			}
		} catch (Exception e) {
			_log.error(e);
		}
	}

	public void editPattern(ActionRequest actionRequest,
			ActionResponse actionResponse) {
		long patternId = ParamUtil.getLong(actionRequest, "patternId");
		String name = ParamUtil.getString(actionRequest, "name");
		String pattern = ParamUtil.getString(actionRequest, "pattern");
		String url = ParamUtil.getString(actionRequest, "url");

		try {
			ServiceContext serviceContext = ServiceContextFactory
					.getInstance(actionRequest);
			if (patternId > 0) {
				LuceneQueryPatternLocalServiceUtil.updateLuceneQueryPattern(
						patternId, serviceContext.getUserId(), name, pattern,
						url);
			} else {
				LuceneQueryPatternLocalServiceUtil.addLuceneQueryPattern(
						serviceContext.getCompanyId(),
						serviceContext.getScopeGroupId(),
						serviceContext.getUserId(), name, pattern, url);
			}
		} catch (Exception e) {
			_log.error(e);
		}
	}

	private Log _log = LogFactoryUtil
			.getLog(LuceneQueryPortlet.class.getName());
}
