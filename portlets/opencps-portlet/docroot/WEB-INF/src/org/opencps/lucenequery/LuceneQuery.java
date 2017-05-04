package org.opencps.lucenequery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.opencps.lucenequery.util.LuceneQueryUtil;
import org.opencps.util.DateTimeUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.BooleanQueryFactoryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
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
	private List<Class<?>> _paramTypes;

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

	public LuceneQuery(String pattern, List<String> paramValues,
			List<String> paramTypes, SearchContext searchContext) {

		BooleanQuery query = BooleanQueryFactoryUtil.create(searchContext);
		List<String> subPatterns = new ArrayList<String>();
		List<String> paramNames = new ArrayList<String>();
		List<BooleanClauseOccur> occurs = null;
		List<BooleanQuery> subQueries = null;
		List<Object> params = new ArrayList<Object>();
		List<Class<?>> clazzs = new ArrayList<Class<?>>();

		if (paramValues != null && paramTypes != null
				&& paramValues.size() == paramTypes.size()) {
			try {
				pattern = LuceneQueryUtil.validPattern(pattern);

				if (Validator.isNull(pattern)) {
					throw new LuceneQueryFormatException();
				}

				for (int i = 0; i < paramValues.size(); i++) {
					String paramType = paramTypes.get(i).toLowerCase();
					Object param = null;
					Class<?> clazz = null;
					switch (paramType) {
					case "long":
						param = GetterUtil.getLong(paramValues.get(i));
						clazz = long.class;
						break;
					case "integer":
						param = GetterUtil.getInteger(paramValues.get(i));
						clazz = int.class;
						break;
					case "int":
						param = GetterUtil.getInteger(paramValues.get(i));
						clazz = int.class;
						break;
					case "short":
						param = GetterUtil.getShort(paramValues.get(i));
						clazz = short.class;
						break;
					case "double":
						param = GetterUtil.getDouble(paramValues.get(i));
						clazz = double.class;
						break;
					case "float":
						param = GetterUtil.getFloat(paramValues.get(i));
						clazz = float.class;
						break;
					case "boolean":
						param = GetterUtil.getBoolean(paramValues.get(i));
						clazz = boolean.class;
						break;
					case "date":
						param = DateTimeUtil.convertStringToDate(paramValues
								.get(i));
						clazz = Date.class;
						break;
					case "string":
						param = GetterUtil.getString(paramValues.get(i));
						clazz = String.class;
						break;
					case "null":
						param = null;
						clazz = null;
						break;
					case "":
						param = null;
						clazz = null;
						break;
					case " ":
						param = null;
						clazz = null;
						break;
					default:
						break;
					}

					params.add(param);
					clazzs.add(clazz);
				}

				LuceneQueryUtil.getSubQueries(pattern, subPatterns);

				if (subPatterns != null && !subPatterns.isEmpty()) {
					subQueries = LuceneQueryUtil.createBooleanQueries(
							subPatterns, params, paramNames, searchContext);

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
				this.setParamTypes(clazzs);
			}
		} else {
			_log.info("########################################## Can not compare menu pattern "
					+ pattern
					+ " : because paramNames and paramTypes not proportional");
		}

	}

	public LuceneQuery(String pattern, String paramValues, String paramTypes,
			SearchContext searchContext) {

		BooleanQuery query = BooleanQueryFactoryUtil.create(searchContext);
		List<String> subPatterns = new ArrayList<String>();
		List<String> paramNames = new ArrayList<String>();
		List<BooleanClauseOccur> occurs = null;
		List<BooleanQuery> subQueries = null;
		List<Object> params = new ArrayList<Object>();
		List<Class<?>> clazzs = new ArrayList<Class<?>>();

		String[] arrParamValue = Validator.isNotNull(paramValues) ? StringUtil
				.split(paramValues) : null;
		String[] arrParamTypes = Validator.isNotNull(paramTypes) ? StringUtil
				.split(paramTypes) : null;

		if (arrParamValue != null && arrParamTypes != null
				&& arrParamTypes.length > 0 && arrParamValue.length > 0
				&& arrParamValue.length == arrParamTypes.length) {
			try {
				pattern = LuceneQueryUtil.validPattern(pattern);

				if (Validator.isNull(pattern)) {
					throw new LuceneQueryFormatException();
				}

				for (int i = 0; i < arrParamValue.length; i++) {
					String paramType = arrParamTypes[i].toLowerCase();
					Object param = null;
					Class<?> clazz = null;
					switch (paramType) {
					case "long":
						param = GetterUtil.getLong(arrParamValue[i]);
						clazz = long.class;
						break;
					case "integer":
						param = GetterUtil.getInteger(arrParamValue[i]);
						clazz = int.class;
						break;
					case "int":
						param = GetterUtil.getInteger(arrParamValue[i]);
						clazz = int.class;
						break;
					case "short":
						param = GetterUtil.getShort(arrParamValue[i]);
						clazz = short.class;
						break;
					case "double":
						param = GetterUtil.getDouble(arrParamValue[i]);
						clazz = double.class;
						break;
					case "float":
						param = GetterUtil.getFloat(arrParamValue[i]);
						clazz = float.class;
						break;
					case "boolean":
						param = GetterUtil.getBoolean(arrParamValue[i]);
						clazz = boolean.class;
						break;
					case "date":
						param = DateTimeUtil
								.convertStringToDate(arrParamValue[i]);
						clazz = Date.class;
						break;
					case "string":
						param = GetterUtil.getString(arrParamValue[i]);
						clazz = String.class;
						break;
					case "null":
						param = null;
						clazz = null;
						break;
					case "":
						param = null;
						clazz = null;
						break;
					case " ":
						param = null;
						clazz = null;
						break;
					default:
						break;
					}

					params.add(param);
					clazzs.add(clazz);
				}

				LuceneQueryUtil.getSubQueries(pattern, subPatterns);

				if (subPatterns != null && !subPatterns.isEmpty()) {
					subQueries = LuceneQueryUtil.createBooleanQueries(
							subPatterns, params, paramNames, searchContext);

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
				this.setParamTypes(clazzs);
			}
		} else {
			_log.info("########################################## Can not compare menu pattern "
					+ pattern
					+ " : because paramNames and paramTypes not proportional");
		}

	}

	public List<Class<?>> getParamTypes() {
		return _paramTypes;
	}

	public void setParamTypes(List<Class<?>> paramTypes) {
		this._paramTypes = paramTypes;
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
