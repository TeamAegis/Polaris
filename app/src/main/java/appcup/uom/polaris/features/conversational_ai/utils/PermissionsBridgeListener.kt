package appcup.uom.polaris.features.conversational_ai.utils

interface PermissionsBridgeListener {
    fun requestRecordAudioPermission(callback: PermissionResultCallback)
    fun isRecordAudioPermissionGranted(): Boolean

    fun requestCameraPermission(callback: PermissionResultCallback)
    fun isCameraPermissionGranted(): Boolean
}

class PermissionBridge {

    private var listener: PermissionsBridgeListener? = null

    fun setListener(listener: PermissionsBridgeListener) {
        this.listener = listener
    }

    fun requestRecordAudioPermission(callback: PermissionResultCallback) {
        listener?.requestRecordAudioPermission(callback) ?: error("Callback handler not set")
    }

    fun isRecordAudioPermissionGranted(): Boolean {
        return listener?.isRecordAudioPermissionGranted() ?: false
    }

    fun requestCameraPermission(callback: PermissionResultCallback) {
        listener?.requestCameraPermission(callback) ?: error("Callback handler not set")
    }

    fun isCameraPermissionGranted(): Boolean {
        return listener?.isCameraPermissionGranted() ?: false
    }
}

interface PermissionResultCallback {
    fun onPermissionGranted()
    fun onPermissionDenied(isPermanentDenied: Boolean)
}