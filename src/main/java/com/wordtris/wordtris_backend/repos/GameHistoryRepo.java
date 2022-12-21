package com.wordtris.wordtris_backend.repos;


import com.wordtris.wordtris_backend.models.GameHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameHistoryRepo extends JpaRepository<GameHistory,Integer> {
    @Query("SELECT c FROM GameHistory c WHERE c.user.username = ?1 ORDER BY c.data DESC ")
    List<GameHistory> getGameHistoryBy(String username);
}
