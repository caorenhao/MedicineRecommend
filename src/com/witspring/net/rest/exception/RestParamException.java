package com.witspring.net.rest.exception;

import java.util.HashMap;
import java.util.Map;

import com.witspring.net.rest.RestParamErrorType;


/**
 * 参数错误异常
 * @author vernkin
 *
 */
public class RestParamException extends RestException {

	private static final long serialVersionUID = 1662128868825558024L;
	
	private static Map<RestParamErrorType, RestErrorStr> ERROR_STR_MAP = 
		new HashMap<RestParamErrorType, RestErrorStr>();
	static {
		ERROR_STR_MAP.put(RestParamErrorType.MISSED, RestErrorStr.PARAM_MISSED);
		ERROR_STR_MAP.put(RestParamErrorType.INVALID_VALUE, RestErrorStr.PARAM_INVALID_VALUE);
		ERROR_STR_MAP.put(RestParamErrorType.INVALID_TYPE, RestErrorStr.PARAM_INVALID_TYPE);
		ERROR_STR_MAP.put(RestParamErrorType.REDUNDANT, RestErrorStr.PARAM_REDUNDANT);
	}
	
	
	
	public RestParamException(RestParamErrorType type, String params) {
		super(toErrorStr(ERROR_STR_MAP.get(type), params), RestErrorCode.PARAM_ERROR);
	}
	
	public RestParamException(Map<String, RestParamErrorType> errorMap) {
		super(null, RestErrorCode.PARAM_ERROR);
		String[] errorStrs = new String[errorMap.size()];
		int i = 0;
		for(Map.Entry<String, RestParamErrorType> entry : errorMap.entrySet()) {
			errorStrs[i] = toErrorStr(ERROR_STR_MAP.get(entry.getValue()), entry.getKey());
			++i;
		}
		super.errorObj = errorStrs;
	}
	
	
}
