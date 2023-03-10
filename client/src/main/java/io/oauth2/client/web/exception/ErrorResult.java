package io.oauth2.client.web.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResult {
    private String code;
    private String message;

    public ErrorResult(String code) {
        this.code = code;
    }
}
