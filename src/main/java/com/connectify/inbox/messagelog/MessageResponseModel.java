package com.connectify.inbox.messagelog;

import java.util.Date;

public record MessageResponseModel(Long message_id, String sender, String message, Date timeSent) {
}
