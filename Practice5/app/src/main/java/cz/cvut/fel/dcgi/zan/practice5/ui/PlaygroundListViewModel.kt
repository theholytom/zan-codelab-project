package cz.cvut.fel.dcgi.zan.practice5.ui

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.fel.dcgi.zan.practice5.data.PlaygroundRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlaygroundListViewModel(
    private val repository: PlaygroundRepository = PlaygroundRepository(),
) : ViewModel() {

    private val _retryTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    // The data source restarts on each retry
    private val playgroundsFlow: Flow<List<Playground>> = _retryTrigger
        .onStart { emit(Unit) }  // emit immediately on first collection
        .flatMapLatest {
            repository.getPlaygrounds()
        }

    private val _favouriteToggles = MutableStateFlow(emptySet<Long>())
    private val _selectedIds = MutableStateFlow(emptySet<Long>())

    // ── Filter state ────────────────────────────────────────────────────────
    private val _searchQuery = MutableStateFlow("")
    private val _showFavouritesOnly = MutableStateFlow(false)
    private val _selectedEquipment = MutableStateFlow(emptySet<Equipment>())

    private val _refreshing = MutableStateFlow(false)

    // ── Combined UI state ───────────────────────────────────────────────────

    private val debouncedQuery = _searchQuery
        .debounce(300)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = "",
        )

    private val _uiState: StateFlow<PlaygroundListUiState> = combine(
        repository.getPlaygrounds(),
        _favouriteToggles,
        _searchQuery,
        combine(_showFavouritesOnly, _refreshing) {favOnly, ref -> favOnly to ref},
        combine(_selectedEquipment, _selectedIds) { eq, sel -> eq to sel },
    ) { playgrounds, toggledIds, query, (favOnly, refreshing), (equipment, selectedIds) ->

        val modified = playgrounds.map { pg ->
            if (pg.id in toggledIds) pg.copy(isFavourite = !pg.isFavourite)
            else pg
        }

        val filtered = modified
            .filter { it.name.contains(query, ignoreCase = true) }
            .filter { if (favOnly) it.isFavourite else true }
            .filter { pg ->
                equipment.isEmpty() || equipment.all { it in pg.equipment }
            }

        PlaygroundListUiState.Content(
            playgrounds = filtered,
            searchQuery = query,
            showFavouritesOnly = favOnly,
            selectedEquipment = equipment,
            selectedIds = selectedIds,
            isSelectionMode = selectedIds.isNotEmpty(),
            isRefreshing = refreshing,
        ) as PlaygroundListUiState
    }
        .catch { e ->
            emit(PlaygroundListUiState.Error(e.message ?: "Unknown error"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PlaygroundListUiState.Loading,
        )

    val uiState: StateFlow<PlaygroundListUiState> = _uiState

    fun onEvent(event: PlaygroundListEvent) {
        when (event) {
            is PlaygroundListEvent.ToggleFavourite -> toggleFavourite(event.id)
            is PlaygroundListEvent.SearchQueryChanged -> {
                _searchQuery.value = event.query
            }
            is PlaygroundListEvent.ToggleFavouritesFilter -> {
                _showFavouritesOnly.value = event.enabled
            }
            is PlaygroundListEvent.ToggleEquipmentFilter -> {
                _selectedEquipment.update { current ->
                    if (event.equipment in current) current - event.equipment
                    else current + event.equipment
                }
            }
            is PlaygroundListEvent.Retry -> {
                _retryTrigger.tryEmit(Unit)
            }
            is PlaygroundListEvent.ToggleSelection -> {
                _selectedIds.update { current ->
                    if (event.id in current) current - event.id else current + event.id
                }
            }
            is PlaygroundListEvent.SelectAll -> {
                val current = _uiState.value
                if (current is PlaygroundListUiState.Content) {
                    _selectedIds.value = current.playgrounds.map { it.id }.toSet()
                }
            }
            is PlaygroundListEvent.ClearSelection -> {
                _selectedIds.value = emptySet()
            }
            is PlaygroundListEvent.DeleteSelected -> {
                // For now, just clear selection (actual delete needs repository)
                // In a real app: repository.deletePlaygrounds(selectedIds.value)
                _selectedIds.value = emptySet()
            }
            is PlaygroundListEvent.FavouriteSelected -> {
                _favouriteToggles.update { current ->
                    current + _selectedIds.value
                }
                _selectedIds.value = emptySet()
            }
            is PlaygroundListEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        val current = _uiState.value
        if (current !is PlaygroundListUiState.Content) return

        viewModelScope.launch {
            // Show refreshing indicator on existing content
            _refreshing.value = true
            try {
                val fresh = withContext(Dispatchers.IO) {
                    repository.refreshPlaygrounds()
                }
                // The fresh data will flow through the pipeline via repository
                // For now, we just stop the refreshing indicator
            } catch (e: Exception) {
                // Show error but keep existing content visible
                // Could emit a one-shot event via SharedFlow (see Step 6)
            } finally {
                _refreshing.value = false
            }
        }
    }

    private fun toggleFavourite(id: Long) {
        _favouriteToggles.update { current ->
            if (id in current) current - id else current + id
        }
    }
}

sealed interface PlaygroundListUiState {
    data object Loading : PlaygroundListUiState
    data class Content(
        val playgrounds: List<Playground>,
        val searchQuery: String = "",
        val showFavouritesOnly: Boolean = false,
        val selectedEquipment: Set<Equipment> = emptySet(),
        val selectedIds: Set<Long> = emptySet(),  // NEW
        val isSelectionMode: Boolean = false,       // NEW
        val isRefreshing: Boolean = false,
    ) : PlaygroundListUiState
    data class Error(val message: String) : PlaygroundListUiState
}

sealed interface PlaygroundListEvent {
    data class ToggleFavourite(val id: Long) : PlaygroundListEvent
    data class SearchQueryChanged(val query: String) : PlaygroundListEvent
    data class ToggleFavouritesFilter(val enabled: Boolean) : PlaygroundListEvent
    data class ToggleEquipmentFilter(val equipment: Equipment) : PlaygroundListEvent
    data object Retry : PlaygroundListEvent
    data class ToggleSelection(val id: Long) : PlaygroundListEvent
    data object SelectAll : PlaygroundListEvent
    data object ClearSelection : PlaygroundListEvent
    data object DeleteSelected : PlaygroundListEvent
    data object FavouriteSelected : PlaygroundListEvent
    data object Refresh : PlaygroundListEvent
}
