package com.zerobase.fastlms.configuration;


import com.zerobase.fastlms.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final MemberService memberService;
    private final UserAuthenticationSuccessHandler userAuthenticationSuccessHandler;

    @Bean
    UserAuthenticationFailureHandler getFailureHandler(){
        return new UserAuthenticationFailureHandler();
    }

    @Bean
    PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {

        web.ignoring().antMatchers("/favicon.ico", "/files/**");

        super.configure(web);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();
        http.headers().frameOptions().sameOrigin();

        // index, 회원가입, email인증 화면은 로그인 상태 없이 사용할 수 있어야 함
        // 비밀번호 찾기, 비밀번호 초기화 화면 추가
        http.authorizeRequests()
                .antMatchers("/"
                        , "/member/register"
                        , "/member/email-auth"
                        , "/member/find/password"
                        , "/member/reset/password"
                )
                .permitAll();

        // admin/** 페이지는 ROLE_ADMIN 권한이 있어야 접근 가능하다
        http.authorizeRequests()
                        .antMatchers("/admin/**")
                                .hasAuthority("ROLE_ADMIN");



        // 로그인 페이지 설정
        http.formLogin()
                .loginPage("/member/login")
                .failureHandler(getFailureHandler()) // 로그인 실패시 후처리하는 Handler 설정하는 곳
                .successHandler(userAuthenticationSuccessHandler)
                .permitAll(); // login페이지도 로그인 상태 없이 사용할 수 있어야 함(permitAll)


        http.logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/member/logout")) // 로그아웃 페이지
                .logoutSuccessUrl("/") // index.html 로 이동
                .invalidateHttpSession(true); // 세션 모두 초기화


        // 접근권한없는 페이지를 user가 접근했을 때 /error/denied page로 이동하겠다.
        http.exceptionHandling()
                .accessDeniedPage("/error/denied");

        super.configure(http);
    }



    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.userDetailsService(memberService)
                .passwordEncoder(getPasswordEncoder());

        super.configure(auth);
    }
}
