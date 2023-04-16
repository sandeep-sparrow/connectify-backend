package com.videopostingsystem.videopostingsystem.videos;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VideoRequest {


    private final VideoService videoService;
    public VideoRequest(VideoService videoService){
        this.videoService = videoService;
    }

    @PostMapping("upload-video")
    public ResponseEntity<?> uploadVideo(@RequestBody VideoModel video, HttpSession session) {
        return videoService.uploadVideo(video, session);
    }


}
