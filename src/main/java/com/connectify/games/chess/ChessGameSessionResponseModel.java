package com.connectify.games.chess;

import java.util.Date;

public record ChessGameSessionResponseModel(Long id, String whitePlayer, String blackPlayer, String turn, String gameStatus, Move recentMove, Date updatedAt) {
}
