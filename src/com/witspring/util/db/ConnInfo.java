package com.witspring.util.db;

import com.witspring.util.StrUtil;

/**
 * Connection Information
 * @author vernkin
 *
 */
public class ConnInfo {

    /**
     * Default Constructor
     */
    public ConnInfo() {
        
    }
    
    /**
     * Constructor with initialized parameters 
     * @param vendorName vendor name
     * @param host host, IP or URL
     * @param port the port for host
     * @param database database name to be connected
     * @param username user name
     * @param pwd password
     * @throws ConfigurationException throws if the 
     * parameter is incorrect
     */
    public ConnInfo(String vendorName, String host, String port, 
            String database, String username, String pwd) 
            throws ConfigurationException {
        this.setVendor(vendorName);
        this.setHost(host);
        this.setPort(port);
        this.setDatabase(database);
        this.setUser(username);
        this.setPwd(pwd);        
    }
    
    
    /**
     * Set the vendorName and update the vendor class
     * @param vendor the vendorName to set
     */
    public void setVendor(String vendor)  throws ConfigurationException {
        this.vendor = vendor;
        this.vendorInfo = DBVendorMgr.getInstance().getVendorInfoByName(vendor);
        if(this.vendorInfo == null)
            throw new ConfigurationException("Vendor " + StrUtil.quote(vendor) + " not found.");
    }

    /**
     * @return the vendorName
     */
    public String getVendor() {
        return vendor;
    }

    /**
     * @return the vendorInfo
     */
    public DBVendorInfo getVendorInfo() {
        return vendorInfo;
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param port the port to set
     */
    public void setPort(String port) {
        this.port = port;
    }

    /**
     * @return the port
     */
    public String getPort() {
        return port;
    }

    /**
     * @param database the database to set
     */
    public void setDatabase(String database) {
        this.database = database;
    }

    /**
     * @return the database
     */
    public String getDatabase() {
        return database;
    }

    /**
     * @param user the username to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the username
     */
    public String getUser() {
        return user;
    }

    /**
     * @param pwd the pwd to set
     */
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    /**
     * @return the pwd
     */
    public String getPwd() {
        return pwd;
    }
    
    public String toString() {
    	return "ConnInfo[" + vendor + ", " + user + "/" + pwd + "@" + 
    		host + ":" + port +"/" + database + "]";
    }

    /** Vendor Name */
    private String vendor;
    
    /** Vendor Information Class */
    private DBVendorInfo vendorInfo;
    
    /** RDBMS Host name */
    private String host;
    
    /** RDBMS port, can be empty */
    private String port;
    
    /** target database name */
    private String database;
    
    /** User Name, maybe encrypted */
    private String user;
    
    /** Password, maybe encryption */
    private String pwd;
    
}
