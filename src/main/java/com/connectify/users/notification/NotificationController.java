package com.connectify.users.notification;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService){
        this.notificationService = notificationService;
    }

    @GetMapping("/notifications")
    public ResponseEntity<?> getNotifications(HttpServletRequest request){
        return notificationService.getNotifications(request);
    }

    @DeleteMapping("/delete-notifications")
    public ResponseEntity<?> removeAllNotifications(HttpServletRequest request){
        return notificationService.removeAllNotifications(request);
    }

    @PostMapping("/read-notifications")
    public ResponseEntity<?> readAllNotifications(HttpServletRequest request){
        return notificationService.readAllNotifications(request);
    }
}
