package com.witspring.net.rest;

import java.util.List;
import java.util.Map;

public abstract class RestHeaders implements Map<String,List<String>> {

	public abstract String getFirst (String key);
	
	public abstract void add(String key, String value);
}
