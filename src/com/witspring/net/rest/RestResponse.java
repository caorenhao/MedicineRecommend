package com.witspring.net.rest;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.witspring.net.rest.exception.RestException;
import com.witspring.util.IOUtil;
import com.witspring.util.zip.StreamCompressAndDecompress;

public abstract class RestResponse {
	
	public static final String DEFAULT_CHARSET = "utf-8";
	
	private Map<String, Object> toWriteRet = null;
	private int httpCode;
	
	/**
	 * Ends this exchange by doing the following in sequence:
	 */
	public abstract void close();
	
	public abstract RestHeaders getHeaders();
	
	public abstract int getResponseCode();
	
	protected abstract OutputStream getBody();
	
	protected abstract void sendResponseHeaders (int rCode, long contentLen) throws IOException;
	
	public abstract void addHeader(String key, String value);
	
	/**
	 * 
	 * @param expirationDate cookie超时时间
	 * @param nameAndValue cookie的key value 如：customer=hppss
	 * @param path	访问什么路径的时候需要cookie值
	 * @param domain	域名
	 * @param isSecure	是否开启安全规则
	 */
	public abstract void setCookie(Date expirationDate, String nameAndValue, String path, String domain, boolean isSecure);
	
	public void reponseWithException(RestException exp) {
		httpCode = HttpURLConnection.HTTP_OK;
		toWriteRet = new HashMap<String, Object>();
		toWriteRet.put("errorcode", exp.getErrorCode());
		toWriteRet.put("errorstr", exp.getErrorObj());
		toWriteRet.put("errordetail", exp.getErrorDetails());
	}
	
	public void reponseWithMessage(Object obj) {
		httpCode = HttpURLConnection.HTTP_OK;
		toWriteRet = new HashMap<String, Object>();
		toWriteRet.put("errorcode", "0");
		toWriteRet.put("ret", obj);
	}
	
	/**
	 * 设置共享的头
	 */
	@SuppressWarnings("unused")
	private void setCommonHeaders() {
		addHeader("Content-Type", "json;charset=utf-8");
	}
	
	private byte [] encodeSendData(String msg) throws Exception {
		byte [] result = null;
		if (getHeaders().containsKey("Accept-Encoding")) {
			String acceptEnc = getHeaders().get("Accept-Encoding").get(0);
			if (acceptEnc == null || !acceptEnc.toLowerCase().equals("gzip")) {
				//程序编码为UTF-8默认为UTF-8
				result = msg.getBytes();
			} else {
				result = StreamCompressAndDecompress
						.compress(msg.getBytes());
				
			}
		} else {
			result = msg.getBytes();
		}
		return result;
	}
	
	private void sendData(String msg) {
//		System.out.println("mesg is"+ msg);
		BufferedOutputStream bs = null;
		try {
			bs = new BufferedOutputStream(getBody());
			byte [] sendMsg = encodeSendData(msg);
//			System.out.println("mesg is byte"+ new String(sendMsg));
			bs.write(sendMsg, 0, sendMsg.length);
			bs.flush();
			bs.close();
		} catch(Throwable t) {
			
		} finally {
			IOUtil.forceClose(bs);
			close();
		}
	}
	
	/**
	 * 最后写入数据
	 */
	public void flushWriteInfo() {
		if(toWriteRet == null) {
			reponseWithMessage("OK");
		}
		String msg = JSON.toJSONString(toWriteRet, SerializerFeature.WriteMapNullValue);		
		try {
			//setCommonHeaders();
			sendResponseHeaders(httpCode, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		sendData(msg);
		
	}
	
	public void appendRet(String key, Object obj) {
		if(toWriteRet == null) {
			reponseWithMessage("OK");
		}
		toWriteRet.put(key, obj);
	}
}
