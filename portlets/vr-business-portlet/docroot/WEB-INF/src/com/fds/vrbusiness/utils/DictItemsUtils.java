package com.fds.vrbusiness.utils;

import java.util.ArrayList;
import java.util.List;

import org.opencps.datamgt.model.DictCollection;
import org.opencps.datamgt.model.DictItem;
import org.opencps.datamgt.model.DictItemLink;
import org.opencps.datamgt.service.DictCollectionLocalServiceUtil;
import org.opencps.datamgt.service.DictItemLinkLocalServiceUtil;
import org.opencps.datamgt.service.DictItemLocalServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

public class DictItemsUtils {
	
	/**
	 * @param dictItemLinkedCode
	 * @param dictCollectionId
	 * @param dictCollectionLinkedId
	 * @return
	 */
	public static List<DictItem> getDictItems(String dictItemLinkedCode,
			long dictCollectionId, long dictCollectionLinkedId) {

		List<DictItem> dictItems = new ArrayList<>();
		
		try {
			List<DictItemLink> dictItemsLink = DictItemLinkLocalServiceUtil
					.getBy_IC_CI_CLI(dictItemLinkedCode, dictCollectionId,
							dictCollectionLinkedId);
			
			for (DictItemLink dil : dictItemsLink) {
				
				DictItem di = getDictItem(dil.getDictItemId());
				
				if (Validator.isNotNull(di)) {
					dictItems.add(di);
				}
				
			}

		} catch (Exception e) {
			_log.error(e);
		}
		
		return dictItems;
	}
	
	/**
	 * @param dictItemId
	 * @return
	 */
	private static DictItem getDictItem(long dictItemId) {
		DictItem di = null;
		
		try {
			di = DictItemLocalServiceUtil.getDictItem(dictItemId);
		} catch (Exception e) {
			_log.error(e);
		}
		
		return di;
	}
	
	/**
	 * @param dictCollectionCode
	 * @return
	 */
	public static long getDictCollectionId(String dictCollectionCode) {
		long dictCollectionId = 0;

		try {
			DictCollection dc = DictCollectionLocalServiceUtil
					.getDictCollectionByCode(dictCollectionCode);

			dictCollectionId = dc.getDictCollectionId();
		} catch (Exception e) {
			_log.error(e);
		}

		return dictCollectionId;
	}
	
	private static Log _log = LogFactoryUtil.getLog(DictItemsUtils.class);
}
