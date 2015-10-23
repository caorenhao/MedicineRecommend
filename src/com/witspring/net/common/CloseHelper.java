package com.witspring.net.common;

import java.io.File;

import com.witspring.util.IOUtil;
import com.witspring.util.OSUtil;
import com.witspring.util.RemoteCmdException;
import com.witspring.util.StrUtil;

public final class CloseHelper {
	
	/**
	 * 强制关闭节点
	 * @param conf
	 * @throws Exception
	 */
	public static void forceCloseNode(NodeConf node) throws Exception {
		if(node == null)
			return;
		WorkingDir dir = new WorkingDir(node.workDir, false);
		if(node.isLocalAddress()) {
			// 属于本地IP
			String pid = null;
			File pidFile = dir.getPidFile();
			if(pidFile.exists() == false) {
				// 进程文件不存在，忽略
				return;
			}
			try {
				pid = IOUtil.readFileAsString(pidFile);
			} catch(Throwable t) {
				// 无法获取PID，忽略
				return;
			}
			// 本地直接关闭进程
			OSUtil.killProcess(pid);
		} else {
			String getPidCmd = node.getSshCmd() + " cat " + dir.getPidFile().getAbsolutePath();
			String pidStr = StrUtil.nullToEmptyString(OSUtil.runRemoteProcess(getPidCmd), true);
			try {
				Integer.parseInt(pidStr);
			} catch(Throwable t) {
				throw new RemoteCmdException("Error PID: " + pidStr);
			}
			String closeCmd = node.getSshCmd() + " kill -9  " + pidStr;
			OSUtil.runRemoteProcess(closeCmd);
		}	
	}

}
