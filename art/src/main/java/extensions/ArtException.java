package extensions;

import java.util.HashMap;

public class ArtException extends Exception {

    /**
     * This constructor creates a new ArtException with the given error code and original exception.
     *
     * @param code the new error code of this instance
     * @param original the original exception
     */
    public ArtException(ErrorCode code, Exception original) {

        this.code = code;
        this.original = original;
    }

    /**
     * This constructor creates a new ArtException with the given error code and original exception.
     * The custom data is used to apply additional information to the message. It is passed to the String.format() function as arguments.
     *
     * @param code the new error code of this instance
     * @param original the original exception
     * @param customData additional data that is passed to the String.format() function (for more detailed error messages)
     */
    public ArtException(ErrorCode code, Exception original, Object[] customData) {

        this(code, original);
        this.customData = customData;
    }

    // ================================
    //        CUSTOM ERROR CODE
    // ================================

    public enum ErrorCode {

        // all error codes
        UnregisteredLocalDatabaseUser,
        WrongLocalDatabaseLoginCredentials,
        Unknown
        ;

        // all error messages
        static final HashMap<ErrorCode, String> errorMessages = new HashMap<>();
        static {
            errorMessages.put(ErrorCode.UnregisteredLocalDatabaseUser, "User is not registered in the local database!");
            errorMessages.put(ErrorCode.WrongLocalDatabaseLoginCredentials, "Wrong username or password or local database login!");
            errorMessages.put(ErrorCode.Unknown, "Unknown error!");
        }

        @Override
        public String toString() {


            return errorMessages.get(this);
        }
    }

    private ErrorCode code;
    private Exception original;
    private Object[] customData = null;

    // ================================
    //      CUSTOM ERROR MESSAGE
    // ================================

    public ErrorCode getErrorCode() {
        return code;
    }

    @Override
    public String getMessage() {
        // use toString() method
        return this.toString();
    }

    @Override
    public String toString() {
        // return the String representation of the error code
        String errorCodeMessage = (code != null) ? code.toString() : ErrorCode.Unknown.toString();
        return (customData != null) ? String.format(errorCodeMessage, customData) : errorCodeMessage;
    }
}
