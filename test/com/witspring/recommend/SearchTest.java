package com.witspring.recommend;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.witspring.http.HttpClientCommon;
import com.witspring.http.HttpClientUtils;
import com.witspring.recommend.MRecommendCost;
import com.witspring.util.IOUtil;

public class SearchTest {

	public SearchTest() throws Exception{
		List<String> indexList = new ArrayList<String>();
		// 初始化疾病及疾病对应ID表
		MRecommendCost.IcdNameMap = new HashMap<String, Integer>();
		List<String> icdNameList = new ArrayList<String>();
		indexList = IOUtil.readStringListFromFile(
				new File(MRecommendCost.ICDNAME_TABLE_PATH), icdNameList);
		for(String index : indexList) {
			String[] strs = index.split(MRecommendCost.ATTR_STR);
			MRecommendCost.IcdNameMap.put(strs[0], Integer.parseInt(strs[1]));
		}
	}
	
	public void search(int icd_name_id, int sex, int ageStart, int ageEnd, 
			String symptoms) throws Exception {
		CloseableHttpClient httpclient = HttpClientUtils.getHttpClient();
		HttpPost post = HttpClientUtils.buildHttpPost(
				"http://192.168.0.171:8052/mrecommend/recommend");
		
		// 设置请求参数
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("icd_name_id", String.valueOf(icd_name_id)));
		nvps.add(new BasicNameValuePair("sex", String.valueOf(sex)));
		nvps.add(new BasicNameValuePair("ageStart", String.valueOf(ageStart)));
		nvps.add(new BasicNameValuePair("ageStart", String.valueOf(ageStart)));
		nvps.add(new BasicNameValuePair("ageEnd", String.valueOf(ageEnd)));
		nvps.add(new BasicNameValuePair("symptom", String.valueOf(symptoms)));
		post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
		
		String content = HttpClientCommon.execute(httpclient, post);
		System.out.println(content);
//		JSONObject obj = JSONObject.parseObject(content);
//		JSONObject ret = obj.getJSONObject("ret");
//		JSONArray meds = ret.getJSONArray("medicineList");
//		for(int i = 0; i < meds.size(); i++) {
//			JSONObject med = meds.getJSONObject(i);
//			String str = med.getString("ypmc");
//			String prob = med.getString("prob");
//			System.out.println(str + "\t" + prob);
//		}
	}
	
	public static void main(String[] args) throws Exception {
		SearchTest search = new SearchTest();
		
		String icd_name = "风湿性关节炎";
//		icd_name = "沙眼";
//		icd_name = "高血压";
		//icd_name = "高血压";
		long start = System.currentTimeMillis();
		if(MRecommendCost.IcdNameMap.containsKey(icd_name)) {
			int icd_name_id = MRecommendCost.IcdNameMap.get(icd_name);
			search.search(icd_name_id, 0, 0, 0, "");
		} else {
			System.out.println("数据库无此病");
		}
		long end = System.currentTimeMillis();
		System.out.println("查询共耗时：" + (end-start) + "ms");
		
//		search.search(106, 0, 0, 0, "");
//		long end = System.currentTimeMillis();
//		System.out.println("查询共耗时：" + (end-start) + "ms");
//		
	}
	
}
