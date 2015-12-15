package com.witspring.recommend;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
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
import com.witspring.mrecommend.conf.ConfigSingleton;
import com.witspring.mrecommend.conf.MRecommendConfig;
import com.witspring.sougou.FileUtil;
import com.witspring.util.IOUtil;
import com.witspring.util.Pair;
import com.witspring.util.StrUtil;

/**
 * 药物推荐搜索.
 * 将计算药物与疾病相关度的方法转变到内存中使用Map来实现(查询速度提高一倍)
 * 
 * @author renhao.cao.
 *         Created 2015年10月26日.
 */
public class MRecommendYPMCSearch {
	
	/**
	 * 初始化疾病ID对应的索引列表.
	 *
	 * @throws Exception
	 */
	public static void init() throws Exception {
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
				//System.out.println(str);
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
		
		// 初始化Sphinx的配置
		MRecommendConfig conf = ConfigSingleton.getMRecommendConfig();
		MRecommendConst.SphinxIP = conf.sphinxConf.server;
		MRecommendConst.SphinxPort = conf.sphinxConf.port;
		MRecommendConst.SphinxPortYpmcDisease = conf.sphinxConf.portYpmcDisease;
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
	
	/**
	 * 按照条件查找所使用的药物.
	 * 	将症状当作过滤条件
	 * @param icd_name_id 疾病编号
	 * @param sex 性别
	 * @param ageStart 年龄开始
	 * @param ageEnd 年龄结束
	 * @param symptoms 症状(0 表示复诊病例的症状)
	 * @return List<Pair<String, Integer>>
	 */
	public static List<Pair<String, Integer>> searchYpmc_mva(int icd_name_id, 
			int sex, int ageStart, int ageEnd, int[] symptoms) {
        String index = getIndex(icd_name_id);
        List<Pair<String, Integer>> ret = new ArrayList<Pair<String, Integer>>();
        SphinxClient cl = new SphinxClient();
        try {
			cl.SetServer(MRecommendConst.SphinxIP, MRecommendConst.SphinxPort);
	        cl.SetLimits(0, MRecommendConst.SPHINX_YPSL);
	        cl.SetConnectTimeout(MRecommendConst.SPHINX_TIMEOUT);
	        
	        // 过滤条件
	        // 疾病过滤
	        cl.SetFilter("icd_name_id", icd_name_id, false);
	        // 性别过滤
	        if(sex != 0)
	        	cl.SetFilter("sex", sex, false);
	        // 年龄过滤
	        if(ageEnd != 0)
	        	cl.SetFilterRange("age", ageStart, ageEnd, false);
	        // 症状过滤
	        if(symptoms != null) {
	        	// 并的关系
	        	/*
	        	for(int symptom : symptoms) {
	        		cl.SetFilter("symptom", symptom, false);
	        	}
	        	*/
	        	// 或的关系
	        	cl.SetFilter("symptom", symptoms, false);
	        }
	        
	        // 分组条件
	        cl.SetSelect("ypmc, @count");
	        cl.SetGroupBy("ypmc", SphinxClient.SPH_GROUPBY_ATTR, "@count desc");
	        
	        // 在Sphinx中搜索
	        SphinxResult res = cl.Query("", index);
	        
	        if(res != null) {
	        	List<Pair<String, Integer>> ypmcs = new ArrayList<Pair<String, Integer>>();
	        	Map<String, Integer> chineseMedicineMap = MRecommendConst.ChineseMedicineMap;
	        	Map<String, Map<Integer, Double>> ypmcDiseaseCorrMap = MRecommendConst.ypmcDiseaseCorrMap;
	        	for (int i = 0; i < res.matches.length; i++){
		            SphinxMatch info = res.matches[i];
		            String ypmc = info.attrValues.get(0).toString();
		            String cnt = info.attrValues.get(1).toString();
		            
		            // 将中药材进行补充过滤
		            if(!chineseMedicineMap.containsKey(ypmc)) {
	            		// 将与该疾病相关度不高的药品过滤
		            	ypmcs.add(new Pair<String, Integer>(ypmc, Integer.parseInt(cnt)));
		            }
		        }
		        
	        	// 计算推荐的药品与疾病的相关性
	        	if(ageEnd == 0) {
	        		for(Pair<String, Integer> ypmc : ypmcs) {
	        			// 如果数量达到返回数量值，则直接返回
	        			if(ret.size() >= MRecommendConst.YPSL)
	        				return ret;
	        			
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
	 * 获取指定索引中的Doc总数
	 * 
	 * @param host 
	 * @param port 
	 * @param index 
	 */
	public static int getTotal_mva(int icd_name_id, int sex, int ageStart, 
			int ageEnd, int[] symptoms) {
		SphinxClient cl = new SphinxClient();
		String index = getIndex(icd_name_id);
		index = index + "_total";
		
        int total = 0;
        try {
			cl.SetServer(MRecommendConst.SphinxIP, MRecommendConst.SphinxPort);
			cl.SetMatchMode (SphinxClient.SPH_MATCH_EXTENDED2);
	        cl.SetLimits (0, 10);
	        cl.SetConnectTimeout(MRecommendConst.SPHINX_TIMEOUT);
	        
	        // 过滤条件
	        cl.SetFilter("icd_name_id", icd_name_id, false);
	        if(sex != 0)
	        	cl.SetFilter("sex", sex, false);
	        if(ageEnd != 0)
	        	cl.SetFilterRange("age", ageStart, ageEnd, false);
	        if(symptoms != null) {
	        	// 并的关系
	        	/*
	        	for(int symptom : symptoms) {
	        		cl.SetFilter("symptom", symptom, false);
	        	}
	        	*/
	        	// 或的关系
	        	cl.SetFilter("symptom", symptoms, false);
	        }
	        
	        SphinxResult res = cl.Query("", index);
	        total = res.totalFound;
	        //System.out.println("total_found:" + res.totalFound);
	        //System.out.println("time:" + res.time);
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
        
        return total;
	}
	
	public void outputAllIcdYpmcSearch() throws Exception {
		// 测试所有疾病需要的最大搜索时间
		Map<String, Integer> topSearchTime = new HashMap<String, Integer>();
		FileWriter fw = new FileWriter(new File(""));
		for(int i = 1; i < 12000; i++) {
			if(MRecommendConst.IcdNameIdMap.containsKey(i)) {
				System.out.println("Process to " + i);
				long start1 = System.currentTimeMillis();
				List<Pair<String, Integer>> ret = MRecommendYPMCSearch
						.searchYpmc_mva(i, 0, 2, 0, null);
				fw.write("疾病id:" + i);
				fw.write("疾病:" + MRecommendConst.IcdNameIdMap.get(i) + "\n");
				fw.write("推荐结果：" + StrUtil.join(ret, ",") + "\n");
				long end1 = System.currentTimeMillis();
				fw.write("推荐共用时：" + (end1-start1) + "ms\n");
				fw.write("\n");
				topSearchTime.put(MRecommendConst.IcdNameIdMap.get(i), 
						(int)(end1-start1));
			}
		}
		IOUtil.forceClose(fw);
		
		PrintWriter pw = new PrintWriter(new File("c:/crh/work/ret2.txt"));
		List<Map.Entry<String, Integer>> list = MRecommendAlgo.sortValueDesc(topSearchTime);
		for(Map.Entry<String, Integer> entry : list) {
			pw.println(entry.getKey() + "\t" + entry.getValue());
		}
		IOUtil.forceClose(pw);
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
		List<Pair<String, Integer>> ret = searchYpmc_mva(icd_name_id, sex, 
				ageStart, ageEnd, symptoms);
		int total = getTotal_mva(icd_name_id, sex, ageStart, ageEnd, symptoms);
		JSONArray array = new JSONArray();
		for(Pair<String, Integer> pair : ret) {
			JSONObject objProb = new JSONObject();
			objProb.put("ypmc", pair.first);
			objProb.put("prob", ((double)pair.second/(double)total*0.9));
			//System.out.println(objProb.toJSONString());
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
	
	/**
	 * TODO Put here a description of what this method does.
	 * 
	 * @param args 
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		init();
		
		MRecommendYPMCSearch search = new MRecommendYPMCSearch();
		MRecommendCache cache = new MRecommendCache();
		//search.outputAllIcdYpmcSearch();
		
		int icd_name_id = 21598;
		int sex = 0;
		int ageStart = 10;
		int ageEnd = 0;
		String[] symptoms = {"咳嗽", "流涕"};
		int[] symptom_ids = null;
		if(symptoms != null) {
			symptom_ids = new int[symptoms.length];
			for(int i = 0; i < symptoms.length; i++) {
				symptom_ids[i] = MRecommendConst.SymptomIdMap.get(symptoms[i]);
			}
		}
		
		long start1 = System.currentTimeMillis();
		JSONArray ret = search.search(cache, icd_name_id, sex, ageStart, 
				ageEnd, symptom_ids);
		System.out.println(ret);
		for(int i = 0; i < ret.size(); i++) {
			JSONObject med = ret.getJSONObject(i);
			String str = med.getString("ypmc");
			String prob = med.getString("prob");
			System.out.println(MRecommendConst.IcdNameIdMap.get(icd_name_id) + "\t" + str + "\t" + prob);
		}
		long end1 = System.currentTimeMillis();
		System.out.println("推荐共用时：" + (end1-start1) + "ms");
	}
}
