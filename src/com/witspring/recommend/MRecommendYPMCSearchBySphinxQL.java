package com.witspring.recommend;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.witspring.db.mysql.SphinxQLManager;
import com.witspring.recommend.MRecommendConst;
import com.witspring.sougou.FileUtil;
import com.witspring.util.IOUtil;
import com.witspring.util.Pair;
import com.witspring.util.StrUtil;
import com.witspring.util.db.DBUtil;

public class MRecommendYPMCSearchBySphinxQL {
	
	private SphinxQLManager dbManager = null;
	
	public MRecommendYPMCSearchBySphinxQL() throws Exception {
		dbManager = SphinxQLManager.getInstance();
		
		// 导入根据疾病ID划分的索引表
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
		
		// 导入疾病及对应的疾病ID表
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
		List<String> symptomList = new ArrayList<String>();
		symptomList = IOUtil.readStringListFromFile(
				new File(MRecommendConst.SYMPTOM_TABLE_PATH), symptomList);
		for(String index : symptomList) {
			String[] strs = index.split(MRecommendConst.ATTR_STR);
			MRecommendConst.SymptomIdMap.put(strs[0], Integer.parseInt(strs[1]));
		}
		
		// 导入中药材词典(过滤(补充过滤,在进索引时已经进行过初步过滤了)推荐药品中的中药材)
		MRecommendConst.ChineseMedicineMap = new HashMap<String, Integer>();
		List<String> chineseMedicineList = new ArrayList<String>();
		chineseMedicineList = IOUtil.readStringListFromFile(
				new File(MRecommendConst.CHINESE_MEDICINE_PATH), chineseMedicineList);
		for(String chineseMedicine : chineseMedicineList) {
			MRecommendConst.ChineseMedicineMap.put(chineseMedicine, 1);
		}
		
		// 导入药品疾病关系表
		Map<String, Map<Integer, Double>> ypmcDiseaseCorrTempMap = 
				new HashMap<String, Map<Integer,Double>>();
		MRecommendConst.ypmcDiseaseCorrMap = new HashMap<String, Map<Integer,Double>>();
		String inputPath = "./data/dict/t_qb_zyzd_ypmc_3/";
		List<String> list = new ArrayList<String>();
		List<File> paths = FileUtil.getAllFiles(inputPath);
		for(File file : paths) {
			list = IOUtil.readStringListFromFile(file, list);
			for(String str : list) {
				String[] strs = str.split(MRecommendConst.ATTR_STR);
				
				Map<Integer, Double> temp = new HashMap<Integer, Double>();
				if(ypmcDiseaseCorrTempMap.containsKey(strs[1]))
					temp = ypmcDiseaseCorrTempMap.get(strs[1]);
				temp.put(Integer.parseInt(strs[4]), Double.parseDouble(strs[2]));
				ypmcDiseaseCorrTempMap.put(strs[1], temp);
			}
		}
		
		// 对药品下的疾病进行排序，排除掉指定顺序之外的疾病
		for(Map.Entry<String, Map<Integer, Double>> entry : ypmcDiseaseCorrTempMap.entrySet()) {
			List<Map.Entry<Integer, Double>> tempList = 
					MRecommendAlgo.sortIntDoubleDesc(entry.getValue());
			Map<Integer, Double> tempMap = new HashMap<Integer, Double>();
			int length = tempList.size() > MRecommendConst.YPMC_DISEASE_RANK ? 
				MRecommendConst.YPMC_DISEASE_RANK : tempList.size();
			for(int i = 0; i < length; i++) {
				tempMap.put(tempList.get(i).getKey(), tempList.get(i).getValue());
			}
			MRecommendConst.ypmcDiseaseCorrMap.put(entry.getKey(), tempMap);
		}
	}
	
