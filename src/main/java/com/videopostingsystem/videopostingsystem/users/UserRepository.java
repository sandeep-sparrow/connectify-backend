package com.videopostingsystem.videopostingsystem.users;

import jakarta.persistence.PostRemove;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;

@Repository
public interface UserRepository extends JpaRepository<Users, String> {
}
