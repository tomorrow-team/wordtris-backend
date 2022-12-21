package com.wordtris.wordtris_backend.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordtris.wordtris_backend.error.Error;
import com.wordtris.wordtris_backend.error.ErrorMsg;
import com.wordtris.wordtris_backend.error.Handler;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler, Handler {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception)
            throws IOException, ServletException {
        String message = exception.getMessage();
        response.setContentType("application/json;charset=utf-8");
        response.setHeader("Access-Control-Allow-Credentials","true");
        switch (message) {
            case "There is no such user.Try again" -> {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getOutputStream().println(objectMapper.writerWithDefaultPrettyPrinter().
                        writeValueAsString(
                                new ErrorMsg(false,
                                        new Error("No such username registered", "email"))));
            }
            case "Bad credentials" -> {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getOutputStream().println(objectMapper.writerWithDefaultPrettyPrinter().
                        writeValueAsString(
                                new ErrorMsg(false,
                                        new Error("Bad credentials", "password"))));
            }
            case "Email not valid" -> {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.getOutputStream().println(objectMapper.writerWithDefaultPrettyPrinter().
                        writeValueAsString(
                                new ErrorMsg(false,
                                        new Error("Email not valid", "email"))));
            }
            case "Password not valid" -> {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.getOutputStream().println(objectMapper.writerWithDefaultPrettyPrinter().
                        writeValueAsString(
                                new ErrorMsg(false,
                                        new Error("Password not valid", "password"))));
            }
            case "You are not confirmed" -> {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.getOutputStream().println(objectMapper.writerWithDefaultPrettyPrinter().
                        writeValueAsString(
                                new ErrorMsg(false,
                                        new Error("Email not confirmed", "email"))));
            }
        }
    }

    @Override
    public void handle_response(HttpServletResponse response, Exception exception) throws IOException {
        String message = exception.getMessage();
        response.setContentType("application/json;charset=utf-8");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        switch (message) {
            case "Such username is taken" -> {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.getOutputStream().println(objectMapper.writerWithDefaultPrettyPrinter().
                        writeValueAsString(
                                new ErrorMsg(false,
                                        new Error("Username is taken", "email"))));
            }
            case "Such nick is taken" -> {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.getOutputStream().println(objectMapper.writerWithDefaultPrettyPrinter().
                        writeValueAsString(
                                new ErrorMsg(false,
                                        new Error("Login is taken", "nickname"))));
            }
            case "Passwords don't match" -> {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.getOutputStream().println(objectMapper.writerWithDefaultPrettyPrinter().
                        writeValueAsString(
                                new ErrorMsg(false,
                                        new Error("Pass not match", "repeat password"))));
            }
            case "Доступ запрещен" -> {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.getOutputStream().println(objectMapper.writerWithDefaultPrettyPrinter().
                        writeValueAsString(
                                new ErrorMsg(false,
                                        new Error("Forbidden", "no rights"))));

            }
            case "There is no such user" -> {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.getOutputStream().println(objectMapper.writerWithDefaultPrettyPrinter().
                        writeValueAsString(
                                new ErrorMsg(false,
                                        new Error("No such user", "no rights"))));

            }
            case "token not found"  -> {
                response.setContentType("text/html;charset=utf-8");
                response.setStatus(HttpStatus.NOT_FOUND.value());
                response.getOutputStream().println("Token not found");

            }
            case "email already confirmed" -> {
                response.setContentType("text/html;charset=utf-8");
                response.setStatus(HttpStatus.NOT_FOUND.value());
                response.getOutputStream().println("Email already confirmed");

            }
            case "token expired" -> {
                response.setContentType("text/html;charset=utf-8");
                response.setStatus(HttpStatus.NOT_FOUND.value());
                response.getOutputStream().println("Token expired");
            }
            case "Can't logout, you are not authenticated" -> {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.getOutputStream().println(objectMapper.writerWithDefaultPrettyPrinter().
                        writeValueAsString(
                                new ErrorMsg(false,
                                        new Error("Not authenticated", "not authenticated"))));
            }
            case "Email not valid" -> {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.getOutputStream().println(objectMapper.writerWithDefaultPrettyPrinter().
                        writeValueAsString(
                                new ErrorMsg(false,
                                        new Error("Email not valid", "email"))));
            }
            case "Password not valid" -> {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.getOutputStream().println(objectMapper.writerWithDefaultPrettyPrinter().
                        writeValueAsString(
                                new ErrorMsg(false,
                                        new Error("Password not valid", "password"))));
            }

            case "Empty passwords" -> {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.getOutputStream().println(objectMapper.writerWithDefaultPrettyPrinter().
                        writeValueAsString(
                                new ErrorMsg(false,
                                        new Error("Empty lines", "password"))));
            }
            case "Old password wrong" -> {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.getOutputStream().println(objectMapper.writerWithDefaultPrettyPrinter().
                        writeValueAsString(
                                new ErrorMsg(false,
                                        new Error("Old password wrong", "password"))));
            }

            case "Empty login" -> {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.getOutputStream().println(objectMapper.writerWithDefaultPrettyPrinter().
                        writeValueAsString(
                                new ErrorMsg(false,
                                        new Error("Login is empty", "login"))));
            }
            case "Empty new password" -> {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.getOutputStream().println(objectMapper.writerWithDefaultPrettyPrinter().
                        writeValueAsString(
                                new ErrorMsg(false,
                                        new Error("New password is empty", "new_pass"))));
            }
            case "Empty repeat password" -> {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.getOutputStream().println(objectMapper.writerWithDefaultPrettyPrinter().
                        writeValueAsString(
                                new ErrorMsg(false,
                                        new Error("Repeat password is empty", "repeat_pass"))));
            }
            case "Empty old password" -> {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.getOutputStream().println(objectMapper.writerWithDefaultPrettyPrinter().
                        writeValueAsString(
                                new ErrorMsg(false,
                                        new Error("Old password is empty", "old_pass"))));
            }

        }
    }
}