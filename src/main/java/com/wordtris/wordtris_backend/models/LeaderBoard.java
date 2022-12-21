package com.wordtris.wordtris_backend.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaderBoard {
    private Integer place;
    private String login;
    private Long score;
    private Integer status_id;
    private Integer avatar_id;

    public LeaderBoard(Integer place, String login, Long score, Integer status_id, Integer avatar_id) {
        this.login = login;
        this.score = score;
        this.status_id = status_id;
        this.avatar_id = avatar_id;
        this.place = place;
    }

    public LeaderBoard() {

    }
}
