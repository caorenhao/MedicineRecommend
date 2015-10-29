package com.witspring.util.db;

import java.util.ArrayList;
import java.util.List;

public class DBVendorInfo {

    public DBVendorInfo(DBVendor pVendor) {
        setVendor(pVendor);
    }

    /**
     * Add name for specific DBVendorInfo
     * 
     * @param name
     *            The name ignore spaces and letter cases
     */
    public void addName(String name) {
        name = name.trim().toUpperCase();
        if (nameList.contains(name) == false)
            nameList.add(name);
    }

    public String getUrl(String host, String port, String dbName) {
        if (port != null && port.isEmpty() == false)
            host = host + ":" + port;
        String url = getUrlPattern().replaceFirst("%s", host);
        url = url.replaceFirst("%s", dbName);
        return url;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    public List<String> getNameList() {
        return nameList;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getDriver() {
        return driver;
    }

    public void setVendor(DBVendor vendor) {
        this.vendor = vendor;
    }

    public DBVendor getVendor() {
        return vendor;
    }
    
    public boolean isExternalLibrary() {
        return libs.isEmpty() == false;
    }
    
    public List<String> getLibs() {
        return libs;
    }
    
    public void addLibrary(String lib) {
        if(lib == null || lib.isEmpty() == true)
            return;
        if(libs.contains(lib) == false)
            libs.add(lib);
    }

    /** The Vendor Type */
    private DBVendor vendor;

    private List<String> nameList = new ArrayList<String>();

    /** Connection URL pattern */
    private String urlPattern;

    /** class path of Driver class */
    private String driver;

    /** Library list */
    private List<String> libs = new ArrayList<String>();
}