	/**
	 * 根据疾病名称选择搜索索引.
	 * 
	 * @param icd_name_id
	 * @return String
	 */
	private static String getIndex(int icd_name_id) {
		String index = "";
		Map<Pair<Integer, Integer>, String> indexMap = MRecommendConst.IndexMap;
		for(Map.Entry<Pair<Integer, Integer>, String> entry : indexMap.entrySet()) {
			if(icd_name_id >= entry.getKey().first && icd_name_id < entry.getKey().second)
				index = entry.getValue();
		}
		
		return index;
	}
	
	public List<Pair<String, Integer>> query(int icd_name_id, int sex, int ageStart, 
			int ageEnd, int[] symptoms) throws Exception {
		String index = getIndex(icd_name_id);
		
		StringBuffer sbWhere = new StringBuffer();
		sbWhere.append(" icd_name_id=").append(icd_name_id).append(" ");
		if(sex != 0)
			sbWhere.append("AND sex=").append(sex).append(" ");
		if(ageEnd != 0)
			sbWhere.append("AND age BETWEEN ").append(ageStart).append(" AND ").append(ageEnd).append(" ");
		if(symptoms != null)
			sbWhere.append("AND symptom IN (").append(StrUtil.join(symptoms, ",")).append(")");
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ypmc, count(*) cnt FROM ").append(index)
			.append(" WHERE").append(sbWhere.toString())
			.append(" GROUP BY ypmc WITHIN GROUP ORDER BY weight() desc ")
			.append("ORDER BY cnt desc LIMIT ")
			.append("0,").append(MRecommendConst.SPHINX_YPSL)
			.append(" OPTION ranker=proximity");
		
		System.out.println(sb.toString());
		
		List<Pair<String, Integer>> ret = new ArrayList<Pair<String,Integer>>();
		PreparedStatement st = null;
		ResultSet rs = null;
		Connection conn = dbManager.getConnection();
		Map<String, Integer> chineseMedicineMap = MRecommendConst.ChineseMedicineMap;
	    Map<String, Map<Integer, Double>> ypmcDiseaseCorrMap = MRecommendConst.ypmcDiseaseCorrMap;
		try {
	    	st = conn.prepareStatement(sb.toString());
	    	rs = st.executeQuery();
	    	List<Pair<String, Integer>> ypmcs = new ArrayList<Pair<String, Integer>>();
        	while(rs.next()) {
				String ypmc = rs.getString(1);
				int cnt = Integer.parseInt(rs.getString(2));
				// 将中药材进行补充过滤
	            if(!chineseMedicineMap.containsKey(ypmc)) {
            		// 将与该疾病相关度不高的药品过滤
	            	ypmcs.add(new Pair<String, Integer>(ypmc, cnt));
	            }
			}
			
			// 计算推荐的药品与疾病的相关性
        	if(ageEnd == 0) {
        		for(Pair<String, Integer> ypmc : ypmcs) {
        			// 如果数量达到返回数量值，则直接返回
        			if(ret.size() >= MRecommendConst.YPSL)
        				break;
        			if(ypmcDiseaseCorrMap.containsKey(ypmc.first)) {
        				Map<Integer, Double> temp = ypmcDiseaseCorrMap
        						.get(ypmc.first);
        				if(temp.containsKey(icd_name_id))
        					ret.add(ypmc);
        			}
        		}
        	} else {
        		for(int i = 0; i < ypmcs.size() && ret.size() < MRecommendConst.YPSL; i++) {
        			ret.add(ypmcs.get(i));
        		}
        	}
		} catch (SQLException exception) {
			exception.printStackTrace();
		} finally {
			DBUtil.forceClose(rs);
			DBUtil.forceClose(st);
		}
	    
	    return ret;
	}
	
