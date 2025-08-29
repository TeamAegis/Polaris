package appcup.uom.polaris.core.presentation.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import appcup.uom.polaris.core.data.Constants
import appcup.uom.polaris.core.data.EventBus
import appcup.uom.polaris.core.data.StaticData
import appcup.uom.polaris.core.domain.DataError
import appcup.uom.polaris.core.domain.Event
import appcup.uom.polaris.core.domain.Result
import appcup.uom.polaris.features.auth.domain.UserRepository
import appcup.uom.polaris.features.conversational_ai.domain.Value.Str
import appcup.uom.polaris.features.quest.domain.QuestRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userRepository: UserRepository,
    private val prefs: DataStore<Preferences>,
    private val questRepository: QuestRepository
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<SettingsEvent>()
    val event = _event.asSharedFlow()

    init {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    theme = StaticData.appTheme,
                )
            }

            EventBus.collectEvents { action ->
                when (action) {
                    is Event.OnThemeChange -> {
                        viewModelScope.launch {
                            prefs.edit {
                                val themeKey = stringPreferencesKey(Constants.PREFERENCES_THEME)
                                it[themeKey] = action.appTheme.name
                            }
                        }
                        _state.update {
                            it.copy(
                                theme = action.appTheme,
                            )
                        }

                        action.onResult(mapOf("result" to Str("success")))
                    }

                    else -> {}
                }

            }
        }
    }

    fun onAction(action: SettingsAction) {
        when (action) {
            SettingsAction.OnRefreshList -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            isRefreshingQuestList = true,
                        )
                    }
                    questRepository.createQuests()
                    _state.update {
                        it.copy(
                            isRefreshingQuestList = false,
                        )
                    }
                }
            }

            is SettingsAction.OnThemeChanged -> {
                viewModelScope.launch {
                    prefs.edit {
                        val themeKey = stringPreferencesKey(Constants.PREFERENCES_THEME)
                        it[themeKey] = action.theme.name
                    }
                }
                _state.update {
                    it.copy(
                        theme = action.theme,
                    )
                }
            }

            is SettingsAction.OnThemeBottomSheetToggled -> {
                _state.update {
                    it.copy(
                        isThemeBottomSheetVisible = action.show,
                    )
                }
            }

            SettingsAction.OnLogoutClicked -> {
                _state.update {
                    it.copy(
                        isLoading = true,
                    )
                }
                viewModelScope.launch {
                    val res = userRepository.logout()
                    _state.update {
                        it.copy(
                            isLoading = false,
                        )
                    }
                    when (res) {
                        is Result.Error<DataError.AuthError> -> {
                            _event.emit(SettingsEvent.Error(res.error.message))
                        }

                        else -> {}
                    }
                }
            }

            else -> {}
        }
    }
}