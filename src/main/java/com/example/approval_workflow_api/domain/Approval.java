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
@Table(name = "approvals")
public class Approval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // どの申請か
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;

    // 誰が承認/却下したか
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "approver_id", nullable = false)
    private User approver;

    // 承認アクション（APPROVE / REJECT）
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalAction action;

    // コメント（任意）
    @Column(length = 1000)
    private String comment;

    // 作成日時
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected Approval() {
        // JPA用のデフォルトコンストラクタ
    }

    public Approval(Request request, User approver, ApprovalAction action, String comment) {
        this.request = request;
        this.approver = approver;
        this.action = action;
        this.comment = comment;
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

    public User getApprover() {
        return approver;
    }

    public ApprovalAction getAction() {
        return action;
    }

    public String getComment() {
        return comment;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Approval approval = (Approval) o;
        return id != null && Objects.equals(id, approval.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
