package tourGuide.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The type Users gathering exception.
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class UsersGatheringException extends Exception {
    /**
     * Instantiates a new Users gathering exception.
     *
     * @param message the message
     */
    public UsersGatheringException(String message) {
        super(message);
    }
}
