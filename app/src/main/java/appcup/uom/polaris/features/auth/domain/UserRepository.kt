package appcup.uom.polaris.features.auth.domain

import appcup.uom.polaris.core.domain.DataError
import appcup.uom.polaris.core.domain.Result

interface UserRepository {
    suspend fun login(email: String, password: String): Result<User, DataError.Local>
    suspend fun register(name: String,email: String, password: String, confirmPassword: String): Result<Unit, DataError.Local>
    suspend fun confirmRegistration(email: String, otp: String): Result<User, DataError.Local>
    suspend fun changePassword(password: String, confirmPassword: String): Result<Unit, DataError.Local>
    suspend fun confirmPasswordChange(nonce: String, password: String, confirmPassword: String): Result<User, DataError.Local>
    suspend fun logout(): Result<Unit, DataError.Local>
    suspend fun getUser(): Result<User, DataError.Local>
    suspend fun updateDisplayName(name: String): Result<Unit, DataError.Local>
    suspend fun forgotPassword(email: String): Result<Unit, DataError.Local>
    suspend fun resetPassword(password: String, confirmPassword: String): Result<Unit, DataError.Local>
}