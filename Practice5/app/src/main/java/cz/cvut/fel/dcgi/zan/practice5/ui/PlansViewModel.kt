package cz.cvut.fel.dcgi.zan.practice5.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlansViewModel : ViewModel() {

    var uiState by mutableStateOf<PlansUiState>(PlansUiState.Loading)
        private set

    init {
        loadPlans()
    }

    private var nextVisitId = SampleData.plannedVisits.size + 1L

    private fun loadPlans() {
        uiState = PlansUiState.Loading

        // Simulate loading from a data source
        viewModelScope.launch {
            delay(1500)

            // Simulate occasional error (toggle this to test error state)
            // uiState = PlaygroundListUiState.Error("Failed to load playgrounds")
            // return@launch

            uiState = PlansUiState.Content(
                visits = SampleData.plannedVisits,
                nextVisitId = SampleData.plannedVisits.size + 1L,
            )
        }
    }

    fun addVisit(playground: Playground, dateMillis: Long, hour: Int, minute: Int) {
        val current = uiState
        if (current is PlansUiState.Content) {
            uiState = current.copy(
                visits = current.visits + PlannedVisit(
                    id = nextVisitId++,
                    playground = playground,
                    dateMillis = dateMillis,
                    hour = hour,
                    minute = minute,
                ),
                nextVisitId = current.nextVisitId + 1
            )
        }
    }

    fun removeVisit(id: Long) {
        val current = uiState
        if (current is PlansUiState.Content) {
            uiState = current.copy(
                visits = current.visits.filter { it.id != id }
            )
        }
    }

    fun restoreVisit(visit: PlannedVisit) {
        val current = uiState
        if (current is PlansUiState.Content) {
            uiState = current.copy(
                visits = current.visits + visit
            )
        }
    }

    fun onEvent(event: PlansEvent) {
        when (event) {
            is PlansEvent.AddVisit -> addVisit(event.playground, event.dateMillis, event.hour, event.minute)
            is PlansEvent.RemoveVisit -> removeVisit(event.id)
            is PlansEvent.RestoreVisit -> restoreVisit(event.visit)
            is PlansEvent.Retry -> loadPlans()
        }
    }
}

sealed interface PlansUiState {
    data object Loading : PlansUiState
    data class Content(
        val visits: List<PlannedVisit>,
        val nextVisitId: Long,
    ) : PlansUiState
    data class Error(val message: String) : PlansUiState
}

sealed interface PlansEvent {
    data class AddVisit(val playground: Playground, val dateMillis: Long, val hour: Int, val minute: Int) : PlansEvent
    data class RemoveVisit(val id: Long) : PlansEvent
    data class RestoreVisit(val visit: PlannedVisit) : PlansEvent
    data object Retry : PlansEvent
}
