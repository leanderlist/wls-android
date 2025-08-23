package at.wls_android.app.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import at.wls_android.app.composables.DisturbanceLineFilters
import at.wls_android.app.composables.WlsHeader
import at.wls_android.app.data.Line
import at.wls_android.app.data.getKtorClient
import at.wls_android.app.model.LineStatePair
import at.wls_android.app.viewmodel.SettingsData
import io.ktor.client.call.body
import io.ktor.client.request.get

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    settingsData: SettingsData
) {
    val lineStateList = remember {
        mutableStateListOf<LineStatePair>()
    }
    val initialTheme = remember { settingsData.getTheme() }
    val isSaveClicked = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        val client = getKtorClient("/api/lines")
        val response = client.get {}
        val body = response.body<List<Line>>()
        if (response.status.value in 200..299) {
            for (line in body) {
                if (settingsData.isEnabled(line)) {
                    lineStateList.add(LineStatePair(line, true))
                } else {
                    lineStateList.add(LineStatePair(line, false))
                }
            }
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
        }
    ) {

        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .navigationBarsPadding()
            ) {
                item {
                    NotificationSettings(lineStateList)
                }
                item {
                    ThemeSettings(settingsData)
                }
                item {
                    SaveSettings(navController, lineStateList, settingsData) {
                        isSaveClicked.value = true
                    }
                }
            }
        }
    }
}

@Composable
fun SaveSettings(
    navController: NavHostController,
    lineStateList: SnapshotStateList<LineStatePair>,
    settingsData: SettingsData,
    onSaveClick: () -> Unit
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 10.dp),
        onClick = {
            settingsData.resetLines()
            for (lineStatePair in lineStateList) {
                settingsData.addLine(lineStatePair)
            }
            onSaveClick()
            navController.popBackStack()
        }
    ) {
        Text(text = "Speichern")
    }
}

@Composable
fun NotificationSettings(lineStateList: SnapshotStateList<LineStatePair>) {
    Row(
        modifier = Modifier.padding(top = 15.dp)
    ) {
        Text(
            text = "Benachrichtigungen konfigurieren:",
            fontSize = 20.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
    }
    Row {
        DisturbanceLineFilters(
            disturbanceLines = lineStateList,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 15.dp)
        )
    }
}

@Composable
fun ThemeSettings(settingsData: SettingsData) {
    val selectedTheme = settingsData.getTheme()

    Column(
        modifier = Modifier.padding(vertical = 10.dp, horizontal = 15.dp)
    ) {
        Text(
            text = "Theme konfigurieren:",
            fontSize = 20.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
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