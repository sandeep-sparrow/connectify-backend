package com.videopostingsystem.videopostingsystem.inbox;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InboxRepository extends JpaRepository<Inbox, String> {
    List<Inbox> findByUser1(String user1);
    List<Inbox> findByUser2(String user2);
}
