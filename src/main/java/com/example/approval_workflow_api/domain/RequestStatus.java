package com.example.approval_workflow_api.domain;

public enum RequestStatus {
    DRAFT,     // 申請の下書き作成
    SUBMITTED, // 申請
    APPROVED,  // 承認
    REJECTED   // 却下/差戻し
}