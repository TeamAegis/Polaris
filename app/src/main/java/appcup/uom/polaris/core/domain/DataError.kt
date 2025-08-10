package appcup.uom.polaris.core.domain

sealed interface DataError: Error {
    enum class Remote(val message: String) : DataError {
        REQUEST_TIMEOUT("The connection timed out. Please try again."),
        TOO_MANY_REQUESTS("You've made too many requests. Please wait a moment and try again."),
        NO_INTERNET("No internet connection. Please check your network settings."),
        SERVER("A server error occurred. Please try again later."),
        SERIALIZATION("There was an issue processing data. Please try again."),
        UNKNOWN("An unknown error occurred. Please try again.")
    }

    enum class GenericError(val message: String) : DataError {
        UNKNOWN("An unknown error occurred."),
        EMPTY_FIELD("Please fill in all fields."),
    }

    enum class JourneyError(val message: String) : DataError {
        UNKNOWN("An unknown error occurred."),
        NAME_EMPTY("Please enter a name for your journey."),
        DESCRIPTION_EMPTY("Please enter a description for your journey."),
        END_LOCATION_NOT_SET("Please set an end location for your journey."),
    }

    enum class ChatError(val message: String) : DataError {
        UNKNOWN("An unknown error occurred."),
        MESSAGE_EMPTY("Message cannot be empty."),
    }

    enum class AuthError(val message: String) : DataError {
        UNKNOWN("An unknown error occurred."),
        EMPTY_FIELD("Please fill in all fields."),
        ACCOUNT_EXISTS("An account with this email already exists."),
        REAUTHENTICATION_REQUIRED("Please reauthenticate to continue."),
        INVALID_LOGIN_CREDENTIALS("Invalid login credentials."),
        INVALID_OTP("Invalid OTP."),
        INVALID_EMAIL("Please enter a valid email address."),
        PASSWORD_TOO_SHORT("Password must be at least 8 characters long."),
        PASSWORD_MISMATCH("Passwords do not match.")
    }
}