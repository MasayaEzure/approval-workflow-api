package com.example.approval_workflow_api.dto;

import jakarta.validation.constraints.NotNull;

public class RequestSubmitDto {
    
    @NotNull(message = "申請者IDは必須です")
    private Long actorId;
    
    public Long getActorId() {
        return actorId;
    }
    
    public void setActorId(Long actorId) {
        this.actorId = actorId;
    }
}

