package com.witspring.net.common;

/**
 * 运行环境的异常
 * @author vernkin
 *
 */
public class RunningEnvException extends Exception {

	private static final long serialVersionUID = -612786853211547930L;

	public RunningEnvException() {
	}

	public RunningEnvException(String message) {
		super(message);
	}

	public RunningEnvException(Throwable cause) {
		super(cause);
	}

	public RunningEnvException(String message, Throwable cause) {
		super(message, cause);
	}

}
