package com.example.approval_workflow_api.dto;

import com.example.approval_workflow_api.domain.RequestStatus;

public class RequestResponseDto {
    
    private Long id;
    private RequestStatus status;
    
    public RequestResponseDto() {
    }
    
    public RequestResponseDto(Long id, RequestStatus status) {
        this.id = id;
        this.status = status;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public RequestStatus getStatus() {
        return status;
    }
    
    public void setStatus(RequestStatus status) {
        this.status = status;
    }
}