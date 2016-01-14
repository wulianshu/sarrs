package com.letv.http.impl;

import java.io.IOException;

import com.letv.http.LetvHttpJavaHandler;
import com.letv.http.LetvHttpLog;
import com.letv.http.bean.LetvBaseBean;
import com.letv.http.bean.LetvDataHull;
import com.letv.http.exception.DataIsErrException;
import com.letv.http.exception.DataIsNullException;
import com.letv.http.exception.DataNoUpdateException;
import com.letv.http.exception.JsonCanNotParseException;
import com.letv.http.exception.ParseException;
import com.letv.http.parse.LetvBaseParser;

/**
 * 请求封装内
 * */
public class LetvHttpTool<T extends LetvBaseBean> {
	
	public <D> LetvDataHull<T> requsetData(LetvHttpBaseParameter<T, D, ?> httpParameter) {
		LetvDataHull<T> dataHull;
		if (httpParameter == null) {
			dataHull = new LetvDataHull<T>();
			dataHull.setDataType(LetvDataHull.DataType.PARAMS_IS_NULL);
			LetvHttpLog.Err("Parameter is null");
			return dataHull;
		}

		if (httpParameter.getType() == LetvHttpParameter.Type.GET) {
			dataHull = doGet(httpParameter);
		} else if (httpParameter.getType() == LetvHttpParameter.Type.POST) {
			dataHull = doPost(httpParameter);
		} else {
			dataHull = new LetvDataHull<T>();
			dataHull.setDataType(LetvDataHull.DataType.REQUESTMETHOD_IS_ERR);
			LetvHttpLog.Err("RequestMethod is error");
			return dataHull;
		}
		dataHull.setUpdataId(httpParameter.getUpdataId());

		return dataHull;
	}

	private <D> LetvDataHull<T> doGet(LetvHttpBaseParameter<T, D, ?> httpParameter) {
		LetvBaseParser<T, D> parser = httpParameter.getParser();
		String response = null ;
		LetvDataHull<T> dataHull = new LetvDataHull<T>();
		
		try {
			LetvHttpJavaHandler handler = new LetvHttpJavaHandler();
			response = handler.doGet(httpParameter);
			if (parser != null) {
				dataHull.setDataEntity(parser.initialParse(response));
				dataHull.setDataType(LetvDataHull.DataType.DATA_IS_INTEGRITY);
				dataHull.setSourceData(response);
				LetvHttpLog.Err("complete!");
				
				return dataHull;
			} else {
				dataHull.setDataType(LetvDataHull.DataType.DATA_PARSER_IS_NULL);
				LetvHttpLog.Err("Do not have parser");
			}
		} catch (IOException e) {
			e.printStackTrace();
			dataHull.setDataType(LetvDataHull.DataType.CONNECTION_FAIL);
			LetvHttpLog.Err("connected is fail");
		} catch (ParseException e) {
			e.printStackTrace();
			dataHull.setDataType(LetvDataHull.DataType.DATA_PARSE_EXCEPTION);
			LetvHttpLog.Err("parse error");
		} catch (DataIsNullException e) {
			e.printStackTrace();
			dataHull.setDataType(LetvDataHull.DataType.DATA_IS_NULL);
			LetvHttpLog.Err("data is null");
		} catch (JsonCanNotParseException e) {
			e.printStackTrace();
			dataHull.setDataType(LetvDataHull.DataType.DATA_CAN_NOT_PARSE);
			dataHull.setErrMsg(parser.getErrorMsg());
			LetvHttpLog.Err("canParse is false");
		} catch (DataIsErrException e) {
			e.printStackTrace();
			dataHull.setDataType(LetvDataHull.DataType.DATA_IS_ERR);
			LetvHttpLog.Err("data is err");
		} catch (DataNoUpdateException e) {
			e.printStackTrace();
			dataHull.setDataType(LetvDataHull.DataType.DATA_NO_UPDATE);
			LetvHttpLog.Err("data has not update");
		} finally {
			if (parser != null) {
				dataHull.setMessage(parser.getMessage());
			}
		}

		return dataHull;
	}

	private <D> LetvDataHull<T> doPost(LetvHttpBaseParameter<T, D, ?> httpParameter) {
		LetvBaseParser<T, D> parser = httpParameter.getParser();
		String response = null ;
		LetvDataHull<T> dataHull = new LetvDataHull<T>();
		
		try {
			LetvHttpJavaHandler handler = new LetvHttpJavaHandler();
			response = handler.doPost(httpParameter);

			if (parser != null) {
				dataHull.setDataType(LetvDataHull.DataType.DATA_IS_INTEGRITY);
				dataHull.setDataEntity(parser.initialParse(response));
				dataHull.setSourceData(response);
				LetvHttpLog.Err("complete!");
				
				return dataHull;
			} else {
				dataHull.setDataType(LetvDataHull.DataType.DATA_PARSER_IS_NULL);
				LetvHttpLog.Err("Do not have parser");
			}
		} catch (IOException e) {
			e.printStackTrace();
			dataHull.setDataType(LetvDataHull.DataType.CONNECTION_FAIL);
			LetvHttpLog.Err("connected is fail");
		} catch (ParseException e) {
			e.printStackTrace();
			dataHull.setDataType(LetvDataHull.DataType.DATA_PARSE_EXCEPTION);
			LetvHttpLog.Err("parse error");
		} catch (DataIsNullException e) {
			e.printStackTrace();
			dataHull.setDataType(LetvDataHull.DataType.DATA_IS_NULL);
			LetvHttpLog.Err("data is null");
		} catch (JsonCanNotParseException e) {
			e.printStackTrace();
			dataHull.setDataType(LetvDataHull.DataType.DATA_CAN_NOT_PARSE);
			dataHull.setErrMsg(parser.getErrorMsg());
			LetvHttpLog.Err("canParse is false");
		} catch (DataIsErrException e) {
			e.printStackTrace();
			dataHull.setDataType(LetvDataHull.DataType.DATA_IS_ERR);
			LetvHttpLog.Err("data is err");
		} catch (DataNoUpdateException e) {
			e.printStackTrace();
			dataHull.setDataType(LetvDataHull.DataType.DATA_NO_UPDATE);
			LetvHttpLog.Err("data has not update");
		} finally {
			if (parser != null) {
				dataHull.setMessage(parser.getMessage());
			}
		}

		return dataHull;
	}
}
