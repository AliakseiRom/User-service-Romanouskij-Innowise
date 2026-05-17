package user_service.user_service.exceptions;

public class PaymentCardLimitExceededException extends CommonException {
    public PaymentCardLimitExceededException(String message) {
        super(message);
    }
}
