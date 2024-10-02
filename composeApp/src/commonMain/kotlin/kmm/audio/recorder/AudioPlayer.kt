package kmm.audio.recorder

import kotlinx.coroutines.flow.Flow

expect class AudioPlayer() {
    fun play(file: String)
    fun pause()
    fun stop()
    fun onPlaybackFinished(): Flow<Unit>
}