package com.connectify.users.token;

import com.connectify.users.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {

    Optional<ConfirmationToken> findByToken(String token);
    void deleteByToken(String token);
    List<ConfirmationToken> findAllByUsers(Users users);
    List<ConfirmationToken> findAllByExpiresAtBefore(LocalDateTime dateTime);
}
