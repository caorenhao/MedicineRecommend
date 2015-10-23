package com.witspring.net.rest.rule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.witspring.net.rest.RestParamErrorType;
import com.witspring.net.rest.RestParamMap;


public class RestParamRuleChecker {

	private Map<String, RestParamRuleBase> ruleMap = 
		new HashMap<String, RestParamRuleBase>();
	
	/**
	 * 添加规则检查器
	 * @param rules
	 */
	public RestParamRuleChecker(RestParamRulePair... rules) {
		if(rules != null) {
			for(RestParamRulePair rule : rules) {
				ruleMap.put(rule.first.toLowerCase(), rule.second);
			}
		}
	}
	
	/**
	 * 检查参数是否正确
	 * @param paramMap 参数列表
	 * @return null表示没有错误，否则为错误的属性名和错误类型的列表
	 */
	public Map<String, RestParamErrorType> check(RestParamMap paramMap) {
		Map<String, RestParamErrorType> errorMap = new HashMap<String, RestParamErrorType>();	
		// 参数的键名列表
		Set<String> paramKeySet = new HashSet<String>();
		paramKeySet.addAll(paramMap.keySet());
		
		for(Map.Entry<String, RestParamRuleBase> rule : ruleMap.entrySet()) {
			String key = rule.getKey();
			// 参数列表中必须包括所有的属性名称，非 required 值可以为null
			if(!paramMap.containsKey(key)) {			
				errorMap.put(key, RestParamErrorType.MISSED);			
				continue;
			}
			
			RestParamErrorType errorType = rule.getValue().check(paramMap.get(key));
			paramKeySet.remove(key); // 删除检查过的key
			if(errorType == null || errorType == RestParamErrorType.NONE)
				continue;
			errorMap.put(key, errorType);
		}
		
		for(String key : paramKeySet) {
			errorMap.put(key, RestParamErrorType.REDUNDANT);
		}
		
		if(errorMap.isEmpty())
			return null;
		return errorMap;
	}
}
