package com.example.approval_workflow_api.dto;

import jakarta.validation.constraints.NotNull;

public class RequestApproveDto {
    
    @NotNull(message = "承認者IDは必須です")
    private Long approverId;
    
    private String comment;
    
    public Long getApproverId() {
        return approverId;
    }
    
    public void setApproverId(Long approverId) {
        this.approverId = approverId;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
}

