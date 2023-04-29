package com.videopostingsystem.videopostingsystem.inbox.messagelog;

import java.util.Date;

public record MessageResponseModel(Long message_id, String receiver, String message, Date timeSent) {
}
