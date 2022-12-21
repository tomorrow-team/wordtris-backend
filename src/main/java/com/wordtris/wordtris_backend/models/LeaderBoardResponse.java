package com.wordtris.wordtris_backend.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LeaderBoardResponse {
    private LeaderBoard user;
    private List<LeaderBoard> leaderBoardList;

    public LeaderBoardResponse(LeaderBoard user, List<LeaderBoard> leaderBoardList) {
        this.user = user;
        this.leaderBoardList = leaderBoardList;
    }
}
