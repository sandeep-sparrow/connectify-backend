package com.videopostingsystem.videopostingsystem.inbox.messagelog;

import com.videopostingsystem.videopostingsystem.inbox.Inbox;
import com.videopostingsystem.videopostingsystem.inbox.InboxRepository;
import com.videopostingsystem.videopostingsystem.inbox.InboxResponseModel;
import com.videopostingsystem.videopostingsystem.users.UserRepository;
import com.videopostingsystem.videopostingsystem.users.Users;
import com.videopostingsystem.videopostingsystem.users.config.JwtService;
import com.videopostingsystem.videopostingsystem.users.notification.NotificationService;
import com.videopostingsystem.videopostingsystem.users.notification.NotificationType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class MessageService {
    private final UserRepository userRepository;
    private final MessageLogRepository messageLogRepository;
    private final InboxRepository inboxRepository;
    @Autowired
    private final NotificationService notificationService;
    private final JwtService jwtService;

    public ResponseEntity<?> postMessage(MessageModel messageModel, HttpServletRequest request){
        String username = jwtService.getUsername(request);
        if (messageModel.receiver() == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You must provide who you intend to send this message to");
        }
        if (messageModel.message() == null){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Message cannot be empty");
        }
        if (userRepository.findById(messageModel.receiver()).isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user provided.");
        }
        Users userObj = userRepository.findById(username).get();
        Users receiverObj = userRepository.findById(messageModel.receiver()).get();

        if (inboxRepository.findById(username+"_"+messageModel.receiver()).isPresent() || inboxRepository.findById(messageModel.receiver()+"_" +username).isPresent()){
            if (inboxRepository.findById(username+"_"+messageModel.receiver()).isPresent()){
                Inbox inbox = inboxRepository.findById(username+"_"+messageModel.receiver()).get();
                inbox.setTimeSent(new Date());
                inbox.setLast_message(messageModel.message());
                inbox.setUnread(true);
                MessageLog messageLog = new MessageLog(inbox, username, messageModel.receiver(), messageModel.message());
                messageLogRepository.save(messageLog);
                inboxRepository.save(inbox);
                notificationService.setNotification(userObj, receiverObj, NotificationType.MESSAGE, messageLog.getMessage_id());
                return ResponseEntity.ok(new MessageResponseModel(messageLog.getMessage_id(), messageLog.getSender(), messageLog.getMessage(), messageLog.getTimeSent()));
            }
            else {
                Inbox inbox = inboxRepository.findById(messageModel.receiver()+"_" +username).get();
                inbox.setTimeSent(new Date());
                inbox.setLast_message(messageModel.message());
                inbox.setUnread(true);
                MessageLog messageLog = new MessageLog(inbox, username, messageModel.receiver(), messageModel.message());
                messageLogRepository.save(messageLog);
                inboxRepository.save(inbox);
                notificationService.setNotification(userObj, receiverObj, NotificationType.MESSAGE, messageLog.getMessage_id());
                return ResponseEntity.ok(new MessageResponseModel(messageLog.getMessage_id(), messageLog.getReceiver(), messageLog.getMessage(), messageLog.getTimeSent()));
            }
        }
        else {
            Inbox inbox = new Inbox(userRepository.findById(username).get(), userRepository.findById(messageModel.receiver()).get(), messageModel.message());
            inbox.setTimeSent(new Date());
            inboxRepository.save(inbox);
            MessageLog messageLog = new MessageLog(inbox, username, messageModel.receiver(), messageModel.message());
            messageLogRepository.save(messageLog);
            notificationService.setNotification(userObj, receiverObj, NotificationType.MESSAGE, messageLog.getMessage_id());
            return ResponseEntity.ok(new MessageResponseModel(messageLog.getMessage_id(), messageLog.getSender(), messageLog.getMessage(), messageLog.getTimeSent()));
        }
    }


    public ResponseEntity<?> getMessageLogs(String user2, HttpServletRequest request) {
        String username = jwtService.getUsername(request);
        if (userRepository.findById(user2).isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user provided");
        }
        if (inboxRepository.findById(username + "_" + user2).isEmpty() && inboxRepository.findById(user2 + "_" + username).isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You have no open chats with " + user2);
        }
        Inbox inbox;
        if (inboxRepository.findById(username + "_" + user2).isPresent()){
            inbox = inboxRepository.findById(username + "_" + user2).get();
        } else {
            inbox = inboxRepository.findById(user2 + "_" + username).get();
        }
        List<MessageLog> messageLogs = messageLogRepository.findAllByInbox(inbox);
        if (messageLogs.get(messageLogs.size()-1).getSender().equals(user2)){
            inbox.setUnread(false);
            inboxRepository.save(inbox);
        }
        List<MessageResponseModel> formattedMessages = new ArrayList<>();
        for (MessageLog messageLog : messageLogs){
            notificationService.removeNotification(userRepository.findById(user2).get(), userRepository.findById(username).get(), NotificationType.MESSAGE, messageLog.getMessage_id());
            formattedMessages.add(new
                    MessageResponseModel(messageLog.getMessage_id(), messageLog.getSender(), messageLog.getMessage(), messageLog.getTimeSent()));
        }
        return ResponseEntity.ok(formattedMessages);
    }

    public ResponseEntity<?> getUnreadInboxes(HttpServletRequest request){
        String username = jwtService.getUsername(request);
        Users user = userRepository.findById(username).get();
        List<Inbox> inboxes = new ArrayList<>();
            if (!inboxRepository.findAllByUser1(user).isEmpty()){
                List<Inbox> inboxes1 = inboxRepository.findAllByUser1(user);
                inboxes.addAll(inboxes1);
            }
            if (!inboxRepository.findAllByUser2(user).isEmpty()){
                List<Inbox> inboxes2 = inboxRepository.findAllByUser2(user);
                inboxes.addAll(inboxes2);
            }
            List<Inbox> unreadInboxes = new ArrayList<>();
            for (Inbox inbox : inboxes){
                if (inbox.isUnread()){
                    unreadInboxes.add(inbox);
                }
            }

            List<Inbox> userUnreadInboxes = new ArrayList<>();

            for (Inbox unreadInbox : unreadInboxes){
                List<MessageLog> messageLogs = messageLogRepository.findAllByInbox(unreadInbox);
                if (!messageLogs.get(messageLogs.size()-1).getSender().equals(username)){
                    userUnreadInboxes.add(unreadInbox);
                }
            }
            List<InboxResponseModel> formattedUnreadInbox = new ArrayList<>();
            for (Inbox inbox : userUnreadInboxes){
                if (username.equals(inbox.getUser1().getUsername())){
                    formattedUnreadInbox.add(new InboxResponseModel(inbox.getUser2().getUsername(), inbox.getLast_message(), inbox.isUnread(), inbox.getTimeSent()));
                }
                else formattedUnreadInbox.add(new InboxResponseModel(inbox.getUser1().getUsername(), inbox.getLast_message(), inbox.isUnread(), inbox.getTimeSent()));
            }

            return ResponseEntity.ok(formattedUnreadInbox);
    }

    public ResponseEntity<?> getInboxes(HttpServletRequest request){
        String username = jwtService.getUsername(request);
        Users user = userRepository.findById(username).get();
        List<Inbox> inboxes = new ArrayList<>();
        if (!inboxRepository.findAllByUser1(user).isEmpty()){
            List<Inbox> inboxes1 = inboxRepository.findAllByUser1(user);
            inboxes.addAll(inboxes1);
        }
        if (!inboxRepository.findAllByUser2(user).isEmpty()){
            List<Inbox> inboxes2 = inboxRepository.findAllByUser2(user);
            inboxes.addAll(inboxes2);
        }
        List<InboxResponseModel> formattedInboxes = new ArrayList<>();
        for (Inbox inbox : inboxes){
            List<MessageLog> messageLogs = messageLogRepository.findAllByInbox(inbox);
            if (username.equals(inbox.getUser1().getUsername())){
                if (inbox.isUnread() && messageLogs.get(messageLogs.size()-1).getSender().equals(username)) {
                    formattedInboxes.add(new InboxResponseModel(inbox.getUser2().getUsername(), inbox.getLast_message(), false, inbox.getTimeSent()));
                }
                else {
                    formattedInboxes.add(new InboxResponseModel(inbox.getUser2().getUsername(), inbox.getLast_message(), inbox.isUnread(), inbox.getTimeSent()));
                }
            }
            else {
                if (inbox.isUnread() && messageLogs.get(messageLogs.size()-1).getSender().equals(username)) {
                    formattedInboxes.add(new InboxResponseModel(inbox.getUser1().getUsername(), inbox.getLast_message(), false, inbox.getTimeSent()));
                }
                else {
                formattedInboxes.add(new InboxResponseModel(inbox.getUser1().getUsername(), inbox.getLast_message(), inbox.isUnread(), inbox.getTimeSent()));
                }
            }
        }

        return ResponseEntity.ok(formattedInboxes);
    }
}
