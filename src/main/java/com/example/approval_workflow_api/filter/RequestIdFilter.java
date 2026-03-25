package com.example.approval_workflow_api.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.util.UUID;


public class RequestIdFilter implements Filter {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestId = UUID.randomUUID().toString();

        try {
            httpRequest.setAttribute("requestId", requestId);
            httpResponse.getWriter().append("Request ID: " + requestId);
            chain.doFilter(request, response);
        } catch (IOException | ServletException e) {
            throw new RuntimeException(e);
        } finally {
            httpRequest.removeAttribute("requestId");
        }
    }

}