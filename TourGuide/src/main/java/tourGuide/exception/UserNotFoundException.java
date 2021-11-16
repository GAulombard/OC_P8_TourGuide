package tourGuide.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The type User not found exception.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends Exception {

    /**
     * Instantiates a new User not found exception.
     *
     * @param s the s
     */
    public UserNotFoundException(String s) {
        super(s);
    }
}
