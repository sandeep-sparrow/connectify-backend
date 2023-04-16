package com.videopostingsystem.videopostingsystem.inbox;

import jakarta.persistence.*;
import java.util.Objects;
@Entity
@Table(name = "inbox")
public class Inbox {
    @Id
    @Column(name = "inbox_id")
    private String inboxId;
    private String user1;
    private String user2;
    private String last_message;
    private boolean unread;

    public Inbox(String user1, String user2, String last_message) {
        this.user1 = user1;
        this.user2 = user2;
        this.last_message = last_message;
        this.unread = true;
        this.inboxId = user1 + "_" + user2;
    }

    public Inbox() {

    }

    public String getInboxId() {
        return inboxId;
    }

    public void setInboxId(String inbox_id) {
        this.inboxId = inbox_id;
    }

    public String getUser1() {
        return user1;
    }

    public void setUser1(String user1) {
        this.user1 = user1;
    }

    public String getUser2() {
        return user2;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inbox inbox = (Inbox) o;
        return inboxId == inbox.inboxId && unread == inbox.unread && Objects.equals(user1, inbox.user1) && Objects.equals(user2, inbox.user2) && Objects.equals(last_message, inbox.last_message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inboxId, user1, user2, last_message, unread);
    }

    @Override
    public String toString() {
        return "Inbox{" +
                "inbox_id=" + inboxId +
                ", user1='" + user1 + '\'' +
                ", user2='" + user2 + '\'' +
                ", last_message='" + last_message + '\'' +
                ", unread=" + unread +
                '}';
    }
}
