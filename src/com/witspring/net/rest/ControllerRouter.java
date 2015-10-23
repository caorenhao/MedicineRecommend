package com.witspring.net.rest;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.witspring.net.rest.resource.NotRouter;

public class ControllerRouter {

	private Class<? extends RestController> ctrlClass;
	
	private Map<String, MethodRouter> methodMap = new HashMap<String, MethodRouter>();
	
	/** 默认的主页， 方法名为 index */
	private MethodRouter indexMR;
	
	/** 初始化执行的方法, setUp() */
	private Method setUpMethod;
	
	public ControllerRouter(Class<? extends RestController> ctrlClass) throws Exception {
		this.ctrlClass = ctrlClass;
		//System.out.println("Class: " + ctrlClass);
		loadMethodRouters_();
	}
	
	private void loadMethodRouters_() throws Exception {
		// 仅限于自己定义的
		for(Method m : ctrlClass.getDeclaredMethods()) {
			int modifier = m.getModifiers();
			// must be public method
			if(!Modifier.isPublic(modifier))
				continue;
			if(setSpecialMethods(m))
				continue;
			
			if(m.getAnnotation(NotRouter.class) != null) {
				continue;
			}
			//System.out.println("Get Method: " + m.getName());
			
			MethodRouter mr = new MethodRouter(m);
			String context = m.getName().toLowerCase();
			methodMap.put(context, mr);
			if(context.equalsIgnoreCase("index"))
				indexMR = mr;
		}
		
		// 特殊方法遍历父类
		for(Method m : ctrlClass.getMethods()) {
			setSpecialMethods(m);
		}
	}
	
	private boolean setSpecialMethods(Method m) {
		String name = m.getName();
		if(name.equals("setUp")) {
			setUpMethod = m;
			return true;
		}
		return false;
	}
	
	public MethodRouter getMethodRouter(String context) {
		if(context == null || context.isEmpty())
			return indexMR;
		
		return methodMap.get(context.toLowerCase());
	}
	
	public Method getSetUpMethod() {
		return setUpMethod;
	}
	
	public Class<? extends RestController> getControllerClass() {
		return ctrlClass;
	}
}
