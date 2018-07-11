package uk.co.paulpop.services.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.paulpop.services.JavaSpringServiceApplication;
import uk.co.paulpop.services.exception.handler.HttpExceptionResponse;
import uk.co.paulpop.services.model.Hello;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = JavaSpringServiceApplication.class)
@RunWith(SpringRunner.class)
public class JavaSpringServiceControllerIT {

    private final HttpHeaders headers = new HttpHeaders();

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setUp() {
        headers.setContentType(APPLICATION_JSON_UTF8);
    }

    @Test
    public void whenGetIsCalledWithNoPathParam_thenReturnNotFound() {
        ResponseEntity<HttpExceptionResponse> response = restTemplate.exchange(
            createURI("/api/"),
            HttpMethod.GET,
            new HttpEntity(headers),
            HttpExceptionResponse.class);

        assertThat(response.getStatusCode(), equalTo(NOT_FOUND));
    }

    @Test
    public void whenGetIsCalledWithSingleCharPathParam_thenReturnHello() {
        ResponseEntity<Hello> response = restTemplate.exchange(
            createURI("/api/P"),
            HttpMethod.GET,
            new HttpEntity(headers),
            Hello.class);

        assertThat(response.getStatusCode(), equalTo(OK));
        assertThat(response.getBody(), equalTo(Hello.builder().message("Hello P").build()));
    }

    @Test
    public void whenGetIsCalledWithSingleSpacePathParam_thenReturnHello() {
        ResponseEntity<Hello> response = restTemplate.exchange(
            createURI("/api/ "),
            HttpMethod.GET,
            new HttpEntity(headers),
            Hello.class);

        assertThat(response.getStatusCode(), equalTo(OK));
        assertThat(response.getBody(), equalTo(Hello.builder().message("Hello  ").build()));
    }

    @Test
    public void whenGetIsCalledWithMultiWordPathParam_thenReturnHello() {
        ResponseEntity<Hello> response = restTemplate.exchange(
            createURI("/api/Paul Pop"),
            HttpMethod.GET,
            new HttpEntity(headers),
            Hello.class);

        assertThat(response.getStatusCode(), equalTo(OK));
        assertThat(response.getBody(), equalTo(Hello.builder().message("Hello Paul Pop").build()));
    }

    private String createURI(String uri) {
        return "http://localhost:" + port + uri;
    }

}
