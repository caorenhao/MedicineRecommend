package com.witspring.net.rest.rule;

import com.witspring.net.rest.RestParamErrorType;

public class RestParamBooleanRule extends RestParamRuleBase {

	public RestParamBooleanRule(boolean isRequired) {
		super(isRequired);
	}
	
	public RestParamBooleanRule(){
		super(true);
	}

	@Override
	protected RestParamErrorType checkImpl(Object value) {
//		if(!(value instanceof Boolean))
//			return RestParamErrorType.INVALID_TYPE;		
		return RestParamErrorType.NONE;
	}

}
