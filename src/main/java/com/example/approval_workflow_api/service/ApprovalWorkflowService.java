package com.example.approval_workflow_api.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.approval_workflow_api.domain.Approval;
import com.example.approval_workflow_api.domain.ApprovalAction;
import com.example.approval_workflow_api.domain.HistoryEvent;
import com.example.approval_workflow_api.domain.Request;
import com.example.approval_workflow_api.domain.RequestHistory;
import com.example.approval_workflow_api.domain.User;
import com.example.approval_workflow_api.domain.UserRole;
import com.example.approval_workflow_api.dto.RequestHistoryDto;
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

        // B-03: 申請作成はREQUESTERロールのみ
        if (!UserRole.REQUESTER.equals(requester.getRole())) {
            throw new BusinessException(
                BusinessErrorCode.FORBIDDEN,
                "申請の作成はREQUESTERロールのユーザーのみが実行できます"
            );
        }

        Request request = new Request(title, requester);
        Request savedRequest = requestRepository.save(request);
        return RequestResponseDto.from(savedRequest);
    }

    @Transactional
    public RequestResponseDto submit(Long requestId, Long actorId) {
        User actor = loadUser(actorId);
        Request request = loadRequest(requestId);

        // 申請者本人のみ提出可能
        if (!actor.getId().equals(request.getRequester().getId())) {
            throw new BusinessException(
                BusinessErrorCode.FORBIDDEN,
                "申請の提出は申請者のみが実行できます"
            );
        }

        // ドメインメソッドで状態遷移（B-05: 状態チェックはエンティティ内で実施）
        try {
            request.submit();
        } catch (IllegalStateException e) {
            throw new BusinessException(BusinessErrorCode.INVALID_STATE, e.getMessage());
        }

        // 履歴を記録
        RequestHistory history = new RequestHistory(
            request, actor, HistoryEvent.SUBMIT, "申請が提出されました"
        );
        historyRepository.save(history);

        return RequestResponseDto.from(request);
    }

    @Transactional
    public RequestResponseDto approve(Long requestId, Long approverId, String comment) {
        User approver = loadUser(approverId);
        requireApproverRole(approver, "承認");

        Request request = loadRequest(requestId);

        // B-01: 自己承認の防止
        if (approver.getId().equals(request.getRequester().getId())) {
            throw new BusinessException(
                BusinessErrorCode.FORBIDDEN,
                "自身の申請を承認することはできません"
            );
        }

        // ドメインメソッドで状態遷移
        try {
            request.approve();
        } catch (IllegalStateException e) {
            throw new BusinessException(BusinessErrorCode.INVALID_STATE, e.getMessage());
        }

        // 承認を記録
        Approval approval = new Approval(request, approver, ApprovalAction.APPROVE, comment);
        approvalRepository.save(approval);

        // 履歴を記録
        String msg = StringUtils.hasText(comment) ? comment : "申請が承認されました";
        RequestHistory history = new RequestHistory(request, approver, HistoryEvent.APPROVE, msg);
        historyRepository.save(history);

        return RequestResponseDto.from(request);
    }

    @Transactional
    public RequestResponseDto reject(Long requestId, Long approverId, String comment) {
        User approver = loadUser(approverId);
        requireApproverRole(approver, "却下");

        Request request = loadRequest(requestId);

        // B-01: 自己却下の防止
        if (approver.getId().equals(request.getRequester().getId())) {
            throw new BusinessException(
                BusinessErrorCode.FORBIDDEN,
                "自身の申請を却下することはできません"
            );
        }

        // ドメインメソッドで状態遷移
        try {
            request.reject();
        } catch (IllegalStateException e) {
            throw new BusinessException(BusinessErrorCode.INVALID_STATE, e.getMessage());
        }

        // 却下を記録
        Approval approval = new Approval(request, approver, ApprovalAction.REJECT, comment);
        approvalRepository.save(approval);

        // 履歴を記録
        String msg = StringUtils.hasText(comment) ? comment : "申請が却下されました";
        RequestHistory history = new RequestHistory(request, approver, HistoryEvent.REJECT, msg);
        historyRepository.save(history);

        return RequestResponseDto.from(request);
    }

    // J-01: ページネーション対応
    @Transactional(readOnly = true)
    public Page<RequestResponseDto> getAllRequests(Pageable pageable) {
        return requestRepository.findAll(pageable)
            .map(RequestResponseDto::from);
    }

    @Transactional(readOnly = true)
    public RequestResponseDto getRequest(Long requestId) {
        Request request = loadRequest(requestId);
        return RequestResponseDto.from(request);
    }

    // R-02: 履歴取得
    @Transactional(readOnly = true)
    public List<RequestHistoryDto> getRequestHistories(Long requestId) {
        // 申請の存在確認
        loadRequest(requestId);
        return historyRepository.findByRequestIdOrderByCreatedAtAsc(requestId).stream()
            .map(RequestHistoryDto::from)
            .toList();
    }

    private User loadUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(
                BusinessErrorCode.NOT_FOUND, "ユーザーが見つかりません: " + userId));
    }

    private Request loadRequest(Long requestId) {
        return requestRepository.findById(requestId)
            .orElseThrow(() -> new BusinessException(
                BusinessErrorCode.NOT_FOUND, "申請が見つかりません: " + requestId));
    }

    private void requireApproverRole(User user, String actionName) {
        if (!UserRole.APPROVER.equals(user.getRole())) {
            throw new BusinessException(
                BusinessErrorCode.FORBIDDEN,
                actionName + "はAPPROVERロールのユーザーのみが実行できます");
        }
    }
}
