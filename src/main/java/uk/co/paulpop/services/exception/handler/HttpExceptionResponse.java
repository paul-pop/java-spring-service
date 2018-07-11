package uk.co.paulpop.services.exception.handler;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * This is used to map an exception to a HTTP status with a relevant message.
 */
@Data
@Builder
public class HttpExceptionResponse {

    private final String message;
    private final List<String> errors;

}
