package com.fcs.invoke;

import com.fcs.invoke.annotation.LoginStateType;

import java.lang.reflect.Method;

/**
 * Created by Lucare.Feng on 2016/3/24.
 */
public class MethodHolder {
    private Method method;
    private Class<?> returnType;
    private Object[] argsMarkers;
    private String[] argsNames;
    private LoginStateType loginState;

    public MethodHolder(Method method, Class<?> returnType, Object[] argsMarkers, String[] argsNames, LoginStateType loginState) {
        this.method = method;
        this.returnType = returnType;
        this.argsMarkers = argsMarkers;
        this.argsNames = argsNames;
        this.loginState = loginState;
    }

    public Method getMethod() {
        return this.method;
    }

    public Object[] getArgsMarkers() {
        return this.argsMarkers;
    }

    public String[] getArgsNames() {
        return this.argsNames;
    }

    public Class<?> getReturnType() {
        return this.returnType;
    }

    public LoginStateType getLoginState() {
        return this.loginState;
    }
}
