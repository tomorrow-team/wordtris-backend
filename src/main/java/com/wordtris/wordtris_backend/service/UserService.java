package com.wordtris.wordtris_backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wordtris.wordtris_backend.email.EmailSender;
import com.wordtris.wordtris_backend.models.*;
import com.wordtris.wordtris_backend.repos.GameHistoryRepo;
import com.wordtris.wordtris_backend.repos.GameUserRepos;
import com.wordtris.wordtris_backend.repos.ScoreRepo;
import com.wordtris.wordtris_backend.token.ConfirmationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.wordtris.wordtris_backend.models.Role.USER;
import static com.wordtris.wordtris_backend.models.Status.BANNED;


@org.springframework.stereotype.Service
public class UserService {
    private static final String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,40}$";
    private static final String emailRegex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    private static final String context = "https://faf1-176-59-23-91.eu.ngrok.io";
    private static  ObjectMapper mapper = new ObjectMapper();
    private final GameUserRepos gameUserRepos;
    private final ScoreRepo scoreRepo;
    private final GameHistoryRepo gameHistoryRepo;
    private final PasswordEncoder passwordEncoder;
    private final IfExists userNotExists;
    private final IfExists nickNotExists;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSender emailSender;

    @Autowired
    public UserService(GameUserRepos gameUserRepos, ScoreRepo scoreRepo,
                       PasswordEncoder passwordEncoder, ConfirmationTokenService service,
                       EmailSender emailSender, GameHistoryRepo gameHistoryRepo) {
        this.gameUserRepos = gameUserRepos;
        this.scoreRepo = scoreRepo;
        this.passwordEncoder = passwordEncoder;
        this.confirmationTokenService = service;
        this.emailSender = emailSender;
        mapper.registerModule(new JavaTimeModule());
        this.gameHistoryRepo = gameHistoryRepo;
        this.userNotExists = x->{
            Optional<GameUser> gameUserOptional = gameUserRepos.findGameUserByUsername(x);
            if(gameUserOptional.isEmpty()){
                return true;
            }
            else{
                throw new IllegalStateException("Such username is taken");
            }
        };
        this.nickNotExists = x->{
            Optional<GameUser> gameUserOptional = gameUserRepos.findByNickname(x);
            if(gameUserOptional.isEmpty()){
                return true;
            }
            else{
                throw new IllegalStateException("Such nick is taken");
            }
        };
    }

    public void getLeaderBoard(HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        GameUser gameUser = getByUserName(currentPrincipalName);
        List<Score> list = scoreRepo.getBoard();
        final int[] i = {1};
        List<LeaderBoard> leaderBoardMessages = list.stream()
                .map(x -> new LeaderBoard(i[0]++, x.getLogin(), x.getScore(), x.getGameUser().getStatus_id(), x.getGameUser().getAvatar_id())).toList();
        Optional<LeaderBoard> user = leaderBoardMessages.stream()
                        .filter(x->x.getLogin().equals(gameUser.getScore().getLogin()))
                                .findFirst();
        LeaderBoard finalUser = user.orElseGet(LeaderBoard::new);
        LeaderBoardResponse leaderBoardResponse = new LeaderBoardResponse(finalUser,leaderBoardMessages.stream().limit(5).collect(Collectors.toList()));
        response.setContentType("application/json;charset=utf-8");
        try {
            response.getOutputStream().println(mapper.writerWithDefaultPrettyPrinter().
                    writeValueAsString(leaderBoardResponse));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GameUser getByUserName(String name) {
        Optional<GameUser> gameUserOptional = gameUserRepos.findGameUserByUsername(name);
        if(gameUserOptional.isPresent()){
            GameUser gameUser = gameUserOptional.orElseGet(GameUser::new);
            List<GameHistory> gameHistories = gameUser.getGameHistory();
            Collections.reverse(gameHistories);
            gameUser.setGameHistory(gameHistories);
            return gameUser;
        }
        else{
            throw new IllegalStateException("There is no such user");
        }
    }
    private boolean ifPassMatched(String pass,String repeat_pass){
        if(pass.equals(repeat_pass)){
            return true;
        }
        else{
            throw new IllegalStateException("Passwords don't match");
        }
    }

    public void registerUser(HttpServletRequest request) {
        String nickname, username, password,repeat_pass;
        try {
            Map<String, String> requestMap = mapper.readValue(request.getInputStream(), new TypeReference<>() {
            });
            username = requestMap.get("username");
            password = requestMap.get("password");
            nickname = requestMap.get("login");
            repeat_pass = requestMap.get("repeat_pass");
        } catch (IOException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
        if(!username.matches(emailRegex)){
            System.out.println(username);
            throw new IllegalStateException("Email not valid");
        }
        else if(!password.matches(passwordRegex)){
            throw new IllegalStateException("Password not valid");
        }
        else if(nickname == null || repeat_pass == null){
            throw new IllegalStateException("Пустые поля");
        }
        if(userNotExists.ifExists(username) && nickNotExists.ifExists(nickname) && ifPassMatched(password,repeat_pass)){
            GameUser gameUser = new GameUser(username,passwordEncoder.encode(password),USER,BANNED);
            Score score = new Score(nickname,0L,gameUser);
            scoreRepo.save(score);
            gameUserRepos.save(gameUser);
            String token = confirmationTokenService.createToken(gameUser);
            String link = context + "/api/token?token=" + token;
            emailSender.send(username, confirmationTokenService.buildEmail(username, link));
        }
    }
    public void changePassword(HttpServletRequest request) {
        String username, new_pass,repeat_pass;
        try {
            Map<String, String> requestMap = mapper.readValue(request.getInputStream(), new TypeReference<>() {
            });
            username = requestMap.get("username");
            new_pass = requestMap.get("new_pass");
            repeat_pass = requestMap.get("repeat_pass");
        } catch (IOException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
        if(!username.matches(emailRegex)){
            throw new IllegalStateException("Email not valid");
        }
        else if(!new_pass.matches(passwordRegex)){
            throw new IllegalStateException("Password not valid");
        }
        else if(new_pass.equals("") || repeat_pass.equals("")){
            throw new IllegalStateException("Empty passwords");
        }
        if(ifPassMatched(new_pass,repeat_pass)){
            GameUser userDb = getByUserName(username);
            userDb.setPassword(passwordEncoder.encode(new_pass));
            String token = confirmationTokenService.createToken(userDb);
            String link = context + "/api/token?token=" + token;
            gameUserRepos.save(userDb);
            emailSender.send(username, confirmationTokenService.buildEmail(username, link));
        }
    }

    @Transactional
    public void setAvatar(HttpServletRequest request,String user) {
        int avatar = 0;
        System.out.println(user);
        try {
            Map<String, String> requestMap = mapper.readValue(request.getInputStream(), new TypeReference<>() {
            });
            avatar = Integer.parseInt(requestMap.get("avatar"));
        } catch (IOException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
        gameUserRepos.updateAvatar(user,avatar);
    }
    @Transactional
    public void setStatus(HttpServletRequest request) {
        int status = 0;
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(user);
        try {
            Map<String, String> requestMap = mapper.readValue(request.getInputStream(), new TypeReference<>() {
            });
            status = Integer.parseInt(requestMap.get("status"));
        } catch (IOException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
        gameUserRepos.updateStatus(user,status);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated()){
            Cookie cookie = new Cookie("JSESSIONID", null);
            cookie.setMaxAge(0);
            cookie.setSecure(true);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        else{
            throw new IllegalStateException("Can't logout, you are not authenticated");
        }
    }

    public void getGameHistory(HttpServletResponse response, String currentPrincipalName) {
        List<GameHistory> list = gameHistoryRepo.getGameHistoryBy(currentPrincipalName);
        List<GameHistoryResponse> leaderBoardMessages = list.stream()
                .map(x-> new GameHistoryResponse(Timestamp.valueOf(x.getData()),x.getScore(), x.getUser().getStatus_id(), x.getUser().getAvatar_id()))
                .collect(Collectors.toList());
        response.setContentType("application/json;charset=utf-8");
        try {
            response.getOutputStream().println(mapper.writerWithDefaultPrettyPrinter().
                        writeValueAsString(leaderBoardMessages));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void set_score(HttpServletRequest request, HttpServletResponse response) {
        int score = 0;
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            Map<String, String> requestMap = mapper.readValue(request.getInputStream(), new TypeReference<>() {
            });
            score = Integer.parseInt(requestMap.get("score"));
        } catch (IOException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
        GameUser gameUser = getByUserName(user);
        gameHistoryRepo.save(new GameHistory(LocalDateTime.now(),score,gameUser));
    }

    public void editProfile(HttpServletRequest request) {
        String login , new_pass ,repeat_pass ,old_pass;
        try {
            Map<String, String> requestMap = mapper.readValue(request.getInputStream(), new TypeReference<>() {
            });
            login = requestMap.get("login");
            old_pass = requestMap.get("old_pass");
            new_pass = requestMap.get("new_pass");
            repeat_pass = requestMap.get("repeat_pass");
        } catch (IOException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
        if(login.equals("")){
            throw new IllegalStateException("Empty login");
        }
        else if(old_pass.equals("")){
            throw new IllegalStateException("Empty old password");
        }
        else if(new_pass.equals("")){
            throw new IllegalStateException("Empty new password");
        }
        else if(repeat_pass.equals("")){
            throw new IllegalStateException("Empty repeat password");
        }
        else{
            String user = SecurityContextHolder.getContext().getAuthentication().getName();
            GameUser gameUser = getByUserName(user);
            if(!passwordEncoder.matches(old_pass,gameUser.getPassword())){
                System.out.println(passwordEncoder.matches(gameUser.getPassword(),old_pass));
                throw new IllegalStateException("Old password wrong");
            }
            else if(!new_pass.matches(passwordRegex)){
                throw new IllegalStateException("Password not valid");
            }
            else if(ifPassMatched(new_pass,repeat_pass) && nickNotExists.ifExists(login)){
                gameUser.setPassword(passwordEncoder.encode(new_pass));
                gameUser.getScore().setLogin(login);
                gameUserRepos.save(gameUser);
            }
        }
    }
}
