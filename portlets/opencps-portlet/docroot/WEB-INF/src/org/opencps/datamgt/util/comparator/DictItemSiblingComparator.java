package org.opencps.datamgt.util.comparator;

import org.opencps.datamgt.model.DictCollection;

import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.OrderByComparator;

/**
 * @author phucnv
 * @date Apr 26, 2017
 */
public class DictItemSiblingComparator extends OrderByComparator {

	private static final long serialVersionUID = 1L;

	public static final String ORDER_BY_ASC = "sibling ASC";

	public static final String ORDER_BY_DESC = "sibling DESC";

	public static final String[] ORDER_BY_FIELDS = { "sibling" };

	public DictItemSiblingComparator() {
		this(false);
	}

	public DictItemSiblingComparator(boolean ascending) {
		_ascending = ascending;
	}

	@Override
	public int compare(Object obj1, Object obj2) {

		DictCollection dictCollection1 = (DictCollection) obj1;
		DictCollection dictCollection2 = (DictCollection) obj2;

		int value = DateUtil.compareTo(dictCollection1.getCreateDate(),
				dictCollection2.getCreateDate());

		if (_ascending) {
			return value;
		} else {
			return -value;
		}
	}

	@Override
	public String getOrderBy() {

		if (_ascending) {
			return ORDER_BY_ASC;
		} else {
			return ORDER_BY_DESC;
		}
	}

	@Override
	public String[] getOrderByFields() {

		return ORDER_BY_FIELDS;
	}

	@Override
	public boolean isAscending() {

		return _ascending;
	}

	private boolean _ascending;

}
