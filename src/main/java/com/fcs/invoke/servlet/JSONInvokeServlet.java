package com.fcs.invoke.servlet;

import com.alibaba.fastjson.JSON;
import com.fcs.common.exception.InvokeNotFoundException;
import com.fcs.common.exception.MessageException;
import com.fcs.common.utils.CommonUtils;
import com.fcs.invoke.*;
import com.fcs.invoke.annotation.LoginStateType;
import com.fcs.invoke.utils.CookieUtils;
import com.fcs.tsession.TSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Lucare.Feng on 2016/3/24.
 */
public class JSONInvokeServlet extends AbstractJSONHttpServlet {
    private static final long serialVersionUID = -6252867909812875308L;
    private static final Logger logger = LoggerFactory.getLogger(JSONInvokeServlet.class);
    private String invokerName = null;
    protected String serviceName = null;

    public JSONInvokeServlet() {
    }

    public void init(ServletConfig config) throws ServletException {
        String serviceName = config.getInitParameter("service");
        this.invokerName = config.getServletName();
        if(CommonUtils.isEmpty(serviceName)) {
            serviceName = this.invokerName.replace("Invoker", "Service");
        }

        this.serviceName = serviceName;
    }

    protected void handleJSONRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException, ServletException {
        String subtime = httpRequest.getParameter("subtime");
        if(subtime == null) {
            throw new IllegalArgumentException("please add subtime.");
        } else {
            String methodName = httpRequest.getParameter("method");
            if(methodName == null) {
                throw new IllegalArgumentException("please add methodName.");
            } else {
                HttpInvokeHolder invokeHolder = new HttpInvokeHolder(subtime, httpRequest, httpResponse);
                this.doInvoke(methodName, invokeHolder);
                if(httpResponse.getStatus() == 200) {
                    HashMap respMap = new HashMap(8, 1.0F);
                    respMap.put("subtime", subtime);
                    respMap.put("method", methodName);
                    respMap.put("status", Integer.valueOf(invokeHolder.getStatus()));
                    respMap.put("result", invokeHolder.getResult());
                    respMap.put("desc", invokeHolder.getDesc());
                    String output = JSON.toJSONString(respMap);
                    httpResponse.getWriter().write(output);
                    httpResponse.flushBuffer();
                }

            }
        }
    }

    protected void doInvoke(String methodName, HttpInvokeHolder invokeHolder) {
        long start = System.currentTimeMillis();
        boolean invokeResult = false;
        boolean isMessage = false;
        MethodHolder methodHolder = null;
        Object[] invokeArgs = null;

        try {
            ServiceHolder t = InvokeManager.foundServiceHolder(this.serviceName);
            if(t == null) {
                throw new InvokeNotFoundException("unfound service=[" + this.serviceName + "] @ " + this.invokerName);
            }

            methodHolder = t.getMethodHolder(methodName);
            if(methodHolder == null) {
                throw new InvokeNotFoundException("unfound method=[" + methodName + "] from service=[" + this.serviceName + "] @ " + this.invokerName);
            }

            this.checkState(methodHolder, invokeHolder);
            if(!invokeHolder.isOk()) {
                return;
            }

            invokeArgs = HttpInvokeHelper.extractInvokeParameters(methodHolder.getArgsNames(), methodHolder.getArgsMarkers(), invokeHolder.getHttpRequest());
            InvokeManager.registerInvokeHolder(invokeHolder);
            methodHolder.getMethod().invoke(t.getService(), invokeArgs);
            invokeResult = true;
        } catch (Throwable var19) {
            Throwable cause = CommonUtils.foundRealThrowable(var19);
            String reson = CommonUtils.formatThrowable(cause);
            isMessage = cause instanceof MessageException;
            if(!isMessage) {
                Map desc = invokeHolder.getHttpRequest().getParameterMap();
                HashMap parameters = new HashMap();
                if(desc != null) {
                    Iterator i$ = desc.keySet().iterator();

                    while(i$.hasNext()) {
                        String key = (String)i$.next();
                        parameters.put(key, Arrays.toString((Object[])desc.get(key)));
                    }
                }

                logger.error("invoke service={}, method={}, userid={}, args={}, parameters={} with:{}", new Object[]{this.serviceName, methodName, "", invokeArgs, parameters, reson});
            }

            if(cause instanceof MustLoginException) {
                redirectLogin(invokeHolder);
            }

            if(cause instanceof NotFoundException) {
                redirectNotFound(invokeHolder);
            } else {
                String desc1 = "系统繁忙, 请稍后重试.";
                if(invokeHolder.isDebug()) {
                    desc1 = CommonUtils.formatThrowableForHtml(cause);
                } else if(isMessage) {
                    desc1 = cause.getMessage();
                }

                invokeHolder.setDesc(desc1);
            }
        } finally {
            InvokeManager.releaseInvoke();
        }

    }

    protected void checkState(MethodHolder mHolder, HttpInvokeHolder invokeHolder) {
        if(!invokeHolder.isDebug()) {
            boolean isLogin = TSessionManager.get(CookieUtils.getCookie("sid", invokeHolder.getHttpRequest())) != null;
            if(mHolder.getLoginState() == LoginStateType.MUST_LOGIN && !isLogin) {
                redirectLogin(invokeHolder);
            } else if(mHolder.getLoginState() == LoginStateType.FORBIN_LOGIN && isLogin) {
                forbiddenLogin(invokeHolder);
            }
        }

    }

    private static void forbiddenLogin(InvokeHolder invokeHolder) {
        invokeHolder.setResult("/account.html");
        invokeHolder.setDesc(302, (String)null);
    }

    private static void redirectNotFound(InvokeHolder invokeHolder) {
        invokeHolder.setResult("/404.html");
        invokeHolder.setDesc(404, (String)null);
    }

    private static void redirectLogin(InvokeHolder invokeHolder) {
        invokeHolder.setResult("/user-login.html");
        invokeHolder.setDesc(302, (String)null);
    }
}
