package com.connectify.users;

import com.connectify.users.Users;

public record AllUserCredentialsResponseModel(Users users, int followers, int following) {
}
