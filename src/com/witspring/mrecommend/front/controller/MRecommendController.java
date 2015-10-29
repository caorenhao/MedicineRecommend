package com.witspring.mrecommend.front.controller;

import org.apache.commons.logging.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.witspring.mrecommend.front.IYQFrontConst;
import com.witspring.mrecommend.front.helper.ControllerParamRules;
import com.witspring.net.rest.rule.RestParamRuleChecker;
import com.witspring.recommend.MRecommendCost;
import com.witspring.util.LoggerConfig;
import com.witspring.util.StrUtil;

/**
 * 根据疾病名称id, 年龄区间, 症状 进行药品推荐的接口.
 *
 * @author renhao.cao.
 *         Created 2015-10-9.
 */
public class MRecommendController extends ApplicationController {
	
	private Log LOGGER = LoggerConfig.getLog(getClass());
	
	private static RestParamRuleChecker mrecommendCheck = new RestParamRuleChecker(
			ControllerParamRules.icd_name_id,
			ControllerParamRules.sex,
			ControllerParamRules.agestart,
			ControllerParamRules.ageend,
			ControllerParamRules.symptom
			);
	
	/**
	 * Http Server 处理线程.
	 *
	 * @throws Exception
	 */
	public void recommend() throws Exception {
		checkParam(mrecommendCheck);
		String icd_name_id = params.getString(ControllerParamRules.icd_name_id.first);
		String sex = params.getString(ControllerParamRules.sex.first);
		String ageStart = params.getString(ControllerParamRules.agestart.first);
		String ageEnd = params.getString(ControllerParamRules.ageend.first);
		String symptom = params.getString(ControllerParamRules.symptom.first);
		
		String[] symptoms = null;
		if(symptom != null && !symptom.isEmpty() && !("null").equals(symptom))
			symptoms = symptom.split(",");
		int[] symptom_ids = null;
		if(symptoms != null) {
			symptom_ids = new int[symptoms.length];
			for(int i = 0; i < symptoms.length; i++) {
				symptom_ids[i] = MRecommendCost.SymptomIdMap.get(symptoms[i]);
			}
		}
		
		JSONArray ret = IYQFrontConst.mRecommendYPMCSearch.search(
				IYQFrontConst.mRecommendCache, 
				Integer.parseInt(icd_name_id), Integer.parseInt(sex), 
				Integer.parseInt(ageStart), Integer.parseInt(ageEnd), symptom_ids);
		
		LOGGER.info("The medicine search task - icd_name_id:" + icd_name_id 
				+ "\tsex:" + sex + "\tageRange:"+ ageStart + "-" + ageEnd 
				+ "\tsymptoms:" + symptom);
		LOGGER.info("The result is:" + StrUtil.join(ret, ","));
		
		JSONObject rootJson = new JSONObject();
		rootJson.put("medicineList", ret);
		this.response.reponseWithMessage(rootJson);
	}
	
	/**
	 * TODO Put here a description of what this method does.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception {
		MRecommendController return1 = new MRecommendController();
		return1.recommend();
	}
}
