package com.example.approval_workflow_api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.approval_workflow_api.domain.Approval;
import com.example.approval_workflow_api.domain.ApprovalAction;
import com.example.approval_workflow_api.domain.HistoryEvent;
import com.example.approval_workflow_api.domain.Request;
import com.example.approval_workflow_api.domain.RequestHistory;
import com.example.approval_workflow_api.domain.RequestStatus;
import com.example.approval_workflow_api.domain.User;
import com.example.approval_workflow_api.domain.UserRole;
import com.example.approval_workflow_api.dto.RequestResponseDto;
import com.example.approval_workflow_api.exception.BusinessErrorCode;
import com.example.approval_workflow_api.exception.BusinessException;
import com.example.approval_workflow_api.repository.ApprovalRepository;
import com.example.approval_workflow_api.repository.RequestHistoryRepository;
import com.example.approval_workflow_api.repository.RequestRepository;
import com.example.approval_workflow_api.repository.UserRepository;

@Service
public class ApprovalWorkflowService {

    private final RequestRepository requestRepository;
    private final ApprovalRepository approvalRepository;
    private final RequestHistoryRepository historyRepository;
    private final UserRepository userRepository;

    public ApprovalWorkflowService(
        RequestRepository requestRepository,
        ApprovalRepository approvalRepository,
        RequestHistoryRepository historyRepository,
        UserRepository userRepository
    ) {
        this.requestRepository = requestRepository;
        this.approvalRepository = approvalRepository;
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public RequestResponseDto createRequest(String title, Long requesterId) {
        User requester = loadUser(requesterId);
        
        Request request = new Request(title, requester);
        Request savedRequest = requestRepository.save(request);
        return new RequestResponseDto(savedRequest.getId(), savedRequest.getStatus());
    }

    @Transactional
    public void submit(Long requestId, Long actorId) {
        // ユーザーを取得
        User actor = loadUser(actorId);
        
        // 申請を取得
        Request request = loadRequest(requestId);

        // actor == request.requesterId を強制
        if (!actor.getId().equals(request.getRequester().getId())) {
            throw new BusinessException(
                BusinessErrorCode.FORBIDDEN,
                "申請の提出は申請者のみが実行できます"
            );
        }

        // 状態がDRAFTであることを確認
        requireStatus(request, RequestStatus.DRAFT, "申請");

        // 状態をSUBMITTEDに変更
        request.setStatus(RequestStatus.SUBMITTED);

        // 履歴を記録
        RequestHistory history = new RequestHistory(
            request,
            actor,
            HistoryEvent.SUBMIT,
            "申請が提出されました"
        );
        historyRepository.save(history);
    }

    @Transactional
    public void approve(Long requestId, Long approverId, String comment) {
        // ユーザーを取得
        User approver = loadUser(approverId);
        
        // ロールチェック
        requireApproverRole(approver, "承認");
        
        // 申請を取得
        Request request = loadRequest(requestId);

        // 状態がSUBMITTEDであることを確認
        requireStatus(request, RequestStatus.SUBMITTED, "承認");

        // 状態をAPPROVEDに変更
        request.setStatus(RequestStatus.APPROVED);

        // 承認を記録
        Approval approval = new Approval(
            request,
            approver,
            ApprovalAction.APPROVE,
            comment
        );
        approvalRepository.save(approval);

        // 履歴を記録
        String msg = StringUtils.hasText(comment) ? comment : "申請が承認されました";
        RequestHistory history = new RequestHistory(
            request,
            approver,
            HistoryEvent.APPROVE,
            msg
        );
        historyRepository.save(history);
    }

    @Transactional
    public void reject(Long requestId, Long approverId, String comment) {
        // ユーザーを取得
        User approver = loadUser(approverId);

        // ロールチェック
        requireApproverRole(approver, "却下");
        
        // 申請を取得
        Request request = loadRequest(requestId);

        // 状態がSUBMITTEDであることを確認
        requireStatus(request, RequestStatus.SUBMITTED, "却下");

        // 状態をREJECTEDに変更
        request.setStatus(RequestStatus.REJECTED);

        // 却下を記録
        Approval approval = new Approval(
            request,
            approver,
            ApprovalAction.REJECT,
            comment
        );
        approvalRepository.save(approval);

        // 履歴を記録
        String msg = StringUtils.hasText(comment) ? comment : "申請が却下されました";
        RequestHistory history = new RequestHistory(
            request,
            approver,
            HistoryEvent.REJECT,
            msg
        );
        historyRepository.save(history);
    }

    @Transactional(readOnly = true)
    public java.util.List<RequestResponseDto> getAllRequests() {
        return requestRepository.findAll().stream()
        .map(r -> new RequestResponseDto(r.getId(),r.getStatus()))
        .toList();
    }

    @Transactional(readOnly = true)
    public RequestResponseDto getRequest(Long requestId) {
        Request request = loadRequest(requestId);
        return new RequestResponseDto(request.getId(), request.getStatus());
    }

    private User loadUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(BusinessErrorCode.NOT_FOUND, "ユーザーが見つかりません: " + userId));
    }

    private Request loadRequest(Long requestId) {
        return requestRepository.findById(requestId)
            .orElseThrow(() -> new BusinessException(BusinessErrorCode.NOT_FOUND, "申請が見つかりません: " + requestId));
    }

    private void requireStatus(Request req, RequestStatus expected, String actionName) {
        if (req.getStatus() != expected) {
            throw new BusinessException(
                BusinessErrorCode.INVALID_STATE,
                "申請の状態が不正です。" + expected + "状態の申請のみ" + actionName + "できます。現在の状態: " + req.getStatus()
            );
        }
    }

    private void requireApproverRole(User user, String actionName) {
        if (!UserRole.APPROVER.equals(user.getRole())) {
            throw new BusinessException(BusinessErrorCode.FORBIDDEN,
                actionName + "はAPPROVERロールのユーザーのみが実行できます");
        }
    }
}
