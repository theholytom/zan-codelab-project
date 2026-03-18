package cz.cvut.fel.dcgi.zan.practice5.ui

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
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun PlansScreen(
    visits: List<PlannedVisit>,
    snackbarHostState: SnackbarHostState,
    onDeleteVisit: (Long) -> Unit,
    modifier: Modifier = Modifier,
    onRestoreVisit: (PlannedVisit) -> Unit
) {
    // TODO (Step 6): Replace AlertDialog deletion with Snackbar + Undo
     val scope = rememberCoroutineScope()

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
                            onDeleteVisit(visit.id)                        // remove from list
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
                        }
                        // TODO (Step 5): Pass onCardClick = { showBottomSheet for this visit }
                    )
                }
            }
        }
    }

    // TODO (Step 5): Add ModalBottomSheet here showing PlaygroundDetailContent
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
