package com.example.approval_workflow_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.approval_workflow_api.dto.RequestApproveDto;
import com.example.approval_workflow_api.dto.RequestCreateDto;
import com.example.approval_workflow_api.dto.RequestRejectDto;
import com.example.approval_workflow_api.dto.RequestResponseDto;
import com.example.approval_workflow_api.dto.RequestSubmitDto;
import com.example.approval_workflow_api.service.ApprovalWorkflowService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@Validated
public class ApprovalWorkflowController {

    private final ApprovalWorkflowService approvalWorkflowService;

    public ApprovalWorkflowController(ApprovalWorkflowService approvalWorkflowService) {
        this.approvalWorkflowService = approvalWorkflowService;
    }

    @PostMapping("/requests")
    public ResponseEntity<RequestResponseDto> createRequest(@Valid @RequestBody RequestCreateDto dto) {
        RequestResponseDto response = approvalWorkflowService.createRequest(dto.getTitle(), dto.getRequesterId());
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .location(ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri())
            .body(response);
    }

    @PostMapping("/requests/{id}/submit")
    public ResponseEntity<Void> submitRequest(@PathVariable Long id, @Valid @RequestBody RequestSubmitDto dto) {
        approvalWorkflowService.submit(id, dto.getActorId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/requests/{id}/approve")
    public ResponseEntity<Void> approveRequest(@PathVariable Long id, @Valid @RequestBody RequestApproveDto dto) {
        approvalWorkflowService.approve(id, dto.getApproverId(), dto.getComment());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/requests/{id}/reject")
    public ResponseEntity<Void> rejectRequest(@PathVariable Long id, @Valid @RequestBody RequestRejectDto dto) {
        approvalWorkflowService.reject(id, dto.getApproverId(), dto.getComment());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/requests")
    public ResponseEntity<List<RequestResponseDto>> getRequests() {
        List<RequestResponseDto> response = approvalWorkflowService.getAllRequests();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/requests/{id}")
    public ResponseEntity<RequestResponseDto> getRequest(@PathVariable Long id) {
        return ResponseEntity.ok(approvalWorkflowService.getRequest(id));
    }
}