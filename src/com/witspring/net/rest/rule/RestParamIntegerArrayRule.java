package com.witspring.net.rest.rule;

import com.alibaba.fastjson.JSONArray;
import com.witspring.net.rest.RestParamErrorType;

public class RestParamIntegerArrayRule extends RestParamRuleBase {

	public RestParamIntegerArrayRule(boolean isRequired) {
		super(isRequired);
	}
	
	public RestParamIntegerArrayRule(){
		super(true);
	}

	@Override
	protected RestParamErrorType checkImpl(Object value) {
		if(!(value instanceof JSONArray))
			return RestParamErrorType.INVALID_TYPE;
		JSONArray array = (JSONArray)value;
		try {
			for(int i = 0; i < array.size(); ++i) {
				if(array.get(i) != null){
					Integer.parseInt(array.get(i).toString());
				}else {
					return RestParamErrorType.INVALID_TYPE;
				}			
			}
		} catch(NumberFormatException nfe) {
			return RestParamErrorType.INVALID_TYPE;
		}		
		return RestParamErrorType.NONE;
	}

}
