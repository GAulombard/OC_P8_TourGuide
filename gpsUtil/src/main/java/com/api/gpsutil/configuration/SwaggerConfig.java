package com.api.gpsutil.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()  //initialize ApiSelectorBuilder
                .apis(RequestHandlerSelectors.basePackage("com.api.gpsutil.controller")) //filter documentation to expose for each controller
                .paths(PathSelectors.any()) // another filter
                .build();
    }

}
