package com.maitaidan.model;

/**
 * Created by Administrator on 2016/6/7.
 */
//@Data
public class GeneralResult<T> {

    private boolean ret;
    private T data;
    private String msg;

    public GeneralResult(boolean ret, T t, String msg) {
        this.ret = ret;
        this.data = t;
        this.msg = msg;
    }

    public GeneralResult() {
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isRet() {
        return ret;
    }

    public void setRet(boolean ret) {
        this.ret = ret;
    }
}
