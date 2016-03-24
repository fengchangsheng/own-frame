package com.fcs.invoke;

/**
 * Created by Lucare.Feng on 2016/3/24.
 */
public class InvokeHolder {

    private String id;
    private int status = 200;
    private String desc;
    private Object result;

    public InvokeHolder(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public boolean isOk() {
        return this.status == 200 && this.desc == null;
    }

    public void setDesc(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public void setDesc(String desc) {
        this.status = 500;
        this.desc = desc;
    }

    public int getStatus() {
        return this.status;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Object getResult() {
        return this.result;
    }

    public boolean isDebug() {
        return false;
    }
}
