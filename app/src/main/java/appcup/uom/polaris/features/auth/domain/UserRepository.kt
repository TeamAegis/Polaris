package appcup.uom.polaris.features.auth.domain

import appcup.uom.polaris.core.domain.DataError
import appcup.uom.polaris.core.domain.Result

interface UserRepository {
    suspend fun login(email: String, password: String): Result<User, DataError.AuthError>
    suspend fun register(name: String,email: String, password: String, confirmPassword: String): Result<Unit, DataError.AuthError>
    suspend fun confirmRegistration(email: String, otp: String): Result<User, DataError.AuthError>
    suspend fun changePassword(password: String, confirmPassword: String): Result<Unit, DataError.AuthError>
    suspend fun confirmPasswordChange(nonce: String, password: String, confirmPassword: String): Result<User, DataError.AuthError>
    suspend fun logout(): Result<Unit, DataError.AuthError>
    suspend fun getUser(): Result<User, DataError.AuthError>
    suspend fun updateDisplayName(name: String): Result<Unit, DataError.AuthError>
    suspend fun forgotPassword(email: String): Result<Unit, DataError.AuthError>
    suspend fun resetPassword(password: String, confirmPassword: String): Result<Unit, DataError.AuthError>
    suspend fun addExperienceAndPoints(experience: Int, points: Int): Result<Unit, DataError.AuthError>
    suspend fun usePoints(points: Int): Result<Unit, DataError.AuthError>
}