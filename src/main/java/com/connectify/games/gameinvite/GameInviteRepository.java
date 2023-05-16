package com.connectify.games.gameinvite;

import com.connectify.users.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface GameInviteRepository extends JpaRepository<GameInvite, Long> {
    List<GameInvite> findAllByInvited(Users invited);
    List<GameInvite> findAllByInviter(Users inviter);
    Optional<GameInvite> findByInviterAndInvited(Users inviter, Users invited);

   List<GameInvite> findByCreatedAtBefore(Date cutoff);
}
