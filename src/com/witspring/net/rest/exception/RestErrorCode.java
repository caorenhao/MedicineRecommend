package com.witspring.net.rest.exception;

public enum RestErrorCode {

	/** 没有登录或者会话过期 */
	SESSION_ERROR(1),
	
	/** 没有权限的访问某些接口 */
	UNAUTHORIRED(2),
	
	/** 路由错误  */
	ROUTER_ERROR(3),

	/** 数据已经超时，网页需要刷新 */
	DATA_EXPIRED(4),
	
	/** 请求参数错误 */
	PARAM_ERROR(5),
	
	/** 其它的逻辑错误 */
	LOGIC_ERROR(6),
	
	/** 用户输入错误，如话题名称重复*/
	USER_ERROR(7) ,
	
	/** 维护不能写的异常 */
	MAINTAIN_WRITE_ERROR(10000);
	
	private int errorCode;
	
	private RestErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
	public int getErrorCode() {
		return errorCode;
	}
}
