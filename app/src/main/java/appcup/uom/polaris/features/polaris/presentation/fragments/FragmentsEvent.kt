package appcup.uom.polaris.features.polaris.presentation.fragments

sealed class FragmentsEvent {
    data class OnError(val message: String) : FragmentsEvent()
    object OnFragmentSaved : FragmentsEvent()
}