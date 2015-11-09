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

import com.witspring.mrecommend.conf.ConfigSingleton;
import com.witspring.mrecommend.conf.MRecommendConfig;
import com.witspring.util.IOUtil;
import com.witspring.util.Pair;
import com.witspring.util.StrUtil;

public class MRecommendIcdnameSearch {

	public static void init() throws Exception {
		MRecommendConst.IndexMap = new HashMap<Pair<Integer, Integer>, String>();
		List<String> indexList = new ArrayList<String>();
		indexList = IOUtil.readStringListFromFile(
				new File(MRecommendConst.INDEX_ICDNAMEID_PATH), indexList);
		for(String index : indexList) {
			String[] strs = index.split(" ");
			String[] range = strs[0].split(",");
			MRecommendConst.IndexMap.put(new Pair<Integer, Integer>(Integer.parseInt(range[0]), 
					Integer.parseInt(range[1])), strs[1]);
		}
		
		// 初始化疾病及疾病对应ID表
		MRecommendConst.IcdNameIdMap = new HashMap<Integer, String>();
		List<String> icdNameList = new ArrayList<String>();
		indexList = IOUtil.readStringListFromFile(
				new File(MRecommendConst.ICDNAME_TABLE_PATH), icdNameList);
		for(String index : indexList) {
			String[] strs = index.split(MRecommendConst.ATTR_STR);
			MRecommendConst.IcdNameIdMap.put(Integer.parseInt(strs[1]), strs[0]);
		}
		
		// 初始化症状及症状对应ID表
		MRecommendConst.SymptomIdMap = new HashMap<String, Integer>();
		List<String> symptomList = new ArrayList<String>();
		indexList = IOUtil.readStringListFromFile(
				new File("./data/index/t_qb_zyzd_jieba_4_1.txt"), symptomList);
		for(String index : symptomList) {
			String[] strs = index.split(MRecommendConst.ATTR_STR);
			MRecommendConst.SymptomIdMap.put(strs[0], Integer.parseInt(strs[1]));
		}
		
		MRecommendConfig conf = ConfigSingleton.getMRecommendConfig();
		MRecommendConst.SphinxIP = conf.sphinxConf.server;
		MRecommendConst.SphinxPort = 9612;
	}
	
	/**
	 * 按照条件查找症状对应的疾病.
	 * 
	 * @param ageStart 年龄开始
	 * @param ageEnd 年龄结束
	 * @param symptoms 症状
	 * @return List<String>
	 */
	public static List<String> searchIcd_name_id_mva(int[] symptoms, int sex, 
			int ageStart, int ageEnd) {
		SphinxClient cl = new SphinxClient();
        String index = "index";
        
        List<String> ret = new ArrayList<String>();
        try {
        	cl.SetServer (MRecommendConst.SphinxIP, MRecommendConst.SphinxPort);
	        cl.SetMatchMode (SphinxClient.SPH_MATCH_EXTENDED2);
	        cl.SetLimits (0, 20);
	        cl.SetConnectTimeout(MRecommendConst.SPHINX_TIMEOUT);
	        
	        // 过滤条件
	        if(sex != 0)
	        	cl.SetFilter("sex", sex, false);
	        if(ageEnd != 0)
	        	cl.SetFilterRange("age", ageStart, ageEnd, false);
	        if(symptoms != null) {
	        	/*
	        	for(int symptom : symptoms) {
	        		cl.SetFilter("symptoms", symptom, false);
	        	}
	        	*/
	        	cl.SetFilter("symptoms", symptoms, false);
	        }
	        
	        // 分组条件
	        cl.SetSelect("icd_name_id, @count");
	        cl.SetGroupBy("icd_name_id", SphinxClient.SPH_GROUPBY_ATTR, "@count desc");
	        
	        // 在Sphinx中搜索
	        SphinxResult res = cl.Query("", index);
	        
	        if(res != null) {
	        	Map<Integer, String> icdNameIdMap = MRecommendConst.IcdNameIdMap;
		        for (int j=0; j<res.matches.length; j++){
		            SphinxMatch info = res.matches[j];
		            String icd_name = icdNameIdMap.get(
		            		Integer.parseInt(info.attrValues.get(0).toString()));
		            System.out.println(icd_name + "\t" + info.attrValues.get(1));
		            ret.add(icd_name);
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
	
	/**
	 * TODO Put here a description of what this method does.
	 * 
	 * @param args 
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		init();
		
		String[] symptoms = {"咳嗽","流鼻涕","便秘","头晕","发热","乏力","腰痛","尿频","胃痛","尿急","眼干燥","腹痛","多汗","腹胀",
				"咳痰","喷嚏","皮肤红疹","流泪","发冷","头胀","哮喘","尿道刺痛","流鼻血","鼻痒","腹泻","腰酸","眼睑浮肿"};
		int sex = 2;
		int ageStart = 30;
		int ageEnd = 60;
		int[] symptom_ids = new int[symptoms.length];
		for(int i = 0; i < symptoms.length; i++) {
			if(MRecommendConst.SymptomIdMap.containsKey(symptoms[i]))
				symptom_ids[i] = MRecommendConst.SymptomIdMap.get(symptoms[i]);
		}
		
		long start1 = System.currentTimeMillis();
		List<String> ret = MRecommendIcdnameSearch.searchIcd_name_id_mva(
				symptom_ids, sex, ageStart, ageEnd);
		long end1 = System.currentTimeMillis();
		System.out.println("可能疾病：" + StrUtil.join(ret, ","));
		System.out.println("推荐共用时：" + (end1-start1) + "ms");
	}
	
}
