package cz.cvut.fel.dcgi.zan.practice5.ui

import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import cz.cvut.fel.dcgi.zan.practice5.R
import cz.cvut.fel.dcgi.zan.practice5.ui.SampleData.playgrounds
import kotlinx.coroutines.delay


// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun PlaygroundListScreen(
    onNavigateToDetail: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlaygroundListViewModel = viewModel(),
    plansViewModel: PlansViewModel = viewModel(
        viewModelStoreOwner = LocalActivity.current!! as ViewModelStoreOwner
    ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is PlaygroundListUiState.Loading -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is PlaygroundListUiState.Error -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { viewModel.onEvent(PlaygroundListEvent.Retry) }) {
                        Text("Retry")
                    }
                }
            }
        }

        is PlaygroundListUiState.Content -> {
            PlaygroundListContent(
                uiState = state,
                onEvent = viewModel::onEvent,
                onNavigateToDetail = onNavigateToDetail,
                modifier = modifier,
            )
        }
    }
}

// ── Content ──────────────────────────────────────────────────────────────────────

// val tabs = listOf("All", "Favourites")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaygroundListContent(
    uiState: PlaygroundListUiState.Content,
    onEvent: (PlaygroundListEvent) -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            if (uiState.isSelectionMode) {
                SelectionTopBar(
                    selectedCount = uiState.selectedIds.size,
                    onClearSelection = { onEvent(PlaygroundListEvent.ClearSelection) },
                    onSelectAll = { onEvent(PlaygroundListEvent.SelectAll) },
                    onDeleteSelected = { onEvent(PlaygroundListEvent.DeleteSelected) },
                    onFavouriteSelected = { onEvent(PlaygroundListEvent.FavouriteSelected) },
                )
            } else {
                // Search field as top bar when not in selection mode
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { onEvent(PlaygroundListEvent.SearchQueryChanged(it)) },
                    label = { Text("Search") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
        },
    ) { innerPadding ->
        val pullToRefreshState = rememberPullToRefreshState()
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .pullToRefresh(
                    isRefreshing = uiState.isRefreshing,
                    state = pullToRefreshState,
                    onRefresh = { onEvent(PlaygroundListEvent.Refresh) },
                ),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {

                val tabs = listOf("All", "Favourites")

                // ── Tab row (Step 1.3) ────────────────────────────────────────────────
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                ) {
                    tabs.forEachIndexed { index, label ->
                        val selectedIndex: Int = if (uiState.showFavouritesOnly) 1 else 0
                        SegmentedButton(
                            selected = selectedIndex == index,
                            onClick = { onEvent(PlaygroundListEvent.ToggleFavouritesFilter(!uiState.showFavouritesOnly)) },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = tabs.size),
                            label = { Text(label) },
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
                            selected = item in uiState.selectedEquipment,
                            onClick = {
                                onEvent(PlaygroundListEvent.ToggleEquipmentFilter(item))
                            },
                            label = { Text(item.label()) },
                            leadingIcon = if (item in uiState.selectedEquipment) {
                                {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            } else null,
                        )
                    }
                }

                // ── List ──────────────────────────────────────────────────────────────
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    items(uiState.playgrounds, key = { it.id }) { playground ->
                        val isSelected = playground.id in uiState.selectedIds

                        PlaygroundCard(
                            playground = playground,
                            isSelected = isSelected,
                            isSelectionMode = uiState.isSelectionMode,
                            onCardClick = {
                                if (uiState.isSelectionMode) {
                                    onEvent(PlaygroundListEvent.ToggleSelection(playground.id))
                                } else {
                                    onNavigateToDetail(playground.id)
                                }
                            },
                            onLongClick = {
                                onEvent(PlaygroundListEvent.ToggleSelection(playground.id))
                            },
                            onFavouriteClick = {
                                onEvent(PlaygroundListEvent.ToggleFavourite(playground.id))
                            },
                        )
                    }
                }
            }
            PullToRefreshDefaults.Indicator(
                state = pullToRefreshState,
                isRefreshing = uiState.isRefreshing,
                modifier = Modifier.align(Alignment.TopCenter),
            )
        }

    }
}

// ── Card ──────────────────────────────────────────────────────────────────────

@Composable
fun PlaygroundCard(
    playground: Playground,
    isSelected: Boolean = false,
    isSelectionMode: Boolean = false,
    onCardClick: () -> Unit,
    onLongClick: () -> Unit = {},
    onFavouriteClick: () -> Unit,
    modifier: Modifier = Modifier,
    //onPlanVisit: (Playground, Long, Int, Int) -> Unit
) {

    var showPlanDialog by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .combinedClickable(
                onClick = onCardClick,
                onLongClick = onLongClick,
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp),
        ) {
            // Show checkbox in selection mode
            if (isSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onCardClick() },
                )
                Spacer(Modifier.width(8.dp))
            }
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
                    //onPlanVisit(playground, dateMillis, hour, minute)
                    showPlanDialog = false
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionTopBar(
    selectedCount: Int,
    onClearSelection: () -> Unit,
    onSelectAll: () -> Unit,
    onDeleteSelected: () -> Unit,
    onFavouriteSelected: () -> Unit,
) {
    TopAppBar(
        title = { Text("$selectedCount selected") },
        navigationIcon = {
            IconButton(onClick = onClearSelection) {
                Icon(Icons.Default.Close, contentDescription = "Clear selection")
            }
        },
        actions = {
            IconButton(onClick = onSelectAll) {
                Icon(painterResource(R.drawable.select_all_24px), contentDescription = "Select all")
            }
            IconButton(onClick = onFavouriteSelected) {
                Icon(Icons.Default.Favorite, contentDescription = "Favourite selected")
            }
            IconButton(onClick = onDeleteSelected) {
                Icon(Icons.Default.Delete, contentDescription = "Delete selected")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    )
}
