package com.connectify.users;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<?> deleteAccount(HttpServletRequest request){
        return userService.deleteAccount(request);
    }

    @DeleteMapping("/delete-account/{user}")
    public ResponseEntity<?> deleteAccountAdmin(@PathVariable("user") String user, HttpServletRequest request){
        return userService.deleteAccountAdmin(user, request);
    }

    @GetMapping("/getUsername")
    public ResponseEntity<?> myUsername(HttpServletRequest request){
        return userService.getUsername(request);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpServletRequest request){
        return userService.getProfile(request);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileModel updateProfileModel, HttpServletRequest request){
        return userService.updateProfile(updateProfileModel, request);
    }

    @PutMapping("/profile-settings")
    public ResponseEntity<?> updateProfileSettings(@RequestBody UpdateProfileSettingsModel updateProfileSettingsModel, HttpServletRequest request){
        return userService.updateProfileSettings(updateProfileSettingsModel, request);
    }

    @GetMapping("/{user}")
    public ResponseEntity<?> getUserProfile(@PathVariable("user") String user, HttpServletRequest request){
        return userService.getUserProfile(user, request);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(){
        return userService.getUsers();
    }

    @PutMapping("/update-theme")
    public ResponseEntity<?> updateTheme(@RequestBody String theme, HttpServletRequest request){
        return userService.updateTheme(theme, request);
    }

    @GetMapping("/theme")
    public ResponseEntity<?> getTheme(HttpServletRequest request){
        return userService.getTheme(request);
    }

    @PostMapping("/online")
    public ResponseEntity<?> onlineHeartBeat(HttpServletRequest request){
        return userService.onlineHeartBeat(request);
    }

    @Scheduled(fixedRate = 180000)
    public void cleanUpOldInvites() {
        userService.cleanUpOnlineList();
    }
}
