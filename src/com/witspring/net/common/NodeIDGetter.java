package com.witspring.net.common;


public interface NodeIDGetter {

	/**
	 * 获取对应的nodeID
	 * @param node 节点配置
	 * @return 如无记录，返回null
	 */
	String getNodeId(NodeConf node);
}
