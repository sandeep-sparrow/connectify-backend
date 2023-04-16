package com.videopostingsystem.videopostingsystem.inbox.messagelog;

import com.videopostingsystem.videopostingsystem.inbox.Inbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageLogRepository extends JpaRepository<MessageLog, Long> {
    List<MessageLog> findByInbox(Inbox inbox);
}
