package com.witspring.mrecommend.front;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.witspring.common.ProcessCtr;
import com.witspring.net.rest.ControllerRouterMgr;
import com.witspring.net.rest.RestRequest;
import com.witspring.net.rest.RestResponse;
import com.witspring.net.rest.sht.SHTRestRequest;
import com.witspring.net.rest.sht.SHTRestResponse;

/**
 * TODO Put here a description of what this class does.
 *
 * @author renhao.cao.
 *         Created 2014-9-25.
 */
public class IYQFrontHandler implements HttpHandler {
	
	private ControllerRouterMgr mgr;
	
	/**
	 * TODO Put here a description of what this constructor does.
	 *
	 * @throws Exception
	 */
	public IYQFrontHandler() throws Exception {
		// 取得项目根目录下所有jar文件
		List<File> jarFiles = new ArrayList<File>();
		for(File f : ProcessCtr.getLocalFIndexRoot().listFiles()) {
			if(!f.isFile() || !f.getName().toLowerCase().endsWith(".jar"))
				continue;
			System.out.println("Add jar file: " + f);
			jarFiles.add(f);
		}
		
		if(jarFiles.isEmpty())
			jarFiles = null;
		this.mgr = new ControllerRouterMgr("com.witspring.mrecommend.front", jarFiles);	
	}
	
	@Override
	public void handle(HttpExchange t) throws IOException {
		RestRequest req = new SHTRestRequest(t);
		RestResponse res = new SHTRestResponse(t);
		try {
			this.mgr.execute(req, res);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
