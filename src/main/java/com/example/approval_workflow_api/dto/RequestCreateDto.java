package com.example.approval_workflow_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RequestCreateDto {
    
    @NotBlank(message = "タイトルは必須です")
    private String title;
    
    @NotNull(message = "申請者IDは必須です")
    private Long requesterId;
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public Long getRequesterId() {
        return requesterId;
    }
    
    public void setRequesterId(Long requesterId) {
        this.requesterId = requesterId;
    }
}

