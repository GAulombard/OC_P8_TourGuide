package tourGuide.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD,ElementType.ANNOTATION_TYPE,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PriceValidator.class)
@Documented
public @interface PriceCheck {

    String message() default "High price should higher than low price";


    Class<?>[] groups() default {}; //boilerplate


    Class<? extends Payload>[] payload() default {}; //boilerplate

}
