package com.wordtris.wordtris_backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "game_user")
public class GameUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "username", length = 100)
    private String username;

    @NotNull
    @JsonIgnore
    @Column(name = "password", length = 100)
    private String password;

    @OneToOne(mappedBy = "gameUser")
    @JsonManagedReference
    @JoinColumn
    private Score score;

    @NotNull
    @JsonIgnore
    @Enumerated(value = EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @NotNull
    @JsonIgnore
    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @NotNull
    @Column(name = "status_id")
    private Integer status_id;

    @NotNull
    @Column(name = "avatar_id")
    private Integer avatar_id;

    @NotNull
    @JsonIgnore
    @JsonManagedReference
    @OneToMany(mappedBy = "user")
    private List<GameHistory> gameHistory;

    public GameUser(String username, String password, Role role, Status status) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.status = status;
    }
}