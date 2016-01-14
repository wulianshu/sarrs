package com.chaojishipin.sarrs.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class JsonUtil {

	/**
	 * @param text
	 * @return
	 * 把JSON文本parse为JSONObject或者JSONArray
	 */
	public static Object parse(String text) {
		
		return JSONObject.parse(text);
		
	}

	/**
	 * @param text
	 * @return
	 * 把JSON文本parse成JSONObject
	 */
	public static JSONObject parseObject(String text) {
		 
		return JSONObject.parseObject(text);
		
	}

	/**
	 * @param text
	 * @param clazz
	 * @return
	 * 把JSON文本parse为JavaBean
	 */
	public static <T> T parseObject(String text, Class<T> clazz) {
		
		return JSONObject.parseObject(text, clazz);
		
	}

	/**
	 * @param text
	 * @return
	 * 把JSON文本parse成JSONArray
	 */
	public static JSONArray parseArray(String text) {
		 
		return JSONObject.parseArray(text);
		
	}

	/**
	 * @param text
	 * @param clazz
	 * @return
	 * 把JSON文本parse成JavaBean集合
	 */
	public static <T> List<T> parseArray(String text, Class<T> clazz) {
		
		return JSONObject.parseArray(text, clazz);
		 
	}

	/**
	 * @param object
	 * @return 
	 * 将JavaBean序列化为JSON文本
	 */
	public static String toJSONString(Object object) {
		
		return JSONObject.toJSONString(object);
		 
	}

	/**
	 * @param object
	 * @param prettyFormat
	 * @return 
	 * 将JavaBean序列化为带格式的JSON文本
	 */
	public static String toJSONString(Object object, boolean prettyFormat) {
		
		return JSONObject.toJSONString(object, prettyFormat);
		 
	}

	/**
	 * @param javaObject
	 * @return 
	 * 将JavaBean转换为JSONObject或者JSONArray
	 */
	public static Object toJSON(Object javaObject){
		
		return JSONObject.toJSON(javaObject);
		
	}
}
