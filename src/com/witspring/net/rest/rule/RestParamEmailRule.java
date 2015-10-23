package com.witspring.net.rest.rule;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.witspring.net.rest.RestParamErrorType;

public class RestParamEmailRule extends RestParamRuleBase {

	public RestParamEmailRule(boolean isRequired) {
		super(isRequired);
	}
	
	public RestParamEmailRule(){
		super(true);
	}

	@Override
	protected RestParamErrorType checkImpl(Object value) {
		if(!(value instanceof String))
			return RestParamErrorType.INVALID_TYPE;
		String strValue = (String)value;
		Pattern p = Pattern.compile("^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$" );
		Matcher m = p.matcher(strValue);
		if(!m.matches()){
			return RestParamErrorType.INVALID_TYPE;
		}
		return RestParamErrorType.NONE;
	}

}
