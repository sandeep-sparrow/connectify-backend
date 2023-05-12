package com.connectify.inbox.messagelog;

import com.connectify.inbox.Inbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageLogRepository extends JpaRepository<MessageLog, Long> {
    List<MessageLog> findAllByInbox(Inbox inbox);
}
