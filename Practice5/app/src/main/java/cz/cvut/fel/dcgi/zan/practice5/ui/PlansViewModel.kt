package cz.cvut.fel.dcgi.zan.practice5.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class PlansViewModel : ViewModel() {

    var visits by mutableStateOf(SampleData.plannedVisits)
        private set

    private var nextVisitId = SampleData.plannedVisits.size + 1L

    fun addVisit(playground: Playground, dateMillis: Long, hour: Int, minute: Int) {
        visits = visits + PlannedVisit(
            id = nextVisitId++,
            playground = playground,
            dateMillis = dateMillis,
            hour = hour,
            minute = minute,
        )
    }

    fun removeVisit(id: Long) {
        visits = visits.filter { it.id != id }
    }

    fun restoreVisit(visit: PlannedVisit) {
        visits = visits + visit
    }
}
