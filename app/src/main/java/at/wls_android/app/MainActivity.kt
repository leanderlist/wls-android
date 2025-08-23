package at.wls_android.app

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.content.edit
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import at.wls_android.app.model.LineStatePair
import at.wls_android.app.navigation.Screen
import at.wls_android.app.screens.DisturbanceListScreen
import at.wls_android.app.screens.FilterScreen
import at.wls_android.app.screens.SettingsScreen
import at.wls_android.app.ui.theme.WlsTheme
import at.wls_android.app.viewmodel.FilterData
import at.wls_android.app.viewmodel.SettingsData
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

class MainActivity : ComponentActivity() {
    private lateinit var settingsData: SettingsData
    private var notificationAlreadyOpened = false

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                false
            }
        }

        settingsData = ViewModelProvider(this)[SettingsData::class.java]

        setContent {
            WlsTheme(dynamicColor = settingsData.getTheme() == "dynamic") {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    val filterData: FilterData = viewModel()

                    NavHost(
                        navController = navController,
                        startDestination = Screen.DisturbanceList.route
                    ) {
                        composable(
                            route = Screen.DisturbanceList.route,
                            enterTransition = {
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = tween(400)
                                )
                            }
                        ) {
                            val openFromNotificationId: String? = if (!notificationAlreadyOpened) {
                                intent.getStringExtra("disturbanceId")
                                    ?.also { notificationAlreadyOpened = true }
                            } else {
                                null
                            }
                            DisturbanceListScreen(
                                navController = navController,
                                filterData = filterData,
                                disturbanceIdToOpen = openFromNotificationId,
                                baseUrl = settingsData.baseUrl.value
                            )
                        }
                        composable(
                            route = Screen.Filter.route,
                            enterTransition = {
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(400)
                                )
                            }
                        ) {
                            FilterScreen(
                                navController = navController,
                                filterData = filterData,
                                baseUrl = settingsData.baseUrl.value
                            )
                        }
                        composable(
                            route = Screen.Settings.route,
                            enterTransition = {
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(400)
                                )
                            }
                        ) {
                            SettingsScreen(
                                navController = navController,
                                settingsData = settingsData,
                                baseUrl = settingsData.baseUrl.value
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        loadSettingsData()
    }

    override fun onStop() {
        super.onStop()
        saveSettingsData()
    }

    private fun loadSettingsData() {
        val sharedPref = getSharedPreferences("WLS-App", MODE_PRIVATE)
        val selectedLines = sharedPref.getString("selectedLines", "[]")
        if (selectedLines?.let { isValidJsonArray(it) } == true) {
            settingsData.selectedLines =
                Gson().fromJson(selectedLines, Array<LineStatePair>::class.java).toMutableList()
        } else {
            settingsData.selectedLines = mutableListOf()
        }
        settingsData.theme.value = sharedPref.getString("theme", "standard") ?: "standard"
        settingsData.baseUrl.value = sharedPref.getString("baseUrl", "https://wls.byleo.net")
            ?: "https://wls.byleo.net"
    }

    private fun isValidJsonArray(json: String): Boolean {
        return try {
            Gson().fromJson(json, Any::class.java) is List<*>
        } catch (_: JsonSyntaxException) {
            false
        }
    }

    private fun saveSettingsData() {
        val sharedPref = getSharedPreferences("WLS-App", MODE_PRIVATE)
        sharedPref.edit {
            putString("selectedLines", Gson().toJson(settingsData.selectedLines))
            putString("theme", settingsData.theme.value)
            putString("baseUrl", settingsData.baseUrl.value)
        }
    }
}
