package com.example.wls_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.Constraints
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.compose.WlsAndroidTheme
import com.example.wls_android.model.LineStatePair
import com.example.wls_android.navigation.Screen
import com.example.wls_android.screens.DisturbanceListScreen
import com.example.wls_android.screens.FilterScreen
import com.example.wls_android.screens.SettingsScreen
import com.example.wls_android.worker.DisturbanceWorker
import com.example.wls_android.viewmodel.FilterData
import com.example.wls_android.viewmodel.SettingsData
import com.google.gson.Gson
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private lateinit var settingsViewModel : SettingsData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSettingsData()
        setContent {
            WlsAndroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val sharedViewModel : FilterData = viewModel()
                    settingsViewModel = viewModel()

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
                            DisturbanceListScreen(navController = navController, filterData = sharedViewModel)
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
                            FilterScreen(navController = navController, filterData = sharedViewModel)
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
                            SettingsScreen(navController = navController, settingsData = settingsViewModel)
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        stopNotificationWorker()
    }

    override fun onStop() {
        super.onStop()
        startNotificationWorker()
        saveSettingsData()
    }

    private fun setSettingsData() {
        val sharedPref = getSharedPreferences("WLS-App", MODE_PRIVATE)
        val selectedLines = sharedPref.getString("selectedLines", "[]")
        settingsViewModel.selectedLines = selectedLines?.let {
            Gson().fromJson(it, Array<LineStatePair>::class.java).toMutableList()
        } ?: mutableListOf()
    }

    private fun saveSettingsData() {
        val sharedPref = getSharedPreferences("WLS-App", MODE_PRIVATE)
        with (sharedPref.edit()) {
            putString("selectedLines", settingsViewModel.selectedLines.toString())
            apply()
        }
    }

    private fun stopNotificationWorker() {
        WorkManager.getInstance(this).cancelAllWorkByTag(DisturbanceWorker.WORKER_TAG)
    }

    private fun startNotificationWorker() {
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
                    "selectedLines" to settingsViewModel.selectedLines.filter { it.enabled }.map { it.line.id }.toTypedArray()
                ))
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(this).enqueue(workRequest)
    }
}