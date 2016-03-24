package com.fcs.invoke;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Lucare.Feng on 2016/3/24.
 */
public class HttpInvokeHolder extends InvokeHolder {
    private static final String __debug__tag__ = "debug";
    private HttpServletRequest httpRequest;
    private HttpServletResponse httpResponse;

    public HttpInvokeHolder(String id, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        super(id);
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
    }

    public HttpServletRequest getHttpRequest() {
        return this.httpRequest;
    }

    public void setHttpRequest(HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public HttpServletResponse getHttpResponse() {
        return this.httpResponse;
    }

    public void setHttpResponse(HttpServletResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    public boolean isDebug() {
        return this.httpRequest.getParameter("debug") != null;
    }
}
