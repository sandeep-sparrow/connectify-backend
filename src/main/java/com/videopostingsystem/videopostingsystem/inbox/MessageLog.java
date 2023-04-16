package com.videopostingsystem.videopostingsystem.inbox;

import jakarta.persistence.*;

import java.util.Date;
import java.util.Objects;

@Entity
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

        public MessageLog() {

        }

        public Long getMessage_id() {
                return message_id;
        }

        public void setMessage_id(Long message_id) {
                this.message_id = message_id;
        }

        public Inbox getInbox() {
                return inbox;
        }

        public void setInbox(Inbox inbox) {
                this.inbox = inbox;
        }

        public String getReceiver() {
                return receiver;
        }

        public void setReceiver(String receiver) {
                this.receiver = receiver;
        }

        public String getSender() {
                return sender;
        }

        public void setSender(String sender) {
                this.sender = sender;
        }

        public String getMessage() {
                return message;
        }

        public void setMessage(String message) {
                this.message = message;
        }

        public Date getTimeSent() {
                return timeSent;
        }

        public void setTimeSent(Date timeSent) {
                this.timeSent = timeSent;
        }

        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                MessageLog that = (MessageLog) o;
                return Objects.equals(message_id, that.message_id) && Objects.equals(inbox, that.inbox) && Objects.equals(receiver, that.receiver) && Objects.equals(sender, that.sender) && Objects.equals(message, that.message) && Objects.equals(timeSent, that.timeSent);
        }

        @Override
        public int hashCode() {
                return Objects.hash(message_id, inbox, receiver, sender, message, timeSent);
        }

        @Override
        public String toString() {
                return "MessageLog{" +
                        "message_id=" + message_id +
                        ", inbox=" + inbox +
                        ", receiver='" + receiver + '\'' +
                        ", sender='" + sender + '\'' +
                        ", message='" + message + '\'' +
                        ", timeSent=" + timeSent +
                        '}';
        }
}
