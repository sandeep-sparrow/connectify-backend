package com.videopostingsystem.videopostingsystem.inbox;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class MessageRequest {

    private final MessageService messageService;
    public MessageRequest(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/inbox/send")
    public ResponseEntity<?> postMessage(@RequestBody MessageModel messageModel, HttpSession session){
        return messageService.postMessage(messageModel, session);
    }

    @GetMapping("/inbox/{user}")
    public ResponseEntity<?> getMessageLogs(@PathVariable("user") String user2, HttpSession session){
        return messageService.getMessageLogs(user2, session);
    }

    @GetMapping("/inbox")
    public ResponseEntity<?> getUnreadInboxes(HttpSession session){
        return messageService.getUnreadInboxes(session);
    }
}
