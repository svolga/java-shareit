package ru.practicum.shareit.util.errors;

import lombok.Value;

@Value
public class ErrorResponse {
    String error;
    String stacktrace;

    public ErrorResponse(String error) {
        this.error = error;
        stacktrace = "";
    }

    public ErrorResponse(String error, String stacktrace) {
        this.error = error;
        this.stacktrace = stacktrace;
    }
}