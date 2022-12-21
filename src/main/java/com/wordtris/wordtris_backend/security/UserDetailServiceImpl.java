package com.wordtris.wordtris_backend.security;


import com.wordtris.wordtris_backend.models.GameUser;
import com.wordtris.wordtris_backend.models.Status;
import com.wordtris.wordtris_backend.repos.GameUserRepos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service("userDetailsServiceImpl")
public class UserDetailServiceImpl  implements UserDetailsService {
    private GameUser user;
    private final GameUserRepos userDbRepository;

    @Autowired
    public UserDetailServiceImpl(GameUserRepos userDbRepository) {
        this.userDbRepository = userDbRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<GameUser> userDbOptional = userDbRepository.findGameUserByUsername(username);
        if(userDbRepository.findGameUserByUsername(username).isEmpty()){
            throw new IllegalStateException("There is no such user.Try again");
        }
        else{
            GameUser user = userDbOptional.orElse(new GameUser());
            if(user.getStatus() == Status.BANNED) {
                System.out.println("hhh");
                throw new IllegalStateException("You are not confirmed");

            }
            else{
                return SecurityUser.fromUser(user);
            }
        }
    }

}
