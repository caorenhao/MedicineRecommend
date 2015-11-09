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

import com.witspring.util.IOUtil;
import com.witspring.util.Pair;

public class MRecommendConcomitantSymptomsSearch {

	public MRecommendConcomitantSymptomsSearch() throws Exception {
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
		
		MRecommendConst.SphinxIP = "192.168.0.171";
		MRecommendConst.SphinxPort = 9512;
	}
	
	public static List<Pair<String, Integer>> searchYpmc_mva(int mainSymptomId, 
			int sex, int ageStart, int ageEnd, int[] symptoms) {
        List<Pair<String, Integer>> ret = new ArrayList<Pair<String, Integer>>();
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
	        cl.SetGroupBy("secondarysymptoms", SphinxClient.SPH_GROUPBY_ATTR, "@count desc");
	        
	        // 在Sphinx中搜索
	        SphinxResult res = cl.Query("", "dist");
	        
	        if(res != null) {
	        	for (int i = 0; i < res.matches.length; i++){
		            SphinxMatch info = res.matches[i];
		            
		            int symptom_id = Integer.parseInt(info.attrValues.get(1).toString());
		            if(!filter.containsKey(symptom_id)) {
		            	String symptom = MRecommendConst.SymptomIdToNameMap.get(symptom_id);
		            	System.out.println(symptom + "\t" + symptom_id 
		            			+ "\t" + info.attrValues.get(0));
		            }
		        }
	        } else {
	        	System.out.println("无查询结果");
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
		MRecommendConcomitantSymptomsSearch test = new MRecommendConcomitantSymptomsSearch();
		int sex = 0;
		int ageStart = 10;
		int ageEnd = 0;
		String[] symptoms = {"咳嗽", "流涕", "鼻塞", "声嘶", "咽痛", "咽干"};
		int[] symptom_ids = null;
		if(symptoms != null) {
			symptom_ids = new int[symptoms.length];
			for(int i = 0; i < symptoms.length; i++) {
				symptom_ids[i] = MRecommendConst.SymptomIdMap.get(symptoms[i]);
			}
		}
		
		long start = System.currentTimeMillis();
		searchYpmc_mva(0, sex, ageStart, ageEnd, symptom_ids);
		long end = System.currentTimeMillis();
		System.out.println("共用时：" + (end-start) + "ms");
	}
	
}
