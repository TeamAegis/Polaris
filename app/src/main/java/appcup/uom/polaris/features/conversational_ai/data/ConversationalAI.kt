package appcup.uom.polaris.features.conversational_ai.data

import ai.pipecat.client.RTVIClient
import ai.pipecat.client.RTVIClientOptions
import ai.pipecat.client.RTVIClientParams
import ai.pipecat.client.RTVIEventCallbacks
import ai.pipecat.client.helper.LLMFunctionCall
import ai.pipecat.client.helper.LLMHelper
import ai.pipecat.client.result.Future
import ai.pipecat.client.result.RTVIError
import ai.pipecat.client.types.Participant
import ai.pipecat.client.types.ServiceConfig
import ai.pipecat.client.types.TransportState
import ai.pipecat.client.types.Value
import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import appcup.uom.polaris.core.data.AppSecrets
import appcup.uom.polaris.core.data.Constants
import appcup.uom.polaris.core.data.toDomainValue
import appcup.uom.polaris.core.data.toPipecatValue
import appcup.uom.polaris.core.extras.navigation.Screen
import appcup.uom.polaris.core.extras.theme.SeedColor
import appcup.uom.polaris.core.presentation.settings.AppTheme
import appcup.uom.polaris.core.extras.transport.GeminiLiveWebsocketTransport
import appcup.uom.polaris.features.conversational_ai.utils.function_call.FunctionCallAction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ConversationalAI() : KoinComponent {
    val context: Context by inject()

    var client: RTVIClient? = null
    var errors: ArrayList<Error> = arrayListOf()
    var mic = true

    private fun <E> Future<E, RTVIError>.displayErrors() = withErrorCallback {
        errors.add(Error(it.description))
    }

    fun start(
        onBotReady: (Boolean) -> Unit,
        onBotStartedSpeaking: () -> Unit,
        onBotStoppedSpeaking: () -> Unit,
        onUserStartedSpeaking: () -> Unit,
        onUserStoppedSpeaking: () -> Unit,
        onConnected: () -> Unit,
        onDisconnected: () -> Unit,
        onRemoteAudioLevel: (Float) -> Unit,
        onUserAudioLevel: (Float) -> Unit,
        onFunctionCall: (FunctionCallAction, appcup.uom.polaris.features.conversational_ai.domain.Value.Object, (Map<String, appcup.uom.polaris.features.conversational_ai.domain.Value>) -> Unit) -> Unit,
        onError: (String) -> Unit
    ) {
        if (client != null) {
            return
        }

        val generationConfig = Value.Object(
            "speech_config" to Value.Object(
                "voice_config" to Value.Object(
                    "prebuilt_voice_config" to Value.Object(
                        "voice_name" to Value.Str("Puck")
                    )
                )
            ),
            "response_modalities" to Value.Array(
                Value.Str("AUDIO")
            )
        )

        val systemInstruction = Value.Object(
            "parts" to Value.Object(
                "text" to Value.Str(Constants.getSystemInstructions())
            )
        )

        val screens = Screen::class.nestedClasses.map { Value.Str(it.simpleName!!) }.toTypedArray()


        val navigateToScreenTool = Value.Object(
            values = arrayOf(
                "name" to Value.Str(FunctionCallAction.NAVIGATE_TO_SCREEN.name),
                "description" to Value.Str("Navigates the user to a specific screen within the application. Before navigating, check the current screen you are in and also the details of each screen being navigated to. This is useful for guiding the user through the app or directly accessing features based on their request."),
                "parameters" to Value.Object(
                    values = arrayOf(
                        "type" to Value.Str("OBJECT"),
                        "properties" to Value.Object(
                            values = arrayOf(
                                "screen" to Value.Object(
                                    values = arrayOf(
                                        "type" to Value.Str("STRING"),
                                        "description" to Value.Str("The name of the screen to navigate to. This must be one of the available screens in the application."),
                                        "enum" to Value.Array(
                                            values = screens
                                        )
                                    )
                                ),
                                "navigation_arguments" to Value.Object(
                                    values = arrayOf(
                                        "type" to Value.Str("OBJECT"),
                                        "description" to Value.Str("Optional arguments to pass to the screen during navigation. This can be used to provide context or data to the destination screen."),
                                    )
                                )
                            )
                        ),
                        "required" to Value.Array(
                            values = arrayOf(
                                Value.Str("screen")
                            )
                        )
                    )
                )
            )
        )
        val getScreenDetailsTool = Value.Object(
            values = arrayOf(
                "name" to Value.Str(FunctionCallAction.GET_SCREEN_DETAILS.name),
                "description" to Value.Str("Retrieves details about a specific screen in the application. This is useful for understanding the content and functionality of a particular screen without navigating to it."),
                "parameters" to Value.Object(
                    values = arrayOf(
                        "type" to Value.Str("OBJECT"),
                        "properties" to Value.Object(
                            values = arrayOf(
                                "screen" to Value.Object(
                                    values = arrayOf(
                                        "type" to Value.Str("STRING"),
                                        "description" to Value.Str("The name of the screen to get details for. This must be one of the available screens in the application."),
                                        "enum" to Value.Array(
                                            values = screens
                                        )
                                    )
                                )
                            )
                        ),
                        "required" to Value.Array(
                            values = arrayOf(
                                Value.Str("screen")
                            )
                        )
                    )
                )
            )
        )
        val getCurrentLocationTool = Value.Object(
            values = arrayOf(
                "name" to Value.Str(FunctionCallAction.GET_CURRENT_LOCATION.name),
                "description" to Value.Str("Gets the current location of the user within the application, specifically the current screen they are viewing. This helps in providing context-aware assistance or information."),
                "parameters" to Value.Object(
                    values = arrayOf(
                        "type" to Value.Str("OBJECT"),
                        "properties" to Value.Object()
                    )
                )
            )
        )
        val navigateBackTool = Value.Object(
            values = arrayOf(
                "name" to Value.Str(FunctionCallAction.NAVIGATE_BACK.name),
                "description" to Value.Str("Navigates the user to the previous screen in the application's navigation stack. Always check the current screen it is in currently. This is useful for allowing the user to go back to a prior view or step."),
                "parameters" to Value.Object(
                    values = arrayOf(
                        "type" to Value.Str("OBJECT"),
                        "properties" to Value.Object()
                    )
                )
            )
        )

        val changeThemeTool = Value.Object(
            values = arrayOf(
                "name" to Value.Str(FunctionCallAction.CHANGE_THEME.name),
                "description" to Value.Str("Must navigate to Settings Screen first. Changes the application's theme. This allows the user to switch between system, light, or dark themes."),
                "parameters" to Value.Object(
                    values = arrayOf(
                        "type" to Value.Str("OBJECT"),
                        "properties" to Value.Object(
                            values = arrayOf(
                                "theme" to Value.Object(
                                    values = arrayOf(
                                        "type" to Value.Str("STRING"),
                                        "description" to Value.Str("The theme to apply. Must be one of the available themes: SYSTEM, LIGHT, or DARK."),
                                        "enum" to Value.Array(
                                            values = AppTheme.entries.map { Value.Str(it.name) }.toTypedArray()
                                        )
                                    )
                                )
                            )
                        ),
                        "required" to Value.Array(
                            values = arrayOf(
                                Value.Str("theme")
                            )
                        )
                    )
                )
            )
        )

        val changeSeedColorTool = Value.Object(
            values = arrayOf(
                "name" to Value.Str(FunctionCallAction.CHANGE_SEED_COLOR.name),
                "description" to Value.Str("Must navigate to Settings Screen first. Changes the application's seed color/theme color. This allows the user to switch between different color palettes for the app's UI."),
                "parameters" to Value.Object(
                    values = arrayOf(
                        "type" to Value.Str("OBJECT"),
                        "properties" to Value.Object(
                            values = arrayOf(
                                "seed_color" to Value.Object(
                                    values = arrayOf(
                                        "type" to Value.Str("STRING"),
                                        "description" to Value.Str("The seed color to apply. This must be one of the available seed colors."),
                                        "enum" to Value.Array(
                                            values = SeedColor.entries.map { Value.Str(it.name) }.toTypedArray()
                                        )
                                    )
                                )
                            )
                        ),
                        "required" to Value.Array(
                            values = arrayOf(
                                Value.Str("seed_color")
                            )
                        )
                    )
                )
            )
        )

        val enableAmoledModeTool = Value.Object(
            values = arrayOf(
                "name" to Value.Str(FunctionCallAction.ENABLE_AMOLED_MODE.name),
                "description" to Value.Str("Must navigate to Settings Screen first. Enables or disables AMOLED mode, which uses pure black backgrounds to save battery on AMOLED screens."),
                "parameters" to Value.Object(
                    values = arrayOf(
                        "type" to Value.Str("OBJECT"),
                        "properties" to Value.Object(
                            values = arrayOf(
                                "enable" to Value.Object(
                                    values = arrayOf(
                                        "type" to Value.Str("BOOLEAN"),
                                        "description" to Value.Str("Whether to enable or disable AMOLED mode.")
                                    )
                                )
                            )
                        ),
                        "required" to Value.Array(
                            values = arrayOf(Value.Str("enable"))
                        )
                    )
                )
            )
        )



        val toolsArray = arrayOf(
            navigateToScreenTool,
            getCurrentLocationTool,
            changeThemeTool,
            navigateBackTool,
            getScreenDetailsTool,
            changeSeedColorTool,
            enableAmoledModeTool
        )


        val options = RTVIClientOptions(
            params = RTVIClientParams(
                baseUrl = null,
                config = GeminiLiveWebsocketTransport.Companion.buildConfig(
                    apiKey = AppSecrets.geminiLiveApiKey,
                    generationConfig = generationConfig,
                    initialUserMessage = "Hey Polaris!",
                    systemInstruction = systemInstruction,
                    tools = Value.Array(
                        Value.Object(
                            "function_declarations" to Value.Array(
                                values = toolsArray
                            )
                        )
                    )
                )
            )
        )

        val callbacks = object : RTVIEventCallbacks() {
            override fun onTransportStateChanged(state: TransportState) {
                if (state == TransportState.Error) {
                    onError("An unexpected error occurred.")
                }
            }

            override fun onBackendError(message: String) {
                onError("An unexpected error occurred.")
            }

            override fun onBotReady(version: String, config: List<ServiceConfig>) {
                onBotReady(true)
            }


            override fun onBotStartedSpeaking() {
                onBotStartedSpeaking()
            }

            override fun onBotStoppedSpeaking() {
                onBotStoppedSpeaking()
            }

            override fun onUserStartedSpeaking() {
                onUserStartedSpeaking()
            }

            override fun onUserStoppedSpeaking() {
                onUserStoppedSpeaking()
            }

            override fun onConnected() {
                speakerMode()
                onConnected()
            }

            override fun onDisconnected() {
                client?.release()
                client = null
                onDisconnected()
            }

            override fun onUserAudioLevel(level: Float) {
                onUserAudioLevel(level)
            }

            override fun onRemoteAudioLevel(level: Float, participant: Participant) {
                onRemoteAudioLevel(level)
            }
        }
        val client = RTVIClient(
            transport = GeminiLiveWebsocketTransport.Factory(context),
            callbacks = callbacks,
            options = options
        )
        client.registerHelper(
            service = LLMHelper.Companion.TAG,
            helper = LLMHelper(object : LLMHelper.Callbacks() {
                override fun onLLMFunctionCall(
                    func: LLMFunctionCall,
                    onResult: (Value) -> Unit
                ) {
                    val functionCallAction = try {
                        FunctionCallAction.valueOf(func.functionName)
                    } catch (_: IllegalArgumentException) {
                        onError("Unknown function call: ${func.functionName}")
                        return
                    }
                    onFunctionCall(functionCallAction, func.args.toDomainValue() as appcup.uom.polaris.features.conversational_ai.domain.Value.Object) { res ->
                        onResult(Value.Object(res.mapValues { it.value.toPipecatValue() }))
                    }
                }
            })
        )

        client.connect().displayErrors().withErrorCallback {
            callbacks.onDisconnected()
        }

        this.client = client
    }

    fun enableMic(enabled: Boolean) {
        client?.enableMic(enabled)?.displayErrors()
    }

    fun toggleMic() = enableMic(!mic)

    fun stop() {
        client?.disconnect()?.displayErrors()
    }

    @Suppress("DEPRECATION")
    fun speakerMode() {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            var speakerDevice: AudioDeviceInfo? = null
            val devices: MutableList<AudioDeviceInfo> =
                audioManager.availableCommunicationDevices
            for (device in devices) {
                if (device.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER) {
                    speakerDevice = device
                    break
                }
            }
            if (speakerDevice != null) {
                val result = audioManager.setCommunicationDevice(speakerDevice)
                if (!result) {
                    errors.add(Error("Failed to set communication device"))
                }
//                    audioManager.clearCommunicationDevice()
            }
        } else {
            audioManager.isSpeakerphoneOn = true
        }
    }
}