package com.witspring.net.common;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.witspring.util.IOUtil;
import com.witspring.util.PIDUtil;
import com.witspring.util.task.PeriodTask;

/**
 * 文件PID跟自身PID不一致就自杀的任务
 * @author Vernkin
 *
 */
public class NotMySuicideTask extends PeriodTask {

	private Log LOGGER = LogFactory.getLog(getClass());
	
	private File pidFile;
	
	public NotMySuicideTask(File pidFile) {
		super(5000);
		this.pidFile = pidFile;
	}

	public void run() throws Exception {
		super.run();
		String filePid = IOUtil.readFileAsString(pidFile);
		if(!filePid.equals(PIDUtil.getPid())) {
			LOGGER.warn("Not the same pid, my is " + PIDUtil.getPid() + 
					", file is " + filePid + ". Then exit mine.");
			System.exit(0);
		}
	}
	
	public void onException(Throwable t) {
		LOGGER.info("OnException", t);
	}
}
