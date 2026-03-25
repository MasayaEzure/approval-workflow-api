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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 申請タイトル
    @Column(nullable = false, length = 255)
    private String title;

    // 申請状態
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.DRAFT;

    // 申請者
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requester_id", nullable = false, updatable = false)
    private User requester;

    // 楽観ロック用バージョン
    @Version
    private Long version;

    // 作成日時
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    // 更新日時
    @Column(nullable = false)
    private Instant updatedAt;

    protected Request() {
        // JPA用のデフォルトコンストラクタ
    }

    public Request(String title, User requester) {
        this.title = title;
        this.status = RequestStatus.DRAFT;
        this.requester = requester;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // --- 状態遷移ドメインメソッド ---

    /**
     * 申請を提出する（DRAFT -> SUBMITTED）
     * @throws IllegalStateException 現在の状態が DRAFT でない場合
     */
    public void submit() {
        if (this.status != RequestStatus.DRAFT) {
            throw new IllegalStateException(
                "申請の状態が不正です。DRAFT状態の申請のみ提出できます。現在の状態: " + this.status);
        }
        this.status = RequestStatus.SUBMITTED;
    }

    /**
     * 申請を承認する（SUBMITTED -> APPROVED）
     * @throws IllegalStateException 現在の状態が SUBMITTED でない場合
     */
    public void approve() {
        if (this.status != RequestStatus.SUBMITTED) {
            throw new IllegalStateException(
                "申請の状態が不正です。SUBMITTED状態の申請のみ承認できます。現在の状態: " + this.status);
        }
        this.status = RequestStatus.APPROVED;
    }

    /**
     * 申請を却下する（SUBMITTED -> REJECTED）
     * @throws IllegalStateException 現在の状態が SUBMITTED でない場合
     */
    public void reject() {
        if (this.status != RequestStatus.SUBMITTED) {
            throw new IllegalStateException(
                "申請の状態が不正です。SUBMITTED状態の申請のみ却下できます。現在の状態: " + this.status);
        }
        this.status = RequestStatus.REJECTED;
    }

    // --- getter ---

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public User getRequester() {
        return requester;
    }

    public Long getVersion() {
        return version;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    // --- equals / hashCode ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return id != null && Objects.equals(id, request.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
