package com.witspring.net.rest.rule;

import com.witspring.util.Pair;

public class RestParamRulePair extends Pair<String, RestParamRuleBase> {

	public RestParamRulePair() {
		super();
	}
	
	public RestParamRulePair(String first, RestParamRuleBase second) {
		super(first, second);
	}
}
