package com.example.wls_android.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
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
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Linien",
            fontSize = 20.sp,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp)
        ) {
            Button(
                modifier = Modifier
                    .weight(1F)
                    .padding(end = 1.dp, start = 10.dp),
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
                    .padding(start = 1.dp, end = 10.dp),
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
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            disturbanceLines.forEachIndexed { index, element ->
                val line = element.line
                val enabled = element.enabled
                LineIcon(
                    modifier = Modifier.padding(5.dp),
                    line = line,
                    enabledState = !enabled,
                    onClick = {
                        disturbanceLines[index] = disturbanceLines[index].copy(enabled = !enabled)
                    }
                )
            }
        }
    }
}