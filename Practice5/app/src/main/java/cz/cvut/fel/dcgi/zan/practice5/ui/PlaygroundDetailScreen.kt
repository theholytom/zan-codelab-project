package cz.cvut.fel.dcgi.zan.practice5.ui

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import cz.cvut.fel.dcgi.zan.practice5.R

// ── Screen ────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaygroundDetailScreen(
    viewModel: PlaygroundDetailViewModel = viewModel(),
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    plansViewModel: PlansViewModel = viewModel(
        viewModelStoreOwner = LocalActivity.current!! as ViewModelStoreOwner
    ),
) {
    // ── State for Plan a visit dialog ─────────────────────────────────────────
    // TODO (Step 3.1): Add showPlanDialog state here
    var showPlanDialog by rememberSaveable { mutableStateOf(false) }
    val playground = viewModel.playground

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(playground.name) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        }
    ) { innerPadding ->

        Column(
            modifier = modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
        ) {

            // ── Hero image ────────────────────────────────────────────────────
            AsyncImage(
                model = playground.imageUrl,
                contentDescription = playground.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
            )

            Column(modifier = Modifier.padding(16.dp)) {

                // ── Name & address ────────────────────────────────────────────
                Text(
                    text = playground.name,
                    style = MaterialTheme.typography.headlineSmall,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = playground.address,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(16.dp))

                // ── Equipment ─────────────────────────────────────────────────
                if (playground.equipment.isNotEmpty()) {
                    Text(
                        text = "Equipment",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(Modifier.height(8.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        playground.equipment.forEach { item ->
                            AssistChip(
                                onClick = {},
                                label = { Text(item.label()) },
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                // ── Plan visit button ─────────────────────────────────────────
                Button(
                    onClick = { showPlanDialog = true },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                ) {
                    Icon(
                        painterResource(R.drawable.outline_calendar_month_24),
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Plan a visit")
                }
            }
        }

        // ── Plan a visit dialog ───────────────────────────────────────────────
        // TODO (Step 3.1): Add PlanVisitDialog here
        if (showPlanDialog) {
            PlanVisitDialog(
                playgroundName = playground.name,
                onDismiss = { showPlanDialog = false },
                onConfirm = { dateMillis, hour, minute ->
                    plansViewModel.addVisit(playground,dateMillis, hour, minute)
                    showPlanDialog = false
                },
            )
        }
    }
}

// ── Plan Visit Dialog ─────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanVisitDialog(
    playgroundName: String,
    onDismiss: () -> Unit,
    onConfirm: (dateMillis: Long, hour: Int, minute: Int) -> Unit,
) {
    // Date state – save only the Long, not DatePickerState
    var selectedDateMillis by rememberSaveable { mutableStateOf<Long?>(null) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    // Time state – save primitives
    var selectedHour by rememberSaveable { mutableStateOf(10) }
    var selectedMinute by rememberSaveable { mutableStateOf(0) }
    var showTimePicker by rememberSaveable { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Plan a visit") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = playgroundName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                // Date picker trigger
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(selectedDateMillis?.let { formatDate(it) } ?: "Select date")
                }
                // Time picker trigger
                OutlinedButton(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(formatTime(selectedHour, selectedMinute))
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    selectedDateMillis?.let { onConfirm(it, selectedHour, selectedMinute) }
                },
                enabled = selectedDateMillis != null,
            ) { Text("Plan") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )

    // ── DatePickerDialog ──────────────────────────────────────────────────────
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDateMillis,
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDateMillis = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // ── TimePickerDialog ──────────────────────────────────────────────────────
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedHour,
            initialMinute = selectedMinute,
        )
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedHour = timePickerState.hour
                    selectedMinute = timePickerState.minute
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
            },
        ) {
            TimePicker(state = timePickerState)
        }
    }
}

// ── TimePickerDialog wrapper (not in Material3 yet) ───────────────────────────

@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        text = { content() },
    )
}
