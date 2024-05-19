package com.example.wls_android.composables

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wls_android.data.Disturbance
import com.example.wls_android.screens.getDateText

@Composable
fun DisturbanceCard(
    disturbance: Disturbance,
    modifier: Modifier = Modifier
) {
    var endTime: String? = disturbance.end_time
    val startTime: String = disturbance.start_time

    if (endTime != null) {
        try {
            endTime = endTime.substring(0, endTime.indexOf('.'))
        } catch (_: Exception) {
        }
    }

    val lines = disturbance.lines
    val title = disturbance.title
    val index = title.indexOf(':')
    val text = if (index != -1 && index + 2 < title.length) { // TODO: vllt Variable umbennen, keine Ahnung was das ist?
        title.substring(index + 2, title.length)
    } else {
        title
    }

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
            }
            Text(
                text = text,
                modifier = Modifier
                    .padding(start = 5.dp)
                    .fillMaxWidth(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = getDateText(startTime, endTime),
                modifier = Modifier
                    .padding(start = 5.dp)
                    .fillMaxWidth(),
                fontSize = 18.sp,
                //fontWeight = FontWeight.Bold
            )
        }
    }
}