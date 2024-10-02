package kmm.audio.recorder

import android.media.MediaPlayer
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow

actual class AudioPlayer {
    private var mediaPlayer: MediaPlayer? = null
    private val playbackFinishedChannel = Channel<Unit>()

    actual fun play(file: String) {
        mediaPlayer?.release() // Libera el anterior si existe
        mediaPlayer = MediaPlayer().apply {
            setDataSource(file)
            setOnCompletionListener {
                playbackFinishedChannel.trySend(Unit).isSuccess
            }
            prepare()
            start()
        }
    }

    actual fun pause() {
        mediaPlayer?.pause()
    }

    actual fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    actual fun onPlaybackFinished(): Flow<Unit>{
        return playbackFinishedChannel.consumeAsFlow()
    }
}