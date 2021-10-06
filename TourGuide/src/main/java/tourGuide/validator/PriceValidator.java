package tourGuide.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tourGuide.model.UserPreferences;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PriceValidator implements ConstraintValidator<PriceCheck, UserPreferences> {

    private Logger logger = LoggerFactory.getLogger(PriceValidator.class);

    @Override
    public boolean isValid(UserPreferences userPreferences, ConstraintValidatorContext context) {

        double highPrice = userPreferences.getHighPricePoint().getNumber().doubleValueExact();
        double lowPrice = userPreferences.getLowerPricePoint().getNumber().doubleValueExact();

        if(highPrice > lowPrice) return true;
        else return false;

    }
}
