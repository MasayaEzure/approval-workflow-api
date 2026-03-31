package com.example.approval_workflow_api.resolver;

import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.core.MethodParameter;

import com.example.approval_workflow_api.annotation.CurrentUser;
import jakarta.servlet.http.HttpServletRequest;
import com.example.approval_workflow_api.repository.UserRepository;
import com.example.approval_workflow_api.exception.BusinessException;
import com.example.approval_workflow_api.exception.BusinessErrorCode;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserResolver implements HandlerMethodArgumentResolver {

    private final UserRepository userRepository;

    public CurrentUserResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String userId = request.getHeader("X-User-Id");
        if (userId == null) {
            throw new BusinessException(BusinessErrorCode.NOT_FOUND, "ユーザーが見つかりません: " + userId);
        }
        return userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.NOT_FOUND, "ユーザーが見つかりません: " + userId));
    }
}