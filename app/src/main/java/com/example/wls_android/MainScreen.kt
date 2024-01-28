package com.example.wls_android

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.example.wls_android.data.Disturbance
import com.example.wls_android.data.RetrofitHelper
import com.example.wls_android.data.WlsApi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {

    val wlsApi = RetrofitHelper.getInstance().create(WlsApi::class.java)
    //val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    var distList = remember {
        mutableStateListOf<Disturbance>()
    }

    LaunchedEffect(key1 = Unit) {
        var temp = wlsApi.getDisturbances()
        var body = temp.body()
        if (temp.code() == 200 && body != null) {
            distList.addAll(body.data)
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
    val lines = disturbance.lines
    /*ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )*/
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(6.dp),
        shape = RoundedCornerShape(5.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(5.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                var color = Color("#2196f3".toColorInt())
                for(line in disturbance.lines) {
                    when(line.type) {
                        0 -> color = Color("#2196f3".toColorInt())
                        1 -> color = Color("#f44336".toColorInt())
                        2 -> {
                            if(line.id.startsWith("U1", true)) color = Color("#f44336".toColorInt())
                            else if(line.id.startsWith("U2", true)) color = Color("#e91e63".toColorInt())
                            else if(line.id.startsWith("U3", true)) color = Color("#ff9800".toColorInt())
                            else if(line.id.startsWith("U4", true)) color = Color("#4caf50".toColorInt())
                            else if(line.id.startsWith("U6", true)) color = Color("#795548".toColorInt())
                        }
                        else -> color = Color("#9c27b0".toColorInt())
                    }

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = color
                        ),
                        shape = RoundedCornerShape(5.dp),
                        modifier = Modifier
                            .wrapContentSize().padding(5.dp)

                        ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = if(line.id.length <= 3) Modifier.wrapContentHeight().width(40.dp) else Modifier.wrapContentSize()
                        ) {
                            Text(
                                text = line.id,
                                modifier = Modifier.padding(2.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            val title = disturbance.title
            Text(
                text = title.substring(title.indexOf(':')+2, title.length),
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            Log.e(disturbance.title, disturbance.title.length.toString())
        }
    }
}