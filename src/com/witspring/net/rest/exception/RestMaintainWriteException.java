package com.witspring.net.rest.exception;

public class RestMaintainWriteException extends RestException {

	private static final long serialVersionUID = 8664506601304092841L;

	public RestMaintainWriteException() {
		super("MAINTAIN_WRITE_ERROR", RestErrorCode.MAINTAIN_WRITE_ERROR);
	}

}
