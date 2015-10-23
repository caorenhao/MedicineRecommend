package com.witspring.net.rest;

import com.witspring.net.rest.exception.RestErrorCode;
import com.witspring.net.rest.exception.RestErrorStr;
import com.witspring.net.rest.exception.RestException;

public class RestUserException extends RestException{

	private static final long serialVersionUID = 5868876054561618383L;

	
	public RestUserException(String msg) {
		this(msg, null);
	}
	public RestUserException(String msg, RestErrorCode rec) {
		super(msg, null, RestErrorCode.USER_ERROR);
	}
	public RestUserException(RestErrorStr res, String params) {
		this(res, params, null);
	}
	
	public RestUserException(RestErrorStr res, String params , String ret , Throwable cause){	
		super(toErrorStr(res, params , ret), cause , RestErrorCode.USER_ERROR ) ;
	}
	
	public RestUserException(RestErrorStr res, String params, Throwable cause) {
		super(toErrorStr(res, params), cause, RestErrorCode.USER_ERROR);
	}
}
