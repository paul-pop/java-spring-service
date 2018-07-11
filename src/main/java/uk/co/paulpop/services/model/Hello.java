package uk.co.paulpop.services.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Hello {

    private final String message;

}
