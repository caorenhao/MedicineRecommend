package com.witspring.net.rest.sht;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.net.httpserver.Headers;
import com.witspring.net.rest.RestHeaders;

public class SHTRestHeaders extends RestHeaders {

	private Headers h;
	
	public SHTRestHeaders(Headers h) {
		this.h = h;
	}
	
	@Override
	public void clear() {
		h.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return h.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return h.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<String, List<String>>> entrySet() {
		return h.entrySet();
	}

	@Override
	public List<String> get(Object key) {
		return h.get(key);
	}

	@Override
	public boolean isEmpty() {
		return h.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		return h.keySet();
	}

	@Override
	public List<String> put(String key, List<String> value) {
		return h.put(key, value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends List<String>> m) {
		h.putAll(m);
	}

	@Override
	public List<String> remove(Object key) {
		return h.remove(key);
	}

	@Override
	public int size() {
		return h.size();
	}

	@Override
	public Collection<List<String>> values() {
		return h.values();
	}

	@Override
	public String getFirst(String key) {
		return h.getFirst(key);
	}
	
	public boolean equals(Object o) {
		return h.equals(o);
	}

    public int hashCode() {
    	return h.hashCode();
	}

	@Override
	public void add(String key, String value) {
		h.add(key, value);
	}

}
