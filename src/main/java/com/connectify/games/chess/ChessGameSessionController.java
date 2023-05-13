package com.connectify.games.chess;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chess")
public class ChessGameSessionController {
    ChessGameSessionService chessGameSessionService;

    public ChessGameSessionController(ChessGameSessionService chessGameSessionService){
        this.chessGameSessionService = chessGameSessionService;
    }

    @PostMapping("/create-session")
    public ResponseEntity<?> createSession(@RequestBody String opponent, HttpServletRequest request) {
        return chessGameSessionService.createSession(opponent, request);
    }

    @PostMapping("/session")
    public ResponseEntity<?> getChessSession(@RequestBody String opponent, HttpServletRequest request){
        return chessGameSessionService.getChessSession(opponent, request);
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<?> getChessSessionWithId(@PathVariable("sessionId") Long id){
        return chessGameSessionService.getChessSessionWithId(id);
    }

    @PutMapping("/post-move/{sessionId}")
    public ResponseEntity<?> postMove(@PathVariable("sessionId") Long sessionId, @RequestBody MoveRequestModel move, HttpServletRequest request) {
        return chessGameSessionService.postMove(sessionId, move, request);
    }

    @DeleteMapping("/delete-session/{sessionId}")
    public ResponseEntity<?> deleteSession(@PathVariable Long sessionId, HttpServletRequest request) {
        return chessGameSessionService.deleteSession(sessionId, request);
    }

}
