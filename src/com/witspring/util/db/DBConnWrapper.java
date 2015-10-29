package com.witspring.util.db;

import java.sql.Connection;

import com.witspring.util.LocalResLifeItf;
import com.witspring.util.LocalResLifeWrapper;

/**
 * 关系型数据连接的包装类
 * @author Vernkin
 *
 */
public class DBConnWrapper implements LocalResLifeItf {

	/**
	 * 数据库连接过期的时间
	 */
	public static final int EXPIRE_SECS = 10 * 60;
	
	private ConnInfo connInfo;
	
	private LocalResLifeWrapper<DBConnWrapper> resWrapper;
	
	private Connection conn;
	
	public DBConnWrapper(ConnInfo connInfo) {
		this.connInfo = connInfo;
		resWrapper = new LocalResLifeWrapper<DBConnWrapper>(this, false, EXPIRE_SECS);
	}
	
	public Connection getConnection() throws Exception {
		return resWrapper.getRes().conn;
	}
	
	@Override
	public void releaseRes() throws Exception {
		DBUtil.forceClose(conn);
		conn = null;
	}

	@Override
	public void acquireRes() throws Exception {
		conn = DBConnectMgr.getConnection(connInfo);
	}

	@Override
	public boolean hasResource() {
		return conn != null;
	}

	public ConnInfo getConnInfo() {
		return connInfo;
	}
}
