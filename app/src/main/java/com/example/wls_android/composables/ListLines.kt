package com.example.wls_android.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wls_android.model.LineStatePair

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ListLines(title : String, lineList : List<LineStatePair>, stateList : SnapshotStateList<LineStatePair>, type: Int) {
    Text(
        text = title,
        textAlign = TextAlign.Start,
        fontSize = 20.sp,
        modifier = Modifier.padding(top = 5.dp)
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
            .clickable {
                stateList.forEachIndexed { index, element ->
                    if (element.line.type == type) {
                        stateList[index] = element.copy(enabled = !element.enabled)
                    }
                }
            }
    )
    FlowRow(
        modifier = Modifier.wrapContentSize()
    ) {
        lineList.forEachIndexed { index, element ->
            val line = element.line
            val enabled = element.enabled
            LineIcon(modifier = Modifier.padding(5.dp),
                line = line,
                enabledState = !enabled,
                onClick = {
                    val statePairIndex = stateList.indexOf(element)
                    stateList[statePairIndex] = stateList[statePairIndex].copy(enabled = !enabled)
                }
            )
        }
    }
}