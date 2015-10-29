package com.witspring.util.db;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author vernkin
 */
public class SQLGenerator {

    /**
     *
     * @param selectedFields if null, insert all fields
     * @return
     */
    public static PreparedStatement getInsertSQL(Connection conn,
            String tableName, Class<?> clazz, Field[] selectedFields)
            throws Exception {
        StringBuilder sb = new StringBuilder(1024);
        if(selectedFields == null)
            selectedFields = clazz.getFields();

        int fieldMaxIndex = selectedFields.length - 1;
        sb.append("INSERT INTO ").append(tableName).append(" (");
        StringBuilder valueSb = new StringBuilder();
        for(int fieldIdx = 0; fieldIdx <= fieldMaxIndex; ++fieldIdx) {
            sb.append(selectedFields[fieldIdx].getName());
            if(fieldIdx != fieldMaxIndex) {
                sb.append(", ");
                valueSb.append("?,");
            } else {
                sb.append(") VALUES (");
                valueSb.append("?)");
                break;
            }
        }
        sb.append(valueSb);
        return conn.prepareStatement(sb.toString());
    }

    /**
     * 
     * @param conn
     * @param tableName
     * @param clazz
     * @param idFieldName used in where `idFieldName` = ?
     * @param selectedFields if null, insert all fields except for idFieldName
     * @return
     * @throws Exception
     */
    public static PreparedStatement getUpdateSQL(Connection conn,
            String tableName, Class<?> clazz, String idFieldName, Field[] selectedFields)
            throws Exception {
        StringBuilder sb = new StringBuilder(1024);
        if(selectedFields == null)
            selectedFields = clazz.getFields();
        // ignore id fields
        List<Field> tmpFields = new ArrayList<Field>(selectedFields.length);
        for(Field f : selectedFields) {
            if(!f.getName().equals(idFieldName)) {
                tmpFields.add(f);
            }
        }
        selectedFields = tmpFields.toArray(new Field[1]);

        int fieldMaxIndex = selectedFields.length - 1;
        sb.append("UPDATE ").append(tableName).append(" SET ");
        for(int fieldIdx = 0; fieldIdx <= fieldMaxIndex; ++fieldIdx) {
            Field f = selectedFields[fieldIdx];
            sb.append(f.getName()).append(" = ?");
            if(fieldIdx != fieldMaxIndex) {
                sb.append(", ");
            } else {
                sb.append(" WHERE ").append(idFieldName).append(" = ?");
                break;
            }
        }

        return conn.prepareStatement(sb.toString());
    }

    public static PreparedStatement getDeleteSQL(Connection conn,
            String tableName, String idFieldName) throws Exception {
        StringBuilder sb = new StringBuilder(64);
        sb.append("DELETE FROM ").append(tableName).append(" WHERE ").
                append(idFieldName).append(" = ?");
        return conn.prepareStatement(sb.toString());
    }
    
    public static PreparedStatement getSelectSQL(Connection conn, 
            String tableName, Class<?> clazz, Field[] selectedFields, 
            Map<String, Object> condMap) throws Exception {
        StringBuilder sb = new StringBuilder(1024);
        if(selectedFields == null)
            selectedFields = clazz.getFields();

        int fieldMaxIndex = selectedFields.length - 1;
        sb.append("SELECT ");
        for(int fieldIdx = 0; fieldIdx <= fieldMaxIndex; ++fieldIdx) {
            sb.append(selectedFields[fieldIdx].getName());
            if(fieldIdx != fieldMaxIndex) {
                sb.append(", ");
            } else {
                sb.append(" FROM ").append(tableName);
                break;
            }
        }
        
        if(condMap != null && !condMap.isEmpty()) {
            sb.append(" WHERE ");
            int condSize = condMap.size();
            int condIdx = 0;
            for(Map.Entry<String, Object> cond : condMap.entrySet()) {
                sb.append(cond.getKey()).append(" = ? ");
                ++condIdx;
                if(condIdx == condSize) {
                    break;
                } else {
                    sb.append(" AND ");
                }
            }
        }
        
        return conn.prepareStatement(sb.toString());
    }
    
    /**
     * 
     * @param stat
     * @param obj the fields to be used <b>MUST</b> be public
     * @param selectedFields selectedFields if null, insert all fields
     * @param idFieldName used in update mode
     * @throws Exception
     */
    public static void fillPreparedStatementAttr(PreparedStatement stat,
            Object obj, Field[] selectedFields, String idFieldName) throws Exception {
        if(selectedFields == null)
            selectedFields = obj.getClass().getFields();
        int fieldCount= selectedFields.length;
        if(idFieldName == null) {
            // insert mode
            for(int fieldIdx = 0; fieldIdx < fieldCount; ++fieldIdx) {
                Field f = selectedFields[fieldIdx];
                stat.setObject(fieldIdx + 1, f.get(obj));
            }
            return;
        }

        // update mode

        int dbFieldIdx = 1;
        for(int fieldIdx = 0; fieldIdx < fieldCount; ++fieldIdx) {
            Field f = selectedFields[fieldIdx];
            if(f.getName().equals(idFieldName))
                continue;
            stat.setObject(dbFieldIdx, f.get(obj));
            ++dbFieldIdx;
        }
        Object idVal = obj.getClass().getField(idFieldName).get(obj);
        stat.setObject(dbFieldIdx, idVal);
    }
}