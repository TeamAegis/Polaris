package appcup.uom.polaris.core.presentation.app

sealed interface AppAction {
    data class OnFabMenuExpanded(val isFabMenuExpanded: Boolean) : AppAction
    object RequestCameraPermission : AppAction
    object RequestLocationPermission : AppAction

    object OnControlPanelExpandedChanged: AppAction

    object OnCreatePublicWaypointClicked: AppAction
}