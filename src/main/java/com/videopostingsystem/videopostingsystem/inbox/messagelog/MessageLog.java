package com.videopostingsystem.videopostingsystem.inbox.messagelog;

import com.videopostingsystem.videopostingsystem.inbox.Inbox;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class MessageLog {
        @Id
        @SequenceGenerator(
                name = "message_id_sequence",
                sequenceName = "message_id_sequence",
                allocationSize = 1
        )
        @GeneratedValue(
                strategy = GenerationType.SEQUENCE,
                generator = "message_id_sequence"
        )
        private Long message_id;
        @ManyToOne
        @JoinColumn(name = "inbox_id")
        private Inbox inbox;
        private String receiver;
        private String sender;
        private String message;
        private Date timeSent = new Date();

        public MessageLog(Inbox inbox,String sender, String receiver, String message) {
                this.inbox = inbox;
                this.sender = sender;
                this.receiver = receiver;
                this.message = message;
        }
}
