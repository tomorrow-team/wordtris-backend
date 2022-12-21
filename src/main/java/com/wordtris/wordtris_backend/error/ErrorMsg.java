package com.wordtris.wordtris_backend.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class ErrorMsg {

    @NotNull
    private boolean status;

    public ErrorMsg(boolean status, Error error) {
        this.status = status;
        this.error = error;
    }

    public ErrorMsg(boolean status) {
        this.status = status;
    }
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Error error;


}
