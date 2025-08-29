package at.wls_android.app

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import at.wls_android.app.data.Line
import at.wls_android.app.data.Subscription
import at.wls_android.app.data.getKtorClient
import at.wls_android.app.navigation.Screen
import at.wls_android.app.screens.DisturbanceListScreen
import at.wls_android.app.screens.FilterScreen
import at.wls_android.app.screens.SettingsScreen
import at.wls_android.app.ui.theme.WlsTheme
import at.wls_android.app.viewmodel.FilterData
import at.wls_android.app.viewmodel.SettingsData
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var settingsData: SettingsData
    private var notificationAlreadyOpened = false
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                false
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        settingsData = ViewModelProvider(this)[SettingsData::class.java]

        setContent {
            val snackBarHost = remember { SnackbarHostState() }
            WlsTheme(dynamicColor = settingsData.getTheme() == "dynamic") {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(hostState = snackBarHost) }) {
                    val navController = rememberNavController()
                    val filterData: FilterData = viewModel()

                    NavHost(
                        navController = navController,
                        startDestination = Screen.DisturbanceList.route
                    ) {
                        composable(
                            route = Screen.DisturbanceList.route, enterTransition = {
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = tween(400)
                                )
                            }) {
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
                                baseUrl = settingsData.getBaseUrl()
                            )
                        }
                        composable(
                            route = Screen.Filter.route, enterTransition = {
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(400)
                                )
                            }) {
                            FilterScreen(
                                navController = navController,
                                filterData = filterData,
                                baseUrl = settingsData.getBaseUrl()
                            )
                        }
                        composable(
                            route = Screen.Settings.route, enterTransition = {
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(400)
                                )
                            }) {
                            SettingsScreen(
                                navController = navController,
                                settingsData = settingsData,
                                baseUrl = settingsData.getBaseUrl(),
                                onSaveClicked = {
                                    try {
                                        handleSettingsSaved()
                                        lifecycleScope.launch {
                                            snackBarHost.showSnackbar("Einstellungen gespeichert")
                                        }
                                    } catch (e: Exception) {
                                        lifecycleScope.launch {
                                            snackBarHost.showSnackbar(e.message.toString())
                                        }
                                    }
                                })
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

        settingsData.setTheme(sharedPref.getString("theme", "standard") ?: "standard")
        settingsData.setBaseUrl(
            sharedPref.getString("baseUrl", "https://wls.byleo.net") ?: "https://wls.byleo.net"
        )

        getFCMToken { token ->
            if (token != null) {
                lifecycleScope.launch {
                    try {
                        val subscription = getSubscription(settingsData.getBaseUrl(), token)
                        if (subscription != null) {
                            settingsData.setSubscribedLines(subscription.subscribedLines)
                        } else {
                            loadSubscribedLinesFromLocalStorage(sharedPref)
                        }
                    } catch (_: Exception) {
                        loadSubscribedLinesFromLocalStorage(sharedPref)
                    }
                }
            } else {
                loadSubscribedLinesFromLocalStorage(sharedPref)
            }
        }
    }

    private fun loadSubscribedLinesFromLocalStorage(sharedPref: android.content.SharedPreferences) {
        val subscribedLines = sharedPref.getString("subscribedLines", "[]")
        if (subscribedLines?.let { isValidJsonArray(it) } == true) {
            try {
                val type = object : TypeToken<List<Line>>() {}.type
                val enabledLines = Gson().fromJson<List<Line>>(subscribedLines, type)
                settingsData.setSubscribedLines(enabledLines)
            } catch (_: Exception) {
                settingsData.resetSubscribedLines()
            }
        } else {
            settingsData.resetSubscribedLines()
        }
    }

    private fun isValidJsonArray(json: String): Boolean {
        return try {
            val jsonElement = Gson().fromJson(json, Any::class.java)
            jsonElement is List<*> || jsonElement is Array<*>
        } catch (_: JsonSyntaxException) {
            false
        }
    }

    private fun handleSettingsSaved() {
        val hasNotificationPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (hasNotificationPermission) {
            getFCMToken { token ->
                if (token != null) {
                    if (settingsData.getSubscribedLines().isEmpty()) {
                        lifecycleScope.launch {
                            unsubscribeFromNotifications(settingsData.getBaseUrl(), token)
                            saveSettingsData()
                        }
                    } else {
                        lifecycleScope.launch {
                            val subscription = subscribeToNotifications(
                                settingsData.getBaseUrl(),
                                token,
                                settingsData.getSubscribedLines().map { it.id })

                            settingsData.setSubscribedLines(subscription.subscribedLines)
                            saveSettingsData()
                        }
                    }
                } else {
                    throw Exception("Fehler beim Abrufen des Tokens für Benachrichtigungen")
                }
            }
        }
    }

    private fun getFCMToken(onTokenReceived: (String?) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                onTokenReceived(token)
            } else {
                onTokenReceived(null)
            }
        }
    }

    private fun saveSettingsData() {
        val sharedPref = getSharedPreferences("WLS-App", MODE_PRIVATE)
        sharedPref.edit {
            putString("subscribedLines", Gson().toJson(settingsData.getSubscribedLines()))
            putString("theme", settingsData.getTheme())
            putString("baseUrl", settingsData.getBaseUrl())
        }
    }

    private suspend fun subscribeToNotifications(
        baseUrl: String, token: String, lineIds: List<String>
    ): Subscription {
        try {
            val client = getKtorClient(baseUrl = baseUrl, path = "/api/subscriptions/${token}")
            val response = client.put {
                setBody(mapOf("lines" to lineIds.joinToString(",")))
            }

            if (response.status.value !in 200..299) {
                throw Exception("Fehler beim Abonnieren von Benachrichtigungen")
            } else {
                val body = response.body<Subscription>()
                return body
            }
        } catch (e: Exception) {
            throw e
        }
    }

    private suspend fun getSubscription(baseUrl: String, token: String): Subscription? {
        try {
            val client = getKtorClient(baseUrl = baseUrl, path = "/api/subscriptions/${token}")
            val response = client.get {  }

            if (response.status.value !in 200..299) {
                return null
            } else {
                val body = response.body<Subscription>()
                return body
            }
        } catch (_: Exception) {
            return null
        }
    }

    private suspend fun unsubscribeFromNotifications(baseUrl: String, token: String) {
        try {
            val client = getKtorClient(baseUrl = baseUrl, path = "/api/subscriptions/${token}")
            val response = client.delete { }
            if (response.status.value !in 200..299) {
                throw Exception("Fehler beim Abbestellen von Benachrichtigungen")
            }
        } catch (e: Exception) {
            throw e
        }
    }
}
