package kmm.audio.recorder

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow

actual class AudioPlayer: AVAudioPlayerDelegateProtocol {
    private val playbackFinishedChannel = Channel<Unit>()
    private var audioPlayer: AVAudioPlayer? = null

    actual fun play(file: String) {
        val nsUrl = NSURL.fileURLWithPath(file)
        audioPlayer = AVAudioPlayer.alloc().initWithContentsOfURL(nsUrl, null)

        audioPlayer?.prepareToPlay()
        audioPlayer?.play()
    }

    actual fun pause() {
        audioPlayer?.pause()
    }

    actual fun stop() {
        audioPlayer?.stop()
    }

    actual fun onPlaybackFinished(): Flow<Unit> {
        return playbackFinishedChannel.consumeAsFlow()
    }

    override fun audioPlayerDidFinishPlaying(player: AVAudioPlayer, successfully: Boolean) {
        playbackFinishedChannel.trySend(Unit).isSuccess
    }
}