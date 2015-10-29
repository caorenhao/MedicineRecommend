package com.witspring.db.mysql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.witspring.util.db.ConfigurationException;
import com.witspring.util.db.ConnInfo;
import com.witspring.util.db.DBConnFactory;
import com.witspring.util.db.DBFactoryException;

/**
 * MySQL数据库配置类.
 *
 * @author renhao.cao.
 *         Created 2015-10-29.
 */
public class CopyOfSphinxQLManager {
	
	public static Statement sm;
	
	public static Connection conn;
	
	
	public CopyOfSphinxQLManager() {
		String host = "192.168.0.171";
		String port = "9406";
		String dbName = "";
		String user = "";
		String pwd = "";
		ConnInfo connInfo;
		try {
			connInfo = new ConnInfo("MYSQL", host, port, dbName, user, pwd);
			conn = DBConnFactory.getConnection(connInfo);
			sm = conn.createStatement();
		} catch (ConfigurationException exception) {
			exception.printStackTrace();
		} catch (DBFactoryException exception) {
			exception.printStackTrace();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
	}
	
	public CopyOfSphinxQLManager(String host, String port, String dbName, String user, 
			String pwd) {
		ConnInfo connInfo;
		try {
			connInfo = new ConnInfo("MYSQL", host, port, dbName, user, pwd);
			conn = DBConnFactory.getConnection(connInfo);
			sm = conn.createStatement();
		} catch (ConfigurationException exception) {
			exception.printStackTrace();
		} catch (DBFactoryException exception) {
			exception.printStackTrace();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
	}
}
