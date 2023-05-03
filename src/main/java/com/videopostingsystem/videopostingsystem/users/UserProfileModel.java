package com.videopostingsystem.videopostingsystem.users;

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


}
