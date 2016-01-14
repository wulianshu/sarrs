package com.letv.http.bean;

/**
 * 请求壳对象
 * 封装：
 * 对象实体
 * 对象状态
 * 回调更新ID
 * 原始数据（调试情况下保留）
 * */
public class LetvDataHull <T extends LetvBaseBean>{

	/**
	 * 请求数据到分派给解析器解析，所有状态
	 * 
	 * DATA_IS_NULL   请求返回数据是空串或NULL时
	 * 
	 * DATA_PARSE_EXCEPTION   在进入解析器后，parse方法在解析异常时
	 * 
	 * CONNECTION_FAIL   请求网络时，连接失败或超时，IO异常时
	 * 
	 * DATA_IS_INTEGRITY    没有出现任何异常，完整完成整个过程时
	 * 
	 * PARAMS_IS_NULL    参数为空时
	 * 
	 * REQUESTMETHOD_IS_ERR    请求不是get或post时
	 * 
	 * DATA_PARSER_IS_NULL    传入的解析器对象为空时
	 * 
	 * DATA_CAN_NOT_PARSE    数据对象不满足解析器 canParse验证时
	 * 
	 * */
	public interface DataType {
		/**
		 * 数据为空
		 * */
		public int DATA_IS_NULL = 0x100;
		/**
		 * 数据解析错误
		 * */
		public int DATA_PARSE_EXCEPTION = 0x101;
		/**
		 * 连接失败
		 * */
		public int CONNECTION_FAIL = 0x102;
		/**
		 * 数据完整
		 * */
		public int DATA_IS_INTEGRITY = 0x103;
		/**
		 * 请求参数为空
		 * */
		public int PARAMS_IS_NULL = 0x104;
		/**
		 * 请求方式不正确
		 * */
		public int REQUESTMETHOD_IS_ERR = 0x105;
		/**
		 * 解析器为空
		 * */
		public int DATA_PARSER_IS_NULL = 0x106;
		/**
		 * 数据不符合解析头文件判断
		 * */
		public int DATA_CAN_NOT_PARSE = 0x107;
		/**
		 * 为解析方法提供元数据时，错误
		 * */
		public int DATA_IS_ERR = 0x108;
		/**
		 * 接口数据无更新
		 * */
		public int DATA_NO_UPDATE = 0x109;
	}

	/**
	 * 数据状态
	 * */
	private int dataType;

	/**
	 * 请求返回的实体
	 * */
	private T dataEntity;

	/**
	 * 更新视图的ID
	 * */
	private int updataId ;
	
	/**
	 * 错误信息
	 * */
	private int errMsg ;
	
	/**
	 * 服务器信息
	 * */
	private String message ;
	
	/**
	 * 原始数据
	 * */
	private String sourceData ;

	/**
	 * 得到数据状态
	 * */
	public int getDataType() {
		return dataType;
	}

	/**
	 * 设置数据状态
	 * */
	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	/**
	 * 得到数据实体
	 * */
	public T getDataEntity() {
		return dataEntity;
	}

	/**
	 * 设置数据实体
	 * */
	public void setDataEntity(T dataEntity) {
		this.dataEntity = dataEntity;
	}

	/**
	 * 得到请求回调ID
	 * */
	public int getUpdataId() {
		return updataId;
	}

	/**
	 * 设置请求回调ID
	 * */
	public void setUpdataId(int updataId) {
		this.updataId = updataId;
	}

	/**
	 * 设置错误信息
	 * */
	public int getErrMsg() {
		return errMsg;
	}
	
	/**
	 * 得到错误信息
	 * */
	public void setErrMsg(int errMsg) {
		this.errMsg = errMsg;
	}

	/**
	 * 得到服务器信息
	 * */
	public String getMessage() {
		return message;
	}

	/**
	 * 设置服务器信息
	 * */
	public void setMessage(String message) {
		this.message = message;
	}

	public String getSourceData() {
		return sourceData;
	}

	public void setSourceData(String sourceData) {
		this.sourceData = sourceData;
	}
}
