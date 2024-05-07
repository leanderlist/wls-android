package com.example.wls_android.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(navController: NavHostController, filterData: FilterData) {

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
        List<MutableState<Boolean>>(14) { mutableStateOf(true) }.toMutableList()
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
                    if (!filterData.filters.isEmpty()) {
                        lineStateList.clear()
                        for (line in body.data) {
                            lineStateList.add(LineStatePair(line, false))
                        }
                    } else {
                        for (line in body.data) {
                            lineStateList.add(LineStatePair(line, true))
                        }
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



    if (!filterData.filters.isEmpty()) {

        //parse enabled lines
        val lineList = filterData.filters["line"]?.split(",")?.toMutableList()
        Log.e("awhawg", lineList.toString())
        if (lineList != null) {
            lineStateList.forEachIndexed { index, pair ->
                if(pair.line.id in lineList) {
                    lineList.remove(pair.line.id)
                    lineStateList[index] = LineStatePair(pair.line, true)
                }
            }
            /*
            for(pair in lineStateList) {
                if(pair.line.id in lineList) {
                    lineList.remove(pair.line.id)
                }
            }*/
        }

        //parse types
        val typeListStr = filterData.filters["type"]?.split(",")
        val typeList : List<Int>? = typeListStr?.map { it.toInt() }
        if (typeList != null) {
            disturbanceTypes.forEachIndexed { index, value ->
                disturbanceTypes[index] = mutableStateOf(index in typeList)
            }
        }

        //parse from & to
        val parseFromDate = filterData.filters["from"]
        if(parseFromDate != null) {
            fromDate = LocalDate.parse(parseFromDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay()
        }
        val parseToDate = filterData.filters["to"]
        if(parseToDate != null) {
            fromDate = LocalDate.parse(parseToDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay()
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
                    Text(text = "Filter")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .padding(top = 15.dp, bottom = 10.dp)
                        .clickable(
                            indication = null,
                            interactionSource = null
                        ) { active = !active }
                ) {
                    Checkbox(
                        checked = active,
                        onCheckedChange = { active = !active },
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .size(20.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "Nur offene Störungen anzeigen",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                }
                ExposedDropdownMenuBox(
                    expanded = dropDownExpanded,
                    onExpandedChange = { change -> dropDownExpanded = change },
                    modifier = Modifier.padding(horizontal = 10.dp)
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
                            .padding(start = 10.dp, end = 5.dp),
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
                                    fromDate =
                                        convertMillisToLocalDateTime(fromPickerState.selectedDateMillis)
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
                            .padding(start = 5.dp, end = 10.dp),
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
                                    toDate =
                                        convertMillisToLocalDateTime(toPickerState.selectedDateMillis)
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 10.dp),
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

                        //type parameter
                        val typeStringBuilder = StringBuilder("")
                        for (i in 0 until disturbanceTypes.size) {
                            if (disturbanceTypes[i].value) {
                                typeStringBuilder.append("$i,")
                            }
                        }
                        typeStringBuilder.setLength(typeStringBuilder.length - 1)
                        filterData.addFilter("type", typeStringBuilder.toString())

                        //order parameter
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
                        }

                        //date filters
                        filterData.addFilter(
                            "from",
                            fromDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        )
                        filterData.addFilter(
                            "to",
                            toDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        )

                        Log.e(lineStateList.toList().size.toString(), lineStateList.toList().toString())

                        navController.popBackStack()
                    },
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
