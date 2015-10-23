package com.witspring.net.common;

/**
 * 获取节点的模式
 * @author vernkin
 *
 */
public interface NodeModeGetter {

	/**
	 * 节点模式
	 * @author vernkin
	 *
	 */
	public static enum NodeMode {
		/** 活跃状态中 */
		AVTIVE,
		/** 存在记录，但是不活跃了 */
		INACTIVE,
		/** 找不到相关记录 */
		NOT_FOUND
	}
	
	/**
	 * 根据nodeId 获取 节点模式
	 * @param nodeId
	 * @return
	 */
	NodeMode getMode(String nodeId);
}
