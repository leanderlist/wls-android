package com.example.wls_android.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.wls_android.R
import com.example.wls_android.composables.DisturbanceLineFilters
import com.example.wls_android.composables.DisturbanceTypeFilters
import com.example.wls_android.data.Line
import com.example.wls_android.data.LineData
import com.example.wls_android.data.getKtorClient
import com.example.wls_android.model.LineStatePair
import com.example.wls_android.viewmodel.FilterData
import io.ktor.client.call.body
import io.ktor.client.request.get
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(navController: NavHostController, filterData: FilterData) {

    val interactionSource = remember {
        MutableInteractionSource()
    }

    var active by remember {
        mutableStateOf(false)
    }

    var dropDownExpanded by remember {
        mutableStateOf(false)
    }
    var dropDownValue by remember {
        mutableStateOf("Startzeit - neuste zuerst")
    }

    var fromDate by remember {
        mutableStateOf(LocalDateTime.now())//.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
    }

    var toDate by remember {
        mutableStateOf(LocalDateTime.now())//.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
    }

    var disturbanceTypes = remember {
        List(14) { true }.toMutableStateList()
    }

    var lineStateList = remember {
        mutableStateListOf<LineStatePair>()
    }

    var fromPickerState = rememberDatePickerState()
    var toPickerState = rememberDatePickerState()

    var showFromPicker by remember {
        mutableStateOf(false)
    }
    var showToPicker by remember {
        mutableStateOf(false)
    }

    var linesList = mutableListOf<Line>()
    var errorMessage: String = ""
    //Log.e("ZAHLEN", disturbanceTypes.toMap().toString())

    LaunchedEffect(key1 = Unit) {
        try {
            val client = getKtorClient("/api/lines")
            val response = client.get {}
            val body = response.body<LineData>()
            if (response.status.value in 200..299) {
                if (body != null) {
                    lineStateList.clear()
                    for (line in body.data) {
                        lineStateList.add(LineStatePair(line, true))
                    }
                } else
                    errorMessage = "Es konnten keine Linien gefunden werden"
            } else {
                errorMessage = "Es sind keine Störungen vorhanden"
            }
        } catch (e: Exception) {
            errorMessage = e.printStackTrace().toString()
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.main_color),
                    titleContentColor = Color.White
                ),
                title = {
                    Text(text = "WLS")
                },
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .padding(10.dp)
                .fillMaxSize()
        ) {
            item {
                Text(
                    text = "Filter",
                    fontSize = 30.sp
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Checkbox(
                        checked = active,
                        onCheckedChange = { active = !active },
                        modifier = Modifier.align(Alignment.CenterVertically),
                    )
                    Text(
                        text = "Nur offene Störungen anzeigen",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .clickable(
                                indication = null,
                                interactionSource = interactionSource
                            ) { active = !active }
                    )
                }
                ExposedDropdownMenuBox(
                    expanded = dropDownExpanded,
                    onExpandedChange = { change -> dropDownExpanded = change }
                ) {
                    TextField(
                        value = dropDownValue,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropDownExpanded)
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = dropDownExpanded,
                        onDismissRequest = { dropDownExpanded = !dropDownExpanded }
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = "Startzeit - neuste zuerst") },
                            onClick = {
                                dropDownValue = "Startzeit - neuste zuerst"
                                dropDownExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(text = "Startzeit - älteste zuerst") },
                            onClick = {
                                dropDownValue = "Startzeit - älteste zuerst"
                                dropDownExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(text = "Endzeit - neuste zuerst") },
                            onClick = {
                                dropDownValue = "Endzeit - neueste zuerst"
                                dropDownExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(text = "Endzeit - älteste zuerst") },
                            onClick = {
                                dropDownValue = "Endzeit - älteste zuerst"
                            }
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                ) {
                    TextField(
                        value = fromDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                        onValueChange = {},
                        label = {
                            Text(text = "Startdatum")
                        },
                        readOnly = true,
                        modifier = Modifier
                            .weight(1F)
                            .padding(end = 2.5F.dp),
                        interactionSource = remember { MutableInteractionSource() }
                            .also { interactionSource ->
                                LaunchedEffect(interactionSource) {
                                    interactionSource.interactions.collect { interaction ->
                                        if (interaction is PressInteraction.Release) {
                                            showFromPicker = true
                                        }
                                    }
                                }
                            }
                    )
                    if (showFromPicker) {
                        DatePickerDialog(
                            onDismissRequest = {},
                            confirmButton = {
                                Button(onClick = {
                                    fromDate = convertMillisToLocalDateTime(fromPickerState.selectedDateMillis)
                                    showFromPicker = false
                                }
                                ) {
                                    Text(text = "Anwenden")
                                }
                            },
                            dismissButton = {
                                Button(onClick = { showFromPicker = false }) {
                                    Text(text = "Abbrechen")
                                }
                            }
                        ) {
                            DatePicker(state = fromPickerState)
                        }
                    }

                    TextField(
                        value = toDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                        onValueChange = {},
                        label = {
                            Text(text = "Enddatum")
                        },
                        readOnly = true,
                        modifier = Modifier
                            .weight(1F)
                            .padding(end = 2.5F.dp),
                        interactionSource = remember { MutableInteractionSource() }
                            .also { interactionSource ->
                                LaunchedEffect(interactionSource) {
                                    interactionSource.interactions.collect { interaction ->
                                        if (interaction is PressInteraction.Release) {
                                            showToPicker = true
                                        }
                                    }
                                }
                            }
                    )
                    if (showToPicker) {
                        DatePickerDialog(
                            onDismissRequest = {},
                            confirmButton = {
                                Button(onClick = {
                                    toDate = convertMillisToLocalDateTime(toPickerState.selectedDateMillis)
                                    showToPicker = false
                                }
                                ) {
                                    Text(text = "Anwenden")
                                }
                            },
                            dismissButton = {
                                Button(onClick = { showToPicker = false }) {
                                    Text(text = "Abbrechen")
                                }
                            }
                        ) {
                            DatePicker(state = toPickerState)
                        }
                    }
                }

                DisturbanceTypeFilters(
                    disturbanceTypes = disturbanceTypes,
                    modifier = Modifier.padding(vertical = 5.dp)
                )
                DisturbanceLineFilters(
                    disturbanceLines = lineStateList,
                    modifier = Modifier.padding(vertical = 5.dp)
                )
                Button(

                    onClick = {
                        filterData.resetFilters()

                        //line parameter
                        val lineStringBuilder = StringBuilder("")
                        for (pair in lineStateList) {
                            if (pair.enabled)
                                lineStringBuilder.append("${pair.line.id},")
                        }
                        lineStringBuilder.setLength(lineStringBuilder.length - 1)
                        filterData.addFilter("line", lineStringBuilder.toString())

                        Log.e("lineData", lineStringBuilder.toString())

                        //type parameter
                        val typeStringBuilder = StringBuilder("")
                        for (i in 0 until disturbanceTypes.size) {
                            if (disturbanceTypes[i] == true) {
                                typeStringBuilder.append("$i,")
                            }
                        }
                        typeStringBuilder.setLength(typeStringBuilder.length - 1)
                        filterData.addFilter("type", typeStringBuilder.toString())

                        Log.e("typeData", typeStringBuilder.toString())

                        when (dropDownValue) {
                            "Startzeit - neuste zuerst" -> {
                                filterData.addFilter("order", "start")
                                filterData.addFilter("desc", "true")
                            }

                            "Startzeit - älteste zuerst" -> {
                                filterData.addFilter("order", "start")
                                filterData.addFilter("desc", "false")
                            }

                            "Endzeit - neuste zuerst" -> {
                                filterData.addFilter("order", "end")
                                filterData.addFilter("desc", "true")
                            }

                            "Endzeit - älteste zuerst" -> {
                                filterData.addFilter("order", "end")
                                filterData.addFilter("desc", "false")
                            }
                        }
                        if (active) {
                            filterData.addFilter("active", "true")
                            Log.e("active", "true")
                        }

                        filterData.addFilter("from", fromDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        Log.e("from", fromDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        filterData.addFilter("to", toDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        Log.e("to", toDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))

                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Filter Anwenden")
                }
            }
        }
    }
}

fun convertMillisToLocalDateTime(millis: Long?): LocalDateTime {
    return if (millis != null)
        Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime()
    else
        LocalDateTime.now()
}
