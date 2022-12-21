package com.wordtris.wordtris_backend.repos;

import com.wordtris.wordtris_backend.models.GameUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface GameUserRepos extends JpaRepository<GameUser, Integer> {

    Optional<GameUser> findGameUserByUsername(String username);

    @Query("SELECT c FROM GameUser c WHERE c.score.login= ?1")
    Optional<GameUser> findByNickname(String nickname);

    @Transactional
    @Modifying
    @Query("UPDATE GameUser c " +
            "SET c.avatar_id = ?2 " +
            "WHERE c.username = ?1")
    void updateAvatar(String username,Integer avatar_id);

    @Transactional
    @Modifying
    @Query("UPDATE GameUser c " +
            "SET c.status_id = ?2 " +
            "WHERE c.username = ?1")
    void updateStatus(String username,Integer status_id);


}
