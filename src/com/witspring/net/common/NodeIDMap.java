package com.witspring.net.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;

import com.witspring.util.LoggerConfig;

/**
 * 管理NodeId 类
 * @author vernkin
 *
 */
public class NodeIDMap {
	
	private Log LOGGER = LoggerConfig.getLog(getClass());
	
	private int cnt = 0;
	
	/** NodeType => Node Id List */
	private Map<NodeType, List<String>> typeMap = new HashMap<NodeType, List<String>>();
	
	/** Node Id => NodeConf */
	private Map<String, NodeConf> idMap = new HashMap<String, NodeConf>();
	
	/** NodeConf Full Key => Node Id */
	private Map<String, String> keyMap = new HashMap<String, String>();
	
	/** the node ids owned by this node */
	private Set<String> ownedNodes = new HashSet<String>();
	
	/** 本地缓存NodeConfGetter */
	private NodeConfGetter confGetter;
	/** 本地缓存NodeIDGetter */
	private NodeIDGetter idGetter;
	
	public NodeIDMap() {
		
	}
	
	public synchronized NodeConf getConf(String nodeId) {
		return idMap.get(nodeId);
	}
	
	/**
	 * 如果是新建，自动添加到被管理的节点中
	 * @param conf
	 * @param type
	 * @return
	 */
	public synchronized String getNodeId(NodeConf conf, 
			NodeType type) throws Exception {
		String fullKey = conf.getGlobalID();
		String nodeId = keyMap.get(fullKey);
		if(nodeId != null) {
			return nodeId;
		}
		
		nodeId = createNodeId(conf, type);
		
		keyMap.put(fullKey, nodeId);
		idMap.put(nodeId, conf);
		List<String> nodeIds = typeMap.get(type);
		if(nodeIds == null) {
			nodeIds = new ArrayList<String>();
			typeMap.put(type, nodeIds);
		}
		nodeIds.add(nodeId);
		addOwnedNode(nodeId);
		return nodeId;
	}
	
	/**
	 * 只尝试去获取nodeId， 不会自动创建
	 * @param conf
	 * @return
	 */
	public synchronized String tryToGetNodeId(NodeConf conf) {
		String fullKey;
		try {
			fullKey = conf.getGlobalID();
			return keyMap.get(fullKey); 
		} catch (Exception e) {
			return null;
		}
		
	}
	
	public synchronized String tryToGetNodeId(String fullKey) {
		return keyMap.get(fullKey); 
	}
	
	/**
	 * 如果已经设置id，使用原来的id
	 * @param conf
	 * @param type
	 * @return
	 */
	private synchronized String createNodeId(NodeConf conf, NodeType type) {
		StringBuilder sb = new StringBuilder(50);
		if(type == null)
			throw new IllegalArgumentException("NodeType is not found");
		else
			sb.append(type.name());
		int id = conf.id;
		if(conf.id <= 0) {
			id = ++cnt;
		}
		
		sb.append('@').append(conf.host).append('@').append(id);
		String ret = sb.toString();
		LOGGER.info("Create NodeId[" + ret + "] for NodeConf[" + conf + 
				"] with NodeType[" + type + "]");
		return ret;
	}
	
	/**
	 * 添加受管理的nodeId
	 * @param nodeId
	 */
	public boolean addOwnedNode(String nodeId) {
		synchronized(ownedNodes) {
			return ownedNodes.add(nodeId);
		}
	}
	
	/**
	 * 移除受管理的nodeId
	 * @param nodeId
	 */
	public boolean removeOwnedNode(String nodeId) {
		synchronized(ownedNodes) {
			return ownedNodes.remove(nodeId);
		}
	}
	
	/**
	 * 获取所有被管理的节点
	 * @return
	 */
	public List<String> getAllOwnedNodes() {
		List<String> ret = new ArrayList<String>();
		synchronized(ownedNodes) {
			ret.addAll(ownedNodes);
		}
		return ret;
	}
	
	/**
	 * 创建本对象的 NodeConfGetter
	 * @return
	 */
	public NodeConfGetter createNodeConfGetter() {
		if(confGetter != null) 
			return confGetter;
		confGetter = new NodeConfGetter(){
			@Override
			public NodeConf getNodeConf(String nodeId) {
				return getConf(nodeId);
			}
			
		};
		
		return confGetter;
	}
	
	/**
	 * 创建本对象的 NodeConfGetter
	 * @return
	 */
	public NodeIDGetter createNodeIDGetter() {
		if(idGetter != null) 
			return idGetter;
		idGetter = new NodeIDGetter(){
			@Override
			public String getNodeId(NodeConf node) {
				return tryToGetNodeId(node);
			}
		};
		
		return idGetter;
	}
	
	
	/**
	 * 根据 nodeId获得节点类型
	 * @param nodeId
	 * @return null表示没有匹配的节点类型
	 */
	public static NodeType getNodeType(String nodeId) {
		int idx = nodeId.indexOf('@');
		String typeName = nodeId.substring(0, idx);
		try {
			return NodeType.valueOf(typeName);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 根据 nodeId获得归属的整数id
	 * @param nodeId
	 * @return -1 表示无法获取
	 */
	public static int getNodeIntId(String nodeId) {
		int idx = nodeId.lastIndexOf('@');
		String intIdStr = nodeId.substring(idx + 1, nodeId.length());
		try {
			return Integer.parseInt(intIdStr);
		} catch (Exception e) {
			return -1;
		}
	}
	
	/**
	 * 根据类型获取所有的节点id
	 * @param type
	 * @return
	 */
	public synchronized List<String> getNodeIdsByType(NodeType type) {
		List<String> ret = new ArrayList<String>();
		List<String> nodeIds = typeMap.get(type);
		if(nodeIds != null)
			ret.addAll(nodeIds);		
		return ret;
	}
	
	/**
	 * 根据类型和id寻找nodeId
	 * @param type
	 * @param id
	 * @return null如果找不到
	 */
	public synchronized String getNodeIdByTypeAndId(NodeType type, long id) {
		List<String> nodeIds = typeMap.get(type);
		if(nodeIds == null)
			return null;
		String suffix = "@" + id;
		for(String nodeId : nodeIds) {
			if(nodeId.endsWith(suffix))
				return nodeId;
		}
		return null;		
	}
}
