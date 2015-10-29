package com.witspring.util.db;

/**
 * 配置异常
 * @author vernkin
 */
public class ConfigurationException extends Exception {

    private static final long serialVersionUID = 387598254967145662L;

    public ConfigurationException(String message) {
        super(message);
    }
    
    public ConfigurationException(String message, Throwable t) {
        super(message, t);
    }
}
