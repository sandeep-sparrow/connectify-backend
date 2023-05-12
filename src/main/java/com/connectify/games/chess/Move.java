package com.connectify.games.chess;

import lombok.*;
import jakarta.persistence.*;

@Embeddable
@NoArgsConstructor
@Getter
@Setter
public class Move {

    @Enumerated(EnumType.STRING)
    private Piece piece;

    private String startPosition;

    private String endPosition;

    private boolean isCapture;

    private boolean isCheck;

    private boolean isCheckmate;


    public Move(Piece piece, String startPosition, String endPosition) {
        this.piece = piece;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.isCapture = false;
        this.isCheck = false;
        this.isCheckmate = false;
    }
}
