package com.letv.http.parse;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import com.letv.http.bean.LetvBaseBean;
import com.letv.http.exception.ParseException;

/**
 * 主解析器，封装部分解析方法(面向XML SAX解析)
 * 
 * 对解析方法，暂作包装，对异常处理的策略再定
 * @param <D>
 * */
public abstract class LetvSAXParser<T extends LetvBaseBean> extends LetvBaseParser<T , String> implements EntityResolver, DTDHandler, ContentHandler, ErrorHandler{
	
	public LetvSAXParser(){
		super(0);
	}
	
	public LetvSAXParser(int from){
		super(from);
	}

	@Override
	public T parse(String data) throws ParseException {
		StringReader stringReader = null;
		InputSource inputSource;
			
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		SAXParser saxParser;
		try {
			saxParser = saxParserFactory.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			xmlReader.setContentHandler(this);
			stringReader = new StringReader(data);
			inputSource = new InputSource(stringReader);
			xmlReader.parse(inputSource);
			return getDataEntity();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(stringReader != null){
				try{
					stringReader.close();
				}catch(Exception e){
					e.printStackTrace();
				}
				stringReader = null ;
				inputSource = null ;
			}
		}
		return null ;
	}
	
	@Override
	public abstract void startDocument() throws SAXException ;

	@Override
	public abstract void endDocument() throws SAXException ;
	
	@Override
	public abstract void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException ;

	@Override
	public abstract void endElement(String uri, String localName, String qName) throws SAXException ;

	@Override
	public abstract void characters(char[] ch, int start, int length) throws SAXException ;
	
	public abstract T getDataEntity();
	
	@Override
	public void warning(SAXParseException exception) throws SAXException {
	}

	@Override
	public void error(SAXParseException exception) throws SAXException {
	}

	@Override
	public void fatalError(SAXParseException exception) throws SAXException {
	}

	@Override
	public void setDocumentLocator(Locator locator) {
	}

	@Override
	public void startPrefixMapping(String prefix, String uri) throws SAXException {
	}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
	}

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
	}

	@Override
	public void processingInstruction(String target, String data) throws SAXException {
	}

	@Override
	public void skippedEntity(String name) throws SAXException {
	}

	@Override
	public void notationDecl(String name, String publicId, String systemId) throws SAXException {
	}

	@Override
	public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName)
			throws SAXException {
	}

	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		return null;
	}
}
