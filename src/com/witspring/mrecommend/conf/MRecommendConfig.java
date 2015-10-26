package com.witspring.mrecommend.conf;

import java.io.File;

import org.jdom2.Element;

/**
 * MRecommend 配置文件解析类.
 *
 * @author renhao.cao.
 *         Created 2015年10月9日.
 */
public class MRecommendConfig extends JDomConfig {
	
	public static class RedisConf {
		public String server;
		public int port;
		public int maxSize;
		/**
		 * 解析方法.
		 *
		 * @param rootEle 根目录
		 * @throws Exception
		 */
		public void parse(Element rootEle) throws Exception {
			Element rdEle = rootEle;
			server = rdEle.getChildText("server");
			port = Integer.parseInt(rdEle.getChildText("port"));
			maxSize = Integer.parseInt(rdEle.getChildText("maxSize"));
		}
	}
	
	public static class HttpConf {
		public String server;
		public int port;
		/**
		 * 解析方法.
		 *
		 * @param rootEle 根目录
		 * @throws Exception
		 */
		public void parse(Element rootEle) throws Exception {
			Element rdEle = rootEle;
			server = rdEle.getChildText("server");
			port = Integer.parseInt(rdEle.getChildText("port"));
		}
	}
	
	public static class SphinxConf {
		public String server;
		public int port;
		public int portYpmcDisease;
		/**
		 * 解析方法.
		 *
		 * @param rootEle 根目录
		 * @throws Exception
		 */
		public void parse(Element rootEle) throws Exception {
			Element rdEle = rootEle;
			server = rdEle.getChildText("server");
			port = Integer.parseInt(rdEle.getChildText("port"));
			portYpmcDisease = Integer.parseInt(rdEle.getChildText("portYpmcDisease"));
		}
	}
	
	public static class ParamConf {
		public int sphinx_max_query;
		public int sphinx_timeout;
		public int sphinx_ypsl;
		public int ypsl;
		public int ypmc_disease_rank;
		/**
		 * 解析方法.
		 *
		 * @param rootEle 根目录
		 * @throws Exception
		 */
		public void parse(Element rootEle) throws Exception {
			Element rdEle = rootEle;
			sphinx_max_query = Integer.parseInt(rdEle.getChildText("sphinx_max_query"));
			sphinx_timeout = Integer.parseInt(rdEle.getChildText("sphinx_timeout"));
			sphinx_ypsl = Integer.parseInt(rdEle.getChildText("sphinx_ypsl"));
			ypsl = Integer.parseInt(rdEle.getChildText("ypsl"));
			ypmc_disease_rank = Integer.parseInt(rdEle.getChildText("ypmc_disease_rank"));
		}
	}
	
	/** redis 配置文件*/
	public RedisConf redisConf = new RedisConf();
	/** http 配置文件*/
	public HttpConf httpConf = new HttpConf();
	/** sphinx 配置文件*/
	public SphinxConf sphinxConf = new SphinxConf();
	/** param 配置文件*/
	public ParamConf paramConf = new ParamConf();
	/** Sphinx根目录配置文件*/
	public String logPath;
	
	@Override
	public File getDefaultConfigFile() throws Exception {
		return findConfigFile(null, "mrecommend.xml");
	}

	@Override
	protected void parse() throws Exception {
		Element root = getRootEle();
		redisConf.parse(root.getChild("redis"));
		httpConf.parse(root.getChild("http"));
		sphinxConf.parse(root.getChild("sphinx"));
		paramConf.parse(root.getChild("param"));
		logPath = rootEle.getChildText("logPath");
	}

}
