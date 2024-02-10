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
import com.example.compose.WlsAndroidTheme
import com.example.wls_android.navigation.Screen
import com.example.wls_android.screens.DisturbanceListScreen
import com.example.wls_android.screens.FilterScreen
import com.example.wls_android.viewmodel.FilterData


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
                    }
                }
            }
        }
    }
}