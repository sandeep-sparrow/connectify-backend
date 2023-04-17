package com.videopostingsystem.videopostingsystem.inbox;

import com.videopostingsystem.videopostingsystem.users.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InboxRepository extends JpaRepository<Inbox, String> {
    List<Inbox> findByUser1(Users user1);
    List<Inbox> findByUser2(Users user2);
}
