/*
 *
 * Copyright (c) 2015, joyowo. All rights reserved.
 *
 */
package com.dzr.web.framework.exception;

import com.dzr.web.util.ApiResultUtil;
import com.dzr.web.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 对系统中的异常处理，这里取名errcode和errmsg主要是参考了微信公众号API
 *
 * @author zhangsheng
 * @version 1.0
 */
@ControllerAdvice
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionControllerAdvice.class);

    @ExceptionHandler({Exception.class})
    protected ResponseEntity<Object> handleInvalidRequest(Exception e, WebRequest request) {
        Map<String, String> error = new HashMap<>();

        if (e instanceof ApiException) {
            logger.error(e.getMessage());
            ApiException apiException = (ApiException) e;
            error.put("errcode", apiException.getErrcode().toString());
            String errmsg = "";
            if (!StringUtils.isNull(apiException.getErrmsg())) {
                errmsg += apiException.getErrmsg();
            }
            errmsg += ApiResultUtil.getInstance().getErrorInfo(apiException.getErrcode().toString());
            error.put("errmsg", errmsg);
        } else if (e instanceof DuplicateKeyException) {
            // 主键重复
            logger.error(e.getMessage());
            error.put("errcode", "10006");
            error.put("errmsg", "系统异常,请稍候再试.");
            error.put("msg2", ApiResultUtil.getInstance().getErrorInfo("10006"));
        } else if (e instanceof BadSqlGrammarException) {
            logger.error(e.getMessage());
            error.put("errcode", "10005");
            error.put("errmsg", "系统异常,请稍候再试.");
            error.put("msg2", "sql error: " + ((BadSqlGrammarException) e).getSQLException().getMessage());
        } else if (e instanceof SQLException) {
            logger.error(e.getMessage());
            error.put("errcode", "10005");
            error.put("errmsg", "系统异常,请稍候再试.");
            error.put("msg2", "sql error: " + e.getMessage());
        } else {
            logger.error(e.getMessage());
            error.put("errcode", "10001");
            error.put("errmsg", "系统异常,请稍候再试.");
            error.put("msg2", "System error");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/json;charset=utf-8"));
        return handleExceptionInternal(e, error, headers, HttpStatus.OK, request);
    }

    /**
     * Customize the response for BindException.
     * <p>This method delegates to {@link #handleExceptionInternal}.
     *
     * @param ex      the exception
     * @param headers the headers to be written to the response
     * @param status  the selected response status
     * @param request the current request
     * @return a {@code ResponseEntity} instance
     */
    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, String> error = new HashMap<>();
        List<ObjectError> objectErrors = ex.getAllErrors();
        String errmsg = objectErrors.stream()
                .map(ObjectError::getDefaultMessage).collect(Collectors.joining(","));

        logger.error(errmsg);
        error.put("errcode", "20000");
        error.put("errmsg", errmsg);
        error.put("msg2", errmsg);

        return handleExceptionInternal(ex, error, headers, HttpStatus.OK, request);
    }
}
