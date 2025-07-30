package appcup.uom.polaris.core.presentation.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import appcup.uom.polaris.core.data.StaticData
import appcup.uom.polaris.features.auth.domain.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionSource
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppViewModel(
    private val supabaseClient: SupabaseClient
): ViewModel() {
    private val _state = MutableStateFlow(AppState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            supabaseClient.auth.sessionStatus.collect { status ->
                when (status) {
                    is SessionStatus.Authenticated -> {
                        when (status.source) {
                            SessionSource.External -> {}
                            else -> {
                                _state.update {
                                    it.copy(isAuthenticated = true)
                                }
                                setUser(status.session.user!!)
                            }
                        }
                    }

                    is SessionStatus.NotAuthenticated -> {
                        _state.update {
                            it.copy(isAuthenticated = false)
                        }
                    }
                    is SessionStatus.Initializing -> {}
                    is SessionStatus.RefreshFailure -> {}
                }
            }
        }
    }

    fun onAction(action: AppAction) {
        when (action) {

        }
    }

    private fun setUser(user: UserInfo) {
        StaticData.user = User(
            id = user.id,
            name = user.userMetadata!!.getOrElse("name") { "" }.toString()
                .removeSurrounding("\""),
            email = user.email!!
        )
    }


}