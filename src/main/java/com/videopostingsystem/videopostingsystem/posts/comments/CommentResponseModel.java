package com.videopostingsystem.videopostingsystem.posts.comments;

import java.util.Date;

public record CommentResponseModel(String user, String content, Date date) {
}
