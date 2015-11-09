package com.witspring.recommend;

import java.util.Map;

import com.witspring.util.Pair;

public class MRecommendConst {

	/** 疾病id区间及对应的索引名称*/
	public final static String INDEX_ICDNAMEID_PATH = "./data/index/IndexRange_IcdNameId.txt";
	
	/** 症状id区间及对应的索引名称*/
	public final static String INDEX_SYMPTOMID_PATH = "./data/index/IndexRange_SymptomId.txt";
	
	/** 疾病id及对应的疾病名称*/
	public final static String ICDNAME_TABLE_PATH = "./data/dict/t_qb_icd_name_1.txt";
	
	/** 症状及对应的ID表路径*/
	public final static String SYMPTOM_TABLE_PATH = "./data/index/t_qb_zyzd_jieba_4_1.txt";
	
	/** 中药材词典路径*/
	public final static String CHINESE_MEDICINE_PATH = "./data/dict/zhongyao_new.txt";
	
	/** 药品名称与疾病之间的相关度数据表的路径*/
	public final static String YPMC_DISEASE_ROOT_PATH = "./data/dict/t_qb_zyzd_ypmc_3";
	
	/** 查询时间过长需要初始化时读入缓存的疾病id*/
	public final static String INIT_ICD_NAME_PATH = "./data/icd_name_id.txt";
	
	/** 部署程序路径*/
	public final static String PROGRAM_PATH = "/home/admin/iyq/MedicineRecommend";
	
	/** Sphinx服务器地址*/
	public static String SphinxIP;
	
	/** Sphinx服务器端口*/
	public static int SphinxPort;
	
	/** 药品与对应疾病相关度索引端口*/
	public static int SphinxPortYpmcDisease;
	
	/** 疾病id对应的索引名称*/
	public static Map<Pair<Integer, Integer>, String> IndexMap;
	
	/** 疾病id对应的疾病名称*/
	public static Map<Integer, String> IcdNameIdMap;
	
	/** 疾病对应的疾病id*/
	public static Map<String, Integer> IcdNameMap;
	
	/** 症状名称对应的症状ID*/
	public static Map<String, Integer> SymptomIdMap;
	
	/** 症状ID对于的症状名称*/
	public static Map<Integer, String> SymptomIdToNameMap;
	
	/** 中药材词典*/
	public static Map<String, Integer> ChineseMedicineMap;
	
	/** 药品与疾病的相关性集合*/
	public static Map<String, Map<Integer, Double>> ypmcDiseaseCorrMap;
	
	/** HDFS导出数据的分隔符*/
	public static String ATTR_STR = "";
	
	/** Sphinx 多任务的任务上限*/
	public static int SPHINX_MAX_QUERYS = 50;
	
	/** Sphinx连接超时时间*/
	public static int SPHINX_TIMEOUT = 30000;
	
	/** 返回的药品数量*/
	public static int YPSL = 20;
	
	/** 药品对应的疾病相关度阈值(使用一种药品的所有疾病排名中取前n的疾病)*/
	public static int YPMC_DISEASE_RANK = 30;
	
	/** 一次从 Sphinx 中取出的药品数量*/
	public static int SPHINX_YPSL = 100;
}
