package com.chaojishipin.sarrs.bean;

import com.letv.http.bean.LetvBaseBean;

import java.util.ArrayList;

/**
 * @author daipei
 */
public class SearchKeyWords implements LetvBaseBean {

	private static final long serialVersionUID = 629851648141396149L;
	private ArrayList<String> words = new ArrayList<String>();

	public ArrayList<String> getWords() {
		return words;
	}

	public void setWords(ArrayList<String> words) {
		this.words = words;
	}
	public void addWords(String word){
		this.words.add(word);
	}

}
