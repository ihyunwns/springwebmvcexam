package com.hyunwns.demoweb.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.io.PrintWriter;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String username = request.getParameter("username");
        String message = getExceptionMessage(exception);

        request.setAttribute("username", username);
        request.setAttribute("errorMsg", message);

        request.getRequestDispatcher("/").forward(request, response);

    }

    private String getExceptionMessage(AuthenticationException exception) {

        // AbstractUserDetailsAuthenticationProvider 에 hideUserNotFoundExceptions 속성으로 UsernameNotFoundException이 BadCredentailException으로 오게됨
        // 따라서 통합해서 메시지를 수정하도록 하였음

        if (exception instanceof BadCredentialsException) {
            return "BE";
        } else if (exception instanceof UsernameNotFoundException) {
            return "UNFE";
        }


//        else if (exception instanceof AccountExpiredException) {
//            return "계정이 만료되었습니다.";
//        } else if (exception instanceof CredentialsExpiredException) {
//            return "비밀번호가 만료되었습니다.";
//        } else if (exception instanceof DisabledException) {
//            return "계정이 비활성화 되었습니다.";
//        } else if (exception instanceof LockedException) {
//            return "계정이 잠겼습니다.";
//        }
        // DB에 구현되어 있지 않은 부분, 따로 해당 항목에 대해서 체크할 수 있게 테이블 생성 필요

        else {
            return "예상치 못한 에러가 발생했습니다. 다시 시도해주세요.";
        }
    }
}
