package com.witspring.recommend;

import com.witspring.mrecommend.conf.ConfigSingleton;
import com.witspring.mrecommend.conf.MRecommendConfig;
import com.witspring.util.JedisWrapper;

public class MRecommendCacheMgr {

	private static JedisWrapper jedis;
	
	public MRecommendCacheMgr() throws Exception {
		MRecommendConfig conf = ConfigSingleton.getMRecommendConfig();
		String server = conf.redisConf.server;
		int port = conf.redisConf.port;
		int maxSize = conf.redisConf.maxSize;
		jedis = JedisWrapper.getInstance(server, port, maxSize);
	}
	
	/** 删除所有数据库*/
	public void delAll() {
		System.out.println("清空全部数据库" + jedis.flushDB());
	}
	
	/** 删除指定数据库*/
	public void del(String key) {
		jedis.del(key);
		System.out.println("删除数据库 " + key + " 成功");
	}
	
	public static void main(String[] args) throws Exception {
		MRecommendCacheMgr test = new MRecommendCacheMgr();
		test.delAll();
		test.del("8169");
	}
	
}
