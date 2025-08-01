package appcup.uom.polaris.features.auth.data

import appcup.uom.polaris.core.domain.DataError
import appcup.uom.polaris.core.domain.Result
import appcup.uom.polaris.core.data.StaticData
import appcup.uom.polaris.features.auth.domain.User
import appcup.uom.polaris.features.auth.domain.UserRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class UserRepositoryImpl(
    private val supabaseClient: SupabaseClient
) : UserRepository {
    override suspend fun login(
        email: String,
        password: String
    ): Result<User, DataError.Local> {
        if (email.isBlank() || password.isBlank()) {
            return Result.Error(DataError.Local.EMPTY_FIELD)
        }

        if (!Regex(
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
            ).matches(email)
        ) {
            return Result.Error(DataError.Local.INVALID_EMAIL)
        }

        try {
            supabaseClient.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            return getUser()
        } catch (_: AuthRestException) {
            return Result.Error(DataError.Local.INVALID_LOGIN_CREDENTIALS)
        } catch (_: Exception) {
            return Result.Error(DataError.Local.UNKNOWN)
        }

    }

    override suspend fun register(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Result<Unit, DataError.Local> {
        if (name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            return Result.Error(DataError.Local.EMPTY_FIELD)
        }

        if (!Regex(
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
            ).matches(email)
        ) {
            return Result.Error(DataError.Local.INVALID_EMAIL)
        }
        if (password.length < 8) {
            return Result.Error(DataError.Local.PASSWORD_TOO_SHORT)
        }
        if (password != confirmPassword) {
            return Result.Error(DataError.Local.PASSWORD_MISMATCH)
        }

         try {
            val user = supabaseClient.auth.signUpWith(Email) {
                this.email = email
                this.password = password
                this.data = buildJsonObject {
                    this.put("name", name)
                }
            }
             return if (user != null) {
                 Result.Success(Unit)
             } else {
                 Result.Error(DataError.Local.UNKNOWN)
             }
        } catch (_: AuthRestException) {
            return Result.Error(DataError.Local.ACCOUNT_EXISTS)
        } catch (_: Exception) {
            return Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun confirmRegistration(email: String, otp: String): Result<User, DataError.Local> {
        if (email.isBlank() || otp.isBlank()) {
            return Result.Error(DataError.Local.EMPTY_FIELD)
        }
        try {
            supabaseClient.auth.verifyEmailOtp(type = OtpType.Email.EMAIL, email = email, token = otp)
            return getUser()
        } catch (_: AuthRestException) {
            return Result.Error(DataError.Local.INVALID_OTP)
        } catch (_: Exception) {
            return Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun changePassword(
        password: String,
        confirmPassword: String
    ): Result<Unit, DataError.Local> {
        if (password.isBlank() || confirmPassword.isBlank()) {
            return Result.Error(DataError.Local.EMPTY_FIELD)
        }
        if (password.length < 8) {
            return Result.Error(DataError.Local.PASSWORD_TOO_SHORT)
        }
        if (password != confirmPassword) {
            return Result.Error(DataError.Local.PASSWORD_MISMATCH)
        }

        try {
            supabaseClient.auth.updateUser {
                this.password = password
            }
            return Result.Success(Unit)
        } catch (_: Exception) {
            try {
                supabaseClient.auth.reauthenticate()
                return Result.Error(DataError.Local.REAUTHENTICATION_REQUIRED)
            } catch (_: Exception) {
                return Result.Error(DataError.Local.UNKNOWN)
            }
        }
    }

    override suspend fun confirmPasswordChange(
        nonce: String,
        password: String,
        confirmPassword: String
    ): Result<User, DataError.Local> {
        if (nonce.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            return Result.Error(DataError.Local.EMPTY_FIELD)
        }
        try {
            supabaseClient.auth.updateUser {
                this.password = password
                this.nonce = nonce
            }
            return getUser()
        } catch (_: Exception) {
            return Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun logout(): Result<Unit, DataError.Local> {
        try {
            supabaseClient.auth.signOut()
            StaticData.user = User(
                id = "",
                name = "",
                email = ""
            )
            return Result.Success(Unit)
        } catch (_: Exception) {
            return Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun getUser(): Result<User, DataError.Local> {
        val user = supabaseClient.auth.currentUserOrNull()
        return if (user == null) {
            Result.Error(DataError.Local.UNKNOWN)
        } else {
            Result.Success(User(
                id = user.id,
                name = user.userMetadata!!.getOrElse("name", {""}).toString().removeSurrounding("\""),
                email = user.email!!
            ))
        }
    }

    override suspend fun updateDisplayName(name: String): Result<Unit, DataError.Local> {
        if (name.isBlank()) {
            return Result.Error(DataError.Local.EMPTY_FIELD)
        }
        try {
            supabaseClient.auth.updateUser {
                data = buildJsonObject {
                    put("name", name)
                }
            }
            StaticData.user = StaticData.user.copy(name = name)
            return Result.Success(Unit)
        } catch (_: Exception) {
            return Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun forgotPassword(email: String): Result<Unit, DataError.Local> {
        if (email.isBlank()) {
            return Result.Error(DataError.Local.EMPTY_FIELD)
        }

        if (!Regex(
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
            ).matches(email)
        ) {
            return Result.Error(DataError.Local.INVALID_EMAIL)
        }

        try {
            supabaseClient.auth.resetPasswordForEmail(email)
            return Result.Success(Unit)
        } catch (_: AuthRestException) {
            return Result.Error(DataError.Local.INVALID_EMAIL)
        } catch (_: Exception) {
            return Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun resetPassword(
        password: String,
        confirmPassword: String
    ): Result<Unit, DataError.Local> {
        if (password.isBlank() || confirmPassword.isBlank()) {
            return Result.Error(DataError.Local.EMPTY_FIELD)
        }
        if (password.length < 8) {
            return Result.Error(DataError.Local.PASSWORD_TOO_SHORT)
        }
        if (password != confirmPassword) {
            return Result.Error(DataError.Local.PASSWORD_MISMATCH)
        }

        try {
            supabaseClient.auth.updateUser {
                this.password = password
            }
            return Result.Success(Unit)
        } catch (_: Exception) {
            return Result.Error(DataError.Local.UNKNOWN)
        }
    }
}