package at.wls_android.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.Constraints
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import at.wls_android.app.model.LineStatePair
import at.wls_android.app.navigation.Screen
import at.wls_android.app.screens.DisturbanceListScreen
import at.wls_android.app.screens.FilterScreen
import at.wls_android.app.screens.SettingsScreen
import at.wls_android.app.ui.theme.AppTheme
import at.wls_android.app.viewmodel.FilterData
import at.wls_android.app.viewmodel.SettingsData
import at.wls_android.app.worker.DisturbanceWorker
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle the result of the permission request here
        if (permissions[Manifest.permission.POST_NOTIFICATIONS] == true) {
            // Permission granted
            Toast.makeText(this, "Benachrichtigungen erlaubt", Toast.LENGTH_SHORT).show()
        } else {
            // Permission denied
            Toast.makeText(this, "Benachrichtigungen verweigert", Toast.LENGTH_SHORT).show()
        }
    }
    private lateinit var settingsViewModel: SettingsData
    private var notificationAlreadyOpened = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                // Keeps the splash screen visible while loading (optional logic can be applied here)
                false
            }
        }

        MobileAds.initialize(this) {}

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
            }
        }
        settingsViewModel = ViewModelProvider(this)[SettingsData::class.java]
        setContent {
            AppTheme(dynamicColor = settingsViewModel.getTheme() == "dynamic") {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val navController = rememberNavController()
                    val sharedViewModel: FilterData = viewModel()

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
                                filterData = sharedViewModel,
                                disturbanceIdToOpen = openFromNotificationId,
                                adMobBanner = { AdmobBanner(modifier = Modifier.fillMaxWidth()) }
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
                                filterData = sharedViewModel,
                                adMobBanner = { AdmobBanner(modifier = Modifier.fillMaxWidth()) }
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
                                settingsData = settingsViewModel
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun AdmobBanner(modifier: Modifier = Modifier) {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                // on below line specifying ad view.
                AdView(context).apply {
                    // on below line specifying ad size
                    //adSize = AdSize.BANNER
                    // on below line specifying ad unit id
                    // currently added a test ad unit id.
                    setAdSize(AdSize.BANNER)
                    adUnitId = "ca-app-pub-3940256099942544/6300978111"
                    // calling load ad to load our ad.
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }

    override fun onStart() {
        super.onStart()
        stopNotificationWorker()
        setSettingsData()
    }

    override fun onStop() {
        super.onStop()
        startNotificationWorker()
        saveSettingsData()
    }

    private fun setSettingsData() {
        val sharedPref = getSharedPreferences("WLS-App", MODE_PRIVATE)
        val selectedLines = sharedPref.getString("selectedLines", "[]")
        if (selectedLines?.let { isValidJsonArray(it) } == true) {
            settingsViewModel.selectedLines =
                Gson().fromJson(selectedLines, Array<LineStatePair>::class.java).toMutableList()
        } else {
            settingsViewModel.selectedLines = mutableListOf()
        }
        settingsViewModel.theme.value = sharedPref.getString("theme", "standard") ?: "standard"
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
        with(sharedPref.edit()) {
            putString("selectedLines", Gson().toJson(settingsViewModel.selectedLines))
            putString("theme", settingsViewModel.theme.value)
            apply()
        }
    }

    private fun stopNotificationWorker() {
        WorkManager.getInstance(this).cancelAllWorkByTag(DisturbanceWorker.WORKER_TAG)
    }

    private fun startNotificationWorker() {
        if (settingsViewModel.hasNoEnabledLines()) {
            // Log.d("MainActivity", "No enabled lines, not starting worker")
            return
        }
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(false)
            .setRequiresCharging(false)
            .setRequiresStorageNotLow(false)
            .setRequiresDeviceIdle(false)
            .build()
        val workRequest = PeriodicWorkRequestBuilder<DisturbanceWorker>(15, TimeUnit.MINUTES)
            .addTag(DisturbanceWorker.WORKER_TAG)
            .setInputData(
                workDataOf(
                    "selectedLines" to settingsViewModel.selectedLines.filter { it.enabled }
                        .map { it.line.id }.toTypedArray()
                )
            )
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(this).enqueue(workRequest)
    }
}