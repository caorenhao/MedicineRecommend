package com.witspring.mrecommend.front;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;

import com.sun.net.httpserver.HttpServer;
import com.witspring.mrecommend.conf.ConfigSingleton;
import com.witspring.mrecommend.conf.MRecommendConfig;
import com.witspring.net.common.WorkingDir;
import com.witspring.recommend.MRecommendAlgo;
import com.witspring.recommend.MRecommendCache;
import com.witspring.recommend.MRecommendConst;
import com.witspring.recommend.MRecommendYPMCSearch;
import com.witspring.recommend.MRecommendYPMCSearchBySphinxQL;
import com.witspring.sougou.FileUtil;
import com.witspring.util.IOUtil;
import com.witspring.util.LoggerConfig;
import com.witspring.util.PIDUtil;
import com.witspring.util.Pair;

/**
 * Http Server 启动类.
 * 
 * @author renhao.cao.
 *         Created 2014-9-25.
 */
public class IYQFrontMain {
	
	private Log LOGGER = LoggerConfig.getLog(getClass());
	
	private static MRecommendConfig conf;
	
	/**
	 * 初始化相关数据.
	 *
	 * @param db
	 * @param memMgr
	 * @param outputMgr
	 * @throws Exception
	 */
	public void init() throws Exception {
		// 导入根据疾病ID划分的索引表
		MRecommendConst.IndexMap = new HashMap<Pair<Integer, Integer>, String>();
		List<String> indexList = new ArrayList<String>();
		indexList = IOUtil.readStringListFromFile(
				new File(MRecommendConst.INDEX_ICDNAMEID_PATH), indexList);
		for(String index : indexList) {
			String[] strs = index.split(" ");
			String[] range = strs[0].split(",");
			MRecommendConst.IndexMap.put(new Pair<Integer, Integer>(
					Integer.parseInt(range[0]), Integer.parseInt(range[1])), strs[1]);
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
		List<String> list = new ArrayList<String>();
		List<File> paths = FileUtil.getAllFiles(MRecommendConst.YPMC_DISEASE_ROOT_PATH);
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
		conf = ConfigSingleton.getMRecommendConfig();
		MRecommendConst.SphinxIP = conf.sphinxConf.server;
		MRecommendConst.SphinxPort = conf.sphinxConf.port;
		MRecommendConst.SphinxPortYpmcDisease = conf.sphinxConf.portYpmcDisease;
		LOGGER.info("Use Sphinx " + MRecommendConst.SphinxIP + ":" 
				+ MRecommendConst.SphinxPort + "," + MRecommendConst.SphinxPortYpmcDisease);
		
		// 初始化参数
		MRecommendConst.SPHINX_MAX_QUERYS = conf.paramConf.sphinx_max_query;
		MRecommendConst.SPHINX_TIMEOUT = conf.paramConf.sphinx_timeout;
		MRecommendConst.SPHINX_YPSL = conf.paramConf.sphinx_ypsl;
		MRecommendConst.YPSL = conf.paramConf.ypsl;
		MRecommendConst.YPMC_DISEASE_RANK = conf.paramConf.ypmc_disease_rank;
		
		IYQFrontConst.mRecommendYPMCSearch = new MRecommendYPMCSearch();
		IYQFrontConst.mRecommendYPMCSearchBySphinxQL = new MRecommendYPMCSearchBySphinxQL();
		IYQFrontConst.mRecommendCache = new MRecommendCache();
		
		// 初始化查询时间过长的推荐结果
		IYQFrontConst.mRecommendCache.initData();
		System.out.println("The cache init end.");
		
		// 开启Http Server
		String ip = conf.httpConf.server;
		int port = conf.httpConf.port;
		HttpServer server = HttpServer.create(new InetSocketAddress(
				ip, port), 5);
		server.createContext("/", new IYQFrontHandler());
		// 创建线程池，否则HttpServer单线程
		//server.setExecutor(Executors.newCachedThreadPool());
		server.setExecutor(newCachedThreadPool());
		server.start();
		LOGGER.info("Start http server " + ip + ":" + port);
		System.out.println("Start http server " + ip + ":" + port);
	}
	
	/** 创建线程池*/
	public ExecutorService newCachedThreadPool() {
		int threadNum = conf.httpConf.threadNum;
        return new ThreadPoolExecutor(threadNum, Integer.MAX_VALUE,
                                      60L, TimeUnit.SECONDS,
                                      new SynchronousQueue<Runnable>());
    }
	
	public static void main(String[] args) throws Exception {
		IYQFrontMain main = new IYQFrontMain();
		
		MRecommendConfig conf = ConfigSingleton.getMRecommendConfig();
		WorkingDir wd = new WorkingDir(conf.logPath);
		PIDUtil.writePidFile(wd.getPidFile());
		wd.startLogClean();
		File logFile = new File(wd.getLogDir().getPath() + "/MRecommend");
		LoggerConfig.initLog(logFile.getPath());
		
		final Log LOGGER = LoggerConfig.getLog(IYQFrontMain.class);
		LOGGER.info("Process ID[" + PIDUtil.getPid() + "]");
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler(){
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				LOGGER.error("Catch Thread Exception: " + t, e);
			}
		});
		
		main.init();
	}
}
