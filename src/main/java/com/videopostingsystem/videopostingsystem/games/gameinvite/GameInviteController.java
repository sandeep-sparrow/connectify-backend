package com.videopostingsystem.videopostingsystem.games.gameinvite;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class GameInviteController {

    GameInviteService gameInviteService;

    public GameInviteController(GameInviteService gameInviteService){
        this.gameInviteService = gameInviteService;
    }

    @GetMapping("/game-invites")
    public ResponseEntity<?> getGameInvites(HttpServletRequest request){
        return gameInviteService.getGameInvites(request);
    }

    @PostMapping("send-invite")
    public ResponseEntity<?> sendGameInvite(@RequestBody GameInviteRequestModel gameInviteRequestModel, HttpServletRequest request){
        return gameInviteService.sendGameInvite(gameInviteRequestModel, request);
    }

    @DeleteMapping("delete-invite")
    public ResponseEntity<?> deleteGameInvite(@RequestBody String inviter, HttpServletRequest request){
        return gameInviteService.deleteGameInvite(inviter, request);
    }

}
