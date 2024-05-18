package com.example.wls_android

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
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
import com.google.gson.reflect.TypeToken
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WlsAndroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val sharedViewModel : FilterData = viewModel()
                    val settingsViewModel : SettingsData = viewModel()

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
    }

    private fun setSettingsData(settingsViewModel: SettingsData) {
        val sharedPreferences = getSharedPreferences("WLS-App", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("settingsData", "")
        Log.d("MainActivity", "Retrieved JSON: $json")
        if (!json.isNullOrEmpty()) {
            val gson = Gson()
            val type = object : TypeToken<List<LineStatePair>>() {}.type
            val data = gson.fromJson<List<LineStatePair>>(json, type)
            Log.d("MainActivity", "Converted data: $data")
            settingsViewModel.selectedLines.addAll(data)
        }
    }

    private fun saveSettingsData(settingsViewModel: SettingsData) {
        val sharedPreferences = getSharedPreferences("WLS-App", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(settingsViewModel.selectedLines)
        editor.putString("settingsData", json)
        editor.apply()
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
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(this).enqueue(workRequest)
    }
}