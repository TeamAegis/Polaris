package appcup.uom.polaris.features.chat.di

import appcup.uom.polaris.core.data.Constants
import appcup.uom.polaris.features.chat.data.ChatRepositoryImpl
import appcup.uom.polaris.features.chat.domain.ChatRepository
import appcup.uom.polaris.features.chat.presentation.chat.ChatViewModel
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.generationConfig
import io.github.jan.supabase.SupabaseClient
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class ChatModule {
    @Single
    fun provideChatRepository(supabaseClient: SupabaseClient): ChatRepository = ChatRepositoryImpl(
        supabaseClient = supabaseClient,
        generativeModel = Firebase.ai(backend = GenerativeBackend.Companion.googleAI())
            .generativeModel(
                modelName = Constants.GEMINI_API_MODEL,
                generationConfig = generationConfig {
                    responseModalities = listOf(ResponseModality.Companion.TEXT)
                }
            )
    )

    @KoinViewModel
    fun provideChatViewModel(chatRepository: ChatRepository) =
        ChatViewModel(chatRepository = chatRepository)
}