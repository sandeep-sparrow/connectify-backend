package com.connectify.games.gameinvite;

import com.connectify.users.Users;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@Table(name = "game_invite")
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class GameInvite {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "inviter", nullable = false, unique = true)
    private Users inviter;

    @ManyToOne
    @JoinColumn(name = "invited", nullable = false, unique = true)
    private Users invited;

    @Enumerated(EnumType.STRING)
    private GameType game;

    @Enumerated(EnumType.STRING)
    private Status status;

    public GameInvite(Users inviter, Users invited, GameType game){
        this.inviter = inviter;
        this.invited = invited;
        this.game = game;
        this.status = Status.PENDING;
    }
}
