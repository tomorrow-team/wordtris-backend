package com.wordtris.wordtris_backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "score")
public class Score {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "login")
    private String login;

    @Column(name = "score")
    private Long score;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonBackReference
    @JoinColumn(name = "game_user_id",referencedColumnName = "id")
    private GameUser gameUser;

    public Score(String login, GameUser gameUser) {
        this.login = login;
        this.gameUser = gameUser;
    }

    public Score(String login, Long score, GameUser gameUser) {
        this.login = login;
        this.score = score;
        this.gameUser = gameUser;
    }
}