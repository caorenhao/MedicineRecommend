package com.witspring.net.rest.rule;

import com.witspring.net.rest.RestParamErrorType;

/**
 * 浮点数检查的规则
 * @author vernkin
 *
 */
public class RestParamDoubleRule extends RestParamRuleBase {

	/** 是否检查最小值 */
	private boolean checkMinValue = false;
	
	/** 最小值，包括 */
	private double minValue = 0.0;
	
	/** 是否检查最大值 */
	private boolean checkMaxValue = false;
	
	/** 最大值，不包括 */
	private double maxValue = 0.0;
	
	/**
	 * 单独检查整数类型
	 * @param isRequired 参数是否必须
	 */
	public RestParamDoubleRule(boolean isRequired) {
		super(isRequired);
	}

	/**
	 * 设置最大值和最小值范围的检查
	 * @param isRequired 参数是否必须
	 * @param minValue 最小值，包括
	 * @param maxValue 最大值，不包括
	 */
	public RestParamDoubleRule(boolean isRequired, double minValue, double maxValue) {
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
	public RestParamDoubleRule(boolean isRequired, double bound, boolean isMin) {
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
		double tmpValue = 0;
		try {
			tmpValue = Double.parseDouble(value.toString());
		} catch(NumberFormatException nfe) {
			return RestParamErrorType.INVALID_TYPE;
		}
		
		if(checkMinValue && tmpValue < minValue)
			return RestParamErrorType.INVALID_VALUE;
		
		if(checkMaxValue && tmpValue >= maxValue)
			return RestParamErrorType.INVALID_VALUE;
		
		return RestParamErrorType.NONE;
	}
}
