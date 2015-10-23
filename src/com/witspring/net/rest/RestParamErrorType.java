package com.witspring.net.rest;

/**
 * Rest请求的参数的错误类型
 * @author vernkin
 *
 */
public enum RestParamErrorType {
	/** 没有错误 */
	NONE,
	/** 缺少参数 */
	MISSED,
	/** 多余的参数 */
	REDUNDANT,
	/** 数据类型不正确，如不是整数 */
	INVALID_TYPE,
	/** 数据的值的不正确，如正整数的情况下输入负整数 */
	INVALID_VALUE
}
