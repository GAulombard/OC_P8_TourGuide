package tourGuide;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Locale;

@SpringBootApplication
//@EnableSwagger2
@EnableFeignClients("tourGuide")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
