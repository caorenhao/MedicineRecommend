package com.witspring.net.rest.sht;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class SHTRestMain {

	public static void main(String[] args) throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress(
				"127.0.0.1", 85), 0);
		server.createContext("/", new HttpRestHandler());
		// 创建线程池，否则HttpServer单线程
		server.setExecutor(Executors.newCachedThreadPool());
		server.start();
		System.out.println("Start OK");
	}
	
	static class MyHandler implements HttpHandler {

		 public void handle(HttpExchange t) throws IOException {
		  //InputStream is = t.getRequestBody();
		  String response = "<font color='#ff0000'>come on baby</font>";
		  t.sendResponseHeaders(200, response.length());
		  OutputStream os = t.getResponseBody();
		  os.write(response.getBytes());
		  os.close();
		 }
	}

}
