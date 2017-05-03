package org.opencps.lucenequery.menu.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.opencps.util.DateTimeUtil;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author trungnt
 *
 */
public class LuceneMenuSchema {
	private int _level;
	private String _name;
	private List<Object> _params;
	private List<Class<?>> _paramTypes;
	private String _pattern;
	private String _strParam;
	private String _strParamType;

	public LuceneMenuSchema() {
		// TODO Auto-generated constructor stub
	}

	public LuceneMenuSchema(int level) {
		this.setLevel(level);
	}

	public LuceneMenuSchema(String name, int level, String pattern,
			String params, String paramTypes) {
		this.setName(name);
		this.setPattern(pattern);
		this.setLevel(level);
		this.setStringParam(params);
		this.setStringParamType(paramTypes);
		String[] arrParam = null;
		String[] arrParamType = null;
		if (Validator.isNotNull(params)) {
			arrParam = StringUtil.split(params);
		}
		if (Validator.isNotNull(paramTypes)) {
			arrParamType = StringUtil.split(paramTypes);
		}

		if (arrParam != null && arrParamType != null && arrParam.length > 0
				&& arrParamType.length > 0
				&& arrParam.length == arrParamType.length) {
			List<Object> objects = new ArrayList<Object>();
			List<Class<?>> clazzs = new ArrayList<Class<?>>();
			for (int i = 0; i < arrParam.length; i++) {
				String paramType = arrParamType[i].toLowerCase();
				Object param = null;
				Class<?> clazz = null;
				switch (paramType) {
				case "long":
					param = GetterUtil.getLong(arrParam[i]);
					clazz = long.class;
					break;
				case "integer":
					param = GetterUtil.getInteger(arrParam[i]);
					clazz = int.class;
					break;
				case "int":
					param = GetterUtil.getInteger(arrParam[i]);
					clazz = int.class;
					break;
				case "short":
					param = GetterUtil.getShort(arrParam[i]);
					clazz = short.class;
					break;
				case "double":
					param = GetterUtil.getDouble(arrParam[i]);
					clazz = double.class;
					break;
				case "float":
					param = GetterUtil.getFloat(arrParam[i]);
					clazz = float.class;
					break;
				case "boolean":
					param = GetterUtil.getBoolean(arrParam[i]);
					clazz = boolean.class;
					break;
				case "date":
					param = DateTimeUtil.convertStringToDate(arrParam[i]);
					clazz = Date.class;
					break;
				case "string":
					param = GetterUtil.getString(arrParam[i]);
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

				objects.add(param);
				clazzs.add(clazz);
			}

			this.setParams(objects);
			this.setParamTypes(clazzs);
		}
	}

	public int getLevel() {
		return _level;
	}

	public String getName() {
		return _name;
	}

	public List<Object> getParams() {
		return _params;
	}

	public List<Class<?>> getParamTypes() {
		return _paramTypes;
	}

	public String getPattern() {
		return _pattern;
	}

	public String getStringParam() {
		return _strParam;
	}

	public String getStringParamType() {
		return _strParamType;
	}

	public void setLevel(int level) {
		this._level = level;
	}

	public void setName(String name) {
		this._name = name;
	}

	public void setParams(List<Object> params) {
		this._params = params;
	}

	public void setParamTypes(List<Class<?>> paramTypes) {
		this._paramTypes = paramTypes;
	}

	public void setPattern(String pattern) {
		this._pattern = pattern;
	}

	public void setStringParam(String strParam) {
		this._strParam = strParam;
	}

	public void setStringParamType(String strParamType) {
		this._strParamType = strParamType;
	}
}
