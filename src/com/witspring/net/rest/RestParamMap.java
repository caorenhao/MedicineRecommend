package com.witspring.net.rest;

import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.witspring.util.NumberFormatUtil;

public class RestParamMap {

	public static enum ExpType {
		MISSED,
		INVALID
	}
	
	public static interface SMExpHandler {
		Exception createStringMapException(ExpType type, String param);
	}
	
	private SMExpHandler handler;

	/** 存储复杂的类型 */
	private JSONObject json = new JSONObject();
	
	public RestParamMap() {
    }

    public RestParamMap(Map<String, Object> m) {
        json.putAll(m);
    }
    
    /**
     * 获取最原始的json对象
     * @return
     */
    public JSONObject getRawJson() {
    	return json;
    }

    public Object put(String key, Object object) {
        return json.put(key, object);
    }
    
    public Object remove(String key) {
    	return json.remove(key);
    }
    
    public boolean containsKey(String key) {
    	return json.containsKey(key);
    }
   
    public Set<String> keySet() {
    	return json.keySet(); 
    }
    
	public void setHandler(SMExpHandler handler) {
		this.handler = handler;
	}

	public SMExpHandler getHandler() {
		return handler;
	}
   
	private Exception createException(ExpType type, String param) {
		if(handler != null) {
			return handler.createStringMapException(type, param);
		} 
		
		switch(type) {
		case MISSED:
			return new IllegalArgumentException("Param Not Found:" + param);
		case INVALID:
			return new IllegalArgumentException("Param is invalid:" + param);
		default:
			return new IllegalArgumentException("Other Error:" + param);
		}
	}    
   
    public String getString(String key, boolean madatory) throws Exception {
        String value = json.getString(key);
        if (value == null) {
        	if(madatory){
        		throw createException(ExpType.MISSED, key);
        	}        
        }
        return value;
    }
   
    public boolean getBoolean(String key, boolean defValue) throws Exception {
    	String strVal = getString(key, false);
    	if(strVal == null)
    		return defValue;
    	strVal = strVal.toLowerCase();
    	if(strVal.equals("true") || strVal.equals("1"))
    		return true;
    	
    	if(strVal.equals("false") || strVal.equals("0"))
    		return false;
    	
    	throw createException(ExpType.INVALID, key);
    }
    
    /**
     * 不存在时返回null
     * @param key
     * @return
     */
    public JSONObject getJSONObject(String key) {
    	return json.getJSONObject(key);
    }
    
    public JSONArray getJSONArray(String key, boolean madatory) throws Exception {
    	JSONArray value = json.getJSONArray(key);
        if (value == null) {
        	if(madatory){
        		throw createException(ExpType.MISSED, key);
        	}        
        }
        return value;
    }
    
    /**
     * 不存在时返回null
     */
    public Object get(String key) {
    	return json.get(key);
    }
    
    /**
     * 一定要包含这个key，否则抛出异常
     */
    public String getString(String key) throws Exception {
    	return getString(key, true);
    }
    
    /**
     * 不存在时返回默认值
     * @param key
     * @param defaultValue 默认值
     * @return
     */
    public String getString(String key, String defaultValue) {
        String value = json.getString(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    /**
     * 必须包括的数值型
     */
    public <T extends Number> T getNumber(String key, 
    		Class<T> type) throws Exception {
    	return getNumber(key, type, true);
    }
    
    /**
     * 选择是否必须传回某个数值型参数，如果 madatory为false，不包含情况下返回null
     * @param key 键名
     * @param type 数值的类型
     * @param madatory 是否必须
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
	public <T extends Number> T getNumber(String key, 
    		Class<T> type, boolean madatory) throws Exception {
    	String strVal = getString(key, madatory);
    	if(madatory == false && strVal == null)
    		return null;
    	try {
			return (T)NumberFormatUtil.stringToNumber(type, strVal);
		} catch (NumberFormatException nfe) {
			throw createException(ExpType.INVALID, key);
		}
    }
    
    @SuppressWarnings("unchecked")
	public <T extends Number> T getNumber(String key, 
    		T defValue) throws Exception {
    	String strVal = getString(key, false);
    	try {
    		if(strVal != null)
				return (T)NumberFormatUtil.stringToNumber(defValue.getClass(), strVal);
    		return defValue;
		} catch (NumberFormatException nfe) {
			throw createException(ExpType.INVALID, key);
		}
    }

    public String toString() {
    	return JSON.toJSONString(json, SerializerFeature.WriteMapNullValue);
    }
}

