package com.videopostingsystem.videopostingsystem.inbox.messagelog;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class MessageController {

    private final MessageService messageService;
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/inbox/send")
    public ResponseEntity<?> postMessage(@RequestBody MessageModel messageModel, HttpServletRequest request){
        return messageService.postMessage(messageModel, request);
    }

    @GetMapping("/inbox/{user}")
    public ResponseEntity<?> getMessageLogs(@PathVariable("user") String user2, HttpServletRequest request){
        return messageService.getMessageLogs(user2, request);
    }

    @GetMapping("/unread-inbox")
    public ResponseEntity<?> getUnreadInboxes(HttpServletRequest request){
        return messageService.getUnreadInboxes(request);
    }

    @GetMapping("/inbox")
    public ResponseEntity<?> getInboxes(HttpServletRequest request){
        return messageService.getInboxes(request);
    }
}
