package com.witspring.recommend;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.witspring.mrecommend.conf.ConfigSingleton;
import com.witspring.mrecommend.conf.MRecommendConfig;
import com.witspring.sougou.FileUtil;
import com.witspring.util.IOUtil;
import com.witspring.util.Pair;

public class OutputDiseaseYpmcList {

	public OutputDiseaseYpmcList() throws Exception {
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
		
		// 初始化Sphinx的配置
		MRecommendConfig conf = ConfigSingleton.getMRecommendConfig();
		MRecommendConst.SphinxIP = conf.sphinxConf.server;
		MRecommendConst.SphinxPort = conf.sphinxConf.port;
		MRecommendConst.SphinxPortYpmcDisease = conf.sphinxConf.portYpmcDisease;
	}
	
	public void output() throws Exception {
		List<String> exsitDiseaseIdList = new ArrayList<String>();
		exsitDiseaseIdList = IOUtil.readStringListFromFile(
				new File("./data/dict/ExsitDisease.txt"), exsitDiseaseIdList);
		
		MRecommendYPMCSearch search = new MRecommendYPMCSearch();
		MRecommendCache cache = new MRecommendCache();
		
		PrintWriter pw = new PrintWriter(new File("c:/crh/work/data/DiseaseYPMC.txt"));
		for(String id : exsitDiseaseIdList) {
			int icd_name_id = Integer.parseInt(id);
			
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
			
			//long start1 = System.currentTimeMillis();
			JSONArray ret = search.search(cache, icd_name_id, sex, ageStart, 
					ageEnd, symptom_ids);
			
			for(int i = 0; i < ret.size(); i++) {
				JSONObject med = ret.getJSONObject(i);
				String str = med.getString("ypmc");
				String prob = med.getString("prob");
				pw.println(MRecommendConst.IcdNameIdMap.get(icd_name_id) + "\t" + str + "\t" + prob);
			}
			
			//long end1 = System.currentTimeMillis();
			//System.out.println("推荐共用时：" + (end1-start1) + "ms");
		}
		IOUtil.forceClose(pw);
	}
	
	public static void main(String[] args) throws Exception {
		OutputDiseaseYpmcList output = new OutputDiseaseYpmcList();
		output.output();
	}
	
}
