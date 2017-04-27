package org.opencps.lucenequery.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.opencps.lucenequery.LuceneQueryFormatException;
import org.opencps.lucenequery.LuceneQuerySyntaxException;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.BooleanQueryFactoryUtil;
import com.liferay.portal.kernel.search.ParseException;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author trungnt
 *
 */
public class LuceneQueryUtil {

	/**
	 * @param query
	 * @param key
	 * @param param
	 * @return
	 */
	protected static BooleanQuery addExactTerm(BooleanQuery query, String key,
			Object param) {
		if (param instanceof Long) {
			query.addExactTerm(key, (long) (param));
		} else if (param instanceof Integer) {
			query.addExactTerm(key, (int) (param));
		} else if (param instanceof Short) {
			query.addExactTerm(key, (short) (param));
		} else if (param instanceof Double) {
			query.addExactTerm(key, (double) (param));
		} else if (param instanceof Float) {
			query.addExactTerm(key, (float) (param));
		} else if (param instanceof Boolean) {
			query.addExactTerm(key, (boolean) (param));
		} else if (param instanceof String) {
			query.addExactTerm(key, (String) (param));
		}

		return query;
	}

	/**
	 * @param query
	 * @param key
	 * @param param
	 * @return
	 */
	protected static BooleanQuery addRangeTerm(BooleanQuery query, String key,
			Object param) {
		if (param instanceof long[]) {
			long[] temp = (long[]) param;
			if (temp != null && temp.length == 2) {
				query.addRangeTerm(key, temp[0], temp[1]);
			}

		} else if (param instanceof int[]) {
			int[] temp = (int[]) param;
			if (temp != null && temp.length == 2) {
				query.addRangeTerm(key, temp[0], temp[1]);
			}

		} else if (param instanceof short[]) {
			short[] temp = (short[]) param;
			if (temp != null && temp.length == 2) {
				query.addRangeTerm(key, temp[0], temp[1]);
			}

		} else if (param instanceof Date[]) {
			Date[] temp = (Date[]) param;
			if (temp != null && temp.length == 2) {
				query.addRangeTerm(key, temp[0].getTime(), temp[1].getTime());
			}

		}
		return query;
	}

	/**
	 * @param pattern
	 * @param params
	 * @param searchContext
	 * @return
	 */
	public static BooleanQuery buildQuerySearch(String pattern,
			List<Object> params, SearchContext searchContext) {
		BooleanQuery query = BooleanQueryFactoryUtil.create(searchContext);
		List<String> subQueries = new ArrayList<String>();
		try {
			pattern = validPattern(pattern);

			if (Validator.isNull(pattern)) {
				throw new LuceneQueryFormatException();
			}
			getSubQueries(pattern, subQueries);

			if (subQueries != null && !subQueries.isEmpty()) {
				List<BooleanQuery> booleanQueries = createBooleanQueries(
						subQueries, params, searchContext);

				List<BooleanClauseOccur> conditions = getBooleanClauseOccurs(
						pattern, subQueries);

				if (booleanQueries.size() - 1 != conditions.size()) {
					throw new LuceneQuerySyntaxException();
				}
				int count = 0;
				for (BooleanQuery booleanQuery : booleanQueries) {
					if (count == 0) {
						query.add(booleanQuery, BooleanClauseOccur.MUST);
					} else {
						query.add(booleanQuery, conditions.get(count - 1));
					}

					count++;
				}
			}

		} catch (Exception e) {
			_log.equals(e);
		}

		return query;
	}

	/**
	 * @param subQueries
	 * @param params
	 * @param searchContext
	 * @return
	 * @throws ParseException
	 */
	protected static List<BooleanQuery> createBooleanQueries(
			List<String> subQueries, List<Object> params,
			SearchContext searchContext) throws ParseException {
		List<BooleanQuery> booleanQueries = new ArrayList<BooleanQuery>();
		if (subQueries != null) {
			for (String subQuery : subQueries) {
				String[] terms = StringUtil.split(subQuery);
				if (terms != null && terms.length > 0) {
					BooleanQuery query = BooleanQueryFactoryUtil
							.create(searchContext);
					for (int t = 0; t < terms.length; t++) {
						int paramPossition = subQueries.indexOf(subQuery)
								* terms.length + t;
						String term = terms[t].trim().toLowerCase();
						String key = StringPool.BLANK;
						if (term.contains((StringPool.EQUAL.toLowerCase()))) {
							key = term
									.substring(
											0,
											term.indexOf(StringPool.EQUAL
													.toLowerCase())).trim();
							addExactTerm(query, key, params.get(paramPossition));
						} else if (term.contains(StringPool.LIKE.toLowerCase())) {
							key = term
									.substring(
											0,
											term.indexOf(StringPool.LIKE
													.toLowerCase())).trim();

							query.addTerm(key, params.get(paramPossition)
									.toString(), true);

						} else if (term.contains(StringPool.BETWEEN
								.toLowerCase())) {
							key = term.substring(
									0,
									term.indexOf(StringPool.BETWEEN
											.toLowerCase())).trim();
							query = addRangeTerm(query, key,
									params.get(paramPossition));
						}

					}

					booleanQueries.add(query);
				}
			}
		}
		return booleanQueries;
	}

