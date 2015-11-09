package com.witspring.recommend;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.witspring.sougou.FileUtil;
import com.witspring.util.IOUtil;
import com.witspring.util.StrUtil;

public class test {

	public void tt() throws Exception {
		PrintWriter pw = new PrintWriter(new File("./data/dict/t_qb_zyzd_ypmc_3.txt"));
		Map<String, Map<Integer, Double>> ypmcDiseaseCorrTempMap = 
				new HashMap<String, Map<Integer,Double>>();
		MRecommendConst.ypmcDiseaseCorrMap = new HashMap<String, Map<Integer,Double>>();
		String inputPath = "./data/dict/t_qb_zyzd_ypmc_3/";
		List<String> list = new ArrayList<String>();
		List<File> paths = FileUtil.getAllFiles(inputPath);
		for(File file : paths) {
			list = IOUtil.readStringListFromFile(file, list);
			for(String str : list) {
				String[] strs = str.split(MRecommendConst.ATTR_STR);
				pw.println(StrUtil.join(strs, "\t"));
				Map<Integer, Double> temp = new HashMap<Integer, Double>();
				if(ypmcDiseaseCorrTempMap.containsKey(strs[1]))
					temp = ypmcDiseaseCorrTempMap.get(strs[1]);
				//System.out.println(str);
				temp.put(Integer.parseInt(strs[4]), Double.parseDouble(strs[2]));
				ypmcDiseaseCorrTempMap.put(strs[1], temp);
			}
		}
		IOUtil.forceClose(pw);
	}
	
	public void t_qb_icd_name_1() throws Exception {
		PrintWriter pw = new PrintWriter(new File("./data/dict/t_qb_icd_name_1_format.txt"));
		List<String> list = new ArrayList<String>();
		list = IOUtil.readStringListFromFile(new File("./data/dict/t_qb_icd_name_1.txt"), list);
		
		for(String line : list) {
			String[] strs = line.split(MRecommendConst.ATTR_STR);
			pw.println(strs[0] + "\t" + strs[1]);
		}
		IOUtil.forceClose(pw);
	}
	
	public void t_qb_zyzd_jieba_4_1() throws Exception {
		PrintWriter pw = new PrintWriter(new File("./data/index/t_qb_zyzd_jieba_4_1_format.txt"));
		List<String> list = new ArrayList<String>();
		list = IOUtil.readStringListFromFile(new File("./data/index/t_qb_zyzd_jieba_4_1.txt"), list);
		
		for(String line : list) {
			String[] strs = line.split(MRecommendConst.ATTR_STR);
			pw.println(strs[0] + "\t" + strs[1]);
		}
		IOUtil.forceClose(pw);
	}
	
	public static void main(String[] args) throws Exception {
		test t = new test();
		t.tt();
		t.t_qb_icd_name_1();
		t.t_qb_zyzd_jieba_4_1();
	}
	
}
