package com.wordtris.wordtris_backend.error;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public interface Handler {
    void handle_response(HttpServletResponse response, Exception exception) throws IOException;
}
