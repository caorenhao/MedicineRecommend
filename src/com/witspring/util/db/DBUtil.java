package com.witspring.util.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public final class DBUtil {

	public static void forceClose(Statement stat) {
        if(stat == null)
            return;
        try {
            stat.close();
        } catch(Throwable t){}
    }
    
    public static void forceClose(ResultSet rs) {
        if(rs == null)
            return;
        try {
            rs.close();
        } catch(Throwable t){}
    }
    
    public static void forceClose(Connection conn) {
        if(conn == null)
            return;
        try {
            conn.close();
        } catch(Throwable t){}
    }
}
