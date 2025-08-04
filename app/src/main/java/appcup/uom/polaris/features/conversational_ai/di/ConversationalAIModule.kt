package appcup.uom.polaris.features.conversational_ai.di

import appcup.uom.polaris.features.conversational_ai.data.ConversationalAI
import appcup.uom.polaris.features.conversational_ai.presentation.ConversationalAIViewModel
import appcup.uom.polaris.features.conversational_ai.utils.PermissionBridge
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class ConversationalAIModule {
    @Single
    fun providePermissionsBridge() = PermissionBridge()

    @Single
    fun provideConversationalAI() = ConversationalAI()

    @KoinViewModel
    fun conversationalAIViewModel(
        conversationalAI: ConversationalAI,
        permissionBridge: PermissionBridge
    ) =
        ConversationalAIViewModel(
            conversationalAI = conversationalAI,
            permissionBridge = permissionBridge
        )

}