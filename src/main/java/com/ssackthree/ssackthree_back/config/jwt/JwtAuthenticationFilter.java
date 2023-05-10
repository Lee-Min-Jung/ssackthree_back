package com.ssackthree.ssackthree_back.config.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        // Request Header 에서 JWT 토큰 추출
        String token = jwtProvider.resolveToken(request);
        log.info("+++++++++++++++++++++++++++++++++++");
        log.info(token);
        // validateToken 으로 토큰 유효성 검사
        if (token != null && jwtProvider.validateToken(token)) {
            // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext 에 저장
            log.info("2222222222222222222222");
            Authentication authentication = jwtProvider.getAuthentication(token);
            log.info("44444444444444444444");
            log.info(authentication.getAuthorities().toString());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("555555555555555555555");
            log.info(SecurityContextHolder.getContext().getAuthentication().toString());
        }
        chain.doFilter(request, response);
    }
}
