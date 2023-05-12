package com.connectify.games.chess;

public record ChessGameSessionResponseModel(Long id, String whitePlayer, String blackPlayer, String turn, String gameStatus, Move recentMove) {
}
