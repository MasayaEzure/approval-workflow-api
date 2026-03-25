package com.example.approval_workflow_api.domain;

import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "request_histories")
public class RequestHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // どの申請か
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;

    // 誰が操作したか
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "actor_id", nullable = false)
    private User actor;

    // イベント（SUBMIT / APPROVE / REJECT）
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HistoryEvent event;

    // メッセージ（任意）
    @Column(length = 1000)
    private String message;

    // 作成日時
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected RequestHistory() {
        // JPA用のデフォルトコンストラクタ
    }

    public RequestHistory(Request request, User actor, HistoryEvent event, String message) {
        this.request = request;
        this.actor = actor;
        this.event = event;
        this.message = message;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Request getRequest() {
        return request;
    }

    public User getActor() {
        return actor;
    }

    public HistoryEvent getEvent() {
        return event;
    }

    public String getMessage() {
        return message;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestHistory that = (RequestHistory) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
