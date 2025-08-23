package at.wls_android.app.screens

import android.util.Log
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import at.wls_android.app.composables.DisturbanceLineFilters
import at.wls_android.app.composables.DisturbanceTypeFilters
import at.wls_android.app.composables.WlsHeader
import at.wls_android.app.data.Line
import at.wls_android.app.data.getKtorClient
import at.wls_android.app.enums.DisturbanceType
import at.wls_android.app.enums.OrderType
import at.wls_android.app.model.LineStatePair
import at.wls_android.app.viewmodel.FilterData
import io.ktor.client.call.body
import io.ktor.client.request.get
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    navController: NavHostController,
    filterData: FilterData
) {
    var onlyActive: Boolean by remember { mutableStateOf(filterData.filters["OnlyActive"]?.toBooleanStrictOrNull() ?: false) }
    var orderBy: OrderType by remember {
        mutableStateOf(
            OrderType.entries.find { it.name == filterData.filters["OrderBy"] } ?: OrderType.StartedAtDesc
        )
    }
    var orderByExpanded by remember { mutableStateOf(false) }

    val apiDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val displayDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    var fromDate: LocalDateTime by remember {
        mutableStateOf(
            filterData.filters["FromDate"]?.let { LocalDate.parse(it, apiDateFormatter).atStartOfDay() } ?: LocalDateTime.now()
        )
    }
    var toDate: LocalDateTime by remember {
        mutableStateOf(
            filterData.filters["ToDate"]?.let { LocalDate.parse(it, apiDateFormatter).atStartOfDay() } ?: LocalDateTime.now()
        )
    }

    val initialSelectedTypes = filterData.filters["Types"]?.split(",")?.toSet() ?: DisturbanceType.entries.map { it.name }.toSet()
    val disturbanceTypeBoolStates = remember {
        DisturbanceType.entries.map { type ->
            mutableStateOf(initialSelectedTypes.contains(type.name))
        }.toMutableStateList()
    }

    val lineStates = remember { mutableStateListOf<LineStatePair>() }

    val fromPickerState = rememberDatePickerState(
        initialSelectedDateMillis = fromDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
    val toPickerState = rememberDatePickerState(
        initialSelectedDateMillis = toDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )

    var showFromPicker by remember { mutableStateOf(false) }
    var showToPicker by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(key1 = Unit) {
        try {
            val client = getKtorClient("/api/lines")
            val response = client.get {}
            val body = response.body<List<Line>>()
            if (response.status.value in 200..299) {
                lineStates.clear()
                val selectedLineIds = filterData.filters["Lines"]?.split(",")?.toSet()
                if (selectedLineIds != null) {
                    body.forEach { line ->
                        lineStates.add(LineStatePair(line, selectedLineIds.contains(line.id)))
                    }
                } else {
                    body.forEach { line ->
                        lineStates.add(LineStatePair(line, true))
                    }
                }
            } else {
                errorMessage = "Fehler beim Laden der Linien: ${response.status}"
            }
        } catch (e: Exception) {
            Log.e("FilterScreen", "Error loading lines: ${e.message}", e)
            errorMessage = "Fehler beim Laden der Linien."
        }
    }

    fun applyFilters() {
        filterData.resetFilters()

        filterData.addFilter("OnlyActive", onlyActive.toString())
        filterData.addFilter("OrderBy", orderBy.name)
        filterData.addFilter("FromDate", fromDate.format(apiDateFormatter))
        filterData.addFilter("ToDate", toDate.format(apiDateFormatter))

        val selectedTypes = DisturbanceType.entries
            .filterIndexed { index, _ -> disturbanceTypeBoolStates[index].value }
            .joinToString(",") { it.name }
        if (selectedTypes.isNotEmpty()) {
            filterData.addFilter("Types", selectedTypes)
        }

        val selectedLines = lineStates.filter { it.enabled }.joinToString(",") { it.line.id }
        if (selectedLines.isNotEmpty()) {
            filterData.addFilter("Lines", selectedLines)
        }
        Log.d("FilterScreen", "Applied Filters: ${filterData.filters}")
        navController.previousBackStackEntry?.savedStateHandle?.set("filtersUpdated", true)
        navController.popBackStack()
    }

    Scaffold(
        topBar = { WlsHeader(navController, disableSettings = true) },
        bottomBar = {
            Button(
                onClick = { applyFilters() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding()
            ) {
                Text("Filter anwenden")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = androidx.compose.material3.MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
            }
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .padding(top = 18.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .toggleable(
                                value = onlyActive,
                                onValueChange = { onlyActive = it },
                                role = Role.Checkbox
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = onlyActive,
                            onCheckedChange = null
                        )
                        Text(
                            text = "Nur offene Störungen anzeigen",
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                item {
                    Text("Sortieren nach:", style = androidx.compose.material3.MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
                    ExposedDropdownMenuBox(
                        expanded = orderByExpanded,
                        onExpandedChange = { orderByExpanded = !orderByExpanded },
                    ) {
                        TextField(
                            value = orderBy.text,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = orderByExpanded) },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = orderByExpanded,
                            onDismissRequest = { orderByExpanded = false }
                        ) {
                            OrderType.entries.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(text = selectionOption.text) },
                                    onClick = {
                                        orderBy = selectionOption
                                        orderByExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    Text("Zeitraum:", style = androidx.compose.material3.MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
                    Row(Modifier.fillMaxWidth()) {
                        TextField(
                            value = fromDate.format(displayDateFormatter),
                            onValueChange = {},
                            label = { Text("Startdatum") },
                            readOnly = true,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 4.dp),
                            interactionSource = remember { MutableInteractionSource() }
                                .also { interactionSource ->
                                    LaunchedEffect(interactionSource) {
                                        interactionSource.interactions.collect {
                                            if (it is PressInteraction.Release) showFromPicker = true
                                        }
                                    }
                                }
                        )
                        TextField(
                            value = toDate.format(displayDateFormatter),
                            onValueChange = {},
                            label = { Text("Enddatum") },
                            readOnly = true,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 4.dp),
                            interactionSource = remember { MutableInteractionSource() }
                                .also { interactionSource ->
                                    LaunchedEffect(interactionSource) {
                                        interactionSource.interactions.collect {
                                            if (it is PressInteraction.Release) showToPicker = true
                                        }
                                    }
                                }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.size(16.dp))
                    DisturbanceTypeFilters(
                        disturbanceList = DisturbanceType.entries,
                        stateList = disturbanceTypeBoolStates
                    )
                }
                 item {
                    Spacer(modifier = Modifier.size(16.dp))
                    DisturbanceLineFilters(
                        disturbanceLines = lineStates
                    )
                }
            }
        }
    }

    if (showFromPicker) {
        DatePickerDialog(
            onDismissRequest = { showFromPicker = false },
            confirmButton = {
                Button(onClick = {
                    fromPickerState.selectedDateMillis?.let {
                        fromDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDateTime()
                    }
                    showFromPicker = false
                }) { Text("Anwenden") }
            },
            dismissButton = { Button(onClick = { showFromPicker = false }) { Text("Abbrechen") } }
        ) { DatePicker(state = fromPickerState) }
    }

    if (showToPicker) {
        DatePickerDialog(
            onDismissRequest = { showToPicker = false },
            confirmButton = {
                Button(onClick = {
                    toPickerState.selectedDateMillis?.let {
                        toDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDateTime()
                    }
                    showToPicker = false
                }) { Text("Anwenden") }
            },
            dismissButton = { Button(onClick = { showToPicker = false }) { Text("Abbrechen") } }
        ) { DatePicker(state = toPickerState) }
    }
}
