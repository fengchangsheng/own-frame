package com.fcs.common.exception;

/**
 * Created by Lucare.Feng on 2016/3/25.
 */
public class InvokeNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -684314835123125276L;

    public InvokeNotFoundException(String message) {
        super(message);
    }

    public InvokeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvokeNotFoundException(Throwable cause) {
        super(cause);
    }
}
