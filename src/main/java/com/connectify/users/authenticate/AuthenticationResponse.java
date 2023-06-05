package com.connectify.users.authenticate;

public record AuthenticationResponse(String token, String theme, String emoji, String username) {
}
