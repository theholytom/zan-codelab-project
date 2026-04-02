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

@Serializable data object CoroutinePlaygroundRoute
