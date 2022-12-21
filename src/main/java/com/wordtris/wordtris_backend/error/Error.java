package com.wordtris.wordtris_backend.error;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Error {
    public Error(String msg, String code) {
        this.msg = msg;
        this.code = code;
    }

    @NotNull
    private String msg;
    @NotNull
    private String code;

}
