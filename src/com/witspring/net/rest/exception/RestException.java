package com.witspring.net.rest.exception;

import com.alibaba.fastjson.JSONObject;


public class RestException extends Exception {

	private static final long serialVersionUID = 4511122899072727042L;
	
	/** 逻辑的错误代码 */
	private int errorCode = -1;
	
	/** 存储错误的对象， 默认为null。为null由getMessage()取代 */
	protected Object errorObj;
	
	protected JSONObject errorDetails = new JSONObject();
	
	public RestException(String msg, Throwable cause, RestErrorCode rec) {
		super(msg, cause);
		this.errorCode = rec.getErrorCode();
	}
	
	public RestException(String msg, RestErrorCode rec) {
		super(msg);
		this.errorCode = rec.getErrorCode();
	}

	public int getErrorCode() {
		return errorCode;
	}
	
	public Object getErrorObj() {
		if(errorObj != null)
			return errorObj;
		return new Object[]{super.getMessage()};
	}
	
	public JSONObject getErrorDetails() {
		return errorDetails;
	}
	
	public void addErrorDetail(String key, Object val) {
		errorDetails.put(key, val);
	}
	
	public Object getErrorDetail(String key) {
		return errorDetails.get(key);
	}
	
	protected static String toErrorStr(RestErrorStr res, String param) {
		String ret = res.toErrorStr();
		if(param != null && !param.isEmpty())
			ret = ret + ":" + param;
		return ret;
	}
	
	protected static String toErrorStr(RestErrorStr res, String param ,String resid) {
		String ret = res.toErrorStr();
		if(param != null && !param.isEmpty())
			ret = ret + ":" + param;
		if(resid != null && !resid.isEmpty())
			ret = ret + "," + "ret:" + resid;
		return ret;
	}
	
	
}
