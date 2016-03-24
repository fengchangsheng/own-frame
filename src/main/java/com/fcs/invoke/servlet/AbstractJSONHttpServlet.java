package com.fcs.invoke.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Lucare.Feng on 2016/3/24.
 */
public abstract class AbstractJSONHttpServlet extends AbstractHttpServlet{
    private static final long serialVersionUID = 137623590564749575L;

    public AbstractJSONHttpServlet() {
    }

    protected void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("application/json; charset=UTF-8");
        this.handleJSONRequest(request, response);
    }

    protected abstract void handleJSONRequest(HttpServletRequest var1, HttpServletResponse var2) throws IOException, ServletException;
}
