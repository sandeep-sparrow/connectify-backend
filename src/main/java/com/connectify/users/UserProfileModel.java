package com.connectify.users;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class UserProfileModel {
    private String username;
    private String country;
    private String bio;
    private String topCategory;
    private String cardColor;
    private String backgroundColor;
    private String profilePic;
    private String online;
    private int followers;
    private int following;
    private boolean follows;

}
