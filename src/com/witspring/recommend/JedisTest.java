package com.witspring.recommend;

import com.witspring.util.JedisWrapper;

public class JedisTest {

	public void delAll() {
		JedisWrapper jedis = JedisWrapper.getInstance("192.168.0.171", 6379, 50);
		System.out.println(jedis.flushDB());
	}
	
	public void del(String key) {
		JedisWrapper jedis = JedisWrapper.getInstance("192.168.0.171", 6379, 50);
		System.out.println(jedis.del(key));
	}
	
	public static void main(String[] args) {
		JedisTest test = new JedisTest();
		//test.delAll();
		test.del("8169");
	}
	
}
