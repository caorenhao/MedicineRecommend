package com.witspring.net.rest.rule;

import com.witspring.net.rest.RestParamErrorType;

/**
 * 检查整数类型的规则
 * @author vernkin
 *
 */
public class RestParamLongRule extends RestParamRuleBase {
	
	/** 是否检查最小值 */
	private boolean checkMinValue = false;
	
	/** 最小值，包括 */
	private long minValue = 0L;
	
	/** 是否检查最大值 */
	private boolean checkMaxValue = false;
	
	/** 最大值，不包括 */
	private long maxValue = 0L;
	
	/**
	 * 单独检查整数类型
	 * @param isRequired 参数是否必须
	 */
	public RestParamLongRule(boolean isRequired) {
		super(isRequired);
	}
	
	/**
	 * 必须存在的整数参数
	 */
	public RestParamLongRule() {
		super(true);
	}

	/**
	 * 设置最大值和最小值范围的检查
	 * @param isRequired 参数是否必须
	 * @param minValue 最小值，包括
	 * @param maxValue 最大值，不包括
	 */
	public RestParamLongRule(boolean isRequired, long minValue, long maxValue) {
		this(isRequired);
		checkMinValue = true;
		this.minValue = minValue;
		checkMaxValue = true;
		this.maxValue = maxValue;
	}
	
	/**
	 * 只设置最大值或者最小值的范围
	 * @param isRequired 参数是否必须
	 * @param bound 表示最小值(包括)或者最大值(不包括)
	 * @param isMin true表示检查最小值(bound表示最小值)，false表示检查最大值(bound为最大值)
	 */
	public RestParamLongRule(boolean isRequired, long bound, boolean isMin) {
		this(isRequired);
		if(isMin) {
			checkMinValue = true;
			this.minValue = bound;
		} else {
			checkMaxValue = true;
			this.maxValue = bound;
		}
	}
	
	protected RestParamErrorType checkImpl(Object value) {
		long tmpValue = 0;
		try {
			if(value != null){
				tmpValue = Long.parseLong(value.toString());
			}		
		} catch(NumberFormatException nfe) {
			System.out.println("print");
			return RestParamErrorType.INVALID_TYPE;
		}
		
		if(checkMinValue && tmpValue < minValue)
			return RestParamErrorType.INVALID_VALUE;
		
		if(checkMaxValue && tmpValue >= maxValue)
			return RestParamErrorType.INVALID_VALUE;
		
		return RestParamErrorType.NONE;
	}
}
