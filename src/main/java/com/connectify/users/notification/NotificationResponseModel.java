package com.connectify.users.notification;

import java.util.Date;

public record NotificationResponseModel(String sender, String content, NotificationType type, Date date, boolean unread) {
}
