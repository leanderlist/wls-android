package com.example.wls_android.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.wls_android.R
import com.example.wls_android.composables.DisturbanceLineFilters
import com.example.wls_android.composables.WlsHeader
import com.example.wls_android.data.LineData
import com.example.wls_android.data.getKtorClient
import com.example.wls_android.model.LineStatePair
import com.example.wls_android.viewmodel.SettingsData
import io.ktor.client.call.body
import io.ktor.client.request.get

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController, settingsData: SettingsData) {
    val lineStateList = remember {
        mutableStateListOf<LineStatePair>()
    }

    LaunchedEffect(key1 = Unit) {
        val client = getKtorClient("/api/lines")
        val response = client.get {}
        val body = response.body<LineData>()
        if (response.status.value in 200..299) {
            for (line in body.data) {
                if (settingsData.isEnabled(line)) {
                    lineStateList.add(LineStatePair(line, true))
                } else {
                    lineStateList.add(LineStatePair(line, false))
                }
            }
            // Log.e("SettingsScreen", "get: ${settingsData.selectedLines}")
        }
    }

    Scaffold(
        topBar = {
            WlsHeader(navController)
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            item {
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
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                    onClick = {
                        settingsData.resetLines()
                        for (lineStatePair in lineStateList) {
                            settingsData.addLine(lineStatePair)
                        }
                        navController.popBackStack()
                    }
                ) {
                    Text(text = "Speichern")
                }
            }
        }
    }
}

