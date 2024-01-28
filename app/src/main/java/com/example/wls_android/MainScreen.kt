package com.example.wls_android

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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
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
    val parseFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    //Log.e("ALARM", disturbance.end_time.substring(0,disturbance.end_time.indexOf('.')-1))
    var strEndTime = disturbance.end_time
    var strStartTime = disturbance.start_time

    if (strEndTime != null) {
        strEndTime = strEndTime.substring(0, strEndTime.indexOf('.'))
        if (strEndTime.length == 17)
            strEndTime += "0"
    }

    if (strStartTime.length == 17)
        strStartTime += "0"

    val end_time = stringToDateTime(strEndTime, parseFormatter)
    val start_time = stringToDateTime(disturbance.start_time, parseFormatter)

    val id = disturbance.id
    val lines = disturbance.lines
    val title = disturbance.title

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
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                var color = colorResource(id = R.color.line_bus)
                for (line in lines) {
                    when (line.type) {
                        0 -> color = colorResource(id = R.color.line_bus)
                        1 -> color = colorResource(id = R.color.line_tram)
                        2 -> {
                            if (line.id.startsWith("U1", true)) color =
                                colorResource(id = R.color.line_u1)
                            else if (line.id.startsWith("U2", true)) color =
                                colorResource(id = R.color.line_u2)
                            else if (line.id.startsWith("U3", true)) color =
                                colorResource(id = R.color.line_u3)
                            else if (line.id.startsWith("U4", true)) color =
                                colorResource(id = R.color.line_u4)
                            else if (line.id.startsWith("U6", true)) color =
                                colorResource(id = R.color.line_u6)
                        }

                        else -> color = colorResource(id = R.color.line_miscellaneous)
                    }

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = color
                        ),
                        shape = RoundedCornerShape(5.dp),
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(5.dp)

                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = if (line.id.length <= 3) Modifier
                                .wrapContentHeight()
                                .width(40.dp) else Modifier.wrapContentSize()
                        ) {
                            Text(
                                text = line.id,
                                modifier = Modifier.padding(2.dp),
                                color = Color.White
                            )
                        }
                    }
                }
                if (disturbance.end_time == null) {
                    if (start_time != null) {
                        Text(
                            text = "Seit: ${start_time.dayOfMonth}.${if (start_time.month.value < 10) "0" + start_time.month.value else start_time.month.value}.${start_time.year} ${start_time.hour}:${start_time.minute}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp),
                            textAlign = TextAlign.End,
                            //color = colorResource(id = R.color.main_color)
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (start_time != null && end_time != null) {
                            Text(
                                text = "Von: ${start_time.dayOfMonth}.${if (start_time.month.value < 10) "0" + start_time.month.value else start_time.month.value}.${start_time.year} ${start_time.hour}:${start_time.minute}",
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth(),
                                //color = colorResource(id = R.color.main_color)
                            )
                            Text(
                                text = "Bis: ${end_time.dayOfMonth}.${if (end_time.month.value < 10) "0" + end_time.month.value else end_time.month.value}.${end_time.year} ${end_time.hour}:${end_time.minute}",
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth(),
                                //color = colorResource(id = R.color.main_color)
                            )
                        }
                    }
                }
            }
            Text(
                text = title.substring(title.indexOf(':') + 2, title.length),
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth(),
                fontSize = 18.sp,
                //fontWeight = FontWeight.Bold
            )
        }
    }
}

fun stringToDateTime(dateStr: String?, formatterFrom: DateTimeFormatter): LocalDateTime? {
    if (dateStr == null) return null
    return LocalDateTime.parse(dateStr, formatterFrom)
}