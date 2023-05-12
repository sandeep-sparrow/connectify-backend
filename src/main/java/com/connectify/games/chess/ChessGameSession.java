package com.connectify.games.chess;

import com.connectify.users.Users;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chess_game_session")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class ChessGameSession {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "white_player", nullable = false)
    private Users whitePlayer;

    @ManyToOne
    @JoinColumn(name = "black_player", nullable = false)
    private Users blackPlayer;

    @Enumerated(EnumType.STRING)
    private Turn turn;

    @Enumerated(EnumType.STRING)
    private GameStatus gameStatus;

    @Embedded
    private Move recentMove;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public ChessGameSession(Users whitePlayer, Users blackPlayer){
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        gameStatus = GameStatus.IN_PROGRESS;
        recentMove = new Move();
        turn = Turn.WHITE;
    }

    public void updateMove(Move recentMove){
        this.recentMove = recentMove;
    }
}
