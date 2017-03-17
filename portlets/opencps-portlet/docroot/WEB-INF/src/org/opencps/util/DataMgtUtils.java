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

package org.opencps.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.opencps.datamgt.model.DictCollection;
import org.opencps.datamgt.model.DictItem;
import org.opencps.datamgt.model.impl.DictCollectionImpl;
import org.opencps.datamgt.service.DictCollectionLocalServiceUtil;
import org.opencps.datamgt.service.DictItemLocalServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author nhanhoang
 * 
 */

public class DataMgtUtils{
	
	private static Log _log = LogFactoryUtil
		    .getLog(DataMgtUtils.class);
	
	public List<DictItem> getDictItemList(long groupId,String collectionCode){
		
		try {

			DictCollection dictCollection = null;

			dictCollection = DictCollectionLocalServiceUtil.getDictCollection(
					groupId, collectionCode);

			if (Validator.isNotNull(dictCollection)) {

				List<DictItem> dictItems = new ArrayList<DictItem>();
				
				dictItems = DictItemLocalServiceUtil.getDictItemsByDictCollectionId(dictCollection.getDictCollectionId());
				
				return dictItems;

			}
		} catch (Exception e) {
			_log.error(e);
		}
		
		return new ArrayList<DictItem>();
	}
	
	public static String getDictItemName(long groupId, String collectionCode,
			String itemCode, Locale locale) {

		try {

			DictCollection dictCollection = null;

			dictCollection = DictCollectionLocalServiceUtil.getDictCollection(
					groupId, collectionCode);

			if (Validator.isNotNull(dictCollection)) {

				DictItem dictItem = null;

				dictItem = DictItemLocalServiceUtil.getDictItemInuseByItemCode(
						dictCollection.getDictCollectionId(), itemCode);

				if (Validator.isNotNull(dictItem)) {

					return dictItem.getItemName(locale);
				}

			}
		} catch (Exception e) {
			_log.error(e);
		}

		return StringPool.BLANK;
	}

}