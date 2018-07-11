package uk.co.paulpop.services.exception.handler;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;

public class HttpExceptionHandlerTest {

    private static final String GENERIC_ERROR_MESSAGE = "Something went wrong";
    private static final String MESSAGE = "message";
    private static final String OBJECT_NAME = "name3";
    private static final String OBJECT_ERROR_MESSAGE = "is null";

    private static final FieldError FE_1 = new FieldError("name1", "field1", "is empty");
    private static final FieldError FE_2 = new FieldError("name2", "field2", "is null");
    private static final ObjectError OE = new ObjectError(OBJECT_NAME, OBJECT_ERROR_MESSAGE);

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private MethodParameter methodParameter;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private ConstraintViolation constraintViolation1;

    @Mock
    private ConstraintViolation constraintViolation2;

    @InjectMocks
    private HttpExceptionHandler handler;

    @Before
    public void setUp() {
        handler = new HttpExceptionHandler();
    }

    @Test
    public void handleMethodArgumentNotValidException_withSingleField_returns400() {
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(FE_1));

        ResponseEntity<HttpExceptionResponse> result = handler.handleMethodArgumentNotValidException(
            new MethodArgumentNotValidException(methodParameter, bindingResult)
        );

        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
        assertThat(result.getStatusCodeValue(), is(equalTo(400)));
        assertThat(result.getBody().getMessage(), is(equalTo("Bad Request")));
        assertThat(result.getBody().getErrors(), hasItems("Parameter field1 is empty"));
    }

    @Test
    public void handleMethodArgumentNotValidException_withMultipleFields_returns400() {
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(FE_1, FE_2));

        ResponseEntity<HttpExceptionResponse> result = handler.handleMethodArgumentNotValidException(
            new MethodArgumentNotValidException(methodParameter, bindingResult)
        );

        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
        assertThat(result.getStatusCodeValue(), is(equalTo(400)));
        assertThat(result.getBody().getMessage(), is(equalTo("Bad Request")));
        assertThat(result.getBody().getErrors(), hasItems(
            "Parameter field1 is empty",
            "Parameter field2 is null"
        ));
    }

    @Test
    public void handleMethodArgumentNotValidException_for_object_returns400() {
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(OE));

        ResponseEntity<HttpExceptionResponse> result = handler.handleMethodArgumentNotValidException(
            new MethodArgumentNotValidException(methodParameter, bindingResult)
        );

        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
        assertThat(result.getStatusCodeValue(), is(equalTo(400)));
        assertThat(result.getBody().getMessage(), is(equalTo("Bad Request")));
        assertThat(result.getBody().getErrors(), hasItems(
            String.format("Object %s %s", OBJECT_NAME, OBJECT_ERROR_MESSAGE)
        ));
    }

    @Test
    public void handleConstraintViolationException_withSingleField_returns400() {
        Set<ConstraintViolation<?>> constraintViolations = new HashSet<>();
        constraintViolations.add(constraintViolation1);

        when(constraintViolation1.getMessage()).thenReturn("ConstraintViolation 1");

        ResponseEntity<HttpExceptionResponse> result = handler.handleConstraintViolationException(
            new ConstraintViolationException("ConstraintViolationException", constraintViolations)
        );

        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
        assertThat(result.getStatusCodeValue(), is(equalTo(400)));
        assertThat(result.getBody().getMessage(), is(equalTo("Bad Request")));
        assertThat(result.getBody().getErrors(), hasItems("ConstraintViolation 1"));
    }

    @Test
    public void handleConstraintViolationException_withMultipleFields_returns400() {
        Set<ConstraintViolation<?>> constraintViolations = new HashSet<>();
        constraintViolations.add(constraintViolation1);
        constraintViolations.add(constraintViolation2);

        when(constraintViolation1.getMessage()).thenReturn("ConstraintViolation 1");
        when(constraintViolation2.getMessage()).thenReturn("ConstraintViolation 2");

        ResponseEntity<HttpExceptionResponse> result = handler.handleConstraintViolationException(
            new ConstraintViolationException("ConstraintViolationException", constraintViolations)
        );
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
        assertThat(result.getStatusCodeValue(), is(equalTo(400)));
        assertThat(result.getBody().getMessage(), is(equalTo("Bad Request")));
        assertThat(result.getBody().getErrors(), hasItems(
            "ConstraintViolation 1",
            "ConstraintViolation 2"
        ));
    }

    @Test
    public void handleServletRequestBindingException_withCustomMessage_returns400WithCustomMessage() {
        ResponseEntity<HttpExceptionResponse> result = handler.handleServletRequestBindingException(
            new ServletRequestBindingException("Custom message")
        );

        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
        assertThat(result.getStatusCodeValue(), is(equalTo(400)));
        assertThat(result.getBody().getMessage(), is(equalTo("Bad Request")));
        assertThat(result.getBody().getErrors(), hasItems("Custom message"));
    }

    @Test
    public void handleMissingServletRequestPartException_shouldReturnCorrectResponse() {
        ResponseEntity<HttpExceptionResponse> result = handler.handleMissingServletRequestPartException(new MissingServletRequestPartException("part"));

        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
        assertThat(result.getStatusCodeValue(), is(equalTo(400)));
        assertThat(result.getBody(), is(equalTo(HttpExceptionResponse.builder()
            .message("Bad Request")
            .errors(Collections.singletonList("Required request part 'part' is not present"))
            .build())));
    }

    @Test
    public void handleMethodArgumentTypeMismatchException_shouldReturnCorrectResponse() {
        ResponseEntity<HttpExceptionResponse> result = handler.handleMethodArgumentTypeMismatchException(
            new MethodArgumentTypeMismatchException("MismatchValue",
                StubEnum.class,
                "parameter",
                methodParameter,
                new Throwable())
        );

        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
        assertThat(result.getStatusCodeValue(), is(equalTo(400)));
        assertThat(result.getBody(), is(equalTo(HttpExceptionResponse.builder()
            .message("Bad Request")
            .errors(Collections.singletonList("Parameter 'parameter' does not accept value 'MismatchValue'"))
            .build())));
    }

    /**
     * Tests for {@link HttpExceptionHandler#handleException()}
     */
    @Test
    public void handleParentException_shouldReturnCorrectResponse() {
        ResponseEntity<HttpExceptionResponse> result = handler.handleException();

        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.INTERNAL_SERVER_ERROR)));
        assertThat(result.getStatusCodeValue(), is(equalTo(500)));
        assertThat(result.getBody().getMessage(), is(equalTo(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())));
        assertThat(result.getBody().getErrors(), hasItems(GENERIC_ERROR_MESSAGE));
    }

    @Test
    public void handleMessageNotReadableException_shouldReturnCorrectResponse() {
        ResponseEntity<HttpExceptionResponse> result = handler.handleMessageNotReadableException(new HttpMessageNotReadableException(MESSAGE));

        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
        assertThat(result.getStatusCodeValue(), is(equalTo(400)));
        assertThat(result.getBody().getMessage(), is(equalTo(HttpStatus.BAD_REQUEST.getReasonPhrase())));
        assertThat(result.getBody().getErrors(), hasItems(MESSAGE));
    }

    @Test
    public void handleMethodNotSupportedException_shouldReturnCorrectResponse() {
        ResponseEntity<HttpExceptionResponse> result = handler.handleMethodNotSupportedException(new HttpRequestMethodNotSupportedException(MESSAGE, null, MESSAGE));

        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
        assertThat(result.getStatusCodeValue(), is(equalTo(400)));
        assertThat(result.getBody().getMessage(), is(equalTo(HttpStatus.BAD_REQUEST.getReasonPhrase())));
        assertThat(result.getBody().getErrors(), hasItems(MESSAGE));
    }

    private enum StubEnum {}
}
