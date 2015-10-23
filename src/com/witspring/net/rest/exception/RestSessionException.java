package com.witspring.net.rest.exception;

/**
 * 会话错误的异常
 * @author vernkin
 *
 */
public class RestSessionException extends RestException {

	private static final long serialVersionUID = 6001404601787494401L;

	/**
	 * 会话错误的类型
	 * @author vernkin
	 *
	 */
	public static enum Type {
		NOT_FOUND,
		EXPIRED,
		INVALID_USER
	}
	
	public RestSessionException(Type type) {
		this(type, null);
	}
	
	public RestSessionException(Type type, Throwable cause) {
		super(getMsg(type), cause, RestErrorCode.SESSION_ERROR);
	}

	private static String getMsg(Type type) {
		RestErrorStr res = RestErrorStr.SESSION_NOT_FOUND;
		if(type == Type.INVALID_USER)
			res = RestErrorStr.SESSION_INVALID_USER;
		else if(type == Type.EXPIRED)
			res = RestErrorStr.SESSION_EXPIRED;
		
		return toErrorStr(res, null);
	}
}
