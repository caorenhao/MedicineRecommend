package com.witspring.net.rest.rule;

import com.witspring.net.rest.RestParamErrorType;

/**
 * 只表明该参数是否需要的规则
 * @author vernkin
 *
 */
public class RestParamRequiredRule extends RestParamRuleBase {

	public RestParamRequiredRule(boolean isRequired) {
		super(isRequired);
	}

	@Override
	protected RestParamErrorType checkImpl(Object value) {
		return RestParamErrorType.NONE;
	}

}
