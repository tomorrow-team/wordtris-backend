package com.wordtris.wordtris_backend.models;

public enum Permission {
    GET_USERS("get_users"),
    GET_LEADERBOARD("get_leaderboard"),
    GET_AUTH_USER("get_auth_user"),
    SET_SCORE("set_score");
    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
