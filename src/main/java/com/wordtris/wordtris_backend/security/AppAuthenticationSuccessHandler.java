package com.wordtris.wordtris_backend.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordtris.wordtris_backend.error.ErrorMsg;
import com.wordtris.wordtris_backend.error.Handler;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class AppAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler
implements Handler {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    protected void handle(HttpServletRequest request, HttpServletResponse response,
                          Authentication authentication) throws IOException, ServletException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json;charset=utf-8");
        ErrorMsg errorMsg = new ErrorMsg(true);
        response.getOutputStream()
                .println( objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorMsg));
    }

    @Override
    public void handle_response(HttpServletResponse response, Exception exception) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json;charset=utf-8");
        ErrorMsg errorMsg = new ErrorMsg(true);
        response.getOutputStream()
                .println( objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorMsg));
    }
    public void handle_token(HttpServletResponse response, Exception exception) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("text/html;charset=utf-8");
        ErrorMsg errorMsg = new ErrorMsg(true);
        response.getOutputStream()
                .println("Email confirmed. Now you can login");
    }
}


