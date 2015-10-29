package com.witspring.util.db;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.List;
import java.util.Properties;

import com.witspring.util.EncryptUtil;

/**
 * Managing connecting
 * 
 * @author vernkin
 * 
 */
public class DBConnFactory {

	private DBConnFactory() {

	}

	/**
	 * Get Target database connection
	 * 
	 * @param info
	 * @return
	 * @throws Exception
	 */
	public static Connection getConnection(ConnInfo info)
			throws DBFactoryException {
		Connection conn = null;

		DBVendorInfo dbvi = info.getVendorInfo();
		//DBVendor vendor = dbvi.getVendor();

		// set username, password and database (name)
		Properties prps = new Properties();
		String userID = info.getUser();
		String userPWD = info.getPwd();
		if (EncryptUtil.isHexa(userID)) {
			if (userID.length() > 4 && userPWD.length() > 4) {
				userID = userID.substring(4, userID.length());
				userPWD = userPWD.substring(4, userPWD.length());
			}
			userID = EncryptUtil.decryptString(userID);
			userPWD = EncryptUtil.decryptString(userPWD);
		}
		prps.setProperty("user", userID);
		prps.setProperty("password", userPWD);
		String databaseName = info.getDatabase();
		if (databaseName != null && databaseName.isEmpty() == false
				&& dbvi.getVendor() != DBVendor.SYMFOWARE) {
			prps.setProperty("DatabaseName", databaseName);
		}

		String url = dbvi.getUrl(info.getHost(), info.getPort(), info
				.getDatabase());
		try {
			if (dbvi.isExternalLibrary()) {
				Driver driver = loadDriverClass(dbvi.getLibs(), dbvi
						.getDriver());
				conn = driver.connect(url, prps);
			} else {
				Class.forName(dbvi.getDriver());
				conn = DriverManager.getConnection(url, prps);
			}
		} catch (Throwable t) {
			throw new DBFactoryException("Error when connects with " + 
					url + ":" + t.getMessage(), t);
		}

		return conn;
	}

	private static Driver loadDriverClass(List<String> jarFiles,
			String driverPath) throws Exception {
		URL url[] = new URL[jarFiles.size()];
		URLClassLoader loader = null;
		try {
			int i = 0;
			for (String jar : jarFiles) {
				url[i] = new File(jar).toURI().toURL();
				++i;
			}

			loader = new URLClassLoader(url);
		} catch (MalformedURLException e) {
			throw e;
		}

		Driver driver = (Driver) loader.loadClass(driverPath).newInstance();
		DriverManager.registerDriver(driver);
		return driver;
	}
}
