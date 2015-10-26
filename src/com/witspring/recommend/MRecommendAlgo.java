package com.witspring.recommend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.witspring.util.Pair;

public class MRecommendAlgo {

	/**
	 * 词频统计算法.
	 * 
	 * @param map 词频统计集合
	 * @param str 词
	 * @return Map<String, Integer>
	 */
	public static Map<String, Integer> strCntMap(Map<String, Integer> map, String str) {
		if(map.containsKey(str)) {
			int number = map.get(str);
			map.put(str, number+1);
		} else {
			map.put(str, 1);
		}
		
		return map;
	}
	
	/**
	 * 对Map<String,Double>按照value进行降序排序.
	 *
	 * @param map Map<String,Double>
	 * @return List<Map.Entry<String,Double>>
	 * @throws IOException
	 */
	public static List<Map.Entry<String,Double>> sortStrDoubleDesc(Map<String,Double> map) throws IOException{
	    List<Map.Entry<String,Double>> infoIds = new ArrayList<Map.Entry<String,Double>>(map.entrySet());
	    Collections.sort(infoIds, new Comparator<Map.Entry<String,Double>>() {
	        @Override
			public int compare(Map.Entry<String,Double> o1, Map.Entry<String,Double> o2) {
	        	if (o2.getValue() > o1.getValue()){
    				return 1;
    			}
    			else if (o1.getValue() > o2.getValue()){
    				return -1;
    			}
    			else{
    				return 0;
    			}
	        }
	    });
	    return infoIds;
	}
	
	/**
	 * 对Map<String,Double>按照value进行降序排序.
	 *
	 * @param map Map<String,Double>
	 * @return List<Map.Entry<String,Double>>
	 * @throws IOException
	 */
	public static List<Map.Entry<Integer, Double>> sortIntDoubleDesc(
			Map<Integer, Double> map) throws IOException{
	    List<Map.Entry<Integer,Double>> infoIds = new ArrayList<Map.Entry<Integer,Double>>(map.entrySet());
	    Collections.sort(infoIds, new Comparator<Map.Entry<Integer,Double>>() {
	        @Override
			public int compare(Map.Entry<Integer,Double> o1, Map.Entry<Integer,Double> o2) {
	        	if (o2.getValue() > o1.getValue()){
    				return 1;
    			}
    			else if (o1.getValue() > o2.getValue()){
    				return -1;
    			}
    			else{
    				return 0;
    			}
	        }
	    });
	    return infoIds;
	}
	
	/** 将Map<String,Integer>的数据按照value降序排列*/
	public static List<Map.Entry<String, Integer>> sortValueDesc(Map<String,Integer> map) throws IOException{ 
	    List<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());  
	    Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {    
	        @Override
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {    
	            return (o2.getValue() - o1.getValue());    
	        }
	    }); 
	    
	    return infoIds;
	}
	
	/** 返回年龄组合*/
	public static String getAgeRange(float ageStart, float ageEnd) {
		StringBuffer sb = new StringBuffer();
		if(ageEnd == 0)
			sb.append("0-0");
		else 
			sb.append(ageStart).append("-").append(ageEnd);
		
		return sb.toString();
	}
	
	/**
	 * 生产独立的ID
	 * 
	 * @return 返回一个创建的hashcode作为唯一的id
	 */
	public static long createContentId() {
		long v1 = UUID.randomUUID().hashCode();
		long v2 = UUID.randomUUID().hashCode();
		long ret = Math.abs((v1 << 32) + v2);
		return ret;
	}
	
	/** 
     * splitAry方法
     * 
     * @param ary 要分割的数组 
     * @param subSize 分割的块大小 
     * @return Object[]
     */  
    private static Object[] splitAry(String[] ary, int subSize) {
    	int count = ary.length % subSize == 0 ? ary.length / subSize: ary.length / subSize + 1;  

    	List<List<String>> subAryList = new ArrayList<List<String>>();
    	
    	for (int i = 0; i < count; i++) {
    		int index = i * subSize;
    		List<String> list = new ArrayList<String>();
    		int j = 0;
    		while (j < subSize && index < ary.length) {
    			list.add(ary[index++]);
    			j++;
    		}
    		subAryList.add(list);
    	}
        
    	Object[] subAry = new Object[subAryList.size()];
        
    	for(int i = 0; i < subAryList.size(); i++){
    		List<String> subList = subAryList.get(i);
    		String[] subAryItem = new String[subList.size()];
    		for(int j = 0; j < subList.size(); j++){
    			subAryItem[j] = subList.get(j);
    		}
    		subAry[i] = subAryItem;
    	}
        
    	return subAry;
	}
    
    /** 
     * splitAry方法
     * 
     * @param ary 要分割的数组 
     * @param subSize 分割的块大小 
     * @return Object[]
     */  
    public static Object[] splitAry(List<Pair<String, Integer>> ary, int subSize) {
    	int arySize = ary.size();
    	int count = arySize % subSize == 0 ? arySize / subSize: arySize / subSize + 1;  

    	List<List<Pair<String, Integer>>> subAryList = new ArrayList<List<Pair<String, Integer>>>();
    	
    	for (int i = 0; i < count; i++) {
    		int index = i * subSize;
    		List<Pair<String, Integer>> list = new ArrayList<Pair<String, Integer>>();
    		int j = 0;
    		while (j < subSize && index < arySize) {
    			list.add(ary.get(index++));
    			j++;
    		}
    		subAryList.add(list);
    	}
        
    	Object[] subAry = new Object[subAryList.size()];
        
    	for(int i = 0; i < subAryList.size(); i++){
    		List<Pair<String, Integer>> subList = subAryList.get(i);
    		List<Pair<String, Integer>> subAryItem = new ArrayList<Pair<String, Integer>>();
    		for(int j = 0; j < subList.size(); j++){
    			subAryItem.add(subList.get(j));
    		}
    		subAry[i] = subAryItem;
    	}
        
    	return subAry;
	}
    
    public static void main(String[] args) {
    	String[] ary = {"0","1","2","3","4","5","6","7","8","9","10","11","12","13",
    			"14","15","16","17","18","19","20"};
        int splitSize = 5;
        Object[] subAry = splitAry(ary, splitSize);
        for(Object obj: subAry){
        	String[] aryItem = (String[]) obj;
        	for(int i = 0; i < aryItem.length; i++){
        		System.out.print(aryItem[i] + ", ");
        	}
        	System.out.println();
        }
    }
}
