package com.connectify.users.notification;

import com.connectify.users.Users;
import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "users", nullable = false)
    private Users users;

    @ManyToOne
    @JoinColumn(name = "sender", nullable = false)
    private Users sender;
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(columnDefinition = "TEXT")
    private String content;
    private Date time;
    @Column(name = "related_object_id")
    private Long relatedObjectId;
    private boolean unread;

    public Notification(Users users, Users sender, NotificationType type, String content, Long relatedObjectId){
        this.users = users;
        this.sender = sender;
        this.type = type;
        this.content = content;
        this.relatedObjectId = relatedObjectId;
        this.time = new Date();
        this.unread = true;
    }


}
