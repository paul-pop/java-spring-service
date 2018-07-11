package uk.co.paulpop.services.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.paulpop.services.exception.handler.HttpExceptionResponse;
import uk.co.paulpop.services.model.Hello;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

@Api("Java Spring Service API")
@RestController
@RequestMapping("/api")
class JavaSpringServiceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaSpringServiceController.class);

    @GetMapping("/{name}")
    @ResponseBody
    @ApiOperation("Says hello to the given name")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses({
        @ApiResponse(code = SC_BAD_REQUEST, message = "Bad request", response = HttpExceptionResponse.class),
        @ApiResponse(code = SC_INTERNAL_SERVER_ERROR, message = "Internal server error", response = HttpExceptionResponse.class)})
    public ResponseEntity<Hello> sayHello(final @PathVariable String name) {

        LOGGER.info("Received request to say hello to {}", name);

        return ResponseEntity.ok(Hello.builder()
            .message(String.format("Hello %s", name))
            .build());
    }

}
