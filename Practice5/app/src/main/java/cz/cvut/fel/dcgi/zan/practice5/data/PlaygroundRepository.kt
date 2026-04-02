package cz.cvut.fel.dcgi.zan.practice5.data

import cz.cvut.fel.dcgi.zan.practice5.ui.Playground
import cz.cvut.fel.dcgi.zan.practice5.ui.SampleData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaygroundRepository {

    private var callCount = 0

    fun getPlaygrounds(): Flow<List<Playground>> = flow {
        delay(1500)
        callCount++
        // Simulate failure on first call (toggle for testing)
        // if (callCount == 1) throw IOException("Network error")
        emit(SampleData.playgrounds)
    }

    /**
     * Simulates a refresh that can fail.
     * Returns the updated list or throws.
     */
    suspend fun refreshPlaygrounds(): List<Playground> {
        delay(2000) // simulate network call
        // Simulate occasional failure:
        // if (Random.nextBoolean()) throw IOException("Refresh failed")
        return SampleData.playgrounds
    }
}
