package cz.cvut.fel.dcgi.zan.practice5.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class PlaygroundDetailViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // The key must match the property name in PlaygroundDetailRoute
    private val playgroundId: Long = savedStateHandle["playgroundId"]
        ?: error("playgroundId is required")

    var playground by mutableStateOf(
        SampleData.playgrounds.first { it.id == playgroundId }
    )
        private set
}
