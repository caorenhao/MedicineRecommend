package com.witspring.recommend;

import org.sphx.api.SphinxClient;
import org.sphx.api.SphinxException;
import org.sphx.api.SphinxMatch;
import org.sphx.api.SphinxResult;

import com.witspring.mrecommend.conf.ConfigSingleton;
import com.witspring.mrecommend.conf.MRecommendConfig;

/**
 * TODO Put here a description of what this class does.
 * 
 * @author renhao.cao
 * 		   Create 2015年12月15日.
 */
public class QWordsWeightTest {

	/**
	 * 初始化疾病ID对应的索引列表.
	 *
	 * @throws Exception
	 */
	public static void init() throws Exception {
		// 初始化Sphinx的配置
		MRecommendConfig conf = ConfigSingleton.getMRecommendConfig();
		MRecommendConst.SphinxIP = conf.sphinxConf.server;
		MRecommendConst.SphinxPort = conf.sphinxConf.port;
		MRecommendConst.SphinxPortYpmcDisease = conf.sphinxConf.portYpmcDisease;
	}
	
	public void searchYpmc_mva() {
        SphinxClient cl = new SphinxClient();
        try {
			cl.SetServer(MRecommendConst.SphinxIP, MRecommendConst.SphinxPort);
	        cl.SetLimits(0, MRecommendConst.SPHINX_YPSL);
	        cl.SetConnectTimeout(MRecommendConst.SPHINX_TIMEOUT);
	        
	        // 在Sphinx中搜索
	        SphinxResult res = cl.Query("123 & 456", "index1");
	        
	        if(res != null) {
	        	for (int i = 0; i < res.matches.length; i++){
		            SphinxMatch info = res.matches[i];
		            for(int j = 0; j < info.attrValues.size(); j++) {
		            	System.out.print(info.attrValues.get(j) + "\t");
		            }
		            System.out.println();
		        }
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
	}
	
	public static void main(String[] args) throws Exception {
		init();
		QWordsWeightTest test = new QWordsWeightTest();
		test.searchYpmc_mva();
	}
	
}
