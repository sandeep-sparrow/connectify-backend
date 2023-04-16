package com.videopostingsystem.videopostingsystem.inbox;

import com.videopostingsystem.videopostingsystem.users.Users;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inbox")
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class Inbox {
    @Id
    @Column(name = "inbox_id")
    private String inboxId;
    @ManyToOne
    @JoinColumn(name = "user1", nullable = false)
    private Users user1;
    @ManyToOne
    @JoinColumn(name = "user2", nullable = false)
    private Users user2;
    private String last_message;
    private boolean unread;

    public Inbox(Users user1, Users user2, String last_message) {
        this.user1 = user1;
        this.user2 = user2;
        this.last_message = last_message;
        this.unread = true;
        this.inboxId = user1.getUsername() + "_" + user2.getUsername();
    }
}
