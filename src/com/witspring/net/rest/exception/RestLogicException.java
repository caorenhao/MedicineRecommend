package com.witspring.net.rest.exception;

public class RestLogicException extends RestException {

	private static final long serialVersionUID = -8146294838031400719L;

	public RestLogicException(String msg) {
		this(msg, null);
	}
	
	public RestLogicException(String msg, Throwable cause) {
		super(msg, null, RestErrorCode.LOGIC_ERROR);
	}
	
	public RestLogicException(RestErrorStr res, String params) {
		this(res, params, null);
	}
	
	public RestLogicException(RestErrorStr res, String params, Throwable cause) {
		super(toErrorStr(res, params), cause, RestErrorCode.LOGIC_ERROR);
	}
}
