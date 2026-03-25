package com.example.approval_workflow_api.dto;

import java.time.Instant;

import com.example.approval_workflow_api.domain.HistoryEvent;
import com.example.approval_workflow_api.domain.RequestHistory;

public class RequestHistoryDto {

    private Long id;
    private HistoryEvent event;
    private String actorName;
    private String message;
    private Instant createdAt;

    public RequestHistoryDto() {
    }

    public RequestHistoryDto(Long id, HistoryEvent event, String actorName,
                             String message, Instant createdAt) {
        this.id = id;
        this.event = event;
        this.actorName = actorName;
        this.message = message;
        this.createdAt = createdAt;
    }

    /**
     * エンティティから DTO を生成するファクトリメソッド
     */
    public static RequestHistoryDto from(RequestHistory history) {
        return new RequestHistoryDto(
            history.getId(),
            history.getEvent(),
            history.getActor().getName(),
            history.getMessage(),
            history.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HistoryEvent getEvent() {
        return event;
    }

    public void setEvent(HistoryEvent event) {
        this.event = event;
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
