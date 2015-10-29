package com.witspring.util.db;

public class DBFactoryException extends Exception {

    private static final long serialVersionUID = -3562988112800354197L;
    
    public DBFactoryException(String message) {
        super(message);
    }
    
    public DBFactoryException(String message, Throwable t) {
        super(message, t);
    }
}
