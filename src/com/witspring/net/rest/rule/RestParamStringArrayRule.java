package com.witspring.net.rest.rule;

import com.witspring.net.rest.RestParamErrorType;

public class RestParamStringArrayRule extends RestParamRuleBase {

	public RestParamStringArrayRule(boolean isRequired) {
		super(isRequired);
	}
	
	public RestParamStringArrayRule(){
		super(true);
	}

	@Override
	protected RestParamErrorType checkImpl(Object value) {
//		if(!(value instanceof JSONArray))
//			return RestParamErrorType.INVALID_TYPE;
//		JSONArray array = (JSONArray)value;
//		try {
//			for(int i = 0; i < array.size(); ++i) {
//				if(!(array.get(i) instanceof String));
//			}
//		} catch(NumberFormatException nfe) {
//			return RestParamErrorType.INVALID_TYPE;
//		}
		return RestParamErrorType.NONE;
	}

}
