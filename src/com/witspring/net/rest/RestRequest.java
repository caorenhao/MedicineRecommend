package com.witspring.net.rest;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.witspring.net.rest.sht.Entry;
import com.witspring.net.rest.sht.Parameters;
import com.witspring.util.IOUtil;
import com.witspring.util.NetUtil;
import com.witspring.util.StrUtil;

public abstract class RestRequest {
	
	private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
	
	private Map<String, String> cookieMap = null;
	
	private String[] paths = null;
	
	private RestParamMap paramMap;
	
	private String postQuery;
	
	private String urlQuery;
	
	/** Controller名称 */
	private String controllerName;
	
	/** 方法的名称 */
	private String methodName;
	
	/** Controller和方法组成的路径  */
	private String path;

	/**
	 * Returns the protocol string from the request in the 
	 * form protocol/majorVersion.minorVersion.
	 * @return
	 */
	public abstract String getProtocol();


	public abstract String getMethod();

	public abstract URI getUri();
	
	public abstract RestHeaders getHeaders();
	
	protected abstract InputStream getBody ();
	
	public abstract InetSocketAddress getRemoteAddress ();
	
	public abstract InetSocketAddress getLocalAddress ();
	
	public abstract Object getAttribute (String name);
	
	public abstract void setAttribute (String name, Object value);
	
	public Map<String, String> getCookieMap() {
		return cookieMap;
	}
	
	public String getCookie(String key) {
		return cookieMap.get(key);
	}
	
	public String[] getPaths() {
		return paths;
	}
	
	private void parseCookieMap(List<String> cookies) {
        cookieMap = new HashMap<String, String>();
        if (cookies != null) {
            for (String cookie : cookies) {
                int index = cookie.indexOf("=");
                if (index > 0) {
                    String key = cookie.substring(0, index);
                    String value = cookie.substring(index + 1);
                    cookieMap.put(key, value);
                }
            }
        }
    }
	
	private List<String> parseFirstRequestHeader(String key) {
        String text = getHeaders().getFirst(key);
        if (text != null) {
            List<String> list = new ArrayList<String>();
            for (String string : text.split(";")) {
                list.add(string.trim());
            }
            return list;
        }
        return null;
    }
	
	private void parseParameterMap() {
        
        urlQuery = getUri().getQuery();
        if (getMethod().equals("POST")) {
            try {
				postQuery =  IOUtil.readInputStreamAsString(getBody(), null);
				postQuery = StrUtil.emptyStringToNull(postQuery, true);			
				
				if(postQuery != null){
					// 尝试解析JSON格式
					try {
						JSONObject json = JSON.parseObject(postQuery);
						// 以此为基础创建RestParamMap
						paramMap = new RestParamMap(json);
						// postRequery 值为null
						postQuery = null;
					} catch (Exception e) {
						// 解析失败，忽略
					}
					
				}
						
				if(urlQuery == null) {
					urlQuery = postQuery;
				} else if(postQuery != null) {
					// postQuery 放在前面
					urlQuery = postQuery + "&" + urlQuery;
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
        }
        
        // paramMap 没有创建的时候自动创建一个
        if(paramMap == null) {
        	paramMap = new RestParamMap();
        }
        
        // 没有可以解析的URL，忽略
        if (urlQuery == null) {
            return;
        }     
        
        int index = 0;
        while (index < urlQuery.length()) {
            int endIndex = urlQuery.indexOf("&", index);
            if (endIndex > 0) {
                putParamString(urlQuery.substring(index, endIndex));
                index = endIndex + 1;
            } else if (index < urlQuery.length()) {
                putParamString(urlQuery.substring(index));
                return;
            }
        }
    }
	
	/**
	 * 添加一个参数表示的字符串, 格式为 key=value
	 * 参数名称统一转换成小写, 已经存在的key或则无效的格式忽略
	 * @param string
	 */
	private void putParamString(String string) {
        Entry<String, String> entry = Parameters.parseEntry(string);
        if (entry != null) {
            String value = StrUtil.decodeUrl(entry.getValue());
            // 添加不重复的键名
            if(!paramMap.containsKey(entry.getKey()))
            	paramMap.put(entry.getKey().toLowerCase(), value);
        }
    }
	
	/**
	 * 具体子类需要调用
	 */
	protected void parseParams_() {
		parseCookieMap(parseFirstRequestHeader("Cookie"));
		path = getUri().getPath();
		path = path.replaceAll("/+", "/");
		paths = path.substring(1).split("/");
		controllerName = null;
		if(paths.length > 0)
			controllerName = paths[0];
		methodName = null;
		if(paths.length > 1)
			methodName = paths[1];
		
		path = "/";
		if(controllerName != null)
			path += controllerName;
		if(methodName != null)
			path += "/" + methodName;
		
		parseParameterMap();
	}
	
	public String getUrlQuery() {
		return urlQuery;
	}
	
	public RestParamMap getParamMap() {
		return paramMap;
	}
	
	public String getControllerName() {
		return controllerName;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public String getPath() {
		return path;
	}

	
	public String getPostQuery() {
		return postQuery;
	}


	public void setPostQuery(String postQuery) {
		this.postQuery = postQuery;
	}


	public String toString() {
		StringBuilder sb = new StringBuilder(512);
		synchronized(df) {
			sb.append(df.format(NetUtil.getCalendar().getTime()));
		}
		sb.append(": [").append(getMethod()).append(" ").
			append(getProtocol()).append("] ").append(getUri()).
			append(" From ").append(getRemoteAddress()).
			append("\n").append(paramMap.toString());
		return sb.toString();	
	}
}
