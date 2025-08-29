# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class appcup.uom.polaris.core.domain.** { *; }
-keep class appcup.uom.polaris.core.extras.navigation.** { *; }
-keep class appcup.uom.polaris.core.extras.transport.** { *; }
-keep class appcup.uom.polaris.features.auth.presentation.otp_confirm_registration.OtpConfirmRegistrationNavArgs
-keep class appcup.uom.polaris.features.auth.presentation.otp_reauthenticate.OtpReauthenticateNavArgs
-keep class appcup.uom.polaris.features.chat.domain.** { *; }
-keep class appcup.uom.polaris.features.conversational_ai.domain.** { *; }
-keep class appcup.uom.polaris.features.polaris.domain.** { *; }
-keep class appcup.uom.polaris.features.qr_code_analyzer.** { *; }
-keep class appcup.uom.polaris.features.quest.domain.** { *; }