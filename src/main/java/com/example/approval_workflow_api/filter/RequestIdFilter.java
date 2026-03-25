package com.example.approval_workflow_api.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

public class RequestIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
     throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        // UUIDを生成してリクエストIDとして設定
        String requestId = UUID.randomUUID().toString();

        // リクエストスコープにリクエストIDを設定
        httpRequest.setAttribute("requestId", requestId);
        // レスポンスヘッダーにX-Request-Idを設定
        httpResponse.setHeader("X-Request-Id", requestId);
        // 次のフィルターに処理を委譲
        chain.doFilter(request, response);
    }
}