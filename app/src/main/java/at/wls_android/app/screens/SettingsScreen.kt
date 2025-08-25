package at.wls_android.app.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import at.wls_android.app.composables.DisturbanceLineFilters
import at.wls_android.app.composables.WlsHeader
import at.wls_android.app.data.Line
import at.wls_android.app.data.getKtorClient
import at.wls_android.app.model.LineStatePair
import at.wls_android.app.viewmodel.SettingsData
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.util.network.UnresolvedAddressException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    settingsData: SettingsData,
    baseUrl: String
) {
    val lineStateList = remember {
        mutableStateListOf<LineStatePair>()
    }
    val initialTheme = remember { settingsData.getTheme() }
    val isSaveClicked = remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var spinnerLoading by remember { mutableStateOf(true) }
    val snackBarHost = remember { SnackbarHostState() }

    val applySettings = {
        settingsData.resetLines()
        for (lineStatePair in lineStateList) {
            settingsData.addLine(lineStatePair)
        }
        isSaveClicked.value = true
        navController.popBackStack()
    }

    LaunchedEffect(key1 = Unit) {
        try {
            spinnerLoading = true
            val client = getKtorClient(baseUrl = baseUrl, path = "/api/lines")
            val response = client.get {}
            if (response.status.value in 200..299) {
                val body = response.body<List<Line>>()
                lineStateList.clear()
                for (line in body) {
                    if (settingsData.isEnabled(line)) {
                        lineStateList.add(LineStatePair(line, true))
                    } else {
                        lineStateList.add(LineStatePair(line, false))
                    }
                }
                errorMessage = ""
            } else {
                errorMessage = if (response.status.value == 400) {
                    "Fehlerhafte Anfrage"
                } else {
                    "Fehler beim Laden der Linien: ${response.status}"
                }
            }
        } catch (_: UnresolvedAddressException) {
            errorMessage = "Server nicht erreichbar. Überprüfe die Basis-URL."
        } catch (_: Exception) {
            errorMessage = "Es ist ein Fehler aufgetreten"
        } finally {
            spinnerLoading = false
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (!isSaveClicked.value) {
                settingsData.setTheme(initialTheme)
            }
        }
    }

    Scaffold(
        topBar = {
            WlsHeader(navController, disableSettings = true)
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHost)
        },
        bottomBar = {
            Button(
                onClick = { applySettings() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding()
            ) {
                Text("Speichern")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (spinnerLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp)
                        .padding(top = 125.dp)
                        .align(Alignment.TopCenter)
                )
            }

/*
            if (errorMessage.isNotEmpty() && !spinnerLoading) {
                Column(
                    modifier = Modifier
                        .zIndex(1f)
                        .align(Alignment.Center)
                        .padding(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .size(50.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(
                        text = errorMessage,
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
*/

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(top = 18.dp)
            ) {

                if (!spinnerLoading)
                    item {
                        BaseUrlSettings(settingsData)
                    }

                if (!spinnerLoading && errorMessage.isEmpty())
                    item {
                        Spacer(modifier = Modifier.size(16.dp))
                        NotificationSettings(lineStateList)
                    }

                if (!spinnerLoading)
                    item {
                        Spacer(modifier = Modifier.size(16.dp))
                        ThemeSettings(settingsData)
                    }
            }
        }
    }
}

@Composable
fun BaseUrlSettings(settingsData: SettingsData) {
    Column {
        Text(
            text = "Basis URL konfigurieren:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        TextField(
            value = settingsData.getBaseUrl(),
            onValueChange = {
                if (it.isEmpty())
                    settingsData.setBaseUrl("https://wls.byleo.net")
                else
                    settingsData.setBaseUrl(it)
            },
            label = { Text("Basis URL") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun NotificationSettings(lineStateList: SnapshotStateList<LineStatePair>) {
    Column {
        Text(
            text = "Benachrichtigungen konfigurieren:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        DisturbanceLineFilters(
            disturbanceLines = lineStateList,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ThemeSettings(settingsData: SettingsData) {
    val selectedTheme = settingsData.getTheme()

    Column {
        Text(
            text = "Theme konfigurieren:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { settingsData.setTheme("standard") }
        ) {
            RadioButton(
                selected = selectedTheme == "standard",
                onClick = { settingsData.setTheme("standard") }
            )
            Text(text = "Standard", modifier = Modifier.padding(start = 8.dp))
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { settingsData.setTheme("dynamic") }
        ) {
            RadioButton(
                selected = selectedTheme == "dynamic",
                onClick = { settingsData.setTheme("dynamic") }
            )
            Text(text = "Dynamic", modifier = Modifier.padding(start = 8.dp))
        }
    }
}