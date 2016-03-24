package com.fcs.common.utils;

/**
 * Created by Lucare.Feng on 2016/3/24.
 */
public class XmlException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public XmlException(String message) {
        super(message);
    }

    public XmlException(String message, Throwable cause) {
        super(message, cause);
    }
}
