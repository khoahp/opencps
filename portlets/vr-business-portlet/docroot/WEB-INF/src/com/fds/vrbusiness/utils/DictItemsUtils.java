package com.fds.vrbusiness.utils;

import java.util.ArrayList;
import java.util.List;

import org.opencps.datamgt.model.DictCollection;
import org.opencps.datamgt.model.DictItem;
import org.opencps.datamgt.model.DictItemType;
import org.opencps.datamgt.service.DictCollectionLocalServiceUtil;
import org.opencps.datamgt.service.DictItemLocalServiceUtil;
import org.opencps.datamgt.service.DictItemTypeLocalServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
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
			List<DictItemType> dictItemsType = DictItemTypeLocalServiceUtil
					.getBy_IC_CI_CLI(dictItemLinkedCode, dictCollectionId,
							dictCollectionLinkedId);
			
			for (DictItemType dit : dictItemsType) {
				
				DictItem di = getDictItem(dit.getDictItemId());
				
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
	
	/**
	 * @param code
	 * @return
	 */
	public static DictItem getDictItemByCode(String code) {
		DictItem dictItem = null;
		
		try {
			dictItem = DictItemLocalServiceUtil.getDictItemByCode(code);
		} catch (Exception e) {
			_log.error(e);
		}
		
		return dictItem;
	}
	
	/**
	 * @param code
	 * @param vehicleClass
	 * @return
	 */
	public static String getDictItemLinkCode(String code, String vehicleClass) {
		String dictItemLinkCode = StringPool.BLANK;

		DictItem dictItem = getDictItemByCode(code);

		try {
			if (Validator.isNotNull(dictItem)) {

				List<DictItemType> dictItemsType = DictItemTypeLocalServiceUtil
						.getByDictItemId(dictItem.getDictItemId());

				DictItemType dictItemType = null;

				if (dictItemsType.size() != 0) {
					dictItemType = dictItemsType.get(0);

					dictItemLinkCode = dictItemType.getDictItemLinkedCode();
				}

			}
		} catch (Exception e) {
			_log.error(e);
		}

		return dictItemLinkCode;
	}
	
	private static Log _log = LogFactoryUtil.getLog(DictItemsUtils.class);
}
