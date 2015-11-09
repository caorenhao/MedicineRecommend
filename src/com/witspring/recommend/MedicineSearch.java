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

/**
 * 药物推荐搜索.
 *
 * @author renhao.cao.
 *         Created 2015年10月7日.
 */
public class MedicineSearch {
	
	/**
	 * 初始化疾病ID对应的索引列表.
	 *
	 * @throws Exception
	 */
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
		
		MRecommendConfig conf = ConfigSingleton.getMRecommendConfig();
		MRecommendConst.SphinxIP = conf.sphinxConf.server;
		MRecommendConst.SphinxPort = conf.sphinxConf.port;
	}
	
	/**
	 * 根据疾病名称选择搜索索引.
	 * 
	 * @param icd_name_id
	 * @return String
	 */
	private static String getIndex(int icd_name_id) {
		String index = "";
		for(Map.Entry<Pair<Integer, Integer>, String> entry : MRecommendConst.IndexMap.entrySet()) {
			if(icd_name_id >= entry.getKey().first && icd_name_id < entry.getKey().second)
				index = entry.getValue();
		}
		
		return index;
	}
	
	/**
	 * 按照条件查找所使用的药物.
	 * 
	 * @param icd_name_id 疾病编号
	 * @param ageStart 年龄开始
	 * @param ageEnd 年龄结束
	 * @param symptoms 症状
	 * @return List<String>
	 */
	public static List<String> searchYpmc(int icd_name_id, float ageStart, float ageEnd, 
			String[] symptoms) {
		SphinxClient cl = new SphinxClient();
        String index = getIndex(icd_name_id);
        index = index + "_1";
        
        List<String> ret = new ArrayList<String>();
        try {
			cl.SetServer (MRecommendConst.SphinxIP, MRecommendConst.SphinxPort);
	        cl.SetMatchMode (SphinxClient.SPH_MATCH_EXTENDED2);
//	        cl.SetRankingMode(SphinxClient.SPH_RANK_EXPR,
//	        		"doc_word_count*1000 + (sum(hit_count)/(sum(hit_count)+doc_word_count))*1000");
//	        cl.SetSortMode(SphinxClient.SPH_SORT_RELEVANCE,"");
	        cl.SetLimits (0, 20);
	        cl.SetConnectTimeout(30000);
	        
	        // 过滤条件
	        cl.SetFilter("icd_name_id", icd_name_id, false);
	        if(ageEnd != 0)
	        	cl.SetFilterFloatRange("age", ageStart, ageEnd, false);
	        
	        // 分组条件
	        cl.SetSelect("ypmc, @count");
	        cl.SetGroupBy("ypmc", SphinxClient.SPH_GROUPBY_ATTR, "@count desc");
	        
	        
	        // 查询条件
	        String query = "";
	        if(symptoms != null) {
	        	StringBuffer sb = new StringBuffer();
		        sb.append("\"").append(symptoms[0]).append("\"");
		        for(int i = 1; i < symptoms.length; i++) {
		        	sb.append(" & \"").append(symptoms[i]).append("\"");
		        }
		        query = sb.toString();
	        }
	        //System.out.println("查询条件：" + query);
	        SphinxResult res = cl.Query(query, index);
	        
	        if(res != null) {
	        	//System.out.println("总结果集：" + res.totalFound + ",返回结果集：" 
	        			//+ res.matches.length);
		        for (int j=0; j<res.matches.length; j++){
		            SphinxMatch info = res.matches[j];
		            //System.out.println("药物：" + info.attrValues.get(0) + "\t" 
		            		//+ info.attrValues.get(1));
		            ret.add(info.attrValues.get(0).toString());
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
		
		int icd_name_id = 106;
		float ageStart = 50f;
		float ageEnd = 0f;
		String[] symptoms = {"咳嗽"};
		
		long start2 = System.currentTimeMillis();
		MedicineSearch.searchYpmc(icd_name_id, ageStart, ageEnd, symptoms);
		long end2 = System.currentTimeMillis();
		System.out.println("推荐共用时：" + (end2-start2) + "ms");
		
	}
}
