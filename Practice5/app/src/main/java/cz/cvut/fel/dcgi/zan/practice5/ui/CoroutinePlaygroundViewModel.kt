package cz.cvut.fel.dcgi.zan.practice5.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

sealed interface CoroutinePlaygroundUiState {
    data class Idle(
        val lastResult: String? = null,
    ) : CoroutinePlaygroundUiState

    data class Computing(
        val description: String,
        val progress: Float = 0f,
    ) : CoroutinePlaygroundUiState
}

sealed interface CoroutinePlaygroundEvent {
    data object StartSequentialWork : CoroutinePlaygroundEvent
    data object StartParallelWork : CoroutinePlaygroundEvent
    data object StartCancellableWork : CoroutinePlaygroundEvent
    data object CancelWork : CoroutinePlaygroundEvent
}

class CoroutinePlaygroundViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<CoroutinePlaygroundUiState>(
        CoroutinePlaygroundUiState.Idle()
    )
    val uiState: StateFlow<CoroutinePlaygroundUiState> = _uiState.asStateFlow()

    private var currentJob: Job? = null

    fun onEvent(event: CoroutinePlaygroundEvent) {
        when (event) {
            is CoroutinePlaygroundEvent.StartSequentialWork -> startSequentialWork()
            is CoroutinePlaygroundEvent.StartParallelWork -> startParallelWork()
            is CoroutinePlaygroundEvent.StartCancellableWork -> startCancellableWork()
            is CoroutinePlaygroundEvent.CancelWork -> cancelWork()
        }
    }

    private fun startSequentialWork() {
        currentJob = viewModelScope.launch {
            _uiState.value = CoroutinePlaygroundUiState.Computing(
                description = "Computing sum sequentially..."
            )

            val startTime = System.currentTimeMillis()

            // Two sequential operations on the IO dispatcher
            val result1 = withContext(Dispatchers.Default) {
                heavyCalculation("Task A", 1_000_000)
            }

            _uiState.value = CoroutinePlaygroundUiState.Computing(
                description = "Task A done, computing Task B...",
                progress = 0.5f,
            )

            val result2 = withContext(Dispatchers.Default) {
                heavyCalculation("Task B", 1_000_000)
            }

            val elapsed = System.currentTimeMillis() - startTime

            _uiState.value = CoroutinePlaygroundUiState.Idle(
                lastResult = "Sequential: $result1 + $result2 = ${result1 + result2}\n" +
                    "Time: ${elapsed}ms"
            )
        }
    }

    private suspend fun heavyCalculation(name: String, iterations: Int): Long {
        var sum = 0L
        for (i in 1..iterations) {
            sum += i
            if (i % 100_000 == 0) yield() // cooperative cancellation
        }
        return sum
    }

    private fun startParallelWork() {
        currentJob = viewModelScope.launch {
            _uiState.value = CoroutinePlaygroundUiState.Computing(
                description = "Computing in parallel..."
            )

            val startTime = System.currentTimeMillis()

            // Two parallel operations using async
            coroutineScope {
                val deferred1 = async(Dispatchers.Default) {
                    heavyCalculation("Task A", 1_000_000)
                }
                val deferred2 = async(Dispatchers.Default) {
                    heavyCalculation("Task B", 1_000_000)
                }

                val result1 = deferred1.await()
                val result2 = deferred2.await()

                val elapsed = System.currentTimeMillis() - startTime

                _uiState.value = CoroutinePlaygroundUiState.Idle(
                    lastResult = "Parallel: $result1 + $result2 = ${result1 + result2}\n" +
                        "Time: ${elapsed}ms (compare with sequential!)"
                )
            }
        }
    }

    private fun startCancellableWork() {
        currentJob = viewModelScope.launch {
            _uiState.value = CoroutinePlaygroundUiState.Computing(
                description = "Computing... (tap Cancel to stop)",
            )

            val result = withContext(Dispatchers.Default) {
                var sum = 0L
                for (i in 1..10_000_000) {
                    ensureActive() // throws CancellationException if cancelled
                    sum += i
                    if (i % 1_000_000 == 0) {
                        withContext(Dispatchers.Main) {
                            _uiState.value = CoroutinePlaygroundUiState.Computing(
                                description = "Progress: ${i / 100_000}%",
                                progress = i / 10_000_000f,
                            )
                        }
                    }
                }
                sum
            }

            _uiState.value = CoroutinePlaygroundUiState.Idle(
                lastResult = "Completed: $result"
            )
        }
    }

    private fun cancelWork() {
        currentJob?.cancel()
        _uiState.value = CoroutinePlaygroundUiState.Idle(
            lastResult = "Cancelled!"
        )
    }
}
