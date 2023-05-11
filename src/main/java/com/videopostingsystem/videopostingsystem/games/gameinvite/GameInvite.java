package com.videopostingsystem.videopostingsystem.games.gameinvite;

import com.videopostingsystem.videopostingsystem.users.Users;
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
    @JoinColumn(name = "inviter", nullable = false)
    private Users inviter;

    @ManyToOne
    @JoinColumn(name = "invited", nullable = false)
    private Users invited;

    @Enumerated(EnumType.STRING)
    private Status status;

    public GameInvite(Users inviter, Users invited){
        this.inviter = inviter;
        this.invited = invited;
        this.status = Status.PENDING;
    }
}
