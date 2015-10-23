package com.witspring.common;

import java.io.File;

import com.witspring.recommend.MRecommendCost;
import com.witspring.util.OSUtil;

/**
 * 主程序目录获取类
 * @author vernkin
 *
 */
public final class ProcessCtr {

	/** 环境变量的值*/
	public static final String FINDEX_ROOT_KEY = "MRECOMMEND_ROOT";
	
	/**
	 * 是否开发模式
	 * 通过检查项目根目录下是否包含bin目录
	 * @return true表示开发模式,false表示非开发模式
	 */
	public static boolean isDevelopMode() {
		return (new File("bin/com/witspring")).exists();
	}
	
	
	/**
	 * 获取主程序目录.
	 *
	 * @return 程序目录文件
	 * @throws Exception
	 */
	public static File getLocalFIndexRoot() throws Exception {
		String diclRoot = null;
		if(OSUtil.getOSType().isWindows()) {
			diclRoot = System.getenv(FINDEX_ROOT_KEY);
			diclRoot = "C:/crh/work/code/MedicineRecommend";
			//System.out.println(diclRoot);
		} else {
			diclRoot = MRecommendCost.PROGRAM_PATH;
		}
		return new File(diclRoot);
	}
}
