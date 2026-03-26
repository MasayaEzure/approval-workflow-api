package com.example.approval_workflow_api.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AccessLogFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(AccessLogFilter.class);

    private static final SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
     throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        log.info("ログ出力開始 :{}", date.format(new Date()));
        log.info("HTTPメソッド: {}", httpRequest.getMethod());
        log.info("リクエストURI: {}", httpRequest.getRequestURI());
        log.info("リクエストヘッダー: {}", httpRequest.getHeaderNames());
        log.info("リクエストボディ: {}", httpRequest.getReader().readLine());

        chain.doFilter(request, response);

        log.info("レスポンスステータス: {}", httpResponse.getStatus());
        log.info("レスポンスヘッダー: {}", httpResponse.getHeaderNames());
        log.info("レスポンスボディ: {}", httpResponse.getWriter().toString());
        log.info("ログ出力終了 :{}", date.format(new Date()));
    }
}