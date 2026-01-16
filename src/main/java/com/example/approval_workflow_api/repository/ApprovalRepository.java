package com.example.approval_workflow_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.approval_workflow_api.domain.Approval;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, Long> {
}

