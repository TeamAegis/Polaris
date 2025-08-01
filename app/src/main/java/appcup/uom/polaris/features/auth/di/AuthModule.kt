package appcup.uom.polaris.features.auth.di

import appcup.uom.polaris.features.auth.data.UserRepositoryImpl
import appcup.uom.polaris.features.auth.domain.UserRepository
import appcup.uom.polaris.features.auth.presentation.change_password.ChangePasswordViewModel
import appcup.uom.polaris.features.auth.presentation.display_name.DisplayNameViewModel
import appcup.uom.polaris.features.auth.presentation.forgot_password.ForgotPasswordViewModel
import appcup.uom.polaris.features.auth.presentation.login.LoginViewModel
import appcup.uom.polaris.features.auth.presentation.otp_confirm_registration.OtpConfirmRegistrationNavArgs
import appcup.uom.polaris.features.auth.presentation.otp_confirm_registration.OtpConfirmRegistrationViewModel
import appcup.uom.polaris.features.auth.presentation.otp_reauthenticate.OtpReauthenticateNavArgs
import appcup.uom.polaris.features.auth.presentation.otp_reauthenticate.OtpReauthenticateViewModel
import appcup.uom.polaris.features.auth.presentation.register.RegisterViewModel
import appcup.uom.polaris.features.auth.presentation.reset_password.ResetPasswordViewModel
import io.github.jan.supabase.SupabaseClient
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Module

@Module
class AuthModule {
    @KoinViewModel
    fun loginViewModel(userRepository: UserRepository) = LoginViewModel(userRepository)
    @KoinViewModel
    fun registerViewModel(userRepository: UserRepository) = RegisterViewModel(userRepository)
    @KoinViewModel
    fun otpConfirmRegistrationViewModel(@InjectedParam args: OtpConfirmRegistrationNavArgs, userRepository: UserRepository) =
        OtpConfirmRegistrationViewModel(args = args, userRepository = userRepository)
    @KoinViewModel
    fun forgotPasswordViewModel(userRepository: UserRepository) = ForgotPasswordViewModel(userRepository)
    @KoinViewModel
    fun resetPasswordViewModel(userRepository: UserRepository) = ResetPasswordViewModel(userRepository)

    @KoinViewModel
    fun changePasswordViewModel(userRepository: UserRepository) = ChangePasswordViewModel(userRepository)
    @KoinViewModel
    fun otpReauthenticateViewModel(@InjectedParam args: OtpReauthenticateNavArgs, userRepository: UserRepository) =
        OtpReauthenticateViewModel(args = args , userRepository = userRepository)
    @KoinViewModel
    fun displayNameViewModel(userRepository: UserRepository) = DisplayNameViewModel(userRepository)
    @Factory
    fun userRepository(supabaseClient: SupabaseClient): UserRepository = UserRepositoryImpl(supabaseClient)

}