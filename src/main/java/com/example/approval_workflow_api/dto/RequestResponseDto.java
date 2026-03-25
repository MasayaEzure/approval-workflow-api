package com.example.approval_workflow_api.dto;

import java.time.Instant;

import com.example.approval_workflow_api.domain.Request;
import com.example.approval_workflow_api.domain.RequestStatus;

public class RequestResponseDto {

    private Long id;
    private String title;
    private RequestStatus status;
    private String requesterName;
    private Instant createdAt;
    private Instant updatedAt;

    public RequestResponseDto() {
    }

    public RequestResponseDto(Long id, String title, RequestStatus status,
                              String requesterName, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.requesterName = requesterName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * エンティティから DTO を生成するファクトリメソッド
     */
    public static RequestResponseDto from(Request request) {
        return new RequestResponseDto(
            request.getId(),
            request.getTitle(),
            request.getStatus(),
            request.getRequester().getName(),
            request.getCreatedAt(),
            request.getUpdatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
