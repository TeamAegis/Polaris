package appcup.uom.polaris.core.presentation.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import appcup.uom.polaris.core.domain.DataError
import appcup.uom.polaris.core.domain.Event
import appcup.uom.polaris.core.domain.Result
import appcup.uom.polaris.core.data.Constants
import appcup.uom.polaris.core.data.EventBus
import appcup.uom.polaris.core.data.StaticData
import appcup.uom.polaris.features.auth.domain.UserRepository
import appcup.uom.polaris.features.conversational_ai.domain.Value.Str
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userRepository: UserRepository,
    private val prefs: DataStore<Preferences>
): ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<SettingsEvent>()
    val event = _event.asSharedFlow()

    init {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    theme = StaticData.appTheme,
                    isAmoled = StaticData.isAmoled,
                    themeColor = StaticData.seedColor,
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

                    is Event.OnSeedColorChange -> {
                        viewModelScope.launch {
                            prefs.edit {
                                val themeColor = stringPreferencesKey(Constants.PREFERENCES_THEME_COLOR)
                                it[themeColor] = action.seedColor.name
                            }
                        }
                        _state.update {
                            it.copy(
                                themeColor = action.seedColor,
                            )
                        }

                        action.onResult(mapOf("result" to Str("success")))
                    }

                    is Event.OnAmoledModeChange -> {
                        viewModelScope.launch {
                            prefs.edit {
                                val amoledKey = booleanPreferencesKey(Constants.PREFERENCES_AMOLED)
                                it[amoledKey] = action.enable
                            }
                        }
                        _state.update {
                            it.copy(
                                isAmoled = action.enable,
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
        when(action) {
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
            is SettingsAction.OnAmoledChanged -> {
                _state.update {
                    it.copy(
                        isAmoled = !_state.value.isAmoled,
                    )
                }
                viewModelScope.launch {
                    prefs.edit {
                        val amoledKey = booleanPreferencesKey(Constants.PREFERENCES_AMOLED)
                        it[amoledKey] = _state.value.isAmoled
                    }
                }
            }
            is SettingsAction.OnColorChanged -> {
                _state.update {
                    it.copy(
                        themeColor = action.color,
                    )
                }
                viewModelScope.launch {
                    prefs.edit {
                        val themeColor = stringPreferencesKey(Constants.PREFERENCES_THEME_COLOR)
                        it[themeColor] = _state.value.themeColor.name
                    }
                }
            }
            is SettingsAction.OnColorBottomSheetToggled -> {
                _state.update {
                    it.copy(
                        isColorBottomSheetVisible = action.show,
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
                        is Result.Error<DataError.Local> -> {
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