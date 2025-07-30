package appcup.uom.polaris.features.conversational_ai.utils

interface PermissionsBridgeListener {
    fun requestRecordAudioPermission(callback: PermissionResultCallback)
    fun isRecordAudioPermissionGranted(): Boolean
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

}

interface PermissionResultCallback {
    fun onPermissionGranted()
    fun onPermissionDenied(isPermanentDenied: Boolean)
}