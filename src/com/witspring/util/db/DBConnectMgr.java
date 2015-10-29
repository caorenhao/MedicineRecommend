/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.witspring.util.db;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author vernkin
 */
public class DBConnectMgr {

    private static Connection connectMysql(String dbHost, String dbPort,
            String dbName, String dbUserName, String dbPassword) throws Exception {
        StringBuilder urlTem = new StringBuilder();

        String enCoding = "?useUnicode=true&characterEncoding=utf8&autoReconnect=true";
        urlTem.append("jdbc:mysql://");
        urlTem.append(dbHost);
        urlTem.append(":");
        urlTem.append(dbPort);
        urlTem.append("/");
        urlTem.append(dbName);
        urlTem.append(enCoding);
        String url = urlTem.toString();
        try
        {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            return DriverManager.getConnection(url, dbUserName, dbPassword);
        }catch(Exception e){
            throw new Exception("Fail to connect database with url=" + url, e);
        }
    }

    public static Connection getConnection(DBVendorInfo vendor, String dbHost, String dbPort,
            String dbName, String dbUserName, String dbPassword) throws Exception {
        switch(vendor.getVendor()) {
            case MYSQL:
                return connectMysql(dbHost, dbPort, dbName, dbUserName, dbPassword);
            default:
                throw new IllegalArgumentException("Can't support DB vendor: " + vendor);
        }
    }
    
    public static Connection getConnection(ConnInfo connInfo) throws Exception {
        return getConnection(connInfo.getVendorInfo(), connInfo.getHost(), connInfo.getPort(), 
        		connInfo.getDatabase(), connInfo.getUser(), connInfo.getPwd());
    }
}
