package com.witspring.util.db;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.witspring.util.IniParser;
import com.witspring.util.IniSection;
import com.witspring.util.StrUtil;

/**
 * Maintains the DB vender associated information
 * @author vernkin
 *
 */
public class DBVendorMgr {

    private static DBVendorMgr mgr = new DBVendorMgr();

    public static DBVendorMgr getInstance() {
        return mgr;
    }

    private DBVendorMgr() {
        vendorMap = new HashMap<DBVendor, DBVendorInfo>();
        nameMap = new HashMap<String, DBVendorInfo>();
        init();
    }

    public DBVendorInfo getVendorInfoByType(DBVendor type) {
        return vendorMap.get(type);
    }

    public DBVendorInfo getVendorInfoByName(String name) {
        name = name.trim().toUpperCase();
        return nameMap.get(name);
    }

    /**
     * Extra DBVenderInfo variables can be set by .ini configuration file
     * 
     * @param iniFilePath
     *            the ini file path
     */
    public void loadConfig(String iniFilePath) throws ConfigurationException {
        try {

            IniParser parser = new IniParser();
            parser.parse(iniFilePath);

            for (IniSection section : parser.values()) {
                String vendor = section.getName();
                DBVendorInfo dbvi = getVendorInfoByName(vendor);
                if (dbvi == null) {
                    throw new ConfigurationException("Vendor " + 
                            StrUtil.quote(vendor) + " not found.");
                }
                for (Map.Entry<String, String> entry : section.entrySet()) {
                    String key = entry.getKey().toLowerCase();
                    String value = entry.getValue();
                    if (key.equalsIgnoreCase("driver")) {
                        dbvi.setDriver(value);
                    }
                    else if (key.equalsIgnoreCase("libs")) {
                        StringTokenizer st = new StringTokenizer(value, ":;");
                        while (st.hasMoreTokens()) {
                            dbvi.addLibrary(st.nextToken().trim());
                        }
                    }
                    else if(key.equalsIgnoreCase("urlPattern")) {
                        dbvi.setUrlPattern(value);
                    }
                    // just ignore other options for further usage
                }

            }

        } catch (Throwable t) {
            throw new ConfigurationException(
                    "In loading config:" + iniFilePath, t);
        }
    }

    private void init() {
        DBVendorInfo dbvi;

        dbvi = new DBVendorInfo(DBVendor.ORACLE);
        dbvi.addName("ORACLE");
        dbvi.setUrlPattern("jdbc:oracle:thin:@%s:%s");
        dbvi.setDriver("oracle.jdbc.driver.OracleDriver");
        vendorMap.put(dbvi.getVendor(), dbvi);

        dbvi = new DBVendorInfo(DBVendor.MSSQL);
        dbvi.addName("MSSQL");
        dbvi.setUrlPattern("jdbc:sqlserver://%s;databaseName=%s;");
        dbvi.setDriver("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        vendorMap.put(dbvi.getVendor(), dbvi);

        dbvi = new DBVendorInfo(DBVendor.MYSQL);
        dbvi.addName("MYSQL");
        dbvi.setUrlPattern("jdbc:mysql://%s/%s?characterEncoding=utf8");
        dbvi.setDriver("com.mysql.jdbc.Driver");
        vendorMap.put(dbvi.getVendor(), dbvi);

        dbvi = new DBVendorInfo(DBVendor.INFORMIX);
        dbvi.addName("INFORMIX");
        dbvi.setUrlPattern("jdbc:informix-sqli://%s/%s");
        vendorMap.put(dbvi.getVendor(), dbvi);

        dbvi = new DBVendorInfo(DBVendor.DB2);
        dbvi.addName("DB2");
        dbvi.setUrlPattern("jdbc:db2://%s/%s");
        vendorMap.put(dbvi.getVendor(), dbvi);

        dbvi = new DBVendorInfo(DBVendor.AS400);
        dbvi.addName("AS400");
        dbvi.setUrlPattern("jdbc:as400://%s/%s");
        vendorMap.put(dbvi.getVendor(), dbvi);

        dbvi = new DBVendorInfo(DBVendor.UNISQL);
        dbvi.addName("UNISQL");
        dbvi.setUrlPattern("jdbc:unisql:%s:%s:::");
        vendorMap.put(dbvi.getVendor(), dbvi);

        dbvi = new DBVendorInfo(DBVendor.SYBASE);
        dbvi.addName("SYBASE");
        dbvi.setUrlPattern("jdbc:sybase:Tds:%s/%s");
        dbvi.setDriver("com.sybase.jdbc3.jdbc.SybDriver");
        vendorMap.put(dbvi.getVendor(), dbvi);

        dbvi = new DBVendorInfo(DBVendor.ACCESS);
        dbvi.addName("ACCESS");
        dbvi.setUrlPattern("jdbc:odbc:%s");
        vendorMap.put(dbvi.getVendor(), dbvi);

        dbvi = new DBVendorInfo(DBVendor.POSTGRE);
        dbvi.addName("POSTGRE");
        dbvi.setUrlPattern("jdbc:postgresql://%s/%s");
        dbvi.setDriver("org.postgresql.Driver");
        vendorMap.put(dbvi.getVendor(), dbvi);

        dbvi = new DBVendorInfo(DBVendor.ALTIBASE3);
        dbvi.addName("ALTIBASE3");
        dbvi.setUrlPattern("");
        vendorMap.put(dbvi.getVendor(), dbvi);

        dbvi = new DBVendorInfo(DBVendor.ALTIBASE);
        dbvi.addName("ALTIBASE");
        dbvi.setUrlPattern("jdbc:Altibase://%s/%s");
        vendorMap.put(dbvi.getVendor(), dbvi);

        dbvi = new DBVendorInfo(DBVendor.DERBY);
        dbvi.addName("DERBY");
        dbvi.setUrlPattern("jdbc:derby://%s/%s;create=true");
        vendorMap.put(dbvi.getVendor(), dbvi);

        dbvi = new DBVendorInfo(DBVendor.SYMFOWARE);
        dbvi.addName("SYMFOWARE");
        dbvi.setUrlPattern("jdbc:symford://%s/%s");
        vendorMap.put(dbvi.getVendor(), dbvi);

        dbvi = new DBVendorInfo(DBVendor.CUBRID);
        dbvi.addName("CUBRID");
        dbvi.setUrlPattern("jdbc:cubrid:%s:%s");
        vendorMap.put(dbvi.getVendor(), dbvi);

        dbvi = new DBVendorInfo(DBVendor.TIBERO);
        dbvi.addName("TIBERO");
        dbvi.setUrlPattern("jdbc:tibero:thin:@%s:%s");
        vendorMap.put(dbvi.getVendor(), dbvi);

        buildNameMap();
    }

    /**
     * Building the name to DBVendorInfo mapping
     */
    private void buildNameMap() {
        for (DBVendorInfo dbvi : vendorMap.values()) {
            for (String name : dbvi.getNameList()) {
                nameMap.put(name, dbvi);
            }
        }
    }

    private Map<DBVendor, DBVendorInfo> vendorMap;
    private Map<String, DBVendorInfo> nameMap;
}
