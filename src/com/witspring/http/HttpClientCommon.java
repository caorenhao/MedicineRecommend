package com.witspring.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;

/**
 * TODO Put here a description of what this class does.
 *
 * @author renhao.cao.
 *         Created 2015年4月1日.
 */
public class HttpClientCommon {

	private static Map<String, String> params;
	
	static {
		params = new HashMap<String, String>();
		params.put("Accept", 
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		params.put("Accept-Language", "en-us,en;q=0.5");
		params.put("Connection", "keep-alive");
		params.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.7 (KHTML, like Gecko) Chrome/16.0.912.75 Safari/535.7");
	}
	
	//public static void addHeader()
	
	/**
	 * 设置请求的header值
	 * 
	 * @param request 
	 * @param cookie 
	 */
	public static void setHeader(HttpUriRequest request, String cookie) {
		params.put("Cookie", cookie);
		HttpClientUtils.addHeader(request, params);
	}
	
	/**
	 * 设置请求的header值
	 * 
	 * @param request 
	 */
	public static void setHeader(HttpUriRequest request) {
		HttpClientUtils.addHeader(request, params);
	}
	
	/**
	 * 提交Get/Post请求, 获取内容 
	 * 
	 * @param httpclient 
	 * @param request 
	 * @param charset 
	 * @return String 返回为null时表示代理无法使用 
	 */
	public static String execute(CloseableHttpClient httpclient, 
			HttpUriRequest request) {
		BufferedReader reader = null;
		StringBuffer sb = new StringBuffer();
		CloseableHttpResponse response = null;
		InputStream in = null;
		
		try {
			response = httpclient.execute(request);
			
			in = response.getEntity().getContent();
	    	reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(in != null)
					in.close();
				if(reader != null)
					reader.close();
				if(response != null)
					response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return sb.toString();
	}
}
