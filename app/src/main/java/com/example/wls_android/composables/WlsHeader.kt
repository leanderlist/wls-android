package com.example.wls_android.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wls_android.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WlsHeader(
    navController: NavController
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        title = {
            Text(text = "Wiener Linien Störungsarchiv", fontWeight = FontWeight.Bold)
        },
        actions = {
            IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Open Settings",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        },
        modifier = Modifier.padding(bottom = 5.dp)
    )
}