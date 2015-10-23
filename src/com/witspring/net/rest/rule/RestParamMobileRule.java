package com.witspring.net.rest.rule;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.witspring.net.rest.RestParamErrorType;

public class RestParamMobileRule extends RestParamRuleBase {

	public RestParamMobileRule(boolean isRequired) {
		super(isRequired);
	}
	
	public RestParamMobileRule(){
		super(true);
	}

	@Override
	protected RestParamErrorType checkImpl(Object value) {
		if(!(value instanceof String))
			return RestParamErrorType.INVALID_TYPE;
		String strValue = (String)value;
		if(strValue.isEmpty()){
			return RestParamErrorType.NONE;
		}
		String check = "((\\d{11})|^((\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1})|(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1}))$)";
		Pattern p = Pattern.compile(check); 
		Matcher m = p.matcher(strValue); 
		if(!m.matches()){
			return RestParamErrorType.INVALID_TYPE;
		}
		return RestParamErrorType.NONE;
	}

}
