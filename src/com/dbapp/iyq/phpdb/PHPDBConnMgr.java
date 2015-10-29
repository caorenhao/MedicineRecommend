package com.dbapp.iyq.phpdb;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.witspring.mrecommend.conf.MRecommendConfig.PhpDBConf;
import com.witspring.util.db.ConnInfo;
import com.witspring.util.db.DBConnWrapper;

/**
 * PHP数据库的总连接
 * @author Vernkin
 *
 */
public class PHPDBConnMgr {
	
	private static Set<PHPDBConnMgr> globalDbs = new HashSet<PHPDBConnMgr>();
	
	
	private Log LOGGER = LogFactory.getLog(getClass());
	
	/**
	 * 共享数据库连接
	 */
	private DBConnWrapper commonConn;
	
	/** 用户数据库连接映射 */
	private Map<String, DBConnWrapper> userConnMap = 
			new HashMap<String, DBConnWrapper>();
	
	public PHPDBConnMgr(PhpDBConf conf) throws Exception {
		this(conf.host, conf.port, conf.user, conf.pwd);
	}
	
	public PHPDBConnMgr(String host, String port, String user, String pwd) 
			throws Exception {
		commonConn = new DBConnWrapper(new ConnInfo("MYSQL", 
				host, port, "yqpalmcommon", user, pwd));
		
		synchronized(globalDbs) {
			globalDbs.add(this);
		}
	}
	
	/**
	 * 释放所有的数据库连接资源
	 */
	public synchronized void closeAll() throws Exception {
		LOGGER.info("closeAll and release all resource");
		synchronized(globalDbs) {
			globalDbs.remove(this);
		}
		try {
			commonConn.releaseRes();
		} catch (Exception ex) {
			LOGGER.warn("Fail to release commondb", ex);
		}
		
		for(Map.Entry<String, DBConnWrapper> e : userConnMap.entrySet()) {
			try {
				e.getValue().releaseRes();
			} catch (Exception ex) {
				LOGGER.warn("Fail to release userdb: " + e.getKey(), ex);
			}
		}
	}
	
	public synchronized Set<String> getUserDBNames() {
		Set<String> dbNames = new HashSet<String>();
		dbNames.addAll(userConnMap.keySet());
		return dbNames;
	}
	
	public synchronized Map<String, DBConnWrapper> getUserDBs() {
		Map<String, DBConnWrapper> dbs = new HashMap<String, DBConnWrapper>();
		dbs.putAll(userConnMap);
		return dbs;
	}
	
	/**
	 * 获取对应数据库的连接
	 * @param dbName
	 * @return
	 */
	public synchronized DBConnWrapper getUserDB(String dbName) {
		return userConnMap.get(dbName);
	}
	
}

