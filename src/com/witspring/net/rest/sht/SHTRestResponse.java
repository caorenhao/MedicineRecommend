package com.witspring.net.rest.sht;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import com.sun.net.httpserver.HttpExchange;
//import com.sun.xml.internal.ws.transport.http.client.HttpCookie;
import com.witspring.net.rest.RestHeaders;
import com.witspring.net.rest.RestResponse;

public class SHTRestResponse extends RestResponse {

	private HttpExchange t;
	
	private SHTRestHeaders header;
	
	public SHTRestResponse(HttpExchange t) {
		this.t = t;
	}

	@Override
	public void close() {
		t.close();
	}
	
	@Override
	public RestHeaders getHeaders() {
		if(header == null)
			header = new SHTRestHeaders(t.getRequestHeaders());
		return header;
	}
	
	public int getResponseCode() {
	    return t.getResponseCode();
    }
	
	protected OutputStream getBody() {
        return t.getResponseBody();
    }
	
	protected void sendResponseHeaders (int rCode, long contentLen) 
    		throws IOException {
		t.sendResponseHeaders (rCode, contentLen);
    }

	@Override
	public void addHeader(String key, String value) {
		t.getResponseHeaders().add(key, value);
	}

	@Override
	public void setCookie(Date expirationDate, String nameAndValue, String path, String domain, boolean isSecure) {
//		HttpCookie cookie = new HttpCookie(expirationDate, nameAndValue, path, domain, isSecure);
//		addHeader("Set-Cookie", cookie.toString());
	}
	
}
