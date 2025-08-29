package appcup.uom.polaris.features.polaris.presentation.fragments

import android.net.Uri
import appcup.uom.polaris.features.polaris.domain.Fragment

data class FragmentsState(
    val fragments: List<Fragment> = emptyList(),
    val isLoading: Boolean = true,
    val showAddFragmentBottomSheet: Boolean = false,
    val newFragmentMessage: String = "",
    val newFragmentImageUri: Uri? = null,
    val isSaving: Boolean = false,
    val editingFragment: Fragment? = null
)
