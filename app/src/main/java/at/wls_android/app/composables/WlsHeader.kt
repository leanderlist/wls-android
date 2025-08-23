package at.wls_android.app.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import at.wls_android.app.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WlsHeader(
    navController: NavController,
    disableSettings: Boolean = false
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        title = {
            Text(text = "Wiener Linien Störungsarchiv", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
        },
        navigationIcon = {
            if (disableSettings) {
                IconButton(onClick = { navController.navigate(Screen.DisturbanceList.route) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        contentDescription = "Open Disturbance List"
                    )
                }
            }
        },
        actions = {
            if (!disableSettings) {
                IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        contentDescription = "Open Settings"
                    )
                }
            }
        }
    )
}