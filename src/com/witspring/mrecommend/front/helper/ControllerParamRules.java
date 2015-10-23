package com.witspring.mrecommend.front.helper;

import com.witspring.net.rest.rule.RestParamRulePair;
import com.witspring.net.rest.rule.RestParamStringRule;

/**
 * TODO Put here a description of what this class does.
 *
 * @author renhao.cao.
 *         Created 2014-9-25.
 */
public class ControllerParamRules {
	
	/** icd_name_id格式检测*/
	public static RestParamRulePair icd_name_id =
			new RestParamRulePair("icd_name_id", new RestParamStringRule(true));
	
	/** sex格式检测*/
	public static RestParamRulePair sex =
			new RestParamRulePair("sex", new RestParamStringRule(true));
	
	/** ageStart格式检测*/
	public static RestParamRulePair agestart =
			new RestParamRulePair("agestart", new RestParamStringRule(true));
	
	/** ageEnd格式检测*/
	public static RestParamRulePair ageend =
			new RestParamRulePair("ageend", new RestParamStringRule(true));
	
	/** symptom格式检测*/
	public static RestParamRulePair symptom =
			new RestParamRulePair("symptom", new RestParamStringRule(false));
}
