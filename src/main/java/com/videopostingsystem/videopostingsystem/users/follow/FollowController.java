package com.videopostingsystem.videopostingsystem.users.follow;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FollowController {

    FollowService followService;

    public FollowController(FollowService followService){
        this.followService = followService;
    }

    @GetMapping("/{user}/follow-count")
    public ResponseEntity<?> getFollowCount(@PathVariable("user") String user){
        return followService.getFollowCount(user);
    }

    @GetMapping("/{user}/followed")
    public ResponseEntity<?> isUserFollowed(@PathVariable("user") String user, HttpServletRequest request){
        return followService.isUserFollowed(user, request);
    }

    @PostMapping("/{user}/follow")
    public ResponseEntity<?> followEvent(@PathVariable("user") String user, HttpServletRequest request){
        return followService.followEvent(user, request);
    }

    @PostMapping("/{user}/unfollow")
    public ResponseEntity<?> unfollowEvent(@PathVariable("user") String user, HttpServletRequest request){
        return followService.unfollowEvent(user, request);
    }

    @GetMapping("/friends")
    public ResponseEntity<?> friendsList(HttpServletRequest request){
        return followService.friendsList(request);
    }
}
