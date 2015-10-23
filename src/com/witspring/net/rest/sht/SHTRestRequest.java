package com.witspring.net.rest.sht;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;

import com.sun.net.httpserver.HttpExchange;
import com.witspring.net.rest.RestHeaders;
import com.witspring.net.rest.RestRequest;

public class SHTRestRequest extends RestRequest {

	private HttpExchange t;
	
	private SHTRestHeaders header;
	
	public SHTRestRequest(HttpExchange t) {
		this.t = t;
		super.parseParams_();
	}

	@Override
	public String getProtocol() {
		return t.getProtocol();
	}

	@Override
	public String getMethod() {
		return t.getRequestMethod();
	}

	@Override
	public URI getUri() {
		return t.getRequestURI();
	}

	@Override
	public RestHeaders getHeaders() {
		if(header == null)
			header = new SHTRestHeaders(t.getRequestHeaders());
		return header;
	}

	@Override
	public InputStream getBody() {
		return t.getRequestBody();
	}
	
	public InetSocketAddress getRemoteAddress (){
		return t.getRemoteAddress();
    }

    public InetSocketAddress getLocalAddress (){
    	return t.getLocalAddress();
    }
    
    public Object getAttribute (String name) {
    	return t.getAttribute (name);
    }

    public void setAttribute (String name, Object value) {
    	t.setAttribute (name, value);
    }

    public void setStreams (InputStream i, OutputStream o) {
    	t.setStreams (i, o);
    }
}
