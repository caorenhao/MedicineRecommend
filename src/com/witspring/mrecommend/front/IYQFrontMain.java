package com.witspring.mrecommend.front;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;

import com.sun.net.httpserver.HttpServer;
import com.witspring.mrecommend.conf.ConfigSingleton;
import com.witspring.mrecommend.conf.MRecommendConfig;
import com.witspring.net.common.WorkingDir;
import com.witspring.recommend.MRecommendCache;
import com.witspring.recommend.MRecommendCost;
import com.witspring.recommend.MRecommendYPMCSearch;
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
		MRecommendCost.IndexMap = new HashMap<Pair<Integer, Integer>, String>();
		List<String> indexList = new ArrayList<String>();
		indexList = IOUtil.readStringListFromFile(
				new File(MRecommendCost.INDEX_TABLE_PATH), indexList);
		for(String index : indexList) {
			String[] strs = index.split(" ");
			String[] range = strs[0].split(",");
			MRecommendCost.IndexMap.put(new Pair<Integer, Integer>(
					Integer.parseInt(range[0]), Integer.parseInt(range[1])), strs[1]);
		}
		
		// 导入症状及对应的症状ID表
		MRecommendCost.SymptomIdMap = new HashMap<String, Integer>();
		List<String> symptomList = new ArrayList<String>();
		symptomList = IOUtil.readStringListFromFile(
				new File(MRecommendCost.SYMPTOM_TABLE_PATH), symptomList);
		for(String index : symptomList) {
			String[] strs = index.split(MRecommendCost.ATTR_STR);
			MRecommendCost.SymptomIdMap.put(strs[0], Integer.parseInt(strs[1]));
		}
		
		// 导入中药材词典(过滤(补充过滤,在进索引时已经进行过初步过滤了)推荐药品中的中药材)
		MRecommendCost.ChineseMedicineMap = new HashMap<String, Integer>();
		List<String> chineseMedicineList = new ArrayList<String>();
		chineseMedicineList = IOUtil.readStringListFromFile(
				new File(MRecommendCost.CHINESE_MEDICINE_PATH), chineseMedicineList);
		for(String chineseMedicine : chineseMedicineList) {
			MRecommendCost.ChineseMedicineMap.put(chineseMedicine, 1);
		}
		
		// 初始化Sphinx的配置
		conf = ConfigSingleton.getMRecommendConfig();
		MRecommendCost.SphinxIP = conf.sphinxConf.server;
		MRecommendCost.SphinxPort = conf.sphinxConf.port;
		MRecommendCost.SphinxPortYpmcDisease = conf.sphinxConf.portYpmcDisease;
		LOGGER.info("Use Sphinx " + MRecommendCost.SphinxIP + ":" 
				+ MRecommendCost.SphinxPort + "," + MRecommendCost.SphinxPortYpmcDisease);
		
		// 初始化参数
		MRecommendCost.SPHINX_MAX_QUERYS = conf.paramConf.sphinx_max_query;
		MRecommendCost.SPHINX_TIMEOUT = conf.paramConf.sphinx_timeout;
		MRecommendCost.SPHINX_YPSL = conf.paramConf.sphinx_ypsl;
		MRecommendCost.YPSL = conf.paramConf.ypsl;
		MRecommendCost.YPMC_DISEASE_RANK = conf.paramConf.ypmc_disease_rank;
		
		// 初始化查询时间过长的推荐结果
		MRecommendCache cache = new MRecommendCache();
		cache.initData();
		System.out.println("The cache init end.");
		
		IYQFrontConst.mRecommendYPMCSearch = new MRecommendYPMCSearch();
		IYQFrontConst.mRecommendCache = new MRecommendCache();
		
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
