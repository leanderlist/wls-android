package com.example.wls_android.screens

import android.content.Intent
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.wls_android.R
import com.example.wls_android.composables.DisturbanceCard
import com.example.wls_android.composables.LineIcon
import com.example.wls_android.data.Data
import com.example.wls_android.data.Disturbance
import com.example.wls_android.data.getKtorClient
import com.example.wls_android.navigation.Screen
import com.example.wls_android.viewmodel.FilterData
import io.ktor.client.call.body
import io.ktor.client.request.get
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisturbanceListScreen(navController : NavHostController, filterData : FilterData) {

    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    var sheetDisturbance by remember {
        mutableStateOf<Disturbance?>(null)
    }

    val disturbanceList = remember {
        mutableStateListOf<Disturbance>()
    }

    val pullRefreshState = rememberPullToRefreshState()

    val filters : SnapshotStateMap<String, String> = filterData.filters

    if (pullRefreshState.isRefreshing) {
        LaunchedEffect(Unit) {
            try {
                val client = getKtorClient("/api/disturbances")
                val response = client.get {
                    url {
                        if(filters.isEmpty()) {
                            parameters.append("from", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                            parameters.append("to", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        } else {
                            for(entry in filters.toMap()) {
                                parameters.append(entry.key, entry.value)
                            }
                        }
                    }
                }
                val body = response.body<Data>()
                if (response.status.value in 200..299) {
                    if (body != null) {
                        disturbanceList.clear()
                        disturbanceList.addAll(body.data)
                        errorMessage = ""
                    } else
                        errorMessage = "Keine Störungen passend zum gesetzten Filter gefunden"
                } else {
                    errorMessage = "Es sind keine Störungen vorhanden"
                }
            } catch(e : Exception) {
                errorMessage = "Es konnte keine Verbindung hergestellt werden"
            }
            pullRefreshState.endRefresh()
        }
    }

    LaunchedEffect(key1 = Unit) {
        try {
            val client = getKtorClient("/api/disturbances")
            val response = client.get {
                url {
                    if(filters.isEmpty()) {
                        parameters.append("from", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        parameters.append("to", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    } else {
                        for(entry in filters.toMap()) {
                            parameters.append(entry.key, entry.value)
                        }
                    }
                }
            }
            val body = response.body<Data>()
            if (response.status.value in 200..299) {
                if(body != null) {
                    disturbanceList.addAll(body.data)
                } else
                    errorMessage = "Keine Störungen passend zum gesetzten Filter gefunden"
            } else {
                errorMessage = "Es sind keine Störungen vorhanden"
            }
        } catch(e : Exception) {
            //errorMessage = "Es konnte keine Verbindung hergestellt werden"
            errorMessage = e.printStackTrace().toString()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.main_color),
                    titleContentColor = Color.White
                ),
                title = {
                    Text(text = "WLS")
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Filter.route) }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Localized description"
                        )
                    }
                }
            )
        }
    ) {
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                if (sheetDisturbance != null) {
                    val initialDate = sheetDisturbance!!.start_time.substring(0,10)
                    val descriptions = sheetDisturbance!!.descriptions
                    val title = sheetDisturbance!!.title

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .navigationBarsPadding()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            for (line in sheetDisturbance!!.lines) {
                                LineIcon(
                                    line = line,
                                    modifier = Modifier
                                        .padding(end = 10.dp, bottom = 5.dp)
                                        .align(Alignment.CenterVertically)
                                )
                            }
                            Spacer(modifier = Modifier.weight(1F))
                            IconButton(
                                onClick = {
                                    val url = "https://wls.byleo.net/stoerung/${sheetDisturbance!!.id}"
                                    val intent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, url)
                                        type = "text/plain"
                                    }
                                    context.startActivity(intent)

                                },
                                modifier = Modifier.align(Alignment.CenterVertically)
                            ) {
                                Icon(
                                    Icons.Filled.Share,
                                    contentDescription = "Favorite",
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Text(
                            text = title.substring(title.indexOf(':') + 2, title.length),
                            style = TextStyle(fontSize = 30.sp),
                            modifier = Modifier.padding(bottom = 5.dp),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = getDateText(sheetDisturbance!!.start_time, sheetDisturbance!!.end_time),
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                        Text(
                            text = "Beschreibung:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = descriptions[0].description,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                        for(i in 1 until descriptions.size) {
                            val descriptionDate = descriptions[i].time.substring(0, descriptions[i].time.indexOf('.'))

                            if(initialDate.equals(descriptionDate.substring(0,10))) {
                                Text(
                                    text = "Update: ${formatStringDate(descriptionDate, 3)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            } else {
                                Text(
                                    text = "Update: ${formatStringDate(descriptionDate, 1)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            }
                            Text(text = descriptions[i].description)
                        }
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .nestedScroll(connection = pullRefreshState.nestedScrollConnection)
                .fillMaxSize()
        ) {
            PullToRefreshContainer(
                state = pullRefreshState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .zIndex(10F)
                    .padding(top = 64.dp)
            )
            if(disturbanceList.isEmpty())
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp).padding(top = 112.dp).align(Alignment.TopCenter)
                )
            LazyColumn(
                modifier = Modifier
                    .padding(it)
                    .padding(horizontal = 5.dp)
                    .fillMaxSize()
            ) {
                items(disturbanceList) { disturbance ->
                    DisturbanceCard(
                        disturbance = disturbance,
                        modifier = Modifier
                            .padding(vertical = 5.dp)
                            .clickable {
                                sheetDisturbance = disturbance
                                showBottomSheet = true
                            }
                    )
                }
            }
            Text(
                text = errorMessage,
                color = Color.White,
                modifier = Modifier
                    .zIndex(11F)
                    .align(Alignment.TopCenter)
                    .padding(top = 100.dp)
            )
        }
    }
}


fun stringToDateTime(dateStr: String?, formatterFrom: DateTimeFormatter): LocalDateTime? {
    if (dateStr == null) return null
    return LocalDateTime.parse(dateStr, formatterFrom)
}
fun formatStringDate(dateStr : String, type : Int) : String {
    val parseFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val dateObj = LocalDateTime.parse(dateStr, parseFormatter)


    when(type) {
        1 -> return dateObj.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
        2 -> return dateObj.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        3 -> return dateObj.format(DateTimeFormatter.ofPattern("HH:mm"))
    }
    return dateObj.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
}

fun getDateText(startTime : String, endTime : String?) : String {
    val initialDate = formatStringDate(startTime, 2)
    var strEndTime : String? = endTime


    if(strEndTime != null && strEndTime.length > 19) {
        strEndTime = strEndTime.substringBefore('.')
    }

    var output = ""
    var prefix = ""

    if(strEndTime == null)
        prefix = "Seit"
    else
        prefix = "Von"

    if(initialDate.equals(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))))
        output = "$prefix heute ${formatStringDate(startTime, 3)}"
    else
        output = "$prefix ${formatStringDate(startTime, 1)}"

    if(strEndTime == null)
        return output
    else
        if(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")).equals(formatStringDate(strEndTime, 2))) {
            if(initialDate.equals(formatStringDate(strEndTime, 2)))
                output += " bis ${formatStringDate(strEndTime, 3)}"
            else
                output += " bis heute ${formatStringDate(strEndTime, 3)}"
        } else {
            if(initialDate.equals(formatStringDate(strEndTime, 2)))
                output += " bis ${formatStringDate(strEndTime, 3)}"
            else
                output += " bis ${formatStringDate(strEndTime, 1)}"
        }

    return output
}