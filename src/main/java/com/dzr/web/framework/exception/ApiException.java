package com.dzr.web.framework.exception;

/**
 * 自定义的异常处理类，使用在业务逻辑上的错误，包括客户端提交的参数不合法之类的
 * 对于系统异常，如空指针异常及sql错误之类的由框架自动捕获并进行处理。
 *
 * @author zhangsheng
 */
@SuppressWarnings("serial")
public class ApiException extends RuntimeException {

    private Integer errcode;
    private String errmsg;

    public ApiException(Integer errcode, String message) {
        super(message);
        this.errcode = errcode;
        this.errmsg = message;
    }

    public ApiException(Integer errorCode) {
//        super(message);
        this.errcode = errorCode;
    }

    public Integer getErrcode() {
        return errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

}