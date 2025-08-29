package appcup.uom.polaris.features.polaris.presentation.fragments

import android.net.Uri

sealed interface FragmentsAction {
    object OnBackClicked : FragmentsAction

    data class OnImageCaptured(val imageUri: Uri) : FragmentsAction

    object ShowAddFragmentBottomSheet : FragmentsAction


    object DismissAddFragmentBottomSheet : FragmentsAction
    data class OnMessageChanged(val message: String) : FragmentsAction
    object OnImageRemoved : FragmentsAction
    object SaveFragment : FragmentsAction

}