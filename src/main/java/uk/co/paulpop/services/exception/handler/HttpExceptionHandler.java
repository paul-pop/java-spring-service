package uk.co.paulpop.services.exception.handler;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * Controller advice that will handle all defined exceptions and return the relevant {@link HttpExceptionResponse} in the HTTP response.
 */
@ControllerAdvice
public class HttpExceptionHandler {

    private static final String GENERIC_ERROR_MESSAGE = "Something went wrong";

    /**
     * Handles {@link MethodArgumentNotValidException} and returns bad request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<HttpExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getAllErrors().stream()
            .map(e -> {
                if (e instanceof FieldError) {
                    return String.format("Parameter %s %s", ((FieldError) e).getField(), e.getDefaultMessage());
                } else {
                    return String.format("Object %s %s", e.getObjectName(), e.getDefaultMessage());
                }
            })
            .collect(Collectors.toList());
        return error(BAD_REQUEST, errors);
    }

    /**
     * Handles {@link ConstraintViolationException} and returns bad request
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ResponseEntity<HttpExceptionResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations().stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.toList());
        return error(BAD_REQUEST, errors);
    }

    /**
     * Handles {@link ServletRequestBindingException} and returns bad request
     */
    @ExceptionHandler(ServletRequestBindingException.class)
    @ResponseBody
    public ResponseEntity<HttpExceptionResponse> handleServletRequestBindingException(ServletRequestBindingException ex) {
        return standardError(ex, BAD_REQUEST);
    }

    /**
     * Handles {@link MissingServletRequestPartException} and returns bad request
     */
    @ExceptionHandler(MissingServletRequestPartException.class)
    @ResponseBody
    public ResponseEntity<HttpExceptionResponse> handleMissingServletRequestPartException(MissingServletRequestPartException ex) {
        return standardError(ex, BAD_REQUEST);
    }

    /**
     * Handles {@link MethodArgumentTypeMismatchException} and returns bad request
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public ResponseEntity<HttpExceptionResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        return singleError(BAD_REQUEST, String.format("Parameter '%s' does not accept value '%s'", ex.getName(), ex.getValue()));
    }

    /**
     * Handles {@link HttpMessageNotReadableException} and returns bad request
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseEntity<HttpExceptionResponse> handleMessageNotReadableException(HttpMessageNotReadableException ex) {
        return standardError(ex, BAD_REQUEST);
    }

    /**
     * Handles {@link HttpRequestMethodNotSupportedException} and returns bad request
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ResponseEntity<HttpExceptionResponse> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        return standardError(ex, BAD_REQUEST);
    }

    /**
     * Handles the generic {@link Exception} and returns internal server error
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    @Order(Ordered.LOWEST_PRECEDENCE)
    public ResponseEntity<HttpExceptionResponse> handleException() {
        return genericError(INTERNAL_SERVER_ERROR);
    }

    /**
     * Returns a http exception response entity where the error message is driven from the exception message
     *
     * @param ex     the exception the exception
     * @param status the status
     * @return a response entity containing a message from the exception and given status
     */
    private ResponseEntity<HttpExceptionResponse> standardError(final Exception ex, final HttpStatus status) {
        return singleError(status, ex.getMessage());
    }

    /**
     * Returns a http exception response entity where the error message is generic
     *
     * @param status the status
     * @return a response entity containing a generic error message and given status
     */
    private ResponseEntity<HttpExceptionResponse> genericError(final HttpStatus status) {
        return singleError(status, GENERIC_ERROR_MESSAGE);
    }

    /**
     * Returns a http exception response entity where the error message is set to that provided
     *
     * @param status  the status
     * @param message the message to set in the response
     * @return a response entity containing the given message and given status
     */
    private ResponseEntity<HttpExceptionResponse> singleError(final HttpStatus status, final String message) {
        return error(status, Collections.singletonList(message));
    }

    /**
     * Logs the exception and returns a response entity with the given status and errors
     *
     * @param status the status to send in the response
     * @param errors the errors to put in the response
     * @return a response entity
     */
    private ResponseEntity<HttpExceptionResponse> error(final HttpStatus status, final List<String> errors) {
        return buildResponse(status, errors);
    }

    /**
     * Builds the {@link ResponseEntity} instance for given http status and errors
     *
     * @param status the http status
     * @param errors the errors
     * @return an instance of ResponseEntity
     */
    private ResponseEntity<HttpExceptionResponse> buildResponse(HttpStatus status, List<String> errors) {
        return ResponseEntity
            .status(status)
            .body(HttpExceptionResponse.builder()
                .message(status.getReasonPhrase())
                .errors(errors)
                .build());
    }
}
