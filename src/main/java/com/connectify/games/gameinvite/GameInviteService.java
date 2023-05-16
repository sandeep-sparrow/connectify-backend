package com.connectify.games.gameinvite;

import com.google.gson.Gson;
import com.connectify.users.UserRepository;
import com.connectify.users.Users;
import com.connectify.users.config.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class GameInviteService {

    private final GameInviteRepository gameInviteRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public ResponseEntity<?> getGameInvites(HttpServletRequest request) {
        String username = jwtService.getUsername(request);
        Users user = userRepository.findById(username).get();
        List<GameInvite> gameInviteList = gameInviteRepository.findAllByInvited(user);
        List<GameInviteResponseModel> gameInviteResponseModelList = new ArrayList<>();
        Gson gson = new Gson();
        for (GameInvite gameInvite : gameInviteList){
            gameInviteResponseModelList.add(new GameInviteResponseModel(gameInvite.getInviter().getUsername(), gameInvite.getInvited().getUsername(), gameInvite.getGame().toString()));
        }
        return ResponseEntity.ok(gson.toJson(gameInviteResponseModelList));
    }

    public ResponseEntity<?> sendGameInvite(GameInviteRequestModel gameInviteRequestModel, HttpServletRequest request){
        if (gameInviteRequestModel.invited() == null || userRepository.findById(gameInviteRequestModel.invited()).isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user");
        }
        String username = jwtService.getUsername(request);
        Users user = userRepository.findById(username).get();
        Users userInvitedObj = userRepository.findById(gameInviteRequestModel.invited()).get();
        if (gameInviteRepository.findAllByInvited(userInvitedObj).size() > 0 || gameInviteRepository.findAllByInviter(user).size() > 0){
            return ResponseEntity.badRequest().body("pending requests");
        }
        GameInvite gameInvite = new GameInvite(user, userInvitedObj, gameInviteRequestModel.game());
        gameInviteRepository.save(gameInvite);
        Gson gson = new Gson();
        GameInviteResponseModel gameInviteResponseModel = new GameInviteResponseModel(username, gameInviteRequestModel.invited(), gameInviteRequestModel.game().toString());
        return ResponseEntity.ok(gson.toJson(gameInviteResponseModel));
    }

    public ResponseEntity<?> deleteGameInvite(String inviter, HttpServletRequest request){
        if (inviter == null || userRepository.findById(inviter).isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user");
        }
        String username = jwtService.getUsername(request);
        Users user = userRepository.findById(username).get();
        Users userInviterObj = userRepository.findById(inviter).get();
        if (gameInviteRepository.findByInviterAndInvited(userInviterObj, user).isEmpty()){
            return ResponseEntity.badRequest().body("No invite");
        }
        gameInviteRepository.deleteById(gameInviteRepository.findByInviterAndInvited(userInviterObj, user).get().getId());
        return ResponseEntity.badRequest().body("deleted");
    }

    public void cleanUpOldInvites() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, -15);
        Date cutoff = cal.getTime();
        List<GameInvite> oldInvites = gameInviteRepository.findByCreatedAtBefore(cutoff);
        for (GameInvite invite : oldInvites){
            gameInviteRepository.deleteById(invite.getId());
        }
    }
}
