package com.connectify.games.chess;

import com.connectify.users.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ChessGameSessionRepository extends JpaRepository<ChessGameSession, Long> {
    Optional<ChessGameSession> findByBlackPlayerAndWhitePlayer(Users blackPlayer, Users whitePlayer);

    List<ChessGameSession> findByHeartbeatBefore(Date cutoff);
}

