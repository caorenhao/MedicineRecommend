package com.witspring.net.rest.exception;

public enum RestErrorStr {
	/** 没找到会话 */
	SESSION_NOT_FOUND,
	/** 会话过期 */
	SESSION_EXPIRED,
	/** 无效的用户名或者密码 */
	SESSION_INVALID_USER,
	
	/** 无权访问 */
	UNAUTHORIRED_ACCESS,
	
	/** 找不到路由 */
	ROUTER_NOT_FOUND,
	/** 不支持请求的方法(Request Method) */
	ROUTER_METHOD_NOT_SUPPORT,
	
	/** 数据已经过期*/
	DATA_EXPIRED,
	
	/** 参数缺少 */
	PARAM_MISSED,
	/** 参数值非法 */
	PARAM_INVALID_VALUE,
	/** 参数类型非法 */
	PARAM_INVALID_TYPE,
	/** 参数多余（不需要） */
	PARAM_REDUNDANT,
	
	/** 重复的值 */
	DUPLICATE_VAL,
	/**  无权访问*/
	NO_POWER,
	
	/** 没有找到可以用数据*/
	DATA_INVALID,
	/** 删除话题分类的时候，如果有话题，不能删除*/
	HAVE_TOPIC,
	/** */
	HAVE_MAP
	;
	
	/**
	 * 转换成标准的错误代码
	 * @return
	 */
	public String toErrorStr() {
		String name = name().toLowerCase();
		return name.replaceAll("_", ".");
	}
}
