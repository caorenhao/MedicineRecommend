package com.witspring.net.rest.rule;

import com.witspring.net.rest.RestParamErrorType;

public class RestParamIntegerRule extends RestParamRuleBase {

	public RestParamIntegerRule(boolean isRequired) {
		super(isRequired);
	}
	
	public RestParamIntegerRule(){
		super(true);
	}

	@Override
	protected RestParamErrorType checkImpl(Object value) {
		try {
			if(value != null){
				Integer.parseInt(value.toString());
			}		
		} catch(NumberFormatException nfe) {
			return RestParamErrorType.INVALID_TYPE;
		}		
		return RestParamErrorType.NONE;
	}
}
