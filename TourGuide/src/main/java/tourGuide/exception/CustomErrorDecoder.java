package tourGuide.exception;

import feign.Response;
import feign.codec.ErrorDecoder;


/**
 * The type Custom error decoder.
 */
public class CustomErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {

        if(response.status() == 404) {
            return new UserNotFoundException("User not found");
        }

        if(response.status() == 500) {
            return new UsersGatheringException(" User gathering exception");
        }

        if(response.status() == 409) {
            return  new UserAlreadyExistsException("User already exist");
        }

        return defaultErrorDecoder.decode(methodKey,response);
    }
}
