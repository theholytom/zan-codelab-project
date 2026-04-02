package cz.cvut.fel.dcgi.zan.practice5.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CoroutinePlaygroundScreen(
    modifier: Modifier = Modifier,
    viewModel: CoroutinePlaygroundViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Coroutine Playground",
            style = MaterialTheme.typography.headlineMedium,
        )

        when (val state = uiState) {
            is CoroutinePlaygroundUiState.Idle -> {
                state.lastResult?.let { result ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = result,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }

                Button(
                    onClick = { viewModel.onEvent(CoroutinePlaygroundEvent.StartSequentialWork) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Run Sequential")
                }

                Button(
                    onClick = { viewModel.onEvent(CoroutinePlaygroundEvent.StartParallelWork) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Run Parallel")
                }

                Button(
                    onClick = { viewModel.onEvent(CoroutinePlaygroundEvent.StartCancellableWork) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Run Cancellable (long)")
                }
            }

            is CoroutinePlaygroundUiState.Computing -> {
                CircularProgressIndicator()
                Text(state.description)

                if (state.progress > 0f) {
                    LinearProgressIndicator(
                        progress = { state.progress },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                OutlinedButton(
                    onClick = { viewModel.onEvent(CoroutinePlaygroundEvent.CancelWork) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}
