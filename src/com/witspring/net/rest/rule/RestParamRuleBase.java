package com.witspring.net.rest.rule;

import com.witspring.net.rest.RestParamErrorType;

public abstract class RestParamRuleBase {
	
	/** 是否必须 */
	protected boolean isRequired;
	
	public RestParamRuleBase(boolean isRequired) {
		this.isRequired = isRequired;
	}
	
	public boolean isRequired() {
		return isRequired;
	}
	
	/**
	 * 检查的入口函数，先判断是否必须条件
	 * @param value
	 * @return null或者RestParamErrorType.NONE 表示没有问题
	 */
	public final RestParamErrorType check(Object value) {
		if(value == null) {
			if(!isRequired)
				return RestParamErrorType.NONE;
			// 必填的情况下输入null或者控制识别为 非法值
			return RestParamErrorType.INVALID_VALUE;
		}
		return checkImpl(value);
	}
	
	/**
	 * 子类覆盖的检查方法
	 * @param value 要检查的值，不为null
	 * @return null或者RestParamErrorType.NONE 表示没有问题
	 */
	protected abstract RestParamErrorType checkImpl(Object value);
}
