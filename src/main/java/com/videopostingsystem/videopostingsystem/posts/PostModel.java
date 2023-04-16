package com.videopostingsystem.videopostingsystem.posts;

import java.util.Date;

public record PostModel(Long id, String username, String title, String body, Long likes, Long bookmarks, Date lastModifiedDate, String category) {
}
