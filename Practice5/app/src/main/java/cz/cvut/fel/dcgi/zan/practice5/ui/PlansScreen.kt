package cz.cvut.fel.dcgi.zan.practice5.ui

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun PlansScreen(
    viewModel: PlansViewModel = viewModel(
        viewModelStoreOwner = LocalActivity.current!! as ViewModelStoreOwner
    ),
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {

    val uiState = viewModel.uiState

    when (uiState) {
        is PlansUiState.Loading -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is PlansUiState.Error -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = uiState.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { viewModel.onEvent(PlansEvent.Retry) }) {
                        Text("Retry")
                    }
                }
            }
        }

        is PlansUiState.Content -> {
            PlansContent(
                visits = uiState.visits,
                modifier = modifier,
                onRemoveVisit = { id ->
                    viewModel.onEvent(PlansEvent.RemoveVisit(id))
                },
                onRestoreVisit = { visit ->
                    viewModel.onEvent(PlansEvent.RestoreVisit(visit))
                },
                snackbarHostState = snackbarHostState
            )
        }
    }
}

// ── Plan Content ─────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlansContent(
    modifier: Modifier = Modifier,
    visits: List<PlannedVisit>,
    onRemoveVisit: (Long) -> Unit,
    onRestoreVisit: (PlannedVisit) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val scope = rememberCoroutineScope()

    var sheetPlayground by remember { mutableStateOf<Playground?>(null) }
    val sheetState = rememberModalBottomSheetState()

    Column(modifier = modifier.fillMaxSize()) {

        if (visits.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No planned visits yet.\nGo to Playgrounds and plan one!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(visits, key = { it.id }) { visit ->
                    PlanCard(
                        visit = visit,
                        onDeleteClick = {
                            val removed = visits.find { it.id == visit.id }
                            onRemoveVisit(visit.id)                        // remove from list
                            scope.launch {
                                val result = snackbarHostState.showSnackbar(
                                    message     = "Visit deleted",
                                    actionLabel = "Undo",
                                    duration    = SnackbarDuration.Short,
                                )
                                if (result == SnackbarResult.ActionPerformed && removed != null) {
                                    onRestoreVisit(visit)
                                }
                            }
                        },
                        onCardClick = { sheetPlayground = visit.playground }
                    )
                }
            }
        }
    }

    sheetPlayground?.let { pg ->
        ModalBottomSheet(
            onDismissRequest = { sheetPlayground = null },
            sheetState = sheetState,
        ) {
            PlaygroundDetailContent(playground = pg)
            Spacer(Modifier.height(32.dp)) // padding above nav bar
        }
    }
}



// ── Plan card ─────────────────────────────────────────────────────────────────

@Composable
fun PlanCard(
    visit: PlannedVisit,
    onDeleteClick: () -> Unit,
    onCardClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    // TODO (Step 4): Move delete confirmation from direct call to AlertDialog


    Card(
        onClick = { onCardClick?.invoke() },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = visit.playground.imageUrl,
                contentDescription = visit.playground.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(56.dp),
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = visit.playground.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${formatDate(visit.dateMillis)}  ${formatTime(visit.hour, visit.minute)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Delete visit")
            }
        }
    }

    // ── Confirm delete dialog (Step 4) ────────────────────────────────────────

}

// ── Playground detail content (reusable, used in ModalBottomSheet) ────────────

@Composable
fun PlaygroundDetailContent(
    playground: Playground,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        AsyncImage(
            model = playground.imageUrl,
            contentDescription = playground.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = playground.name,
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = playground.address,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (playground.equipment.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Equipment",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                playground.equipment.forEach { item ->
                    AssistChip(onClick = {}, label = { Text(item.label()) })
                }
            }
        }
    }
}
