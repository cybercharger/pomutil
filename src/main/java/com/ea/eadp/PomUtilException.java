package com.ea.eadp;

/**
 * Created by chriskang on 12/22/2016.
 */
public class PomUtilException extends RuntimeException {
    public PomUtilException(String message) {
        super(message);
    }

    public PomUtilException(String message, Throwable cause) {
        super(message, cause);
    }

    public PomUtilException(Throwable cause) {
        super(cause);
    }

    public PomUtilException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
