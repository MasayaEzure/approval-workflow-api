package com.example.approval_workflow_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.approval_workflow_api.domain.RequestHistory;

public interface RequestHistoryRepository extends JpaRepository<RequestHistory, Long> {

    List<RequestHistory> findByRequestIdOrderByCreatedAtAsc(Long requestId);
}
