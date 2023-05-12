package com.connectify.users.authenticate;

public record AuthenticateModel(String username, String email, String password, String security_clearance) {
}
