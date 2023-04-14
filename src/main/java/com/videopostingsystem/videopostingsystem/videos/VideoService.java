package com.videopostingsystem.videopostingsystem.videos;

import com.videopostingsystem.videopostingsystem.users.UserRepository;
import com.videopostingsystem.videopostingsystem.users.Users;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class VideoService {
    private final VideoRepository videoRepository;
    private final UserRepository userRepository;

    public VideoService(VideoRepository videoRepository, UserRepository userRepository) {
        this.videoRepository = videoRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> uploadVideo(VideoModel video, HttpSession session){
        String loggedInUser = (String) session.getAttribute("loggedInUser");
        if (loggedInUser != null){
            Users user = userRepository.getReferenceById(loggedInUser);
            Video newVideo = new Video(user, video.title(), video.link(), video.tags());
            newVideo.setSummary("");
            AssemblyAI videoProcessor = new AssemblyAI(video.link());
            String summary = videoProcessor.call();
            if (summary == null){
                return ResponseEntity.badRequest().body("Video could not be validated");
            }
            newVideo.setSummary(summary);
            newVideo.setStatus("completed");
            videoRepository.save(newVideo);
            return ResponseEntity.ok(newVideo);
        }
        else {
            return ResponseEntity.badRequest().body("You must be signed in to upload a video.");
        }
    }
}
