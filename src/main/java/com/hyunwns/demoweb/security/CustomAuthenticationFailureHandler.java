package com.hyunwns.demoweb.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String errMessage;

        if(exception.getMessage().equalsIgnoreCase("Bad credentials")){
            errMessage = "Bad credentials";
        } else if(exception.getMessage().equalsIgnoreCase("Unauthorized")){
            errMessage = "Unauthorized";
        } else if(exception.getMessage().equalsIgnoreCase("Access denied")){
            errMessage = "Access denied";
        } else if(exception.getMessage().equalsIgnoreCase("Bad credentials")){}


    }
}
