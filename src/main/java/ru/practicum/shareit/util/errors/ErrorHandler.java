package ru.practicum.shareit.util.errors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.util.exceptions.AccessIsNotAllowedException;
import ru.practicum.shareit.util.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.util.exceptions.DateTimeException;
import ru.practicum.shareit.util.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.util.exceptions.UnavailableItemException;
import ru.practicum.shareit.util.exceptions.UnsupportedStatusException;


import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.io.PrintWriter;
import java.io.StringWriter;

@RestControllerAdvice("ru.practicum.shareit")
@Slf4j
public class ErrorHandler {
    @ExceptionHandler({ObjectNotFoundException.class, EntityNotFoundException.class,
            AccessIsNotAllowedException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundId(final RuntimeException e) {
        return new ErrorResponse("Объект не найден: " + e.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailAlreadyExists(final RuntimeException e) {
        return new ErrorResponse("E-mail уже существует " + e.getMessage());
    }

    @ExceptionHandler({ValidationException.class,
            ConstraintViolationException.class, MethodArgumentNotValidException.class,
            DateTimeException.class, UnavailableItemException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleFailValidation(final RuntimeException e) {
        return new ErrorResponse("Ошибка валидации: " + e.getMessage());
    }

    @ExceptionHandler({UnsupportedStatusException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnsupportedBookingStatus(final RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnknownError(final Exception e) {
        log.error("500 Unknown error --> {}, -> {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage(), getStackTrace(e));
    }

    private String getStackTrace(Exception  e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        return pw.toString();
    }

}