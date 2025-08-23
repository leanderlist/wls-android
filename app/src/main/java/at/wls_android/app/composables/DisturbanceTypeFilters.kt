package at.wls_android.app.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowLeft
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.wls_android.app.enums.DisturbanceType

@Composable
fun DisturbanceTypeFilters(
    stateList: SnapshotStateList<MutableState<Boolean>>,
    modifier: Modifier = Modifier
) {
    val showTypes = remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Card(
            onClick = { showTypes.value = !showTypes.value },
            modifier = Modifier
                .padding(top = 5.dp)
        ) {
            Row {
                Text(
                    text = "Störungstypen",
                    textAlign = TextAlign.Start,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(8.dp)
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
                    (if (!showTypes.value) Icons.AutoMirrored.Rounded.ArrowLeft else Icons.Rounded.ArrowDropDown),
                    tint = Color.White,
                    modifier = Modifier
                        .padding(5.dp)
                        .align(Alignment.CenterVertically),
                    contentDescription = null
                )
            }
        }

        if (showTypes.value) {
            Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                DisturbanceType.entries.forEachIndexed { index, disturbanceType ->
                    DisturbanceType(label = disturbanceType.text, checked = stateList[index])
                }
            }
        }
    }
}

@Composable
fun DisturbanceType(
    label: String,
    checked: MutableState<Boolean>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp, horizontal = 10.dp)
            .clickable(
                interactionSource = null,
                indication = null
            ) { checked.value = !checked.value }
    ) {
        Checkbox(
            checked = checked.value,
            onCheckedChange = { checked.value = !checked.value },
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(20.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = label,
            modifier = Modifier
                .align(Alignment.CenterVertically)
        )
    }
}
