package com.example.wls_android.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DisturbanceTypeFilters(
    disturbanceTypes: MutableList<MutableState<Boolean>>,
    modifier: Modifier = Modifier
) {

    val typeTexts = listOf(
        "Verspätungen",
        "Verkehrsunfälle",
        "Schadhafte Fahrzeuge",
        "Gleisschäden",
        "Weichenstörungen",
        "Fahrleitungsgebrechen",
        "Signalstörungen",
        "Rettungseinsätze",
        "Polizeieinsätze",
        "Feuerwehreinsätze",
        "Falschparker",
        "Demonstrationen",
        "Veranstaltungen",
        "Sonstige"
    )

    Column(
        modifier = modifier.fillMaxWidth()
    ) {

        Text(
            text = "Störungstypen",
            fontSize = 20.sp,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        Column(modifier = Modifier.padding(horizontal = 10.dp)) {
            for(i in 0..13) {
                DisturbanceType(label = typeTexts[i], checked = disturbanceTypes[i])
            }
        }

    }
}

@Composable
fun DisturbanceType(
    label : String,
    checked : MutableState<Boolean>
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 5.dp)
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