package com.witspring.net.rest.exception;

public class RestDataExpiredException extends RestException {

	private static final long serialVersionUID = -3818603133135918277L;

	public RestDataExpiredException(Throwable cause) {
		super(RestErrorStr.DATA_EXPIRED.toErrorStr(), 
				cause, RestErrorCode.DATA_EXPIRED);
	}
	
	public RestDataExpiredException() {
		this(null);
	}
}
