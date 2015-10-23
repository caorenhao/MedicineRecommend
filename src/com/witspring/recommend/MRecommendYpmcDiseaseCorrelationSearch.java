package com.witspring.recommend;

import java.util.List;

import org.sphx.api.SphinxClient;
import org.sphx.api.SphinxException;
import org.sphx.api.SphinxMatch;
import org.sphx.api.SphinxResult;

import com.witspring.util.CRC32;
import com.witspring.util.Pair;
import com.witspring.util.StrUtil;

public class MRecommendYpmcDiseaseCorrelationSearch {
	
	public static boolean getCorrelation(String ypmc, int icd_name_id) {
		boolean flag = false;
		SphinxClient cl = new SphinxClient();
        try {
        	cl.SetServer (MRecommendCost.SphinxIP, MRecommendCost.SphinxPortYpmcDisease);
			cl.SetMatchMode (SphinxClient.SPH_MATCH_EXTENDED2);
	        cl.SetLimits (0, MRecommendCost.YPMC_DISEASE_RANK);
	        cl.SetConnectTimeout(MRecommendCost.SPHINX_TIMEOUT);
	        
	        // 过滤条件
	        cl.SetFilter("icd_name_id", icd_name_id, false);
	        cl.SetFilter("ypmc_id", CRC32.getCRC32(ypmc), false);
	        cl.SetFilterRange("rank", 1, MRecommendCost.YPMC_DISEASE_RANK, false);
        	
	        SphinxResult res = cl.Query("", "index");
	        if(res != null) {
	        	if(res.total == 1) {
	        		SphinxMatch info = res.matches[0];
	        		System.out.println(info.attrValues.get(0) + "\t" 
	        				+ info.attrValues.get(1) + "\t" + info.attrValues.get(2) 
	        				+ "\t" + info.attrValues.get(3));
		        	flag = true;
	        	}
	        } else {
	        	System.out.println(ypmc + "-" + icd_name_id + ":无查询结果");
	        }
	        cl.ResetFilters();
		} catch (SphinxException ex) {
			ex.printStackTrace();
		} finally {
			if(cl != null) {
				try {
					cl.Close();
				} catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}
        
        return flag;
	}
	
	public static boolean[] getCorrelation(String[] ypmcs, int icd_name_id) {
		boolean[] flags = new boolean[ypmcs.length];
		SphinxClient cl = new SphinxClient();
        try {
        	cl.SetServer (MRecommendCost.SphinxIP, MRecommendCost.SphinxPortYpmcDisease);
			cl.SetMatchMode (SphinxClient.SPH_MATCH_EXTENDED2);
	        cl.SetLimits (0, MRecommendCost.YPMC_DISEASE_RANK);
	        cl.SetConnectTimeout(MRecommendCost.SPHINX_TIMEOUT);
	        
	        for(int i = 0; i < ypmcs.length; i++) {
	        	int ypmc_id = CRC32.getCRC32(ypmcs[i]);
	        	cl.ResetFilters();
				cl.SetFilter("ypmc_id", ypmc_id, false);
				cl.SetFilter("icd_name_id", icd_name_id, false);
		        cl.SetFilterRange("rank", 1, MRecommendCost.YPMC_DISEASE_RANK, false);
	        	cl.AddQuery("", "index", "");
	        }
	        SphinxResult[] res = cl.RunQueries();
        	
	        if(res!=null) {
	        	for(int i=0; i< res.length; ++i){
	        		if(res[i].total == 1)
	        			flags[i] = true;
	        		else 
	        			flags[i] = false;
		        }
	        } else {
	        	System.out.println("连接出错");
	        }
		} catch (SphinxException ex) {
			ex.printStackTrace();
		} finally {
			if(cl != null) {
				try {
					cl.Close();
				} catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}
        
        return flags;
	}
	
	public static boolean[] getCorrelation(List<Pair<String, Integer>> ypmcs, 
			int icd_name_id) {
		boolean[] flags = new boolean[ypmcs.size()];
		SphinxClient cl = new SphinxClient();
        try {
        	cl.SetServer(MRecommendCost.SphinxIP, MRecommendCost.SphinxPortYpmcDisease);
			cl.SetMatchMode(SphinxClient.SPH_MATCH_EXTENDED2);
	        cl.SetLimits(0, MRecommendCost.YPMC_DISEASE_RANK);
	        cl.SetConnectTimeout(MRecommendCost.SPHINX_TIMEOUT);
	        
	        for(int i = 0; i < ypmcs.size(); i++) {
	        	int ypmc_id = CRC32.getCRC32(ypmcs.get(i).first);
	        	cl.ResetFilters();
				cl.SetFilter("ypmc_id", ypmc_id, false);
				cl.SetFilter("icd_name_id", icd_name_id, false);
		        cl.SetFilterRange("rank", 1, MRecommendCost.YPMC_DISEASE_RANK, false);
	        	cl.AddQuery("", "index", "");
	        }
	        SphinxResult[] res = cl.RunQueries();
        	
	        if(res!=null) {
	        	for(int i=0; i< res.length; ++i){
	        		if(res[i].total == 1)
	        			flags[i] = true;
	        		else 
	        			flags[i] = false;
		        }
	        } else {
	        	System.out.println("连接出错" + StrUtil.join(ypmcs, ","));
	        }
		} catch (SphinxException ex) {
			ex.printStackTrace();
		} finally {
			if(cl != null) {
				try {
					cl.Close();
				} catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}
        
        return flags;
	}
	
	public static boolean getCorrelation(String ypmc, String icd_name) {
		boolean flag = false;
		SphinxClient cl = new SphinxClient();
        try {
        	cl.SetServer (MRecommendCost.SphinxIP, MRecommendCost.SphinxPortYpmcDisease);
			cl.SetMatchMode (SphinxClient.SPH_MATCH_EXTENDED2);
	        cl.SetLimits (0, MRecommendCost.YPMC_DISEASE_RANK);
	        cl.SetConnectTimeout(MRecommendCost.SPHINX_TIMEOUT);
	        
	        // 过滤条件
	        cl.SetFilter("ypmc_id", CRC32.getCRC32(ypmc), false);
	        cl.SetFilterRange("rank", 1, MRecommendCost.YPMC_DISEASE_RANK, false);
	        
	        String query = "\"" + icd_name + "\"";
	        SphinxResult res = cl.Query(query, "index");
	        
	        if(res != null) {
	        	if(res.total == 1)
		        	flag = true;
	        } else {
	        	System.out.println("无查询结果");
	        }
		} catch (SphinxException ex) {
			ex.printStackTrace();
		} finally {
			if(cl != null) {
				try {
					cl.Close();
				} catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}
        
        return flag;
	}
	
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		MRecommendCost.SphinxIP = "192.168.0.171";
		MRecommendCost.SphinxPortYpmcDisease = 9512;
		
//		String[] strs = {"盐酸金霉素眼膏", "磺胺醋酰钠滴眼液", "复方甲氧那明胶囊", 
//				"孟鲁司特钠片", "三拗片", "氯雷他定片", "盐酸氨溴索片", "阿奇霉素颗粒", 
//				"消咳喘片"};
//		List<Pair<String, Integer>> list = new ArrayList<Pair<String,Integer>>();
//		for(String str : strs) {
//			list.add(new Pair<String, Integer>(str, 1));
//		}
//		boolean[] flag = getCorrelation(list, 5178);
//		for(int i = 0; i < flag.length; i++) {
//			System.out.println(strs[i] + "\t" + flag[i]);
//		}
		/*
		boolean[] flag = getCorrelation(strs, 5178);
		for(int i = 0; i < flag.length; i++) {
			System.out.println(strs[i] + "\t" + flag[i]);
		}
		*/
		
		
		boolean flag = getCorrelation("西瓜霜润喉片", 5178);
		System.out.println(flag);
		long end = System.currentTimeMillis();
		System.out.println((end-start) + "ms");
	}
}
