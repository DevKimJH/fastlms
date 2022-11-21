package com.zerobase.fastlms.configuration;

import com.zerobase.fastlms.admin.model.LoginHistoryParam;
import com.zerobase.fastlms.member.service.MemberService;
import com.zerobase.fastlms.util.RequestUtils;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;


@Component
@RequiredArgsConstructor
public class UserAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final MemberService memberService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {


        LoginHistoryParam loginHistoryParam = new LoginHistoryParam();

        loginHistoryParam.setUserAgent(RequestUtils.getUserAgent(request));
        loginHistoryParam.setClientIp(RequestUtils.getClientIP(request));
        loginHistoryParam.setUserId(authentication.getName());
        loginHistoryParam.setLoginTime(LocalDateTime.now());

        memberService.addLoginHistory(loginHistoryParam);


        super.onAuthenticationSuccess(request, response, authentication);
    }
}
