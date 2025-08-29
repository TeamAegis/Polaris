package appcup.uom.polaris.features.polaris.presentation.fragments

import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import appcup.uom.polaris.core.presentation.components.PolarisIconButton
import appcup.uom.polaris.core.presentation.components.PolarisTopAppBar
import appcup.uom.polaris.features.auth.presentation.components.LoadingOverlay
import appcup.uom.polaris.features.polaris.presentation.fragments.components.AddFragmentBottomSheet
import appcup.uom.polaris.features.polaris.presentation.fragments.components.FragmentCard
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FragmentsScreen(
    viewModel: FragmentsViewModel = koinViewModel(),
    snackBarHostState: SnackbarHostState,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is FragmentsEvent.OnError -> {
                    scope.launch {
                        snackBarHostState.showSnackbar(event.message)
                    }
                }

                FragmentsEvent.OnFragmentSaved -> {
                    scope.launch {
                        snackBarHostState.showSnackbar("Fragment saved successfully")
                    }
                }
            }
        }
    }

    FragmentsScreenImpl(
        state = state,
        snackbarHostState = snackBarHostState,
        onAction = { action ->
            when (action) {
                FragmentsAction.OnBackClicked -> {
                    onBack()
                }

                else -> {
                    viewModel.onAction(action)
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FragmentsScreenImpl(
    state: FragmentsState,
    snackbarHostState: SnackbarHostState,
    onAction: (FragmentsAction) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val createImageFile = remember {
        {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "FRAGMENT_$timeStamp"
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            File.createTempFile(imageFileName, ".jpg", storageDir)
        }
    }
    var tempImageFile by remember { mutableStateOf<File?>(null) }
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess && tempImageUri != null) {
            onAction(FragmentsAction.OnImageCaptured(tempImageUri!!))
        }
    }

    Scaffold(topBar = {
        Column {
            PolarisTopAppBar(
                "Fragments", navigationIcon = {
                    PolarisIconButton(
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                    ) {
                        onAction(FragmentsAction.OnBackClicked)
                    }
                }, {})
            if (state.isLoading)
                LinearWavyProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
        }

    }, floatingActionButton = {
        FloatingActionButton(
            onClick = {
                onAction(FragmentsAction.ShowAddFragmentBottomSheet)
            },
            modifier = Modifier
                .padding(16.dp)
                .navigationBarsPadding(),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                imageVector = Icons.Default.Add, contentDescription = "Add Fragment"
            )
        }
    }) { contentPadding ->
        if (!state.isLoading)
            if (state.fragments.isEmpty()) {
                // Empty State
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Collections,
                        contentDescription = "No fragments",
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "No fragments yet",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Create your first fragment to capture memories and moments at this location",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            onAction(FragmentsAction.ShowAddFragmentBottomSheet)
                        }, colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Fragment",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Create Fragment")
                    }
                }
            } else {
                // Fragments List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = contentPadding.calculateTopPadding() + 8.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.fragments) { fragment ->
                        FragmentCard(
                            fragment = fragment,
                        )
                    }

                    // Add some bottom padding
                    item {
                        Spacer(modifier = Modifier.height(128.dp))
                    }
                }
            }
    }


    // Add Fragment Bottom Sheet
    if (state.showAddFragmentBottomSheet) {
        AddFragmentBottomSheet(
            message = state.newFragmentMessage,
            imageUri = state.newFragmentImageUri,
            isSaving = state.isSaving,
            onMessageChange = { onAction(FragmentsAction.OnMessageChanged(it)) },
            onTakePhoto = {
                try {
                    tempImageFile = createImageFile()
                    tempImageUri = FileProvider.getUriForFile(
                        context, "${context.packageName}.provider", tempImageFile!!
                    )
                    cameraLauncher.launch(tempImageUri!!)
                } catch (e: Exception) {
                    scope.launch {
                        snackbarHostState.showSnackbar(e.message ?: "Error taking photo")
                    }
                }
            },
            onRemovePhoto = {
                onAction(FragmentsAction.OnImageRemoved)
            },
            onSave = {
                onAction(FragmentsAction.SaveFragment)
            },
            onDismiss = {
                onAction(FragmentsAction.DismissAddFragmentBottomSheet)
            })
    }

    LoadingOverlay(state.isSaving || state.isLoading)
}