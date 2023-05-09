package com.videopostingsystem.videopostingsystem.recommendSystem;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
public class FeedController {

    FeedService feedService;

    @GetMapping("/feed")
    public ResponseEntity<?> feed(HttpServletRequest request){
        return feedService.feed(request);
    }

}
