package com.example.wls_android

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.wls_android.data.Disturbance
import com.example.wls_android.data.RetrofitHelper
import com.example.wls_android.data.WlsApi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {

    val wlsApi = RetrofitHelper.getInstance().create(WlsApi::class.java)
    //val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    var distList = remember {
        mutableListOf<Disturbance>()
    }

    LaunchedEffect(distList) {
        Log.d("DistList", distList.toString())
    }

    LaunchedEffect(key1 = Unit) {
        var temp = wlsApi.getDisturbances()
        var body = temp.body()
        if (body != null) {
            distList = body.data.toMutableList()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(text = "WLS")
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            items(distList) { disturbance ->
                DisturbanceCard(disturbance = disturbance)
            }
        }
    }
}

@Composable
fun DisturbanceCard(disturbance: Disturbance) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
    ) {
        Text(
            text = disturbance.title,
            modifier = Modifier
                .padding(16.dp),
            textAlign = TextAlign.Center,
        )
    }
}