package at.wls_android.app.composables

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowLeft
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.wls_android.app.R
import at.wls_android.app.model.LineStatePair

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ListLines(
    title: String,
    lineList: List<LineStatePair>,
    stateList: SnapshotStateList<LineStatePair>,
    type: Int
) {
    val showLines = remember {
        mutableStateOf(false)
    }
    Card(
        onClick = { showLines.value = !showLines.value },
        modifier = Modifier
            .padding(top = 5.dp)
            .padding(horizontal = 10.dp)
    ) {
        Row {
            Text(
                text = title,
                textAlign = TextAlign.Start,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(5.dp)
                    .padding(end = 10.dp)
                    .wrapContentWidth()
                    .align(Alignment.CenterVertically)
            )
            HorizontalDivider(
                thickness = 2.dp,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1F),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Icon(
                (if (!showLines.value) Icons.Rounded.ArrowLeft else Icons.Rounded.ArrowDropDown),
                contentDescription = stringResource(R.string.app_name),
                tint = Color.White,
                modifier = Modifier
                    .padding(5.dp)
                    .align(Alignment.CenterVertically)
            )
        }
    }

    if (showLines.value) {
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
                        stateList[statePairIndex] =
                            stateList[statePairIndex].copy(enabled = !enabled)
                    }
                )
            }
        }
    }
}