package uk.co.paulpop.services.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger configuration class.
 */
@Configuration
@EnableSwagger2
class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage("uk.co.paulpop.services"))
            .paths(PathSelectors.any())
            .build()
            .useDefaultResponseMessages(false)
            .apiInfo(new ApiInfoBuilder()
                .title("Java Spring API")
                .description("This API is just a skeleton so it won't do much :-(")
                .version("1.0")
                .build());
    }
}
