package com.videopostingsystem.videopostingsystem.recommendSystem;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:63343")
public class FeedController {

    FeedService feedService;

    @GetMapping("/feed")
    public ResponseEntity<?> feed(HttpSession session){
        return feedService.feed(session);
    }

}
