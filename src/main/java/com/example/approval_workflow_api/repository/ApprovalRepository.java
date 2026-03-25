package com.example.approval_workflow_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.approval_workflow_api.domain.Approval;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {
}
