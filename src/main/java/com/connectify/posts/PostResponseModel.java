package com.connectify.posts;

import java.util.Date;

public record PostResponseModel(Long id, String username, String title, String body, Long likes, Long bookmarks, Date lastModifiedDate, String category) {
}
