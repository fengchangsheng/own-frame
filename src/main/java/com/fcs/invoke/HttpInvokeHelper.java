package com.fcs.invoke;

import com.fcs.common.utils.CommonUtils;
import com.fcs.common.utils.GenericUtils;
import com.fcs.common.utils.StringUtil;
import com.fcs.common.utils.TimeUtils;
import com.fcs.invoke.utils.CookieUtils;
import com.fcs.invoke.utils.NetUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * Created by Lucare.Feng on 2016/3/25.
 */
public class HttpInvokeHelper {
    public static final String __debug__tag__ = "debug";

    public HttpInvokeHelper() {
    }

    public static void setInvokeResult(Object result) {
        getInvokeHolder().setResult(result);
    }

    public static Object getInvokeResult() {
        return getInvokeHolder().getResult();
    }

    public static void setInvokeDesc(String desc) {
        getInvokeHolder().setDesc(desc);
    }

    private static InvokeHolder getInvokeHolder() {
        return InvokeManager.getInvokeHolder();
    }

    public static String getInvokeId() {
        return getInvokeHolder().getId();
    }

    public static HttpServletRequest getHttpServletRequest() {
        InvokeHolder iHolder = InvokeManager.getInvokeHolder();
        return iHolder != null && iHolder instanceof HttpInvokeHolder?((HttpInvokeHolder)iHolder).getHttpRequest():null;
    }

    public static HttpServletResponse getHttpServletResponse() {
        InvokeHolder iHolder = InvokeManager.getInvokeHolder();
        return iHolder != null && iHolder instanceof HttpInvokeHolder?((HttpInvokeHolder)iHolder).getHttpResponse():null;
    }

    public static void sendRedirect(String redirect) {
        try {
            getHttpServletResponse().sendRedirect(redirect);
        } catch (Exception var2) {
            CommonUtils.illegalStateException(var2);
        }

    }

    public static String getRequestIP() {
        return NetUtils.getRequestIP(getHttpServletRequest());
    }

    public static <T> T getAttribute(String key) {
        return getAttribute(key, getHttpServletRequest());
    }

    public static <T> T getAttribute(String key, HttpServletRequest httpRequest) {
        HttpSession session = httpRequest == null?null:httpRequest.getSession(false);
        return session == null?null:session.getAttribute(key);
    }

    public static String register(int timeout, String key, Object user) {
        HttpServletRequest request = getHttpServletRequest();
        HttpSession session = request.getSession(true);
        session.setMaxInactiveInterval(timeout);
        session.setAttribute(key, user);
        return session.getId();
    }

    public static <T> T unregister(String key) {
        HttpServletRequest request = getHttpServletRequest();
        HttpSession session = request.getSession(false);
        Object stamp = null;
        if(session != null) {
            stamp = session.getAttribute(key);
            session.removeAttribute(key);
            session.setMaxInactiveInterval(1);
        }

        return stamp;
    }

    public static Object[] extractInvokeParameters(String[] argsNames, Object[] argsMarks, HttpServletRequest request) {
        Object[] parameters = null;
        if(argsNames != null) {
            parameters = new Object[argsNames.length];

            for(int index = 0; index < argsNames.length; ++index) {
                String target = request.getParameter(argsNames[index]);
                parameters[index] = target == null?argsMarks[index]: GenericUtils.parseObject(argsMarks[index], target);
            }
        }

        return parameters;
    }

    public static boolean checkPostTime() {
        try {
            String e = CookieUtils.getCookie("user_post_time", getHttpServletRequest());
            if(StringUtil.isEmpty(e)) {
                CookieUtils.setCookie("user_post_time", TimeUtils.stringOfUnixtimestampNow(), 30, getHttpServletResponse());
                return true;
            } else {
                long diff = TimeUtils.getUnixTimestamp(new Date()) - Long.valueOf(e).longValue();
                if(diff > 30L) {
                    CookieUtils.setCookie("user_post_time", TimeUtils.stringOfUnixtimestampNow(), 30, getHttpServletResponse());
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception var3) {
            return true;
        }
    }
}
