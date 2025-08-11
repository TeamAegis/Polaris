package appcup.uom.polaris.core.presentation.settings

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import appcup.uom.polaris.core.presentation.components.PolarisIconButton
import appcup.uom.polaris.core.presentation.components.PolarisLargeTopAppBar
import appcup.uom.polaris.features.auth.presentation.components.LoadingOverlay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    onBack: () -> Unit,
    navigateToChangeDisplayName: () -> Unit,
    navigateToChangePassword: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest {
            when (it) {
                is SettingsEvent.Error -> {
                    snackbarHostState.showSnackbar(it.message)
                }
            }
        }
    }

    SettingsScreenImpl(
        state = state,
        onAction = { action ->
            when (action) {
                SettingsAction.OnBackClicked -> {
                    onBack()
                }

                SettingsAction.OnChangeDisplayNameClicked -> {
                    navigateToChangeDisplayName()
                }

                SettingsAction.OnChangePasswordClicked -> {
                    navigateToChangePassword()
                }

                else -> {
                    viewModel.onAction(action)
                }
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenImpl(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        flingAnimationSpec = rememberSplineBasedDecay(),
        state = rememberTopAppBarState()
    )
    val scope = rememberCoroutineScope()

    val themeSheetState = rememberModalBottomSheetState()
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            PolarisLargeTopAppBar(
                title = "Settings",
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    PolarisIconButton(
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    ) {
                        onAction(SettingsAction.OnBackClicked)
                    }
                },
                actions = {
                    PolarisIconButton(
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Logout",
                                tint = MaterialTheme.colorScheme.error
                            )
                        },
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                    ) {
                        onAction(SettingsAction.OnLogoutClicked)
                    }
                }
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            if (state.isThemeBottomSheetVisible) {
                ModalBottomSheet(
                    onDismissRequest = { onAction(SettingsAction.OnThemeBottomSheetToggled(false)) },
                    sheetState = themeSheetState
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            "Select Theme",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        ThemeOptionRow(
                            icon = Icons.Default.LightMode,
                            text = "Light",
                            isSelected = state.theme == AppTheme.Light,
                            onClick = {
                                onAction(SettingsAction.OnThemeChanged(AppTheme.Light))
                                scope.launch { themeSheetState.hide() }.invokeOnCompletion {
                                    if (!themeSheetState.isVisible) {
                                        onAction(SettingsAction.OnThemeBottomSheetToggled(false))
                                    }
                                }
                            })
                        ThemeOptionRow(
                            icon = Icons.Default.DarkMode,
                            text = "Dark",
                            isSelected = state.theme == AppTheme.Dark,
                            onClick = {
                                onAction(SettingsAction.OnThemeChanged(AppTheme.Dark))
                                scope.launch { themeSheetState.hide() }.invokeOnCompletion {
                                    if (!themeSheetState.isVisible) {
                                        onAction(SettingsAction.OnThemeBottomSheetToggled(false))
                                    }
                                }
                            })
                        ThemeOptionRow(
                            icon = Icons.Default.BrightnessMedium, // Or PhoneAndroid for System
                            text = "System Default",
                            isSelected = state.theme == AppTheme.System,
                            onClick = {
                                onAction(SettingsAction.OnThemeChanged(AppTheme.System))
                                scope.launch { themeSheetState.hide() }.invokeOnCompletion {
                                    onAction(
                                        SettingsAction.OnThemeBottomSheetToggled(false)
                                    )
                                }
                            })
                    }
                }
            }


            SettingsGroupTitle("Appearance")
            SettingsCard {
                SettingsClickableRow(
                    icon = when (state.theme) {
                        AppTheme.Light -> Icons.Default.LightMode
                        AppTheme.Dark -> Icons.Default.DarkMode
                        AppTheme.System -> Icons.Default.BrightnessMedium
                    },
                    title = "Theme",
                    currentValue = state.theme.name,
                    onClick = {
                        onAction(SettingsAction.OnThemeBottomSheetToggled(true))
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))


            SettingsGroupTitle("Account")
            SettingsCard {
                SettingsClickableRow(
                    icon = Icons.Default.AccountCircle,
                    title = "Display Name",
                    onClick = {
                        onAction(SettingsAction.OnChangeDisplayNameClicked)
                    }
                )
                HorizontalDivider()
                SettingsClickableRow(
                    icon = Icons.Default.Security,
                    title = "Change Password",
                    onClick = {
                        onAction(SettingsAction.OnChangePasswordClicked)
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))


            // --- About Section ---
            SettingsGroupTitle("About")
            SettingsCard {
                SettingsInfoRow(
                    icon = Icons.Default.Info,
                    title = "App Version",
                    value = "1.0.0 (Build 1)"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }


    LoadingOverlay(isLoading = state.isLoading)
}

@Composable
fun SettingsGroupTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 8.dp) // Adjusted padding
    )
}

@Composable
fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors().copy(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(content = content)
    }
}

@Composable
fun SettingsClickableRow(
    icon: ImageVector,
    title: String,
    currentValue: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        currentValue?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 8.dp, end = 8.dp)
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Composable
fun ThemeOptionRow(
    icon: ImageVector,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun SettingsInfoRow(
    icon: ImageVector,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}