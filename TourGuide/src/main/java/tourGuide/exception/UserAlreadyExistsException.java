package tourGuide.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The type User already exists exception.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyExistsException extends Exception {
    /**
     * Instantiates a new User already exists exception.
     *
     * @param s the s
     */
    public UserAlreadyExistsException(String s) {
        super(s);
    }
}
