package com.witspring.recommend;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

import com.witspring.util.IOUtil;

public class PressureTest { //用于管理线程和提供线程服务的类

	private ExecutorService exe = null;//线程池
	private static final int POOL_SIZE = 10;//线程池的容量
 
	public PressureTest() throws Exception {
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
		
		exe = Executors.newFixedThreadPool(POOL_SIZE);//创建线程池
		System.out.println("the server is ready...");
	}
 
	public void server(int poolNums) {
		int i = 0;
		while(i < poolNums) {
			exe.execute(new Worker(i));//运行线程池
			i++;
		}
	}
 
	class Worker implements Runnable { //工作线程，线程要完成的工作在此类中实现  
		int id;
		Worker(int id) {
			this.id = id;
		}
		public void run() {
			try {
				SearchTest search = new SearchTest();
				String icd_name = "高血压";
				if(MRecommendCost.IcdNameMap.containsKey(icd_name)) {
					int icd_name_id = MRecommendCost.IcdNameMap.get(icd_name);
					Random rand = new Random();
					icd_name_id = rand.nextInt(10000);
					long start = System.currentTimeMillis();
					search.search(icd_name_id, 0, 0, 0, "");
					long end = System.currentTimeMillis();
					System.out.println("task " + id + ":start" + ",疾病 " 
							+ icd_name_id + " 查询共耗时：" + (end-start) + "ms");
				} else {
					System.out.println("数据库无此病");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			//System.out.println("task " + id + ":start");//具体要做的事
		}
	} 
 
	public static void main(String[] args) throws Exception {
		new PressureTest().server(100);
	}
}