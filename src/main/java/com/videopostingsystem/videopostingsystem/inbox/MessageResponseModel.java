package com.videopostingsystem.videopostingsystem.inbox;

import java.util.Date;

public record MessageResponseModel(Long message_id, String sender, String message, Date timeSent) {
}
