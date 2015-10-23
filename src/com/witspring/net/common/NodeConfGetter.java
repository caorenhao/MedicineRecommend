package com.witspring.net.common;


/**
 * 通过node ID获取 配置类
 * @author vernkin
 *
 */
public interface NodeConfGetter {

	/**
	 * 通过node ID获取 配置类
	 * @param nodeId 节点ID
	 * @return null表示没有相关记录
	 */
	NodeConf getNodeConf(String nodeId);
}
