package com.witspring.net.rest;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import com.witspring.net.rest.resource.Method;

/**
 * 解析方法的路由器
 * @author vernkin
 *
 */
public class MethodRouter {

	private java.lang.reflect.Method method;
	
	/** 支持的请求方式，为空表示都支持。里面的字符串为大写 */
	private Set<String> requestMethods;
	
	public MethodRouter(java.lang.reflect.Method method) throws Exception {
		this.method = method;
		parseMethod_();
	}
	
	private void parseMethod_() {
		Set<String> reqMethods = new HashSet<String>();
		Annotation[] ans = method.getAnnotations();
		for(Annotation an : ans) {
			Annotation mtdAna = an.annotationType().getAnnotation(Method.class);
			// 表示这个一个http请求方法
			if(mtdAna != null) {
				String reqMethod = ((Method)mtdAna).value();
				reqMethods.add(reqMethod.toUpperCase());
				//System.out.println("Method " + method.getName() + ": " + reqMethod);
				continue;
			}
		}
		
		if(!reqMethods.isEmpty())
			requestMethods = reqMethods;
	}
	
	public boolean supportRequestMethod(String method) {
		return requestMethods == null || requestMethods.contains(method.toUpperCase());
	}
	
	public java.lang.reflect.Method getMethod() {
		return method;
	}
}
