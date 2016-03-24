package com.fcs.invoke;

import com.fcs.common.utils.CommonUtils;
import com.fcs.common.utils.DomNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Lucare.Feng on 2016/3/24.
 */
public class InvokeManager {
    private static final Logger logger = LoggerFactory.getLogger(InvokeManager.class);
    private static final String __INVOKE__HOLDER__KEY__ = "__invoke__holder__thread__local__key__";
    private static final ThreadLocal<Map<String, Object>> __invoke__context__ = new ThreadLocal();
    private static final Map<String, ServiceHolder> __service__holder__container__ = new HashMap();

    private InvokeManager() {
    }

    public static void main(String[] args) {
        start();
    }

    public static synchronized void start() {
        DomNode root = DomNode.getRootFromClassPath("services.xml");
        List list = root.elements("service");
        Iterator i$ = list.iterator();

        while(i$.hasNext()) {
            DomNode db = (DomNode)i$.next();
            String name = db.attributeValue("name");
            String apiNode = db.elementText("home-api");
            String classNode = db.elementText("home-class");
            Class apiClass = CommonUtils.classForName(apiNode.trim());
            Object service = CommonUtils.newInstance(classNode.trim());
            ServiceHolder holder = new ServiceHolder(name, apiClass, service);
            __service__holder__container__.put(name, holder);
        }

        logger.info("invoke manager started...");
    }

    public static ServiceHolder foundServiceHolder(String name) {
        return (ServiceHolder)__service__holder__container__.get(name);
    }

    public static void registerInvokeHolder(InvokeHolder iHolder) {
        Object __context__ = (Map)__invoke__context__.get();
        if(__context__ == null) {
            __context__ = new HashMap();
            __invoke__context__.set(__context__);
        }

        ((Map)__context__).put("__invoke__holder__thread__local__key__", iHolder);
    }

    public static void releaseInvoke() {
        Map context = (Map)__invoke__context__.get();
        if(context != null) {
            context.clear();
        }

    }

    public static InvokeHolder getInvokeHolder() {
        return (InvokeHolder)foundInvokeValue("__invoke__holder__thread__local__key__");
    }

    private static <T> T foundInvokeValue(String key) {
        Map context = (Map)__invoke__context__.get();
        return context == null?null:context.get(key);
    }
}
