package com.connectify.inbox;

import com.connectify.users.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InboxRepository extends JpaRepository<Inbox, String> {
    List<Inbox> findAllByUser1(Users user1);
    List<Inbox> findAllByUser2(Users user2);
}
