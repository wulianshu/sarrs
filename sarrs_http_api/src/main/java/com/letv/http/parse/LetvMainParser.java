package com.letv.http.parse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.letv.http.bean.LetvBaseBean;

/**
 * 主解析器，封装部分解析方法(面向JSON 解析)
 * 
 * 对解析方法，暂作包装，对异常处理的策略再定
 * 
 * @param <D>
 * */
public abstract class LetvMainParser<T extends LetvBaseBean, D> extends LetvBaseParser<T, D> {

	public LetvMainParser() {
		super(0);
	}

	public LetvMainParser(int from) {
		super(from);
	}

	public boolean has(JSONObject jsonObject, String name) {
		if (jsonObject == null) {
			return false;
		}
		return jsonObject.has(name);
	}

	protected int getInt(JSONObject jsonObject, String name) throws JSONException {

		int value = -1;
		String valueString = getString(jsonObject, name);
		if (!TextUtils.isEmpty(valueString)) {
			value = Integer.parseInt(valueString);
		}

		return value;
	}

	protected int getInt(JSONArray jsonArray, int index) throws JSONException {

		int value = -1;

		String valueString = getString(jsonArray, index);
		if (!TextUtils.isEmpty(valueString)) {
			value = Integer.parseInt(valueString);
		}

		return value;
	}

	protected long getLong(JSONObject jsonObject, String name) throws JSONException {

		long value = -1;
		String valueString = getString(jsonObject, name);
		if (!TextUtils.isEmpty(valueString)) {
			value = Long.parseLong(valueString);
		}

		return value;
	}

	protected long getLong(JSONArray jsonArray, int index) throws JSONException {

		long value = -1;

		String valueString = getString(jsonArray, index);
		if (!TextUtils.isEmpty(valueString)) {
			value = Long.parseLong(valueString);
		}

		return value;
	}

	protected boolean getBoolean(JSONObject jsonObject, String name) throws JSONException {

		boolean value = false;
		value = jsonObject.getBoolean(name);

		return value;
	}

	protected boolean getBoolean(JSONArray jsonArray, int index) throws JSONException {

		boolean value = false;

		value = jsonArray.getBoolean(index);

		return value;
	}

	protected float getFloat(JSONObject jsonObject, String name) throws JSONException {

		float value = -1;
		String valueString = getString(jsonObject, name);
		if (!TextUtils.isEmpty(valueString)) {
			try {
				value = Float.parseFloat(valueString);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return value;
	}

	protected float getFloat(JSONArray jsonArray, int index) throws JSONException {

		float value = -1;
		String valueString = getString(jsonArray, index);
		if (!TextUtils.isEmpty(valueString)) {
			try {
				value = Float.parseFloat(valueString);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return value;
	}

	protected String getString(JSONObject jsonObject, String name) throws JSONException {

		String value = "";

		if (!jsonObject.has(name)) {
			return value;
		}

		value = jsonObject.getString(name);

		if ("null".equalsIgnoreCase(value)) {
			value = "";
		}

		return value;
	}

	protected String getString(JSONArray jsonArray, int index) throws JSONException {

		String value = "";
		value = jsonArray.getString(index);

		if ("null".equalsIgnoreCase(value)) {
			value = "";
		}

		return value;
	}

	protected JSONArray getJSONArray(JSONObject jsonObject, String name) throws JSONException {

		if (jsonObject == null) {
			throw new JSONException("JSONObject is null");
		}

		JSONArray array = jsonObject.getJSONArray(name);

		return array;
	}

	protected JSONArray getJSONArray(JSONArray jsonArray, int index) throws JSONException {

		if (jsonArray == null) {
			throw new JSONException("JSONArray is null");
		}

		JSONArray array = jsonArray.getJSONArray(index);

		return array;
	}

	protected JSONObject getJSONObject(JSONObject jsonObject, String name) throws JSONException {

		if (jsonObject == null) {
			throw new JSONException("JSONObject is null");
		}

		JSONObject object = jsonObject.getJSONObject(name);

		return object;
	}

	protected JSONObject getJSONObject(JSONArray jsonArray, int index) throws JSONException {

		if (jsonArray == null) {
			throw new JSONException("JSONArray is null");
		}

		JSONObject object = jsonArray.getJSONObject(index);

		return object;
	}
}
