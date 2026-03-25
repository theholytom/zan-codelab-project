package cz.cvut.fel.dcgi.zan.practice5.ui

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlaygroundListViewModel : ViewModel() {

    var uiState by mutableStateOf<PlaygroundListUiState>(PlaygroundListUiState.Loading)
        private set

    init {
        loadPlaygrounds()
    }

    fun onEvent(event: PlaygroundListEvent) {
        when (event) {
            is PlaygroundListEvent.ToggleFavourite -> toggleFavourite(event.id)
            is PlaygroundListEvent.Retry -> loadPlaygrounds()
        }
    }

    private fun loadPlaygrounds() {
        uiState = PlaygroundListUiState.Loading

        // Simulate loading from a data source
        viewModelScope.launch {
            delay(1500)

            // Simulate occasional error (toggle this to test error state)
            // uiState = PlaygroundListUiState.Error("Failed to load playgrounds")
            // return@launch

            uiState = PlaygroundListUiState.Content(
                playgrounds = SampleData.playgrounds,
            )
        }
    }

    private fun toggleFavourite(id: Long) {
        val current = uiState
        if (current is PlaygroundListUiState.Content) {
            uiState = current.copy(
                playgrounds = current.playgrounds.map {
                    if (it.id == id) it.copy(isFavourite = !it.isFavourite) else it
                }
            )
        }
    }
}

sealed interface PlaygroundListUiState {
    data object Loading : PlaygroundListUiState
    data class Content(
        val playgrounds: List<Playground>,
    ) : PlaygroundListUiState
    data class Error(val message: String) : PlaygroundListUiState
}

sealed interface PlaygroundListEvent {
    data class ToggleFavourite(val id: Long) : PlaygroundListEvent
    data object Retry : PlaygroundListEvent
}
