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

import java.util.Date;
import java.util.List;

import org.opencps.lucenequery.NoSuchLuceneQueryPatternException;
import org.opencps.lucenequery.model.LuceneQueryPattern;
import org.opencps.lucenequery.service.base.LuceneQueryPatternLocalServiceBaseImpl;

import com.liferay.portal.kernel.exception.SystemException;

/**
 * The implementation of the lucene query pattern local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are
 * added, rerun ServiceBuilder to copy their definitions into the
 * {@link org.opencps.lucenequery.service.LuceneQueryPatternLocalService}
 * interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security
 * checks based on the propagated JAAS credentials because this service can only
 * be accessed from within the same VM.
 * </p>
 *
 * @author trungnt
 * @see org.opencps.lucenequery.service.base.LuceneQueryPatternLocalServiceBaseImpl
 * @see org.opencps.lucenequery.service.LuceneQueryPatternLocalServiceUtil
 */
public class LuceneQueryPatternLocalServiceImpl extends
		LuceneQueryPatternLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 * 
	 * Never reference this interface directly. Always use {@link
	 * org.opencps.lucenequery.service.LuceneQueryPatternLocalServiceUtil} to
	 * access the lucene query pattern local service.
	 */

	/**
	 * @param companyId
	 * @param groupId
	 * @param userId
	 * @param name
	 * @param pattern
	 * @param url
	 * @return
	 * @throws SystemException
	 */
	public LuceneQueryPattern addLuceneQueryPattern(long companyId,
			long groupId, long userId, String name, String pattern, String url)
			throws SystemException {
		long patternId = counterLocalService.increment(LuceneQueryPattern.class
				.getName());
		LuceneQueryPattern luceneQueryPattern = luceneQueryPatternPersistence
				.create(patternId);

		Date now = new Date();

		luceneQueryPattern.setCompanyId(companyId);
		luceneQueryPattern.setCreatedDate(now);
		luceneQueryPattern.setGroupId(groupId);
		luceneQueryPattern.setName(name);
		luceneQueryPattern.setPattern(pattern);
		luceneQueryPattern.setUrl(url);
		luceneQueryPattern.setUserId(userId);
		luceneQueryPattern.setModifiedDate(now);

		return luceneQueryPatternPersistence.update(luceneQueryPattern);
	}

	/**
	 * @param groupId
	 * @return
	 * @throws SystemException
	 */
	public int countByGroupId(long groupId) throws SystemException {
		return luceneQueryPatternPersistence.countByGroupId(groupId);
	}

	/**
	 * @param groupId
	 * @param start
	 * @param end
	 * @return
	 * @throws SystemException
	 */
	public List<LuceneQueryPattern> getLuceneQueryPatternsByGroupId(
			long groupId, int start, int end) throws SystemException {
		return luceneQueryPatternPersistence.findByGroupId(groupId, start, end);
	}

	/**
	 * @param patternId
	 * @param userId
	 * @param name
	 * @param pattern
	 * @param url
	 * @return
	 * @throws SystemException
	 * @throws NoSuchLuceneQueryPatternException
	 */
	public LuceneQueryPattern updateLuceneQueryPattern(long patternId,
			long userId, String name, String pattern, String url)
			throws SystemException, NoSuchLuceneQueryPatternException {

		LuceneQueryPattern luceneQueryPattern = luceneQueryPatternPersistence
				.findByPrimaryKey(patternId);

		Date now = new Date();

		luceneQueryPattern.setName(name);
		luceneQueryPattern.setPattern(pattern);
		luceneQueryPattern.setUrl(url);
		luceneQueryPattern.setUserId(userId);
		luceneQueryPattern.setModifiedDate(now);

		return luceneQueryPatternPersistence.update(luceneQueryPattern);
	}
}