package com.chaojishipin.sarrs.bean;

import java.util.HashMap;

import com.letv.http.bean.LetvBaseBean;

public class VStreamInfoList implements LetvBaseBean {

	/**
	 * 码流信息列表
	 */
	private static final long serialVersionUID = 7092778219919088871L;
	
	/**
	 * 码流信息与类型对应的map
	 */
	private HashMap<String, VStreamInfo> map = new HashMap<String, VStreamInfo>() ;
	
	private String [] types;
	
	public void setTypes(String[] types) {
		this.types = types;
	}

	public HashMap<String, VStreamInfo> getMap() {
		return map;
	}

	public void setMap(HashMap<String, VStreamInfo> map) {
		this.map = map;
	}

	public String [] getTypes() {
		return types;
	}

	public void put(String key, VStreamInfo value){
		map.put(key, value);
	}
	
	public VStreamInfo get(String key) {
		return map.get(key);
	}
	
	public boolean isContains(String type){
		return map.containsKey(type);
	}


}
