package cz.cvut.fel.dcgi.zan.practice5.ui

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class PlaygroundListViewModel : ViewModel() {

    init {
        Log.d("ViewModel", "PlaygroundListViewModel created: $this")
    }

    // ── State that survives configuration changes ────────────────────────────
    var playgrounds by mutableStateOf(SampleData.playgrounds)
        private set

    // ── Actions ──────────────────────────────────────────────────────────────
    fun toggleFavourite(id: Long) {
        playgrounds = playgrounds.map {
            if (it.id == id) it.copy(isFavourite = !it.isFavourite) else it
        }
    }
}
