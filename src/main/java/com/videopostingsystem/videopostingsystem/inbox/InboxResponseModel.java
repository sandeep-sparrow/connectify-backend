package com.videopostingsystem.videopostingsystem.inbox;

public record InboxResponseModel(String user, String last_message, boolean unread) {
}
