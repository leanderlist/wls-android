package com.example.wls_android.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wls_android.model.LineStatePair

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DisturbanceLineFilters(
    disturbanceLines: SnapshotStateList<LineStatePair>,
    modifier: Modifier = Modifier
) {
    val busList = disturbanceLines.toList().filter { it.line.type == 0 } //3
    val tramList = disturbanceLines.toList().filter { it.line.type == 1 } //2
    val metroList = disturbanceLines.toList().filter { it.line.type == 2 } //1
    val miscList = disturbanceLines.toList().filter { it.line.type == 3 } //4
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Linien",
            fontSize = 20.sp,
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 10.dp)

        )
        Row(
            modifier = Modifier
                .padding(vertical = 5.dp, horizontal = 10.dp)
                .fillMaxWidth()
        ) {
            Button(
                modifier = Modifier
                    .weight(1F)
                    .padding(end = 1.dp),
                onClick = {
                    disturbanceLines.forEachIndexed { index, element ->
                        disturbanceLines[index] = element.copy(enabled = true)
                    }
                },
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    bottomStart = 20.dp,
                    topEnd = 0.dp,
                    bottomEnd = 0.dp
                )
            ) {
                Text(text = "Alle auswählen")
            }
            Button(
                modifier = Modifier
                    .weight(1F)
                    .padding(start = 1.dp),
                onClick = {
                    disturbanceLines.forEachIndexed { index, element ->
                        disturbanceLines[index] = element.copy(enabled = false)
                    }
                },
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    bottomStart = 0.dp,
                    topEnd = 20.dp,
                    bottomEnd = 20.dp
                )
            ) {
                Text(text = "Alle abwählen")
            }
        }
        ListLines(
            title = "U-Bahnen",
            lineList = metroList,
            stateList = disturbanceLines
        )
        ListLines(
            title = "Straßenbahnen",
            lineList = tramList,
            stateList = disturbanceLines
        )
        ListLines(
            title = "Busse",
            lineList = busList,
            stateList = disturbanceLines
        )
        ListLines(
            title = "Sonstiges",
            lineList = miscList,
            stateList = disturbanceLines
        )
    }
}