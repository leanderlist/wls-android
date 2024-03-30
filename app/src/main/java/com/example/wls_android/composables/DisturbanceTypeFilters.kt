package com.example.wls_android.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DisturbanceTypeFilters(
    disturbanceTypes: SnapshotStateList<Boolean>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {

        Text(
            text = "Störungstypen",
            fontSize = 20.sp,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        Column(modifier = Modifier.padding(horizontal = 10.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Checkbox(
                    checked = disturbanceTypes[0],
                    onCheckedChange = { disturbanceTypes[0] = !disturbanceTypes[0] },
                    modifier = Modifier.align(Alignment.CenterVertically)
                        .size(20.dp)
                        .padding(10.dp)
                )
                Text(
                    text = "Verspätungen",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clickable { disturbanceTypes[0] = !disturbanceTypes[0] }
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Checkbox(
                    checked = disturbanceTypes[1],
                    onCheckedChange = { disturbanceTypes[1] = !disturbanceTypes[1] },
                    modifier = Modifier.align(Alignment.CenterVertically)
                        .size(20.dp)
                        .padding(10.dp)
                )
                Text(
                    text = "Verkehrsunfälle",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clickable { disturbanceTypes[1] = !disturbanceTypes[1] }
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Checkbox(
                    checked = disturbanceTypes[2],
                    onCheckedChange = { disturbanceTypes[2] = !disturbanceTypes[2] },
                    modifier = Modifier.align(Alignment.CenterVertically)
                        .size(20.dp)
                        .padding(10.dp)
                )
                Text(
                    text = "Schadhafte Fahrzeuge",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clickable { disturbanceTypes[2] = !disturbanceTypes[2] }
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Checkbox(
                    checked = disturbanceTypes[3],
                    onCheckedChange = { disturbanceTypes[3] = !disturbanceTypes[3] },
                    modifier = Modifier.align(Alignment.CenterVertically)
                        .size(20.dp)
                        .padding(10.dp)
                )
                Text(
                    text = "Gleisschäden",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clickable { disturbanceTypes[3] = !disturbanceTypes[3] }
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Checkbox(
                    checked = disturbanceTypes[4],
                    onCheckedChange = { disturbanceTypes[4] = !disturbanceTypes[4] },
                    modifier = Modifier.align(Alignment.CenterVertically)
                        .size(20.dp)
                        .padding(10.dp)
                )
                Text(
                    text = "Weichenstörungen",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clickable { disturbanceTypes[4] = !disturbanceTypes[4] }
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Checkbox(
                    checked = disturbanceTypes[5],
                    onCheckedChange = { disturbanceTypes[5] = !disturbanceTypes[5] },
                    modifier = Modifier.align(Alignment.CenterVertically)
                        .size(20.dp)
                        .padding(10.dp)
                )
                Text(
                    text = "Fahrleitungsgebrechen",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clickable { disturbanceTypes[5] = !disturbanceTypes[5] }
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Checkbox(
                    checked = disturbanceTypes[6],
                    onCheckedChange = { disturbanceTypes[6] = !disturbanceTypes[6] },
                    modifier = Modifier.align(Alignment.CenterVertically)
                        .size(20.dp)
                        .padding(10.dp)
                )
                Text(
                    text = "Signalstörungen",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clickable { disturbanceTypes[6] = !disturbanceTypes[6] }
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Checkbox(
                    checked = disturbanceTypes[7],
                    onCheckedChange = { disturbanceTypes[7] = !disturbanceTypes[7] },
                    modifier = Modifier.align(Alignment.CenterVertically)
                        .size(20.dp)
                        .padding(10.dp)
                )
                Text(
                    text = "Rettunseinsätze",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clickable { disturbanceTypes[7] = !disturbanceTypes[7] }
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Checkbox(
                    checked = disturbanceTypes[8],
                    onCheckedChange = { disturbanceTypes[8] = !disturbanceTypes[8] },
                    modifier = Modifier.align(Alignment.CenterVertically)
                        .size(20.dp)
                        .padding(10.dp)
                )
                Text(
                    text = "Polizeieinsätze",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clickable { disturbanceTypes[8] = !disturbanceTypes[8] }
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Checkbox(
                    checked = disturbanceTypes[9],
                    onCheckedChange = { disturbanceTypes[9] = !disturbanceTypes[9] },
                    modifier = Modifier.align(Alignment.CenterVertically)
                        .size(20.dp)
                        .padding(10.dp)
                )
                Text(
                    text = "Feuerwehreinsätze",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clickable { disturbanceTypes[9] = !disturbanceTypes[9] }
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Checkbox(
                    checked = disturbanceTypes[10],
                    onCheckedChange = { disturbanceTypes[10] = !disturbanceTypes[10] },
                    modifier = Modifier.align(Alignment.CenterVertically)
                        .size(20.dp)
                        .padding(10.dp)
                )
                Text(
                    text = "Falschparker",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clickable { disturbanceTypes[10] = !disturbanceTypes[10] }
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Checkbox(
                    checked = disturbanceTypes[11],
                    onCheckedChange = { disturbanceTypes[11] = !disturbanceTypes[11] },
                    modifier = Modifier.align(Alignment.CenterVertically)
                        .size(20.dp)
                        .padding(10.dp)
                )
                Text(
                    text = "Demonstrationen",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clickable { disturbanceTypes[11] = !disturbanceTypes[11] }
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Checkbox(
                    checked = disturbanceTypes[12],
                    onCheckedChange = { disturbanceTypes[12] = !disturbanceTypes[12] },
                    modifier = Modifier.align(Alignment.CenterVertically)
                        .size(20.dp)
                        .padding(10.dp)
                )
                Text(
                    text = "Veranstaltungen",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clickable { disturbanceTypes[12] = !disturbanceTypes[12] }
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Checkbox(
                    checked = disturbanceTypes[13],
                    onCheckedChange = { disturbanceTypes[13] = !disturbanceTypes[13] },
                    modifier = Modifier.align(Alignment.CenterVertically)
                        .size(20.dp)
                        .padding(20.dp)
                )
                Text(
                    text = "Sonstige",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clickable { disturbanceTypes[13] = !disturbanceTypes[13] }
                )
            }
        }

    }
}