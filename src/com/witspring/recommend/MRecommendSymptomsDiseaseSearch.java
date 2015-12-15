package com.witspring.recommend;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sphx.api.SphinxClient;
import org.sphx.api.SphinxException;
import org.sphx.api.SphinxMatch;
import org.sphx.api.SphinxResult;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.witspring.util.IOUtil;

public class MRecommendSymptomsDiseaseSearch {

	public MRecommendSymptomsDiseaseSearch() throws Exception {
		// 导入疾病ID及对应的疾病名称表
		MRecommendConst.IcdNameIdMap = new HashMap<Integer, String>();
		List<String> icdNameList = new ArrayList<String>();
		icdNameList = IOUtil.readStringListFromFile(
				new File(MRecommendConst.ICDNAME_TABLE_PATH), icdNameList);
		for(String index : icdNameList) {
			String[] strs = index.split(MRecommendConst.ATTR_STR);
			MRecommendConst.IcdNameIdMap.put(Integer.parseInt(strs[1]), strs[0]);
		}
		
		// 导入症状及对应的症状ID表
		MRecommendConst.SymptomIdMap = new HashMap<String, Integer>();
		MRecommendConst.SymptomIdToNameMap = new HashMap<Integer, String>();
		List<String> symptomList = new ArrayList<String>();
		symptomList = IOUtil.readStringListFromFile(
				new File(MRecommendConst.SYMPTOM_TABLE_PATH), symptomList);
		for(String index : symptomList) {
			String[] strs = index.split(MRecommendConst.ATTR_STR);
			MRecommendConst.SymptomIdMap.put(strs[0], Integer.parseInt(strs[1]));
			MRecommendConst.SymptomIdToNameMap.put(Integer.parseInt(strs[1]), strs[0]);
		}
	}
	
	public JSONArray searchDiseases(int sex, int ageStart, int ageEnd, 
			int[] symptoms) {
        JSONArray ret = new JSONArray();
        SphinxClient cl = new SphinxClient();
        try {
			cl.SetServer(MRecommendConst.SphinxIP, MRecommendConst.SphinxPort);
	        cl.SetLimits(0, 20);
	        cl.SetConnectTimeout(MRecommendConst.SPHINX_TIMEOUT);
	        
	        // 过滤条件
	        // 性别过滤
	        if(sex != 0)
	        	cl.SetFilter("sex", sex, false);
	        // 年龄过滤
	        if(ageEnd != 0)
	        	cl.SetFilterRange("age", ageStart, ageEnd, false);
	        //主要症状过滤
	        
	        // 症状过滤
	        Map<Integer, Integer> filter = new HashMap<Integer, Integer>();
	        if(symptoms != null) {
	        	// 设置主要症状过滤
	        	cl.SetFilter("mainsymptom", symptoms[0], false);
	        	filter.put(symptoms[0], 1);
	        	
	        	// 并的关系
	        	for(int i = 1; i < symptoms.length; i++) {
	        		cl.SetFilter("secondarysymptoms", symptoms[i], false);
	        		filter.put(symptoms[i], 1);
	        	}
	        }
	        
	        // 分组条件
	        cl.SetSelect("@count");
	        cl.SetGroupBy("icd_name_id", SphinxClient.SPH_GROUPBY_ATTR, "@count desc");
	        
	        // 在Sphinx中搜索
	        SphinxResult res = cl.Query("", "dist");
	        
	        if(res != null) {
	        	for (int i = 0; i < res.matches.length; i++){
		            SphinxMatch info = res.matches[i];
		            
		            int icd_name_id = Integer.parseInt(info.attrValues.get(1).toString());
		            String icd_name = MRecommendConst.IcdNameIdMap.get(icd_name_id);
	            	JSONObject obj = new JSONObject();
		        	obj.put("icd_name", icd_name);
	            	obj.put("cnt", info.attrValues.get(0));
	            	ret.add(obj);
		        }
	        } else {
	        	System.out.println("无查询结果");
	        	return null;
	        }
		} catch (SphinxException ex) {
			ex.printStackTrace();
		} finally {
			if(cl != null) {
				try {
					cl.Close();
				} catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}
        
        return ret;
	}
	
	public static void main(String[] args) throws Exception {
		MRecommendSymptomsDiseaseSearch test = new MRecommendSymptomsDiseaseSearch();
		MRecommendConst.SphinxIP = "192.168.0.171";
		MRecommendConst.SphinxPort = 9612;
		int sex = 0;
		int ageStart = 0;
		int ageEnd = 15;
		String[] symptoms = {"月经失调"};
		int[] symptom_ids = null;
		if(symptoms != null) {
			symptom_ids = new int[symptoms.length];
			for(int i = 0; i < symptoms.length; i++) {
				symptom_ids[i] = MRecommendConst.SymptomIdMap.get(symptoms[i]);
			}
		}
		
		long start = System.currentTimeMillis();
		test.searchDiseases(sex, ageStart, ageEnd, symptom_ids);
		long end = System.currentTimeMillis();
		System.out.println("共用时：" + (end-start) + "ms");
	}
	
}