	protected static List<BooleanClauseOccur> getBooleanClauseOccurs(
			String pattern, List<String> subQueries) {
		// System.out.println(pattern);
		List<BooleanClauseOccur> booleanClauseOccurs = new ArrayList<BooleanClauseOccur>();
		pattern = pattern.replaceAll("\\(", StringPool.BLANK);

		pattern = pattern.replaceAll("\\)", StringPool.BLANK);

		pattern = pattern.replaceAll(StringPool.SPACE, StringPool.BLANK);
		// System.out.println(pattern);
		for (String subQuery : subQueries) {
			subQuery = subQuery.replaceAll(StringPool.SPACE, StringPool.BLANK);
			// System.out.println("sq " + subQuery);
			pattern = pattern.replace(subQuery, StringPool.BLANK);
		}

		// System.out.println(pattern);

		pattern = pattern.replaceAll("\\]\\[", StringPool.COMMA);

		pattern = pattern.replaceAll("\\[", StringPool.BLANK);

		pattern = pattern.replaceAll("\\]", StringPool.BLANK);

		// System.out.println(pattern);

		String[] conditions = StringUtil.split(pattern);

		if (conditions != null && conditions.length > 0) {
			for (int c = 0; c < conditions.length; c++) {
				// System.out.println(conditions[c]);
				if (conditions[c].toLowerCase().equals("and")) {
					booleanClauseOccurs.add(BooleanClauseOccur.MUST);
				} else if (conditions[c].toLowerCase().equals("or")) {
					booleanClauseOccurs.add(BooleanClauseOccur.SHOULD);
				} else if (conditions[c].toLowerCase().equals("not")) {
					booleanClauseOccurs.add(BooleanClauseOccur.MUST_NOT);
				}
			}
		}

		return booleanClauseOccurs;
	}

	/**
	 * @param pattern
	 * @return
	 */
	protected static List<String> getSplitIndex(String pattern) {
		List<String> splitIndexs = new ArrayList<String>();
		int eliminateParenthesis = 0;
		int startIndex = 0;
		int endIndex = 0;

		for (int i = 0; i < pattern.length(); i++) {

			Character c = pattern.charAt(i);

			if (c.toString().equals(StringPool.OPEN_PARENTHESIS)) {
				eliminateParenthesis += 1;
			} else if (c.toString().equals(StringPool.CLOSE_PARENTHESIS)) {
				eliminateParenthesis += -1;
			}

			if (eliminateParenthesis == 1
					&& c.toString().equals(StringPool.OPEN_PARENTHESIS)) {
				startIndex = i;
			}

			if (eliminateParenthesis == 0
					&& c.toString().equals(StringPool.CLOSE_PARENTHESIS)) {
				endIndex = i;

			}

			if (!splitIndexs.contains(startIndex + StringPool.DASH + endIndex)
					&& startIndex < endIndex) {

				splitIndexs.add(startIndex + StringPool.DASH + endIndex);
			}
		}

		return splitIndexs;
	}

	/**
	 * @param pattern
	 * @param subQueries
	 * @return
	 * @throws ParseException
	 */
	public static List<String> getSubQueries(String pattern,
			List<String> subQueries) throws ParseException {

		pattern = validPattern(pattern);

		// if (Validator.isNull(pattern)) {
		// return null;
		// }

		List<String> splitIndexs = getSplitIndex(pattern);

		if (splitIndexs != null) {
			for (String splitIndex : splitIndexs) {

				int[] splitIndexsTemp = StringUtil.split(splitIndex,
						StringPool.DASH, 0);
				String subQuery = pattern.substring(splitIndexsTemp[0],
						splitIndexsTemp[1] + 1);
				if (subQuery.contains("[and]") || subQuery.contains("[or]")
						|| subQuery.contains("[not]")) {
					getSubQueries(subQuery, subQueries);
				} else {
					subQuery = subQuery.replaceAll("\\(", StringPool.BLANK);

					subQuery = subQuery.replaceAll("\\)", StringPool.BLANK);

					subQueries.add(subQuery);

				}
			}
		}

		return subQueries;
	}

	/**
	 * @param pattern
	 * @return
	 */
	protected static String validPattern(String pattern) {
		int eliminateParenthesis = 0;
		int startParenthesisIndex = 0;
		int endParenthesisIndex = 0;
		pattern = pattern.trim().toLowerCase();
		for (int i = 0; i < pattern.length(); i++) {

			Character c = pattern.charAt(i);

			if (c.toString().equals(StringPool.OPEN_PARENTHESIS)) {
				eliminateParenthesis += 1;
			} else if (c.toString().equals(StringPool.CLOSE_PARENTHESIS)) {
				eliminateParenthesis += -1;
			}

			if (eliminateParenthesis == 1
					&& c.toString().equals(StringPool.OPEN_PARENTHESIS)) {
				startParenthesisIndex = i;
			}

			if (eliminateParenthesis == 0
					&& c.toString().equals(StringPool.CLOSE_PARENTHESIS)) {
				endParenthesisIndex = i;
			}

		}

		if (eliminateParenthesis != 0) {
			return StringPool.BLANK;
		}

		if (endParenthesisIndex == pattern.length() - 1
				&& startParenthesisIndex == 0) {
			pattern = pattern.substring(startParenthesisIndex + 1,
					endParenthesisIndex);

			pattern = validPattern(pattern);

		}

		return pattern;
	}

	private static Log _log = LogFactoryUtil.getLog(LuceneQueryUtil.class
			.getName());

}
