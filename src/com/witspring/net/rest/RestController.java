package com.witspring.net.rest;

import java.util.Map;

import com.witspring.net.rest.RestParamMap.ExpType;
import com.witspring.net.rest.RestParamMap.SMExpHandler;
import com.witspring.net.rest.exception.RestException;
import com.witspring.net.rest.exception.RestLogicException;
import com.witspring.net.rest.exception.RestParamException;

public class RestController implements SMExpHandler {

	protected RestParamMap params;
	
	protected Map<String, String> cookies;
	
	protected RestRequest request;
	
	protected RestResponse response;
	
	public RestController() {
	}

	public void setRequest(RestRequest request) {
		this.request = request;
		params = request.getParamMap();
		params.setHandler(this);
		cookies = request.getCookieMap();
	}

	public RestRequest getRequest() {
		return request;
	}

	public void setResponse(RestResponse response) {
		this.response = response;
	}

	public RestResponse getResponse() {
		return response;
	}
	
	public final void responseMessage(String msg) {
		response.reponseWithMessage(msg);
	}
	
	public final void responseMessage(RestException exp) {
		response.reponseWithException(exp);
	}

	@Override
	public Exception createStringMapException(ExpType type, String param) {
		switch(type) {
		case MISSED:
			return new RestParamException(RestParamErrorType.MISSED, param);
		case INVALID:
			return new RestParamException(RestParamErrorType.INVALID_VALUE, param);
		default:
			return new RestLogicException("Unknow StringMap Exception:" + param);
		}
	}
	
}
