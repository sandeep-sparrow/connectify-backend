package com.videopostingsystem.videopostingsystem.inbox;

import com.videopostingsystem.videopostingsystem.users.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {
    private final UserRepository userRepository;
    private final MessageLogRepository messageLogRepository;
    private final InboxRepository inboxRepository;

    public MessageService(UserRepository userRepository, MessageLogRepository messageLogRepository, InboxRepository inboxRepository) {
        this.userRepository = userRepository;
        this.messageLogRepository = messageLogRepository;
        this.inboxRepository = inboxRepository;
    }

    public ResponseEntity<?> postMessage(MessageModel messageModel, HttpSession session){
        String user = (String) session.getAttribute("loggedInUser");
        if (user != null && userRepository.findById(user).isPresent()){
            if (messageModel.receiver() != null){
                if (messageModel.message() != null){
                    if (userRepository.findById(messageModel.receiver()).isPresent()){
                        if (inboxRepository.findById(user+"_"+messageModel.receiver()).isPresent() || inboxRepository.findById(messageModel.receiver()+"_" +user).isPresent()){
                            if (inboxRepository.findById(user+"_"+messageModel.receiver()).isPresent()){
                                Inbox inbox = inboxRepository.findById(user+"_"+messageModel.receiver()).get();
                                inbox.setLast_message(messageModel.message());
                                inbox.setUnread(true);
                                MessageLog messageLog = new MessageLog(inbox, user, messageModel.receiver(), messageModel.message());
                                messageLogRepository.save(messageLog);
                                inboxRepository.save(inbox);
                                return ResponseEntity.ok(messageLog);
                            }
                            else {
                                Inbox inbox = inboxRepository.findById(messageModel.receiver()+"_" +user).get();
                                inbox.setLast_message(messageModel.message());
                                inbox.setUnread(true);
                                MessageLog messageLog = new MessageLog(inbox, user, messageModel.receiver(), messageModel.message());
                                messageLogRepository.save(messageLog);
                                inboxRepository.save(inbox);
                                return ResponseEntity.ok(messageLog);
                            }
                        }
                        else {
                            Inbox inbox = new Inbox(user, messageModel.receiver(), messageModel.message());
                            inboxRepository.save(inbox);
                            MessageLog messageLog = new MessageLog(inbox, user, messageModel.receiver(), messageModel.message());
                            messageLogRepository.save(messageLog);
                            return ResponseEntity.ok(messageLog);
                        }

                    }
                    else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user provided.");
                }
                else return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Message cannot be empty");
            }
            else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You must provide who you intend to send this message to");
        }
        else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
    }


    public ResponseEntity<?> getMessageLogs(String user2, HttpSession session) {
        String user = (String) session.getAttribute("loggedInUser");
        if (user != null && userRepository.findById(user).isPresent()){
            if (userRepository.findById(user2).isPresent()){
                if (inboxRepository.findById(user + "_" + user2).isPresent() || inboxRepository.findById(user2 + "_" + user).isPresent()){
                    Inbox inbox;
                    if (inboxRepository.findById(user + "_" + user2).isPresent()){
                        inbox = inboxRepository.findById(user + "_" + user2).get();


                    }
                    else {
                        inbox = inboxRepository.findById(user2 + "_" + user).get();
                    }
                    List<MessageLog> messageLogs = messageLogRepository.findByInbox(inbox);
                    if (messageLogs.get(messageLogs.size()-1).getSender().equals(user2)){
                        inbox.setUnread(false);
                        inboxRepository.save(inbox);
                    }
                    List<MessageResponseModel> formattedMessages = new ArrayList<>();
                    for (MessageLog messageLog : messageLogs){
                        formattedMessages.add(new
                                MessageResponseModel(messageLog.getMessage_id(), messageLog.getSender(), messageLog.getMessage(), messageLog.getTimeSent()));
                    }
                    return ResponseEntity.ok(formattedMessages);
                }
                else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You have no open chats with " + user2);
            }
            else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user provided");
        }
        else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
    }

    public ResponseEntity<?> getUnreadInboxes(HttpSession session){
        String user = (String) session.getAttribute("loggedInUser");
        if (user != null && userRepository.findById(user).isPresent()){
            List<Inbox> inboxes = new ArrayList<>();
            if (!inboxRepository.findByUser1(user).isEmpty()){
                List<Inbox> inboxes1 = inboxRepository.findByUser1(user);
                inboxes.addAll(inboxes1);
            }
            if (!inboxRepository.findByUser2(user).isEmpty()){
                List<Inbox> inboxes2 = inboxRepository.findByUser2(user);
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
                List<MessageLog> messageLogs = messageLogRepository.findByInbox(unreadInbox);
                if (!messageLogs.get(messageLogs.size()-1).getSender().equals(user)){
                    userUnreadInboxes.add(unreadInbox);
                }
            }
            List<InboxResponseModel> formattedUnreadInbox = new ArrayList<>();
            for (Inbox inbox : userUnreadInboxes){
                if (user.equals(inbox.getUser1())){
                    formattedUnreadInbox.add(new InboxResponseModel(inbox.getUser2(), inbox.getLast_message(), inbox.isUnread()));
                }
                else formattedUnreadInbox.add(new InboxResponseModel(inbox.getUser1(), inbox.getLast_message(), inbox.isUnread()));
            }

            return ResponseEntity.ok(formattedUnreadInbox);

            }
        else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
    }
}
