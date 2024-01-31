package com.example.wls_android

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wls_android.data.Disturbance
import com.example.wls_android.data.RetrofitHelper
import com.example.wls_android.data.WlsApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {

    val gesturePadding = WindowInsets.navigationBars.asPaddingValues()

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember {
        mutableStateOf(false)
    }

    var sheetDisturbance by remember {
        mutableStateOf<Disturbance?>(null)
    }

    val wlsApi = RetrofitHelper.getInstance().create(WlsApi::class.java)

    val distList = remember {
        mutableStateListOf<Disturbance>()
    }

    LaunchedEffect(key1 = Unit) {
        val temp = wlsApi.getDisturbances()
        val body = temp.body()
        if (temp.code() == 200 && body != null) {
            distList.addAll(body.data)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.main_color),
                    titleContentColor = Color.White
                    //titleContentColor = MaterialTheme.colorScheme.onErrorContainer,
                ),
                title = {
                    Text(text = "WLS")
                }
            )
        }
    ) {
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                modifier = Modifier.wrapContentHeight(),
                sheetState = sheetState
            ) {
                if (sheetDisturbance != null) {
                    val initialDate = sheetDisturbance!!.start_time.substring(0,10)
                    val strStartTime = sheetDisturbance!!.start_time
                    var strEndTime : String? = null
                    if(sheetDisturbance!!.end_time != null) {
                        strEndTime = sheetDisturbance!!.end_time?.substring(0, sheetDisturbance!!.end_time!!.indexOf('.'))
                    }

                    val descriptions = sheetDisturbance!!.descriptions

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(10.dp)
                            .navigationBarsPadding()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            for (line in sheetDisturbance!!.lines) {
                                LineIcon(
                                    line = line,
                                    modifier = Modifier.padding(end = 10.dp, bottom = 5.dp)
                                )
                            }
                        }
                        Text(
                            text = sheetDisturbance!!.title.substring(
                                sheetDisturbance!!.title.indexOf(
                                    ':'
                                ) + 2, sheetDisturbance!!.title.length
                            ),
                            style = TextStyle(fontSize = 30.sp),
                            modifier = Modifier.padding(bottom = 5.dp),
                            fontWeight = FontWeight.Bold
                        )
                        if(strEndTime == null)
                            Text(
                                text = "Seit: ${formatStringDate(strStartTime, 1)}",
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                        else if(initialDate.equals(strEndTime!!.substring(0,10)))
                            Text(
                                text = "Von: ${formatStringDate(strStartTime, 1)} bis ${formatStringDate(strEndTime!!, 3)}",
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                        else
                            Text(
                                text = "Von: ${formatStringDate(strStartTime, 1)} bis ${formatStringDate(strEndTime!!, 1)}",
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
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .padding(6.dp)
                .fillMaxSize()
        ) {
            items(distList) { disturbance ->
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