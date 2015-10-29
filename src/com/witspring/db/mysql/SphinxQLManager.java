package com.witspring.db.mysql;

import java.sql.Connection;
import java.sql.Statement;

import com.witspring.util.db.ConnInfo;
import com.witspring.util.db.DBConnWrapper;
import com.witspring.util.db.DBUtil;

/**
 * MySQL数据库配置类.
 *
 * @author renhao.cao.
 *         Created 2015-10-29.
 */
public class SphinxQLManager {
	
	public static Statement sm;
	
	//public static Connection conn;
	
	private static SphinxQLManager sphinxQLMgr;;
	
	public static SphinxQLManager getInstance() throws Exception {
		ConnInfo connInfo = new ConnInfo("MYSQL", "192.168.0.171", 
				"9406", "", "", "");
		SphinxQLManager dbmgr = sphinxQLMgr;
		if(dbmgr == null) {
			dbmgr = new SphinxQLManager(connInfo);
			sphinxQLMgr = dbmgr;
		}
		
		return dbmgr;
	}
	
	private DBConnWrapper conn;
	
	private SphinxQLManager(ConnInfo connInfo) throws Exception {
		conn = new DBConnWrapper(connInfo);
	}
	
	/**
	 * 获取数据库连接类.
	 *
	 * @return Connection
	 * @throws Exception
	 */
	public Connection getConnection() throws Exception {
		return conn.getConnection();
	}
	
	/**
	 * 执行更新操作.
	 *
	 * @param sql
	 * @return int
	 * @throws Exception
	 */
	public int executeUpdate(String sql) throws Exception {
	 	Statement stat = null;
		try {	
			stat = conn.getConnection().createStatement();
			return stat.executeUpdate(sql);
		} catch(Exception ex) {
			throw ex;
		} finally {
			DBUtil.forceClose(stat);
		}
	}
	
//	public SphinxQLManager() {
//		String host = "192.168.0.171";
//		String port = "9406";
//		String dbName = "";
//		String user = "";
//		String pwd = "";
//		ConnInfo connInfo;
//		try {
//			connInfo = new ConnInfo("MYSQL", host, port, dbName, user, pwd);
//			conn = DBConnFactory.getConnection(connInfo);
//			sm = conn.createStatement();
//		} catch (ConfigurationException exception) {
//			exception.printStackTrace();
//		} catch (DBFactoryException exception) {
//			exception.printStackTrace();
//		} catch (SQLException exception) {
//			exception.printStackTrace();
//		}
//	}
//	
//	public SphinxQLManager(String host, String port, String dbName, String user, 
//			String pwd) {
//		ConnInfo connInfo;
//		try {
//			connInfo = new ConnInfo("MYSQL", host, port, dbName, user, pwd);
//			conn = DBConnFactory.getConnection(connInfo);
//			sm = conn.createStatement();
//		} catch (ConfigurationException exception) {
//			exception.printStackTrace();
//		} catch (DBFactoryException exception) {
//			exception.printStackTrace();
//		} catch (SQLException exception) {
//			exception.printStackTrace();
//		}
//	}
}
