package com.witspring.net.rest.rule;

import com.witspring.net.rest.RestParamErrorType;

public class RestParamStringRule extends RestParamRuleBase {

	/** 是否检查最小长度 */
	private boolean checkMinLen = false;
	
	/** 最小长度，包括 */
	private int minLen = 0;
	
	/** 是否检查最大长度 */
	private boolean checkMaxLen = false;
	
	/** 最大长度，不包括 */
	private int maxLen = 0;
	
	private boolean flag = true ; 
	
	/**
	 * 只检查字符串本身
	 * @param isRequired 参数是否必须
	 */
	public RestParamStringRule(boolean isRequired) {
		super(isRequired);
		flag = isRequired ;
	}
	
	/**
	 * 必须包含的字符串
	 */
	public RestParamStringRule() {
		super(true);
	}

	/**
	 * 检查字符串的最小长度和最大长度
	 * @param isRequired 参数是否必须
	 * @param minLen 最小长度，包括
	 * @param maxLen 最大长度，不包括
	 */
	public RestParamStringRule(boolean isRequired, int minLen, int maxLen) {
		this(isRequired);
		flag = isRequired; 
		checkMinLen = true;
		this.minLen = minLen;
		checkMaxLen = true;
		this.maxLen = maxLen;
	}
	
	/**
	 * 只设置最大长度或者最小长度的范围
	 * @param isRequired 参数是否必须
	 * @param bound 表示最小长度(包括)或者最大长度(不包括)
	 * @param isMin true表示检查最小长度(bound表示最小长度)，false表示检查最大长度(bound为最大长度)
	 */
	public RestParamStringRule(boolean isRequired, int bound, boolean isMin) {
		this(isRequired);
		flag = isRequired ; 
		if(isMin) {
			checkMinLen = true;
			this.minLen = bound;
		} else {
			checkMaxLen = true;
			this.maxLen = bound;
		}
	}
	
	protected RestParamErrorType checkImpl(Object value) {
		if(!(value instanceof String))
			return RestParamErrorType.INVALID_TYPE;
		String strValue = (String)value;
		int strLen = strValue.length();
		if(checkMinLen && strLen < minLen)
			return RestParamErrorType.INVALID_VALUE;
		
		if(checkMaxLen && strLen >= maxLen)
			return RestParamErrorType.INVALID_VALUE;
		if(flag){
			if("".endsWith(strValue.trim())){
				return RestParamErrorType.INVALID_VALUE;
			}
		}		
		return RestParamErrorType.NONE;
	}
}
