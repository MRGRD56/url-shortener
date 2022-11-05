package ru.mrgrd56.go.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

public class StatusCodeException extends HttpStatusCodeException {
    public StatusCodeException(HttpStatus statusCode) {
        super(statusCode);
    }

    public StatusCodeException(HttpStatus statusCode, String statusText) {
        super(statusCode, statusText);
    }
}
