package com.witspring.net.rest.exception;


/**
 * 路由错误
 * @author vernkin
 *
 */
public class RestRouterException extends RestException {

	private static final long serialVersionUID = -3528953125566255180L;

	public static enum Type {
		NOT_FOUND,
		REQMETHOD_NOT_SUPPOR;
	}
	
	public RestRouterException(Type type, String path) {
		this(type, path, null);
	}
	
	public RestRouterException(Type type, String path, Throwable cause) {
		super(getMsg(type, path), cause, RestErrorCode.ROUTER_ERROR);
	}
	
	private static String getMsg(Type type, String path) {
		RestErrorStr res = (type == Type.NOT_FOUND) ? 
				RestErrorStr.ROUTER_NOT_FOUND : RestErrorStr.ROUTER_METHOD_NOT_SUPPORT;
		if(path == null)
			path = "/";
		return toErrorStr(res, path);
	}
}
