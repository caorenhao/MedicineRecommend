package com.witspring.net.common;

/**
 * 网络通信的角色
 * @author vernkin
 *
 */
public enum NetRole {
	/** 服务器端 */
	SERVER(true, false),
	/** 客户端 */
	CLIENT(false, true),
	/** 服务端和客户端同时具备 */
	SERVER_CLIENT(true, true);
	
	private boolean isServer;
	private boolean isClient;
	
	private NetRole(boolean isServer, boolean isClient) {
		this.isServer = isServer;
		this.isClient = isClient;
	}
	
	public boolean isServer() {
		return isServer;
	}
	
	public boolean isClient() {
		return isClient;
	}
}
