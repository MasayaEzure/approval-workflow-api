package com.example.approval_workflow_api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RequestApproveDto {

    @NotNull(message = "承認者IDは必須です")
    private Long approverId;

    @Size(max = 1000, message = "コメントは1000文字以内で入力してください")
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
