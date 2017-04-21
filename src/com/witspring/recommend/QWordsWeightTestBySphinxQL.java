package com.witspring.recommend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.witspring.db.mysql.SphinxQLManager;
import com.witspring.util.db.DBUtil;

public class QWordsWeightTestBySphinxQL {
	
	private SphinxQLManager dbManager = null;
	
	public QWordsWeightTestBySphinxQL() throws Exception {
		dbManager = SphinxQLManager.getInstance();
	}
	
	public void query() throws Exception {
		String sql = "SELECT *,weight() FROM index1 WHERE MATCH('123^100.0 | 321^10.0 | 457^10.0') LIMIT 0,100 OPTION ranker=expr('sum(sum_idf)*1000');";
//		String sql = "SELECT *,weight() FROM index1 WHERE MATCH('123^10000.0 | 321^1.0 | 457^1.0') LIMIT 0,100 OPTION ranker=proximity;";
		PreparedStatement st = null;
		ResultSet rs = null;
		Connection conn = dbManager.getConnection();
		try {
	    	st = conn.prepareStatement(sql);
	    	rs = st.executeQuery();
        	while(rs.next()) {
        		System.out.println(rs.getString(1) + " " + rs.getString(2) + " " + 
        				rs.getString(3) + " " + rs.getString(4) + " " + rs.getString(5)
        				 + " " + rs.getString(6) + " " + rs.getString(7));
//        		for(int j = 0; j < rs.getRow(); j++) {
//	            	System.out.print(rs.getString(j) + "\t");
//	            }
//	            System.out.println();
			}
		} catch (SQLException exception) {
			exception.printStackTrace();
		} finally {
			DBUtil.forceClose(rs);
			DBUtil.forceClose(st);
		}
	}
	
	public static void main(String[] args) throws Exception {
		QWordsWeightTestBySphinxQL test = new QWordsWeightTestBySphinxQL();
		test.query();
	}
	
}
