package com.wordtris.wordtris_backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Entity
@Table(name = "game_history")
public class GameHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "data")
    private LocalDateTime data;

    @Column(name = "score")
    private Integer score;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private GameUser user;

    public GameHistory(LocalDateTime data, Integer score, GameUser user) {
        this.data = data;
        this.score = score;
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public GameUser getUser() {
        return user;
    }

    public void setUser(GameUser user) {
        this.user = user;
    }

}