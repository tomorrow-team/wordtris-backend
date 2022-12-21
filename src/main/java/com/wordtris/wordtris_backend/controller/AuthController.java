package com.wordtris.wordtris_backend.controller;


import com.wordtris.wordtris_backend.models.GameUser;
import com.wordtris.wordtris_backend.security.AppAuthenticationSuccessHandler;
import com.wordtris.wordtris_backend.security.CustomAuthenticationFailureHandler;
import com.wordtris.wordtris_backend.service.UserService;
import com.wordtris.wordtris_backend.token.ConfirmationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController

@RequestMapping("/api")
public class AuthController {
    private final UserService service;
    private final CustomAuthenticationFailureHandler errorHandler;
    private final AppAuthenticationSuccessHandler successHandler;
    private final ConfirmationTokenService confirmToken;

    @Autowired
    public AuthController(UserService service, CustomAuthenticationFailureHandler errorHandler,
                          AppAuthenticationSuccessHandler successHandler, ConfirmationTokenService confirmToken) {
        this.service = service;
        this.errorHandler = errorHandler;
        this.successHandler = successHandler;
        this.confirmToken = confirmToken;
    }
    @PostMapping("/register")
    public void registerUser(HttpServletRequest request,HttpServletResponse response) throws IOException {
        service.registerUser(request);
        successHandler.handle_response(response,null);
    }
    @GetMapping("/leaderboard")
    public void getLeaderboard(HttpServletResponse response){
        service.getLeaderBoard(response);
    }

    @PreAuthorize("hasAuthority('get_auth_user')")
    @GetMapping("/game_history")
    public void getGameHistory(HttpServletResponse response){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        service.getGameHistory(response,currentPrincipalName);
    }

    @PreAuthorize("hasAuthority('get_auth_user')")
    @PutMapping("/avatar")
    public void setAvatar(HttpServletRequest request,HttpServletResponse response) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        service.setAvatar(request,currentPrincipalName);
        successHandler.handle_response(response,null);
    }
    @PreAuthorize("hasAuthority('get_auth_user')")
    @PutMapping("/status")
    public void setStatus(HttpServletRequest request,HttpServletResponse response) throws IOException {
        service.setStatus(request);
        successHandler.handle_response(response,null);
    }
    @PreAuthorize("hasAuthority('get_auth_user')")
    @GetMapping("/user")
    public GameUser getUser(HttpServletResponse response) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        return service.getByUserName(currentPrincipalName);
    }

    @GetMapping("/token")
    public void confirm(@RequestParam("token") String token,HttpServletResponse response) throws IOException {
        confirmToken.confirmToken(token);
        successHandler.handle_token(response,null);
    }


    @PreAuthorize("hasAuthority('get_auth_user')")
    @PutMapping("/change")
    public void editProfile(HttpServletRequest request,HttpServletResponse response) throws IOException {
        service.editProfile(request);
        successHandler.handle_response(response,null);
    }
    @GetMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        service.logout(request,response);
        successHandler.handle_response(response,null);
    }

    @PostMapping("/score")
    public void score(HttpServletRequest request, HttpServletResponse response) throws IOException {
        service.set_score(request,response);
        successHandler.handle_response(response,null);
    }
    @PostMapping("/forget")
    public void forgetPass(HttpServletRequest request,HttpServletResponse response) throws IOException {
        service.changePassword(request);
        successHandler.handle_response(response,null);
    }

    @ExceptionHandler({ IllegalStateException.class, AccessDeniedException.class})
    public void handleException(HttpServletResponse response, Exception exception) throws IOException {
        errorHandler.handle_response(response,exception);
    }
}
