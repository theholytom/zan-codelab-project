package cz.cvut.fel.dcgi.zan.practice5.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage


// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun PlaygroundListScreen(
    playgrounds: List<Playground>,
    onNavigateToDetail: (Long) -> Unit,
    onToggleFavourite: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    // ── Search query ──────────────────────────────────────────────────────────
    // TODO (Step 1.1): Type text, rotate the device — what happens?
    // TODO (Step 1.2): Replace 'remember' with 'rememberSaveable' and rotate again.
    var query by remember { mutableStateOf("") }

    // ── Tab / filter state ────────────────────────────────────────────────────
    // TODO (Step 1.3): Same bug — replace 'remember' with 'rememberSaveable'.
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("All", "Favourites")

    // ── Equipment filter ──────────────────────────────────────────────────────
    // TODO (Step 2.1): Try storing Set<Equipment> with plain remember — then rotate.
    // TODO (Step 2.2 / 2.3): Fix with @Parcelize wrapper or a custom Saver.
    var selectedEquipment by remember { mutableStateOf(emptySet<Equipment>()) }

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
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) },
                )
            }
        }

        // TODO (Step 3): Replace TabRow above with SingleChoiceSegmentedButtonRow

        // ── Filter chips (Step 4) ─────────────────────────────────────────────
        // TODO (Step 4): Add a LazyRow of FilterChip for each Equipment value here

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
) {
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
        }
    }
}