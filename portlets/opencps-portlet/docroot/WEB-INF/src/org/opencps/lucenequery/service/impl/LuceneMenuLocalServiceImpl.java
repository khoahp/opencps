/**
 * OpenCPS is the open source Core Public Services software
 * Copyright (C) 2016-present OpenCPS community
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>
 */

package org.opencps.lucenequery.service.impl;

import java.util.List;

import org.opencps.lucenequery.model.LuceneMenu;
import org.opencps.lucenequery.service.base.LuceneMenuLocalServiceBaseImpl;
import com.liferay.portal.kernel.exception.SystemException;

/**
 * The implementation of the lucene menu local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are
 * added, rerun ServiceBuilder to copy their definitions into the
 * {@link org.opencps.lucenequery.service.LuceneMenuLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security
 * checks based on the propagated JAAS credentials because this service can only
 * be accessed from within the same VM.
 * </p>
 *
 * @author trungnt
 * @see org.opencps.lucenequery.service.base.LuceneMenuLocalServiceBaseImpl
 * @see org.opencps.lucenequery.service.LuceneMenuLocalServiceUtil
 */
public class LuceneMenuLocalServiceImpl extends LuceneMenuLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 * 
	 * Never reference this interface directly. Always use {@link
	 * org.opencps.lucenequery.service.LuceneMenuLocalServiceUtil} to access the
	 * lucene menu local service.
	 */

	/**
	 * @param groupId
	 * @param menuGroupId
	 * @return
	 * @throws SystemException
	 */
	public int countByGroupIdAndMenuGroupId(long groupId, long menuGroupId)
			throws SystemException {
		return luceneMenuPersistence.countByGroupIdAndMenuGroupId(groupId,
				menuGroupId);
	}

	/**
	 * @param groupId
	 * @param menuGroupId
	 * @return
	 * @throws SystemException
	 */
	public List<LuceneMenu> getLuceneMenusByGroupIdAndMenuGroupId(long groupId,
			long menuGroupId) throws SystemException {
		return luceneMenuPersistence.findByGroupIdAndMenuGroupId(groupId,
				menuGroupId);
	}

	/**
	 * @param groupId
	 * @param menuGroupId
	 * @param level
	 * @return
	 * @throws SystemException
	 */
	public List<LuceneMenu> getLuceneMenusByG_MG_L(long groupId,
			long menuGroupId, int level) throws SystemException {
		return luceneMenuPersistence.findByG_MG_L(groupId, menuGroupId, level);
	}
	
	
	/**
	 * @param groupId
	 * @param menuGroupId
	 * @param parentId
	 * @return
	 * @throws SystemException
	 */
	public List<LuceneMenu> getLuceneMenusByG_MG_P(long groupId,
			long menuGroupId, long parentId) throws SystemException {
		return luceneMenuPersistence.findByG_MG_P(groupId, menuGroupId,
				parentId);
	}

	/**
	 * @param groupId
	 * @param menuGroupId
	 * @param start
	 * @param end
	 * @return
	 * @throws SystemException
	 */
	public List<LuceneMenu> getLuceneMenusByGroupIdAndMenuGroupId(long groupId,
			long menuGroupId, int start, int end) throws SystemException {
		return luceneMenuPersistence.findByGroupIdAndMenuGroupId(groupId,
				menuGroupId, start, end);
	}
}