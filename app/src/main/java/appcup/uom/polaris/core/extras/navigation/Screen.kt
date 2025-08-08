package appcup.uom.polaris.core.extras.navigation

import androidx.navigation3.runtime.NavKey
import appcup.uom.polaris.features.conversational_ai.domain.Value
import appcup.uom.polaris.features.conversational_ai.utils.function_call.FunctionCallNavigation
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen() : FunctionCallNavigation, NavKey {
    @Serializable
    object Start : Screen() {
        override fun getScreenDetails(): Value {
            return Value.Object(
                values = arrayOf(
                    "name" to Value.Str("Start"),
                    "description" to Value.Str("This is the starting screen of the app. It should not be navigated to."),
                    "navigation_arguments" to Value.Null
                )
            )

        }
    }

    @Serializable
    object Login : Screen() {
        override fun getScreenDetails(): Value {
            return Value.Object(
                values = arrayOf(
                    "name" to Value.Str("Login"),
                    "description" to Value.Str("This is the login screen of the app. It should not be navigated to."),
                    "navigation_arguments" to Value.Null
                )
            )
        }
    }

    @Serializable
    object Register : Screen() {
        override fun getScreenDetails(): Value {
            return Value.Object(
                values = arrayOf(
                    "name" to Value.Str("Register"),
                    "description" to Value.Str("This is the register screen of the app. It should not be navigated to."),
                    "navigation_arguments" to Value.Null
                )
            )
        }
    }

    @Serializable
    data class OtpConfirmRegistration(
        val email: String,
    ) : Screen() {
        override fun getScreenDetails(): Value {
            return Value.Object(
                values = arrayOf(
                    "name" to Value.Str("OtpConfirmRegistration"),
                    "description" to Value.Str("This is the OTP confirmation screen of the app. It should not be navigated to."),
                    "navigation_arguments" to Value.Object(
                        values = arrayOf(
                            "email" to Value.Object(
                                values = arrayOf(
                                    "type" to Value.Str("STRING"),
                                    "description" to Value.Str("The email address associated with the account being confirmed."),
                                )
                            )
                        )
                    )
                )
            )
        }
    }

    @Serializable
    data class OtpReauthenticate(
        val password: String,
        val confirmPassword: String
    ) : Screen() {
        override fun getScreenDetails(): Value {
            return Value.Object(
                values = arrayOf(
                    "name" to Value.Str("OtpReauthenticate"),
                    "description" to Value.Str("This is the OTP reauthentication screen of the app. It should not be navigated to."),
                    "navigation_arguments" to Value.Object(
                        values = arrayOf(
                            "email" to Value.Object(
                                values = arrayOf(
                                    "type" to Value.Str("STRING"),
                                    "description" to Value.Str("The email address associated with the account being reauthenticated."),
                                ),
                            ),
                            "password" to Value.Object(
                                values = arrayOf(
                                    "type" to Value.Str("STRING"),
                                    "description" to Value.Str("The password associated with the account being reauthenticated."),
                                ),
                            ),
                            "confirmPassword" to Value.Object(
                                values = arrayOf(
                                    "type" to Value.Str("STRING"),
                                    "description" to Value.Str("The password associated with the account being reauthenticated."),
                                ),
                            ),
                        )
                    )
                )
            )

        }
    }

    @Serializable
    object ForgotPassword : Screen() {
        override fun getScreenDetails(): Value {
            return Value.Object(
                values = arrayOf(
                    "name" to Value.Str("ForgotPassword"),
                    "description" to Value.Str("This is the forgot password screen of the app. It should not be navigated to."),
                    "navigation_arguments" to Value.Null
                )
            )
        }
    }

    @Serializable
    object Map : Screen() {
        override fun getScreenDetails(): Value {
            return Value.Object(
                values = arrayOf(
                    "name" to Value.Str("Map"),
                    "description" to Value.Str("This is the map screen of the app."),
                    "navigation_arguments" to Value.Null
                )
            )
        }
    }

    @Serializable
    object Memories : Screen() {
        override fun getScreenDetails(): Value {
            return Value.Object(
                values = arrayOf(
                    "name" to Value.Str("Memories"),
                    "description" to Value.Str("This is the memories screen of the app."),
                    "navigation_arguments" to Value.Null
                )
            )
        }
    }

    @Serializable
    object Settings : Screen() {
        override fun getScreenDetails(): Value {
            return Value.Object(
                values = arrayOf(
                    "name" to Value.Str("Settings"),
                    "description" to Value.Str("This is the settings screen of the app."),
                    "navigation_arguments" to Value.Null
                )
            )
        }
    }

    @Serializable
    object More : Screen() {
        override fun getScreenDetails(): Value {
            return Value.Object(
                values = arrayOf(
                    "name" to Value.Str("More"),
                    "description" to Value.Str("This is the more screen of the app."),
                    "navigation_arguments" to Value.Object(
                        values = arrayOf(
                            "email" to Value.Object(
                                values = arrayOf(
                                    "type" to Value.Str("STRING"),
                                    "description" to Value.Str("The email address associated with the account."),
                                )
                            )
                        )
                    )
                )
            )
        }
    }

    @Serializable
    object CreateJourney : Screen() {
        override fun getScreenDetails(): Value {
            return Value.Object(
                values = arrayOf(
                    "name" to Value.Str("CreateJourney"),
                    "description" to Value.Str("This is the create journey screen of the app."),
                    "navigation_arguments" to Value.Null
                )
            )
        }
    }

    @Serializable
    object ChangeDisplayName : Screen() {
        override fun getScreenDetails(): Value {
            return Value.Object(
                values = arrayOf(
                    "name" to Value.Str("ChangeDisplayName"),
                    "description" to Value.Str("This is the change display name screen of the app."),
                    "navigation_arguments" to Value.Null
                )
            )
        }
    }

    @Serializable
    object ResetPassword : Screen() {
        override fun getScreenDetails(): Value {
            return Value.Object(
                values = arrayOf(
                    "name" to Value.Str("ResetPassword"),
                    "description" to Value.Str("This is the reset password screen of the app. It should not be navigated to."),
                    "navigation_arguments" to Value.Null
                )
            )
        }
    }

    @Serializable
    object ChangePassword : Screen() {
        override fun getScreenDetails(): Value {
            return Value.Object(
                values = arrayOf(
                    "name" to Value.Str("ChangePassword"),
                    "description" to Value.Str("This is the change password screen of the app. It should not be navigated to."),
                    "navigation_arguments" to Value.Null
                )
            )
        }
    }

    @Serializable
    object LiveTranslate : Screen() {
        override fun getScreenDetails(): Value {
            return Value.Object(
                values = arrayOf(
                    "name" to Value.Str("LiveTranslate"),
                    "description" to Value.Str("This is the live translate screen of the app. It should not be navigated to."),
                    "navigation_arguments" to Value.Null
                )
            )
        }
    }

    @Serializable
    object Chat : Screen() {
        override fun getScreenDetails(): Value {
            return Value.Object(
                values = arrayOf(
                    "name" to Value.Str("Chat"),
                    "description" to Value.Str("This is the chat screen of the app. It should not be navigated to."),
                    "navigation_arguments" to Value.Null
                )
            )
        }
    }
}

