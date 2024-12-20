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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.wls_android.composables.DisturbanceCard
import com.example.wls_android.composables.LineIcon
import com.example.wls_android.composables.WlsHeader
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
fun DisturbanceListScreen(
    navController: NavHostController,
    filterData: FilterData,
    disturbanceIdToOpen: String?
) {

    val context = LocalContext.current
    val scrollState = rememberLazyListState()
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

    var spinnerLoading by remember {
        mutableStateOf(true)
    }

    val snackBarHost = remember {
        SnackbarHostState()
    }

    val scope = rememberCoroutineScope()

    val pullRefreshState = rememberPullToRefreshState()

    val filters: SnapshotStateMap<String, String> = filterData.filters

    if (pullRefreshState.isRefreshing) {
        LaunchedEffect(Unit) {
            try {
                val client = getKtorClient("/api/disturbances")
                val response = client.get {
                    url {
                        if (filters.isEmpty()) {
                            parameters.append(
                                "from",
                                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            )
                            parameters.append(
                                "to",
                                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            )
                        } else {
                            for (entry in filters.toMap()) {
                                parameters.append(entry.key, entry.value)
                            }
                        }
                    }
                }
                val body = response.body<Data>()
                if (response.status.value in 200..299) {
                    disturbanceList.clear()
                    disturbanceList.addAll(body.data)
                    spinnerLoading = false
                    errorMessage = ""
                    spinnerLoading = false
                } else {
                    errorMessage = "Es sind keine Störungen vorhanden"
                    spinnerLoading = false
                }
            } catch (e: Exception) {
                snackBarHost.showSnackbar("Es konnte keine Verbindung hergestellt werden")
                spinnerLoading = false
            }
            pullRefreshState.endRefresh()
        }
    }

    LaunchedEffect(key1 = Unit) {
        try {
            val client = getKtorClient("/api/disturbances")
            val response = client.get {
                url {
                    if (filters.isEmpty()) {
                        parameters.append(
                            "from",
                            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        )
                        parameters.append(
                            "to",
                            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        )
                    } else {
                        for (entry in filters.toMap()) {
                            parameters.append(entry.key, entry.value)
                        }
                    }
                }
            }
            val body = response.body<Data>()
            if (response.status.value in 200..299) {
                disturbanceList.addAll(body.data)
                spinnerLoading = false

                // Open the sheet with the disturbance if disturbanceIdToOpen is not null
                disturbanceIdToOpen?.let { disturbanceId ->
                    val disturbance = disturbanceList.find { it.id == disturbanceId }
                    if (disturbance != null) {
                        sheetDisturbance = disturbance
                        showBottomSheet = true
                    } else {
                        snackBarHost.showSnackbar("Gewählte Störung nicht gefunden")
                    }
                }
            } else {
                errorMessage = "Es sind keine Störungen vorhanden"
                spinnerLoading = false
            }
        } catch (e: Exception) {
            errorMessage = "Es konnte keine Verbindung hergestellt werden"
            spinnerLoading = false
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.Filter.route) }) {
                Icon(
                    imageVector = Icons.Filled.FilterAlt,
                    contentDescription = "Open Filter screen"
                )
            }
        },
        topBar = {
            WlsHeader(navController)
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHost)
        }
    ) {
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
            ) {
                if (sheetDisturbance != null) {
                    val initialDate = sheetDisturbance!!.start_time.substring(0, 10)
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
                                    val url =
                                        "https://wls.byleo.net/stoerung/${sheetDisturbance!!.id}"
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
                                    contentDescription = "Share disturbance",
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
                            text = getDateText(
                                sheetDisturbance!!.start_time,
                                sheetDisturbance!!.end_time
                            ),
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                        HorizontalDivider(Modifier.padding(bottom = 10.dp))
                        Column(
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                                .weight(weight = 1f, fill = false)
                        ) {

                            Text(
                                text = "Beschreibung:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            Text(
                                text = descriptions[0].description,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                            for (i in 1 until descriptions.size) {
                                val descriptionDate =
                                    descriptions[i].time.substring(
                                        0,
                                        descriptions[i].time.indexOf('.')
                                    )

                                if (initialDate.equals(descriptionDate.substring(0, 10))) {
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
            if (spinnerLoading)
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp)
                        .padding(top = 112.dp)
                        .align(Alignment.TopCenter)
                )
            LazyColumn(
                state = scrollState,
                modifier = Modifier
                    .padding(it)
                    .padding(horizontal = 5.dp)
                    .fillMaxSize()
            ) {
                items(disturbanceList) { disturbance ->
                    DisturbanceCard(
                        disturbance = disturbance,
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .clickable {
                                sheetDisturbance = disturbance
                                showBottomSheet = true
                            }
                    )
                }
            }
            Text(
                text = errorMessage,
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

fun formatStringDate(dateStr: String, type: Int): String {
    val parseFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val dateObj = LocalDateTime.parse(dateStr, parseFormatter)


    when (type) {
        1 -> return dateObj.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
        2 -> return dateObj.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        3 -> return dateObj.format(DateTimeFormatter.ofPattern("HH:mm"))
    }
    return dateObj.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
}

fun getDateText(startTime: String, endTime: String?): String {
    val initialDate = formatStringDate(startTime, 2)
    var strEndTime: String? = endTime


    if (strEndTime != null && strEndTime.length > 19) {
        strEndTime = strEndTime.substringBefore('.')
    }

    var output = ""
    var prefix = ""

    if (strEndTime == null)
        prefix = "Seit"
    else
        prefix = "Von"

    if (initialDate.equals(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))))
        output = "$prefix heute ${formatStringDate(startTime, 3)}"
    else
        output = "$prefix ${formatStringDate(startTime, 1)}"

    if (strEndTime == null)
        return output
    else
        if (LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                .equals(formatStringDate(strEndTime, 2))
        ) {
            if (initialDate.equals(formatStringDate(strEndTime, 2)))
                output += " bis ${formatStringDate(strEndTime, 3)}"
            else
                output += " bis heute ${formatStringDate(strEndTime, 3)}"
        } else {
            if (initialDate.equals(formatStringDate(strEndTime, 2)))
                output += " bis ${formatStringDate(strEndTime, 3)}"
            else
                output += " bis ${formatStringDate(strEndTime, 1)}"
        }

    return output
}