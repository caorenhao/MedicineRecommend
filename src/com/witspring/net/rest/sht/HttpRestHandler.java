package com.witspring.net.rest.sht;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.witspring.net.rest.ControllerRouterMgr;

public class HttpRestHandler implements HttpHandler {

	private ControllerRouterMgr mgr;
	
	
	public HttpRestHandler() throws Exception {
		mgr = new ControllerRouterMgr("com.dbapp.iyq.front");
	}
	
	@Override
	public void handle(HttpExchange t) throws IOException {
		System.out.println("Handler$$$$$$$$$$$$$$$$$$");
		mgr.execute(new SHTRestRequest(t), new SHTRestResponse(t));
	}

}
