package api_interaction;

import java.util.Date;

public record PostModel(long id, User user, String title, String body
,Date creationDate, Date lastModifiedDate, String tag) {
}

record User(String username, String password){
}
