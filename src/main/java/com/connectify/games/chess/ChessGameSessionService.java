package com.connectify.games.chess;

import com.connectify.users.UserRepository;
import com.connectify.users.Users;
import com.connectify.users.config.JwtService;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@AllArgsConstructor
public class ChessGameSessionService {

    ChessGameSessionRepository chessGameSessionRepository;
    UserRepository userRepository;

    JwtService jwtService;


    public ResponseEntity<?> createSession(String opponent, HttpServletRequest request) {
        String username = jwtService.getUsername(request);
        if (opponent == null || userRepository.findById(opponent).isEmpty()){
            return ResponseEntity.badRequest().body("invalid");
        }
        Random random = new Random();
        int teamChoice = random.nextInt(2);
        ChessGameSession chessGameSession;
        Users userObj = userRepository.findById(username).get();
        Users opponentObj = userRepository.findById(opponent).get();
        if (teamChoice == 0){
            chessGameSession = new ChessGameSession(opponentObj, userObj);
        }
        else {
            chessGameSession = new ChessGameSession(userObj, opponentObj);
        }
        chessGameSessionRepository.save(chessGameSession);
        Map<String, Object> response = new HashMap<>();
        response.put("id", chessGameSession.getId());

        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity<?> getChessSession(String opponent, HttpServletRequest request) {
        if (opponent == null || userRepository.findById(opponent).isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid opponent");
        }
        String username = jwtService.getUsername(request);
        Users userObj = userRepository.findById(username).get();
        Users opponentObj = userRepository.findById(opponent).get();
        if (chessGameSessionRepository.findByBlackPlayerAndWhitePlayer(userObj, opponentObj).isEmpty() && chessGameSessionRepository.findByBlackPlayerAndWhitePlayer(opponentObj, userObj).isEmpty()){
            return ResponseEntity.ok("No game open between users yet");
        }

        Gson gson = new Gson();
        ChessGameSession chessGameSession;
        if (chessGameSessionRepository.findByBlackPlayerAndWhitePlayer(userObj, opponentObj).isPresent()){
            chessGameSession = chessGameSessionRepository.findByBlackPlayerAndWhitePlayer(userObj, opponentObj).get();
        }
        else {
            chessGameSession = chessGameSessionRepository.findByBlackPlayerAndWhitePlayer(opponentObj, userObj).get();
        }
        ChessGameSessionResponseModel chessGameSessionResponseModel = new ChessGameSessionResponseModel(chessGameSession.getId(), chessGameSession.getWhitePlayer().getUsername(), chessGameSession.getBlackPlayer().getUsername(), chessGameSession.getTurn().toString(), chessGameSession.getGameStatus().toString(), chessGameSession.getRecentMove());
        return ResponseEntity.ok(gson.toJson(chessGameSessionResponseModel));
    }

    public ResponseEntity<?> getChessSessionWithId(Long id) {
        if (id == null || chessGameSessionRepository.findById(id).isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Game does not exist");
        }

        Gson gson = new Gson();
        ChessGameSession chessGameSession = chessGameSessionRepository.findById(id).get();
        ChessGameSessionResponseModel chessGameSessionResponseModel = new ChessGameSessionResponseModel(chessGameSession.getId(), chessGameSession.getWhitePlayer().getUsername(), chessGameSession.getBlackPlayer().getUsername(), chessGameSession.getTurn().toString(), chessGameSession.getGameStatus().toString(), chessGameSession.getRecentMove());
        return ResponseEntity.ok(gson.toJson(chessGameSessionResponseModel));
    }


    public ResponseEntity<?> postMove(Long sessionId, MoveRequestModel move, HttpServletRequest request) {

        if (sessionId == null || chessGameSessionRepository.findById(sessionId).isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("game with sessionId " + sessionId + "does not exist");
        }
        ChessGameSession chessGameSession = chessGameSessionRepository.findById(sessionId).get();
        String username = jwtService.getUsername(request);
        Users userObj = userRepository.findById(username).get();
        if (!chessGameSession.getBlackPlayer().getUsername().equals(userObj.getUsername()) && !chessGameSession.getWhitePlayer().getUsername().equals(userObj.getUsername())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is not part of this game");
        }
        Turn userColor;
        if (chessGameSession.getBlackPlayer().getUsername().equals(userObj.getUsername())){
            userColor = Turn.BLACK;
        }
        else {
            userColor = Turn.WHITE;
        }
        if (!chessGameSession.getTurn().equals(userColor)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("It is not your turn");
        }

        Piece nextPiece = Piece.valueOf(move.piece().toUpperCase());

        Move nextMove = new Move(nextPiece, move.startPosition(), move.endPosition());
        if (userColor.equals(Turn.WHITE)){
            chessGameSession.setTurn(Turn.BLACK);
        }
        else {
            chessGameSession.setTurn(Turn.WHITE);
        }
        chessGameSession.updateMove(nextMove);
        chessGameSessionRepository.save(chessGameSession);

        return ResponseEntity.ok("Move was successful");
    }


    public ResponseEntity<?> deleteSession(Long sessionId, HttpServletRequest request) {
        String username = jwtService.getUsername(request);
        return null;
    }

    public ResponseEntity<?> updateGameStatus(Long id, String gameStatus, HttpServletRequest request) {
        if (id == null || chessGameSessionRepository.findById(id).isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("game with id " + id + "does not exist");
        }
        ChessGameSession chessGameSession = chessGameSessionRepository.findById(id).get();
        String username = jwtService.getUsername(request);
        Users userObj = userRepository.findById(username).get();
        if (!chessGameSession.getBlackPlayer().getUsername().equals(userObj.getUsername()) && !chessGameSession.getWhitePlayer().getUsername().equals(userObj.getUsername())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is not part of this game");
        }

        chessGameSession.setGameStatus(GameStatus.valueOf(gameStatus));

        chessGameSessionRepository.save(chessGameSession);

        return ResponseEntity.ok(gameStatus);
    }
}
