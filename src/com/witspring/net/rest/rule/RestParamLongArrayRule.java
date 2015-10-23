package com.witspring.net.rest.rule;

import com.alibaba.fastjson.JSONArray;
import com.witspring.net.rest.RestParamErrorType;

/**
 * 输入必须的整数型的数组
 * @author vernkin
 *
 */
public class RestParamLongArrayRule extends RestParamRuleBase {

	public RestParamLongArrayRule(boolean isRequired) {
		super(isRequired);
	}

	/**
	 * 必须包括
	 */
	public RestParamLongArrayRule() {
		super(true);
	}

	@Override
	protected RestParamErrorType checkImpl(Object value) {
		if(!(value instanceof JSONArray))
			return RestParamErrorType.INVALID_TYPE;
		JSONArray array = (JSONArray)value;
		try {
			for(int i = 0; i < array.size(); ++i) {
				Long.parseLong(array.get(i).toString());
			}
		} catch(NumberFormatException nfe) {
			return RestParamErrorType.INVALID_TYPE;
		}
		
		return RestParamErrorType.NONE;
	}
}
