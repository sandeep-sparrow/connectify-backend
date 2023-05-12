package com.connectify.inbox;

import java.util.Date;

public record InboxResponseModel(String user, String last_message, boolean unread, Date timeSent) {
}
