public class FormatException extends RuntimeException {
    //nothing, just a wrapper to distinguish between custom and real RuntimeExceptions

    FormatException(String message) {
        super(message);
    }
}
