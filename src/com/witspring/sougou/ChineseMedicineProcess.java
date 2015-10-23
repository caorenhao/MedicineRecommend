package com.witspring.sougou;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.witspring.recommend.MRecommendCost;
import com.witspring.util.IOUtil;

public class ChineseMedicineProcess {

	private static Map<String, Integer> map;
	
	public void parse() throws Exception {
		PrintWriter pw = new PrintWriter(new File("./data/dict/zhongyao_new.txt"));
		List<String> list = new ArrayList<String>();
		list = IOUtil.readStringListFromFile(new File("./data/dict/zhongyao.txt"), list);
		for(String str : list) {
			pw.println(str);
			pw.println("(甲)" + str);
			pw.println("炒" + str);
			pw.println(str + "(炙)");
			pw.println(str + "(炒)");
			pw.println("(甲)" + str + "(炒)");
			pw.println(str + "(生)");
			pw.println(str + "(炭)");
			pw.println(str + "(煅)");
			pw.println(str + "(制)");
			pw.println(str + "(汉)");
			pw.println("（肉）" + str);
		}
		IOUtil.forceClose(pw);
	}
	
	public void filter() throws Exception {
		PrintWriter pw = new PrintWriter(new File("c:/crh/work/data/med.txt"));
		List<String> list = new ArrayList<String>();
		list = IOUtil.readStringListFromFile(new File("c:/crh/work/data/000000_0"), list);
		for(String str : list) {
			String[] texts = str.split(MRecommendCost.ATTR_STR);
			pw.println(texts[0]);
		}
		IOUtil.forceClose(pw);
	}
	
	public static void main(String[] args) throws Exception {
		ChineseMedicineProcess process = new ChineseMedicineProcess();
		//process.parse();
		process.filter();
		
	}
	
}