	public int queryTotal(int icd_name_id, int sex, int ageStart, int ageEnd, 
			int[] symptoms) throws Exception {
		String index = getIndex(icd_name_id);
		
		int ret = 0;
		StringBuffer sbWhere = new StringBuffer();
		sbWhere.append(" icd_name_id=").append(icd_name_id).append(" ");
		if(sex != 0)
			sbWhere.append("AND sex=").append(sex).append(" ");
		if(ageEnd != 0)
			sbWhere.append("AND age BETWEEN ").append(ageStart).append(" AND ").append(ageEnd).append(" ");
		if(symptoms != null)
			sbWhere.append("AND symptom IN (").append(StrUtil.join(symptoms, ",")).append(")");
		
		StringBuffer sb = new StringBuffer();
		sb.append("select count(*) cnt from ").append(index).append("_total")
			.append(" where").append(sbWhere.toString())
			.append(" limit 0,1");
		
		System.out.println(sb.toString());
		PreparedStatement st = null;
		ResultSet rs = null;
		Connection conn = dbManager.getConnection();
	    try {
	    	st = conn.prepareStatement(sb.toString());
	    	rs = st.executeQuery();
			while(rs.next()) {
				ret = rs.getInt(1);
			}
		} catch (SQLException exception) {
			exception.printStackTrace();
		} finally {
			DBUtil.forceClose(rs);
			DBUtil.forceClose(st);
		}
	    
	    return ret;
	}
	
	public JSONArray search(MRecommendCache cache, int icd_name_id, int sex, 
			int ageStart, int ageEnd, int[] symptoms) throws Exception {
		// 查询缓存中是否存在结果, 若无结果则去搜索引擎搜索
		JSONArray obj = cache.get(icd_name_id, symptoms, sex, ageStart, ageEnd);
		if(obj != null) {
			//System.out.println("直接从cache中返回");
			return obj;
		}
		
		long start = System.currentTimeMillis();
		List<Pair<String, Integer>> ret = query(icd_name_id, sex, ageStart, 
				ageEnd, symptoms);
		int total = queryTotal(icd_name_id, sex, ageStart, ageEnd, symptoms);
		JSONArray array = new JSONArray();
		for(Pair<String, Integer> pair : ret) {
			JSONObject objProb = new JSONObject();
			objProb.put("ypmc", pair.first);
			objProb.put("prob", ((double)pair.second/(double)total*0.9));
			array.add(objProb);
		}
		long end = System.currentTimeMillis();
		long time = end - start;
		// 如果处理时间超过500ms, 则将结果加入缓存中
		if(time > 500) {
			System.out.println("icd_name_id:" + icd_name_id + "\tsymptoms:" 
					+ StrUtil.join(symptoms,",") + "\tsex:" + sex + "\tageRange:" 
					+ ageStart + "-" + ageEnd + "\ttime:" + time 
					+ "\t查询超过阈值, 将结果放入缓存");
			cache.put(icd_name_id, symptoms, sex, ageStart, ageEnd, array);
		}
		
		return array;
	}
	
	public static void main(String[] args) throws Exception {
		MRecommendYPMCSearchBySphinxQL test = new MRecommendYPMCSearchBySphinxQL();
		MRecommendCache cache = new MRecommendCache();
		
		int icd_name_id = 106;
		int sex = 0;
		int ageStart = 10;
		int ageEnd = 0;
		String[] symptoms = null;
		int[] symptom_ids = null;
		if(symptoms != null) {
			symptom_ids = new int[symptoms.length];
			for(int i = 0; i < symptoms.length; i++) {
				symptom_ids[i] = MRecommendConst.SymptomIdMap.get(symptoms[i]);
			}
		}
		long start = System.currentTimeMillis();
		JSONArray ret = test.search(cache, icd_name_id, sex, ageStart, ageEnd, symptom_ids);
		System.out.println(ret);
		for(int i = 0; i < ret.size(); i++) {
			JSONObject med = ret.getJSONObject(i);
			String str = med.getString("ypmc");
			String prob = med.getString("prob");
			System.out.println(MRecommendConst.IcdNameIdMap.get(icd_name_id) + "\t" + str + "\t" + prob);
		}
		long end = System.currentTimeMillis();
		System.out.println("推荐共用时：" + (end-start) + "ms");
	}
	
}
