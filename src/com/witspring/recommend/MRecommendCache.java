package com.witspring.recommend;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.witspring.mrecommend.conf.ConfigSingleton;
import com.witspring.mrecommend.conf.MRecommendConfig;
import com.witspring.util.CRC32;
import com.witspring.util.IOUtil;
import com.witspring.util.JedisWrapper;
import com.witspring.util.NetUtil;
import com.witspring.util.StrUtil;

public class MRecommendCache {

	private JedisWrapper jedis;
	
	public MRecommendCache() throws Exception {
		MRecommendConfig conf = ConfigSingleton.getMRecommendConfig();
		String server = conf.redisConf.server;
		int port = conf.redisConf.port;
		int maxSize = 50;
		jedis = JedisWrapper.getInstance(server, port, maxSize);
	}
	
	/** 初始化查询时间过长的数据*/
	public void initData() throws Exception {
		MRecommendYPMCSearch search = new MRecommendYPMCSearch();
		List<String> icd_name_ids = new ArrayList<String>();
		icd_name_ids = IOUtil.readStringListFromFile(new File(
				MRecommendCost.INIT_ICD_NAME_PATH), icd_name_ids);
		for(String icd_name_id : icd_name_ids) {
			for(int i = 0; i < 3; i++) {
				search.search(this, Integer.parseInt(icd_name_id), i, 0, 0, null);
				NetUtil.sleep(500);
			}
		}
	}
	
	public JSONArray get(int icd_name_id, int[] symptoms, int sex, float ageStart, 
			float ageEnd) throws Exception {
		String ageRange = MRecommendAlgo.getAgeRange(ageStart, ageEnd);
		String symptom = StrUtil.join(symptoms, ",");
		StringBuffer sb = new StringBuffer();
		String key = sb.append(symptom).append("_").append(sex).append("_")
				.append(ageRange).toString();
		//String keyDes = DesUtil.encrypt(key, DesUtil.KEY);
		int keyId = CRC32.getCRC32(key);
		String ret = jedis.hget(String.valueOf(icd_name_id), String.valueOf(keyId));
		JSONArray result = null;
		if(ret != null) {
			result = JSONArray.parseArray(ret);
			//System.out.println(result.toJSONString());
		}
		
		return result;
	}
	
	public long put(int icd_name_id, int[] symptoms,int sex, float ageStart, 
			float ageEnd, JSONArray array) throws Exception {
		// 组合数据
		String ageRange = MRecommendAlgo.getAgeRange(ageStart, ageEnd);
		String symptom = StrUtil.join(symptoms, ",");
		StringBuffer sb = new StringBuffer();
		String key = sb.append(symptom).append("_").append(sex).append("_")
				.append(ageRange).toString();
		//String keyDes = DesUtil.encrypt(key, DesUtil.KEY);
		int keyId = CRC32.getCRC32(key);
		long ret = jedis.hset(String.valueOf(icd_name_id), String.valueOf(keyId), 
				array.toJSONString());
		
		return ret;
	}
	
	public static void main(String[] args) throws Exception {
		MRecommendCache cache = new MRecommendCache();
		cache.initData();
		
		/*
		int icd_name_id = 106;
		float ageStart = 20f;
		float ageEnd = 0f;
		String[] symptoms = null;
		int[] symptom_ids = null;
		if(symptoms != null) {
			symptom_ids = new int[symptoms.length];
			for(int i = 0; i < symptoms.length; i++) {
				symptom_ids[i] = MRecommendCost.SymptomIdMap.get(symptoms[i]);
			}
		}
		
		JSONArray obj = cache.get(icd_name_id, symptom_ids, ageStart, ageEnd);
		System.out.println(obj.toJSONString());
		*/
	}
	
}
