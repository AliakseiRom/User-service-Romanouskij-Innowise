package user_service.user_service.exceptions;

public class UserWithEmailAlreadyExists extends CommonException {
    public UserWithEmailAlreadyExists(String message) {
        super(message);
    }
}
