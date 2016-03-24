package com.fcs.invoke.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Lucare.Feng on 2016/3/24.
 */
public abstract class AbstractHttpServlet extends HttpServlet {

    private static final long serialVersionUID = 137623590564749575L;

    public AbstractHttpServlet() {
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        this.handleRequest(request, response);
    }

    protected abstract void handleRequest(HttpServletRequest var1, HttpServletResponse var2) throws IOException, ServletException;
}
