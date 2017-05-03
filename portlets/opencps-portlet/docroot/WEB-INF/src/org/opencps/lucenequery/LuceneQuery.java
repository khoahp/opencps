package org.opencps.lucenequery;

import java.util.ArrayList;
import java.util.List;

import org.opencps.lucenequery.util.LuceneQueryUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.BooleanQueryFactoryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.Validator;

public class LuceneQuery {
	private SearchContext _searchContext;
	private String _pattern;
	private BooleanQuery _query;
	private List<BooleanQuery> _subQueries;
	private List<String> _subPatterns;
	private List<String> _paramNames;
	private List<Object> _params;
	private List<BooleanClauseOccur> _occurs;

	public LuceneQuery(String pattern, List<Object> params,
			SearchContext searchContext) {
		BooleanQuery query = BooleanQueryFactoryUtil.create(searchContext);
		List<String> subPatterns = new ArrayList<String>();
		List<String> paramNames = new ArrayList<String>();
		List<BooleanClauseOccur> occurs = null;
		List<BooleanQuery> subQueries = null;
		try {
			pattern = LuceneQueryUtil.validPattern(pattern);

			if (Validator.isNull(pattern)) {
				throw new LuceneQueryFormatException();
			}

			LuceneQueryUtil.getSubQueries(pattern, subPatterns);

			if (subPatterns != null && !subPatterns.isEmpty()) {
				subQueries = LuceneQueryUtil.createBooleanQueries(subPatterns,
						params, paramNames, searchContext);

				occurs = LuceneQueryUtil.getBooleanClauseOccurs(pattern,
						subPatterns);

				if (subQueries.size() - 1 != occurs.size()) {
					throw new LuceneQuerySyntaxException();
				}
				int count = 0;
				for (BooleanQuery booleanQuery : subQueries) {
					if (count == 0) {
						query.add(booleanQuery, BooleanClauseOccur.MUST);
					} else {
						query.add(booleanQuery, occurs.get(count - 1));
					}

					count++;
				}
			}

		} catch (Exception e) {
			_log.error(e);
		} finally {
			this.setOccurs(occurs);
			this.setParams(params);
			this.setPattern(pattern);
			this.setQuery(query);
			this.setSubPatterns(subPatterns);
			this.setSubQueries(subQueries);
			this.setSearchContext(searchContext);
			this.setParamNames(paramNames);
		}
	}

	public SearchContext getSearchContext() {
		return _searchContext;
	}

	public void setSearchContext(SearchContext searchContext) {
		this._searchContext = searchContext;
	}

	public String getPattern() {
		return _pattern;
	}

	public void setPattern(String pattern) {
		this._pattern = pattern;
	}

	public BooleanQuery getQuery() {
		return _query;
	}

	public void setQuery(BooleanQuery query) {
		this._query = query;
	}

	public List<BooleanQuery> getSubQueries() {
		return _subQueries;
	}

	public void setSubQueries(List<BooleanQuery> subQueries) {
		this._subQueries = subQueries;
	}

	public List<String> getSubPatterns() {
		return _subPatterns;
	}

	public void setSubPatterns(List<String> subPatterns) {
		this._subPatterns = subPatterns;
	}

	public List<String> getParamNames() {
		return _paramNames;
	}

	public void setParamNames(List<String> paramNames) {
		this._paramNames = paramNames;
	}

	public List<Object> getParams() {
		return _params;
	}

	public void setParams(List<Object> params) {
		this._params = params;
	}

	public List<BooleanClauseOccur> getOccurs() {
		return _occurs;
	}

	public void setOccurs(List<BooleanClauseOccur> occurs) {
		this._occurs = occurs;
	}

	private Log _log = LogFactoryUtil.getLog(LuceneQuery.class.getName());
}
