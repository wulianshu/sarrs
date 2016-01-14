package com.letv.http.parse;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.letv.http.bean.LetvBaseBean;
import com.letv.http.exception.ParseException;

/**
 * 主解析器，封装部分解析方法(面向XML DOM解析)
 * 
 * 对解析方法，暂作包装，对异常处理的策略再定
 * @param <D>
 * */
public abstract class LetvDomParser<T extends LetvBaseBean> extends LetvBaseParser<T , String> {
	
	public LetvDomParser(){
		super(0);
	}
	
	public LetvDomParser(int from){
		super(from);
	}

	@Override
	public T parse(String data) throws ParseException {
		
		DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
		ByteArrayInputStream inputStream = null ;
		//得到DocumentBuilder对象
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			inputStream = new ByteArrayInputStream(data.getBytes());
			Document document=builder.parse(inputStream);
			return handler(document);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(inputStream != null){
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				inputStream = null ;
			}
		}
		return null;
	}
	
	public abstract T handler(Document document);
}
