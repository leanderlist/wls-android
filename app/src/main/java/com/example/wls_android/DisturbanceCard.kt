package com.example.wls_android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wls_android.data.Disturbance
import java.time.format.DateTimeFormatter

@Composable
fun DisturbanceCard(
    disturbance: Disturbance,
    modifier: Modifier = Modifier
) {
    val parseFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val formatFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    //Log.e("ALARM", disturbance.end_time.substring(0,disturbance.end_time.indexOf('.')-1))
    var strEndTime = disturbance.end_time
    val strStartTime = disturbance.start_time

    var endTime : String? = null
    var startTime : String? = null

    if (strEndTime != null) {
        try {
            strEndTime = strEndTime.substring(0, strEndTime.indexOf('.'))
        } catch (_: Exception) {}
    }

    stringToDateTime(strStartTime, parseFormatter)?.let {
        startTime = it.format(formatFormatter)
    }

    stringToDateTime(strEndTime, parseFormatter)?.let {
        endTime = it.format(formatFormatter)
    }

    val lines = disturbance.lines
    val title = disturbance.title

    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
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
                for (line in lines) {
                    LineIcon(
                        line = line,
                        modifier = Modifier.padding(5.dp)
                    )
                }
                if (disturbance.end_time == null) {
                    if (startTime != null && endTime == null) {
                        Text(
                            text = "Seit: $startTime",
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
                        if (startTime != null && endTime != null) {
                            Text(
                                text = "Von: $startTime",
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth(),
                                //color = colorResource(id = R.color.main_color)
                            )
                            Text(
                                text = "Bis: $endTime",
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