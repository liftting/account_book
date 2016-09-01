package com.cyg.cygnet.base;

/**
 * Created by wm on 16/8/30.
 */
public class CtResult<T> {

    public static final int ERROR = 0;
    public static final int SUCCESS = 1;

    private int status;
    private T result;
    private String msg;
    private Object obj;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}
