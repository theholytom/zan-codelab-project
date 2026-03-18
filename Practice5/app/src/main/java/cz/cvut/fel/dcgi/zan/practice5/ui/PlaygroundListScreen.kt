package cz.cvut.fel.dcgi.zan.practice5.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import cz.cvut.fel.dcgi.zan.practice5.R


// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun PlaygroundListScreen(
    playgrounds: List<Playground>,
    onNavigateToDetail: (Long) -> Unit,
    onToggleFavourite: (Long) -> Unit,
    modifier: Modifier = Modifier,
    onPlanVisit: (Playground, Long, Int, Int) -> Unit
) {
    // ── Search query ──────────────────────────────────────────────────────────
    var query by rememberSaveable { mutableStateOf("") }

    // ── Tab / filter state ────────────────────────────────────────────────────
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
    val tabs = listOf("All", "Favourites")

    // ── Equipment filter ──────────────────────────────────────────────────────
    var selectedEquipment by rememberSaveable { mutableStateOf(setOf(Equipment.SLIDE)) }

    // ── Derived list ──────────────────────────────────────────────────────────
    val displayedPlaygrounds = playgrounds
        .filter { it.name.contains(query, ignoreCase = true) }
        .filter { if (selectedTabIndex == 1) it.isFavourite else true }
        .filter { pg -> selectedEquipment.isEmpty() || selectedEquipment.all { it in pg.equipment } }

    Column(modifier = modifier.fillMaxSize()) {

        // ── Search field (Step 1.1 / 1.2) ────────────────────────────────────
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        )

        // ── Tab row (Step 1.3) ────────────────────────────────────────────────
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            tabs.forEachIndexed { index, label ->
                SegmentedButton(
                    selected = selectedTabIndex == index,
                    onClick  = { selectedTabIndex = index },
                    shape    = SegmentedButtonDefaults.itemShape(index = index, count = tabs.size),
                    label    = { Text(label) },
                )
            }
        }

        // ── Filter chips (Step 4) ─────────────────────────────────────────────
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        ) {
            items(Equipment.entries) { item ->
                FilterChip(
                    selected = item in selectedEquipment,
                    onClick  = {
                        selectedEquipment = if (item in selectedEquipment)
                            selectedEquipment - item
                        else
                            selectedEquipment + item
                    },
                    label = { Text(item.label()) },
                    leadingIcon = if (item in selectedEquipment) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    } else null,
                )
            }
        }

        // ── List ──────────────────────────────────────────────────────────────
        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(displayedPlaygrounds, key = { it.id }) { playground ->
                PlaygroundCard(
                    playground = playground,
                    onCardClick = { onNavigateToDetail(playground.id) },
                    onFavouriteClick = { onToggleFavourite(playground.id) },
                    onPlanVisit = onPlanVisit
                )
            }
        }
    }
}

// ── Card ──────────────────────────────────────────────────────────────────────

@Composable
fun PlaygroundCard(
    playground: Playground,
    onCardClick: () -> Unit,
    onFavouriteClick: () -> Unit,
    modifier: Modifier = Modifier,
    onPlanVisit: (Playground, Long, Int, Int) -> Unit
) {

    var showPlanDialog by rememberSaveable { mutableStateOf(false) }

    Card(
        onClick = onCardClick,
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
                model = playground.imageUrl,
                contentDescription = playground.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(64.dp),
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = playground.name,
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = playground.address,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                )
                // TODO (Step 5 – Bonus): Show equipment icons/chips here
            }
            IconButton(onClick = onFavouriteClick) {
                Icon(
                    imageVector = if (playground.isFavourite) Icons.Default.Favorite
                    else Icons.Default.FavoriteBorder,
                    contentDescription = if (playground.isFavourite) "Remove from favourites"
                    else "Add to favourites",
                    tint = if (playground.isFavourite) MaterialTheme.colorScheme.primary
                    else LocalContentColor.current,
                )
            }
            IconButton(onClick = { showPlanDialog = true }) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_calendar_month_24),
                    contentDescription = "Calendar",
                    tint = LocalContentColor.current
                )
            }
        }
        if (showPlanDialog) {
            PlanVisitDialog(
                playgroundName = playground.name,
                onDismiss = { showPlanDialog = false },
                onConfirm = { dateMillis, hour, minute ->
                    onPlanVisit(playground, dateMillis, hour, minute)
                    showPlanDialog = false
                },
            )
        }
    }
}
