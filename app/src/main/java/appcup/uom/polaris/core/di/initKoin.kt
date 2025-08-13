package appcup.uom.polaris.core.di

import appcup.uom.polaris.features.auth.di.AuthModule
import appcup.uom.polaris.features.chat.di.ChatModule
import appcup.uom.polaris.features.conversational_ai.di.ConversationalAIModule
import appcup.uom.polaris.core.di.MemoryModule
import appcup.uom.polaris.features.polaris.di.PolarisModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.ksp.generated.module

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            AppModule().module,
            AuthModule().module,
            ConversationalAIModule().module,
            PolarisModule().module,
            ChatModule().module,
            NetworkModule().module,
            MemoryModule().module
        )
    }
}