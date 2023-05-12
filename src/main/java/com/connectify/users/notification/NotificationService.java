package com.connectify.users.notification;

import com.connectify.users.config.JwtService;
import com.google.gson.Gson;
import com.connectify.users.UserRepository;
import com.connectify.users.Users;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public ResponseEntity<?> getNotifications(HttpServletRequest request) {
        String username = jwtService.getUsername(request);
        Users user = userRepository.findById(username).get();
        List<Notification> notifications = notificationRepository.findAllByUsers(user);
        List<NotificationResponseModel> notificationResponseModel = new ArrayList<>();
        for (Notification notification : notifications){
            notificationResponseModel.add(new NotificationResponseModel(notification.getSender().getUsername(), notification.getContent(), notification.getType(), notification.getTime()));
        }
        Gson gson = new Gson();
        return ResponseEntity.ok(gson.toJson(notificationResponseModel));
    }

    public ResponseEntity<?> removeAllNotifications(HttpServletRequest request){
        String username = jwtService.getUsername(request);
        Users user = userRepository.findById(username).get();
        List<Notification> userNotifications = notificationRepository.findAllByUsers(user);
        for (Notification notification: userNotifications){
            notificationRepository.deleteById(notification.getId());
        }
        return ResponseEntity.ok("Removed all notifications");
    }

    public void setNotification(Users notificationSender, Users notificationReceiver, NotificationType type, Long objectId){
        String content = "";
        if (type == NotificationType.COMMENT){
            content =  "commented on your post.";
        } else if (type == NotificationType.FOLLOW) {
            content = "started following you.";
        } else if (type == NotificationType.LIKE) {
            content = "liked your post.";
        } else if (type == NotificationType.TAG) {
            content = "mentioned you in a comment.";
        } else if (type == NotificationType.MESSAGE) {
            content = "sent you a message.";
        }
        Notification newNotification = new Notification(notificationReceiver, notificationSender, type, content, objectId);
        notificationRepository.save(newNotification);
    }

    public void removeNotification(Users notificationSender, Users notificationReceiver, NotificationType type, Long objectId){
            List<Notification> notifications = notificationRepository.findByTypeAndRelatedObjectIdAndSenderAndUsers(type, objectId, notificationSender, notificationReceiver);
            for (Notification notification : notifications){
                notificationRepository.deleteById(notification.getId());
            }
    }
}
