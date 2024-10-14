package kmm.audio.recorder

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioPlayerDelegateProtocol
import platform.AVFoundation.AVPlayerItemDidPlayToEndTimeNotification
import platform.Foundation.NSNotification
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSURL
import platform.Foundation.addObserver

actual class AudioPlayer {
    private val playbackFinishedChannel = Channel<Unit>()
    private var audioPlayer: AVAudioPlayer? = null

    @OptIn(ExperimentalForeignApi::class)
    actual fun play(file: String) {
        val nsUrl = NSURL.fileURLWithPath(file)
        audioPlayer = AVAudioPlayer(contentsOfURL = nsUrl, error = null).apply {
            prepareToPlay()
            play()
        }

        NSNotificationCenter.defaultCenter.addObserver(observer = this, selector = platform.darwin.sel_registerName("audioDidFinishPlaying:"),
            name = AVPlayerItemDidPlayToEndTimeNotification,
            `object` = audioPlayer
        )
    }

    actual fun pause() {
        audioPlayer?.pause()
    }

    actual fun stop() {
        audioPlayer?.stop()
    }

    fun audioDidFinishPlaying(notification: NSNotification){
        onPlaybackFinished()
    }

    actual fun onPlaybackFinished(): Flow<Unit> {
        return playbackFinishedChannel.consumeAsFlow()
    }
}