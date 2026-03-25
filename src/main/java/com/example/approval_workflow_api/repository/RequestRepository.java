package com.example.approval_workflow_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.approval_workflow_api.domain.Request;

public interface RequestRepository extends JpaRepository<Request, Long> {
}
