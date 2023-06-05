package com.connectify.users.token;

import com.connectify.users.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ValidateTokenRepository extends JpaRepository<ValidateToken, Long> {
    Optional<ValidateToken> findByToken(String token);
    void deleteByToken(String token);
    List<ValidateToken> findAllByUsers(Users users);
    List<ValidateToken> findAllByExpiresAtBefore(Date expiresAt);
}
