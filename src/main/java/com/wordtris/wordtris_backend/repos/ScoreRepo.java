package com.wordtris.wordtris_backend.repos;

import com.wordtris.wordtris_backend.models.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreRepo extends JpaRepository<Score,Integer> {
    @Query("SELECT c FROM Score c order by c.score desc")
    List<Score> getBoard();
}
