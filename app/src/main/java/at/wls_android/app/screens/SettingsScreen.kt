package at.wls_android.app.screens

import android.Manifest
import android.content.Intent
import android.content.Intent.CATEGORY_DEFAULT
import android.content.Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_NO_HISTORY
import android.content.pm.PackageManager
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.net.toUri
import androidx.lifecycle.compose.LocalLifecycleOwner
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
    baseUrl: String,
    onSaveClicked: () -> Unit = {}
) {
    val lineStateList = remember {
        mutableStateListOf<LineStatePair>()
    }
    val initialTheme = remember { settingsData.getTheme() }
    val initialBaseUrl = remember { settingsData.getBaseUrl() }
    val isSaveClicked = remember { mutableStateOf(false) }
    var tempBaseUrl by remember { mutableStateOf(initialBaseUrl) }
    var errorMessage by remember { mutableStateOf("") }
    var spinnerLoading by remember { mutableStateOf(true) }
    val snackBarHost = remember { SnackbarHostState() }
    val context = LocalContext.current

    var hasNotificationPermission by remember { mutableStateOf(false) }

    fun checkNotificationPermission() {
        hasNotificationPermission = context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
    }

    LaunchedEffect(Unit) {
        checkNotificationPermission()
    }

    val applySettings = {
        settingsData.setSubscribedLines(
            lineStateList.filter { it.enabled }.map { it.line }
        )
        settingsData.setBaseUrl(tempBaseUrl)
        isSaveClicked.value = true

        onSaveClicked()

        navController.popBackStack()
    }

    LaunchedEffect(key1 = baseUrl) {
        try {
            spinnerLoading = true
            val client = getKtorClient(baseUrl = baseUrl, path = "/api/lines")
            val response = client.get {}
            if (response.status.value in 200..299) {
                val body = response.body<List<Line>>()
                lineStateList.clear()
                for (line in body) {
                    if (settingsData.getSubscribedLines().contains(line)) {
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
            errorMessage = "Es ist ein Fehler aufgetreten. Versuche es erneut."
        } finally {
            spinnerLoading = false
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(Unit) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                checkNotificationPermission()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
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

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(top = 18.dp)
            ) {

                if (!spinnerLoading)
                    item {
                        BaseUrlSettings(tempBaseUrl) { newUrl -> tempBaseUrl = newUrl }
                    }

                if (!spinnerLoading && errorMessage.isEmpty() && hasNotificationPermission)
                    item {
                        Spacer(modifier = Modifier.size(16.dp))
                        NotificationSettings(lineStateList)
                    }
                if (!hasNotificationPermission && !spinnerLoading && errorMessage.isEmpty())
                    item {
                        val context = LocalContext.current
                        Spacer(modifier = Modifier.size(16.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        ) {
                            Text(
                                text = "Benachrichtigungen aktivieren:",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "Um Benachrichtigungen zu konfigurieren, aktiviere diese bitte zuerst in den App-Einstellungen deines Geräts.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            Button(
                                onClick = {
                                    val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                        data = "package:${context.packageName}".toUri()
                                        addCategory(CATEGORY_DEFAULT)
                                        addFlags(FLAG_ACTIVITY_NEW_TASK)
                                        addFlags(FLAG_ACTIVITY_NO_HISTORY)
                                        addFlags(FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                                    }
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("App-Einstellungen öffnen")
                            }
                        }
                    }

                if (!spinnerLoading)
                    item {
                        Spacer(modifier = Modifier.size(16.dp))
                        ThemeSettings(initialTheme) { newTheme -> settingsData.setTheme(newTheme) }
                    }
            }
        }
    }
}

@Composable
fun BaseUrlSettings(baseUrl: String, onBaseUrlChanged: (String) -> Unit) {
    var textFieldValue by remember { mutableStateOf(baseUrl) }

    Column {
        Text(
            text = "Basis URL konfigurieren:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        TextField(
            value = textFieldValue,
            onValueChange = {
                textFieldValue = it
                onBaseUrlChanged(it.ifEmpty { "https://wls.byleo.net" })
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
fun ThemeSettings(initialTheme: String, onThemeChanged: (String) -> Unit) {
    var selectedTheme by remember { mutableStateOf(initialTheme) }
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
                .clickable { onThemeChanged("standard"); selectedTheme = "standard" }
        ) {
            RadioButton(
                selected = selectedTheme == "standard",
                onClick = { onThemeChanged("standard"); selectedTheme = "standard" }
            )
            Text(text = "Standard", modifier = Modifier.padding(start = 8.dp))
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onThemeChanged("dynamic"); selectedTheme = "dynamic" }
        ) {
            RadioButton(
                selected = selectedTheme == "dynamic",
                onClick = { onThemeChanged("dynamic"); selectedTheme = "dynamic" }
            )
            Text(text = "Dynamic", modifier = Modifier.padding(start = 8.dp))
        }
    }
}