package cz.cvut.fel.dcgi.zan.practice5.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.serialization.Serializable

// ── Navigation routes ─────────────────────────────────────────────────────────

@Serializable object PlaygroundListRoute
@Serializable object PlansRoute
@Serializable data class PlaygroundDetailRoute(val playgroundId: Long)

// ── Shared app state (no ViewModel – plain hoisted state in MainActivity) ─────

class AppState {
    val playgrounds = SampleData.playgrounds
//    val visits = mutableStateListOf(*SampleData.plannedVisits.toTypedArray())

//    fun toggleFavourite(id: Long) {
//        val idx = playgrounds.indexOfFirst { it.id == id }
//        if (idx >= 0) playgrounds[idx] = playgrounds[idx].copy(isFavourite = !playgrounds[idx].isFavourite)
//    }
//
//    fun addVisit(playground: Playground, dateMillis: Long, hour: Int, minute: Int) {
//        visits.add(
//            PlannedVisit(
//                id = nextVisitId++,
//                playground = playground,
//                dateMillis = dateMillis,
//                hour = hour,
//                minute = minute,
//            )
//        )
//    }
//
//    fun removeVisit(id: Long) {
//        visits.removeIf { it.id == id }
//    }
//
//    fun restoreVisit(visit: PlannedVisit) {
//        visits.add(visit)
//    }
}
