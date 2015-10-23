package com.witspring.net.rest;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSONObject;
import com.witspring.net.rest.exception.RestException;
import com.witspring.net.rest.exception.RestLogicException;
import com.witspring.net.rest.exception.RestParamException;
import com.witspring.net.rest.exception.RestRouterException;
import com.witspring.net.rest.exception.RestSessionException;
import com.witspring.util.NetUtil;
import com.witspring.util.ReflectionUtil;

public class ControllerRouterMgr {

	private final String CTRL_SUFFIX = "Controller";
	
	private final String CTRL_ROOT = "Application";
	
	private String rootPkgName;
	
	/** ApplicationController */
	private ControllerRouter rootCtrl;
	private Map<String, ControllerRouter> ctrlMap = 
		new HashMap<String, ControllerRouter>();
	
	/** 可能包含Controller的jarFile */
	private List<File> jarFiles = null;
	
	public ControllerRouterMgr(String rootPkgName) throws Exception {
		this.rootPkgName = rootPkgName;
		loadCtrlRouters_();
	}
	
	public ControllerRouterMgr(String rootPkgName, List<File> jarFiles) throws Exception {
		this.rootPkgName = rootPkgName;
		this.jarFiles = jarFiles;
		loadCtrlRouters_();
	}
	
	private void loadCtrlRouters_() throws Exception {
		String pkgName = this.rootPkgName + ".controller";
		loadControllerClass(ReflectionUtil.getClasses(pkgName));
		if(jarFiles != null && !jarFiles.isEmpty()) {
			// 转换pkgName的"." 为 "/"
			pkgName = pkgName.replaceAll("\\.", "/");
			for(File jarFile : jarFiles) {
				Set<Class<?>> classes = ReflectionUtil.getFromJARFile(
						jarFile.getAbsolutePath(), pkgName);
				loadControllerClass(classes);
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void loadControllerClass(Set<Class<?>> classes) throws Exception {
		for (Class c : classes) {
			String className = c.getSimpleName();
			if(!className.endsWith(CTRL_SUFFIX))
				continue;
			// Can check Super Class here 
			String context = className.substring(0, className.length() - 
					CTRL_SUFFIX.length()).toLowerCase();
			if(context.equalsIgnoreCase(CTRL_ROOT)) {
				rootCtrl = new ControllerRouter(c);
			} else {
				//System.out.println("Add Controller: " + context);
				ControllerRouter cr = new ControllerRouter(c);
				ctrlMap.put(context, cr);
			}
		}
	}
	
	public void execute(RestRequest request, RestResponse response) {
		long executeTimeStart = NetUtil.getTimeInMillis();
		JSONObject timeObj = new JSONObject();
		String ctrlName = request.getControllerName();
		String methodName = request.getMethodName();
		String path = request.getPath();
		System.out.println("[" + Thread.currentThread().getName() + 
				"]: Request " + ctrlName + "/" + methodName + ": " + request.getParamMap());
		ControllerRouter cr = getControllerRouter(ctrlName);
		if(cr == null) {
			response.reponseWithException(new RestRouterException(
					RestRouterException.Type.NOT_FOUND, path));
			response.flushWriteInfo();
			return;
		}
		
		MethodRouter mr = cr.getMethodRouter(methodName);
		if(mr == null) {
			response.reponseWithException(new RestRouterException(
					RestRouterException.Type.NOT_FOUND, path));
			response.flushWriteInfo();
			return;
		}
		
		// 执行具体的操作
		try {
			RestController rc = cr.getControllerClass().newInstance();
			rc.setRequest(request);
			rc.setResponse(response);
			
			long startTime = NetUtil.getTimeInMillis();
			// 若有初始化方法先初始化
			Method setUpMethod = cr.getSetUpMethod();
			if(setUpMethod != null)
				setUpMethod.invoke(rc);
			timeObj.put("setup", NetUtil.getTimeInMillis() - startTime);
			if(!mr.supportRequestMethod(request.getMethod())) {
				response.reponseWithException(new RestRouterException(
						RestRouterException.Type.REQMETHOD_NOT_SUPPOR, path));
				response.flushWriteInfo();
				return;
			}
			
			startTime = NetUtil.getTimeInMillis();

			mr.getMethod().invoke(rc);
			timeObj.put("method", NetUtil.getTimeInMillis() - startTime);
			timeObj.put("execute", NetUtil.getTimeInMillis() - executeTimeStart);
		} catch(Throwable t) {
			// 要汇报的异常
			RestException reportException = null;
			if(t instanceof InvocationTargetException) {
				boolean print = true;
				Throwable cause = t.getCause();
				if(cause instanceof RestParamException) {
					// 参数错误不打印
					print = false;
					reportException = (RestException)cause;
				} else if(cause instanceof RestSessionException) {
					// 会话错误不打印
					print = false;
					reportException = (RestException)cause;
				} else if(cause instanceof RestException) {
					reportException = (RestException)cause;
				} else {
					// 包装其它异常
					reportException = new RestLogicException(cause.getMessage(), cause);
				}
				
				if(print) {
					cause.printStackTrace();
				}
			} else if(t instanceof RestException) {
				reportException = (RestException)t;
				t.printStackTrace();
			} else {
				reportException = new RestLogicException(t.getMessage(), t);
				t.printStackTrace();
			}
			
			response.reponseWithException(reportException);
		} finally {
			response.appendRet("times", timeObj);
			response.flushWriteInfo();
		}
		System.out.println("[" + Thread.currentThread().getName() + "]:" + timeObj);
	}
	
	public ControllerRouter getControllerRouter(String context) {
		if(context == null || context.isEmpty())
			return rootCtrl;
		return ctrlMap.get(context.toLowerCase());
	}
	
	public static void main(String[] args) throws Exception {
		new ControllerRouterMgr("com.dbapp.iyq.front");
	}
}
