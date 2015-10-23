package com.witspring.net.rest.exception;

/**
 * 没有认证的错误
 * @author vernkin
 *
 */
public class RestUnauthorizedException extends RestException {

	private static final long serialVersionUID = 2657641328186974042L;

	public RestUnauthorizedException() {
		this(null);
	}
	
	public RestUnauthorizedException(Throwable cause) {
		super(RestErrorStr.UNAUTHORIRED_ACCESS.toErrorStr(), 
				cause, RestErrorCode.UNAUTHORIRED);
	}
}
