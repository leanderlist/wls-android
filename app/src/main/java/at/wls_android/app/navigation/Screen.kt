package at.wls_android.app.navigation

sealed class Screen(
    val route: String
) {
    object DisturbanceList : Screen("disturbance_list")
    object Filter : Screen("filter")
    object Settings : Screen("settings")
}
