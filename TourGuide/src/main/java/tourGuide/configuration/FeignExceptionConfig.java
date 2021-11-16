package tourGuide.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tourGuide.exception.CustomErrorDecoder;

/**
 * The type Feign exception config.
 */
@Configuration
public class FeignExceptionConfig {

    /**
     * Custom error decoder custom error decoder.
     *
     * @return the custom error decoder
     */
    @Bean
    public CustomErrorDecoder customErrorDecoder() {
        return new CustomErrorDecoder();
    }

}
