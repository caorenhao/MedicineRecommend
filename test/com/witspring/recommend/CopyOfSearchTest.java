package com.witspring.recommend;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.witspring.http.HttpClientCommon;
import com.witspring.http.HttpClientUtils;
import com.witspring.util.IOUtil;

public class CopyOfSearchTest {

	public CopyOfSearchTest() throws Exception{
		List<String> indexList = new ArrayList<String>();
		// 初始化疾病及疾病对应ID表
		MRecommendConst.IcdNameMap = new HashMap<String, Integer>();
		List<String> icdNameList = new ArrayList<String>();
		indexList = IOUtil.readStringListFromFile(
				new File(MRecommendConst.ICDNAME_TABLE_PATH), icdNameList);
		for(String index : indexList) {
			String[] strs = index.split(MRecommendConst.ATTR_STR);
			MRecommendConst.IcdNameMap.put(strs[0], Integer.parseInt(strs[1]));
		}
	}
	
	public void search(int icd_name_id, int sex, int ageStart, int ageEnd, 
			String symptoms) throws Exception {
		CloseableHttpClient httpclient = HttpClientUtils.getHttpClient();
//		HttpPost post = HttpClientUtils.buildHttpPost(
//				"http://192.168.0.171:8052/mrecommend/recommend");
		
//		HttpPost post = HttpClientUtils.buildHttpPost(
//				"http://127.0.0.1:8080/SpringMVC/getMedicineList");
		
		String url = "http://localhost:8080/getMedicineList";
		url = url + "?icd_name_id="+icd_name_id + "&sex=" + sex + "&ageStart="
				+ageStart+ "&ageEnd="+ageEnd;
		HttpGet get = HttpClientUtils.buildHttpGet(url);
		
		String content = HttpClientCommon.execute(httpclient, get);
		System.out.println(content);
		JSONArray meds = JSONArray.parseArray(content);
		for(int i = 0; i < meds.size(); i++) {
			JSONObject med = meds.getJSONObject(i);
			String str = med.getString("ypmc");
			String prob = med.getString("prob");
			System.out.println(str + "\t" + prob);
		}
	}
	
	public static void main(String[] args) throws Exception {
		CopyOfSearchTest search = new CopyOfSearchTest();
		
		String icd_name = "感冒";
//		icd_name = "沙眼";
//		icd_name = "高血压";
		//icd_name = "高血压";
		long start = System.currentTimeMillis();
		if(MRecommendConst.IcdNameMap.containsKey(icd_name)) {
			int icd_name_id = MRecommendConst.IcdNameMap.get(icd_name);
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
