package com.wordtris.wordtris_backend.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.wordtris.wordtris_backend.token.ConfirmationToken;
import com.wordtris.wordtris_backend.models.GameUser;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface ConfirmationTokenRepository
        extends JpaRepository<ConfirmationToken, Long> {

    Optional<ConfirmationToken> findByToken(String token);

    @Transactional
    @Modifying
    @Query("UPDATE ConfirmationToken c " +
            "SET c.confirmedAt = ?2 " +
            "WHERE c.token = ?1")
    void updateConfirmedAt(String token,
                           LocalDateTime confirmedAt);

    @Query("SELECT c FROM ConfirmationToken c WHERE c.user = ?1 and c.confirmedAt is null")
    List<ConfirmationToken> findNotConfirmed(GameUser userDb);
}
