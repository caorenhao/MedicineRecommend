package com.witspring.util.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class DBOPHelper {

	private static Set<String> RESERVED_SET = new HashSet<String>();
	static {
		RESERVED_SET.add("range");
	}
	
	public static String joinColumnsArray(Collection<String> cols) {
		return joinColumnsArray(cols, false);
	}
	
	public static String joinColumnsArray(Collection<String> cols, boolean includeQuote) {
		StringBuffer sb = new StringBuffer(256);
		int idx = 0;
		for(String str : cols) {
			if(idx > 0)
				sb.append(", ");
			if(RESERVED_SET.contains(str)) {
				sb.append('`').append(str).append('`');
			} else {
				if(includeQuote) {
					sb.append('`');
				}
				sb.append(str);
				if(includeQuote) {
					sb.append('`');
				}
			}
			++idx;
		}
		return sb.toString();
	}
	
	/**
	 * 合并 IN 属性值。输出的内容包括 IN 和 左右括号.
	 * 如 values 为 ['a', 'b'], 输出为" IN ('a', 'b')"
	 * 如 values 为 [1, 2], 输出为" IN (1, 2)"
	 * @param <T> 为整数型或者字符串型
	 * @param values 值列表
	 * @param sb 输出的StringBuilder
	 */
	public static<T extends Object> void joinInValues(Collection<T> values, 
			StringBuilder sb) {
		T firstEle = values.iterator().next();
		boolean isNumber = (firstEle instanceof Number);
		sb.append(" IN (");
		boolean isNotFirst = false;
		for(T ele : values) {
			if(isNotFirst) {
				sb.append(", ");
			} else {
				isNotFirst = true;
			}
			
			if(isNumber) {
				sb.append(ele);
			} else {
				sb.append('\'').append(ele).append('\'');
			}
		}
		sb.append(')');
	}
	
	/**
	 * 获取问号并列的字符串, 前后包含括号
	 * size 为 3 的时候，输出为 (?,?,?)
	 * @param size 问号的个数
	 * @return
	 */
	public static String getMarkList(int size) {
		StringBuffer sb = new StringBuffer(size * 2 + 1);
		sb.append('(').append('?');
		for(int i = 1; i < size; ++i)
			sb.append(',').append('?');
		sb.append(')');
		return sb.toString();
	}
	
	/**
	 * 获取数据库中的若干条数据，根据 origFilterName 指定的属性(经过mappings映射) 
	 * 过滤值为 filterVal的若干条数据
	 * @param conn 数据库连接
	 * @param tableName 表名
	 * @param cols 要选择的列
	 * @param filterColName 过滤的列名称
	 * @param filterVals 过滤的属性值的列表
	 * @return 符合结果的 列名 => 值 的map
	 * @throws Exception
	 */
	public static<T extends Object> List<Map<String, Object>> getRecords(Connection conn,
			String tableName, String[] cols, String filterColName, 
			Collection<T> filterVals) throws Exception {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		if(cols == null || cols.length == 0)
			return ret;		
		
		StringBuilder sb = new StringBuilder(512);
		sb.append("SELECT ").append(joinColumnsArray(Arrays.asList(cols)));
		sb.append(" FROM ").append(tableName);
		if(filterColName != null) {
			sb.append(" WHERE ");
			sb.append(filterColName);
			joinInValues(filterVals, sb);
		}
		
		Statement queryStat = null;
		ResultSet rs = null;
		
		try {
			queryStat = conn.createStatement();
			rs = queryStat.executeQuery(sb.toString());
			while(rs.next()) {
				HashMap<String, Object> obj = new HashMap<String, Object>();
				for(String col : cols) {
					obj.put(col, rs.getObject(col));
				}
				ret.add(obj);
			}
		} catch(Exception ex) {
			throw ex;
		} finally {
			DBUtil.forceClose(rs);
			DBUtil.forceClose(queryStat);
		}
		
		return ret;
	}
	
	/**
	 * 同 getRecords(), 除了不需要输入cols数组。selectFields自动转换成映射
	 * @param selectFields 为SQL选择的字段，如 "id, name, attr1"
	 * @throws Exception
	 */
	public static<T extends Object> List<Map<String, Object>> getRecords(
			Connection conn, String tableName, String selectFields, 
			String filterColName, Collection<T> filterVals) throws Exception {
		Set<String> colSet = new HashSet<String>();
		
		for(String attr : selectFields.split(",")) {
			if(attr == null)
				continue;
			attr = attr.trim();
			if(attr.isEmpty())
				continue;
			colSet.add(attr);
		}
		return getRecords(conn, tableName, colSet.toArray(new String[colSet.size()]), 
				filterColName, filterVals);
	}
}
