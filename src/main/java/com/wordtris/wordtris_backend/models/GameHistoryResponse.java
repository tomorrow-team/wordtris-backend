package com.wordtris.wordtris_backend.models;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class GameHistoryResponse {
    private Timestamp data;
    private Integer score;
    private Integer status_id;
    private Integer avatar_id;

    public GameHistoryResponse(Timestamp data, Integer score, Integer status_id, Integer avatar_id) {
        this.data = data;
        this.score = score;
        this.status_id = status_id;
        this.avatar_id = avatar_id;
    }

}
