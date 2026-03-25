package com.example.approval_workflow_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RequestCreateDto {

    @NotBlank(message = "タイトルは必須です")
    @Size(max = 255, message = "タイトルは255文字以内で入力してください")
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
